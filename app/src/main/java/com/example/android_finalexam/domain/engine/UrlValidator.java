package com.example.android_finalexam.domain.engine;

import java.net.InetAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

public class UrlValidator {

    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;
        private final String normalizedHost;

        public ValidationResult(boolean isValid, String errorMessage, String normalizedHost) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
            this.normalizedHost = normalizedHost;
        }

        public boolean isValid() { return isValid; }
        public String getErrorMessage() { return errorMessage; }
        public String getNormalizedHost() { return normalizedHost; }
    }

    public static ValidationResult validate(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập đường dẫn URL.", null);
        }

        String trimmed = urlString.trim();

        // 1. Enforce HTTPS scheme
        if (!trimmed.toLowerCase(Locale.ROOT).startsWith("https://")) {
            if (trimmed.toLowerCase(Locale.ROOT).startsWith("http://")) {
                return new ValidationResult(false, "Chỉ chấp nhận giao thức bảo mật HTTPS.", null);
            }
            return new ValidationResult(false, "Đường dẫn không hợp lệ. Phải bắt đầu bằng https://", null);
        }

        try {
            URI uri = new URI(trimmed);
            String host = uri.getHost();

            if (host == null || host.trim().isEmpty()) {
                return new ValidationResult(false, "Tên miền (Host) không hợp lệ.", null);
            }

            String lowerHost = host.toLowerCase(Locale.ROOT);

            // 2. Reject YouTube URLs (Terms of Service Compliance)
            if (lowerHost.contains("youtube.com") || lowerHost.contains("youtu.be")) {
                return new ValidationResult(false,
                        "Liên kết không phải là tệp âm thanh tải trực tiếp. Ứng dụng không hỗ trợ bóc tách video YouTube.",
                        lowerHost);
            }

            // 3. SSRF Protection: Block localhost and loopback
            if (lowerHost.equals("localhost") || lowerHost.equals("127.0.0.1") || lowerHost.equals("::1")) {
                return new ValidationResult(false, "Địa chỉ nội bộ không được phép truy cập.", lowerHost);
            }

            // 4. Block Private IP ranges (RFC 1918 & Link-Local)
            if (isPrivateOrLocalIp(lowerHost)) {
                return new ValidationResult(false, "Địa chỉ mạng riêng tư không được phép truy cập.", lowerHost);
            }

            return new ValidationResult(true, null, lowerHost);
        } catch (Exception e) {
            return new ValidationResult(false, "Cú pháp URL không hợp lệ: " + e.getMessage(), null);
        }
    }

    private static boolean isPrivateOrLocalIp(String host) {
        // Simple regex check for IPv4
        if (host.matches("^(\\d{1,3}\\.){3}\\d{1,3}$")) {
            String[] parts = host.split("\\.");
            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);

            // 10.0.0.0/8
            if (first == 10) return true;
            // 127.0.0.0/8
            if (first == 127) return true;
            // 169.254.0.0/16
            if (first == 169 && second == 254) return true;
            // 172.16.0.0/12
            if (first == 172 && (second >= 16 && second <= 31)) return true;
            // 192.168.0.0/16
            if (first == 192 && second == 168) return true;
        }
        return false;
    }

    public static String computeSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }
}
