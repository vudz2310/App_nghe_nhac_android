package com.example.android_finalexam.data.local.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android_finalexam.data.local.database.DatabaseContract;
import com.example.android_finalexam.domain.model.Playlist;
import com.example.android_finalexam.domain.model.SongItem;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDao {

    private final SQLiteDatabase db;

    public PlaylistDao(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Playlist> getAllPlaylists() {
        List<Playlist> list = new ArrayList<>();
        String sql = "SELECT p.*, COUNT(ps.song_id) AS song_count " +
                "FROM " + DatabaseContract.PlaylistEntry.TABLE_NAME + " p " +
                "LEFT JOIN " + DatabaseContract.PlaylistSongEntry.TABLE_NAME + " ps ON p.id = ps.playlist_id " +
                "GROUP BY p.id ORDER BY p.updated_at DESC;";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    list.add(cursorToPlaylist(cursor));
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    public Playlist getPlaylistById(int id) {
        String sql = "SELECT p.*, COUNT(ps.song_id) AS song_count " +
                "FROM " + DatabaseContract.PlaylistEntry.TABLE_NAME + " p " +
                "LEFT JOIN " + DatabaseContract.PlaylistSongEntry.TABLE_NAME + " ps ON p.id = ps.playlist_id " +
                "WHERE p.id = ? GROUP BY p.id LIMIT 1;";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursorToPlaylist(cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public long createPlaylist(String name, String description, String coverRes) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_NAME, name);
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_DESCRIPTION, description);
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_COVER_RES, coverRes != null ? coverRes : "playinglogo");
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
        return db.insert(DatabaseContract.PlaylistEntry.TABLE_NAME, null, cv);
    }

    public boolean updatePlaylist(int id, String name, String description) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_NAME, name);
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_DESCRIPTION, description);
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
        return db.update(DatabaseContract.PlaylistEntry.TABLE_NAME, cv,
                DatabaseContract.PlaylistEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deletePlaylist(int id) {
        return db.delete(DatabaseContract.PlaylistEntry.TABLE_NAME,
                DatabaseContract.PlaylistEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean addSongToPlaylist(int playlistId, int songId) {
        // Query current max position
        int nextPos = 0;
        Cursor cursor = db.rawQuery("SELECT MAX(position) FROM " + DatabaseContract.PlaylistSongEntry.TABLE_NAME +
                " WHERE playlist_id = ?", new String[]{String.valueOf(playlistId)});
        if (cursor != null) {
            try {
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    nextPos = cursor.getInt(0) + 1;
                }
            } finally {
                cursor.close();
            }
        }

        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID, playlistId);
        cv.put(DatabaseContract.PlaylistSongEntry.COLUMN_SONG_ID, songId);
        cv.put(DatabaseContract.PlaylistSongEntry.COLUMN_POSITION, nextPos);
        cv.put(DatabaseContract.PlaylistSongEntry.COLUMN_ADDED_AT, System.currentTimeMillis());
        long row = db.insertWithOnConflict(DatabaseContract.PlaylistSongEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        // Update playlist updated_at
        ContentValues updateCv = new ContentValues();
        updateCv.put(DatabaseContract.PlaylistEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
        db.update(DatabaseContract.PlaylistEntry.TABLE_NAME, updateCv,
                DatabaseContract.PlaylistEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(playlistId)});

        return row != -1;
    }

    public boolean removeSongFromPlaylist(int playlistId, int songId) {
        return db.delete(DatabaseContract.PlaylistSongEntry.TABLE_NAME,
                DatabaseContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + " = ? AND " +
                DatabaseContract.PlaylistSongEntry.COLUMN_SONG_ID + " = ?",
                new String[]{String.valueOf(playlistId), String.valueOf(songId)}) > 0;
    }

    public List<SongItem> getSongsInPlaylist(int playlistId) {
        List<SongItem> list = new ArrayList<>();
        String sql = "SELECT s.id, s.title, s.artist_id, a.name AS artist_name, " +
                "s.album_id, al.title AS album_title, s.duration_ms, " +
                "s.audio_resource, s.image_resource, " +
                "CASE WHEN l.song_id IS NOT NULL THEN 1 ELSE 0 END AS is_liked " +
                "FROM " + DatabaseContract.PlaylistSongEntry.TABLE_NAME + " ps " +
                "INNER JOIN " + DatabaseContract.SongEntry.TABLE_NAME + " s ON ps.song_id = s.id " +
                "LEFT JOIN " + DatabaseContract.ArtistEntry.TABLE_NAME + " a ON s.artist_id = a.id " +
                "LEFT JOIN " + DatabaseContract.AlbumEntry.TABLE_NAME + " al ON s.album_id = al.id " +
                "LEFT JOIN " + DatabaseContract.LikedSongEntry.TABLE_NAME + " l ON s.id = l.song_id " +
                "WHERE ps.playlist_id = ? " +
                "ORDER BY ps.position ASC;";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(playlistId)});
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
    private Playlist cursorToPlaylist(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        String coverRes = cursor.getString(cursor.getColumnIndex("cover_res"));
        int songCount = cursor.getInt(cursor.getColumnIndex("song_count"));
        long createdAt = cursor.getLong(cursor.getColumnIndex("created_at"));
        long updatedAt = cursor.getLong(cursor.getColumnIndex("updated_at"));
        return new Playlist(id, name, description, coverRes, songCount, createdAt, updatedAt);
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
