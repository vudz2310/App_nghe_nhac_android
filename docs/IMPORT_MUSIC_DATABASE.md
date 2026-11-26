# THIẾT KẾ CƠ SỞ DỮ LIỆU & MIGRATION — IMPORT MUSIC

> **Dự án:** Resonance Music (Independent Android Music Player)  
> **Cơ sở dữ liệu:** SQLite Native (`MusicDatabaseHelper`)  
> **Phiên bản nâng cấp:** `DATABASE_VERSION = 3` (từ phiên bản v2 lên v3)

---

## 1. MỞ RỘNG BẢNG `songs` (EXTENDED SCHEMA)

Để hỗ trợ cả bài hát đóng gói sẵn (`source_type = 'ASSET'`) và bài hát nhập từ URL (`source_type = 'IMPORTED'`), bảng `songs` được bổ sung các cột mới:

| Tên cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Mã định danh duy nhất của bài hát |
| `title` | `TEXT` | `NOT NULL` | Tựa đề bài hát |
| `artist_id` | `INTEGER` | `NOT NULL, FK -> artists(id)` | Khóa ngoại trỏ đến nghệ sĩ |
| `album_id` | `INTEGER` | `NULL, FK -> albums(id)` | Khóa ngoại trỏ đến album (có thể NULL) |
| `duration_ms` | `INTEGER` | `NOT NULL DEFAULT 0` | Thời lượng bài hát (mili-giây) |
| `audio_resource`| `TEXT` | `NULL` | Tên resource trong `res/raw` (NULL nếu là Imported) |
| `image_resource`| `TEXT` | `NULL` | Tên resource drawable hoặc đường dẫn ảnh bìa local |
| `source_type` | `TEXT` | `NOT NULL DEFAULT 'ASSET'` | Nguồn gốc: `'ASSET'` hoặc `'IMPORTED'` |
| `mime_type` | `TEXT` | `NULL` | Loại MIME (ví dụ: `'audio/mpeg'`, `'audio/mp4'`) |
| `file_size` | `INTEGER` | `NOT NULL DEFAULT 0` | Dung lượng tệp tính bằng byte |
| `file_name` | `TEXT` | `NULL` | Tên tệp lưu trữ vật lý (ví dụ: `170000_night_drive.mp3`) |
| `app_file_path` | `TEXT` | `NULL` | Đường dẫn tuyệt đối trong bộ nhớ riêng của app |
| `public_content_uri`| `TEXT` | `NULL` | URI content trong MediaStore chung |
| `storage_mode` | `TEXT` | `NOT NULL DEFAULT 'APP'` | Chế độ lưu: `'APP'`, `'PUBLIC'`, `'BOTH'` |
| `source_host` | `TEXT` | `NULL` | Tên miền nguồn tải đã chuẩn hóa (ví dụ: `cdn.example.com`) |
| `checksum_sha256`| `TEXT` | `NULL` | Mã băm SHA-256 xác minh toàn vẹn và chống trùng lặp |
| `created_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now') * 1000)` | Thời điểm nhập bài hát |

---

## 2. BẢNG MỚI: `import_jobs` (QUẢN LÝ TIẾN TRÌNH NHẬP NHẠC)

Bảng này lưu trữ trạng thái các phiên tải nhằm phục vụ màn hình lịch sử tải, đồng thời đảm bảo không tạo bản ghi rác trong bảng `songs` nếu tác vụ bị hủy hoặc gặp lỗi:

| Tên cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Mã định danh tác vụ |
| `source_url_hash` | `TEXT` | `NOT NULL` | Mã băm SHA-256 của URL (bảo vệ chữ ký token) |
| `file_name` | `TEXT` | `NULL` | Tên tệp dự kiến |
| `status` | `TEXT` | `NOT NULL` | Trạng thái: `DOWNLOADING`, `COMPLETED`, `FAILED`, `CANCELLED` |
| `progress` | `INTEGER` | `NOT NULL DEFAULT 0` | Tiến trình phần trăm (`0 - 100`) |
| `temp_path` | `TEXT` | `NULL` | Đường dẫn tệp tạm thời |
| `error_code` | `TEXT` | `NULL` | Mã lỗi phân loại (ví dụ: `ERR_HTTP_404`, `ERR_SSRF_BLOCKED`) |
| `error_message` | `TEXT` | `NULL` | Thông điệp lỗi an toàn hiển thị cho người dùng |
| `created_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now') * 1000)` | Thời điểm tạo tác vụ |
| `updated_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now') * 1000)` | Thời điểm cập nhật cuối |

---

## 3. CHỈ MỤC TRUY VẤN MỚI (INDEXES)

```sql
-- Chỉ mục phân loại bài hát theo nguồn gốc (ASSET vs IMPORTED)
CREATE INDEX IF NOT EXISTS idx_songs_source_type ON songs(source_type);

-- Chỉ mục kiểm tra trùng lặp checksum của bài hát nhập
CREATE INDEX IF NOT EXISTS idx_songs_checksum ON songs(checksum_sha256);

-- Chỉ mục tra cứu tác vụ tải gần nhất
CREATE INDEX IF NOT EXISTS idx_import_jobs_created ON import_jobs(created_at DESC);
```

---

## 4. CHIẾN LƯỢC MIGRATION AN TOÀN TỪ V2 LÊN V3

Nhằm bảo toàn 100% dữ liệu 6 bài hát mẫu, các playlist cá nhân và danh sách bài hát yêu thích hiện có:

```java
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < 2) {
        // Migration v1 -> v2 (Đã thực hiện ở các phase trước)
    }
    
    if (oldVersion < 3) {
        // Nâng cấp lên v3: Thêm các cột cho tính năng Import Music vào bảng songs
        db.execSQL("ALTER TABLE " + DatabaseContract.SongEntry.TABLE_NAME + 
                   " ADD COLUMN " + DatabaseContract.SongEntry.COLUMN_SOURCE_TYPE + " TEXT NOT NULL DEFAULT 'ASSET';");
        db.execSQL("ALTER TABLE " + DatabaseContract.SongEntry.TABLE_NAME + 
                   " ADD COLUMN " + DatabaseContract.SongEntry.COLUMN_MIME_TYPE + " TEXT;");
        db.execSQL("ALTER TABLE " + DatabaseContract.SongEntry.TABLE_NAME + 
                   " ADD COLUMN " + DatabaseContract.SongEntry.COLUMN_FILE_SIZE + " INTEGER NOT NULL DEFAULT 0;");
        db.execSQL("ALTER TABLE " + DatabaseContract.SongEntry.TABLE_NAME + 
                   " ADD COLUMN " + DatabaseContract.SongEntry.COLUMN_FILE_NAME + " TEXT;");
        db.execSQL("ALTER TABLE " + DatabaseContract.SongEntry.TABLE_NAME + 
                   " ADD COLUMN " + DatabaseContract.SongEntry.COLUMN_APP_FILE_PATH + " TEXT;");
        db.execSQL("ALTER TABLE " + DatabaseContract.SongEntry.TABLE_NAME + 
                   " ADD COLUMN " + DatabaseContract.SongEntry.COLUMN_PUBLIC_CONTENT_URI + " TEXT;");
        db.execSQL("ALTER TABLE " + DatabaseContract.SongEntry.TABLE_NAME + 
                   " ADD COLUMN " + DatabaseContract.SongEntry.COLUMN_STORAGE_MODE + " TEXT NOT NULL DEFAULT 'APP';");
        db.execSQL("ALTER TABLE " + DatabaseContract.SongEntry.TABLE_NAME + 
                   " ADD COLUMN " + DatabaseContract.SongEntry.COLUMN_SOURCE_HOST + " TEXT;");
        db.execSQL("ALTER TABLE " + DatabaseContract.SongEntry.TABLE_NAME + 
                   " ADD COLUMN " + DatabaseContract.SongEntry.COLUMN_CHECKSUM_SHA256 + " TEXT;");

        // Tạo bảng import_jobs
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseContract.ImportJobEntry.TABLE_NAME + " (" +
                DatabaseContract.ImportJobEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DatabaseContract.ImportJobEntry.COLUMN_SOURCE_URL_HASH + " TEXT NOT NULL, " +
                DatabaseContract.ImportJobEntry.COLUMN_FILE_NAME + " TEXT, " +
                DatabaseContract.ImportJobEntry.COLUMN_STATUS + " TEXT NOT NULL, " +
                DatabaseContract.ImportJobEntry.COLUMN_PROGRESS + " INTEGER NOT NULL DEFAULT 0, " +
                DatabaseContract.ImportJobEntry.COLUMN_TEMP_PATH + " TEXT, " +
                DatabaseContract.ImportJobEntry.COLUMN_ERROR_CODE + " TEXT, " +
                DatabaseContract.ImportJobEntry.COLUMN_ERROR_MESSAGE + " TEXT, " +
                DatabaseContract.ImportJobEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000), " +
                DatabaseContract.ImportJobEntry.COLUMN_UPDATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s', 'now') * 1000)" +
                ");");

        // Tạo chỉ mục
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_source_type ON " + DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_SOURCE_TYPE + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_songs_checksum ON " + DatabaseContract.SongEntry.TABLE_NAME + " (" + DatabaseContract.SongEntry.COLUMN_CHECKSUM_SHA256 + ");");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_import_jobs_created ON " + DatabaseContract.ImportJobEntry.TABLE_NAME + " (" + DatabaseContract.ImportJobEntry.COLUMN_CREATED_AT + " DESC);");
    }
}
```

---

## 5. NGUYÊN TẮC TOÀN VẸN GIAO DỊCH (TRANSACTIONAL INTEGRITY)

1. **Chống bản ghi mồ côi (Orphan Record Prevention):**
   Chỉ thực hiện câu lệnh `INSERT INTO songs` sau khi cả hai tệp tại App Storage và MediaStore (nếu chọn `BOTH`) đã hoàn tất quá trình ghi dữ liệu và vượt qua kiểm tra kích thước file (`file.length() > 0`).
2. **Rollback tự động:**
   Nếu câu lệnh `db.insert()` ném ngoại lệ:
   - Xóa ngay tệp vừa tạo trong `filesDir/music/`.
   - Gọi `contentResolver.delete(publicUri, null, null)` để thu hồi bản ghi MediaStore vừa tạo.
3. **Xóa bài hát an toàn:**
   Khi người dùng chọn xóa bài hát đã nhập:
   - Xóa tệp nội bộ trong `app_file_path`.
   - Nếu bản sao công cộng tồn tại, xóa thông qua ContentResolver.
   - Xóa bản ghi trong SQLite (tự động xóa quan hệ trong `playlist_songs`, `liked_songs`, `listening_history` nhờ `ON DELETE CASCADE`).
