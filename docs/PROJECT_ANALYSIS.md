# PROJECT ANALYSIS — BÁO CÁO ĐÁNH GIÁ HIỆN TRẠNG DỰ ÁN

> **Dự án:** Resonance Music (Independent Android Music Player)  
> **Package:** `com.example.android_finalexam`  
> **Mục tiêu:** Chuyển đổi ứng dụng nghe nhạc truyền thống sang kiến trúc MVVM + Repository, phát nhạc nền bằng AndroidX Media3 ExoPlayer, quản lý dữ liệu với SQLite đa bảng và giao diện Dark Theme hiện đại.

---

## 1. TỔNG QUAN HIỆN TRẠNG DỰ ÁN (PROJECT AUDIT)

### 1.1. Môi trường Build & Toolchain
- **Gradle Version:** Gradle `8.13` (Wrapper: `gradle-8.13-bin.zip`).
- **Android Gradle Plugin (AGP):** `8.13.2` (khai báo qua `libs.plugins.androidApplication`).
- **Java Virtual Machine (JVM):** Oracle JDK `17.0.12` (`org.gradle.java.home=C:/Program Files/Java/jdk-17`).
- **Source & Target Compatibility:** `JavaVersion.VERSION_1_8` (Java 8 bytecode, desugaring hỗ trợ tốt trên Android).
- **SDK Targets:**
  - `compileSdk`: `34` (Android 14)
  - `targetSdk`: `34` (Android 14)
  - `minSdk`: `31` (Android 12 — đảm bảo hỗ trợ đầy đủ các API hiện đại, không cần lo lắng về các giới hạn Android cũ).
- **Trạng thái Build:** Chạy lệnh `./gradlew assembleDebug` thành công 100% trong 5 giây (33 tasks, không lỗi biên dịch).

### 1.2. Phân tích Quản lý Dependencies (`libs.versions.toml` & `app/build.gradle.kts`)
Hiện tại, dự án sử dụng **Gradle Version Catalog** tại `gradle/libs.versions.toml`:
```toml
[versions]
agp = "8.13.2"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
appcompat = "1.7.0"
material = "1.12.0"
activity = "1.8.0"
constraintlayout = "2.1.4"
```
**Đánh giá thư viện còn thiếu cho mục tiêu sản phẩm:**
1. **Âm thanh & Phát nền (Audio Playback):** Hoàn toàn chưa có `androidx.media3` (`media3-exoplayer`, `media3-session`, `media3-ui`). Dự án đang dùng `android.media.MediaPlayer` cổ điển của hệ điều hành.
2. **Kiến trúc MVVM:** Chưa có `androidx.lifecycle:lifecycle-viewmodel` và `androidx.lifecycle:lifecycle-livedata`.
3. **Hiển thị danh sách:** Chưa có `androidx.recyclerview:recyclerview` (đang dùng `android.widget.ListView` cũ).
4. **Tải và cache ảnh:** Chưa tích hợp thư viện tối ưu như `Glide` (đang gọi `Resources.getIdentifier()` trực tiếp trên Main Thread).

### 1.3. Phân tích AndroidManifest (`app/src/main/AndroidManifest.xml`)
- **Namespace & Package:**
  - Manifest hiện tại có thuộc tính `package="com.example.android_finalexam"` tại thẻ `<manifest>`. AGP 8+ hiển thị cảnh báo khuyến nghị bỏ thuộc tính này vì `namespace` đã được xác định trong `app/build.gradle.kts`.
- **Quyền hạn (Permissions) hiện có:**
  - `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`. Đây là các quyền thừa đối với một ứng dụng 100% offline, tuy nhiên không gây lỗi runtime.
- **Quyền hạn BẮT BUỘC còn thiếu cho Media3 & Background Service:**
  - `android.permission.FOREGROUND_SERVICE`
  - `android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK` (bắt buộc từ Android 14 / API 34 khi chạy Service phát âm thanh).
  - `android.permission.POST_NOTIFICATIONS` (bắt buộc từ Android 13 / API 33 để hiển thị thanh điều khiển trên thanh thông báo hệ thống).
- **Khai báo Components:**
  - Hiện có 5 Activities: `ListSong` (Launcher), `MainActivity`, `FavoritesSong`, `AddSong`, `EditSong`.
  - Chưa khai báo `<service>` cho `PlaybackService` với action `androidx.media3.session.MediaSessionService`.

---

## 2. ĐÁNH GIÁ MÃ NGUỒN HIỆN HỮU (EXISTING CODE AUDIT)

### 2.1. Lớp Trình phát nhạc (`MainActivity.java`)
- **Cơ chế:** Khởi tạo trực tiếp một đối tượng `android.media.MediaPlayer mediaPlayer` ngay bên trong `MainActivity`.
- **Hạn chế nghiêm trọng:**
  1. Khi người dùng bấm nút Back (`finish()`) hoặc chuyển sang Activity khác, `mediaPlayer.stop()` và `mediaPlayer.release()` được gọi, bài hát dừng lập tức. **Không có khả năng phát nhạc nền**.
  2. Không có `MediaSession` hay `Notification` để điều khiển từ màn hình khóa hay tai nghe Bluetooth.
  3. Quản lý trạng thái bằng biến cục bộ, dễ phát sinh lỗi `IllegalStateException` khi người dùng bấm liên tục.
  4. Sử dụng `Handler` cập nhật `SeekBar` mỗi 500ms trực tiếp trên Activity.

### 2.2. Lớp Dữ liệu & SQLite (`DatabaseHelper.java`)
- **Tên cơ sở dữ liệu:** `songs_database`, phiên bản `1`.
- **Cấu trúc bảng hiện tại:**
  - Bảng `songs`: `id`, `title`, `artist`, `image_resource`, `audio_resource`.
  - Bảng `favorites`: Sao chép y nguyên cấu trúc của `songs`.
- **Hạn chế nghiêm trọng:**
  1. **Phi quan hệ & Trùng lặp dữ liệu:** Bảng `favorites` sao chép lại toàn bộ thông tin bài hát (`title`, `artist`, `image_resource`, `audio_resource`) thay vì chỉ lưu `song_id` tham chiếu khóa ngoại đến bảng `songs`. Khi một bài hát được sửa thông tin qua `EditSong`, danh sách `favorites` không được cập nhật tương ứng.
  2. **Thiếu các thực thể học thuật & tổ chức nhạc:** Chưa có bảng `artists`, `albums`, `playlists`, `playlist_songs`, `listening_history`.
  3. **Thao tác đồng bộ trên Main Thread:** Toàn bộ lệnh `db.insert()`, `db.query()`, `db.update()` và `db.delete()` đang được gọi trực tiếp từ Main UI Thread bên trong các Activity, tiềm ẩn nguy cơ giật lag (frame drop) và lỗi ANR (Application Not Responding).

### 2.3. Lớp Giao diện & Hiển thị (`ListSong.java`, `SongAdapter.java`, v.v.)
- **ListView thay vì RecyclerView:** Sử dụng `android.widget.ListView` và `ArrayAdapter<Song>`. Cơ chế này tải lại View kém mượt mà, không hỗ trợ tối ưu animation và phân loại ViewHolder chuyên nghiệp.
- **Tra cứu tài nguyên động chậm (Reflection):** `SongAdapter` gọi `resources.getIdentifier(imageName, "drawable", ...)` và `resources.getIdentifier(audioName, "raw", ...)` mỗi lần hiển thị một item. Đây là thao tác tra cứu chuỗi tốn CPU trong phương thức `getView()`.
- **Chưa có cấu trúc Single Activity + Fragments:** Mỗi màn hình là một Activity riêng biệt (`ListSong`, `FavoritesSong`, `AddSong`, `EditSong`, `MainActivity`), khiến việc chia sẻ phiên phát nhạc (Playback Session) giữa các màn hình và duy trì Mini Player ở đáy màn hình trở nên bất khả thi nếu không có `MediaSessionService`.

---

## 3. TÀI NGUYÊN NỘI BỘ SẴN CÓ (EXISTING ASSETS)

### 3.1. Tệp âm thanh (`app/src/main/res/raw/`)
Có 6 bài hát thực tế với dung lượng từ 4.4MB đến 11.2MB, định dạng MP3 chất lượng cao:
- `viva_lavi_da__coldplay.mp3` (8.07 MB)
- `muoitam_kis.mp3` (8.68 MB)
- `dead_feelings__moshi.mp3` (6.87 MB)
- `wave_alpha__7uppercuts_cam.mp3` (6.84 MB)
- `dunglamtraitimanhdau.mp3` (11.20 MB)
- `ineedyourlove_magnus_carlsson.mp3` (4.47 MB)

### 3.2. Ảnh bìa & Biểu tượng (`app/src/main/res/drawable/`)
- Đã có ảnh bìa tương ứng: `viva_lavi_da.png`, `muoitam.png`, `dead_feelings.png`, `wave_alpha.png`, `anh1.png`, `playinglogo.jpg`.
- Các vector drawable điều khiển: `ic_playback.png`, `ic_playnext.png`, `ic_stop.png`, `ic_volume.png`.
- Các shape viền bo tròn: `button_circle.xml`, `button_primary.xml`, `button_secondary.xml`, `card_item_bg.xml`, `input_field_bg.xml`, `search_bg.xml`.

### 3.3. Màu sắc & Theme (`res/values/colors.xml`, `res/values/themes.xml`)
- Đã thiết lập bảng màu Dark Theme phong cách Spotify:
  - `spotify_black`: `#121212` (Nền chính)
  - `spotify_dark_card`: `#181818` (Nền card)
  - `spotify_card_hover`: `#282828` (Nền hover/active)
  - `spotify_green`: `#1DB954` (Màu thương hiệu / Accent)
  - `spotify_text_primary`: `#FFFFFF`
  - `spotify_text_secondary`: `#B3B3B3`
- Theme chính: `Theme.Material3.DayNight.NoActionBar` với `windowBackground`, `statusBarColor`, `navigationBarColor` đều trỏ về `spotify_black`.

---

## 4. MA TRẬN SO SÁNH: HIỆN TRẠNG VS KIẾN TRÚC MỤC TIÊU

| Tiêu chí | Trạng thái hiện tại | Kiến trúc mục tiêu (Target) |
| :--- | :--- | :--- |
| **Kiến trúc mã nguồn** | Monolithic Activities (Activity xử lý tất cả) | MVVM + Repository Pattern |
| **Điều hướng ứng dụng** | Nhiều Activities chuyển qua Intent (`startActivity`) | Single Activity (`MainActivity`) + Jetpack Navigation / FragmentManager |
| **Trình phát nhạc** | `android.media.MediaPlayer` cục bộ trong Activity | AndroidX Media3 ExoPlayer trong `MediaSessionService` |
| **Phát nhạc nền** | ❌ Không có (thoát màn hình là dừng phát) |  Hoạt động khi khóa máy, ẩn ứng dụng, hỗ trợ Notification MediaStyle |
| **Đồng bộ trình phát** | Chỉ có 1 màn hình `MainActivity` | Đồng bộ 100% giữa Mini Player (gắn đáy), Full Player và Notification |
| **Cơ sở dữ liệu** | 2 bảng phẳng (`songs`, `favorites`), sao chép dữ liệu | 7 bảng quan hệ (`artists`, `albums`, `songs`, `playlists`, `playlist_songs`, `liked_songs`, `listening_history`) |
| **Thực thi Database** | Main Thread (Blocking) | Background Worker Thread / ThreadPool Executor |
| **Hiển thị danh sách** | `ListView` + `ArrayAdapter` | `RecyclerView` + `ListAdapter` + `DiffUtil` |
| **Quản lý Hàng đợi** | Không có (chỉ có list đơn giản trong Intent) | `QueueManager` chuyên dụng hỗ trợ Next, Previous, Shuffle giữ nguyên bài hiện tại, Repeat (Off/All/One) |

---

## 5. ĐÁNH GIÁ RỦI RO & BIỆN PHÁP KIỂM SOÁT

### Rủi ro 1: Rò rỉ bộ nhớ (Memory Leak) và xung đột vòng đời MediaSession
- *Mô tả:* Khi UI kết nối với Background Service thông qua `MediaController`, nếu không giải phóng `MediaController.releaseFuture()` hoặc đăng ký Listener mà không hủy khi Fragment bị Destroy, ứng dụng sẽ bị rò rỉ bộ nhớ hoặc gọi callback trên View đã bị hủy.
- *Giải pháp:* `PlaybackController` phải bám sát vòng đời của Host Activity hoặc dùng `DefaultLifecycleObserver` để tự động unbind/release khi Activity `onDestroy()`.

### Rủi ro 2: Bị hệ điều hành Android 13/14 thu hồi Service khi chạy nền (Background Killing)
- *Mô tả:* Trên Android 14 (API 34), nếu Foreground Service không khai báo đúng thuộc tính `foregroundServiceType="mediaPlayback"` và không kích hoạt thông báo hợp lệ trong thời gian quy định, hệ điều hành sẽ ném ra ngoại lệ `ForegroundServiceStartNotAllowedException`.
- *Giải pháp:* Khai báo đầy đủ quyền trong Manifest, cấu hình `MediaSessionService` theo chuẩn Media3 với Notification Provider tự động và yêu cầu quyền `POST_NOTIFICATIONS` tại runtime trên Android 13+.

### Rủi ro 3: Sai lệch thứ tự phát khi bật / tắt Shuffle (Xáo trộn hàng đợi)
- *Mô tả:* Lỗi thường gặp của các ứng dụng nghe nhạc là khi bật Shuffle, bài hát đang phát bị gián đoạn hoặc bị mất vị trí gốc, và khi tắt Shuffle không thể khôi phục lại thứ tự ban đầu của Playlist.
- *Giải pháp:* Để Media3 ExoPlayer quản lý timeline phát ngẫu nhiên (`setShuffleModeEnabled(true)`), đồng thời `QueueManager` lưu trữ bản sao danh sách gốc (original queue) và danh sách hiển thị, đảm bảo bài hát đang phát luôn được giữ nguyên vị trí active.

### Rủi ro 4: Di chuyển dữ liệu SQLite cũ (Migration Safety)
- *Mô tả:* Ứng dụng hiện tại đã có cơ sở dữ liệu `songs_database` phiên bản 1 trên các máy thử nghiệm.
- *Giải pháp:* Nâng `DATABASE_VERSION = 2`, xây dựng script `onUpgrade` chuyển đổi an toàn các bản ghi từ bảng `favorites` cũ sang `liked_songs` mới mà không làm mất dữ liệu người dùng.
