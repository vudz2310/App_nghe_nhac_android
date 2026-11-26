# 🎵 Resonance Music — Ứng Dụng Nghe Nhạc Android Độc Lập (Offline)

> *"Where words fail, music speaks."* — Hans Christian Andersen  
> **Resonance Music** là ứng dụng nghe nhạc di động ngoại tuyến hoàn chỉnh trên hệ điều hành Android, được xây dựng theo phong cách giao diện Dark Theme hiện đại lấy cảm hứng từ Spotify, phát nhạc nền bằng **AndroidX Media3 ExoPlayer**, quản lý cơ sở dữ liệu quan hệ với **SQLite** và kiến trúc chuẩn **MVVM + Repository**.

---

## 🌟 Tính Năng Nổi Bật

### 1. 🎧 Không Gian Phát Nhạc Hoàn Chỉnh (Mini Player & Full Player)
- **Mini Player gắn đáy:** Luôn hiển thị trạng thái phát, tên bài hát, nghệ sĩ, ảnh bìa, thanh tiến trình siêu mỏng và các nút điều khiển nhanh (Play/Pause, Next, Like). Nhấp vào bất kỳ đâu trên Mini Player để mở Full Player.
- **Full Player toàn màn hình (`FullPlayerDialog`):**
  - Ảnh bìa album bo góc lớn, tiêu đề bài hát tự động cuộn chữ (Marquee).
  - Thanh `SeekBar` mượt mà, hỗ trợ tua nhạc thời gian thực kèm nhãn thời gian hiện tại (`00:00`) và tổng thời lượng (`04:02`).
  - Cụm điều khiển: **Shuffle** (Xáo trộn thứ tự phát ngẫu nhiên nhưng bảo toàn bài đang phát), **Previous** (Tua về đầu bài hoặc lùi bài trước), **Play / Pause** nổi bật, **Next**, **Repeat** (Off -> All -> One).
  - Nút **Yêu thích (Heart)**: Thao tác 1 chạm lưu ngay vào cơ sở dữ liệu.
  - Nút mở danh sách **Hàng đợi (Queue)** trực quan.

### 2. 🌌 Phát Nhạc Nền Tuyệt Đối (Background Playback & System Control)
- Chạy nền độc lập qua `androidx.media3.session.MediaSessionService` (`PlaybackService`).
- **Khóa màn hình (Lockscreen):** Nhạc tiếp tục phát mượt mà, hỗ trợ widget điều khiển màn hình khóa Android.
- **Chuyển ứng dụng:** Chuyển sang ứng dụng khác hoặc bấm Home không làm gián đoạn âm thanh.
- **Notification điều khiển:** Tích hợp thanh thông báo hệ thống MediaStyle với nút Play/Pause/Next/Previous và ảnh bìa bài hát.
- **Audio Focus tự động:** Tự động tạm dừng khi có cuộc gọi đến, giảm âm lượng khi có thông báo định vị/chuông báo, tự động dừng khi ngắt kết nối tai nghe (`ACTION_AUDIO_BECOMING_NOISY`).

### 3. 📚 Thư Viện Nhạc Cục Bộ & Quản Lý Danh Sách Phát (Library & Playlists)
- **Tự động khởi tạo kho nhạc (Auto-seeding SQLite):** Nạp sẵn 6 bài hát chất lượng cao:
  - 🎸 *Viva La Vida* — Coldplay (Album: *Viva la Vida or Death and All His Friends*)
  - 🌙 *Mười Tám* — Kis (Album: *Độc hành*)
  - 🌧️ *Dead Feelings* — Moshi (Album: *Lofi Night*)
  - ⚡ *Wave Alpha* — 7uppercuts, Cam (Album: *Sóng Ngầm*)
  - 💖 *Đừng Làm Trái Tim Anh Đau* — Sơn Tùng M-TP
  - ✨ *I Need Your Love* — Magnus Carlsson
- **Quản lý Playlist (CRUD):** Tạo playlist mới, xem danh sách bài hát trong playlist, thêm bài hát vào playlist, xóa playlist (bảo toàn bài hát gốc trong database).
- **Danh sách yêu thích (Liked Songs):** Lối tắt chuyên biệt truy cập toàn bộ các bài hát đã đánh dấu tim.

### 4. 🔍 Tìm Kiếm Thời Gian Thực (Live Search)
- Lọc tức thì bài hát theo tiêu đề hoặc nghệ sĩ với cơ chế **Debounce 300ms** tối ưu hiệu năng.

---

## 🛠️ Kiến Trúc Hệ Thống (Architecture & Tech Stack)

```text
                 PRESENTATION LAYER (UI)
┌────────────────────────────────────────────────────────┐
│  AppMainActivity (Single Host Activity)                │
│   ├── HomeFragment        (Nghe gần đây, Album)        │
│   ├── SearchFragment      (Tìm kiếm thời gian thực)    │
│   └── LibraryFragment     (Playlists, Yêu thích)       │
│                                                        │
│  Interactive Overlays:                                 │
│   ├── MiniPlayerView      (Gắn đáy)                    │
│   ├── FullPlayerDialog    (Toàn màn hình)              │
│   └── QueueDialog         (Hàng đợi phát)              │
└──────────────────────────┬─────────────────────────────┘
                           │
             ┌─────────────┴─────────────┐
             ▼                           ▼
      [ LUỒNG DỮ LIỆU ]           [ LUỒNG PHÁT NHẠC ]
             │                           │
         ViewModel                PlaybackController
             │                           │ (MediaController IPC)
         Repository                      ▼
             │                    PlaybackService
      ┌──────┴──────┐                    │
      ▼             ▼              MediaSession
SQLite Database  Asset/Raw               │
 (7 Bảng DB)   (6 MP3 / Covers)      ExoPlayer
```

- **Ngôn ngữ:** Thuần Java 8+ (`JavaVersion.VERSION_1_8`), biên dịch trên môi trường JDK 17.
- **Nền tảng đích:** `minSdk = 31` (Android 12), `targetSdk = 34` (Android 14), `compileSdk = 34`.
- **Giao diện:** XML Layouts chuẩn Dark Theme Spotify (`#121212`, `#181818`, `#1DB954`).
- **Audio Engine:** `androidx.media3:media3-exoplayer:1.3.1` + `media3-session:1.3.1` + `media3-ui:1.3.1`.
- **Cơ sở dữ liệu:** `SQLiteOpenHelper` (`MusicDatabaseHelper` v2) với 7 bảng quan hệ có khóa ngoại (`artists`, `albums`, `songs`, `playlists`, `playlist_songs`, `liked_songs`, `listening_history`) và chỉ mục truy vấn tối ưu.
- **Đa luồng an toàn:** Toàn bộ truy vấn Database chạy trên Background Executor Thread, cập nhật UI qua `LiveData` và Main Looper.

---

## 📁 Cấu Trúc Mã Nguồn

```text
app/src/main/
├── java/com/example/android_finalexam/
│   ├── data/
│   │   ├── local/
│   │   │   ├── database/
│   │   │   │   ├── DatabaseContract.java     # Định nghĩa 7 bảng và cột SQLite
│   │   │   │   ├── MusicDatabaseHelper.java  # SQLiteOpenHelper v2, Foreign Keys
│   │   │   │   └── DatabaseSeeder.java       # Tự động nạp dữ liệu mẫu
│   │   │   └── dao/
│   │   │       ├── SongDao.java              # Truy vấn bài hát, tìm kiếm
│   │   │       ├── ArtistDao.java            # Truy vấn nghệ sĩ
│   │   │       ├── AlbumDao.java             # Truy vấn album
│   │   │       ├── PlaylistDao.java          # CRUD playlist và playlist_songs
│   │   │       ├── LikedSongDao.java         # Quản lý bài hát yêu thích
│   │   │       └── HistoryDao.java           # Lịch sử nghe gần đây
│   │   └── repository/
│   │       ├── SongRepository.java           # Repository bài hát & nghệ sĩ
│   │       └── PlaylistRepository.java       # Repository playlist
│   │
│   ├── domain/model/
│   │   ├── SongItem.java                     # Model bài hát
│   │   ├── Artist.java                       # Model nghệ sĩ
│   │   ├── Album.java                        # Model album
│   │   └── Playlist.java                     # Model playlist
│   │
│   ├── player/
│   │   ├── PlaybackService.java              # MediaSessionService phát nền
│   │   ├── PlaybackController.java           # MediaController cầu nối UI
│   │   ├── PlaybackState.java                # Trạng thái phát thời gian thực
│   │   ├── PlaybackStatus.java               # Enum trạng thái
│   │   └── QueueManager.java                 # Quản lý hàng đợi, Shuffle & Repeat
│   │
│   ├── ui/
│   │   ├── main/
│   │   │   └── AppMainActivity.java          # Màn hình chính lưu trữ Navigation
│   │   ├── home/
│   │   │   ├── HomeFragment.java             # Trang chủ
│   │   │   ├── HomeViewModel.java            # ViewModel trang chủ
│   │   │   └── adapter/                      # Adapters cho Horizontal Carousels
│   │   ├── search/
│   │   │   ├── SearchFragment.java           # Trang tìm kiếm
│   │   │   └── SearchViewModel.java          # ViewModel tìm kiếm
│   │   ├── library/
│   │   │   ├── LibraryFragment.java          # Trang thư viện
│   │   │   ├── LibraryViewModel.java         # ViewModel thư viện
│   │   │   └── adapter/                      # Playlist Adapter
│   │   ├── player/
│   │   │   ├── FullPlayerDialog.java         # Trình phát toàn màn hình
│   │   │   ├── QueueDialog.java              # Hàng đợi phát nhạc
│   │   │   └── QueueAdapter.java             # Adapter hàng đợi
│   │   └── common/adapter/
│   │       └── SongListAdapter.java          # Adapter danh sách bài hát chuẩn
│   │
│   └── utils/
│       └── ResourceUtils.java                # Tra cứu an toàn Drawable & Raw URI
│
└── res/
    ├── drawable/                             # 15+ Vector XML icons & Backgrounds
    ├── layout/                               # XML layouts chuẩn Dark Theme
    ├── menu/                                 # bottom_nav_menu.xml
    ├── raw/                                  # 6 tệp MP3 chất lượng cao
    └── values/                               # colors.xml, dimens.xml, strings.xml, themes.xml
```

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### 1. Build trực tiếp bằng Gradle
```bash
./gradlew assembleDebug
```
Tệp APK sau khi build nằm tại:
`app/build/outputs/apk/debug/app-debug.apk`

### 2. Cài đặt trực tiếp lên thiết bị Android hoặc Emulator qua ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📋 Tài Liệu Đặc Tả Chi Tiết

- [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md): Ngữ cảnh & chuẩn mực phát triển độc lập.
- [docs/PROJECT_ANALYSIS.md](docs/PROJECT_ANALYSIS.md): Báo cáo đánh giá hiện trạng & kiểm toán kỹ thuật.
- [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md): Đặc tả yêu cầu 8 phân hệ & ma trận kiểm thử TC01 - TC15.
- [docs/DATABASE_DESIGN.md](docs/DATABASE_DESIGN.md): Thiết kế cơ sở dữ liệu quan hệ SQLite 7 bảng.
- [docs/IMPLEMENTATION_PLAN.md](docs/IMPLEMENTATION_PLAN.md): Kế hoạch triển khai tuần tự theo 8 Phase.
