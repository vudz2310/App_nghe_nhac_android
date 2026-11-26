package com.example.android_finalexam;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ListSong extends AppCompatActivity {
    private static final int ADD_SONG_REQUEST = 1;

    private ActivityResultLauncher<Intent> editSongLauncher;

    ListView listViewSongs;
    ArrayList<Song> songList;
    ArrayList<Song> filteredSongList;
    SongAdapter songAdapter;
    EditText edtSearch;
    Button btnShowFavorites, btnAddSong;
    android.widget.TextView tvEmptyState;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    private Handler handler = new Handler();
    private long lastClickTime = 0;
    private int lastClickPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_song);

        listViewSongs = findViewById(R.id.lvsongs);
        edtSearch = findViewById(R.id.edtSearch);
        btnShowFavorites = findViewById(R.id.btnFavorites);
        btnAddSong = findViewById(R.id.btnAdd);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        songList = new ArrayList<>();
        loadSongsFromDatabase();
        filteredSongList = new ArrayList<>(songList);
        songAdapter = new SongAdapter(ListSong.this, filteredSongList);
        listViewSongs.setAdapter(songAdapter);

        // Register the ActivityResultLauncher for editing songs
        editSongLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Song updatedSong = (Song) data.getSerializableExtra("updatedSong");
                            if (updatedSong != null) {
                                updateSongInList(updatedSong);
                                songAdapter.notifyDataSetChanged();
                                Toast.makeText(this, "Bài hát đã được cập nhật", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        listViewSongs.setOnItemClickListener((parent, view, position, id) -> {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < 500 && lastClickPosition == position) {
                // Nhấp đúp (Double-tap) -> Mở màn hình chỉnh sửa bài hát
                handler.removeCallbacksAndMessages(null);
                lastClickTime = 0;
                lastClickPosition = -1;

                Song song = filteredSongList.get(position);
                Intent editSongIntent = new Intent(ListSong.this, EditSong.class);
                editSongIntent.putExtra("song", song);
                editSongLauncher.launch(editSongIntent);
            } else {
                // Nhấp đơn (Single-tap) -> Mở trình phát nhạc
                lastClickTime = clickTime;
                lastClickPosition = position;
                final int selectedPos = position;

                handler.postDelayed(() -> {
                    if (lastClickPosition == selectedPos) {
                        Song song = filteredSongList.get(selectedPos);
                        Intent openMusicPlayer = new Intent(ListSong.this, MainActivity.class);
                        openMusicPlayer.putExtra("song", song);
                        openMusicPlayer.putExtra("currentPosition", selectedPos);
                        openMusicPlayer.putExtra("songList", filteredSongList);
                        startActivity(openMusicPlayer);
                        lastClickTime = 0;
                        lastClickPosition = -1;
                    }
                }, 300);
            }
        });

        // Nhấn giữ (Long click) để mở menu tùy chọn
        listViewSongs.setOnItemLongClickListener((parent, view, position, id) -> {
            Song selectedSong = filteredSongList.get(position);
            showSongOptionsDialog(selectedSong, position);
            return true;
         });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().toLowerCase(Locale.getDefault());
                filterSongs(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnShowFavorites.setOnClickListener(v -> {
            Intent showFavorites = new Intent(ListSong.this, FavoritesSong.class);
            startActivity(showFavorites);
        });

        btnAddSong.setOnClickListener(v -> {
            Intent addSongIntent = new Intent(ListSong.this, AddSong.class);
            startActivityForResult(addSongIntent, ADD_SONG_REQUEST);
        });
    }

    private void showSongOptionsDialog(Song song, int position) {
        String[] options = {"Phát nhạc", "Chỉnh sửa", "Xóa bài hát"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(song.getTitle() + " - " + song.getArtist());
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Phát nhạc
                    Intent openMusicPlayer = new Intent(ListSong.this, MainActivity.class);
                    openMusicPlayer.putExtra("song", song);
                    openMusicPlayer.putExtra("currentPosition", position);
                    openMusicPlayer.putExtra("songList", filteredSongList);
                    startActivity(openMusicPlayer);
                    break;
                case 1: // Chỉnh sửa
                    Intent editSongIntent = new Intent(ListSong.this, EditSong.class);
                    editSongIntent.putExtra("song", song);
                    editSongLauncher.launch(editSongIntent);
                    break;
                case 2: // Xóa bài hát
                    new AlertDialog.Builder(this)
                            .setTitle("Xác nhận xóa")
                            .setMessage("Bạn có chắc chắn muốn xóa bài hát \"" + song.getTitle() + "\" không?")
                            .setPositiveButton("Xóa", (d, w) -> removeSong(position))
                            .setNegativeButton("Hủy", null)
                            .show();
                    break;
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSongsFromDatabase();
        filterSongs(edtSearch.getText().toString().toLowerCase(Locale.getDefault()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_SONG_REQUEST && resultCode == RESULT_OK) {
            loadSongsFromDatabase();
            filterSongs(edtSearch.getText().toString().toLowerCase(Locale.getDefault()));
            Toast.makeText(this, "Bài hát đã được thêm", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSongInList(Song updatedSong) {
        for (int i = 0; i < songList.size(); i++) {
            Song song = songList.get(i);
            if (song.getId() == updatedSong.getId()) {
                songList.set(i, updatedSong);
                break;
            }
        }

        for (int i = 0; i < filteredSongList.size(); i++) {
            Song song = filteredSongList.get(i);
            if (song.getId() == updatedSong.getId()) {
                filteredSongList.set(i, updatedSong);
                break;
            }
        }
    }

    private void filterSongs(String keyword) {
        filteredSongList.clear();
        if (keyword.isEmpty()) {
            filteredSongList.addAll(songList);
        } else {
            for (Song song : songList) {
                if (passesFilter(song, keyword)) {
                    filteredSongList.add(song);
                }
            }
        }
        if (songAdapter != null) {
            songAdapter.notifyDataSetChanged();
        }
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(filteredSongList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void removeSong(int position) {
        if (position >= 0 && position < filteredSongList.size()) {
            Song removedSong = filteredSongList.get(position);
            if (databaseHelper.deleteSong(removedSong)) {
                songList.remove(removedSong);
                filteredSongList.remove(position);
                songAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Bài hát đã được xóa", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không thể xóa bài hát", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    private void loadSongsFromDatabase() {
        databaseHelper.seedDefaultSongsIfEmpty(db);
        Cursor cursor = db.query(DatabaseHelper.TABLE_SONGS, null, null, null, null, null, null);
        songList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ARTIST));
                String imageResource = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_RESOURCE));
                String audioResource = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUDIO_RESOURCE));
                songList.add(new Song(id, title, artist, imageResource, audioResource));
            }
            cursor.close();
        }
    }

    private boolean passesFilter(Song song, String keyword) {
        return song.getTitle().toLowerCase(Locale.getDefault()).contains(keyword) ||
               song.getArtist().toLowerCase(Locale.getDefault()).contains(keyword);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
