# PROJECT CONTEXT — INDEPENDENT ANDROID MUSIC PLAYER

> **Tài liệu ngữ cảnh và thông số kỹ thuật cốt lõi dành cho AI Coding Agent & Developer.**  
> Dự án: **Resonance Music (Android Independent Music Player)**  
> Trách nhiệm: Phát triển ứng dụng nghe nhạc Android độc lập, thuần Java, giao diện XML Dark Theme hiện đại (lấy cảm hứng từ Spotify), phát nhạc offline với AndroidX Media3 ExoPlayer, quản lý dữ liệu bằng SQLite thuần và kiến trúc MVVM + Repository.

---

## 1. TỔNG QUAN DỰ ÁN & PHẠM VI NGHIỆP VỤ

### 1.1. Mục tiêu
Xây dựng ứng dụng nghe nhạc di động chạy trên hệ điều hành Android hoàn toàn độc lập, không phụ thuộc vào bất kỳ kết nối mạng nào và không gọi đến Spotify API hoặc bất kỳ dịch vụ streaming bên ngoài nào.

### 1.2. Đối tượng & Phạm vi sử dụng
- **Nền tảng:** Android Smartphone (hỗ trợ Android 12+ / SDK 31 đến SDK 34+).
- **Xác thực:** Không cần tài khoản, không cần đăng nhập. Mở app là có thể trải nghiệm toàn bộ tính năng.
- **Nguồn âm thanh:** Đóng gói trực tiếp trong ứng dụng (`res/raw/`) cùng ảnh bìa (`res/drawable/`).
- **Lưu trữ dữ liệu:** Toàn bộ thư viện, playlist cá nhân, bài hát yêu thích và lịch sử nghe nhạc được lưu trữ bền vững trong cơ sở dữ liệu SQLite cục bộ trên thiết bị.
- **Phát nhạc:** Phát nhạc ngoại tuyến (Offline 100%), hỗ trợ phát nền khi khóa màn hình hoặc chuyển ứng dụng, điều khiển qua Notification và thiết bị ngoại vi (tai nghe, Bluetooth).

### 1.3. Ngoài phạm vi (Out of Scope for V1)
- Tài khoản người dùng trực tuyến, đăng nhập xã hội.
- Đồng bộ hóa đám mây giữa nhiều thiết bị.
- Streaming từ máy chủ từ xa, tải bài hát từ Internet.
- Trí tuệ nhân tạo (AI) gợi ý bài hát theo hành vi người dùng.
- Thanh toán Premium, quảng cáo thương mại, Podcast.

---

## 2. TECH STACK & MÔI TRƯỜNG KỸ THUẬT

| Thành phần | Công nghệ / Quy chuẩn | Ghi chú |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java 8+ (`sourceCompatibility = JavaVersion.VERSION_1_8`) | Tuyệt đối KHÔNG chuyển đổi sang Kotlin |
| **Giao diện** | Android XML Layouts | Dark Theme hiện đại, Content-First; KHÔNG dùng Jetpack Compose |
| **Hệ điều hành đích** | `minSdk = 31`, `targetSdk = 34`, `compileSdk = 34` | Đã cấu hình và kiểm chứng trong Gradle |
| **Build System** | Gradle 8.13 + AGP 8.13.2 | Quản lý dependencies qua `libs.versions.toml` |
| **JVM Daemon** | JDK 17 (`C:/Program Files/Java/jdk-17`) | Môi trường biên dịch hiện hành |
| **Kiến trúc ứng dụng** | MVVM (Model - View - ViewModel) + Repository | Phân chia trách nhiệm rõ ràng, một chiều |
| **Cơ sở dữ liệu** | `SQLiteOpenHelper` + SQLite DAO | Không dùng Room ORM; chạy bất đồng bộ qua Executor/Background Thread |
| **Audio Engine** | AndroidX Media3 ExoPlayer (`1.11.1` hoặc tương thích) | Tách biệt Player trong `MediaSessionService` |
| **Background Playback** | `MediaSessionService` + `MediaSession` + `MediaController` | Notification điều khiển hệ thống, phản hồi Audio Focus |
| **Hiển thị danh sách** | `RecyclerView` + `ListAdapter` / `DiffUtil` | Thay thế toàn bộ `ListView` và `ArrayAdapter` cũ |
| **State Management** | `LiveData` + `ViewModel` | Đảm bảo UI phản ứng tự động theo dữ liệu |
| **Image Loading** | `Glide` (hoặc xử lý Resource Drawable tối ưu) | Tải và cache ảnh bìa mượt mà |

---

## 3. KIẾN TRÚC TỔNG THỂ & HAI LUỒNG TÁCH BIỆT

```text
                 PRESENTATION LAYER (UI)
┌────────────────────────────────────────────────────────┐
│  MainActivity (Single Activity Container)             │
│   ├── HomeFragment        (Khám phá, Nghe gần đây)     │
│   ├── SearchFragment      (Tìm kiếm thời gian thực)    │
│   ├── LibraryFragment     (Playlists, Yêu thích, v.v.) │
│   ├── PlaylistDetailFragment                           │
│   ├── AlbumDetailFragment                              │
│   └── ArtistDetailFragment                             │
│                                                        │
│  Global Components:                                    │
│   ├── MiniPlayerView      (Gắn đáy màn hình)           │
│   ├── FullPlayerBottomSheet / Dialog                   │
│   └── QueueBottomSheet    (Hàng đợi phát nhạc)         │
└──────────────────────────┬─────────────────────────────┘
                           │
             ┌─────────────┴─────────────┐
             ▼                           ▼
      [ LUỒNG DỮ LIỆU ]           [ LUỒNG PHÁT NHẠC ]
             │                           │
         ViewModel                MediaController
             │                           │ (IPC / Binder)
         Repository                      ▼
             │                    PlaybackService
      ┌──────┴──────┐                    │
      ▼             ▼              MediaSession
SQLite Local   Asset/Raw                 │
 (Database)     (Audio/Cover)        ExoPlayer
                                         │
                                   Audio Output
```

### 3.1. Quy tắc cốt lõi:
1. **Luồng dữ liệu (Data Flow):** `UI (Fragment) → ViewModel → Repository → SQLite DAO / Asset DataSource`. UI quan sát `LiveData`, không bao giờ gọi trực tiếp SQLite hoặc mở Cursor.
2. **Luồng phát nhạc (Playback Flow):** `UI (MiniPlayer/FullPlayer) → MediaController → PlaybackService (MediaSession) → ExoPlayer`. Activity hoặc Fragment **tuyệt đối không** tự khởi tạo hay sở hữu vòng đời của `ExoPlayer` / `MediaPlayer`.
3. **Đồng bộ phiên phát:** Mini Player và Full Player là 2 góc nhìn (views) của cùng một phiên `MediaSession`. Khi bài hát đổi hoặc pause/play, cả hai thành phần UI cùng nhận cập nhật tức thời qua `Player.Listener`.

---

## 4. DANH MỤC TÀI NGUYÊN MẪU CÓ SẴN (BUNDLED ASSETS)

Ứng dụng đã có sẵn 6 tệp âm thanh MP3 chất lượng cao và ảnh bìa trong thư mục tài nguyên:

| ID | Tiêu đề | Nghệ sĩ | Album | Thời lượng (ước tính) | Audio Resource (`res/raw`) | Cover Resource (`res/drawable`) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | Viva La Vida | Coldplay | Viva la Vida or Death and All His Friends | 242000 ms (04:02) | `viva_lavi_da__coldplay` | `viva_lavi_da` |
| **2** | Mười Tám | Kis | Độc hành | 215000 ms (03:35) | `muoitam_kis` | `muoitam` |
| **3** | Dead Feelings | Moshi | Lofi Night | 185000 ms (03:05) | `dead_feelings__moshi` | `dead_feelings` |
| **4** | Wave Alpha | 7uppercuts, Cam | Sóng Ngầm | 192000 ms (03:12) | `wave_alpha__7uppercuts_cam` | `wave_alpha` |
| **5** | Đừng Làm Trái Tim Anh Đau | Sơn Tùng M-TP | Single 2024 | 315000 ms (05:15) | `dunglamtraitimanhdau` | `anh1` |
| **6** | I Need Your Love | Magnus Carlsson | Euro Pop | 178000 ms (02:58) | `ineedyourlove_magnus_carlsson` | `anh1` |

---

## 5. CẤU TRÚC GÓI ĐÍCH (TARGET PACKAGE STRUCTURE)

Dự án sẽ tái cấu trúc mã nguồn theo mô hình chuẩn Clean Architecture & MVVM trong package `com.example.android_finalexam`:

```text
app/src/main/java/com/example/android_finalexam/
├── MusicApplication.java                # Application class, khởi tạo DB & Service
│
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── MusicDatabaseHelper.java # Quản lý kết nối SQLite, schema & migration
│   │   │   ├── DatabaseContract.java    # Khai báo hằng số bảng và cột
│   │   │   └── DatabaseSeeder.java      # Nạp dữ liệu mẫu ban đầu (idempotent)
│   │   └── dao/
│   │       ├── SongDao.java             # Truy vấn bài hát, tìm kiếm
│   │       ├── ArtistDao.java           # Truy vấn nghệ sĩ
│   │       ├── AlbumDao.java            # Truy vấn album
│   │       ├── PlaylistDao.java         # CRUD playlist và playlist_songs
│   │       ├── LikedSongDao.java        # Quản lý yêu thích
│   │       └── HistoryDao.java          # Ghi nhận và lấy lịch sử nghe gần đây
│   └── repository/
│       ├── SongRepository.java
│       ├── PlaylistRepository.java
│       └── LibraryRepository.java
│
├── domain/
│   └── model/
│       ├── Song.java                    # Model bài hát mở rộng
│       ├── Artist.java                  # Model nghệ sĩ
│       ├── Album.java                   # Model album
│       ├── Playlist.java                # Model danh sách phát
│       ├── PlaybackState.java           # Trạng thái phát nhạc
│       └── QueueItem.java               # Phần tử trong hàng đợi
│
├── player/
│   ├── PlaybackService.java             # MediaSessionService chạy nền
│   ├── PlaybackController.java          # Wrapper MediaController cho UI
│   └── QueueManager.java                # Quản lý hàng đợi, Shuffle, Repeat logic
│
├── ui/
│   ├── main/
│   │   ├── MainActivity.java            # Single Host Activity, chứa BottomNav & MiniPlayer
│   │   └── MainViewModel.java
│   ├── home/
│   │   ├── HomeFragment.java
│   │   ├── HomeViewModel.java
│   │   └── adapter/                     # Adapters cho Recent, Featured, Albums
│   ├── search/
│   │   ├── SearchFragment.java
│   │   └── SearchViewModel.java
│   ├── library/
│   │   ├── LibraryFragment.java
│   │   └── LibraryViewModel.java
│   ├── player/
│   │   ├── MiniPlayerView.java          # Custom View hoặc Fragment cho Mini Player
│   │   ├── FullPlayerBottomSheet.java   # Màn hình Full Player
│   │   └── QueueBottomSheet.java        # Màn hình danh sách hàng đợi phát
│   ├── playlist/
│   │   ├── PlaylistDetailFragment.java
│   │   └── CreatePlaylistDialog.java
│   ├── album/
│   │   └── AlbumDetailFragment.java
│   ├── artist/
│   │   └── ArtistDetailFragment.java
│   └── common/
│       ├── BaseFragment.java
│       └── viewholder/
│
└── utils/
    ├── TimeUtils.java                   # Định dạng mm:ss
    ├── ResourceUtils.java               # Tra cứu Drawable và Raw ID an toàn
    └── Constants.java                   # Khai báo Action, Keys, Intent extras
```

---

## 6. QUY TẮC PHÁT TRIỂN & BẢO TOÀN DỰ ÁN

1. **Không sửa đổi hoặc xóa code đang chạy khi chưa được giao:** Các Activity cũ (`ListSong`, `MainActivity`, v.v.) được giữ nguyên cho đến khi các Module mới được nghiệm thu độc lập.
2. **Không tự ý nâng toàn bộ Gradle/SDK:** Giữ vững `compileSdk = 34`, `targetSdk = 34`, `minSdk = 31`, `Java 8` compatibility và Gradle 8.13.
3. **Thực thi theo từng Phase tuần tự:** Mỗi phase phải hoàn tất build `assembleDebug` với 0 lỗi biên dịch trước khi bắt đầu phase tiếp theo.
4. **Xử lý Threading an toàn:** Toàn bộ truy vấn SQLite phải thực thi trong Background Thread (sử dụng `Executors.newSingleThreadExecutor()` hoặc background Handler), trả kết quả về UI qua Main Looper / `LiveData.postValue()`.
5. **Duy trì quyền riêng tư & Offline:** Ứng dụng không yêu cầu các quyền nhạy cảm không cần thiết (chỉ dùng `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `POST_NOTIFICATIONS` cho tính năng nghe nhạc nền).
