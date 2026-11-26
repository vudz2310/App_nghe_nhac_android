package com.example.android_finalexam.domain.engine;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import com.example.android_finalexam.domain.model.AudioMetadata;

import java.io.File;
import java.io.FileOutputStream;

public class AudioMetadataReader {

    public static AudioMetadata extractMetadata(Context context, File audioFile, String sourceHost, String checksumSha256) {
        AudioMetadata metadata = new AudioMetadata();
        metadata.setTempFilePath(audioFile.getAbsolutePath());
        metadata.setFileSize(audioFile.length());
        metadata.setSourceHost(sourceHost);
        metadata.setChecksumSha256(checksumSha256);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(audioFile.getAbsolutePath());

            // 1. Title
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if (title == null || title.trim().isEmpty()) {
                title = sanitizeFileNameToTitle(audioFile.getName());
            }
            metadata.setTitle(title.trim());

            // 2. Artist
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (artist == null || artist.trim().isEmpty()) {
                artist = "Nghệ sĩ chưa rõ";
            }
            metadata.setArtist(artist.trim());

            // 3. Album
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            if (album == null || album.trim().isEmpty()) {
                album = "Đĩa đơn tải về";
            }
            metadata.setAlbum(album.trim());

            // 4. Genre
            String genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            metadata.setGenre(genre != null ? genre.trim() : "Music");

            // 5. Duration
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int durationMs = 0;
            if (durationStr != null) {
                try {
                    durationMs = Integer.parseInt(durationStr);
                } catch (NumberFormatException ignored) {}
            }
            metadata.setDurationMs(durationMs);

            // 6. MIME type
            String mime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            metadata.setMimeType(mime != null ? mime : "audio/mpeg");

            // 7. Embedded Artwork
            byte[] rawPic = retriever.getEmbeddedPicture();
            if (rawPic != null && rawPic.length > 0) {
                File artworkFile = saveArtworkToCache(context, rawPic, checksumSha256);
                if (artworkFile != null) {
                    metadata.setArtworkLocalPath(artworkFile.getAbsolutePath());
                }
            }

            // 8. Suggested File Name
            String safeTitle = metadata.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_").trim();
            metadata.setSuggestedFileName(safeTitle + ".mp3");

        } catch (Exception ignored) {
            // Fallback if retriever fails
            metadata.setTitle(sanitizeFileNameToTitle(audioFile.getName()));
            metadata.setArtist("Nghệ sĩ chưa rõ");
            metadata.setAlbum("Đĩa đơn tải về");
            metadata.setMimeType("audio/mpeg");
            metadata.setSuggestedFileName(metadata.getTitle() + ".mp3");
        } finally {
            try {
                retriever.release();
            } catch (Exception ignored) {}
        }

        return metadata;
    }

    private static String sanitizeFileNameToTitle(String fileName) {
        if (fileName == null) return "Bài hát mới";
        String name = fileName;
        if (name.endsWith(".part")) {
            name = name.substring(0, name.length() - 5);
        }
        if (name.endsWith(".mp3") || name.endsWith(".m4a") || name.endsWith(".aac")) {
            name = name.substring(0, name.lastIndexOf('.'));
        }
        return name.replace('_', ' ').replace('-', ' ').trim();
    }

    private static File saveArtworkToCache(Context context, byte[] artworkBytes, String checksum) {
        try {
            File artworkDir = new File(context.getCacheDir(), "artwork");
            if (!artworkDir.exists()) {
                artworkDir.mkdirs();
            }
            File artworkFile = new File(artworkDir, "art_" + checksum + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(artworkFile)) {
                fos.write(artworkBytes);
                fos.flush();
            }
            return artworkFile;
        } catch (Exception e) {
            return null;
        }
    }
}
