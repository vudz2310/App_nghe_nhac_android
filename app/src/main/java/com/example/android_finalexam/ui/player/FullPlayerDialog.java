package com.example.android_finalexam.ui.player;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.android_finalexam.R;
import com.example.android_finalexam.data.repository.SongRepository;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.player.PlaybackController;
import com.example.android_finalexam.player.PlaybackState;
import com.example.android_finalexam.utils.ResourceUtils;

public class FullPlayerDialog extends Dialog implements PlaybackController.PlaybackStateListener {

    private final PlaybackController playbackController;
    private final SongRepository songRepository;

    private ImageButton btnCollapsePlayer;
    private TextView tvPlayerContextTitle;
    private ImageButton btnPlayerMore;
    private ImageView ivFullPlayerCover;
    private TextView tvFullPlayerTitle;
    private TextView tvFullPlayerArtist;
    private ImageButton btnFullPlayerLike;
    private SeekBar seekBarFullPlayer;
    private TextView tvCurrentPosition;
    private TextView tvTotalDuration;
    private ImageButton btnShuffle;
    private ImageButton btnPrevious;
    private ImageButton btnPlayPause;
    private ImageButton btnNext;
    private ImageButton btnRepeat;
    private ImageButton btnOpenQueue;

    private boolean isUserSeeking = false;

    public FullPlayerDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.playbackController = PlaybackController.getInstance(context);
        this.songRepository = new SongRepository(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_full_player);

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnCollapsePlayer = findViewById(R.id.btnCollapsePlayer);
        tvPlayerContextTitle = findViewById(R.id.tvPlayerContextTitle);
        btnPlayerMore = findViewById(R.id.btnPlayerMore);
        ivFullPlayerCover = findViewById(R.id.ivFullPlayerCover);
        tvFullPlayerTitle = findViewById(R.id.tvFullPlayerTitle);
        tvFullPlayerArtist = findViewById(R.id.tvFullPlayerArtist);
        btnFullPlayerLike = findViewById(R.id.btnFullPlayerLike);
        seekBarFullPlayer = findViewById(R.id.seekBarFullPlayer);
        tvCurrentPosition = findViewById(R.id.tvCurrentPosition);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnOpenQueue = findViewById(R.id.btnOpenQueue);
    }

    private void setupListeners() {
        btnCollapsePlayer.setOnClickListener(v -> dismiss());

        btnPlayPause.setOnClickListener(v -> playbackController.togglePlayPause());
        btnNext.setOnClickListener(v -> playbackController.skipNext());
        btnPrevious.setOnClickListener(v -> playbackController.skipPrevious());
        btnShuffle.setOnClickListener(v -> playbackController.toggleShuffle());
        btnRepeat.setOnClickListener(v -> playbackController.toggleRepeatMode());

        btnFullPlayerLike.setOnClickListener(v -> {
            SongItem song = playbackController.getPlaybackState().getCurrentSong();
            if (song != null) {
                songRepository.toggleLike(song.getId(), isLiked -> {
                    song.setLiked(isLiked);
                    btnFullPlayerLike.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
                });
            }
        });

        btnOpenQueue.setOnClickListener(v -> {
            QueueDialog queueDialog = new QueueDialog(getContext());
            queueDialog.show();
        });

        seekBarFullPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long duration = playbackController.getPlaybackState().getDurationMs();
                    long targetMs = (duration * progress) / 1000;
                    tvCurrentPosition.setText(ResourceUtils.formatDuration((int) targetMs));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
                long duration = playbackController.getPlaybackState().getDurationMs();
                long targetMs = (duration * seekBar.getProgress()) / 1000;
                playbackController.seekTo(targetMs);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        playbackController.addListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        playbackController.removeListener(this);
    }

    @Override
    public void onPlaybackStateChanged(PlaybackState state) {
        if (state == null) return;
        SongItem song = state.getCurrentSong();
        if (song != null) {
            tvFullPlayerTitle.setText(song.getTitle());
            tvFullPlayerArtist.setText(song.getArtistName());
            if (song.getAlbumTitle() != null && !song.getAlbumTitle().isEmpty()) {
                tvPlayerContextTitle.setText(song.getAlbumTitle());
            }

            int coverRes = ResourceUtils.getDrawableResId(getContext(), song.getCoverRes());
            ivFullPlayerCover.setImageResource(coverRes);

            btnFullPlayerLike.setImageResource(song.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        }

        btnPlayPause.setImageResource(state.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);

        btnShuffle.setColorFilter(state.isShuffleEnabled() ?
                getContext().getColor(R.color.brand_primary) :
                getContext().getColor(R.color.white));

        int repeatMode = state.getRepeatMode();
        btnRepeat.setColorFilter(repeatMode != 0 ?
                getContext().getColor(R.color.brand_primary) :
                getContext().getColor(R.color.white));
    }

    @Override
    public void onPositionDiscontinuity(long currentPos, long duration) {
        if (!isUserSeeking && duration > 0) {
            int progress = (int) ((currentPos * 1000) / duration);
            seekBarFullPlayer.setProgress(progress);
            tvCurrentPosition.setText(ResourceUtils.formatDuration((int) currentPos));
            tvTotalDuration.setText(ResourceUtils.formatDuration((int) duration));
        }
    }
}
