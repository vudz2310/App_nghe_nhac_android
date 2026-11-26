---
name: android-java-developer
description: Comprehensive development skill for Android Java projects. Covers application architecture, Java best practices, UI/UX implementation, SQLite database persistence, debugging workflows, and feature blueprints for native Android development.
license: MIT
metadata:
  author: Senior Android Engineer
  last-updated: '2026-11-26'
  keywords:
  - android
  - java
  - architecture
  - sqlite
  - mediaplayer
  - ui-ux
  - debugging
  - best-practices
---

# Android Java Developer Skill

Kỹ năng toàn diện hướng dẫn phát triển ứng dụng Android thuần ngôn ngữ Java (Native Android with Java), tập trung vào chất lượng mã nguồn, độ ổn định runtime, quản lý vòng đời chặt chẽ và trải nghiệm người dùng mượt mà.

---

## 🎯 Mục Tiêu & Phạm Vi

Skill này cung cấp các tiêu chuẩn, hướng dẫn kiến trúc và mẫu code thực tế nhằm:
1. Đảm bảo ứng dụng Android viết bằng Java hoạt động trơn tru, không gặp lỗi `NullPointerException`, `IllegalStateException` hay rò rỉ bộ nhớ (`Memory Leak`).
2. Chuẩn hóa kiến trúc tách biệt giữa Tầng hiển thị (UI/Activity), Tầng xử lý logic nghiệp vụ, và Tầng lưu trữ dữ liệu (SQLite Database).
3. Hỗ trợ quy trình kiểm tra lỗi, debug bằng Logcat và build hệ thống với Gradle.

---

## 📂 Danh Mục Tài Liệu Tham Khảo (References)

Để tra cứu chi tiết từng phần, tham khảo các tài liệu chuyên sâu:

- [**Kiến Trúc & Tổ Chức Dự Án**](references/architecture.md): Cấu trúc gói, phân tầng trách nhiệm (Separation of Concerns), xử lý tương tác giữa các Activities và Adapter.
- [**Quy Chuẩn Viết Code Java**](references/java-guidelines.md): Java Core idioms, quản lý luồng (`Handler`/`Looper`), xử lý ngoại lệ an toàn, phòng ngừa Memory Leak.
- [**Quy Chuẩn Thiết Kế Giao Diện UI/UX**](references/ui-guidelines.md): XML Layouts, Material Design, Touch Target, phản hồi người dùng, cử chỉ tương tác (Gesture Detection).
- [**Quy Chuẩn Cơ Sở Dữ Liệu SQLite**](references/database-guidelines.md): `SQLiteOpenHelper`, CRUD an toàn chống SQL Injection, quản lý kết nối, tự động nạp dữ liệu mẫu (Auto-seeding).
- [**Quy Trình Build & Debug Lỗi**](references/build-debug.md): Cấu hình tương thích Gradle & JDK, tra cứu Logcat, xử lý lỗi tương thích JVM và chạy giả lập Android.

---

## 📝 Mẫu Khung Mã Nguồn & Checklist (Templates)

Các bản mẫu có thể tái sử dụng ngay khi phát triển tính năng mới:

- [**Feature Plan Template**](templates/feature-plan.md): Mẫu lập kế hoạch triển khai tính năng trước khi code (tiêu chí nghiệm thu, phạm vi tệp, rủi ro).
- [**Activity & Component Checklist**](templates/activity-fragment-checklist.md): Bảng tự kiểm tra chất lượng Activity/Fragment trước và sau khi viết code.
- [**Activity Template**](templates/activity-template.md): Khung chuẩn một Activity hoàn chỉnh gồm binding View, quản lý trạng thái vòng đời, xử lý sự kiện và giải phóng tài nguyên.
- [**Feature Template**](templates/feature-template.md): Mẫu triển khai tính năng end-to-end từ Model, Database Helper, Adapter cho đến giao diện người dùng.
- [**Project Context**](templates/project-context.md): Mẫu tài liệu tổng hợp ngữ cảnh thực tế của dự án.

