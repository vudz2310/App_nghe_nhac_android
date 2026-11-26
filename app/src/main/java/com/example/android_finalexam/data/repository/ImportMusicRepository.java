package com.example.android_finalexam.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.example.android_finalexam.data.local.dao.ImportJobDao;
import com.example.android_finalexam.data.local.dao.SongDao;
import com.example.android_finalexam.data.local.database.DatabaseContract;
import com.example.android_finalexam.data.local.database.MusicDatabaseHelper;
import com.example.android_finalexam.domain.engine.AudioFileValidator;
import com.example.android_finalexam.domain.engine.AudioMetadataReader;
import com.example.android_finalexam.domain.engine.DownloadEngine;
import com.example.android_finalexam.domain.engine.MusicStorageManager;
import com.example.android_finalexam.domain.engine.UrlValidator;
import com.example.android_finalexam.domain.model.AudioMetadata;
import com.example.android_finalexam.domain.model.SongItem;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImportMusicRepository {

    public interface OnDownloadProgressCallback {
        void onProgress(long bytesRead, long totalBytes, int percent);
    }

    public interface OnMetadataExtractedCallback {
        void onSuccess(AudioMetadata metadata);
        void onError(String message);
    }

    public interface OnSaveCompletedCallback {
        void onSuccess(SongItem savedSong);
        void onError(String message);
    }

    private final Context appContext;
    private final MusicDatabaseHelper dbHelper;
    private final SongDao songDao;
    private final ImportJobDao importJobDao;
    private final DownloadEngine downloadEngine = new DownloadEngine();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ImportMusicRepository(Context context) {
        this.appContext = context.getApplicationContext();
        this.dbHelper = MusicDatabaseHelper.getInstance(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        this.songDao = new SongDao(db);
        this.importJobDao = new ImportJobDao(db);
    }

    public void startDownloadAndExtraction(String url, OnDownloadProgressCallback progressCallback, OnMetadataExtractedCallback metadataCallback) {
        executor.execute(() -> {
            // 1. Validate URL & Security
            UrlValidator.ValidationResult valResult = UrlValidator.validate(url);
            if (!valResult.isValid()) {
                mainHandler.post(() -> metadataCallback.onError(valResult.getErrorMessage()));
                return;
            }

            String urlHash = UrlValidator.computeSha256(url);
            long jobId = importJobDao.createJob(urlHash, "download.part");

            try {
                // 2. Download stream to temp file
                File tempFile = downloadEngine.downloadAudio(appContext, url, urlHash, (bytesRead, totalBytes, percent) -> {
                    importJobDao.updateProgress(jobId, percent);
                    mainHandler.post(() -> {
                        if (progressCallback != null) {
                            progressCallback.onProgress(bytesRead, totalBytes, percent);
                        }
                    });
                });

                // 3. Validate audio file signature
                AudioFileValidator.FileValidationResult fileResult = AudioFileValidator.validateAudioFile(tempFile);
                if (!fileResult.isValidAudio()) {
                    tempFile.delete();
                    importJobDao.markFailed(jobId, "ERR_INVALID_FORMAT", fileResult.getErrorMessage());
                    mainHandler.post(() -> metadataCallback.onError(fileResult.getErrorMessage()));
                    return;
                }

                // 4. Extract metadata with MediaMetadataRetriever
                AudioMetadata metadata = AudioMetadataReader.extractMetadata(appContext, tempFile, valResult.getNormalizedHost(), fileResult.getChecksumSha256());
                metadata.setMimeType(fileResult.getDetectedFormat());

                mainHandler.post(() -> metadataCallback.onSuccess(metadata));

            } catch (Exception e) {
                importJobDao.markFailed(jobId, "ERR_NETWORK", e.getMessage());
                mainHandler.post(() -> metadataCallback.onError("Lỗi tải tệp: " + e.getMessage()));
            }
        });
    }

    public void cancelDownload() {
        downloadEngine.cancel();
    }

    public void confirmAndSave(AudioMetadata metadata, String storageMode, OnSaveCompletedCallback callback) {
        executor.execute(() -> {
            File tempFile = new File(metadata.getTempFilePath());
            if (!tempFile.exists()) {
                mainHandler.post(() -> callback.onError("Tệp tạm thời đã bị xóa hoặc không tồn tại. Vui lòng thử lại."));
                return;
            }

            try {
                // 1. Save to Storage (BOTH / APP / PUBLIC)
                String fileName = metadata.getSuggestedFileName();
                MusicStorageManager.StorageSaveResult saveResult =
                        MusicStorageManager.saveByMode(appContext, tempFile, fileName, metadata.getMimeType(), storageMode);

                if (!saveResult.isSuccess()) {
                    mainHandler.post(() -> callback.onError(saveResult.getErrorMessage()));
                    return;
                }

                // 2. Find or Insert Artist
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int artistId = findOrInsertArtist(db, metadata.getArtist(), metadata.getArtworkLocalPath());

                // 3. Find or Insert Album (if present)
                Integer albumId = null;
                if (metadata.getAlbum() != null && !metadata.getAlbum().trim().isEmpty()) {
                    albumId = findOrInsertAlbum(db, metadata.getAlbum(), artistId, metadata.getArtworkLocalPath());
                }

                // 4. Insert into songs table
                SongItem song = new SongItem();
                song.setTitle(metadata.getTitle());
                song.setArtistId(artistId);
                song.setArtistName(metadata.getArtist());
                song.setAlbumId(albumId);
                song.setAlbumTitle(metadata.getAlbum());
                song.setDurationMs(metadata.getDurationMs());
                song.setCoverRes(metadata.getArtworkLocalPath() != null ? metadata.getArtworkLocalPath() : "playinglogo");
                song.setSourceType("IMPORTED");
                song.setMimeType(metadata.getMimeType());
                song.setFileSize(metadata.getFileSize());
                song.setFileName(fileName);
                song.setAppFilePath(saveResult.getAppFile() != null ? saveResult.getAppFile().getAbsolutePath() : null);
                song.setPublicContentUri(saveResult.getPublicUri() != null ? saveResult.getPublicUri().toString() : null);
                song.setStorageMode(saveResult.getActualStorageMode());
                song.setSourceHost(metadata.getSourceHost());
                song.setChecksumSha256(metadata.getChecksumSha256());

                long songId = songDao.insertImportedSong(song);
                song.setId((int) songId);

                // 5. Clean temp file
                tempFile.delete();

                mainHandler.post(() -> callback.onSuccess(song));

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Lỗi lưu trữ cơ sở dữ liệu: " + e.getMessage()));
            }
        });
    }

    private int findOrInsertArtist(SQLiteDatabase db, String artistName, String avatarPath) {
        Cursor c = db.query(DatabaseContract.ArtistEntry.TABLE_NAME,
                new String[]{DatabaseContract.ArtistEntry.COLUMN_ID},
                DatabaseContract.ArtistEntry.COLUMN_NAME + " = ?",
                new String[]{artistName}, null, null, null, "1");
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(0);
                }
            } finally {
                c.close();
            }
        }

        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ArtistEntry.COLUMN_NAME, artistName);
        cv.put(DatabaseContract.ArtistEntry.COLUMN_AVATAR_RES, avatarPath);
        cv.put(DatabaseContract.ArtistEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
        return (int) db.insert(DatabaseContract.ArtistEntry.TABLE_NAME, null, cv);
    }

    private int findOrInsertAlbum(SQLiteDatabase db, String albumTitle, int artistId, String coverPath) {
        Cursor c = db.query(DatabaseContract.AlbumEntry.TABLE_NAME,
                new String[]{DatabaseContract.AlbumEntry.COLUMN_ID},
                DatabaseContract.AlbumEntry.COLUMN_TITLE + " = ? AND " + DatabaseContract.AlbumEntry.COLUMN_ARTIST_ID + " = ?",
                new String[]{albumTitle, String.valueOf(artistId)}, null, null, null, "1");
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(0);
                }
            } finally {
                c.close();
            }
        }

        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.AlbumEntry.COLUMN_TITLE, albumTitle);
        cv.put(DatabaseContract.AlbumEntry.COLUMN_ARTIST_ID, artistId);
        cv.put(DatabaseContract.AlbumEntry.COLUMN_COVER_RES, coverPath);
        cv.put(DatabaseContract.AlbumEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
        return (int) db.insert(DatabaseContract.AlbumEntry.TABLE_NAME, null, cv);
    }
}
