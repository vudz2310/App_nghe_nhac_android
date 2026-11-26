---
name: android-music-player
description: Technical guidance, best practices, and architecture rules for developing Android music player applications. Covers MediaPlayer lifecycle management, smooth UI progress updates, gesture handling, SQLite offline storage, and background audio streaming.
license: MIT
metadata:
  author: Android Developer
  last-updated: '2026-11-26'
  keywords:
  - Android
  - Music Player
  - MediaPlayer
  - SeekBar
  - Audio Lifecycle
  - SQLite
  - Gesture
---

# Android Music Player Development Skill

Hướng dẫn quy chuẩn kỹ thuật và các mẫu thiết kế chuẩn (Best Practices) để xây dựng ứng dụng nghe nhạc ổn định, mượt mà trên hệ điều hành Android.

---

## 1. Quản lý Vòng đời MediaPlayer (Lifecycle Management)

`MediaPlayer` trong Android hoạt động theo một máy trạng thái (State Machine). Việc gọi sai hàm ở trạng thái không hợp lệ sẽ dẫn đến lỗi nghiêm trọng `IllegalStateException`.

### Quy tắc bất di bất dịch:
1. **Khởi tạo và Chuẩn bị:**
   - Khi phát tài nguyên nội bộ (`raw` / `asset`), sử dụng `MediaPlayer.create(context, resId)` (hàm này tự động gọi `prepare()`).
   - Luôn gán listener `setOnPreparedListener` và `setOnCompletionListener`.

2. **Dọn dẹp tài nguyên (Resource Cleanup):**
   - Khi người dùng thoát màn hình (`onDestroy()`) hoặc chuyển bài hát, **BẮT BUỘC** dừng và giải phóng player cũ:
     ```java
     if (mediaPlayer != null) {
         if (mediaPlayer.isPlaying()) {
             mediaPlayer.stop();
         }
         mediaPlayer.release();
         mediaPlayer = null;
     }
     ```

3. **Xử lý bất đồng bộ & Try-Catch an toàn:**
   - Mọi thao tác truy xuất `mediaPlayer.isPlaying()` hoặc `mediaPlayer.getCurrentPosition()` nên được bọc trong `try-catch (IllegalStateException e)` để tránh crash đột ngột khi player đang giải phóng.

---

## 2. Đồng bộ Thanh Tiến Trình (SeekBar Synchronization)

Tránh tạo Thread thủ công không được kiểm soát vòng đời (dễ gây rò rỉ bộ nhớ - Memory Leak). Hãy sử dụng `Handler` chạy trên Main Looper kết hợp `Runnable`:

```java
private final Handler seekBarHandler = new Handler(Looper.getMainLooper());
private final Runnable seekBarRunnable = new Runnable() {
    @Override
    public void run() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    int current = mediaPlayer.getCurrentPosition();
                    tvTimeCurrent.setText(formatTime(current));
                    seekBarTime.setProgress(current);
                }
            } catch (IllegalStateException ignored) {}
            seekBarHandler.postDelayed(this, 500); // Cập nhật mượt mỗi 500ms
        }
    }
};

// Bắt đầu cập nhật:
private void startTracking() {
    seekBarHandler.removeCallbacks(seekBarRunnable);
    seekBarHandler.post(seekBarRunnable);
}

// Hủy bỏ khi Destroy:
@Override
protected void onDestroy() {
    super.onDestroy();
    seekBarHandler.removeCallbacks(seekBarRunnable);
}
```

---

## 3. Tương tác Cử chỉ (UX Gestures & Interaction)

Để mang lại trải nghiệm tiện dụng cho người dùng trong danh sách bài hát:
- **Single Tap (1 chạm):** Mở trình phát nhạc (Now Playing) với bài hát được chọn.
- **Double Tap (2 chạm):** Mở nhanh trình biên tập thông tin bài hát (Edit Song).
- **Long Press (Nhấn giữ):** Hiển thị `AlertDialog` chứa các tùy chọn ngữ cảnh: *Phát nhạc*, *Chỉnh sửa*, *Xóa bài hát* hoặc *Thêm vào yêu thích*.
- **Search Filtering:** Hỗ trợ lọc danh sách theo thời gian thực (Real-time Filter) kết hợp cả tên bài hát và nghệ sĩ (`passesFilter(Song song, String keyword)`).

---

## 4. Quản lý Dữ liệu Ngoại Tuyến (SQLite / Room)

1. **Auto-seeding dữ liệu mẫu:**
   - Khi cơ sở dữ liệu vừa tạo hoặc còn trống, luôn có hàm tự động nạp danh sách nhạc mẫu (`seedDefaultSongsIfEmpty()`) để người dùng có thể trải nghiệm ngay lập tức.
2. **Khóa chính & Quan hệ:**
   - Xóa và cập nhật bài hát nên dựa trên `ID` (`COLUMN_ID`), tránh dựa trên tiêu đề `title` vì tiêu đề có thể bị trùng lặp.
3. **Danh sách yêu thích (Favorites):**
   - Kiểm tra trùng lặp (`cursor.getCount() > 0`) trước khi thêm vào danh sách yêu thích để tránh dư thừa dữ liệu.
