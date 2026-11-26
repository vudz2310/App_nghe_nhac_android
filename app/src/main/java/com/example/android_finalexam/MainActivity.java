package com.example.android_finalexam;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tvTimeBegin, tvTimeEnd, tvNamePlaying, tvArtist;
    SeekBar seekBarTime;
    Button btnPlay, btnBack, btnPlayNext, btnPlayPrevious, btnLike;
    MediaPlayer mediaPlayer;
    ImageView ivCover;
    int currentPosition;
    ArrayList<Song> songList;
    private android.os.Handler seekBarHandler = new android.os.Handler();
    private Runnable seekBarRunnable;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing);

        songList = (ArrayList<Song>) getIntent().getSerializableExtra("songList");
        currentPosition = getIntent().getIntExtra("currentPosition", 0);

        tvTimeBegin = findViewById(R.id.tvTimeBegin);
        tvTimeEnd = findViewById(R.id.tvTimeEnd);
        seekBarTime = findViewById(R.id.seekBarTime);
        btnPlay = findViewById(R.id.btnPlay);
        btnBack = findViewById(R.id.btnBack);
        btnPlayNext = findViewById(R.id.btnPlayNext);
        btnLike = findViewById(R.id.btnLike);
        btnPlayPrevious = findViewById(R.id.btnPlayPrevious);
        tvNamePlaying = findViewById(R.id.tvNamePlaying);
        tvArtist = findViewById(R.id.tvArtist);
        ivCover = findViewById(R.id.ivCover);

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        seekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying()) {
                            int current = mediaPlayer.getCurrentPosition();
                            tvTimeBegin.setText(millisecondsToString(current));
                            seekBarTime.setProgress(current);
                        }
                    } catch (IllegalStateException ignored) {}
                    seekBarHandler.postDelayed(this, 500);
                }
            }
        };

        if (songList != null && !songList.isEmpty()) {
            playSong(currentPosition, true);
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        btnPlay.setText("▶");
                    } else {
                        mediaPlayer.start();
                        btnPlay.setText("⏸");
                    }
                }
            }
        });

        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
                tvTimeBegin.setText(millisecondsToString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                finish();
            }
        });

        btnPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList == null || songList.isEmpty()) return;
                currentPosition++;
                if (currentPosition >= songList.size()) {
                    currentPosition = 0;
                }
                playSong(currentPosition, true);
            }
        });

        btnPlayPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList == null || songList.isEmpty()) return;
                currentPosition--;
                if (currentPosition < 0) {
                    currentPosition = songList.size() - 1;
                }
                playSong(currentPosition, true);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList != null && currentPosition >= 0 && currentPosition < songList.size()) {
                    addSongToFavorites(songList.get(currentPosition));
                }
            }
        });
    }

    private void playSong(int position, boolean autoPlay) {
        if (songList == null || position < 0 || position >= songList.size()) return;
        Song song = songList.get(position);

        tvNamePlaying.setText(song.getTitle());
        tvArtist.setText(song.getArtist());

        // Set image resource
        int imageResource = getResources().getIdentifier(song.getImageResource(), "drawable", getPackageName());
        if (imageResource != 0) {
            ivCover.setImageResource(imageResource);
        } else {
            ivCover.setImageResource(R.drawable.playinglogo);
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Get resource ID of audio file
        int audioResourceId = getResources().getIdentifier(song.getAudioResource(), "raw", getPackageName());
        if (audioResourceId == 0) {
            Toast.makeText(this, "Không tìm thấy file âm thanh cho bài hát này", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create
        mediaPlayer = MediaPlayer.create(MainActivity.this, audioResourceId);
        if (mediaPlayer == null) {
            Toast.makeText(this, "Không thể phát bài hát này", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = mediaPlayer.getDuration();
        tvTimeEnd.setText(millisecondsToString(duration));
        seekBarTime.setMax(duration);

        if (autoPlay) {
            mediaPlayer.start();
            btnPlay.setText("⏸");
        } else {
            btnPlay.setText("▶");
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlay.setText("▶");
            }
        });

        startSeekBarUpdater();
    }

    private void addSongToFavorites(Song song) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_FAVORITES, null,
                DatabaseHelper.COLUMN_TITLE + " = ? AND " + DatabaseHelper.COLUMN_ARTIST + " = ?",
                new String[]{song.getTitle(), song.getArtist()}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            Toast.makeText(MainActivity.this, "Bài hát đã có trong danh sách yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cursor != null) {
            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, song.getTitle());
        values.put(DatabaseHelper.COLUMN_ARTIST, song.getArtist());
        values.put(DatabaseHelper.COLUMN_IMAGE_RESOURCE, song.getImageResource());
        values.put(DatabaseHelper.COLUMN_AUDIO_RESOURCE, song.getAudioResource());
        db.insert(DatabaseHelper.TABLE_FAVORITES, null, values);
        Toast.makeText(MainActivity.this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
    }

    private void startSeekBarUpdater() {
        seekBarHandler.removeCallbacks(seekBarRunnable);
        seekBarHandler.post(seekBarRunnable);
    }

    private String millisecondsToString(long milliseconds) {
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (seekBarHandler != null && seekBarRunnable != null) {
            seekBarHandler.removeCallbacks(seekBarRunnable);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (db != null) {
            db.close();
        }
    }
}
