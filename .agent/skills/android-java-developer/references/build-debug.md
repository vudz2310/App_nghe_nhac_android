# Quy Trình Build & Debug Lỗi Dự Án Android

Tài liệu cung cấp các bước xử lý lỗi thường gặp trong quá trình cấu hình Gradle, đồng bộ JDK và chạy kiểm thử trên máy ảo (Emulator).

---

## 1. Cấu Hình Tương Thích Giữa Gradle & Java JDK

Lỗi phổ biến nhất khi build dự án Android cũ trên Android Studio phiên bản mới là:
> *"Incompatible Gradle JVM version: Gradle 8.4 is incompatible with Gradle JVM version 25..."*

### Ma trận tương thích cơ bản:
| Gradle Version | Java JDK hỗ trợ |
| :--- | :--- |
| **Gradle 8.0 - 8.4** | Java 8 tới Java 20 (Khuyến nghị **JDK 17**) |
| **Gradle 8.5 - 8.13** | Hỗ trợ Java 21+ |

### Cách khắc phục chuẩn:
1. **Trong `gradle.properties`:**
   Chỉ định tường minh đường dẫn Java 17:
   ```properties
   org.gradle.java.home=C:/Program Files/Java/jdk-17
   ```
2. **Trong `.idea/gradle.xml`:**
   Đặt giá trị `gradleJvm` thành `#JAVA_HOME`:
   ```xml
   <option name="gradleJvm" value="#JAVA_HOME" />
   ```
3. **Trong `gradle-wrapper.properties` và `libs.versions.toml`:**
   Nâng cấp phiên bản Gradle và Android Gradle Plugin (AGP) đồng bộ nếu muốn sử dụng JDK mới hơn.

---

## 2. Lệnh Build Bằng Dòng Lệnh (CLI)

- **Biên dịch APK Debug:**
  ```powershell
  ./gradlew assembleDebug
  ```
- **Xóa cache build sạch sẽ:**
  ```powershell
  ./gradlew clean
  ```
- **Kiểm tra dependency cây:**
  ```powershell
  ./gradlew app:dependencies
  ```

---

## 3. Khởi Chạy Giả Lập & Cài Đặt Ứng Dụng Qua ADB

1. **Liệt kê danh sách AVD:**
   ```powershell
   & "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -list-avds
   ```
2. **Khởi chạy máy ảo trong nền:**
   ```powershell
   Start-Process -FilePath "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -ArgumentList "-avd Medium_Phone_API_37.0 -no-snapshot-load"
   ```
3. **Kiểm tra trạng thái thiết bị:**
   ```powershell
   adb devices
   adb shell getprop sys.boot_completed
   ```
4. **Cài đặt file APK trực tiếp:**
   ```powershell
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
