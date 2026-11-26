# Feature Template: Mẫu Triển Khai Tính Năng Hoàn Chỉnh

Mẫu quy trình và khung code từ A-Z để phát triển một tính năng mới (ví dụ: Playlist, Thể loại nhạc, Lịch sử nghe) trong ứng dụng Android Java.

---

## Bước 1: Khai Báo Data Model (`Model.java`)

```java
package com.example.android_finalexam;

import java.io.Serializable;

public class Playlist implements Serializable {
    private int id;
    private String name;
    private String description;

    public Playlist(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

---

## Bước 2: Bổ Sung Bảng Vào `DatabaseHelper.java`

```java
public static final String TABLE_PLAYLISTS = "playlists";
public static final String COLUMN_PLAYLIST_ID = "id";
public static final String COLUMN_PLAYLIST_NAME = "name";
public static final String COLUMN_PLAYLIST_DESC = "description";

// Trong onCreate:
String createPlaylistsTable = "CREATE TABLE " + TABLE_PLAYLISTS + " (" +
        COLUMN_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_PLAYLIST_NAME + " TEXT, " +
        COLUMN_PLAYLIST_DESC + " TEXT)";
db.execSQL(createPlaylistsTable);

// Hàm thêm dữ liệu:
public boolean addPlaylist(String name, String desc) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_PLAYLIST_NAME, name);
    values.put(COLUMN_PLAYLIST_DESC, desc);
    long result = db.insert(TABLE_PLAYLISTS, null, values);
    return result != -1;
}
```

---

## Bước 3: Tạo Adapter Kèm ViewHolder (`PlaylistAdapter.java`)

```java
package com.example.android_finalexam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {

    private static class ViewHolder {
        TextView tvName;
        TextView tvDesc;
    }

    public PlaylistAdapter(@NonNull Context context, @NonNull ArrayList<Playlist> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_playlist, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvPlaylistName);
            holder.tvDesc = convertView.findViewById(R.id.tvPlaylistDesc);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Playlist item = getItem(position);
        if (item != null) {
            holder.tvName.setText(item.getName());
            holder.tvDesc.setText(item.getDescription());
        }

        return convertView;
    }
}
```

---

## Bước 4: Đăng Ký Màn Hình Mới Trong `AndroidManifest.xml`

```xml
<activity 
    android:name=".PlaylistActivity" 
    android:exported="false" />
```
