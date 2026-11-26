package com.example.android_finalexam.data.local.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDatabaseHelper extends SQLiteOpenHelper {

    private static volatile MusicDatabaseHelper instance;
    private final Context appContext;

    public static synchronized MusicDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MusicDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public MusicDatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        this.appContext = context.getApplicationContext();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAllTables(db);
        createIndexes(db);
        DatabaseSeeder.seedDefaultData(db);
    }

    private void createAllTables(SQLiteDatabase db) {
        // 1. artists
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.ArtistEntry.TABLE_NAME + " (" +
                DatabaseContract.ArtistEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.ArtistEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DatabaseContract.ArtistEntry.COLUMN_AVATAR_RES + " TEXT, " +
                DatabaseContract.ArtistEntry.COLUMN_BIO + " TEXT, " +
                DatabaseContract.ArtistEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000)" +
                ");");

        // 2. albums
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.AlbumEntry.TABLE_NAME + " (" +
                DatabaseContract.AlbumEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.AlbumEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                DatabaseContract.AlbumEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL, " +
                DatabaseContract.AlbumEntry.COLUMN_COVER_RES + " TEXT, " +
                DatabaseContract.AlbumEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                DatabaseContract.AlbumEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000), " +
                "FOREIGN KEY (" + DatabaseContract.AlbumEntry.COLUMN_ARTIST_ID + ") REFERENCES " +
                DatabaseContract.ArtistEntry.TABLE_NAME + " (" + DatabaseContract.ArtistEntry.COLUMN_ID + ") ON DELETE CASCADE" +
                ");");

        // 3. songs (Extended with Import Music columns)
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.SongEntry.TABLE_NAME + " (" +
                DatabaseContract.SongEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.SongEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                DatabaseContract.SongEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL, " +
                DatabaseContract.SongEntry.COLUMN_ALBUM_ID + " INTEGER, " +
                DatabaseContract.SongEntry.COLUMN_DURATION_MS + " INTEGER NOT NULL DEFAULT 0, " +
                DatabaseContract.SongEntry.COLUMN_AUDIO_RESOURCE + " TEXT, " +
                DatabaseContract.SongEntry.COLUMN_IMAGE_RESOURCE + " TEXT, " +
                DatabaseContract.SongEntry.COLUMN_SOURCE_TYPE + " TEXT NOT NULL DEFAULT 'ASSET', " +
                DatabaseContract.SongEntry.COLUMN_MIME_TYPE + " TEXT, " +
                DatabaseContract.SongEntry.COLUMN_FILE_SIZE + " INTEGER NOT NULL DEFAULT 0, " +
                DatabaseContract.SongEntry.COLUMN_FILE_NAME + " TEXT, " +
                DatabaseContract.SongEntry.COLUMN_APP_FILE_PATH + " TEXT, " +
                DatabaseContract.SongEntry.COLUMN_PUBLIC_CONTENT_URI + " TEXT, " +
                DatabaseContract.SongEntry.COLUMN_STORAGE_MODE + " TEXT NOT NULL DEFAULT 'APP', " +
                DatabaseContract.SongEntry.COLUMN_SOURCE_HOST + " TEXT, " +
                DatabaseContract.SongEntry.COLUMN_CHECKSUM_SHA256 + " TEXT, " +
                DatabaseContract.SongEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000), " +
                "FOREIGN KEY (" + DatabaseContract.SongEntry.COLUMN_ARTIST_ID + ") REFERENCES " +
                DatabaseContract.ArtistEntry.TABLE_NAME + " (" + DatabaseContract.ArtistEntry.COLUMN_ID + ") ON DELETE RESTRICT, " +
                "FOREIGN KEY (" + DatabaseContract.SongEntry.COLUMN_ALBUM_ID + ") REFERENCES " +
                DatabaseContract.AlbumEntry.TABLE_NAME + " (" + DatabaseContract.AlbumEntry.COLUMN_ID + ") ON DELETE SET NULL" +
                ");");

        // 4. playlists
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.PlaylistEntry.TABLE_NAME + " (" +
                DatabaseContract.PlaylistEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.PlaylistEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DatabaseContract.PlaylistEntry.COLUMN_DESCRIPTION + " TEXT, " +
                DatabaseContract.PlaylistEntry.COLUMN_COVER_RES + " TEXT, " +
                DatabaseContract.PlaylistEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000), " +
                DatabaseContract.PlaylistEntry.COLUMN_UPDATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000)" +
                ");");

        // 5. playlist_songs
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.PlaylistSongEntry.TABLE_NAME + " (" +
                DatabaseContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + " INTEGER NOT NULL, " +
                DatabaseContract.PlaylistSongEntry.COLUMN_SONG_ID + " INTEGER NOT NULL, " +
                DatabaseContract.PlaylistSongEntry.COLUMN_POSITION + " INTEGER NOT NULL DEFAULT 0, " +
                DatabaseContract.PlaylistSongEntry.COLUMN_ADDED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000), " +
                "PRIMARY KEY (" + DatabaseContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + ", " + DatabaseContract.PlaylistSongEntry.COLUMN_SONG_ID + "), " +
                "FOREIGN KEY (" + DatabaseContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + ") REFERENCES " +
                DatabaseContract.PlaylistEntry.TABLE_NAME + " (" + DatabaseContract.PlaylistEntry.COLUMN_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DatabaseContract.PlaylistSongEntry.COLUMN_SONG_ID + ") REFERENCES " +
                DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_ID + ") ON DELETE CASCADE" +
                ");");

        // 6. liked_songs
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.LikedSongEntry.TABLE_NAME + " (" +
                DatabaseContract.LikedSongEntry.COLUMN_SONG_ID + " INTEGER PRIMARY KEY, " +
                DatabaseContract.LikedSongEntry.COLUMN_LIKED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000), " +
                "FOREIGN KEY (" + DatabaseContract.LikedSongEntry.COLUMN_SONG_ID + ") REFERENCES " +
                DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_ID + ") ON DELETE CASCADE" +
                ");");

        // 7. listening_history
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.HistoryEntry.TABLE_NAME + " (" +
                DatabaseContract.HistoryEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.HistoryEntry.COLUMN_SONG_ID + " INTEGER NOT NULL, " +
                DatabaseContract.HistoryEntry.COLUMN_PLAYED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000), " +
                DatabaseContract.HistoryEntry.COLUMN_PROGRESS_MS + " INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY (" + DatabaseContract.HistoryEntry.COLUMN_SONG_ID + ") REFERENCES " +
                DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_ID + ") ON DELETE CASCADE" +
                ");");

        // 8. import_jobs
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.ImportJobEntry.TABLE_NAME + " (" +
                DatabaseContract.ImportJobEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.ImportJobEntry.COLUMN_SOURCE_URL_HASH + " TEXT NOT NULL, " +
                DatabaseContract.ImportJobEntry.COLUMN_FILE_NAME + " TEXT, " +
                DatabaseContract.ImportJobEntry.COLUMN_STATUS + " TEXT NOT NULL, " +
                DatabaseContract.ImportJobEntry.COLUMN_PROGRESS + " INTEGER NOT NULL DEFAULT 0, " +
                DatabaseContract.ImportJobEntry.COLUMN_TEMP_PATH + " TEXT, " +
                DatabaseContract.ImportJobEntry.COLUMN_ERROR_CODE + " TEXT, " +
                DatabaseContract.ImportJobEntry.COLUMN_ERROR_MESSAGE + " TEXT, " +
                DatabaseContract.ImportJobEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000), " +
                DatabaseContract.ImportJobEntry.COLUMN_UPDATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000)" +
                ");");
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_title ON " + DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_TITLE + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_artist_id ON " + DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_ARTIST_ID + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_album_id ON " + DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_ALBUM_ID + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_source_type ON " + DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_SOURCE_TYPE + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_checksum ON " + DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_CHECKSUM_SHA256 + ");");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_artists_name ON " + DatabaseContract.ArtistEntry.TABLE_NAME + " (" + DatabaseContract.ArtistEntry.COLUMN_NAME + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_albums_title ON " + DatabaseContract.AlbumEntry.TABLE_NAME + " (" + DatabaseContract.AlbumEntry.COLUMN_TITLE + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_playlist_songs_pos ON " + DatabaseContract.PlaylistSongEntry.TABLE_NAME + " (" + DatabaseContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + ", " + DatabaseContract.PlaylistSongEntry.COLUMN_POSITION + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_history_played_at ON " + DatabaseContract.HistoryEntry.TABLE_NAME + " (" + DatabaseContract.HistoryEntry.COLUMN_PLAYED_AT + " DESC);");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_import_jobs_created ON " + DatabaseContract.ImportJobEntry.TABLE_NAME + " (" + DatabaseContract.ImportJobEntry.COLUMN_CREATED_AT + " DESC);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS favorites;");
            db.execSQL("DROP TABLE IF EXISTS songs;");
            createAllTables(db);
            createIndexes(db);
            DatabaseSeeder.seedDefaultData(db);
            return;
        }

        if (oldVersion < 3) {
            // Safe migration from v2 to v3 for Import Music
            tryAddColumn(db, DatabaseContract.SongEntry.TABLE_NAME, DatabaseContract.SongEntry.COLUMN_SOURCE_TYPE, "TEXT NOT NULL DEFAULT 'ASSET'");
            tryAddColumn(db, DatabaseContract.SongEntry.TABLE_NAME, DatabaseContract.SongEntry.COLUMN_MIME_TYPE, "TEXT");
            tryAddColumn(db, DatabaseContract.SongEntry.TABLE_NAME, DatabaseContract.SongEntry.COLUMN_FILE_SIZE, "INTEGER NOT NULL DEFAULT 0");
            tryAddColumn(db, DatabaseContract.SongEntry.TABLE_NAME, DatabaseContract.SongEntry.COLUMN_FILE_NAME, "TEXT");
            tryAddColumn(db, DatabaseContract.SongEntry.TABLE_NAME, DatabaseContract.SongEntry.COLUMN_APP_FILE_PATH, "TEXT");
            tryAddColumn(db, DatabaseContract.SongEntry.TABLE_NAME, DatabaseContract.SongEntry.COLUMN_PUBLIC_CONTENT_URI, "TEXT");
            tryAddColumn(db, DatabaseContract.SongEntry.TABLE_NAME, DatabaseContract.SongEntry.COLUMN_STORAGE_MODE, "TEXT NOT NULL DEFAULT 'APP'");
            tryAddColumn(db, DatabaseContract.SongEntry.TABLE_NAME, DatabaseContract.SongEntry.COLUMN_SOURCE_HOST, "TEXT");
            tryAddColumn(db, DatabaseContract.SongEntry.TABLE_NAME, DatabaseContract.SongEntry.COLUMN_CHECKSUM_SHA256, "TEXT");

            // Create import_jobs table
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.ImportJobEntry.TABLE_NAME + " (" +
                    DatabaseContract.ImportJobEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DatabaseContract.ImportJobEntry.COLUMN_SOURCE_URL_HASH + " TEXT NOT NULL, " +
                    DatabaseContract.ImportJobEntry.COLUMN_FILE_NAME + " TEXT, " +
                    DatabaseContract.ImportJobEntry.COLUMN_STATUS + " TEXT NOT NULL, " +
                    DatabaseContract.ImportJobEntry.COLUMN_PROGRESS + " INTEGER NOT NULL DEFAULT 0, " +
                    DatabaseContract.ImportJobEntry.COLUMN_TEMP_PATH + " TEXT, " +
                    DatabaseContract.ImportJobEntry.COLUMN_ERROR_CODE + " TEXT, " +
                    DatabaseContract.ImportJobEntry.COLUMN_ERROR_MESSAGE + " TEXT, " +
                    DatabaseContract.ImportJobEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000), " +
                    DatabaseContract.ImportJobEntry.COLUMN_UPDATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000)" +
                    ");");

            createIndexes(db);
        }

        if (oldVersion < 4) {
            // v4: Reseed with ZingMP3 Top 100 catalog data
            db.execSQL("DELETE FROM " + DatabaseContract.PlaylistSongEntry.TABLE_NAME + ";");
            db.execSQL("DELETE FROM " + DatabaseContract.LikedSongEntry.TABLE_NAME + ";");
            db.execSQL("DELETE FROM " + DatabaseContract.HistoryEntry.TABLE_NAME + ";");
            db.execSQL("DELETE FROM " + DatabaseContract.SongEntry.TABLE_NAME + ";");
            db.execSQL("DELETE FROM " + DatabaseContract.AlbumEntry.TABLE_NAME + ";");
            db.execSQL("DELETE FROM " + DatabaseContract.ArtistEntry.TABLE_NAME + ";");
            db.execSQL("DELETE FROM " + DatabaseContract.PlaylistEntry.TABLE_NAME + ";");
            DatabaseSeeder.seedDefaultData(db);
        }
    }

    private void tryAddColumn(SQLiteDatabase db, String tableName, String columnName, String columnType) {
        try {
            db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType + ";");
        } catch (Exception ignored) {
            // Column already exists or error ignored
        }
    }
}
