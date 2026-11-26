package com.example.android_finalexam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditSong extends AppCompatActivity {
    EditText edtTitle, edtArtist, edtNameImg, edtNameAudio;
    Button btnUpdate, btnCancel;
    Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_song);

        edtTitle = findViewById(R.id.edtTitle);
        edtArtist = findViewById(R.id.edtArtist);
        edtNameImg = findViewById(R.id.edtNameImg);
        edtNameAudio = findViewById(R.id.edtNameAudio);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        // Get the song data from the intent
        song = (Song) getIntent().getSerializableExtra("song");
        if (song != null) {
            edtTitle.setText(song.getTitle());
            edtArtist.setText(song.getArtist());
            edtNameImg.setText(song.getImageResource());
            edtNameAudio.setText(song.getAudioResource());
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu bài hát", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnUpdate.setOnClickListener(v -> {
            String newTitle = edtTitle.getText().toString().trim();
            String newArtist = edtArtist.getText().toString().trim();
            String newImageResource = edtNameImg.getText().toString().trim();
            String newAudioResource = edtNameAudio.getText().toString().trim();

            if (newTitle.isEmpty() || newArtist.isEmpty() || newImageResource.isEmpty() || newAudioResource.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật bài hát trong cơ sở dữ liệu
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            boolean isUpdated = databaseHelper.updateSong(song.getId(), newTitle, newArtist, newImageResource, newAudioResource);

            if (isUpdated) {
                song.setTitle(newTitle);
                song.setArtist(newArtist);
                song.setImageResource(newImageResource);
                song.setAudioResource(newAudioResource);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedSong", song);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
