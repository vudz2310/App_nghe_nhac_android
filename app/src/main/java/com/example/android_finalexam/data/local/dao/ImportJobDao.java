package com.example.android_finalexam.data.local.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android_finalexam.data.local.database.DatabaseContract;

public class ImportJobDao {

    private final SQLiteDatabase db;

    public ImportJobDao(SQLiteDatabase db) {
        this.db = db;
    }

    public long createJob(String urlHash, String fileName) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_SOURCE_URL_HASH, urlHash);
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_FILE_NAME, fileName);
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_STATUS, "DOWNLOADING");
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_PROGRESS, 0);
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
        return db.insert(DatabaseContract.ImportJobEntry.TABLE_NAME, null, cv);
    }

    public void updateProgress(long jobId, int progress) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_PROGRESS, progress);
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
        db.update(DatabaseContract.ImportJobEntry.TABLE_NAME, cv,
                DatabaseContract.ImportJobEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(jobId)});
    }

    public void markCompleted(long jobId) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_STATUS, "COMPLETED");
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_PROGRESS, 100);
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
        db.update(DatabaseContract.ImportJobEntry.TABLE_NAME, cv,
                DatabaseContract.ImportJobEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(jobId)});
    }

    public void markFailed(long jobId, String errorCode, String errorMessage) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_STATUS, "FAILED");
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_ERROR_CODE, errorCode);
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_ERROR_MESSAGE, errorMessage);
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
        db.update(DatabaseContract.ImportJobEntry.TABLE_NAME, cv,
                DatabaseContract.ImportJobEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(jobId)});
    }

    public void markCancelled(long jobId) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_STATUS, "CANCELLED");
        cv.put(DatabaseContract.ImportJobEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());
        db.update(DatabaseContract.ImportJobEntry.TABLE_NAME, cv,
                DatabaseContract.ImportJobEntry.COLUMN_ID + " = ?", new String[]{String.valueOf(jobId)});
    }
}
