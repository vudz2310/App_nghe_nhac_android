# PROJECT_CONTEXT.md — Ngữ Cảnh Dự Án

> Điền các thông tin đã được kiểm chứng vào tệp này để AI và các thành viên nắm vững cấu trúc dự án.

## 1. Thông Tin Nhận Diện Dự Án
- **Tên dự án:** Resonance Music (App_nghe_nhac_android)
- **Mục tiêu:** Ứng dụng nghe nhạc ngoại tuyến cá nhân hóa trên Android
- **Mô-đun:** `:app`

## 2. Môi Trường Xây Dựng (Build Environment)
- **Gradle Wrapper:** 8.13 (hoặc 8.4+)
- **Android Gradle Plugin (AGP):** 8.13.2
- **JDK:** Java 17 (`C:/Program Files/Java/jdk-17`)
- **compileSdk:** 34
- **targetSdk:** 34
- **minSdk:** 31
- **Ngôn ngữ:** Java (Source & Target Compatibility: 1.8 / 17)

## 3. Kiến Trúc & Công Nghệ Dữ Liệu
- **UI:** XML Layouts truyền thống + Material Components
- **Cơ sở dữ liệu:** SQLite (`SQLiteOpenHelper`)
- **Phát nhạc:** Android `MediaPlayer` kết hợp `Handler`/`Looper` cập nhật `SeekBar`
- **Giao tiếp Activity:** `ActivityResultLauncher`
