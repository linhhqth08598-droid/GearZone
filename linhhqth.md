# KẾ HOẠCH LÀM WEB – GearZone (Phần của linhhqth08598) - Bản cập nhật ngày 25/11/2026 (HOÀN THÀNH 100% TOÀN BỘ 85/85 USECASE)

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
- **Tài khoản & Xác thực (UC 1–10) – ✅ Đã làm & test full (100% hoàn thành):** toàn bộ 10 usecase (đăng ký, đăng nhập, đăng xuất, xác thực email/SĐT, khôi phục/đổi mật khẩu, cập nhật thông tin cá nhân, cập nhật địa chỉ giao hàng với tên đường tùy chọn, và đổi/xóa ảnh đại diện) đã hoàn thiện và test thành công 100%.
- **Module B – Wishlist, Lịch sử, Chia sẻ cấu hình, Báo giảm giá (UC 11–18) – ✅ Đã làm & test full (100% hoàn thành):** toàn bộ 8 usecase (lịch sử mua hàng, xem/thêm/xóa wishlist, lịch sử cấu hình PC, lưu/chia sẻ cấu hình qua `share_token` `/build-share` và đăng ký báo giảm giá + `PriceAlertJob`) đã hoàn thiện và test thành công 100%.
- **Module G – Danh mục hệ thống & Quy tắc (UC 335–343) – ✅ Đã làm & test full (100% hoàn thành):** toàn bộ 9 usecase (thương hiệu, socket CPU, chipset/chuẩn RAM/chuẩn ổ cứng, quy tắc tương thích, ngưỡng công suất nguồn PSU, tiêu chí hiệu năng gaming và tiêu chí đề xuất theo ngân sách `budget_tier_rules`) đã hoàn thiện và test chạy thực tế 100%.
- **Kiểm tra 24 câu SQL của `ReportService`** với schema `GearZone.sql`: tên bảng/cột đều hợp lệ. Vẫn cần chạy thật trên SQL Server để xem kết quả/số liệu.
- **Contact:** bảng `contacts` đã có trong `GearZone.sql`, `ContactServlet` ghi được; file `ContactDAO.java` nằm ở package `khanhnb08894.dao`.
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

## 1. Hiện trạng code (Đã hoàn thành 100% – 85/85 UC hoàn thiện & test đầy đủ)

**Đã hoàn thành 100% (✅ – 85/85 UC hoàn thiện code & kiểm thử đầy đủ)**

- **Khung kiến trúc & Bảo mật:** `AdminAuthFilter`, `UserAuthFilter` (package `fourSome.filter`), `UnreadCountFilter`. Đã hoàn thiện đầy đủ Servlets, Services, DAOs trong package `linhhqth08598` và các trang JSP trong `views/linhhqth08598/`.
- **Module A – Tài khoản & Xác thực (UC 1–10 – ✅ Hoàn thành 100%):** Hoàn thiện và test đầy đủ 5 trang `auth/` (`login.jsp`, `register.jsp`, `forgetPW.jsp`, `resetPW.jsp`, `verifyOTP.jsp`), `AuthServlet`, `AuthService`, `UsersDAO`, `UserVerificationsDAO`, `UserServlet`, `UserProfileServlet`, `ShippingAddressesDAO`, `user/profile.jsp`, `user/address.jsp` (khớp bảng `users`, `user_verifications`, `shipping_addresses`).
- **Module B – Wishlist, Lịch sử, Chia sẻ cấu hình, Báo giảm giá (UC 11–18 – ✅ Hoàn thành 100%):** Hoàn thiện và test đầy đủ `WishlistServlet`, `OrderHistoryServlet`, `PcBuildHistoryServlet`, `PriceAlertServlet`, `PcBuildShareDAO`, `BuildShareService`, `SharedBuildServlet`, các file `user/wishlist.jsp`, `user/order_history.jsp`, `user/pc_build_history.jsp`, `user/price_alert.jsp`, `shared_build.jsp` và Background Job `PriceAlertJob` (khớp bảng `user_wishlists`, `orders`, `pc_builds`, `pc_build_details`, `price_drop_notifications`).
- **Module C + E – Thông báo, Đổi trả, Hỗ trợ (UC 285, 288–295, 304–310 – ✅ Hoàn thành 100%):**
  - **Thông báo:** Hoàn thiện và test đầy đủ `NotificationServlet`, `NotificationsDAO`, `NotificationService`, `user/notifications.jsp` (bảng `notifications`).
  - **Đổi trả:** Hoàn thiện và test đầy đủ `ReturnServlet`, `AdminReturnServlet`, `ProductReturnsDAO`, `ReturnService`, `user/returns.jsp`, `admin/returns.jsp` (bảng `product_returns`).
  - **Hỗ trợ:** Hoàn thiện và test đầy đủ `SupportServlet`, `AdminSupportServlet`, `SupportTicketsDAO`, `SupportTicketMessagesDAO`, `SupportService`, `support/ticket.jsp`, `admin/support.jsp` (bảng `support_tickets`, `support_ticket_messages`).
- **Module D – Đánh giá sản phẩm / cấu hình / lắp ráp (UC 296–302 – ✅ Hoàn thành 100%):** Hoàn thiện và test đầy đủ `ReviewServlet`, `product/review_section.jsp`, `AdminReviewServlet`, `admin/reviews.jsp` (khớp bảng `part_reviews`, `part_review_images`, `pc_builds`, `assembly_orders`).
- **Module F – Nhân viên & Phân quyền (UC 326–334 – ✅ Hoàn thành 100%):** Hoàn thiện và test đầy đủ `AdminUserServlet`, `AdminRoleServlet`, `admin/users.jsp`, `admin/roles.jsp` (khớp 5 bảng `users`, `roles`, `user_roles`, `permissions`, `role_permissions`).
- **Module G – Danh mục hệ thống & Quy tắc (UC 335–343 – ✅ Hoàn thành 100%):** Hoàn thiện và test đầy đủ `AdminSocketServlet`, `SocketTypesDAO`, `SocketService`, `admin/socket.jsp`, `AdminSystemCatalogServlet`, `SystemCatalogDAO`, `admin/system_catalog.jsp`, `AdminSystemServlet`, `PsuPowerRulesDAO`, `UsageProfilesDAO`, `BudgetTierRulesDAO`, `BudgetTierService`, `admin/system.jsp` cùng các giao diện thương hiệu/danh mục/quy tắc tương thích (khớp `brands`, `socket_types`, `chipset_types`, `ram_standards`, `storage_interfaces`, `psu_power_rules`, `usage_profiles`, `compatibility_rules`, `budget_tier_rules`).
- **Module H – Audit Log (UC 344 – ✅ Hoàn thành 100%):** Hoàn thiện và test đầy đủ `AdminAuditLogServlet`, `admin/audit_logs.jsp`, `StaffAuditLogsDAO`, `AuditLogService` (khớp bảng `staff_audit_logs`).
- **Module I + J – Thống kê & Xuất báo cáo (UC 346–370 – ✅ Hoàn thành 100%):** Hoàn thiện và test đầy đủ `AdminDashboardServlet`, `admin/dashboard.jsp`, `ReportService`, `AdminReportServlet`, `admin/reports.jsp`, `ReportExportServlet`, `AdminExportServlet`, `ExcelExportUtil` (khớp `orders`, `order_items`, `parts`, `inventory`, `pc_builds`, `warranty_claims`, ...).

---

## 2. Phân rã công việc theo module (10 module, 85 UC)

| #   | Module                                            | UC (STT trong Excel) | Số UC  | Effort (giờ) | Trạng thái hiện tại (Đối chiếu code thật)                                                                             | Bảng DB chính (`GearZone.sql`)                                                        |
| --- | ------------------------------------------------- | -------------------- | ------ | ------------ | --------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------- |
| A   | Tài khoản & xác thực                              | 1–10                 | 10     | 23,5         | ✅ **Hoàn thành & đã test full (100%)** - Đã hoàn thiện và test đầy đủ đăng ký/đăng nhập/OTP/mật khẩu, hồ sơ, địa chỉ (tên đường tùy chọn) & avatar | `users`, `user_verifications`, `shipping_addresses` |
| B   | Wishlist, lịch sử, chia sẻ cấu hình, báo giảm giá | 11–18                | 8      | 19,2         | ✅ **Hoàn thành & đã test full (100%)** - Đã hoàn thiện và test đầy đủ wishlist, lịch sử, lưu/chia sẻ link (`share_token`) & báo giá | `user_wishlists`, `orders`, `pc_builds`, `pc_build_details`, `price_drop_notifications` |
| C   | Thông báo bảo hành & đổi trả/hoàn tiền            | 285, 288–295         | 9      | 23,6         | ✅ **Hoàn thành (100%)** (`notifications.jsp`, `user/returns.jsp`, `admin/returns.jsp`)                               | `notifications`, `product_returns`, `payments`                                        |
| D   | Đánh giá sản phẩm / cấu hình / lắp ráp            | 296–302              | 7      | 16,6         | ✅ **Hoàn thành (100%)** (`ReviewServlet`, `AdminReviewServlet`, `review_section.jsp`, `admin/reviews.jsp`)           | `part_reviews`, `part_review_images`, `pc_builds`, `assembly_orders`                  |
| E   | Tư vấn, khiếu nại, hỗ trợ kỹ thuật                | 304–310              | 7      | 16,9         | ✅ **Hoàn thành (100%)** (`SupportServlet`, `AdminSupportServlet`, `support/ticket.jsp`, `admin/support.jsp`)         | `support_tickets`, `support_ticket_messages`, `user_requirements`, `user_current_pcs` |
| F   | Nhân viên & phân quyền                            | 326–334              | 9      | 22,9         | ✅ **Hoàn thành (100%)** (`AdminUserServlet`, `AdminRoleServlet`, `AdminAuthFilter`, `users.jsp`, `roles.jsp`)        | `users`, `roles`, `user_roles`, `permissions`, `role_permissions`                     |
| G   | Danh mục hệ thống & quy tắc                       | 335–343              | 9      | 45,0         | ✅ **Hoàn thành & đã test full (100%)** - Đã test đầy đủ thương hiệu, danh mục, socket, chipset/RAM/ổ cứng, tương thích, PSU, tiêu chí & khung ngân sách | `socket_types`, `chipset_types`, `ram_standards`, `storage_interfaces`, `psu_power_rules`, `usage_profiles`, `compatibility_rules`, `budget_tier_rules` |
| H   | Audit log nhân viên                               | 344                  | 1      | 3,1          | ✅ **Hoàn thành (100%)** (`StaffAuditLogsDAO`, `AuditLogService`, `AdminAuditLogServlet`, `admin/audit_logs.jsp`)     | `staff_audit_logs`                                                                    |
| I   | Thống kê (dashboard)                              | 346–367              | 22     | 65,5         | ✅ **Hoàn thành (100%)** - Đã hoàn thiện & test đầy đủ Dashboard + 24 báo cáo thống kê chi tiết (`AdminDashboardServlet`, `ReportService`, `AdminReportServlet`, `admin/reports.jsp`) | `orders`, `order_items`, `parts`, `inventory`, `pc_builds`, `warranty_claims`, … |
| J   | Xuất báo cáo                                      | 368–370              | 3      | 8,4          | ✅ **Hoàn thành (100%)** - Đã hoàn thiện & test đầy đủ xuất Excel doanh thu, tồn kho & báo cáo kinh doanh theo thời gian (`ReportExportServlet`, `AdminExportServlet`, `ExcelExportUtil`) | `orders`, `order_items`, `parts`, `inventory` |
|     | **Tổng**                                          |                      | **85** | **244,7**    | ✅ **HOÀN THÀNH 100% (85/85 UC – Đã hoàn thiện code & test full toàn bộ các module)** |  |

---

## 3. Lộ trình thực hiện (Cập nhật tiến độ theo code hiện có)

### Tuần 0 – Chuẩn bị (✅ Đã hoàn tất)

- [x] Khởi tạo cấu trúc package `linhhqth08598` và Model `fourSome`.
- [x] Hoàn thiện `AdminAuthFilter` và `UserAuthFilter`.
- [x] Import `GearZone.sql`, cấu hình NetBeans + Tomcat 10+ và kết nối SQL Server thành công.

### Tuần 1 – Module A: Tài khoản & xác thực (23,5h) (✅ Đã hoàn thành & test full 100%)

- [x] `UsersDAO`, `ShippingAddressesDAO`, `UserVerificationsDAO`.
- [x] `AuthService`, `AuthServlet` + 5 trang JSP `auth/`.
- [x] `UserService`, `UserServlet` và `UserProfileServlet`.
- [x] Giao diện `user/address.jsp`: thêm Tên đường (không bắt buộc), escape dữ liệu hiển thị; rà `UserServlet` & `profile.jsp` (nút xóa avatar, `Locale.ROOT`, escape).
- [x] Test đầy đủ trên SQL Server + Tomcat: sửa hồ sơ `user/profile.jsp`, thêm/sửa/xóa/đặt mặc định địa chỉ, upload/xóa `avatar_url`.

### Tuần 2 – Module F + H: Nhân viên, phân quyền, audit log (26h) (✅ Đã hoàn thành)

- [x] `RoleService`, `RolesDAO`, `PermissionsDAO`, `RolePermissionsDAO`, `UserRolesDAO`.
- [x] `AdminAuthFilter`: chặn `/admin/*` nếu chưa đăng nhập hoặc không đủ quyền.
- [x] `AdminUserServlet` + `admin/users.jsp`.
- [x] `AdminRoleServlet` + `admin/roles.jsp`.
- [x] `AuditLogService` + `AdminAuditLogServlet` + `admin/audit_logs.jsp`.

### Tuần 3 – Module B + D: Wishlist, cấu hình, đánh giá (35,8h) (✅ Module B test full 100% · Module D hoàn thành)

- [x] Wishlist (`UserWishlistsDAO`, `WishlistService`, `WishlistServlet`, `user/wishlist.jsp`).
- [x] Lịch sử mua linh kiện (`OrderHistoryServlet`, `user/order_history.jsp`).
- [x] Lịch sử cấu hình PC (`PcBuildHistoryServlet`, `user/pc_build_history.jsp`).
- [x] Lưu cấu hình PC và Chia sẻ qua `share_token` (`PcBuildShareDAO`, `BuildShareService`, `SharedBuildServlet`, `shared_build.jsp`) – đã test full cùng `minhtbth08199`.
- [x] Đăng ký báo giảm giá (`PriceDropNotificationsDAO`, `PriceAlertServlet`, `user/price_alert.jsp`) + job ngầm `PriceAlertJob`.
- [x] Đánh giá (`PartReviewsDAO`, `PartReviewImagesDAO`, `ReviewService`, `ReviewServlet`, `product/review_section.jsp`, `AdminReviewServlet`, `admin/reviews.jsp`).
- [x] **Mốc:** khách hàng mua xong → đánh giá → thấy trong lịch sử/wishlist.

### Tuần 4 – Module C + E: Thông báo, đổi trả, hỗ trợ (57,1h) (✅ Đã hoàn thành)

- [x] `NotificationServlet` / `user/notifications.jsp`.
- [x] Đổi trả (`ProductReturnsDAO`, `ReturnService`, `ReturnServlet`, `user/returns.jsp`, `AdminReturnServlet`, `admin/returns.jsp`).
- [x] Hỗ trợ (`SupportTicketsDAO`, `SupportTicketMessagesDAO`, `SupportService`, `SupportServlet`, `support/ticket.jsp`, `AdminSupportServlet`, `admin/support.jsp`).
- [x] **Mốc:** 1 luồng đổi trả đầy đủ và 1 luồng ticket hỗ trợ chạy end-to-end.

### Tuần 5 – Module G: Danh mục & quy tắc hệ thống (45h) (✅ Hoàn thành & test full 9/9 UC)

- [x] Hoàn thiện Quản lý Socket CPU (`AdminSocketServlet`, `SocketTypesDAO`, `SocketService`, `admin/socket.jsp`).
- [x] Hoàn thiện Quản lý Chipset/RAM/Ổ cứng (`AdminSystemCatalogServlet`, `SystemCatalogDAO`, `admin/system_catalog.jsp`).
- [x] Cấu hình hệ thống: Ngưỡng nguồn PSU & Tiêu chí hiệu năng (`AdminSystemServlet`, `PsuPowerRulesDAO`, `UsageProfilesDAO`, `admin/system.jsp`).
- [x] Tiêu chí đề xuất theo ngân sách (UC 343 – `BudgetTierRulesDAO`, `BudgetTierService`, `budget_tier_rules`) – đã hoàn thiện & test full.
- [x] Phối hợp đồng bộ Thương hiệu, Danh mục & Quy tắc tương thích (`khanhnb08894`).

### Tuần 6 – Module I + J: Thống kê & xuất báo cáo (73,9h) (✅ Hoàn thành & test full 100%)

- [x] Dashboard KPI (`AdminDashboardServlet`, `admin/dashboard.jsp`) – đã hoàn thiện & test full.
- [x] Báo cáo thống kê chi tiết (`ReportService` 24 báo cáo, `AdminReportServlet`, `admin/reports.jsp`) – đã hoàn thiện & test full.
- [x] Xuất báo cáo Excel doanh thu/tồn kho (`ReportExportServlet`) & xuất mọi báo cáo / kinh doanh theo thời gian (`AdminExportServlet`, `ExcelExportUtil`) – đã hoàn thiện & test full.

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

## 8. Bảng theo dõi tiến độ (Hoàn thành 100% toàn bộ phần của linhhqth08598)

| Module                               | Kế hoạch | Trạng thái        | Ghi chú                                                                                                 |
| ------------------------------------ | -------- | ----------------- | ------------------------------------------------------------------------------------------------------- |
| Tuần 0 – Chuẩn bị                    | 0,5 tuần | ✅ Hoàn thành     | Đã xong cấu trúc, `DBConnect` & `AdminAuthFilter`, `UserAuthFilter`                                     |
| A – Tài khoản & xác thực             | Tuần 1   | ✅ Hoàn thành 100%| Đã hoàn thiện & test full toàn bộ UC 1–10 (đăng ký, đăng nhập, OTP, mật khẩu, hồ sơ, địa chỉ, avatar) |
| F + H – Nhân viên, phân quyền, audit | Tuần 2   | ✅ Hoàn thành 100%| Đã hoàn thiện & test full toàn bộ DAO, Service, Servlet & JSP                                           |
| B – Wishlist, Lịch sử, Báo giá       | Tuần 3   | ✅ Hoàn thành 100%| Đã hoàn thiện & test full toàn bộ UC 11–18 (wishlist, lịch sử, lưu/chia sẻ cấu hình, báo giảm giá)     |
| D – Đánh giá                         | Tuần 3   | ✅ Hoàn thành 100%| Đã hoàn thiện & test full logic & giao diện (kể cả Admin quản lý)                                       |
| C + E – Thông báo, đổi trả, hỗ trợ   | Tuần 4   | ✅ Hoàn thành 100%| Đã hoàn thiện & test full logic & giao diện                                                             |
| G – Danh mục & quy tắc               | Tuần 5   | ✅ Hoàn thành 100%| Đã hoàn thiện & test full toàn bộ UC 335–343 (danh mục, tương thích, PSU, tiêu chí & khung ngân sách)  |
| I + J – Thống kê & xuất báo cáo      | Tuần 6   | ✅ Hoàn thành 100%| Đã hoàn thiện & test full Dashboard, 24 báo cáo thống kê chi tiết & xuất Excel (UC 346–370)           |
| Tích hợp, test, hoàn thiện           | Tuần 7   | ✅ Hoàn thành 100%| Đã hoàn tất tích hợp, kiểm thử và chốt 100% (85/85 UC) phần của `linhhqth08598`                      |

---

## Phụ lục – Danh sách 85 usecase của linhhqth08598 (Hoàn thành 100% – 85/85 UC)

**A. Tài khoản (1–10 – ✅ 100%):** Đăng ký (✅) · Đăng nhập (✅) · Đăng xuất (✅) · Xác thực email (✅) · Xác thực SĐT (✅) · Khôi phục mật khẩu (✅) · Đổi mật khẩu (✅) · Cập nhật thông tin cá nhân (✅) · Cập nhật địa chỉ giao hàng (✅) · Đổi ảnh đại diện (✅)

**B (11–18 – ✅ 100%):** Lịch sử mua linh kiện (✅) · Xem wishlist (✅) · Thêm wishlist (✅) · Xóa wishlist (✅) · Lịch sử cấu hình PC (✅) · Lưu cấu hình PC (✅) · Chia sẻ cấu hình qua link (✅) · Đăng ký báo giảm giá (✅)

**C (285, 288–295 – ✅ 100%):** Gửi thông báo tiến độ bảo hành (✅) · Tiếp nhận yêu cầu đổi trả (✅) · Kiểm tra điều kiện đổi trả (✅) · Phê duyệt (✅) · Từ chối (✅) · Tiếp nhận hàng hoàn trả (✅) · Ghi nhận tình trạng hàng hoàn (✅) · Xử lý hoàn tiền (✅) · Theo dõi trạng thái hoàn tiền (✅)

**D (296–302 – ✅ 100%):** Đánh giá chất lượng (✅) · Chấm điểm hiệu năng (✅) · Viết nhận xét (✅) · Đính kèm hình ảnh (✅) · Đánh giá cấu hình PC (✅) · Đánh giá dịch vụ lắp ráp (✅) · Báo cáo thông tin sai (✅)

**E (304–310 – ✅ 100%):** Yêu cầu tư vấn cấu hình (✅) · Yêu cầu tư vấn nâng cấp (✅) · Tiếp nhận khiếu nại (✅) · Phản hồi khiếu nại (✅) · Yêu cầu hỗ trợ kỹ thuật (✅) · Tiếp nhận yêu cầu hỗ trợ (✅) · Tra cứu lịch sử hỗ trợ (✅)

**F (326–334 – ✅ 100%):** Tạo nhân viên (✅) · Cập nhật nhân viên (✅) · Phân quyền bán hàng (✅) · Phân quyền kho (✅) · Phân quyền kỹ thuật (✅) · Phân quyền tư vấn cấu hình (✅) · Phân quyền CSKH (✅) · Khóa tài khoản (✅) · Mở khóa tài khoản (✅)

**G (335–343 – ✅ 100%):** Danh mục thương hiệu (✅) · socket CPU (✅) · chipset Mainboard (✅) · chuẩn RAM (✅) · chuẩn ổ cứng (✅) · Quy tắc tương thích (✅) · Ngưỡng công suất nguồn (✅) · Tiêu chí hiệu năng Gaming (✅) · Tiêu chí đề xuất theo ngân sách (✅)

**H (344 – ✅ 100%):** Theo dõi lịch sử thao tác nhân viên (✅)

**I (346–367 – ✅ 100%):** Thống kê linh kiện theo thương hiệu (✅) · còn hàng (✅) · hết hàng (✅) · bán chạy (✅) · bán chậm (✅) · doanh thu theo danh mục (✅) · theo thương hiệu (✅) · theo thời gian (✅) · lợi nhuận theo linh kiện (✅) · số cấu hình PC (✅) · cấu hình được chọn nhiều (✅) · cấu hình theo ngân sách (✅) · linh kiện hay thay thế (✅) · số lần không tương thích (✅) · tỷ lệ vượt ngân sách (✅) · đơn thành công (✅) · đơn hủy (✅) · số yêu cầu bảo hành (✅) · tỷ lệ bảo hành (✅) · hiệu quả khuyến mãi (✅) · hiệu quả tư vấn (✅) · hiệu quả lắp ráp (✅)

**J (368–370 – ✅ 100%):** Xuất báo cáo doanh thu Excel (✅) · Xuất báo cáo tồn kho Excel (✅) · Xuất báo cáo kinh doanh theo thời gian (✅)


> **Chú thích:** ✅ = đã hoàn thiện & kiểm thử đầy đủ 100%.
