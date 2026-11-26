# KIẾN TRÚC KỸ THUẬT — TÍNH NĂNG IMPORT MUSIC

> **Dự án:** Resonance Music (Independent Android Music Player)  
> **Mô hình kiến trúc:** Clean Architecture + MVVM + Repository Pattern  
> **Ngôn ngữ:** Thuần Java 8+  
> **Nguyên tắc:** Tách biệt rõ ràng giữa Presentation, Download Engine, File Storage và Database.

---

## 1. SƠ ĐỒ KIẾN TRÚC TỔNG THỂ (COMPONENT ARCHITECTURE)

```text
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                              │
│                                                                        │
│   ImportMusicFragment (Nhập URL, Thanh tiến trình, Metadata Review)    │
│            │                                    ▲                      │
│            ▼ (User Input / Actions)             │ (LiveData State)     │
│   ImportMusicViewModel (Quản lý trạng thái UI, Form Validation)        │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                         DOMAIN & BUSINESS LAYER                        │
│                                                                        │
│   ImportMusicRepository (Điều phối toàn bộ quy trình Import)          │
│   ├── UrlValidator (Kiểm tra HTTPS, chặn SSRF, kiểm tra redirect)      │
│   ├── AudioFileValidator (Kiểm tra Magic Bytes & định dạng nhị phân)   │
│   ├── AudioMetadataReader (MediaMetadataRetriever trích xuất ID3)      │
│   ├── MusicStorageManager (Lưu App Storage & Public MediaStore)        │
│   └── SongUriResolver (Định tuyến URI phát nhạc cho Media3 ExoPlayer)  │
└───────────────────┬───────────────────────────────┬────────────────────┘
                    │                               │
                    ▼                               ▼
┌──────────────────────────────────────┐ ┌───────────────────────────────┐
│           NETWORK LAYER              │ │        STORAGE LAYER          │
│                                      │ │                               │
│  OkHttp Streaming Client             │ │  1. Temporary Cache (.part)   │
│  - Hỗ trợ timeout, hủy luồng tải     │ │  2. App Storage (files/music) │
│  - Bộ đệm 8KB, theo dõi tiến trình   │ │  3. MediaStore.Audio.Media    │
└──────────────────────────────────────┘ └───────────────────────────────┘
                    │                               │
                    └───────────────┬───────────────┘
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                         DATA LAYER (SQLITE)                            │
│                                                                        │
│   MusicDatabaseHelper v3 (Database SQLite quan hệ)                     │
│   ├── SongDao (Mở rộng hỗ trợ source_type = 'IMPORTED')                │
│   └── ImportJobDao (Theo dõi lịch sử và trạng thái tác vụ tải)         │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. PHÂN TÁCH TRÁCH NHIỆM TỪNG THÀNH PHẦN (COMPONENT RESPONSIBILITIES)

### 2.1. Tầng Presentation
- **`ImportMusicFragment`**:
  - Giao diện 3 giai đoạn: Nhập URL -> Thanh tiến trình tải thời gian thực -> Màn hình Metadata Review để người dùng duyệt và sửa thông tin trước khi lưu.
  - Tuyệt đối không chứa logic mạng hay thao tác tệp tin.
- **`ImportMusicViewModel`**:
  - Nắm giữ `MutableLiveData<ImportState>` và `MutableLiveData<AudioMetadata>`.
  - Nhận lệnh từ View, kích hoạt Repository và cập nhật trạng thái UI phản ứng (Reactive UI).

### 2.2. Tầng Nghiệp vụ & Điều phối (Domain & Engine)
- **`ImportMusicRepository`**:
  - Đóng vai trò Facade điều phối 4 bước: (1) Validate URL -> (2) Tải tệp tạm -> (3) Đọc Metadata -> (4) Lưu trữ kép & ghi Database.
  - Sử dụng `ExecutorService` chạy hoàn toàn trên Background Worker Thread.
- **`UrlValidator`**:
  - Xác thực chuẩn URL theo RFC 3986.
  - Chặn triệt để các dải địa chỉ IP nhạy cảm: `localhost`, `127.0.0.1`, `::1`, `10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`, `169.254.0.0/16`.
  - Xác thực host sau mỗi lần redirect (tối đa 3 lần).
- **`AudioFileValidator`**:
  - Đọc 16 bytes đầu tiên của tệp để kiểm tra Header Signature (Magic Numbers):
    - MP3: `ID3` (`0x49 0x44 0x33`) hoặc Sync Frame `0xFF 0xFB` / `0xFF 0xF3`.
    - M4A / AAC: Chứa subtype `ftyp` (`0x66 0x74 0x79 0x70 0x4D 0x34 0x41 0x20`).
- **`AudioMetadataReader`**:
  - Bao bọc `android.media.MediaMetadataRetriever`.
  - Trích xuất: Tiêu đề, Ca sĩ, Album, Thời lượng và Ảnh bìa nhúng (Artwork).
  - Tự động fallback về tên tệp nếu siêu dữ liệu bị thiếu.
- **`MusicStorageManager`**:
  - Triển khai interface lưu trữ kép:
    - `saveToAppStorage(File tempFile, String fileName)`: Sao chép tệp vào `context.getFilesDir()/music/`.
    - `saveToPublicMusic(File tempFile, String fileName, String mimeType)`: Chèn bản ghi vào `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI` với `IS_PENDING = 1`, ghi dữ liệu qua `ContentResolver`, sau đó đặt `IS_PENDING = 0`.
- **`SongUriResolver`**:
  - Cung cấp phương thức `Uri resolveAudioUri(Context context, SongItem song)` dùng chung cho toàn bộ trình phát nhạc.
  - Quy tắc định tuyến:
    1. Nếu `song.getAppFilePath() != null` và tệp tồn tại: Trả về `Uri.fromFile(...)`.
    2. Nếu `song.getPublicContentUri() != null`: Kiểm tra quyền truy cập `ContentResolver`, nếu hợp lệ trả về Content Uri.
    3. Ngược lại: Trả về URI tài nguyên nội bộ `android.resource://...`.

---

## 3. THIẾT KẾ DATA CONTRACT NỘI BỘ (INTERNAL DATA MODELS)

### 3.1. `AudioMetadata` (POJO Siêu dữ liệu bài hát)
```java
public class AudioMetadata {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int durationMs;
    private long fileSize;
    private String mimeType;
    private String sourceHost;
    private byte[] embeddedArtwork; // Nullable
    private String tempFilePath;
    // Getters and Setters
}
```

### 3.2. `ImportState` (Trạng thái tác vụ nhập nhạc)
```java
public class ImportState {
    public enum Status {
        IDLE, VALIDATING, FETCHING, DOWNLOADING, 
        EXTRACTING_METADATA, AWAITING_CONFIRMATION, 
        SAVING, COMPLETED, FAILED, CANCELLED
    }
    
    private Status status = Status.IDLE;
    private int progressPercent = 0;
    private long bytesDownloaded = 0;
    private long totalBytes = 0;
    private String message = "";
    private AudioMetadata metadata = null;
    private SongItem createdSong = null;
    // Getters and Setters
}
```

---

## 4. BẢO MẬT TẢI DỮ LIỆU & QUẢN LÝ BỘ NHỚ RAM

1. **Bộ đệm luồng (Stream-to-File Buffer):**
   - Đọc từng khối `byte[] buffer = new byte[8192]` và ghi thẳng ra `FileOutputStream`.
   - Không chuyển toàn bộ dữ liệu tệp vào `ByteArrayOutputStream` hoặc mảng byte trong bộ nhớ heap.
2. **Ngắt kết nối mạng an toàn (Cancellable Call):**
   - Lưu giữ tham chiếu `okhttp3.Call currentCall`.
   - Khi người dùng bấm nút Hủy: Gọi `currentCall.cancel()`, đóng các luồng I/O trong khối `finally` và xóa ngay tệp tạm thời.
3. **Bảo vệ quyền riêng tư người dùng:**
   - URL nguồn có chứa chữ ký số (Signed Query Token) sẽ được băm SHA-256 trước khi lưu vào bảng `import_jobs`.
   - Không in URL nhạy cảm ra Logcat.
