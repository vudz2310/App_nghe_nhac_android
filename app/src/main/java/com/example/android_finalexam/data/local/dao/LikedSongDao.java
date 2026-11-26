package com.example.android_finalexam.data.local.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android_finalexam.data.local.database.DatabaseContract;
import com.example.android_finalexam.domain.model.SongItem;

import java.util.ArrayList;
import java.util.List;

public class LikedSongDao {

    private final SQLiteDatabase db;

    public LikedSongDao(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean isSongLiked(int songId) {
        Cursor cursor = db.query(DatabaseContract.LikedSongEntry.TABLE_NAME,
                new String[]{DatabaseContract.LikedSongEntry.COLUMN_SONG_ID},
                DatabaseContract.LikedSongEntry.COLUMN_SONG_ID + " = ?",
                new String[]{String.valueOf(songId)}, null, null, null, "1");
        if (cursor != null) {
            try {
                return cursor.getCount() > 0;
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    public boolean toggleLike(int songId) {
        if (isSongLiked(songId)) {
            db.delete(DatabaseContract.LikedSongEntry.TABLE_NAME,
                    DatabaseContract.LikedSongEntry.COLUMN_SONG_ID + " = ?",
                    new String[]{String.valueOf(songId)});
            return false;
        } else {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseContract.LikedSongEntry.COLUMN_SONG_ID, songId);
            cv.put(DatabaseContract.LikedSongEntry.COLUMN_LIKED_AT, System.currentTimeMillis());
            db.insertWithOnConflict(DatabaseContract.LikedSongEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            return true;
        }
    }

    public int getLikedSongCount() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.LikedSongEntry.TABLE_NAME, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }
        return 0;
    }

    public List<SongItem> getLikedSongs() {
        List<SongItem> list = new ArrayList<>();
        String sql = "SELECT s.id, s.title, s.artist_id, a.name AS artist_name, " +
                "s.album_id, al.title AS album_title, s.duration_ms, " +
                "s.audio_resource, s.image_resource, 1 AS is_liked " +
                "FROM " + DatabaseContract.LikedSongEntry.TABLE_NAME + " l " +
                "INNER JOIN " + DatabaseContract.SongEntry.TABLE_NAME + " s ON l.song_id = s.id " +
                "LEFT JOIN " + DatabaseContract.ArtistEntry.TABLE_NAME + " a ON s.artist_id = a.id " +
                "LEFT JOIN " + DatabaseContract.AlbumEntry.TABLE_NAME + " al ON s.album_id = al.id " +
                "ORDER BY l.liked_at DESC;";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    list.add(cursorToSongItem(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    @SuppressLint("Range")
    private SongItem cursorToSongItem(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String title = cursor.getString(cursor.getColumnIndex("title"));
        int artistId = cursor.getInt(cursor.getColumnIndex("artist_id"));
        String artistName = cursor.getString(cursor.getColumnIndex("artist_name"));
        Integer albumId = !cursor.isNull(cursor.getColumnIndex("album_id")) ? cursor.getInt(cursor.getColumnIndex("album_id")) : null;
        String albumTitle = cursor.getString(cursor.getColumnIndex("album_title"));
        int durationMs = cursor.getInt(cursor.getColumnIndex("duration_ms"));
        String audioRes = cursor.getString(cursor.getColumnIndex("audio_resource"));
        String coverRes = cursor.getString(cursor.getColumnIndex("image_resource"));
        return new SongItem(id, title, artistId, artistName, albumId, albumTitle, durationMs, audioRes, coverRes, true);
    }
}
