package com.example.android_finalexam.data.local.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android_finalexam.data.local.database.DatabaseContract;
import com.example.android_finalexam.domain.model.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumDao {

    private final SQLiteDatabase db;

    public AlbumDao(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Album> getAllAlbums() {
        List<Album> list = new ArrayList<>();
        String sql = "SELECT al.*, a.name AS artist_name FROM " + DatabaseContract.AlbumEntry.TABLE_NAME + " al " +
                "LEFT JOIN " + DatabaseContract.ArtistEntry.TABLE_NAME + " a ON al.artist_id = a.id " +
                "ORDER BY al.title ASC;";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    list.add(cursorToAlbum(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    public Album getAlbumById(int id) {
        String sql = "SELECT al.*, a.name AS artist_name FROM " + DatabaseContract.AlbumEntry.TABLE_NAME + " al " +
                "LEFT JOIN " + DatabaseContract.ArtistEntry.TABLE_NAME + " a ON al.artist_id = a.id " +
                "WHERE al.id = ? LIMIT 1;";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursorToAlbum(cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    @SuppressLint("Range")
    private Album cursorToAlbum(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String title = cursor.getString(cursor.getColumnIndex("title"));
        int artistId = cursor.getInt(cursor.getColumnIndex("artist_id"));
        String artistName = cursor.getString(cursor.getColumnIndex("artist_name"));
        String coverRes = cursor.getString(cursor.getColumnIndex("cover_res"));
        String releaseDate = cursor.getString(cursor.getColumnIndex("release_date"));
        long createdAt = cursor.getLong(cursor.getColumnIndex("created_at"));
        return new Album(id, title, artistId, artistName, coverRes, releaseDate, createdAt);
    }
}
