package com.example.android_finalexam.data.local.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android_finalexam.data.local.database.DatabaseContract;
import com.example.android_finalexam.domain.model.SongItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao {

    private final SQLiteDatabase db;

    public HistoryDao(SQLiteDatabase db) {
        this.db = db;
    }

    public void recordHistory(int songId, int progressMs) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.HistoryEntry.COLUMN_SONG_ID, songId);
        cv.put(DatabaseContract.HistoryEntry.COLUMN_PLAYED_AT, System.currentTimeMillis());
        cv.put(DatabaseContract.HistoryEntry.COLUMN_PROGRESS_MS, progressMs);
        db.insert(DatabaseContract.HistoryEntry.TABLE_NAME, null, cv);
    }

    public List<SongItem> getRecentlyPlayedSongs(int limit) {
        List<SongItem> list = new ArrayList<>();
        String sql = "SELECT s.id, s.title, s.artist_id, a.name AS artist_name, " +
                "s.album_id, al.title AS album_title, s.duration_ms, " +
                "s.audio_resource, s.image_resource, " +
                "CASE WHEN l.song_id IS NOT NULL THEN 1 ELSE 0 END AS is_liked, " +
                "MAX(h.played_at) as last_played " +
                "FROM " + DatabaseContract.HistoryEntry.TABLE_NAME + " h " +
                "INNER JOIN " + DatabaseContract.SongEntry.TABLE_NAME + " s ON h.song_id = s.id " +
                "LEFT JOIN " + DatabaseContract.ArtistEntry.TABLE_NAME + " a ON s.artist_id = a.id " +
                "LEFT JOIN " + DatabaseContract.AlbumEntry.TABLE_NAME + " al ON s.album_id = al.id " +
                "LEFT JOIN " + DatabaseContract.LikedSongEntry.TABLE_NAME + " l ON s.id = l.song_id " +
                "GROUP BY s.id " +
                "ORDER BY last_played DESC " +
                "LIMIT ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(limit)});
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
        boolean isLiked = cursor.getInt(cursor.getColumnIndex("is_liked")) == 1;
        return new SongItem(id, title, artistId, artistName, albumId, albumTitle, durationMs, audioRes, coverRes, isLiked);
    }
}
