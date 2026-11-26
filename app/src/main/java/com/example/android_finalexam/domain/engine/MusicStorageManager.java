package com.example.android_finalexam.domain.engine;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MusicStorageManager {

    public static class StorageSaveResult {
        private final File appFile;
        private final Uri publicUri;
        private final String actualStorageMode;
        private final boolean isSuccess;
        private final String errorMessage;

        public StorageSaveResult(File appFile, Uri publicUri, String actualStorageMode, boolean isSuccess, String errorMessage) {
            this.appFile = appFile;
            this.publicUri = publicUri;
            this.actualStorageMode = actualStorageMode;
            this.isSuccess = isSuccess;
            this.errorMessage = errorMessage;
        }

        public File getAppFile() { return appFile; }
        public Uri getPublicUri() { return publicUri; }
        public String getActualStorageMode() { return actualStorageMode; }
        public boolean isSuccess() { return isSuccess; }
        public String getErrorMessage() { return errorMessage; }
    }

    public static File saveToAppStorage(Context context, File sourceFile, String targetFileName) throws IOException {
        File musicDir = new File(context.getFilesDir(), "music");
        if (!musicDir.exists()) {
            musicDir.mkdirs();
        }

        String safeName = System.currentTimeMillis() + "_" + targetFileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        File destFile = new File(musicDir, safeName);

        copyFile(sourceFile, destFile);

        if (!destFile.exists() || destFile.length() == 0) {
            throw new IOException("Không thể lưu tệp vào bộ nhớ ứng dụng.");
        }

        return destFile;
    }

    public static Uri saveToPublicMusic(Context context, File sourceFile, String displayName, String mimeType) throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Audio.Media.MIME_TYPE, mimeType != null ? mimeType : "audio/mpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/MyMusic");
            values.put(MediaStore.Audio.Media.IS_PENDING, 1);
        }

        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri uri = context.getContentResolver().insert(collection, values);

        if (uri == null) {
            throw new IOException("Không thể tạo bản ghi MediaStore trong thư mục Music.");
        }

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = context.getContentResolver().openOutputStream(uri)) {

            if (out == null) {
                throw new IOException("Không thể mở luồng ghi vào MediaStore.");
            }

            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear();
                values.put(MediaStore.Audio.Media.IS_PENDING, 0);
                context.getContentResolver().update(uri, values, null, null);
            }

            return uri;
        } catch (Exception e) {
            context.getContentResolver().delete(uri, null, null);
            throw new IOException("Lỗi sao chép vào Public MediaStore: " + e.getMessage(), e);
        }
    }

    public static StorageSaveResult saveByMode(Context context, File sourceFile, String fileName, String mimeType, String requestedMode) {
        File appSaved = null;
        Uri publicSaved = null;
        String finalMode = "APP";

        boolean saveApp = "APP".equalsIgnoreCase(requestedMode) || "BOTH".equalsIgnoreCase(requestedMode);
        boolean savePublic = "PUBLIC".equalsIgnoreCase(requestedMode) || "BOTH".equalsIgnoreCase(requestedMode);

        try {
            if (saveApp) {
                appSaved = saveToAppStorage(context, sourceFile, fileName);
            }
        } catch (Exception e) {
            if (!savePublic) {
                return new StorageSaveResult(null, null, null, false, "Lỗi lưu App Storage: " + e.getMessage());
            }
        }

        try {
            if (savePublic) {
                publicSaved = saveToPublicMusic(context, sourceFile, fileName, mimeType);
            }
        } catch (Exception e) {
            if (!saveApp || appSaved == null) {
                return new StorageSaveResult(null, null, null, false, "Lỗi lưu Public Music: " + e.getMessage());
            }
        }

        if (appSaved != null && publicSaved != null) {
            finalMode = "BOTH";
        } else if (appSaved != null) {
            finalMode = "APP";
        } else if (publicSaved != null) {
            finalMode = "PUBLIC";
        } else {
            return new StorageSaveResult(null, null, null, false, "Không thể lưu tệp vào bất kỳ vị trí nào.");
        }

        return new StorageSaveResult(appSaved, publicSaved, finalMode, true, null);
    }

    private static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
        }
    }
}
