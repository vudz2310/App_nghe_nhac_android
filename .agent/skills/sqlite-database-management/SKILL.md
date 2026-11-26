---
name: sqlite-database-management
description: Guidelines and patterns for managing SQLite databases in Android apps. Covers SQLiteOpenHelper, schema versioning, CRUD operations, defensive querying, and automated database seeding.
license: MIT
metadata:
  author: Android Developer
  last-updated: '2026-11-26'
  keywords:
  - Android
  - SQLite
  - SQLiteOpenHelper
  - Database
  - CRUD
  - Seeding
---

# SQLite Database Management Skill

Quy chuẩn thiết kế và thao tác cơ sở dữ liệu SQLite trong ứng dụng Android.

---

## 1. Cấu trúc SQLiteOpenHelper Chuẩn

```java
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "songs_database";
    public static final int DATABASE_VERSION = 1;

    // Tên bảng và các cột
    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ARTIST = "artist";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
```

---

## 2. Các Quy Tắc An Toàn (Safety Rules)

1. **Luôn sử dụng Parameterized Query (Tránh SQL Injection):**
   - Dùng `selectionArgs` trong `query()`, `update()`, `delete()` thay vì nối chuỗi trực tiếp:
     ```java
     // ĐÚNG
     db.delete(TABLE_SONGS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
     ```
2. **Luôn đóng Cursor trong khối finally hoặc sau khi đọc:**
   ```java
   Cursor cursor = db.query(...);
   if (cursor != null) {
       try {
           while (cursor.moveToNext()) {
               // Đọc dữ liệu
           }
       } finally {
           cursor.close();
       }
   }
   ```
3. **Quản lý kết nối Database:**
   - Tránh đóng kết nối `db.close()` ở giữa các hàm thao tác nhanh nếu Activity còn đang hiển thị. Chỉ đóng `db.close()` khi Activity bị hủy (`onDestroy()`).
