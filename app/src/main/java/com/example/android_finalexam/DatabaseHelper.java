package com.example.android_finalexam;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "songs_database";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_SONGS = "songs";
    public static final String TABLE_FAVORITES = "favorites";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_IMAGE_RESOURCE = "image_resource";
    public static final String COLUMN_AUDIO_RESOURCE = "audio_resource";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSongsTable = "CREATE TABLE " + TABLE_SONGS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_ARTIST + " TEXT, " +
                COLUMN_IMAGE_RESOURCE + " TEXT, " +
                COLUMN_AUDIO_RESOURCE + " TEXT)";
        db.execSQL(createSongsTable);

        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_ARTIST + " TEXT, " +
                COLUMN_IMAGE_RESOURCE + " TEXT, " +
                COLUMN_AUDIO_RESOURCE + " TEXT)";
        db.execSQL(createFavoritesTable);

        insertDefaultSongs(db);
    }

    public void insertDefaultSongs(SQLiteDatabase db) {
        insertSong(db, "Viva La Vida", "Coldplay", "viva_lavi_da", "viva_lavi_da__coldplay");
        insertSong(db, "Mười Tám", "Kis", "muoitam", "muoitam_kis");
        insertSong(db, "Dead Feelings", "Moshi", "dead_feelings", "dead_feelings__moshi");
        insertSong(db, "Wave Alpha", "7uppercuts, Cam", "wave_alpha", "wave_alpha__7uppercuts_cam");
        insertSong(db, "Đừng Làm Trái Tim Anh Đau", "Sơn Tùng M-TP", "anh1", "dunglamtraitimanhdau");
        insertSong(db, "I Need Your Love", "Magnus Carlsson", "anh1", "ineedyourlove_magnus_carlsson");
    }

    private void insertSong(SQLiteDatabase db, String title, String artist, String imageResource, String audioResource) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_ARTIST, artist);
        values.put(COLUMN_IMAGE_RESOURCE, imageResource);
        values.put(COLUMN_AUDIO_RESOURCE, audioResource);
        db.insert(TABLE_SONGS, null, values);
    }

    public void seedDefaultSongsIfEmpty(SQLiteDatabase db) {
        android.database.Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SONGS, null);
        if (cursor != null) {
            if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                insertDefaultSongs(db);
            }
            cursor.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    public boolean deleteSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_SONGS, COLUMN_ID + " = ?", new String[]{String.valueOf(song.getId())});
        return deletedRows > 0;
    }

    public boolean updateSong(int id, String newTitle, String newArtist, String newImageResource, String newAudioResource) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, newTitle);
        contentValues.put(COLUMN_ARTIST, newArtist);
        contentValues.put(COLUMN_IMAGE_RESOURCE, newImageResource);
        contentValues.put(COLUMN_AUDIO_RESOURCE, newAudioResource);

        int rowsAffected = db.update(TABLE_SONGS, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

}


