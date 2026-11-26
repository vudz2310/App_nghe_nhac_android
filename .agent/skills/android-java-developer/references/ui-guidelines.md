# Quy Chuẩn Giao Diện UI/UX Trong Android

Tài liệu hướng dẫn thiết kế Layout XML, xử lý tương tác người dùng, cử chỉ và phản hồi trên màn hình.

---

## 1. Quy Chuẩn Đặt Tên View ID Trong XML

Đặt tên ID phải có tiền tố mô tả loại View để dễ tìm kiếm và đọc code:

| Loại View | Tiền tố | Ví dụ |
| :--- | :--- | :--- |
| `TextView` | `tv` | `tvSongTitle`, `tvArtistName`, `tvDuration` |
| `Button` | `btn` | `btnPlay`, `btnPause`, `btnFavorites` |
| `EditText` | `edt` | `edtSearch`, `edtSongTitle` |
| `ImageView` | `iv` | `ivCoverArt`, `ivPlayIcon` |
| `ListView` / `RecyclerView` | `lv` / `rv` | `lvSongs`, `rvPlaylist` |
| `SeekBar` | `sb` hoặc `seekBar` | `seekBarProgress`, `sbVolume` |

---

## 2. Tiêu Chuẩn Kích Thước & Khả Năng Tiếp Cận (Accessibility)

1. **Vùng chạm tối thiểu (Touch Target Size):**
   - Mọi nút bấm, icon tương tác phải có kích thước tối thiểu `48dp x 48dp` (theo Material Design) để người dùng bấm chính xác.
2. **Fallback cho hình ảnh & âm thanh:**
   - Khi load ảnh động từ tên resource (dùng `getIdentifier`), nếu không tìm thấy resource (trả về `0`), phải luôn có ảnh mặc định thay thế (`R.drawable.playinglogo`) để tránh màn hình bị trống hoặc crash.

---

## 3. Xử Lý Cử Chỉ Người Dùng (Gesture & Touch)

Trong danh sách bài hát hoặc đối tượng:
- **Nhấp đơn (Single Click):** Mở trình phát nhạc hoặc mở chi tiết.
- **Nhấp đúp (Double Tap):** Dùng biến đếm thời gian hoặc `GestureDetector` để mở nhanh tính năng chỉnh sửa (`Edit`).
- **Nhấn giữ (Long Click):** Hiển thị hộp thoại xác nhận hoặc menu hành động (Action Dialog).

```java
listView.setOnItemClickListener((parent, view, position, id) -> {
    long currentTime = System.currentTimeMillis();
    if (currentTime - lastClickTime < 500 && lastPosition == position) {
        // Double tap action
    } else {
        // Single tap action (delay kiểm tra)
    }
});
```
