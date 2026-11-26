# IMPLEMENTATION PLAN — KẾ HOẠCH TRIỂN KHAI THEO GIAI ĐOẠN

> **Dự án:** Resonance Music (Independent Android Music Player)  
> **Phương pháp tiếp cận:** Phân chia thành 8 giai đoạn nhỏ (Phases) khép kín. Mỗi giai đoạn đều phải build thành công (`assembleDebug`) và đạt đủ tiêu chí nghiệm thu trước khi chuyển tiếp. Tuyệt đối không sinh mã nguồn ồ ạt mà không qua kiểm thử.

---

## 1. TỔNG QUAN 8 GIAI ĐOẠN TRIỂN KHAI

```text
┌────────────────────────────────────────────────────────┐
│ Phase 1: Project Setup & Baseline Alignment            │
│  - Hoàn thiện tài liệu, cấu hình Gradle & Dependencies  │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ Phase 2: Design System & UI Foundation                 │
│  - Dark Theme, Navigation Container, BottomNav, Tokens │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ Phase 3: Data Layer & SQLite Persistence               │
│  - Entity Models, DatabaseHelper v2, DAOs, Seeder      │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ Phase 4: Core Browsing Screens                         │
│  - Home, Search, Library, Playlist, Artist, Album      │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ Phase 5: Audio Playback Engine (Media3 ExoPlayer)      │
│  - PlaybackService, MediaSession, QueueManager, State  │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ Phase 6: Background Playback & UI Synchronization      │
│  - Mini Player, Full Player, Notification, Audio Focus │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ Phase 7: UX Polish, Robustness & Edge Cases            │
│  - Loading/Empty/Error states, Leak check, Dialogs     │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ Phase 8: Final Review, Regression & Release APK Build  │
│  - Kiểm thử 15 Test Cases, Build Signed/Debug APK      │
└────────────────────────────────────────────────────────┘
```

---

## 2. CHI TIẾT TỪNG GIAI ĐOẠN (PHASE SPECIFICATIONS)

---

### PHASE 1: PROJECT SETUP & BASELINE ALIGNMENT
- **Mục tiêu:**
  - Thiết lập nền tảng dự án ổn định, kiểm soát phiên bản và chuẩn bị các thư viện cần thiết.
  - Bổ sung các dependencies chuẩn vào `gradle/libs.versions.toml` và `app/build.gradle.kts`.
  - Khai báo các quyền hạn nền tảng trong `AndroidManifest.xml`.
  - Đảm bảo dự án biên dịch sạch sẽ (`assembleDebug` PASS) với Java 8 + SDK 34.
- **Danh sách file tạo / sửa:**
  - Sửa `gradle/libs.versions.toml`: Khai báo Media3 `1.11.1` (hoặc bản tương thích), Lifecycle ViewModel/LiveData `2.7.0` (hoặc tương thích), RecyclerView `1.3.2`, Glide `4.16.0`.
  - Sửa `app/build.gradle.kts`: Áp dụng các dependencies mới, giữ nguyên `JavaVersion.VERSION_1_8` và SDK 34.
  - Sửa `app/src/main/AndroidManifest.xml`: Xóa cảnh báo package thừa, bổ sung `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `POST_NOTIFICATIONS`.
- **Phụ thuộc:** Không.
- **Rủi ro & Biện pháp kiểm soát:**
  - *Rủi ro:* Xung đột phiên bản AndroidX giữa Media3 và AGP 8.13.2.
  - *Biện pháp:* Dùng cùng một phiên bản cho toàn bộ gói `media3-*` (ví dụ `1.11.1` hoặc bản stable mới nhất phù hợp `compileSdk = 34`).
- **Tiêu chí nghiệm thu (Acceptance Criteria):**
  1. `./gradlew assembleDebug` hoàn thành thành công trong dưới 10 giây với 0 lỗi biên dịch.
  2. Manifest không còn cảnh báo "package attribute is ignored".

---

### PHASE 2: DESIGN SYSTEM & UI FOUNDATION
- **Mục tiêu:**
  - Xây dựng hệ thống giao diện Dark Theme chuẩn xác theo cảm hứng Spotify.
  - Tạo Single Activity container (`MainActivity`) tích hợp Bottom Navigation Bar (`Home`, `Search`, `Library`).
  - Thiết kế các UI Components tái sử dụng: `SongItemView`, `AlbumCardView`, `PlaylistCardView`, thanh `MiniPlayerView` gắn đáy.
- **Danh sách file tạo / sửa:**
  - Tạo `res/values/dimens.xml`, chuẩn hóa `res/values/colors.xml` và `res/values/themes.xml`.
  - Tạo layout `res/layout/activity_main_container.xml` (hoặc refactor `MainActivity` layout với `FragmentContainerView`, `BottomNavigationView`, `mini_player_include`).
  - Tạo menu điều hướng `res/menu/bottom_nav_menu.xml`.
  - Tạo drawables: Icon trạng thái active/inactive cho Bottom Navigation, ripple effect, placeholder bìa đĩa.
  - Tạo các khung Fragment ban đầu: `HomeFragment.java`, `SearchFragment.java`, `LibraryFragment.java`.
- **Phụ thuộc:** Hoàn thành Phase 1.
- **Rủi ro & Biện pháp kiểm soát:**
  - *Rủi ro:* Mini Player che khuất nội dung của các Fragment bên dưới.
  - *Biện pháp:* Sử dụng `CoordinatorLayout` hoặc đệm dưới (`android:paddingBottom`) tương ứng với chiều cao của Mini Player + BottomNav.
- **Tiêu chí nghiệm thu:**
  1. Mở ứng dụng hiển thị giao diện Dark Theme hiện đại, chuyển đổi mượt mà giữa 3 Tab Home, Search, Library mà không bị giật lag.
  2. Mini Player hiển thị gọn gàng ở đáy màn hình, sẵn sàng đón nhận trạng thái phát.

---

### PHASE 3: DATA LAYER & SQLITE PERSISTENCE
- **Mục tiêu:**
  - Hiện thực hóa kiến trúc dữ liệu quan hệ 7 bảng theo đúng `docs/DATABASE_DESIGN.md`.
  - Xây dựng `MusicDatabaseHelper` (v2), `DatabaseContract`, các DAO chuyên biệt (`SongDao`, `ArtistDao`, `AlbumDao`, `PlaylistDao`, `LikedSongDao`, `HistoryDao`).
  - Xây dựng `DatabaseSeeder` tự động nạp 6 bài hát mẫu kèm quan hệ Artist, Album, Playlist mẫu.
  - Xây dựng `SongRepository`, `PlaylistRepository`, `LibraryRepository`.
- **Danh sách file tạo / sửa:**
  - Tạo package `data/local/database/`: `MusicDatabaseHelper.java`, `DatabaseContract.java`, `DatabaseSeeder.java`.
  - Tạo package `data/local/dao/`: `SongDao.java`, `ArtistDao.java`, `AlbumDao.java`, `PlaylistDao.java`, `LikedSongDao.java`, `HistoryDao.java`.
  - Tạo package `domain/model/`: `Song.java`, `Artist.java`, `Album.java`, `Playlist.java`.
  - Tạo package `data/repository/`: `SongRepository.java`, `PlaylistRepository.java`, `LibraryRepository.java`.
  - Tạo `utils/ResourceUtils.java` tra cứu an toàn các ID tài nguyên đóng gói.
- **Phụ thuộc:** Hoàn thành Phase 2.
- **Rủi ro & Biện pháp kiểm soát:**
  - *Rủi ro:* Thao tác đọc ghi Database làm đơ giao diện Main UI Thread (gây ANR).
  - *Biện pháp:* Đóng gói mọi thao tác truy vấn DAO qua `java.util.concurrent.Executors.newSingleThreadExecutor()` hoặc background worker, trả dữ liệu ra ngoài qua `LiveData` hoặc Callback.
- **Tiêu chí nghiệm thu:**
  1. Kiểm thử Unit/Integration đọc dữ liệu: 6 bài hát mẫu, 6 nghệ sĩ, 4 album và 2 playlist được nạp đúng vào SQLite.
  2. Thao tác Thêm/Xóa/Sửa Playlist hoạt động bền vững, dữ liệu tồn tại sau khi tắt và mở lại ứng dụng (TC01, TC04, TC06, TC14).

---

### PHASE 4: CORE BROWSING SCREENS (HOME, SEARCH, LIBRARY)
- **Mục tiêu:**
  - Hoàn thiện giao diện và kết nối dữ liệu thật từ SQLite vào 3 màn hình cốt lõi: Home, Search, Library cùng các màn hình chi tiết (Playlist Detail, Album Detail, Artist Detail).
  - Sử dụng `RecyclerView` + `ListAdapter` hiển thị danh sách bài hát mượt mà.
  - Tích hợp ô tìm kiếm tức thì với Debounce 300ms.
- **Danh sách file tạo / sửa:**
  - Package `ui/home/`: `HomeFragment.java`, `HomeViewModel.java`, `RecentSongsAdapter.java`, `AlbumsHorizontalAdapter.java`.
  - Package `ui/search/`: `SearchFragment.java`, `SearchViewModel.java`, `SearchResultAdapter.java`.
  - Package `ui/library/`: `LibraryFragment.java`, `LibraryViewModel.java`, `PlaylistListAdapter.java`.
  - Package `ui/playlist/`: `PlaylistDetailFragment.java`, `PlaylistDetailViewModel.java`, `CreatePlaylistDialog.java`.
  - Package `ui/album/`: `AlbumDetailFragment.java`.
  - Package `ui/artist/`: `ArtistDetailFragment.java`.
  - Layouts XML tương ứng trong `res/layout/`.
- **Phụ thuộc:** Hoàn thành Phase 3.
- **Rủi ro & Biện pháp kiểm soát:**
  - *Rủi ro:* Danh sách cuộn bị giật hình (Jank) do decode ảnh bìa lớn.
  - *Biện pháp:* Tối ưu kích thước hiển thị của `ImageView`, áp dụng `inSampleSize` hoặc thư viện Glide để nạp ảnh bất đồng bộ với bộ nhớ đệm (Memory Cache).
- **Tiêu chí nghiệm thu:**
  1. Màn hình Home hiển thị đầy đủ danh sách bài hát và album (TC02).
  2. Tìm kiếm theo tên bài hát hoặc nghệ sĩ phản hồi tức thì với kết quả chính xác (TC03).
  3. Mở xem chi tiết Playlist, Album, Nghệ sĩ hiển thị danh sách bài hát tương ứng.

---

### PHASE 5: AUDIO PLAYBACK ENGINE (MEDIA3 EXOPLAYER)
- **Mục tiêu:**
  - Xây dựng tầng phát nhạc chuyên nghiệp, tách biệt hoàn toàn khỏi Activity bằng AndroidX Media3.
  - Triển khai `PlaybackService` kế thừa `MediaSessionService`.
  - Khởi tạo `ExoPlayer` duy nhất được bao bọc bởi `MediaSession`.
  - Xây dựng `QueueManager` quản lý danh sách phát, thứ tự bài hát, logic Shuffle (bảo toàn bài đang phát) và Repeat (Off, All, One).
  - Xây dựng `PlaybackController` làm cầu nối giữa UI và Service thông qua `MediaController`.
- **Danh sách file tạo / sửa:**
  - Package `player/`:
    - `PlaybackService.java`: Khởi tạo Player, MediaSession, xử lý Audio Focus.
    - `PlaybackController.java`: Quản lý kết nối `MediaController.Builder(context, sessionToken)`.
    - `QueueManager.java`: Quản lý MediaItems, timeline phát ngẫu nhiên và lặp lại.
    - `PlaybackState.java`: Enum và POJO chứa tiến trình, trạng thái phát, bài hát hiện tại.
  - Khai báo `<service>` trong `AndroidManifest.xml`.
- **Phụ thuộc:** Hoàn thành Phase 4.
- **Rủi ro & Biện pháp kiểm soát:**
  - *Rủi ro:* Lỗi giải phóng `MediaController` hoặc mất kết nối IPC khi Activity tái tạo (Configuration Change / Xoay màn hình).
  - *Biện pháp:* Khởi tạo `PlaybackController` gắn theo vòng đời của Application hoặc Host Activity với Singleton/ServiceConnection an toàn.
- **Tiêu chí nghiệm thu:**
  1. Phát thành công các tệp MP3 mẫu từ `res/raw` thông qua URI nội bộ (`android.resource://...`) (TC07).
  2. Các lệnh điều khiển Play, Pause, Next, Previous, Seek hoạt động chính xác theo tiêu chuẩn Media3 (TC08, TC09, TC10).

---

### PHASE 6: BACKGROUND PLAYBACK & UI SYNCHRONIZATION
- **Mục tiêu:**
  - Hoàn thiện phát nhạc nền khi ẩn ứng dụng hoặc khóa màn hình.
  - Hiển thị thanh thông báo hệ thống (Notification) tích hợp sẵn các nút điều khiển âm nhạc.
  - Đồng bộ trạng thái thời gian thực giữa: Mini Player, Full Player BottomSheet, Queue BottomSheet và Notification.
  - Bắt các sự kiện tai nghe ngắt kết nối (`ACTION_AUDIO_BECOMING_NOISY`), cuộc gọi đến (Audio Focus Transient).
- **Danh sách file tạo / sửa:**
  - Package `ui/player/`:
    - `MiniPlayerView.java`: Bắt sự kiện click để mở Full Player, cập nhật trạng thái Play/Pause.
    - `FullPlayerBottomSheet.java`: Giao diện phát toàn màn hình với SeekBar tương tác thời gian thực, nút Shuffle, Repeat, Like, Next, Previous.
    - `QueueBottomSheet.java`: Hiển thị hàng đợi bài hát đang phát và các bài tiếp theo, hỗ trợ xóa và sắp xếp.
  - Cấu hình Notification trong `PlaybackService.java`.
- **Phụ thuộc:** Hoàn thành Phase 5.
- **Rủi ro & Biện pháp kiểm soát:**
  - *Rủi ro:* SeekBar bị giật khi người dùng đang kéo ngón tay (Touch Conflict với tiến trình cập nhật từ Player).
  - *Biện pháp:* Dùng biến cờ `isTrackingTouch` trong `OnSeekBarChangeListener`. Khi người dùng chạm kéo, tạm dừng cập nhật tự động từ Player, chỉ cập nhật khi nhả ngón tay (`onStopTrackingTouch`).
- **Tiêu chí nghiệm thu:**
  1. Khóa màn hình hoặc chuyển sang ứng dụng khác nhạc vẫn tiếp tục phát liền mạch (TC11, TC12).
  2. Bấm Play/Pause/Next trên Notification điều khiển chính xác và UI cập nhật đồng thời (TC13).
  3. Mini Player và Full Player luôn hiển thị cùng một thông tin bài hát và tiến trình.

---

### PHASE 7: UX POLISH, ROBUSTNESS & EDGE CASES
- **Mục tiêu:**
  - Xử lý toàn diện các trạng thái UX: Loading (Skeleton/Shimmer), Empty State (khi danh sách trống, kèm CTA), Error State (khi lỗi, kèm nút thử lại).
  - Hoàn thiện các Dialog xác nhận: Xóa playlist, Xóa bài hát, Thoát chế độ phát.
  - Kiểm tra và khắc phục triệt để các nguy cơ Memory Leak (rò rỉ Listener, Handler, Cursor).
  - Đảm bảo ứng dụng không bao giờ bị crash đột ngột khi gặp file âm thanh lỗi (TC15).
- **Danh sách file tạo / sửa:**
  - Tạo các layout trạng thái trống: `layout_empty_state.xml`, `layout_error_state.xml`.
  - Tối ưu hóa các Adapter, sử dụng `DiffUtil.ItemCallback` để chỉ vẽ lại item thay đổi.
  - Rà soát toàn bộ các điểm truy xuất tài nguyên, bọc `try-catch` an toàn khi tải MediaItem.
- **Phụ thuộc:** Hoàn thành Phase 6.
- **Rủi ro & Biện pháp kiểm soát:**
  - *Rủi ro:* Người dùng bấm liên tục (Spam Click) vào nút Play/Pause hoặc Chuyển bài làm treo hệ thống.
  - *Biện pháp:* Bổ sung cơ chế Debounce hoặc khóa tạm thời tương tác nút trong 300ms sau mỗi lần bấm.
- **Tiêu chí nghiệm thu:**
  1. Không có màn hình trắng (Blank Screen) hay lỗi văng ứng dụng bất thường khi danh sách trống.
  2. Thử nghiệm ngắt tài nguyên âm thanh đột ngột không làm crash ứng dụng (TC15).

---

### PHASE 8: FINAL REVIEW, REGRESSION & RELEASE APK BUILD
- **Mục tiêu:**
  - Kiểm tra toàn bộ 15 Test Cases theo ma trận nghiệm thu (TC01 đến TC15).
  - Tối ưu Proguard / R8 (nếu bật) và kiểm tra dung lượng APK.
  - Thực hiện lệnh `./gradlew assembleDebug` và sinh ra tệp APK hoàn chỉnh có thể cài đặt trực tiếp trên thiết bị Android hoặc máy ảo Emulator.
  - Cập nhật tài liệu hướng dẫn sử dụng và báo cáo tổng kết.
- **Danh sách file tạo / sửa:**
  - Cập nhật `README.md` với ảnh chụp màn hình, hướng dẫn cài đặt và cấu trúc mã nguồn.
  - Tệp build đầu ra: `app/build/outputs/apk/debug/app-debug.apk`.
- **Phụ thuộc:** Hoàn thành Phase 7.
- **Tiêu chí nghiệm thu:**
  1. Toàn bộ 15 tiêu chí kiểm thử (TC01 - TC15) đều đạt kết quả mong muốn 100%.
  2. APK cài đặt và khởi chạy mượt mà trên thiết bị Android thật.
