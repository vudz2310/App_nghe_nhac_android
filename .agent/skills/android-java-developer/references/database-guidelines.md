# Quy Chuẩn Cơ Sở Dữ Liệu SQLite Trong Android

Hướng dẫn thiết kế cơ sở dữ liệu với `SQLiteOpenHelper`, thực thi truy vấn an toàn và quản lý dữ liệu mẫu.

---

## 1. Thiết Kế Bảng & Tên Cột

- Định nghĩa tên bảng và tên cột thành các hằng số `public static final String` trong lớp `DatabaseHelper`.
- Luôn có cột khóa chính `id INTEGER PRIMARY KEY AUTOINCREMENT`.

```java
public static final String TABLE_SONGS = "songs";
public static final String COLUMN_ID = "id";
public static final String COLUMN_TITLE = "title";
public static final String COLUMN_ARTIST = "artist";
public static final String COLUMN_IMAGE_RESOURCE = "image_resource";
public static final String COLUMN_AUDIO_RESOURCE = "audio_resource";
```

---

## 2. Truy Vấn Tham Số Hóa (Parameterized Queries)

Tuyệt đối **KHÔNG** nối chuỗi trong mệnh đề `WHERE` để ngăn chặn lỗi định dạng ký tự đặc biệt (dấu nháy đơn, nháy kép) và phòng chống SQL Injection.

```java
// ĐÚNG: Sử dụng mảng String[] whereArgs
public boolean deleteSong(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    int rows = db.delete(TABLE_SONGS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    return rows > 0;
}

// SAI: Nối chuỗi trực tiếp
// db.delete(TABLE_SONGS, "title = '" + title + "'", null);
```

---

## 3. Tự Động Khởi Tạo Dữ Liệu Mẫu (Auto-Seeding)

Để đảm bảo trải nghiệm người dùng ngay sau khi cài đặt hoặc sau khi xóa dữ liệu ứng dụng, cung cấp hàm kiểm tra và nạp sẵn dữ liệu mặc định:

```java
public void seedDefaultSongsIfEmpty(SQLiteDatabase db) {
    Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SONGS, null);
    if (cursor != null) {
        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
            insertDefaultSongs(db);
        }
        cursor.close();
    }
}
```

---

## 4. Quản Lý Đóng Mở Kết Nối

- Đối với các màn hình chính (như `ListSong`), giữ kết nối cơ sở dữ liệu mở trong suốt vòng đời của Activity và chỉ gọi `db.close()` tại phương thức `onDestroy()`.
- Luôn kiểm tra `cursor != null` và gọi `cursor.close()` ngay sau khi duyệt xong dữ liệu.

---

## 5. Mở Rộng: Tích Hợp REST API & Firebase (Khi Cần Thiết)

Khi dự án mở rộng từ Offline sang Online:
1. **REST API (Retrofit / OkHttp):**
   - Xác định rõ hợp đồng dữ liệu (API Contract: endpoints, request/response DTOs, mã trạng thái HTTP).
   - Thực thi gọi mạng trên Background Thread (qua Callbacks hoặc ExecutorService), không bao giờ gọi trên UI Main Thread.
   - Luôn sử dụng kết nối bảo mật `HTTPS`.
2. **Firebase:**
   - Xác định chính xác dịch vụ Firebase sử dụng (Authentication, Firestore, Realtime Database, Cloud Storage).
   - Tôn trọng quy tắc bảo mật Firebase Security Rules phía máy chủ. Không lưu trữ private keys, service-account JSON trực tiếp trong mã nguồn ứng dụng Android client.

