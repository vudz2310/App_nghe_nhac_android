package com.example.android_finalexam.data.local.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android_finalexam.data.local.database.DatabaseContract;
import com.example.android_finalexam.domain.model.SongItem;

import java.util.ArrayList;
import java.util.List;

public class SongDao {

    private final SQLiteDatabase db;

    public SongDao(SQLiteDatabase db) {
        this.db = db;
    }

    private static final String SONG_QUERY_BASE =
            "SELECT s.id, s.title, s.artist_id, a.name AS artist_name, " +
            "s.album_id, al.title AS album_title, s.duration_ms, " +
            "s.audio_resource, s.image_resource, " +
            "s.source_type, s.mime_type, s.file_size, s.file_name, " +
            "s.app_file_path, s.public_content_uri, s.storage_mode, s.source_host, s.checksum_sha256, " +
            "CASE WHEN l.song_id IS NOT NULL THEN 1 ELSE 0 END AS is_liked " +
            "FROM " + DatabaseContract.SongEntry.TABLE_NAME + " s " +
            "LEFT JOIN " + DatabaseContract.ArtistEntry.TABLE_NAME + " a ON s.artist_id = a.id " +
            "LEFT JOIN " + DatabaseContract.AlbumEntry.TABLE_NAME + " al ON s.album_id = al.id " +
            "LEFT JOIN " + DatabaseContract.LikedSongEntry.TABLE_NAME + " l ON s.id = l.song_id ";

    public List<SongItem> getAllSongs() {
        List<SongItem> list = new ArrayList<>();
        String sql = SONG_QUERY_BASE + "ORDER BY s.id DESC;";
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

    public SongItem getSongById(int id) {
        String sql = SONG_QUERY_BASE + "WHERE s.id = ? LIMIT 1;";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursorToSongItem(cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public List<SongItem> searchSongs(String keyword) {
        List<SongItem> list = new ArrayList<>();
        String query = "%" + (keyword != null ? keyword.trim().toLowerCase() : "") + "%";
        String sql = SONG_QUERY_BASE +
                "WHERE LOWER(s.title) LIKE ? OR LOWER(a.name) LIKE ? " +
                "ORDER BY s.title ASC;";
        Cursor cursor = db.rawQuery(sql, new String[]{query, query});
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

    public List<SongItem> getSongsByAlbumId(int albumId) {
        List<SongItem> list = new ArrayList<>();
        String sql = SONG_QUERY_BASE + "WHERE s.album_id = ? ORDER BY s.id ASC;";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(albumId)});
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

    public List<SongItem> getSongsByArtistId(int artistId) {
        List<SongItem> list = new ArrayList<>();
        String sql = SONG_QUERY_BASE + "WHERE s.artist_id = ? ORDER BY s.id ASC;";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(artistId)});
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

    public long insertImportedSong(SongItem song) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.SongEntry.COLUMN_TITLE, song.getTitle());
        cv.put(DatabaseContract.SongEntry.COLUMN_ARTIST_ID, song.getArtistId());
        if (song.getAlbumId() != null) {
            cv.put(DatabaseContract.SongEntry.COLUMN_ALBUM_ID, song.getAlbumId());
        }
        cv.put(DatabaseContract.SongEntry.COLUMN_DURATION_MS, song.getDurationMs());
        cv.put(DatabaseContract.SongEntry.COLUMN_AUDIO_RESOURCE, song.getAudioRes());
        cv.put(DatabaseContract.SongEntry.COLUMN_IMAGE_RESOURCE, song.getCoverRes());
        cv.put(DatabaseContract.SongEntry.COLUMN_SOURCE_TYPE, "IMPORTED");
        cv.put(DatabaseContract.SongEntry.COLUMN_MIME_TYPE, song.getMimeType());
        cv.put(DatabaseContract.SongEntry.COLUMN_FILE_SIZE, song.getFileSize());
        cv.put(DatabaseContract.SongEntry.COLUMN_FILE_NAME, song.getFileName());
        cv.put(DatabaseContract.SongEntry.COLUMN_APP_FILE_PATH, song.getAppFilePath());
        cv.put(DatabaseContract.SongEntry.COLUMN_PUBLIC_CONTENT_URI, song.getPublicContentUri());
        cv.put(DatabaseContract.SongEntry.COLUMN_STORAGE_MODE, song.getStorageMode());
        cv.put(DatabaseContract.SongEntry.COLUMN_SOURCE_HOST, song.getSourceHost());
        cv.put(DatabaseContract.SongEntry.COLUMN_CHECKSUM_SHA256, song.getChecksumSha256());
        cv.put(DatabaseContract.SongEntry.COLUMN_CREATED_AT, System.currentTimeMillis());

        return db.insert(DatabaseContract.SongEntry.TABLE_NAME, null, cv);
    }

    public boolean deleteSong(int id) {
        return db.delete(DatabaseContract.SongEntry.TABLE_NAME,
                DatabaseContract.SongEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    @SuppressLint("Range")
    private SongItem cursorToSongItem(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String title = cursor.getString(cursor.getColumnIndex("title"));
        int artistId = cursor.getInt(cursor.getColumnIndex("artist_id"));
        String artistName = cursor.getString(cursor.getColumnIndex("artist_name"));

        Integer albumId = null;
        if (!cursor.isNull(cursor.getColumnIndex("album_id"))) {
            albumId = cursor.getInt(cursor.getColumnIndex("album_id"));
        }
        String albumTitle = cursor.getString(cursor.getColumnIndex("album_title"));
        int durationMs = cursor.getInt(cursor.getColumnIndex("duration_ms"));
        String audioRes = cursor.getString(cursor.getColumnIndex("audio_resource"));
        String coverRes = cursor.getString(cursor.getColumnIndex("image_resource"));
        boolean isLiked = cursor.getInt(cursor.getColumnIndex("is_liked")) == 1;

        SongItem item = new SongItem(id, title, artistId, artistName, albumId, albumTitle, durationMs, audioRes, coverRes, isLiked);

        // Extended fields if column exists
        int idxSourceType = cursor.getColumnIndex("source_type");
        if (idxSourceType != -1 && !cursor.isNull(idxSourceType)) {
            item.setSourceType(cursor.getString(idxSourceType));
        }
        int idxMime = cursor.getColumnIndex("mime_type");
        if (idxMime != -1 && !cursor.isNull(idxMime)) {
            item.setMimeType(cursor.getString(idxMime));
        }
        int idxSize = cursor.getColumnIndex("file_size");
        if (idxSize != -1 && !cursor.isNull(idxSize)) {
            item.setFileSize(cursor.getLong(idxSize));
        }
        int idxFileName = cursor.getColumnIndex("file_name");
        if (idxFileName != -1 && !cursor.isNull(idxFileName)) {
            item.setFileName(cursor.getString(idxFileName));
        }
        int idxAppPath = cursor.getColumnIndex("app_file_path");
        if (idxAppPath != -1 && !cursor.isNull(idxAppPath)) {
            item.setAppFilePath(cursor.getString(idxAppPath));
        }
        int idxPublicUri = cursor.getColumnIndex("public_content_uri");
        if (idxPublicUri != -1 && !cursor.isNull(idxPublicUri)) {
            item.setPublicContentUri(cursor.getString(idxPublicUri));
        }
        int idxStorageMode = cursor.getColumnIndex("storage_mode");
        if (idxStorageMode != -1 && !cursor.isNull(idxStorageMode)) {
            item.setStorageMode(cursor.getString(idxStorageMode));
        }
        int idxHost = cursor.getColumnIndex("source_host");
        if (idxHost != -1 && !cursor.isNull(idxHost)) {
            item.setSourceHost(cursor.getString(idxHost));
        }
        int idxChecksum = cursor.getColumnIndex("checksum_sha256");
        if (idxChecksum != -1 && !cursor.isNull(idxChecksum)) {
            item.setChecksumSha256(cursor.getString(idxChecksum));
        }

        return item;
    }
}
