---
name: android-ui-ux-design
description: Design principles and UI/UX standards for Android media applications. Covers XML layouts, adaptive theme colors, typography, touch target guidelines, and fluid responsive interactions.
license: MIT
metadata:
  author: Android Developer
  last-updated: '2026-11-26'
  keywords:
  - Android
  - UI/UX
  - Layout
  - Material Design
  - Touch Target
  - Responsive
---

# Android Media UI/UX Design Skill

Bộ quy chuẩn thiết kế giao diện (UI) và trải nghiệm người dùng (UX) cho ứng dụng giải trí đa phương tiện trên Android.

---

## 1. Nguyên Tắc Cốt Lõi (Core Principles)

1. **Content-First:** Âm nhạc và nghệ sĩ là trung tâm. Bìa đĩa nhạc (Artwork) cần được hiển thị sắc nét, bo góc mềm mại (`rounded_corners`).
2. **Kích thước vùng bấm tối thiểu (Touch Target):** Các nút điều khiển như Play/Pause, Next, Previous cần có kích thước tối thiểu `48dp x 48dp` để người dùng thao tác dễ dàng mà không bấm nhầm.
3. **Phản hồi tức thì (Instant Feedback):**
   - Khi bấm Play/Pause, giao diện đổi trạng thái văn bản/icon ngay lập tức.
   - Thêm thông báo nhẹ nhàng (`Toast` hoặc `Snackbar`) khi thao tác Yêu thích, Thêm bài hát, Xóa bài hát thành công.

---

## 2. Tiêu Chuẩn Layout XML

- Ưu tiên dùng `ConstraintLayout` hoặc lồng `LinearLayout` tối thiểu để tối ưu hiệu năng render phân cấp View (View Hierarchy Depth).
- Các định danh ID (`android:id`) phải tuân theo tiền tố rõ ràng:
  - `tv` cho `TextView` (ví dụ: `tvNamePlaying`, `tvArtist`)
  - `btn` cho `Button` (ví dụ: `btnPlay`, `btnBack`)
  - `iv` cho `ImageView` (ví dụ: `ivCover`)
  - `edt` cho `EditText` (ví dụ: `edtSearch`, `edtTitle`)
