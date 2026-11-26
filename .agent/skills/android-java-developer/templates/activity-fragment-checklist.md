# Activity & Component Implementation Checklist

Bảng kiểm tra (Checklist) trước và sau khi hoàn thiện một Activity hoặc Fragment để đảm bảo mã nguồn đạt chuẩn chất lượng cao nhất.

---

## 1. Trước Khi Viết Code (Before Implementation)
- [ ] Xác định rõ luồng điều hướng (Intent, tham số đầu vào, kết quả trả về).
- [ ] Kiểm tra View IDs và layout XML tương ứng.
- [ ] Xác định các Model và phương thức DatabaseHelper cần gọi.
- [ ] Đăng ký Activity trong `AndroidManifest.xml` (chú ý cờ `android:exported`).

## 2. Chuẩn Hóa Mã Nguồn Java
- [ ] **Không thực thi I/O trên Main Thread** (truy vấn DB lớn, đọc file dung lượng cao).
- [ ] **Không giữ static tham chiếu tới Activity/View** để tránh rò rỉ bộ nhớ (Memory Leak).
- [ ] Giải phóng tài nguyên đầy đủ tại `onDestroy()` (`mediaPlayer.release()`, `db.close()`, `handler.removeCallbacks()`).
- [ ] Validate dữ liệu đầu vào (`isEmpty()`, kiểm tra null).
- [ ] Bọc try-catch đối với các tác vụ đa phương tiện (`IllegalStateException`).
- [ ] Xử lý chống nhấn liên tục (Double click / Spam tap).

## 3. Giao Diện & XML Layout
- [ ] Tái sử dụng Theme, Colors, Dimens đã định nghĩa trong dự án.
- [ ] Đặt tên View ID chuẩn tiền tố (`tv`, `btn`, `edt`, `iv`, `lv`).
- [ ] Kích thước vùng bấm tối thiểu đạt `48dp x 48dp`.
- [ ] Có ảnh fallback dự phòng nếu không tìm thấy resource ID động.

## 4. Kiểm Thử & Nghiệm Thu
- [ ] Chạy `./gradlew assembleDebug` đạt 0 lỗi biên dịch.
- [ ] Chạy thử nghiệm trên Emulator/thiết bị thực tế.
- [ ] Thử nghiệm xoay màn hình hoặc ẩn ứng dụng xem có bị crash không.
