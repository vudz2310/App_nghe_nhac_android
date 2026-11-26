# Activity Template Chuẩn Cho Android Java

Mẫu code khung hoàn chỉnh của một `AppCompatActivity` trong Android Java, bao gồm:
- Khởi tạo Views
- Kết nối Cơ sở dữ liệu SQLite
- Đăng ký `ActivityResultLauncher`
- Quản lý vòng đời và giải phóng tài nguyên an toàn

---

```java
package com.example.android_finalexam;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SampleActivity extends AppCompatActivity {

    // 1. Khai báo Views
    private EditText edtSearch;
    private Button btnAction;
    private ListView lvItems;

    // 2. Khai báo Database Helper
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    // 3. Activity Result Launcher
    private ActivityResultLauncher<Intent> detailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_activity_layout);

        // Khởi tạo Views
        initViews();

        // Khởi tạo Database
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        // Đăng ký Launcher nhận kết quả
        registerLaunchers();

        // Cài đặt sự kiện (Listeners)
        setupEventListeners();

        // Nạp dữ liệu ban đầu
        loadInitialData();
    }

    private void initViews() {
        edtSearch = findViewById(R.id.edtSearch);
        btnAction = findViewById(R.id.btnAction);
        lvItems = findViewById(R.id.lvItems);
    }

    private void registerLaunchers() {
        detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Toast.makeText(this, "Thao tác thành công", Toast.LENGTH_SHORT).show();
                    loadInitialData(); // Làm mới danh sách
                }
            }
        );
    }

    private void setupEventListeners() {
        btnAction.setOnClickListener(v -> {
            String input = edtSearch.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            // Thực hiện hành động
        });
    }

    private void loadInitialData() {
        // Đọc dữ liệu từ SQLite
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInitialData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng kết nối cơ sở dữ liệu
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
```
