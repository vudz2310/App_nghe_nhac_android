package com.example.android_finalexam.domain.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class AudioFileValidator {

    public static class FileValidationResult {
        private final boolean isValidAudio;
        private final String detectedFormat;
        private final String checksumSha256;
        private final String errorMessage;

        public FileValidationResult(boolean isValidAudio, String detectedFormat, String checksumSha256, String errorMessage) {
            this.isValidAudio = isValidAudio;
            this.detectedFormat = detectedFormat;
            this.checksumSha256 = checksumSha256;
            this.errorMessage = errorMessage;
        }

        public boolean isValidAudio() { return isValidAudio; }
        public String getDetectedFormat() { return detectedFormat; }
        public String getChecksumSha256() { return checksumSha256; }
        public String getErrorMessage() { return errorMessage; }
    }

    public static FileValidationResult validateAudioFile(File file) {
        if (file == null || !file.exists() || file.length() < 32) {
            return new FileValidationResult(false, null, null, "Tệp không tồn tại hoặc dung lượng quá nhỏ.");
        }

        byte[] header = new byte[32];
        String checksum = null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream is = new FileInputStream(file)) {
                int read = is.read(header);
                if (read < 16) {
                    return new FileValidationResult(false, null, null, "Tệp âm thanh bị lỗi cấu trúc header.");
                }

                // Update digest for the whole file
                digest.update(header, 0, read);
                byte[] buffer = new byte[8192];
                int n;
                while ((n = is.read(buffer)) != -1) {
                    digest.update(buffer, 0, n);
                }
            }

            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            checksum = sb.toString();

        } catch (Exception e) {
            return new FileValidationResult(false, null, null, "Lỗi đọc tệp: " + e.getMessage());
        }

        // Check Magic Numbers
        // 1. MP3 with ID3v2 tag: "ID3" (0x49, 0x44, 0x33)
        if (header[0] == 0x49 && header[1] == 0x44 && header[2] == 0x33) {
            return new FileValidationResult(true, "audio/mpeg", checksum, null);
        }

        // 2. MP3 MPEG frame sync: 0xFF followed by 0xFB, 0xF3, or 0xF2
        if ((header[0] & 0xFF) == 0xFF && ((header[1] & 0xE0) == 0xE0)) {
            return new FileValidationResult(true, "audio/mpeg", checksum, null);
        }

        // 3. M4A/AAC ISO Base Media File Format: bytes 4-7 contain "ftyp" (0x66, 0x74, 0x79, 0x70)
        if (header[4] == 0x66 && header[5] == 0x74 && header[6] == 0x79 && header[7] == 0x70) {
            return new FileValidationResult(true, "audio/mp4", checksum, null);
        }

        // 4. OGG: "OggS" (0x4F, 0x67, 0x67, 0x53)
        if (header[0] == 0x4F && header[1] == 0x67 && header[2] == 0x67 && header[3] == 0x53) {
            return new FileValidationResult(true, "audio/ogg", checksum, null);
        }

        // 5. Check if it's HTML or text error response
        String headerStr = new String(header, 0, Math.min(header.length, 32)).toLowerCase();
        if (headerStr.contains("<!doctype") || headerStr.contains("<html") || headerStr.contains("<?xml") || headerStr.contains("{")) {
            return new FileValidationResult(false, null, null, "Nguồn tải trả về mã trang web (HTML/JSON) thay vì tệp âm thanh.");
        }

        // If not strictly recognized by magic numbers but has non-zero size, we can allow MediaMetadataRetriever to attempt decoding
        return new FileValidationResult(true, "audio/mpeg", checksum, null);
    }
}
