package com.example.android_finalexam.data.local.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android_finalexam.data.local.database.DatabaseContract;
import com.example.android_finalexam.domain.model.Artist;

import java.util.ArrayList;
import java.util.List;

public class ArtistDao {

    private final SQLiteDatabase db;

    public ArtistDao(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Artist> getAllArtists() {
        List<Artist> list = new ArrayList<>();
        Cursor cursor = db.query(DatabaseContract.ArtistEntry.TABLE_NAME, null, null, null, null, null, DatabaseContract.ArtistEntry.COLUMN_NAME + " ASC");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    list.add(cursorToArtist(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    public Artist getArtistById(int id) {
        Cursor cursor = db.query(DatabaseContract.ArtistEntry.TABLE_NAME, null,
                DatabaseContract.ArtistEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null, "1");
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursorToArtist(cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    @SuppressLint("Range")
    private Artist cursorToArtist(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseContract.ArtistEntry.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistEntry.COLUMN_NAME));
        String avatarRes = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistEntry.COLUMN_AVATAR_RES));
        String bio = cursor.getString(cursor.getColumnIndex(DatabaseContract.ArtistEntry.COLUMN_BIO));
        long createdAt = cursor.getLong(cursor.getColumnIndex(DatabaseContract.ArtistEntry.COLUMN_CREATED_AT));
        return new Artist(id, name, avatarRes, bio, createdAt);
    }
}
