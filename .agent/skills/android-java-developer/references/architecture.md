# Kiến Trúc & Tổ Chức Dự Án Android Java

Tài liệu hướng dẫn phân chia cấu trúc và tổ chức dự án ứng dụng Android sử dụng Java để đảm bảo khả năng mở rộng, bảo trì và dễ dàng kiểm thử.

---

## 1. Nguyên Tắc Phân Tầng Trách Nhiệm (Separation of Concerns)

Một ứng dụng Android Java chuẩn cần tách bạch rõ ràng giữa các thành phần:

```
┌────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                   │
│   Activities, Fragments, Custom Views, Dialogs         │
│   (Chỉ đảm nhận hiển thị và bắt sự kiện người dùng)    │
└──────────────────────────┬─────────────────────────────┘
                           │ Data Binding / Events
┌──────────────────────────▼─────────────────────────────┐
│                      ADAPTER LAYER                     │
│   BaseAdapter, ArrayAdapter, RecyclerView.Adapter      │
│   ViewHolder Pattern (Tối ưu tái sử dụng View)         │
└──────────────────────────┬─────────────────────────────┘
                           │ Model Objects
┌──────────────────────────▼─────────────────────────────┐
│                      DOMAIN / MODEL                    │
│   POJO / Data Classes (Song, User, Playlist, etc.)     │
│   Serializable / Parcelable                            │
└──────────────────────────┬─────────────────────────────┘
                           │ CRUD Operations
┌──────────────────────────▼─────────────────────────────┐
│                    PERSISTENCE LAYER                   │
│   SQLiteOpenHelper (DatabaseHelper)                    │
│   SharedPreferences, Content Providers                 │
└────────────────────────────────────────────────────────┘
```

---

## 2. Quy Tắc Giao Tiếp Giữa Các Activity

1. **Truyền Dữ Liệu Qua Intent:**
   - Sử dụng `Serializable` hoặc `Parcelable` cho các đối tượng phức tạp (`Song`).
   - Khóa truyền Intent (`Intent Extra Keys`) nên được định nghĩa dạng hằng số `public static final String EXTRA_SONG = "extra_song"`.
2. **Nhận Kết Quả Từ Activity Khác (Activity Results):**
   - Không sử dụng phương thức cũ đã bị deprecated (`startActivityForResult` / `onActivityResult`).
   - Bắt buộc sử dụng `ActivityResultLauncher` với `ActivityResultContracts.StartActivityForResult()`:
     ```java
     private ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
         new ActivityResultContracts.StartActivityForResult(),
         result -> {
             if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                 // Xử lý dữ liệu trả về
             }
         }
     );
     ```

---

## 3. Tối Ưu Hóa ListView Với ViewHolder Pattern

Tránh gọi `findViewById()` liên tục trong hàm `getView()` của Adapter. Luôn triển khai mẫu `ViewHolder`:

```java
private static class ViewHolder {
    TextView titleTextView;
    TextView artistTextView;
    ImageView imageView;
}

@NonNull
@Override
public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_song, parent, false);
        holder = new ViewHolder();
        holder.titleTextView = convertView.findViewById(R.id.song_title);
        holder.artistTextView = convertView.findViewById(R.id.song_artist);
        holder.imageView = convertView.findViewById(R.id.song_image);
        convertView.setTag(holder);
    } else {
        holder = (ViewHolder) convertView.getTag();
    }
    // Gán dữ liệu vào holder...
    return convertView;
}
```
