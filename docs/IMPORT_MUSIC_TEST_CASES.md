# BỘ TIÊU CHÍ KIỂM THỬ — TÍNH NĂNG IMPORT MUSIC

> **Dự án:** Resonance Music (Independent Android Music Player)  
> **Bộ kiểm thử:** `TC-IMP01` đến `TC-IMP15`  
> **Mục tiêu:** Đảm bảo tính năng Import Music hoạt động chính xác, an toàn, bảo vệ người dùng và duy trì trải nghiệm nghe nhạc ngoại tuyến hoàn hảo.

---

## MA TRẬN 15 TÌNH HUỐNG KIỂM THỬ (TEST CASE MATRIX)

| Mã kiểm thử | Tên tình huống kiểm thử | Các bước thực hiện (Test Steps) | Kết quả mong đợi (Expected Result) |
| :---: | :--- | :--- | :--- |
| **TC-IMP01** | Nhập thành công URL tệp MP3 trực tiếp (Chế độ `BOTH`) | 1. Dán liên kết HTTPS trỏ trực tiếp đến tệp MP3.<br>2. Nhấn `Phân tích URL`.<br>3. Kiểm tra thông tin hiển thị tại Metadata Review.<br>4. Chọn lưu `BOTH` và bấm `Lưu vào thư viện`. | - Tải thành công 100%.<br>- Tệp xuất hiện tại `filesDir/music/` và `Music/MyMusic/`.<br>- Bảng `songs` ghi nhận `source_type = 'IMPORTED'`, `storage_mode = 'BOTH'`.<br>- Bài hát xuất hiện ngay trong Thư viện. |
| **TC-IMP02** | Từ chối URL YouTube & trang web HTML | 1. Dán link `https://www.youtube.com/watch?v=...` hoặc `https://youtu.be/...`.<br>2. Nhấn `Phân tích URL`. | - Hệ thống từ chối ngay lập tức tại bước `UrlValidator`.<br>- Hiển thị thông báo: *"Liên kết không phải là tệp âm thanh tải trực tiếp. Ứng dụng không hỗ trợ bóc tách video YouTube."*<br>- Không có request tải file nào được gửi đi. |
| **TC-IMP03** | Chặn tấn công SSRF & Địa chỉ IP nội bộ | 1. Dán link `http://127.0.0.1/song.mp3`, `http://localhost:8080/audio.mp3` hoặc IP LAN `192.168.1.100`.<br>2. Nhấn `Phân tích URL`. | - Báo lỗi URL không hợp lệ hoặc không an toàn.<br>- Chặn hoàn toàn các scheme không an toàn (HTTP thường, file://, ftp://). |
| **TC-IMP04** | Kiểm tra chuyển hướng (Redirect Validation) | 1. Dán liên kết rút gọn HTTPS hợp lệ chuyển hướng đến tệp MP3 thực tế.<br>2. Nhấn `Phân tích URL`. | - Hệ thống theo dõi tối đa 3 lần redirect.<br>- Kiểm tra lại Host đích đảm bảo an toàn.<br>- Lấy đúng tệp âm thanh nhị phân. |
| **TC-IMP05** | Từ chối MIME Type không phải âm thanh | 1. Dán liên kết trỏ tới tệp ảnh (`image/png`) hoặc tệp PDF giả danh đuôi `.mp3`.<br>2. Nhấn `Phân tích`. | - Nhận diện `Content-Type` không bắt đầu bằng `audio/`.<br>- Dừng tác vụ và thông báo: *"Nội dung tải về không phải là định dạng âm thanh được hỗ trợ."* |
| **TC-IMP06** | Từ chối tệp vượt quá giới hạn 100 MB | 1. Dán liên kết tệp âm thanh dung lượng > 100 MB.<br>2. Nhấn `Phân tích`. | - Nhận diện `Content-Length > 100MB`.<br>- Từ chối tải và hiển thị cảnh báo dung lượng vượt giới hạn. |
| **TC-IMP07** | Bóc tách đầy đủ Metadata ID3 | 1. Nhập tệp MP3 có sẵn thẻ ID3: Tiêu đề, Ca sĩ, Album, Ảnh bìa nhúng. | - Màn hình Metadata Review tự động điền đúng Tiêu đề, Ca sĩ, Album và ảnh bìa.<br>- Các trường hiển thị sắc nét, cho phép chỉnh sửa nếu muốn. |
| **TC-IMP08** | Xử lý tệp thiếu Metadata | 1. Nhập tệp MP3 không có thẻ ID3 tag.<br>2. Quan sát màn hình Review. | - Tiêu đề tự động lấy theo tên tệp (bỏ đuôi .mp3).<br>- Ca sĩ hiển thị "Nghệ sĩ chưa rõ".<br>- Cho phép người dùng tự gõ chỉnh sửa trước khi lưu. |
| **TC-IMP09** | Tùy chọn lưu chỉ App Storage | 1. Tại bước lưu, chọn chế độ `App Storage`.<br>2. Bấm Xác nhận. | - Tệp chỉ lưu vào `filesDir/music/`.<br>- `public_content_uri` trong database để `NULL`.<br>- Nhạc vẫn phát bình thường trong ứng dụng. |
| **TC-IMP10** | Tùy chọn lưu Public Music (`MediaStore`) | 1. Tại bước lưu, chọn chế độ `Public Music`.<br>2. Bấm Xác nhận. | - Tạo bản ghi trong `MediaStore.Audio.Media`.<br>- Sử dụng cờ `IS_PENDING = 1` trong lúc ghi và chuyển `IS_PENDING = 0` khi xong.<br>- Tệp xuất hiện trong ứng dụng quản lý tệp của điện thoại. |
| **TC-IMP11** | Hủy tải tệp giữa chừng (Cancel Action) | 1. Đang tải tệp 50 MB, tiến trình đạt 35%.<br>2. Nhấn nút `Hủy (Cancel)`. | - HTTP Call bị ngắt kết nối ngay lập tức.<br>- Tệp tạm thời `.part` trong thư mục cache bị xóa sạch.<br>- Trạng thái tác vụ chuyển thành `CANCELLED`. |
| **TC-IMP12** | Mất kết nối mạng khi đang tải | 1. Bật chế độ máy bay khi tiến trình đang tải ở mức 60%. | - Hệ thống bắt ngoại lệ mạng `IOException`.<br>- Xóa tệp tạm thời, chuyển trạng thái `FAILED`.<br>- Hiển thị thông báo mất kết nối kèm nút `[ Thử lại ]`. |
| **TC-IMP13** | Phát bài hát vừa Import bằng Media3 | 1. Sau khi import thành công, bấm vào bài hát trong Thư viện hoặc bấm `Phát ngay`. | - `SongUriResolver` phân giải URI tệp nội bộ hợp lệ.<br>- `ExoPlayer` phát âm thanh chuẩn xác, không bị giật lag.<br>- Mini Player và Full Player hiển thị đúng ảnh bìa và metadata vừa nhập. |
| **TC-IMP14** | Phát hiện và xử lý tệp công cộng bị xóa ngoài hệ thống | 1. Dùng ứng dụng Files ngoài xóa tệp trong `Music/MyMusic/`.<br>2. Mở Resonance Music và bấm phát bài hát đó. | - `SongUriResolver` phát hiện Content URI không còn truy cập được.<br>- Tự động chuyển hướng sang đọc bản sao trong `App Storage` (`app_file_path`).<br>- Bài hát tiếp tục phát mượt mà, không báo lỗi. |
| **TC-IMP15** | Bảo toàn cơ sở dữ liệu khi khởi động lại app | 1. Tắt ứng dụng hoàn toàn (vuốt tắt khỏi đa nhiệm).<br>2. Mở lại ứng dụng. | - Toàn bộ các bài hát đã Import vẫn hiển thị đầy đủ trong Thư viện.<br>- 6 bài hát mẫu có sẵn không bị mất hay bị ghi đè. |
