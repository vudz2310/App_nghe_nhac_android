package com.example.android_finalexam.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.android_finalexam.R;
import com.example.android_finalexam.data.repository.PlaylistRepository;
import com.example.android_finalexam.data.repository.SongRepository;
import com.example.android_finalexam.domain.model.Playlist;
import com.example.android_finalexam.domain.model.SongItem;
import com.example.android_finalexam.player.PlaybackController;
import com.example.android_finalexam.player.PlaybackState;
import com.example.android_finalexam.ui.home.HomeFragment;
import com.example.android_finalexam.ui.library.LibraryFragment;
import com.example.android_finalexam.ui.player.FullPlayerDialog;
import com.example.android_finalexam.ui.search.SearchFragment;
import com.example.android_finalexam.utils.ResourceUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AppMainActivity extends AppCompatActivity implements PlaybackController.PlaybackStateListener {

    private static final int PERMISSION_REQ_NOTIFICATIONS = 101;

    private BottomNavigationView bottomNavigationView;
    private View miniPlayerView;
    private ImageView ivMiniCover;
    private TextView tvMiniTitle;
    private TextView tvMiniArtist;
    private ImageButton btnMiniLike;
    private ImageButton btnMiniPlayPause;
    private ImageButton btnMiniNext;
    private ProgressBar pbMiniProgress;

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment searchFragment = new SearchFragment();
    private final Fragment libraryFragment = new LibraryFragment();
    private Fragment activeFragment = homeFragment;

    private PlaybackController playbackController;
    private SongRepository songRepository;
    private PlaylistRepository playlistRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);

        playbackController = PlaybackController.getInstance(this);
        songRepository = new SongRepository(this);
        playlistRepository = new PlaylistRepository(this);

        initViews();
        setupNavigation(savedInstanceState);
        setupMiniPlayerControls();
        checkNotificationPermission();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQ_NOTIFICATIONS);
            }
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        miniPlayerView = findViewById(R.id.includedMiniPlayer);
        ivMiniCover = miniPlayerView.findViewById(R.id.ivMiniCover);
        tvMiniTitle = miniPlayerView.findViewById(R.id.tvMiniTitle);
        tvMiniArtist = miniPlayerView.findViewById(R.id.tvMiniArtist);
        btnMiniLike = miniPlayerView.findViewById(R.id.btnMiniLike);
        btnMiniPlayPause = miniPlayerView.findViewById(R.id.btnMiniPlayPause);
        btnMiniNext = miniPlayerView.findViewById(R.id.btnMiniNext);
        pbMiniProgress = miniPlayerView.findViewById(R.id.pbMiniProgress);
    }

    private void setupNavigation(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, libraryFragment, "3").hide(libraryFragment)
                    .add(R.id.fragmentContainer, searchFragment, "2").hide(searchFragment)
                    .add(R.id.fragmentContainer, homeFragment, "1")
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                fm.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
                return true;
            } else if (itemId == R.id.nav_search) {
                fm.beginTransaction().hide(activeFragment).show(searchFragment).commit();
                activeFragment = searchFragment;
                return true;
            } else if (itemId == R.id.nav_library) {
                fm.beginTransaction().hide(activeFragment).show(libraryFragment).commit();
                activeFragment = libraryFragment;
                return true;
            }
            return false;
        });
    }

    private void setupMiniPlayerControls() {
        // Clicking on miniPlayerView opens the FullPlayerDialog
        miniPlayerView.setOnClickListener(v -> {
            if (playbackController.getPlaybackState().getCurrentSong() != null) {
                FullPlayerDialog fullPlayer = new FullPlayerDialog(this);
                fullPlayer.show();
            } else {
                Toast.makeText(this, "Hãy chọn một bài hát để bắt đầu", Toast.LENGTH_SHORT).show();
            }
        });

        btnMiniPlayPause.setOnClickListener(v -> playbackController.togglePlayPause());
        btnMiniNext.setOnClickListener(v -> playbackController.skipNext());

        btnMiniLike.setOnClickListener(v -> {
            SongItem current = playbackController.getPlaybackState().getCurrentSong();
            if (current != null) {
                songRepository.toggleLike(current.getId(), isLiked -> {
                    current.setLiked(isLiked);
                    btnMiniLike.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
                    Toast.makeText(this, isLiked ? "Đã thêm vào yêu thích" : "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                });
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
            tvMiniTitle.setText(song.getTitle());
            tvMiniArtist.setText(song.getArtistName());

            int coverResId = ResourceUtils.getDrawableResId(this, song.getCoverRes());
            ivMiniCover.setImageResource(coverResId);

            btnMiniLike.setImageResource(song.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        }

        btnMiniPlayPause.setImageResource(state.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @Override
    public void onPositionDiscontinuity(long currentPos, long duration) {
        if (duration > 0) {
            int progress = (int) ((currentPos * 1000) / duration);
            pbMiniProgress.setProgress(progress);
        }
    }

    public void playSongList(List<SongItem> songs, int startIndex) {
        playbackController.playSongList(songs, startIndex);
        if (songs != null && startIndex >= 0 && startIndex < songs.size()) {
            SongItem song = songs.get(startIndex);
            songRepository.recordHistory(song.getId(), 0);
        }
    }

    public void showSongOptionsMenu(SongItem song, View anchorView) {
        String[] options = {"Phát ngay", (song.isLiked() ? "Bỏ thích" : "Thêm vào yêu thích"), "Thêm vào playlist"};
        new AlertDialog.Builder(this)
                .setTitle(song.getTitle() + " - " + song.getArtistName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        List<SongItem> single = new ArrayList<>();
                        single.add(song);
                        playSongList(single, 0);
                    } else if (which == 1) {
                        songRepository.toggleLike(song.getId(), isLiked -> {
                            song.setLiked(isLiked);
                            SongItem current = playbackController.getPlaybackState().getCurrentSong();
                            if (current != null && current.getId() == song.getId()) {
                                btnMiniLike.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
                            }
                            Toast.makeText(this, isLiked ? "Đã thích bài hát" : "Đã bỏ thích", Toast.LENGTH_SHORT).show();
                        });
                    } else if (which == 2) {
                        showAddToPlaylistDialog(song);
                    }
                })
                .show();
    }

    private void showAddToPlaylistDialog(SongItem song) {
        playlistRepository.getAllPlaylists(playlists -> {
            if (playlists.isEmpty()) {
                Toast.makeText(this, "Bạn chưa có playlist nào. Hãy tạo trong tab Thư viện!", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] names = new String[playlists.size()];
            for (int i = 0; i < playlists.size(); i++) {
                names[i] = playlists.get(i).getName();
            }
            new AlertDialog.Builder(this)
                    .setTitle("Thêm vào Playlist")
                    .setItems(names, (dialog, which) -> {
                        Playlist chosen = playlists.get(which);
                        playlistRepository.addSongToPlaylist(chosen.getId(), song.getId(), success -> {
                            Toast.makeText(this, success ? "Đã thêm vào " + chosen.getName() : "Không thể thêm", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .show();
        });
    }
}
