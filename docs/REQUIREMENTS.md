# REQUIREMENTS SPECIFICATION — ĐẶC TẢ YÊU CẦU PHẦN MỀM

> **Dự án:** Resonance Music — Ứng dụng nghe nhạc Android độc lập (Offline)  
> **Phiên bản tài liệu:** 1.0  
> **Ngày phê duyệt:** 26/11/2026  
> **Ngôn ngữ triển khai:** Java 8+  
> **Hệ điều hành:** Android 12 (API 31) đến Android 14+ (API 34)

---

## 1. MỤC TIÊU VÀ PHẠM VI SẢN PHẨM

### 1.1. Tuyên ngôn sản phẩm
Resonance Music là ứng dụng nghe nhạc di động ngoại tuyến hoàn toàn, mang đến trải nghiệm giao diện cao cấp, mượt mà và tối giản lấy cảm hứng từ phong cách streaming hiện đại (Dark Theme, Content-First). Ứng dụng hoạt động 100% không cần mạng Internet, không phụ thuộc vào bất kỳ dịch vụ hay API của bên thứ ba nào (hoàn toàn không dùng Spotify API).

### 1.2. Đối tượng người dùng & Kịch bản sử dụng
- Người dùng Android muốn thưởng thức âm nhạc chất lượng cao lưu sẵn trong máy.
- Người dùng tập luyện thể thao, du lịch, đi máy bay hoặc ở những nơi không có sóng điện thoại/Wi-Fi.
- Sinh viên, kỹ sư cần một ứng dụng nghe nhạc gọn gàng, khởi động tức thì, không quảng cáo, không yêu cầu đăng nhập.

### 1.3. Bảng phân định phạm vi (Scope Matrix)
| Tính năng | Phiên bản 1 (In Scope) | Tương lai (Out of Scope) |
| :--- | :---: | :---: |
| Phát nhạc ngoại tuyến từ tài nguyên đóng gói / lưu trữ máy |  |  |
| Phát nhạc nền khi khóa máy hoặc ẩn ứng dụng |  |  |
| Điều khiển phát qua Notification & Màn hình khóa |  |  |
| Hàng đợi phát (Queue), Shuffle, Repeat |  |  |
| Quản lý Playlist cá nhân (CRUD) |  |  |
| Danh sách bài hát yêu thích (Liked Songs) |  |  |
| Tìm kiếm tức thì theo Bài hát, Nghệ sĩ, Album |  |  |
| Khám phá trang Nghệ sĩ & Chi tiết Album |  |  |
| Tài khoản trực tuyến & Đăng nhập | ❌ |  |
| Đồng bộ dữ liệu nhiều thiết bị qua Cloud | ❌ |  |
| Streaming từ máy chủ từ xa | ❌ |  |
| Đề xuất âm nhạc bằng thuật toán AI | ❌ |  |
| Tải nhạc số từ YouTube / Spotify / SoundCloud | ❌ |  |

---

## 2. ĐẶC TẢ CHI TIẾT 8 PHÂN HỆ NGHIỆP VỤ (FUNCTIONAL SPECIFICATIONS)

```text
┌────────────────────────────────────────────────────────┐
│                   RESONANCE MUSIC APP                  │
├────────────────────────────────────────────────────────┤
│ 1. Home             -> /home                           │
│ 2. Search           -> /search                         │
│ 3. Library          -> /library                        │
│ 4. Player           -> Mini Player & Full Player       │
│ 5. Queue            -> Queue Bottom Sheet              │
│ 6. Playlist         -> /playlist/:id                   │
│ 7. Artist & Album   -> /artist/:id & /album/:id        │
│ 8. Settings/Profile -> /profile                        │
└────────────────────────────────────────────────────────┘
```

### 2.1. Phân hệ 1: Home (Trang chủ & Khám phá)
- **Header:** Lời chào theo thời gian trong ngày ("Chào buổi sáng", "Chào buổi chiều", "Chào buổi tối") kèm biểu tượng truy cập Profile/Settings.
- **Section "Nghe gần đây" (Recently Played):** Hiển thị danh sách ngang (Horizontal Carousel) các bài hát/album đã nghe gần nhất lấy từ bảng `listening_history`.
- **Section "Dành cho bạn" (Made for You):** Các playlist mẫu mặc định được tạo sẵn khi cài đặt (ví dụ: "Nhạc tuyển chọn", "Giai điệu năng lượng").
- **Section "Album nổi bật" (Popular Albums):** Danh sách các album nhạc có trong cơ sở dữ liệu.
- **Section "Nghệ sĩ quen thuộc" (Top Artists):** Danh sách avatar tròn của các nghệ sĩ nổi bật.
- **Hành vi khi click bài hát:** Gửi yêu cầu phát ngay đến `PlaybackController`, cập nhật Queue và hiển thị Mini Player.

### 2.2. Phân hệ 2: Search (Tìm kiếm nội dung)
- **Ô tìm kiếm tức thì:** Hỗ trợ nhập từ khóa với cơ chế lọc thời gian thực (Debounce 300ms) để không gây nghẽn UI.
- **Bộ lọc loại kết quả (Filter Chips):** `Tất cả` | `Bài hát` | `Nghệ sĩ` | `Album` | `Playlist`.
- **Thuật toán tìm kiếm cục bộ:**
  - Truy vấn SQLite chuẩn hóa:
    ```sql
    SELECT s.*, a.name AS artist_name, al.title AS album_title 
    FROM songs s 
    LEFT JOIN artists a ON s.artist_id = a.id 
    LEFT JOIN albums al ON s.album_id = al.id 
    WHERE LOWER(s.title) LIKE LOWER(?) OR LOWER(a.name) LIKE LOWER(?)
    ORDER BY s.title ASC;
    ```
- **Lịch sử tìm kiếm:** Hiển thị các từ khóa đã tìm gần đây, cho phép xóa từng từ khóa hoặc xóa tất cả.
- **Trạng thái kết quả:** Hiển thị rõ ràng khi đang tìm, khi có kết quả và khi không tìm thấy kết quả phù hợp.

### 2.3. Phân hệ 3: Library (Thư viện cá nhân)
- **Hệ thống Tab danh mục:**
  1. **Tab Playlists:** Danh sách tất cả playlist do người dùng tạo và playlist mẫu.
  2. **Tab Liked Songs (Bài hát đã thích):** Lối tắt cố định dẫn vào danh sách các bài hát được gắn tim.
  3. **Tab Artists (Nghệ sĩ):** Danh sách các nghệ sĩ có bài hát trong thư viện.
  4. **Tab Albums (Album):** Danh sách album kèm ảnh bìa và số lượng bài hát.
- **Hành động tạo Playlist mới:**
  - Nút bấm `[ + Tạo playlist ]` mở Dialog nhập tên và mô tả.
  - Tự động gán ảnh bìa mặc định và lưu vào bảng `playlists`.

### 2.4. Phân hệ 4: Player (Trình phát nhạc Mini & Full)
Một phiên phát nhạc duy nhất phục vụ cả Mini Player và Full Player:

#### A. Mini Player (Thanh phát thu nhỏ gắn đáy màn hình)
- **Vị trí:** Cố định ngay phía trên thanh Bottom Navigation.
- **Thành phần:** Ảnh bìa nhỏ (bo góc 6dp), tên bài hát, nghệ sĩ, thanh tiến trình siêu mỏng ở cạnh dưới, nút `Play/Pause`, nút `Next`.
- **Tương tác:** Vuốt lên hoặc nhấp vào thanh Mini Player để mở Full Player (bằng Bottom Sheet hoặc Transition animation mượt mà).

#### B. Full Player (Trình phát toàn màn hình)
- **Thành phần:**
  - Header: Nút thu nhỏ (`Collapse / Down arrow`), Tiêu đề ngữ cảnh phát ("Đang phát từ Playlist X"), Nút menu tùy chọn (`More`).
  - Vùng trung tâm: Ảnh bìa lớn hình vuông (Artwork bo góc 16dp), tên bài hát (chữ đậm lớn), tên nghệ sĩ, nút `Like` (Trái tim: Đầy màu xanh khi thích, viền trắng khi chưa thích).
  - Thanh tiến trình: `SeekBar` mượt mà, thời gian hiện tại (`tvTimeCurrent`) và tổng thời lượng (`tvTimeDuration`).
  - Cụm nút điều khiển chính:
    - Nút `Shuffle` (Ngẫu nhiên): Tắt / Bật (đổi màu xanh khi bật).
    - Nút `Previous` (Bài trước): Nếu tiến trình > 3 giây, tua về đầu bài; nếu tiến trình <= 3 giây, chuyển về bài trước trong hàng đợi.
    - Nút `Play/Pause`: Nút tròn lớn nổi bật ở giữa (`button_circle`), đổi icon Play/Pause tức thì.
    - Nút `Next` (Bài tiếp theo): Chuyển sang bài tiếp theo trong hàng đợi.
    - Nút `Repeat` (Lặp lại): 3 trạng thái xoay vòng: `Repeat Off` -> `Repeat All` -> `Repeat One` (hiển thị icon lặp 1 bài).
  - Footer: Nút mở danh sách Hàng đợi (`Queue Button`).

### 2.5. Phân hệ 5: Queue (Quản lý hàng đợi phát)
- **Trạng thái hàng đợi:**
  - Phần đang phát: Hiển thị bài hát hiện tại có sóng âm hoặc icon nổi bật.
  - Phần tiếp theo: Danh sách các bài hát sắp được phát.
- **Thao tác hàng đợi:**
  - Xem trước bài hát kế tiếp.
  - Xóa bài hát khỏi hàng đợi bằng thao tác vuốt (Swipe to dismiss) hoặc nút bấm.
  - Kéo thả sắp xếp thứ tự phát (Drag & Drop Reorder).
  - Thêm bài hát bất kỳ vào cuối hàng đợi ("Thêm vào hàng đợi").

### 2.6. Phân hệ 6: Playlist Detail (Chi tiết Playlist)
- **Thông tin hiển thị:** Tên playlist, mô tả, ảnh bìa ghép hoặc mặc định, tổng số bài hát, tổng thời lượng.
- **Hành động chính:**
  - `Phát tất cả (Play All)`: Nạp toàn bộ bài hát vào Queue và phát từ bài đầu tiên.
  - `Trộn bài (Shuffle Play)`: Nạp danh sách và kích hoạt chế độ Shuffle ngay từ đầu.
  - `Sửa Playlist`: Đổi tên và mô tả.
  - `Xóa Playlist`: Hiển thị Dialog xác nhận. Quy tắc an toàn: Xóa playlist **chỉ xóa liên kết trong bảng `playlist_songs`**, tuyệt đối **không xóa bài hát gốc trong bảng `songs`**.
  - `Xóa bài khỏi Playlist`: Xóa bài đã chọn khỏi playlist đó.

### 2.7. Phân hệ 7: Artist & Album Detail
- **Artist Detail:** Ảnh đại diện nghệ sĩ, tiểu sử tóm tắt, danh sách bài hát phổ biến nhất, danh sách các album của nghệ sĩ.
- **Album Detail:** Ảnh bìa album, năm phát hành, tên nghệ sĩ, danh sách bài hát theo số thứ tự track.

### 2.8. Phân hệ 8: Profile & Settings
- Thông tin phiên bản ứng dụng (`v1.0.0 — Resonance Music`).
- Dung lượng bộ nhớ cache và dữ liệu bài hát offline.
- Tùy chọn phát nhạc: Tự động phát bài kế tiếp (Autoplay Next), chuẩn hóa âm lượng.

---

## 3. ĐẶC TẢ PHÁT NHẠC NỀN & ĐIỀU KHIỂN HỆ THỐNG

### 3.1. Dịch vụ phát nền (`MediaSessionService`)
1. **Khóa màn hình (Lockscreen):** Âm thanh tiếp tục phát liền mạch không ngắt quãng. Màn hình khóa hiển thị widget điều khiển Media của Android.
2. **Chuyển ứng dụng (Backgrounding):** Khi người dùng chuyển sang trình duyệt, mạng xã hội hoặc bấm nút Home, nhạc vẫn phát bình thường.
3. **Notification điều khiển hệ thống:**
   - Sử dụng chuẩn `androidx.media3.ui.PlayerNotificationManager` hoặc `MediaNotification.Provider`.
   - Hiển thị tên bài hát, nghệ sĩ, ảnh bìa (Large Icon) và các nút điều khiển: `Previous`, `Play/Pause`, `Next`.
   - Nhấp vào Notification sẽ mở lại màn hình `MainActivity`.
4. **Xử lý Audio Focus:**
   - Khi có cuộc gọi đến: Tạm dừng phát (`PAUSE`), tự động tiếp tục khi cuộc gọi kết thúc.
   - Khi ứng dụng khác phát âm thanh ngắn (chuông báo, định vị GPS): Giảm âm lượng tạm thời (Audio Ducking) rồi tăng lại.
   - Khi rút tai nghe có dây hoặc ngắt kết nối Bluetooth: Bắt sự kiện `AudioManager.ACTION_AUDIO_BECOMING_NOISY` và tự động `PAUSE` ngay lập tức để tránh phát to ra loa ngoài nơi công cộng.
5. **Tai nghe & Thiết bị Bluetooth:** Phản hồi chính xác các sự kiện phím cứng `KEYCODE_MEDIA_PLAY`, `KEYCODE_MEDIA_PAUSE`, `KEYCODE_MEDIA_NEXT`, `KEYCODE_MEDIA_PREVIOUS`.

---

## 4. MA TRẬN TIÊU CHÍ NGHIỆM THU (ACCEPTANCE TEST MATRIX)

| Mã | Tình huống kiểm thử | Các bước thực hiện | Kết quả mong muốn |
| :---: | :--- | :--- | :--- |
| **TC01** | Mở ứng dụng lần đầu tiên | Cài đặt APK mới, mở app | Ứng dụng tự động khởi tạo cơ sở dữ liệu SQLite và seed đủ 6 bài hát mẫu kèm nghệ sĩ, album và playlist mẫu. Không crash, mở thẳng Home. |
| **TC02** | Xem trang Home | Xem màn hình Home sau khi mở | Hiển thị đầy đủ các phần: Lời chào, Nghe gần đây, Dành cho bạn, Album nổi bật. Danh sách cuộn mượt mà. |
| **TC03** | Tìm kiếm bài hát | Vào tab Search, gõ "viva" hoặc "coldplay" | Kết quả lọc ra ngay bài hát "Viva La Vida" của Coldplay theo thời gian thực mà không cần nhấn nút Enter. |
| **TC04** | Tạo playlist mới | Vào Library -> Bấm Tạo Playlist -> Nhập "Nhạc Chill" -> Lưu | Playlist "Nhạc Chill" xuất hiện trong danh sách, dữ liệu được ghi vào SQLite. |
| **TC05** | Thêm bài vào playlist | Mở menu bài hát -> Chọn Thêm vào Playlist -> Chọn "Nhạc Chill" | Bài hát được lưu vào bảng `playlist_songs`, mở playlist thấy bài hát hiển thị đúng thứ tự. |
| **TC06** | Thích bài hát (Like) | Bấm nút trái tim trên Full Player hoặc trong danh sách | Trái tim chuyển màu xanh (`spotify_green`), bản ghi được lưu vào bảng `liked_songs`. Vào Tab Liked Songs thấy bài đó. |
| **TC07** | Phát một bài hát | Bấm vào bài "Mười Tám" trong danh sách | File MP3 mẫu phát ra âm thanh rõ ràng, Mini Player hiển thị ở đáy màn hình với tên bài và trạng thái Playing. |
| **TC08** | Chuyển bài (Next) | Bấm nút Next trên Mini Player hoặc Full Player | Bài kế tiếp trong hàng đợi lập tức phát, ảnh bìa, tiêu đề và thanh tiến trình cập nhật tương ứng. |
| **TC09** | Chế độ Repeat One | Bật chế độ lặp 1 bài -> Tua đến cuối bài hát | Khi bài hát kết thúc, nó tự động phát lại chính bài đó từ đầu thay vì chuyển sang bài khác. |
| **TC10** | Chế độ Shuffle | Bật Shuffle -> Kiểm tra bài kế tiếp | Hàng đợi được xáo trộn ngẫu nhiên, nhưng **bài hát đang phát vẫn tiếp tục bình thường** không bị gián đoạn. |
| **TC11** | Khóa màn hình khi đang phát | Đang phát nhạc -> Tắt màn hình điện thoại | Nhạc vẫn tiếp tục phát mượt mà, màn hình khóa hiển thị widget phát nhạc của Android. |
| **TC12** | Chuyển sang ứng dụng khác | Đang phát nhạc -> Bấm Home mở ứng dụng khác | Nhạc không bị dừng, thanh thông báo hệ thống hiển thị Notification điều khiển. |
| **TC13** | Điều khiển từ Notification | Bấm Play/Pause/Next trực tiếp trên Notification | Media3 Service tiếp nhận lệnh và thay đổi trạng thái phát ngay lập tức, giao diện trong app đồng bộ theo. |
| **TC14** | Đóng và mở lại ứng dụng | Vuốt tắt ứng dụng khỏi danh sách đa nhiệm -> Mở lại | Toàn bộ playlist cá nhân, danh sách bài hát yêu thích và lịch sử nghe nhạc vẫn được giữ nguyên vẹn từ SQLite. |
| **TC15** | Xử lý tài nguyên lỗi | Xóa file hoặc giả lập tài nguyên âm thanh hỏng | Ứng dụng hiển thị thông báo lỗi nhẹ nhàng (Toast/Snackbar), tự động chuyển bài hoặc dừng an toàn, không bị crash `NullPointerException` hay `IllegalStateException`. |
