package com.example.android_finalexam;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddSong extends AppCompatActivity {

    EditText edtTitle, edtArtist, edtNameImg, edtNameAudio;
    Button btnAddSong, btnCancel;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_song);

        edtTitle = findViewById(R.id.edtTitle);
        edtArtist = findViewById(R.id.edtArtist);
        edtNameImg = findViewById(R.id.edtNameImg);
        btnAddSong = findViewById(R.id.btnAddSong);
        btnCancel = findViewById(R.id.btnCancel);
        edtNameAudio = findViewById(R.id.edtNameAudio);

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        btnAddSong.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String artist = edtArtist.getText().toString().trim();
            String imgName = edtNameImg.getText().toString().trim();
            String audioName = edtNameAudio.getText().toString().trim();

            if (title.isEmpty() || artist.isEmpty() || imgName.isEmpty() || audioName.isEmpty()) {
                Toast.makeText(AddSong.this, "Vui lòng nhập đầy đủ các trường thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }

            saveSongToDatabase(title, artist, imgName, audioName);
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveSongToDatabase(String title, String artist, String imageFileName, String audioFileName) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, title);
        values.put(DatabaseHelper.COLUMN_ARTIST, artist);
        values.put(DatabaseHelper.COLUMN_IMAGE_RESOURCE, imageFileName);
        values.put(DatabaseHelper.COLUMN_AUDIO_RESOURCE, audioFileName);

        long newRowId = db.insert(DatabaseHelper.TABLE_SONGS, null, values);
        if (newRowId != -1) {
            Song newSong = new Song((int) newRowId, title, artist, imageFileName, audioFileName);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newSong", newSong);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Thêm bài hát thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không thể lưu bài hát", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
