# DATABASE DESIGN — THIẾT KẾ CƠ SỞ DỮ LIỆU SQLITE

> **Dự án:** Resonance Music (Independent Android Music Player)  
> **Cơ chế lưu trữ:** `SQLiteOpenHelper` (Android Native SQLite)  
> **Tên File Database:** `songs_database`  
> **Phiên bản Schema hiện tại:** `2` (Nâng cấp từ bản v1 phẳng lên bản v2 quan hệ)

---

## 1. SƠ ĐỒ QUAN HỆ THỰC THỂ (ENTITY RELATIONSHIP MODEL)

```text
┌────────────────┐          ┌────────────────┐
│    artists     │          │     albums     │
├────────────────┤          ├────────────────┤
│ id (PK)        │1       n │ id (PK)        │
│ name           │─────────<│ title          │
│ avatar_res     │          │ artist_id (FK) │
│ bio            │          │ cover_res      │
│ created_at     │          │ release_date   │
└───────┬────────┘          └───────┬────────┘
        │1                          │1
        │                           │
        │n                          │n
   ┌────┴───────────────────────────┴────┐
   │                songs                │
   ├─────────────────────────────────────┤
   │ id (PK)                             │
   │ title                               │
   │ artist_id (FK -> artists.id)        │
   │ album_id (FK -> albums.id, NULL)    │
   │ duration_ms                         │
   │ audio_res                           │
   │ cover_res                           │
   │ created_at                          │
   └──────┬───────────────────────┬──────┘
          │1                      │1
          │                       │
          │n                      │n
┌─────────┴──────────┐   ┌────────┴──────────┐   ┌─────────────────────┐
│    liked_songs     │   │  playlist_songs   │   │  listening_history  │
├────────────────────┤   ├───────────────────┤   ├─────────────────────┤
│ song_id (PK, FK)   │   │ playlist_id (PK)  │   │ id (PK)             │
│ liked_at           │   │ song_id (PK, FK)  │   │ song_id (FK)        │
└────────────────────┘   │ position          │   │ played_at           │
                         │ added_at          │   │ progress_ms         │
                         └─────────┬─────────┘   └─────────────────────┘
                                   │n
                                   │
                                   │1
                         ┌─────────┴─────────┐
                         │     playlists     │
                         ├───────────────────┤
                         │ id (PK)           │
                         │ name              │
                         │ description       │
                         │ cover_res         │
                         │ created_at        │
                         │ updated_at        │
                         └───────────────────┘
```

---

## 2. ĐẶC TẢ CHI TIẾT 7 BẢNG QUAN HỆ

### 2.1. Bảng `artists` (Nghệ sĩ)
Lưu trữ thông tin các ca sĩ, ban nhạc hoặc nhà sản xuất âm nhạc.

| Tên cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Mã định danh duy nhất của nghệ sĩ |
| `name` | `TEXT` | `NOT NULL` | Tên nghệ sĩ (ví dụ: "Coldplay", "Kis") |
| `avatar_res` | `TEXT` | `NULL` | Tên resource ảnh đại diện trong `res/drawable` |
| `bio` | `TEXT` | `NULL` | Tiểu sử hoặc thông tin tóm tắt |
| `created_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now'))` | Thời điểm tạo bản ghi (Unix timestamp ms) |

### 2.2. Bảng `albums` (Album nhạc)
Lưu trữ danh sách album hoặc đĩa đơn phát hành.

| Tên cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Mã định danh duy nhất của album |
| `title` | `TEXT` | `NOT NULL` | Tựa đề album |
| `artist_id` | `INTEGER` | `NOT NULL, REFERENCES artists(id) ON DELETE CASCADE` | Khóa ngoại trỏ đến nghệ sĩ chủ sở hữu |
| `cover_res` | `TEXT` | `NULL` | Tên resource ảnh bìa album trong `res/drawable` |
| `release_date` | `TEXT` | `NULL` | Năm hoặc ngày phát hành (ví dụ: "2008") |
| `created_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now'))` | Thời điểm tạo bản ghi |

### 2.3. Bảng `songs` (Bài hát)
Thực thể trung tâm lưu giữ metadata bài hát và định vị tài nguyên âm thanh đóng gói.

| Tên cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Mã định danh duy nhất của bài hát |
| `title` | `TEXT` | `NOT NULL` | Tựa đề bài hát |
| `artist_id` | `INTEGER` | `NOT NULL, REFERENCES artists(id) ON DELETE RESTRICT` | Khóa ngoại liên kết nghệ sĩ trình bày |
| `album_id` | `INTEGER` | `NULL, REFERENCES albums(id) ON DELETE SET NULL` | Khóa ngoại liên kết album (có thể NULL nếu là Single) |
| `duration_ms` | `INTEGER` | `NOT NULL DEFAULT 0` | Thời lượng bài hát tính bằng mili-giây |
| `audio_res` | `TEXT` | `NOT NULL` | Tên resource tệp MP3 trong `res/raw` (không có đuôi .mp3) |
| `cover_res` | `TEXT` | `NULL` | Tên resource ảnh bìa trong `res/drawable` |
| `created_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now'))` | Thời điểm tạo bản ghi |

### 2.4. Bảng `playlists` (Danh sách phát)
Lưu trữ các playlist cá nhân do người dùng tạo hoặc playlist mặc định của hệ thống.

| Tên cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Mã định danh playlist |
| `name` | `TEXT` | `NOT NULL` | Tên playlist (ví dụ: "Nhạc tập gym", "Acoustic Chill") |
| `description` | `TEXT` | `NULL` | Mô tả nội dung playlist |
| `cover_res` | `TEXT` | `NULL` | Tên ảnh bìa đại diện của playlist |
| `created_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now'))` | Ngày tạo |
| `updated_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now'))` | Ngày cập nhật gần nhất |

### 2.5. Bảng `playlist_songs` (Bảng trung gian n - n giữa Playlist và Song)
Quản lý các bài hát thuộc playlist và vị trí thứ tự xuất hiện.

| Tên cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `playlist_id` | `INTEGER` | `NOT NULL, REFERENCES playlists(id) ON DELETE CASCADE` | Khóa ngoại trỏ đến playlist |
| `song_id` | `INTEGER` | `NOT NULL, REFERENCES songs(id) ON DELETE CASCADE` | Khóa ngoại trỏ đến bài hát |
| `position` | `INTEGER` | `NOT NULL DEFAULT 0` | Số thứ tự sắp xếp bài hát trong playlist (0, 1, 2...) |
| `added_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now'))` | Thời điểm bài hát được thêm vào playlist |

> **Ràng buộc Khóa chính:** `PRIMARY KEY (playlist_id, song_id)`  
> Đảm bảo một bài hát không bị thêm trùng lặp vào cùng một playlist nếu quy tắc nghiệp vụ cấm trùng lặp.

### 2.6. Bảng `liked_songs` (Bài hát yêu thích)
Lưu danh sách bài hát người dùng đã đánh dấu yêu thích.

| Tên cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `song_id` | `INTEGER` | `PRIMARY KEY, REFERENCES songs(id) ON DELETE CASCADE` | Khóa chính và khóa ngoại trỏ trực tiếp đến `songs.id` |
| `liked_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now'))` | Thời điểm nhấn thích bài hát |

> **Ưu điểm thiết kế:** Thay thế hoàn toàn bảng `favorites` cũ (vốn sao chép dư thừa các trường). Bảng này chỉ cần 1 số nguyên `song_id`, tối ưu 100% dung lượng và luôn phản ánh dữ liệu mới nhất của bài hát qua câu lệnh `JOIN`.

### 2.7. Bảng `listening_history` (Lịch sử nghe nhạc)
Lưu lại các lần nghe nhạc gần đây để phục vụ tính năng "Recently Played" trên trang Home.

| Tên cột | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Mã phiên nghe nhạc |
| `song_id` | `INTEGER` | `NOT NULL, REFERENCES songs(id) ON DELETE CASCADE` | Khóa ngoại trỏ đến bài hát |
| `played_at` | `INTEGER` | `NOT NULL DEFAULT (strftime('%s', 'now'))` | Thời điểm phát |
| `progress_ms` | `INTEGER` | `NOT NULL DEFAULT 0` | Tiến trình bài hát đạt được trước khi chuyển/dừng |

---

## 3. CHỈ MỤC TỐI ƯU HIỆU NĂNG (INDEXES)

Để đảm bảo các thao tác tìm kiếm thời gian thực (Live Search) và truy vấn quan hệ có độ trễ dưới 16ms (đáp ứng tốc độ khung hình 60fps):

```sql
-- Chỉ mục tìm kiếm bài hát theo tiêu đề
CREATE INDEX IF NOT EXISTS idx_songs_title ON songs(title);

-- Chỉ mục khóa ngoại liên kết bài hát với nghệ sĩ và album
CREATE INDEX IF NOT EXISTS idx_songs_artist_id ON songs(artist_id);
CREATE INDEX IF NOT EXISTS idx_songs_album_id ON songs(album_id);

-- Chỉ mục tìm kiếm nghệ sĩ và album
CREATE INDEX IF NOT EXISTS idx_artists_name ON artists(name);
CREATE INDEX IF NOT EXISTS idx_albums_title ON albums(title);

-- Chỉ mục sắp xếp thứ tự bài hát trong playlist
CREATE INDEX IF NOT EXISTS idx_playlist_songs_pos ON playlist_songs(playlist_id, position);

-- Chỉ mục truy vấn lịch sử nghe gần nhất
CREATE INDEX IF NOT EXISTS idx_history_played_at ON listening_history(played_at DESC);
```

---

## 4. QUY TRÌNH NẠP DỮ LIỆU MẪU (DATABASE SEEDING STRATEGY)

Nhằm đảm bảo dữ liệu luôn nhất quán và không bao giờ xóa mất playlist của người dùng:

1. **Kiểm tra trạng thái Seed:**
   - Đọc bảng metadata hoặc kiểm tra số lượng bản ghi:
     ```sql
     SELECT COUNT(*) FROM songs;
     ```
   - Nếu `COUNT(*) > 0`: Bỏ qua quá trình seed, không ghi đè dữ liệu.
2. **Nếu cơ sở dữ liệu trống (`COUNT == 0`), thực hiện theo thứ tự ràng buộc khóa ngoại:**
   - **Bước 1 — Insert Artists:**
     - ID 1: "Coldplay", avatar: "viva_lavi_da", bio: "British rock band formed in London in 1997."
     - ID 2: "Kis", avatar: "muoitam", bio: "Vietnamese indie artist."
     - ID 3: "Moshi", avatar: "dead_feelings", bio: "Lofi and chill music producer."
     - ID 4: "7uppercuts, Cam", avatar: "wave_alpha", bio: "Vietnamese punk rock & alternative."
     - ID 5: "Sơn Tùng M-TP", avatar: "anh1", bio: "Top V-Pop singer and songwriter."
     - ID 6: "Magnus Carlsson", avatar: "anh1", bio: "Swedish pop singer."
   - **Bước 2 — Insert Albums:**
     - ID 1: "Viva la Vida or Death and All His Friends" (artist_id: 1, cover: "viva_lavi_da", year: "2008")
     - ID 2: "Độc hành" (artist_id: 2, cover: "muoitam", year: "2023")
     - ID 3: "Lofi Night" (artist_id: 3, cover: "dead_feelings", year: "2022")
     - ID 4: "Sóng Ngầm" (artist_id: 4, cover: "wave_alpha", year: "2021")
   - **Bước 3 — Insert Songs:**
     - ID 1: "Viva La Vida" (artist: 1, album: 1, duration: 242000, audio: "viva_lavi_da__coldplay", cover: "viva_lavi_da")
     - ID 2: "Mười Tám" (artist: 2, album: 2, duration: 215000, audio: "muoitam_kis", cover: "muoitam")
     - ID 3: "Dead Feelings" (artist: 3, album: 3, duration: 185000, audio: "dead_feelings__moshi", cover: "dead_feelings")
     - ID 4: "Wave Alpha" (artist: 4, album: 4, duration: 192000, audio: "wave_alpha__7uppercuts_cam", cover: "wave_alpha")
     - ID 5: "Đừng Làm Trái Tim Anh Đau" (artist: 5, album: NULL, duration: 315000, audio: "dunglamtraitimanhdau", cover: "anh1")
     - ID 6: "I Need Your Love" (artist: 6, album: NULL, duration: 178000, audio: "ineedyourlove_magnus_carlsson", cover: "anh1")
   - **Bước 4 — Insert Default Playlists:**
     - Playlist 1: "Giai điệu tuyển chọn" (Tuyển tập các bản hit mẫu)
     - Playlist 2: "Đêm muộn & Thư giãn" (Nhạc chill, lofi)
   - **Bước 5 — Insert Playlist Songs:**
     - Gán các bài hát tương ứng vào Playlist 1 và 2 với số thứ tự `position` từ 0 đến N.

---

## 5. KẾ HOẠCH NÂNG CẤP SCHEMA (MIGRATION STRATEGY V1 -> V2)

Dự án hiện tại đang có `DatabaseHelper` với `DATABASE_VERSION = 1` gồm 2 bảng phẳng `songs` và `favorites`. Khi cập nhật lên phiên bản 2:

```java
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < 2) {
        // 1. Tạo các bảng mới (artists, albums, playlists, playlist_songs, liked_songs, listening_history)
        createRelationalTables(db);
        
        // 2. Di chuyển dữ liệu yêu thích từ bảng 'favorites' cũ sang 'liked_songs' mới
        // Dựa trên việc tìm khớp tiêu đề bài hát đã có trong bảng 'songs'
        String migrateFavoritesSql = "INSERT OR IGNORE INTO liked_songs (song_id, liked_at) " +
                "SELECT s.id, strftime('%s', 'now') FROM songs s " +
                "INNER JOIN favorites f ON s.title = f.title;";
        try {
            db.execSQL(migrateFavoritesSql);
        } catch (Exception ignored) {}
        
        // 3. Xóa bảng favorites cũ sau khi đã bảo toàn dữ liệu
        db.execSQL("DROP TABLE IF EXISTS favorites;");
    }
}
```

---

## 6. QUY TẮC THAO TÁC CƠ SỞ DỮ LIỆU AN TOÀN (SAFETY RULES)

1. **Bật Khóa Ngoại (Foreign Keys Support):**
   Trong SQLite, khóa ngoại bị tắt theo mặc định. Trong phương thức `onConfigure` của `SQLiteOpenHelper`, luôn gọi:
   ```java
   @Override
   public void onConfigure(SQLiteDatabase db) {
       super.onConfigure(db);
       db.setForeignKeyConstraintsEnabled(true);
   }
   ```
2. **Chống SQL Injection:** Tuyệt đối không nối chuỗi câu lệnh SQL với dữ liệu đầu vào người dùng. Luôn dùng `selectionArgs` hoặc `SQLiteStatement`.
3. **Thực thi Bất Đồng Bộ:** Toàn bộ lệnh gọi tới DAO/DatabaseHelper phải được thực hiện trong background thread (thông qua Executor) và bắn kết quả về UI bằng `LiveData` hoặc Callback.
4. **Đóng Cursor & Transaction:** Mọi truy vấn `Cursor` đều phải được bọc trong khối `try-finally` để gọi `cursor.close()`. Khi insert/update hàng loạt (batch insert trong seeder), luôn dùng `db.beginTransaction()`, `db.setTransactionSuccessful()`, `db.endTransaction()`.
