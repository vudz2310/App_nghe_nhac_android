# Quy Chuẩn Lập Trình Java Cho Android

Bộ quy tắc và thói quen lập trình (Best Practices) giúp mã nguồn Java chạy ổn định, tránh các lỗi phổ biến trong môi trường Android.

---

## 1. Phòng Tránh NullPointerException (NPE)

1. **Kiểm tra Null trước khi truy xuất:**
   - Khi nhận dữ liệu từ `Intent.getExtras()` hoặc `Cursor`, luôn kiểm tra `null`.
2. **So sánh chuỗi an toàn:**
   - Đặt hằng số hoặc chuỗi đã biết trước lên đầu biểu thức so sánh:
     ```java
     // NÊN DÙNG
     if ("active".equals(status)) { ... }
     
     // TRÁNH (nếu status null sẽ gây NPE)
     if (status.equals("active")) { ... }
     ```

---

## 2. Quản Lý Đa Luồng & Tránh Memory Leak

### Tuyệt đối không dùng `new Thread()` vô điều kiện:
Khi tạo một `Thread` chạy ngầm trong Activity mà không được dừng lại khi Activity bị hủy (`onDestroy`), Thread đó sẽ giữ tham chiếu tới Activity và gây ra hiện tượng rò rỉ bộ nhớ nghiêm trọng (Memory Leak).

### Giải pháp chuẩn với `Handler` & `Looper`:
Sử dụng `Handler` kết hợp với `Runnable` và luôn hủy callbacks trong `onDestroy()`:
```java
private final Handler handler = new Handler(Looper.getMainLooper());
private Runnable runnableTask;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    runnableTask = new Runnable() {
        @Override
        public void run() {
            // Cập nhật giao diện trên Main Thread
            handler.postDelayed(this, 1000);
        }
    };
    handler.post(runnableTask);
}

@Override
protected void onDestroy() {
    super.onDestroy();
    if (handler != null && runnableTask != null) {
        handler.removeCallbacks(runnableTask);
    }
}
```

---

## 3. Quản Lý Vòng Đời Của MediaPlayer

- **State Machine:** `MediaPlayer` có các trạng thái: `Idle`, `Initialized`, `Preparing`, `Prepared`, `Started`, `Paused`, `Stopped`, `PlaybackCompleted`, `End`, `Error`.
- Không gọi `start()` khi chưa ở trạng thái `Prepared`.
- Bọc các lệnh lấy thông tin trong khối `try-catch (IllegalStateException e)`.
- Khi dừng hoặc chuyển bài hát:
  ```java
  if (mediaPlayer != null) {
      if (mediaPlayer.isPlaying()) {
          mediaPlayer.stop();
      }
      mediaPlayer.release();
      mediaPlayer = null;
  }
  ```
