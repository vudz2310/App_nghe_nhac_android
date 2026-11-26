package com.example.android_finalexam.domain.engine;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadEngine {

    public interface DownloadProgressListener {
        void onProgress(long bytesDownloaded, long totalBytes, int percent);
    }

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100 MB default limit

    private final OkHttpClient client;
    private Call currentCall;
    private volatile boolean isCancelled = false;

    public DownloadEngine() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    public File downloadAudio(Context context, String audioUrl, String urlHash, DownloadProgressListener listener) throws IOException {
        isCancelled = false;

        // 1. Prepare temp directory and file
        File tempDir = new File(context.getCacheDir(), "import_temp");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File tempFile = new File(tempDir, urlHash + ".part");
        if (tempFile.exists()) {
            tempFile.delete();
        }

        Request request = new Request.Builder()
                .url(audioUrl)
                .header("User-Agent", "Mozilla/5.0 (Android; ResonanceMusic/1.0)")
                .get()
                .build();

        currentCall = client.newCall(request);

        try (Response response = currentCall.execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Máy chủ phản hồi mã lỗi HTTP: " + response.code());
            }

            // 2. Validate Content-Type
            String contentType = response.header("Content-Type");
            if (contentType != null) {
                String lowerType = contentType.toLowerCase(Locale.ROOT);
                if (lowerType.contains("text/html") || lowerType.contains("application/json")) {
                    throw new IOException("Nội dung tải về không phải tệp âm thanh (Content-Type: " + contentType + ")");
                }
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Phản hồi rỗng từ máy chủ.");
            }

            long contentLength = body.contentLength();
            if (contentLength > MAX_FILE_SIZE) {
                throw new IOException("Kích thước tệp vượt quá giới hạn cho phép (Tối đa 100 MB).");
            }

            // 3. Stream data to temp file with progress callback
            try (InputStream is = body.byteStream();
                 FileOutputStream fos = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[8192];
                long bytesDownloaded = 0;
                int read;

                while ((read = is.read(buffer)) != -1) {
                    if (isCancelled) {
                        tempFile.delete();
                        throw new IOException("Người dùng đã hủy tác vụ tải.");
                    }

                    fos.write(buffer, 0, read);
                    bytesDownloaded += read;

                    if (bytesDownloaded > MAX_FILE_SIZE) {
                        tempFile.delete();
                        throw new IOException("Tệp âm thanh vượt quá giới hạn 100 MB.");
                    }

                    if (listener != null) {
                        int percent = (contentLength > 0) ? (int) ((bytesDownloaded * 100) / contentLength) : 0;
                        listener.onProgress(bytesDownloaded, contentLength, percent);
                    }
                }

                fos.flush();
            }

            return tempFile;
        } catch (IOException e) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            throw e;
        }
    }

    public void cancel() {
        isCancelled = true;
        if (currentCall != null) {
            currentCall.cancel();
        }
    }
}
