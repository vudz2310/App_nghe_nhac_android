# ĐẶC TẢ YÊU CẦU NGHIỆP VỤ — TÍNH NĂNG IMPORT MUSIC

> **Dự án:** Resonance Music (Independent Android Music Player)  
> **Phân hệ:** Import Music (Nhập nhạc từ URL trực tiếp có bản quyền)  
> **Ngôn ngữ:** Java 8+  
> **Môi trường:** Android 12 (API 31) – Android 14+ (API 34)  
> **Chế độ lưu trữ:** `BOTH` (App-specific Private Storage + Public Shared MediaStore)

---

## 1. TỔNG QUAN VÀ NGUYÊN TẮC NGHIỆP VỤ CỐT LÕI

### 1.1. Mục tiêu
Cho phép người dùng bổ sung các bài hát mới vào kho nhạc cá nhân ngoại tuyến từ các đường dẫn tải trực tiếp (Direct Audio URL) được cấp phép phân phối, mà không cần đưa thêm tệp MP3 vào gói cài đặt APK.

### 1.2. Phân định ranh giới & Quyết định bản quyền
1. **Tuyệt đối KHÔNG xây dựng YouTube Downloader / Scraper:**
   - Ứng dụng từ chối tải từ các URL thuộc tên miền `youtube.com`, `youtu.be`, hoặc các dịch vụ bóc tách luồng streaming trái phép.
   - YouTube Data API v3 chỉ cung cấp metadata và không hỗ trợ tải file âm thanh. Việc bóc tách stream vi phạm trực tiếp *YouTube Developer Policies*.
2. **Nguồn tải hợp lệ:**
   - URL giao thức HTTPS trỏ trực tiếp đến tệp âm thanh nhị phân (Binary Audio File: MP3, M4A, AAC) có quyền tải về hợp pháp.
   - Máy chủ nguồn phản hồi `HTTP 200 OK` với header `Content-Type: audio/...`.
3. **Cơ chế lưu trữ kép (`BOTH`):**
   - **App Storage (Bộ nhớ riêng):** Lưu tại `context.getFilesDir()/music/`. Bản lưu này do ứng dụng toàn quyền quản lý, không bị các ứng dụng dọn dẹp rác xóa nhầm, đảm bảo luôn sẵn sàng phát nhạc nội bộ.
   - **Public Music (Thư mục dùng chung):** Lưu thông qua `MediaStore.Audio.Media` vào thư mục `Music/MyMusic/` của điện thoại để người dùng có thể tìm thấy, sao chép hoặc nghe qua các trình phát khác.
   - **Quy tắc an toàn:** Chỉ tải tệp một lần vào thư mục đệm tạm (`cache/import/.part`), sau khi xác minh toàn vẹn mới sao chép vào hai vị trí lưu trữ và tạo bản ghi SQLite.

---

## 2. LUỒNG NGHIỆP VỤ CHI TIẾT (IMPORT WORKFLOW)

```text
[1. Nhập URL] ────────> [2. Validate URL & Security]
                               │ (Hợp lệ)
                               ▼
[4. Tải Tạm & Tiến Trình] <── [3. Kiểm Tra HTTP & Header]
          │ (Hoàn tất)
          ▼
[5. Đọc Metadata & Artwork]
          │
          ▼
[6. Metadata Review (Chỉnh sửa tên, ca sĩ, album)]
          │ (Người dùng bấm Xác nhận)
          ▼
[7. Lưu File: App Storage + Public MediaStore]
          │ (Xác nhận 2 bản sao thành công)
          ▼
[8. Ghi Database SQLite & Xóa Cache Tạm]
          │
          ▼
[9. Cập Nhật Thư Viện & Cho Phép Phát Ngay]
```

### 2.1. Bước 1: Nhập URL
- Người dùng dán đường dẫn âm thanh vào ô nhập liệu (`TextInputEditText`).
- Hỗ trợ nút `Dán nhanh (Paste)` từ Clipboard hệ thống.
- Hướng dẫn định dạng hợp lệ: Phải là liên kết HTTPS trực tiếp đến tệp âm thanh.

### 2.2. Bước 2: Kiểm tra an toàn mạng (URL Security & SSRF Protection)
- **Chỉ chấp nhận Scheme `https://`:** Từ chối `http://`, `file://`, `content://`, `ftp://`, `javascript:`.
- **Chặn tấn công SSRF (Server-Side Request Forgery) & IP nội bộ:**
  - Từ chối `localhost`, `127.0.0.1`, `::1`.
  - Từ chối các dải IP riêng tư RFC 1918: `10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`.
  - Từ chối dải Link-Local: `169.254.0.0/16`.
- **Kiểm soát Redirect:** Tối đa 3 lần chuyển hướng. Sau mỗi lần redirect, kiểm tra lại toàn bộ điều kiện an toàn của Host mới.
- **Không lưu Token bảo mật:** Nếu URL chứa query parameter nhạy cảm hoặc chữ ký số (Signed URL), hệ thống chỉ lưu `source_host` đã chuẩn hóa vào cơ sở dữ liệu để bảo vệ quyền riêng tư.

### 2.3. Bước 3: Kiểm tra HTTP Response & File Signature
- Thực hiện yêu cầu HTTP HEAD hoặc GET ban đầu:
  - Kiểm tra `response.isSuccessful()` (HTTP 200).
  - Kiểm tra `Content-Type`: Phải bắt đầu bằng `audio/` (ví dụ `audio/mpeg`, `audio/mp4`, `audio/x-m4a`, `audio/aac`).
  - Kiểm tra `Content-Length`: Giới hạn mặc định tối đa **100 MB** cho mỗi tệp. Cảnh báo và dừng nếu kích thước vượt quá giới hạn hoặc bộ nhớ máy còn lại dưới 200 MB.
- Kiểm tra chữ ký file nhị phân (Magic Numbers) trên 16 bytes đầu tiên của file:
  - MP3: Bắt đầu bằng header `ID3` (`49 44 33`) hoặc Sync Frame `FF FB` / `FF F3`.
  - M4A / AAC: Chứa subtype `ftyp` (`66 74 79 70 M4A `).
  - Nếu file trả về HTML (mã trang web) hoặc văn bản: Hủy ngay lập tức và thông báo lỗi "Nguồn không phải là tệp âm thanh trực tiếp".

### 2.4. Bước 4: Tải tạm & Theo dõi tiến trình
- Tải luồng dữ liệu vào tệp tạm thời: `context.getCacheDir()/import_temp/<hash>.part`.
- Đọc theo bộ đệm (Buffer 8KB), không nạp toàn bộ file vào RAM để tránh `OutOfMemoryError`.
- Bắn sự kiện cập nhật tiến trình: Tỉ lệ phần trăm (`0% - 100%`), dung lượng đã tải (`MB / Total MB`), tốc độ tải (`KB/s`).
- Hỗ trợ nút `Hủy (Cancel)` tức thì: Đóng kết nối mạng và xóa sạch tệp tạm `.part`.

### 2.5. Bước 5: Bóc tách Metadata (Metadata Extraction)
- Sử dụng `android.media.MediaMetadataRetriever` đọc tệp tạm:
  - **Tên bài hát (Title):** `METADATA_KEY_TITLE` (nếu trống, dùng tên file sau khi bỏ phần mở rộng `.mp3`).
  - **Nghệ sĩ (Artist):** `METADATA_KEY_ARTIST` (nếu trống, hiển thị "Nghệ sĩ chưa rõ", tuyệt đối không lấy tên domain/host làm ca sĩ).
  - **Album:** `METADATA_KEY_ALBUM` (nếu trống, hiển thị "Đĩa đơn tải về").
  - **Thể loại (Genre):** `METADATA_KEY_GENRE`.
  - **Thời lượng (Duration):** `METADATA_KEY_DURATION` (mili-giây).
  - **Ảnh bìa nhúng (Embedded Artwork):** `getEmbeddedPicture()`. Nếu có, giải mã bitmap và lưu vào bộ nhớ cache ảnh riêng của app.

### 2.6. Bước 6: Màn hình xác nhận & Chỉnh sửa (Metadata Review)
- Hiển thị giao diện xem trước đầy đủ:
  - Ảnh bìa (hoặc ảnh đĩa nhạc mặc định).
  - Các ô nhập cho phép sửa: Tiêu đề, Tên nghệ sĩ, Tên album.
  - Các thông số kỹ thuật cố định: Thời lượng, Định dạng âm thanh, Dung lượng tệp, Vị trí lưu (`App Storage + Public Music`).
- Người dùng nhấn nút `[ Lưu vào thư viện ]` để xác nhận.

### 2.7. Bước 7: Thực thi lưu trữ kép (Dual Storage Pipeline)
1. **Lưu App Storage:**
   - Tạo thư mục `filesDir/music/` (nếu chưa có).
   - Sao chép tệp tạm sang `filesDir/music/<timestamp>_<filename>.mp3`.
   - Kiểm tra `appFile.exists() && appFile.length() > 0`.
2. **Lưu Public Music (`MediaStore`):**
   - Tạo `ContentValues`:
     - `DISPLAY_NAME = <title>.mp3`
     - `MIME_TYPE = "audio/mpeg"`
     - `RELATIVE_PATH = Environment.DIRECTORY_MUSIC + "/MyMusic"`
     - `IS_PENDING = 1` (Đánh dấu đang ghi trên Android 10+ / API 29+)
   - Mở `OutputStream` qua `contentResolver.openOutputStream(publicUri)`.
   - Sao chép dữ liệu và cập nhật `IS_PENDING = 0` khi hoàn tất.
3. **Rollback an toàn:**
   - Nếu lưu App Storage thất bại: Hủy bỏ, báo lỗi.
   - Nếu lưu Public MediaStore thất bại nhưng App Storage thành công: Giữ bản App Storage, ghi nhận `storage_mode = 'APP'`, thông báo cho người dùng và hỗ trợ xuất ra Public sau.
   - Xóa tệp tạm trong thư mục cache.

### 2.8. Bước 8: Ghi nhận cơ sở dữ liệu SQLite
- Tìm hoặc thêm mới bản ghi `artists` theo tên nghệ sĩ.
- Tìm hoặc thêm mới bản ghi `albums` theo tên album.
- Tạo bản ghi mới trong bảng `songs` với đầy đủ các trường: `source_type = 'IMPORTED'`, `app_file_path`, `public_content_uri`, `storage_mode = 'BOTH'`, `file_size`, `checksum_sha256`.
- Cập nhật trạng thái tác vụ trong bảng `import_jobs` thành `COMPLETED`.

### 2.9. Bước 9: Hoàn tất & Tích hợp phát nhạc
- Đưa bài hát mới vào danh sách hiển thị của Thư viện (`LibraryFragment`) và Trang chủ (`HomeFragment`).
- Cung cấp nút `[ Phát ngay ]` trên thông báo thành công.

---

## 3. MÁY TRẠNG THÁI IMPORT (IMPORT STATE MACHINE)

```text
       ┌───────────┐
       │   IDLE    │
       └─────┬─────┘
             │ (Nhập URL & bấm Phân tích)
             ▼
       ┌───────────┐       (Lỗi URL/SSRF)      ┌───────────┐
       │VALIDATING ├──────────────────────────>│  FAILED   │
       └─────┬─────┘                           └───────────┘
             │ (URL hợp lệ)                          ▲
             ▼                                       │
       ┌───────────┐       (Lỗi HTTP/Size/Header)    │
       │ FETCHING  ├─────────────────────────────────┤
       └─────┬─────┘                                 │
             │ (Header hợp lệ)                       │
             ▼                                       │
       ┌───────────┐       (Lỗi mạng/Timeout)        │
       │DOWNLOADING├─────────────────────────────────┤
       └─────┬─────┘                                 │
             │ (Tải xong file tạm)                   │
             ▼                                       │
       ┌───────────────────┐                         │
       │EXTRACTING_METADATA│                         │
       └─────┬─────────────┘                         │
             │                                       │
             ▼                                       │
       ┌─────────────────────┐  (Người dùng bấm Hủy) │
       │AWAITING_CONFIRMATION├──────────────────────>│ CANCELLED │
       └─────┬───────────────┘                       └───────────┘
             │ (Người dùng bấm Xác nhận Lưu)
             ▼
       ┌───────────┐       (Lỗi ghi File/Database)
       │  SAVING   ├─────────────────────────────────┘
       └─────┬─────┘
             │ (Lưu thành công)
             ▼
       ┌───────────┐
       │ COMPLETED │
       └───────────┘
```

---

## 4. XỬ LÝ LỖI & PHỤC HỒI (ERROR RECOVERY MATRIX)

| Tình huống ngoại lệ | Nguyên nhân | Cách xử lý an toàn của hệ thống |
| :--- | :--- | :--- |
| **URL YouTube / Web HTML** | Người dùng dán link video YouTube hoặc link xem web | Báo lỗi: *"Liên kết không phải là tệp âm thanh tải trực tiếp. Ứng dụng không hỗ trợ bóc tách video YouTube."* |
| **Địa chỉ nội bộ / IP riêng** | URL trỏ tới `127.0.0.1` hoặc IP LAN | Chặn ngay tại bước `UrlValidator`, thông báo *"Địa chỉ URL không hợp lệ hoặc không an toàn."* |
| **HTTP 403 / 404** | Liên kết hết hạn, link yêu cầu đăng nhập | Thông báo *"Không thể truy cập tệp âm thanh (Mã lỗi HTTP %d). Vui lòng kiểm tra quyền truy cập của liên kết."* |
| **Tệp quá 100 MB** | File audio quá dài hoặc tệp video nặng | Từ chối tải, hiển thị: *"Dung lượng tệp vượt quá giới hạn cho phép (Tối đa 100 MB)."* |
| **Mất kết nối mạng giữa chừng** | Wi-Fi ngắt, rớt sóng 4G | Dừng tải, xóa file tạm `.part`, cập nhật trạng thái `FAILED`, hiển thị nút `[ Thử lại ]`. |
| **Người dùng hủy tải** | Bấm nút Hủy khi đang tải | Ngắt HTTP Request Call, xóa tệp tạm trong cache, chuyển trạng thái `CANCELLED`. |
| **Lưu App Storage được, Public thất bại** | Bộ nhớ ngoài bị đầy hoặc giới hạn quyền MediaStore | Giữ bản App Storage, lưu `storage_mode = 'APP'`, hiển thị cảnh báo: *"Đã lưu vào bộ nhớ ứng dụng. Bản sao công cộng chưa thể lưu."* |
| **Người dùng xóa file ngoài thư mục Music** | Dùng app quản lý file xóa file trong `Music/MyMusic` | `SongUriResolver` tự động phát hiện mất file Public và fallback chuyển sang phát bản nội bộ trong App Storage. |
| **Tệp âm thanh tải về bị hỏng (Corrupted)** | File bị cắt ngắn, lỗi encoding | `MediaMetadataRetriever` báo lỗi -> Dừng lưu, xóa file tạm, không tạo bản ghi rác trong cơ sở dữ liệu. |
