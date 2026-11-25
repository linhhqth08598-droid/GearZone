# KẾ HOẠCH LÀM WEB – GearZone (Phần của linhhqth08598) - Bản cập nhật ngày 25/11/2026 (đã bổ sung SQL UC 343, rà & hoàn thiện phần code/JSP còn lại)

|                          |                                                                                                                               |
| ------------------------ | ----------------------------------------------------------------------------------------------------------------------------- |
| **Thành viên**           | Hà Quang Linh – TH08598 (package `linhhqth08598`)                                                                             |
| **Môn / Nhóm**           | JAV102 – Nhóm 2                                                                                                               |
| **Công nghệ**            | Java Servlet + JSP (Jakarta EE, `jakarta.servlet`), SQL Server (JDBC), NetBeans, kiến trúc Controller → Service → DAO → Model |
| **Khối lượng được giao** | **85 usecase – 244,7 giờ (~30,6 ngày công, 25% dự án)**                                                                       |
| **Thành viên khác**      | dungntth08581 (98 UC), khanhnb08894 (114 UC), minhtbth08199 (73 UC)                                                           |

---

## 0. Kết quả rà soát & cập nhật (15/11/2026 – 25/11/2026)

- **Biên dịch thử toàn bộ source:** chỉ lỗi ở `EmailUtil` do môi trường rà soát thiếu thư viện `jakarta.mail` (không phải lỗi code); `ReportExportServlet`/`AdminExportServlet` dùng `ExcelExportUtil` tạo OOXML trực tiếp trong bộ nhớ. Các file DAO/Service/Controller của `linhhqth08598` đã rà và cập nhật thêm ngày 25/11/2026.
- **Kiểm tra 24 câu SQL của `ReportService`** với schema `GearZone.sql`: tên bảng/cột đều hợp lệ. Vẫn cần chạy thật trên SQL Server để xem kết quả/số liệu.
- **UC 16, 17 (Lưu/Chia sẻ cấu hình):** phía Linh đã có `PcBuildShareDAO`, `BuildShareService`, `SharedBuildServlet` (`/build-share`), `pc_build_history.jsp`, `shared_build.jsp`; ngày 25/11/2026 đã rà lại điều kiện `saveBuild` (chỉ lưu `DRAFT` có ít nhất 1 `pc_build_details` đang `ACTIVE`), `regenerateToken` (chỉ áp dụng cho `SAVED`), sửa link "Xem / sửa" sang `/build-pc?buildId=...` khớp `BuildPCServlet`, và ẩn nút "Chia sẻ" khi cấu hình chưa ở trạng thái `SAVED`.
- **Contact:** bảng `contacts` đã có trong `GearZone.sql`, `ContactServlet` ghi được; file `ContactDAO.java` nằm ở package `khanhnb08894.dao` (không có trong `linhhqth08598`).
- **UC 343 (ngân sách):** đã tạo file `GearZone_UC343_budget_tier_rules.sql` tạo bảng `budget_tier_rules` + dữ liệu khung ngân sách mẫu (idempotent, khớp `GearZone.sql`), đi kèm `BudgetTierRulesDAO`, `BudgetTierService` và tab "Đề xuất theo ngân sách" trong `admin/system.jsp`. Cần chạy file SQL này trên SQL Server và nối `BudgetTierService` vào `RecommendPCService` khi đề xuất theo ngân sách.
- **Địa chỉ & Hồ sơ / Avatar (UC 8–10):** đã bỏ hẳn Quận/Huyện ở form/servlet; ngày 25/11/2026 đã cập nhật thêm nút xóa ảnh đại diện (`removeAvatar`), nhận diện `Locale.ROOT` khi kiểm tra đuôi file ảnh trong `UserServlet`, escape thông báo flash & chữ cái đầu avatar an toàn khi `fullName` trống trong `profile.jsp`.
- **Phân trang:** đã áp dụng cho `admin/audit_logs.jsp`, `admin/reviews.jsp`, `admin/returns.jsp`, `admin/support.jsp`, `user/order_history.jsp`, `user/notifications.jsp` (dùng chung `components/pagination.jsp`).
- **Sidebar người dùng:** đã bổ sung mục **So sánh linh kiện** (`/compare`) trên `web/views/components/sidebar.jsp` ngày 25/11/2026.

### Bổ sung lần 3 – làm hộ phần phụ thuộc người khác (15/11/2026)

- **Lỗi `ERR_INVALID_RESPONSE` khi xuất Excel (`/admin/export-report`)** – sửa: file Excel được tạo trong bộ nhớ (`ExcelExportUtil`) rồi mới gửi; lỗi (thiếu thư viện POI, thiếu font, SQL...) sẽ hiện trang lỗi rõ ràng thay vì phản hồi hỏng. Cả `ReportExportServlet` (2 nút dashboard) và `AdminExportServlet` dùng chung.
- **Làm hộ Minh (để UC 16–17 chạy được):** `BuildPCServlet` + `BuildPCService` + `custom.jsp` bản gọn (tạo cấu hình nháp, chọn/xóa linh kiện, tự tính tổng ở server, báo thiếu linh kiện bắt buộc, lưu qua `BuildShareService`); `PublicBuildServlet` (`/shared-build`) chuyển sang `/build-share`.
- **Làm hộ Minh (UC 343):** `BudgetTierService.findTier()` / `allocate()` để `RecommendPCService` chia ngân sách theo khung admin cấu hình.
- **Làm hộ Dũng (UC 285 và thông báo):** `NotificationService.notifyWarrantyProgress()`, `notifyOrder()`, `notifyPromotion()`, `send()` + `NotificationsDAO.insert()`. Dũng chỉ cần gọi khi đổi trạng thái bảo hành/đơn hàng.
- **Lần 4:** sửa lỗi nút "Xem / sửa" (servlet chỉ đọc `buildId` trong khi nút gửi `id`); nối `CompatibilityService` của Khánh vào trang Build PC; làm hộ Dũng giỏ hàng bản gọn (`/cart`) + thêm cả cấu hình vào giỏ (UC 98/221) + cảnh báo tồn kho (UC 99).
- **Lần 5:** sửa lỗi 500 (`l.short` là từ khóa); làm hộ Dũng luồng đơn hàng gọn (`/checkout`, `/order`, `/admin/orders`, COD) và hộ Khánh `InventoryService` (giữ/hoàn/xuất kho) để Lịch sử mua hàng, đánh giá, đổi trả, báo cáo của Linh có đơn DELIVERED thật để test. Lưu địa chỉ giao hàng viết lại trong 1 giao dịch và hiện lỗi SQL thật khi thất bại.
- **Chuông thông báo:** trước đây không nơi nào gán `session.unreadCount` nên số đỏ không bao giờ hiện – đã thêm `UnreadCountFilter`.

---

## 1. Hiện trạng code (Đã rà soát đối chiếu thực tế từ Source Code & `GearZone.sql`)

**Đã làm được (✅ – 82/85 UC có code, ~96%; còn 3 UC 8–10 cần test). Ký hiệu ✱ = mới viết xong, đã biên dịch nhưng CHƯA test trên web (23 UC)**

- **Khung kiến trúc & Bảo mật:** `AdminAuthFilter`, `UserAuthFilter` (package `fourSome.filter`). Đã hoàn thiện 24 Servlets, 12 Services, 21 DAOs trong package `linhhqth08598` và 26 trang JSP trong `views/linhhqth08598/`.
- **Module A – Tài khoản & Xác thực (UC 1–7):** Hoàn thiện toàn bộ 5 trang `auth/` (`login.jsp`, `register.jsp`, `forgetPW.jsp`, `resetPW.jsp`, `verifyOTP.jsp`), `AuthServlet`, `AuthService`, `UsersDAO`, `UserVerificationsDAO` (khớp bảng `users`, `user_verifications`).
- **Module B – Wishlist, Lịch sử, Báo giá (UC 11–14, 18):** Hoàn thiện `WishlistServlet`, `OrderHistoryServlet`, `PriceAlertServlet`, các file `user/wishlist.jsp`, `user/order_history.jsp`, `user/price_alert.jsp` và Background Job `PriceAlertJob` (khớp bảng `user_wishlists`, `orders`, `price_drop_notifications`).
- **Module B (Phần 3) – Lịch sử cấu hình PC (UC 15):** Hoàn thiện `PcBuildHistoryServlet`, `user/pc_build_history.jsp` hiển thị danh sách và chi tiết cấu hình PC từ bảng `pc_builds` và `pc_build_details`.
- **Module D – Đánh giá sản phẩm (UC 296–302):** Hoàn thiện `ReviewServlet`, `product/review_section.jsp` (hỗ trợ chấm điểm nửa sao `quality_rating` `decimal(2,1)`, điểm hiệu năng `performance_rating`, đính kèm ảnh Base64 vào `part_review_images`), `AdminReviewServlet`, `admin/reviews.jsp` (đã chạy được trên web; đã sửa lỗi hiển thị hàng khi hover).
- **Module F – Nhân viên & Phân quyền (UC 326–334):** Hoàn thiện `AdminUserServlet`, `AdminRoleServlet`, `admin/users.jsp`, `admin/roles.jsp` (khớp 5 bảng `users`, `roles`, `user_roles`, `permissions`, `role_permissions`).
- **Module H – Audit Log (UC 344):** Hoàn thiện `AdminAuditLogServlet`, `admin/audit_logs.jsp`, `StaffAuditLogsDAO`, `AuditLogService` (khớp bảng `staff_audit_logs`, có tìm kiếm & lọc theo ngày).
- **Module C + E – Thông báo, Đổi trả, Hỗ trợ (UC 285, 288–295, 304–310):**
  - **Thông báo:** Hoàn thiện `NotificationServlet`, `NotificationsDAO`, `NotificationService`, `user/notifications.jsp` (bảng `notifications`).
  - **Đổi trả:** Hoàn thiện `ReturnServlet`, `AdminReturnServlet`, `ProductReturnsDAO`, `ReturnService`, `user/returns.jsp`, `admin/returns.jsp` (bảng `product_returns`).
  - **Hỗ trợ:** Hoàn thiện `SupportServlet`, `AdminSupportServlet`, `SupportTicketsDAO`, `SupportTicketMessagesDAO`, `SupportService`, `support/ticket.jsp`, `admin/support.jsp` (bảng `support_tickets`, `support_ticket_messages`).
- **Module G – Danh mục hệ thống & Quy tắc (UC 335–342):**
  - Hoàn thiện Quản lý Socket CPU: `AdminSocketServlet`, `SocketTypesDAO`, `SocketService`, `admin/socket.jsp` (bảng `socket_types`).
  - Hoàn thiện Danh mục hệ thống (Chipset, chuẩn RAM, chuẩn ổ cứng): `AdminSystemCatalogServlet`, `SystemCatalogDAO`, `admin/system_catalog.jsp` (bảng `chipset_types`, `ram_standards`, `storage_interfaces`).
  - ✱ Cấu hình hệ thống (UC 341–342): `AdminSystemServlet`, `PsuPowerRulesDAO`, `UsageProfilesDAO`, `admin/system.jsp` – thêm/sửa/bật-tắt ngưỡng công suất nguồn PSU và tiêu chí hiệu năng gaming (bảng `psu_power_rules`, `usage_profiles`). *(Giao diện CRUD Thương hiệu/Danh mục/Quy tắc tương thích tích hợp cùng `khanhnb08894` tại `admin/catalog_brand.jsp`, `admin/catalog_category.jsp`, `admin/compatibility.jsp`; mục sidebar "Cấu hình hệ thống" đã được thêm)*.
- **Module I + J – Thống kê & Xuất báo cáo (UC 346–370):** Dashboard (`AdminDashboardServlet`, `admin/dashboard.jsp`, KPI + biểu đồ danh mục + Top 5 bán chạy) và xuất Excel doanh thu/tồn kho (`ReportExportServlet`) đã chạy được. ✱ Mới viết: `ReportService` (24 báo cáo), `AdminReportServlet`, `admin/reports.jsp` (UC 346–367, có lọc ngày), `AdminExportServlet` (UC 368–370, xuất Excel mọi báo cáo) và mục sidebar "Thống kê chi tiết".

**Đã hoàn thiện code & rà soát bổ sung (✅ / 🔄 – 85/85 UC đã có code; các mục ✱ đã viết xong và rà code, chờ chạy kiểm thử trực tiếp trên SQL Server + Tomcat)**

- **Module A (UC 8–10 – Hồ sơ, Sổ địa chỉ, Ảnh đại diện):** Đã có đủ `UserServlet`, `ShippingAddressesDAO`, `user/profile.jsp`, `user/address.jsp`; ngày 25/11/2026 đã bổ sung nút xóa avatar trên `profile.jsp`, dùng `Locale.ROOT` khi kiểm tra đuôi file ảnh và escape an toàn. Còn bước test trực tiếp trên web với SQL Server + Tomcat.
- **Module B (UC 16–17 – Lưu & Chia sẻ cấu hình):** Đã có `PcBuildShareDAO`, `BuildShareService`, `SharedBuildServlet`, `pc_build_history.jsp`, `shared_build.jsp`; ngày 25/11/2026 đã rà lại điều kiện lưu cấu hình có linh kiện `ACTIVE`, thu hồi token chỉ trên cấu hình `SAVED`, sửa đường dẫn `/build-pc?buildId=...` và ẩn nút chia sẻ khi chưa `SAVED`. Còn bước test end-to-end với dữ liệu `pc_builds` thật.
- **Module G (UC 343 – Tiêu chí đề xuất theo ngân sách):** Đã có `BudgetTierRulesDAO`, `BudgetTierService`, `admin/system.jsp` và đã tạo file `GearZone_UC343_budget_tier_rules.sql` tạo bảng `budget_tier_rules` + dữ liệu khung ngân sách mẫu. Cần chạy file SQL trên SQL Server và phối hợp `minhtbth08199` gọi `BudgetTierService`.

---

## 2. Phân rã công việc theo module (10 module, 85 UC)

| #   | Module                                            | UC (STT trong Excel) | Số UC  | Effort (giờ) | Trạng thái hiện tại (Đối chiếu code thật)                                                                             | Bảng DB chính (`GearZone.sql`)                                                        |
| --- | ------------------------------------------------- | -------------------- | ------ | ------------ | --------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------- |
| A   | Tài khoản & xác thực                              | 1–10                 | 10     | 23,5         | 🔄 **Đã có đủ code + rà 25/11 (cần test web)** - Xong UC 1–7; UC 8–10 có code, thêm Tên đường & nút xóa avatar, còn test web | `users`, `user_verifications`, `shipping_addresses` |
| B   | Wishlist, lịch sử, chia sẻ cấu hình, báo giảm giá | 11–18                | 8      | 19,2         | 🔄 **Đã có đủ code + rà 25/11 (cần test web)** - Xong UC 11–15, 18; UC 16–17 đã sửa link/trạng thái, chờ test end-to-end | `user_wishlists`, `orders`, `pc_builds`, `pc_build_details`, `price_drop_notifications` |
| C   | Thông báo bảo hành & đổi trả/hoàn tiền            | 285, 288–295         | 9      | 23,6         | ✅ **Hoàn thành (100%)** (`notifications.jsp`, `user/returns.jsp`, `admin/returns.jsp`)                               | `notifications`, `product_returns`, `payments`                                        |
| D   | Đánh giá sản phẩm / cấu hình / lắp ráp            | 296–302              | 7      | 16,6         | ✅ **Hoàn thành (100%)** (`ReviewServlet`, `AdminReviewServlet`, `review_section.jsp`, `admin/reviews.jsp`)           | `part_reviews`, `part_review_images`, `pc_builds`, `assembly_orders`                  |
| E   | Tư vấn, khiếu nại, hỗ trợ kỹ thuật                | 304–310              | 7      | 16,9         | ✅ **Hoàn thành (100%)** (`SupportServlet`, `AdminSupportServlet`, `support/ticket.jsp`, `admin/support.jsp`)         | `support_tickets`, `support_ticket_messages`, `user_requirements`, `user_current_pcs` |
| F   | Nhân viên & phân quyền                            | 326–334              | 9      | 22,9         | ✅ **Hoàn thành (100%)** (`AdminUserServlet`, `AdminRoleServlet`, `AdminAuthFilter`, `users.jsp`, `roles.jsp`)        | `users`, `roles`, `user_roles`, `permissions`, `role_permissions`                     |
| G   | Danh mục hệ thống & quy tắc                       | 335–343              | 9      | 45,0         | 🔄 **9/9 UC có code + SQL 25/11 (✱ cần test)** - UC 335–340 ✅; UC 341–343 ✱ (`GearZone_UC343_budget_tier_rules.sql` đã tạo) | `socket_types`, `chipset_types`, `ram_standards`, `storage_interfaces`, `psu_power_rules`, `usage_profiles`, `compatibility_rules`, `budget_tier_rules` |
| H   | Audit log nhân viên                               | 344                  | 1      | 3,1          | ✅ **Hoàn thành (100%)** (`StaffAuditLogsDAO`, `AuditLogService`, `AdminAuditLogServlet`, `admin/audit_logs.jsp`)     | `staff_audit_logs`                                                                    |
| I   | Thống kê (dashboard)                              | 346–367              | 22     | 65,5         | ✅ **Hoàn thành code** - Dashboard ✅ (có sẵn, gồm bán chạy + doanh thu theo danh mục); 20 UC còn lại ✱ nằm trong `reports.jsp` (24 báo cáo, mới viết, cần test) | `orders`, `order_items`, `parts`, `inventory`, `pc_builds`, `warranty_claims`, … |
| J   | Xuất báo cáo                                      | 368–370              | 3      | 8,4          | ✅ **Hoàn thành code** - UC 368, 369 ✅ (`ReportExportServlet`); UC 370 ✱ (`AdminExportServlet`, mới viết, cần test) | `orders`, `order_items`, `parts`, `inventory` |
|     | **Tổng**                                          |                      | **85** | **244,7**    | **85/85 UC đã có code** (bao gồm 23 UC ✱/🔄 đã rà code ngày 25/11/2026 & file SQL UC 343; còn chạy kiểm thử trực tiếp trên DB/Tomcat) |  |

---

## 3. Lộ trình thực hiện (Cập nhật tiến độ theo code hiện có)

### Tuần 0 – Chuẩn bị (✅ Đã hoàn tất)

- [x] Khởi tạo cấu trúc package `linhhqth08598` và Model `fourSome`.
- [x] Hoàn thiện `AdminAuthFilter` và `UserAuthFilter`.
- [x] Import `GearZone.sql`, cấu hình NetBeans + Tomcat 10+ và kết nối SQL Server thành công.

### Tuần 1 – Module A: Tài khoản & xác thực (23,5h) (🔄 Đạt ~90%)

- [x] `UsersDAO`, `ShippingAddressesDAO`, `UserVerificationsDAO`.
- [x] `AuthService`, `AuthServlet` + 5 trang JSP `auth/`.
- [x] `UserService`, `UserServlet` và `UserProfileServlet`.
- [x] Giao diện `user/address.jsp`: thêm Tên đường (không bắt buộc), escape dữ liệu hiển thị; rà `UserServlet` & `profile.jsp` ngày 25/11/2026 (nút xóa avatar, `Locale.ROOT`, escape).
- [ ] Test trực tiếp trên SQL Server + Tomcat: sửa hồ sơ `user/profile.jsp`, thêm/sửa/xóa/đặt mặc định địa chỉ, upload/xóa `avatar_url`.

### Tuần 2 – Module F + H: Nhân viên, phân quyền, audit log (26h) (✅ Đã hoàn thành)

- [x] `RoleService`, `RolesDAO`, `PermissionsDAO`, `RolePermissionsDAO`, `UserRolesDAO`.
- [x] `AdminAuthFilter`: chặn `/admin/*` nếu chưa đăng nhập hoặc không đủ quyền.
- [x] `AdminUserServlet` + `admin/users.jsp`.
- [x] `AdminRoleServlet` + `admin/roles.jsp`.
- [x] `AuditLogService` + `AdminAuditLogServlet` + `admin/audit_logs.jsp`.

### Tuần 3 – Module B + D: Wishlist, cấu hình, đánh giá (35,8h) (🔄 Đạt 88%)

- [x] Wishlist (`UserWishlistsDAO`, `WishlistService`, `WishlistServlet`, `user/wishlist.jsp`).
- [x] Lịch sử mua linh kiện (`OrderHistoryServlet`, `user/order_history.jsp`).
- [x] Lịch sử cấu hình PC (`PcBuildHistoryServlet`, `user/pc_build_history.jsp`).
- [x] ✱ Lưu cấu hình PC và Chia sẻ qua `share_token` – code phía Linh đã rà lại điều kiện lưu/thu hồi/link `buildId` ngày 25/11/2026; cần test end-to-end cùng `minhtbth08199`.
- [x] Đăng ký báo giảm giá (`PriceDropNotificationsDAO`, `PriceAlertServlet`, `user/price_alert.jsp`) + job ngầm `PriceAlertJob`.
- [x] Đánh giá (`PartReviewsDAO`, `PartReviewImagesDAO`, `ReviewService`, `ReviewServlet`, `product/review_section.jsp`, `AdminReviewServlet`, `admin/reviews.jsp`).
- [x] **Mốc:** khách hàng mua xong → đánh giá → thấy trong lịch sử/wishlist.

### Tuần 4 – Module C + E: Thông báo, đổi trả, hỗ trợ (57,1h) (✅ Đã hoàn thành)

- [x] `NotificationServlet` / `user/notifications.jsp`.
- [x] Đổi trả (`ProductReturnsDAO`, `ReturnService`, `ReturnServlet`, `user/returns.jsp`, `AdminReturnServlet`, `admin/returns.jsp`).
- [x] Hỗ trợ (`SupportTicketsDAO`, `SupportTicketMessagesDAO`, `SupportService`, `SupportServlet`, `support/ticket.jsp`, `AdminSupportServlet`, `admin/support.jsp`).
- [x] **Mốc:** 1 luồng đổi trả đầy đủ và 1 luồng ticket hỗ trợ chạy end-to-end.

### Tuần 5 – Module G: Danh mục & quy tắc hệ thống (45h) (🔄 8/9 UC)

- [x] Hoàn thiện Quản lý Socket CPU (`AdminSocketServlet`, `admin/socket.jsp`).
- [x] Hoàn thiện Quản lý Chipset/RAM/Ổ cứng (`AdminSystemCatalogServlet`, `admin/system_catalog.jsp`).
- [x] ✱ Cấu hình hệ thống: Ngưỡng nguồn PSU & Tiêu chí hiệu năng (`AdminSystemServlet`, `admin/system.jsp`) – mới viết, cần test.
- [x] ✱ Tiêu chí đề xuất theo ngân sách (UC 343) – bảng `budget_tier_rules` (`GearZone_UC343_budget_tier_rules.sql` đã tạo) + tab mới trong `admin/system.jsp`, cần chạy SQL trên DB thật và test.
- [x] Phối hợp đồng bộ Thương hiệu, Danh mục & Quy tắc tương thích (`khanhnb08894`).

### Tuần 6 – Module I + J: Thống kê & xuất báo cáo (73,9h) (✅ Đã có code, ✱ cần test)

- [x] Dashboard KPI (`AdminDashboardServlet`, `admin/dashboard.jsp`).
- [x] ✱ Báo cáo thống kê chi tiết (`ReportService` 24 báo cáo, `AdminReportServlet`, `admin/reports.jsp`) – mới viết, cần test.
- [x] Xuất báo cáo Excel doanh thu/tồn kho (`ReportExportServlet`); ✱ xuất mọi báo cáo (`AdminExportServlet`) – mới viết, cần test.

---

## 4. Quy trình làm mỗi usecase (Khuôn chuẩn)

1. **DAO** – viết câu SQL dùng `PreparedStatement`, test riêng bằng hàm `main`.
2. **Service** – nghiệp vụ + validate + ghi audit log (nếu là thao tác admin).
3. **Servlet** – nhận tham số, gọi service, `request.setAttribute`, `forward` tới JSP (POST thành công thì `redirect`).
4. **JSP** – dùng JSTL/EL (`${...}`), include `header.jsp`/`footer.jsp`, escape dữ liệu (`<c:out>`).
5. **Test tay** – 1 case đúng + 1–2 case lỗi.
6. **Commit** – 1 usecase / 1 commit, message dạng `feat(UC08): cập nhật thông tin cá nhân`.

---

## 5. Checklist an toàn & chất lượng

- [x] Mật khẩu luôn hash (`PasswordHasher`), không log mật khẩu / OTP.
- [x] Mọi SQL dùng `PreparedStatement` (chống SQL injection).
- [x] Trang `/admin/*` đi qua `AdminAuthFilter`; trang `/user/*` đi qua `UserAuthFilter`.
- [x] Upload: kiểm tra đuôi file (`IMAGE_EXTENSIONS`) + dung lượng (`MAX_IMAGE_SIZE`) (Đã làm với ảnh Base64 lưu vào `varchar(max)`).
- [x] OTP hết hạn sau 5 phút, dùng 1 lần (`user_verifications.expires_at`).
- [x] Xóa mềm (đổi `status`) thay vì xóa cứng ở các bảng có khóa ngoại.
- [x] Escape nội dung người dùng nhập bằng `<c:out>` ở các trang của Linh (hỗ trợ, ticket, thông báo, đổi trả, đánh giá, địa chỉ, hồ sơ...). Một số trang của thành viên khác chưa rà.
- [x] Modal đặt ngoài `<tbody>` (socket, support, returns, system_catalog, catalog_brand, catalog_category) để bảng không bị lỗi hiển thị.
- [x] Sửa tên package gõ sai (`linhhqth098`, `linhhhqth08598`) về `linhhqth08598`.
- [x] Phân trang bằng `PaginationUtil` (`DEFAULT_PAGE_SIZE = 12`, `ADMIN_PAGE_SIZE = 10`) – đã áp dụng cho các trang danh sách của Linh (✱ cần test); trang của thành viên khác tự áp dụng.
- [x] Các hàm lấy dữ liệu dùng `try-with-resources` đóng `Connection`.

---

## 6. Phụ thuộc & phối hợp với thành viên khác

| Với ai        | Nội dung                                                                         | Khi nào   |
| ------------- | -------------------------------------------------------------------------------- | --------- |
| minhtbth08199 | Chia sẻ cấu hình (`share_token`), lịch sử cấu hình, ngưỡng PSU, tiêu chí đề xuất | Tuần 3, 5 |
| khanhnb08894  | Quy tắc tương thích, danh mục thương hiệu/socket, dữ liệu tồn kho                | Tuần 5, 6 |
| dungntth08581 | Đơn hàng/thanh toán cho lịch sử mua & đổi trả, bảo hành → thông báo tiến độ      | Tuần 4    |

---

## 7. Rủi ro & cách giảm

| Rủi ro                                        | Cách xử lý                                                                                  |
| --------------------------------------------- | ------------------------------------------------------------------------------------------- |
| Thống kê (65,5h) dồn cuối, dễ trễ             | Đã viết xong `ReportService` (24 báo cáo) + `reports.jsp`; còn phải test từng báo cáo với dữ liệu thật (SQL chưa chạy thử trên SQL Server) |
| Phụ thuộc dữ liệu/phần của bạn khác chưa xong | Dùng dữ liệu mẫu trong DB, thống nhất tên cột/URL sớm                                       |
| Gửi OTP SĐT cần API bên thứ 3                 | Làm dạng giả lập (in ra console/DB) trước, gắn SMS thật nếu còn thời gian                   |
| Gửi email lỗi (SMTP/App password)             | Đã cấu hình và test qua `EmailUtil`                                                         |
| Xung đột Git                                  | Mỗi người sửa trong package riêng; file chung (`style.css`, `header.jsp`) báo trước khi sửa |

---

## 8. Bảng theo dõi tiến độ (Đã cập nhật theo code mới nhất)

| Module                               | Kế hoạch | Trạng thái        | Ghi chú                                                                                                 |
| ------------------------------------ | -------- | ----------------- | ------------------------------------------------------------------------------------------------------- |
| Tuần 0 – Chuẩn bị                    | 0,5 tuần | ✅ Hoàn thành     | Đã xong cấu trúc, `DBConnect` & `AdminAuthFilter`, `UserAuthFilter`                                     |
| A – Tài khoản & xác thực             | Tuần 1   | 🔄 Rà xong 25/11  | Xong UC 1–7; UC 8–10 có code + Tên đường & nút xóa avatar, còn test web |
| F + H – Nhân viên, phân quyền, audit | Tuần 2   | ✅ Hoàn thành     | Xong toàn bộ DAO, Service, Servlet & JSP                                                                |
| B – Wishlist, Lịch sử, Báo giá       | Tuần 3   | 🔄 Rà xong 25/11  | Đã rà lại UC 16–17 (`PcBuildShareDAO`, `pc_build_history.jsp`); chờ test với dữ liệu `pc_builds` thật |
| D – Đánh giá                         | Tuần 3   | ✅ Hoàn thành     | Đã xong logic & giao diện (kể cả Admin quản lý)                                                         |
| C + E – Thông báo, đổi trả, hỗ trợ   | Tuần 4   | ✅ Hoàn thành     | Đã xong logic & giao diện                                                                               |
| G – Danh mục & quy tắc               | Tuần 5   | 🔄 Đã có SQL 25/11 | Đã tạo `GearZone_UC343_budget_tier_rules.sql` + `admin/system.jsp` 3 tab (✱ cần chạy SQL & test) |
| I + J – Thống kê & xuất báo cáo      | Tuần 6   | ✅ Có code (✱ cần test) | Dashboard + xuất Excel cũ chạy được; ✱ `reports.jsp` (24 báo cáo) và `AdminExportServlet` mới viết |
| Tích hợp, test, hoàn thiện           | Tuần 7   | 🔄 Đang làm       | Đã tạo SQL UC 343 & rà code/JSP còn lại ngày 25/11; còn chạy `GearZone_UC343_budget_tier_rules.sql` + test trên Tomcat/SQL Server |

---

## Phụ lục – Danh sách 85 usecase của linhhqth08598 (Đối chiếu thực tế)

**A. Tài khoản (1–10):** Đăng ký (✅) · Đăng nhập (✅) · Đăng xuất (✅) · Xác thực email (✅) · Xác thực SĐT (✅) · Khôi phục mật khẩu (✅) · Đổi mật khẩu (✅) · Cập nhật thông tin cá nhân (🔄 test) · Cập nhật địa chỉ giao hàng (🔄 đã thêm Tên đường, cần test) · Đổi ảnh đại diện (🔄 test)

**B (11–18):** Lịch sử mua linh kiện (✅) · Xem wishlist (✅) · Thêm wishlist (✅) · Xóa wishlist (✅) · Lịch sử cấu hình PC (✅) · Lưu cấu hình PC (✱ code xong, test) · Chia sẻ cấu hình qua link (✱ code xong, test) · Đăng ký báo giảm giá (✅)

**C (285, 288–295):** Gửi thông báo tiến độ bảo hành (✅) · Tiếp nhận yêu cầu đổi trả (✅) · Kiểm tra điều kiện đổi trả (✅) · Phê duyệt (✅) · Từ chối (✅) · Tiếp nhận hàng hoàn trả (✅) · Ghi nhận tình trạng hàng hoàn (✅) · Xử lý hoàn tiền (✅) · Theo dõi trạng thái hoàn tiền (✅)

**D (296–302):** Đánh giá chất lượng (✅) · Chấm điểm hiệu năng (✅) · Viết nhận xét (✅) · Đính kèm hình ảnh (✅) · Đánh giá cấu hình PC (✅) · Đánh giá dịch vụ lắp ráp (✅) · Báo cáo thông tin sai (✅)

**E (304–310):** Yêu cầu tư vấn cấu hình (✅) · Yêu cầu tư vấn nâng cấp (✅) · Tiếp nhận khiếu nại (✅) · Phản hồi khiếu nại (✅) · Yêu cầu hỗ trợ kỹ thuật (✅) · Tiếp nhận yêu cầu hỗ trợ (✅) · Tra cứu lịch sử hỗ trợ (✅)

**F (326–334):** Tạo nhân viên (✅) · Cập nhật nhân viên (✅) · Phân quyền bán hàng (✅) · Phân quyền kho (✅) · Phân quyền kỹ thuật (✅) · Phân quyền tư vấn cấu hình (✅) · Phân quyền CSKH (✅) · Khóa tài khoản (✅) · Mở khóa tài khoản (✅)

**G (335–343):** Danh mục thương hiệu (✅) · socket CPU (✅) · chipset Mainboard (✅) · chuẩn RAM (✅) · chuẩn ổ cứng (✅) · Quy tắc tương thích (✅) · Ngưỡng công suất nguồn (✅✱) · Tiêu chí hiệu năng Gaming (✅✱) · Tiêu chí đề xuất theo ngân sách (✅✱ bảng `budget_tier_rules`)

**H (344):** Theo dõi lịch sử thao tác nhân viên (✅)

**I (346–367):** Thống kê linh kiện theo thương hiệu (✅✱) · còn hàng (✅✱) · hết hàng (✅✱) · bán chạy (✅) · bán chậm (✅✱) · doanh thu theo danh mục (✅) · theo thương hiệu (✅✱) · theo thời gian (✅✱) · lợi nhuận theo linh kiện (✅✱) · số cấu hình PC (✅✱) · cấu hình được chọn nhiều (✅✱) · cấu hình theo ngân sách (✅✱) · linh kiện hay thay thế (✅✱) · số lần không tương thích (✅✱) · tỷ lệ vượt ngân sách (✅✱) · đơn thành công (✅✱) · đơn hủy (✅✱) · số yêu cầu bảo hành (✅✱) · tỷ lệ bảo hành (✅✱) · hiệu quả khuyến mãi (✅✱) · hiệu quả tư vấn (✅✱) · hiệu quả lắp ráp (✅✱)

**J (368–370):** Xuất báo cáo doanh thu Excel (✅) · Xuất báo cáo tồn kho Excel (✅) · Xuất báo cáo kinh doanh theo thời gian (✅✱)


> **Chú thích:** ✅ = đã có code đầy đủ · ✱ = mới viết xong, đã biên dịch nhưng chưa test trên web · 🔄 = có code nhưng còn việc/cần test · ☐ = chưa làm (chờ phối hợp).
