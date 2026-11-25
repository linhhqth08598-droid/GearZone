# KẾ HOẠCH LÀM WEB – GearZone (Phần của dungntth08581) – Cập nhật tiến độ ngày 25/11/2026 (hoàn thiện & test thành công Module A, B, C, D, E & F; bổ sung gửi mail trạng thái đơn hàng & khuyến mãi)

| | |
|---|---|
| **Thành viên** | Nguyễn Tấn Dũng – TH08581 (package `dungntth08581`) |
| **Môn / Nhóm** | JAV102 – Nhóm 2 |
| **Công nghệ** | Java Servlet + JSP (Jakarta EE), SQL Server (JDBC), NetBeans, Controller → Service → DAO → Model |
| **Khối lượng được giao** | **98 usecase – 244,7 giờ (~30,6 ngày công, 25% dự án)** |

---

## 0. Tóm tắt tiến độ so với kế hoạch (đối chiếu code `GearZone.zip` + SQL + Excel, 20/11/2026)

| Chỉ số | Kết quả |
|---|---|
| UC đã có code | **98/98 (100%)** – A 7/7 · B 14/14 · C 15/15 · D 20/20 · E 16/16 · F 25/25 · G 1/1 |
| Đã test tay trên web | **98/98 (100%) – Test full toàn bộ Module A, B, C, D, E, F & G (kể cả mail trạng thái đơn hàng có màu phân biệt từng trạng thái & khuyến mãi)** |
| Effort đã có code | **244,7 / 244,7 giờ (100%)** |
| Còn lại | **0 UC chưa có code, 0 UC chờ test** – Toàn bộ 98/98 UC của Dũng đã hoàn thiện & test full |

**Tiến triển mới trong lần cập nhật này (20/11/2026):**
- **Hoàn thành code Module E – Bảo hành (16/16 UC, 36,5h):** `EWarrantiesDAO` (10,8 KB), `WarrantyClaimsDAO` (8,5 KB), `WarrantyClaimLogsDAO` (3,7 KB), `WarrantyService` (23,5 KB), `WarrantyServlet`, `PublicWarrantyServlet`, `AdminWarrantyServlet`, cùng 3 giao diện `support/warranty.jsp`, `user/warranty_claims.jsp`, `admin/warranties.jsp`.
- **Hoàn thành code Module G – Hỏi thông số kỹ thuật (1/1 UC – UC 303, 2,3h):** tích hợp gửi câu hỏi thông số kỹ thuật linh kiện vào `support_tickets` (`ticket_type = 'PART_QUESTION'`).
- **Tích hợp thanh toán PayOSUtil:** đã hoàn thiện `PayOSUtil` (15,9 KB) dùng chung trong `fourSome/util/`.

**Còn tồn & cần lưu ý kỹ thuật (đối chiếu `GearZone.sql`):**
- **Test Module D (20 UC) + Module E (16 UC) + UC 303 từ đầu đến cuối** trên dữ liệu thật.
- ⚠️ **Cảnh báo khớp ràng buộc DB cho Module E (`GearZone.sql`):** Bảng `warranty_claims` có `CHECK` constraint chỉ chấp nhận các trạng thái: `'PENDING'`, `'RECEIVED'`, `'APPROVED'`, `'AT_SERVICE_CENTER'`, `'REPAIRED'`, `'REPLACED'`, `'COMPLETED'`, `'REJECTED'`, `'CANCELLED'`. Lưu ý khi test UC 281 (*Chuyển linh kiện đến trung tâm bảo hành*), giá trị `status` trong code phải là `'AT_SERVICE_CENTER'` (không dùng `'SENT_TO_CENTER'` như câu SQL nháp trong file Excel).
- Đơn PayOS quá hạn (link sống 15 phút) **chưa có job tự hủy** để nhả kho giữ (`reserved_quantity`); hoàn tiền khi admin hủy đơn đã thanh toán online chưa xử lý tự động.
- Email xác nhận đơn (UC 234) và email khuyến mãi (UC 250) mới gửi thông báo trong app (`notifications`), **chưa kích hoạt gửi email** qua `EmailUtil`.
- **Module F (Nâng cấp PC – 25 UC, 64,5h) chưa làm:** `UpgradePCService` (67 bytes), `UpgradePCServlet` (724 bytes), `UpgradeApiServlet` (893 bytes), `buildpc/upgrade.jsp` (683 bytes) và `user/current_pc.jsp` (689 bytes) hiện vẫn là file khung rỗng.

---

## 1. Hiện trạng code (đối chiếu `GearZone-main.zip` ngày 20/11/2026)

### Đã hoàn thiện code nghiệp vụ & giao diện (73/98 UC)
- **Module A – Giỏ hàng & kiểm tra tồn kho (7/7 UC – ✅ Đã test):** `CartItemsDAO`, `CartService` (10,4 KB), `CartServlet` (5,2 KB), `checkout/cart.jsp` (8,1 KB).
- **Module B – Checkout, đơn hàng & thanh toán (14/14 UC – ✅ Đã test):** `OrdersDAO`, `OrderItemsDAO`, `PaymentsDAO`, `ShippingMethodsDAO`, `OrderService` (27,7 KB), `PaymentService` (13,9 KB), `ShippingService` (5,0 KB), `PayOSUtil` (15,9 KB), `CheckoutServlet`, `PaymentCallbackServlet`, `OrderServlet`, `AdminOrderServlet`, `AdminShippingServlet`, cùng các trang `checkout/payment.jsp`, `user/order_detail.jsp`, `user/invoice.jsp`, `admin/orders.jsp`, `admin/shipping.jsp`. *(Lưu ý: `user/orders.jsp` và `checkout/success.jsp` ~680 bytes nên kiểm tra xem có redirect sang trang lịch sử đơn của Linh hay không).*
- **Module C – Mã giảm giá & khuyến mãi (15/15 UC – ✅ Đã test):** `CouponsDAO`, `PromotionsDAO`, `ComboPromotionsDAO`, `PromotionService` (41,7 KB), `PromotionServlet`, `AdminPromotionServlet`, `promotion/list.jsp`, `admin/promotions.jsp`.
- **Module D – Dịch vụ lắp ráp PC (20/20 UC – ✅ Đã có code & đã test):** `AssemblyOrdersDAO`, `AssemblyPhotosDAO`, `AssemblyTestReportsDAO`, `AssemblyService` (45,1 KB), `AssemblyServlet`, `AdminAssemblyServlet`, `AssemblyMediaServlet`, `user/assembly.jsp`, `user/assembly_register.jsp`, `user/assembly_detail.jsp`, `admin/assembly.jsp`, `admin/assembly_detail.jsp`.
- **Module E – Bảo hành (16/16 UC – ✅ Đã có code & đã test):** `EWarrantiesDAO` (10,8 KB), `WarrantyClaimsDAO` (8,5 KB), `WarrantyClaimLogsDAO` (3,7 KB), `WarrantyService` (23,5 KB), `WarrantyServlet` (3,0 KB), `PublicWarrantyServlet` (1,4 KB), `AdminWarrantyServlet` (7,0 KB), `support/warranty.jsp` (5,9 KB), `user/warranty_claims.jsp` (7,3 KB), `admin/warranties.jsp` (13,6 KB).
- **Module G – Hỏi thông số kỹ thuật (1/1 UC – ✅ Đã có code & đã test):** UC 303 tạo ticket `PART_QUESTION` trong `support_tickets`.

### Hoàn thành 100% (98/98 UC đã có code & đã test full)
- **Module F – Nâng cấp PC (25/25 UC – ✅ Đã có code & đã test full):** `UserCurrentPcsDAO`, `UpgradeRecommendationsDAO`, `UpgradePCService`, `UpgradePCServlet`, `views/minhtbth08199/user/current_pc.jsp` và `views/minhtbth08199/buildpc/upgrade.jsp` đã hoàn thiện và test full luồng khai báo/lấy từ lịch sử build → phân tích nghẽn → chọn gói & tạo cấu hình → thêm giỏ hàng.
- **Email trạng thái đơn hàng & khuyến mãi (UC 234, 250 – ✅ Đã có code & đã test full):** Gửi mail bất đồng bộ qua `EmailUtil` với giao diện HTML phân biệt màu badge theo từng trạng thái đơn (`PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`) và thông báo khuyến mãi.

---

## 2. Phân rã tiến độ theo module (7 module, 98 UC)

| # | Module | UC (STT trong Excel) | Số UC | Effort (giờ) | Bảng DB chính | Trạng thái (20/11/2026) |
|---|---|---|---|---|---|---|
| A | Giỏ hàng & kiểm tra tồn kho | 209, 216–221 | 7 | 13,8 | `cart_items`, `inventory`, `compatibility_rules` | ✅ **7/7** (Đã test) |
| B | Checkout, đơn hàng, thanh toán | 222–235 | 14 | 48,7 | `orders`, `order_items`, `payments`, `shipping_methods`, `shipping_addresses`, `coupons` | ✅ **14/14** (Đã test thật PayOS) |
| C | Mã giảm giá & khuyến mãi | 236–250 | 15 | 39,0 | `coupons`, `promotions`, `combo_promotions` | ✅ **15/15** (Đã test; chờ test lại combo tự trừ) |
| D | Dịch vụ lắp ráp PC | 251–270 | 20 | 39,9 | `assembly_orders`, `assembly_photos`, `assembly_test_reports`, `part_serials` | ✅ **20/20** (Đã có code & đã test) |
| E | Bảo hành | 271–287 | 16 | 36,5 | `e_warranties`, `warranty_claims`, `warranty_claim_logs`, `part_serials` | ✅ **16/16** (Đã có code & đã test) |
| F | Nâng cấp PC | 171–196 (trừ 179) | 25 | 64,5 | `user_current_pcs`, `upgrade_recommendations`, `parts`, `spec_*` | ✅ **25/25** (Đã có code & đã test) |
| G | Hỏi thông số kỹ thuật | 303 | 1 | 2,3 | `support_tickets` | ✅ **1/1** (Đã có code & đã test) |
| | **Tổng** | | **98** | **244,7** | **98/98 UC (100%) đã có code & đã test full** | **244,7h / 244,7h (100%)** |

---

## 3. Chi tiết tiến độ thực hiện theo từng giai đoạn

### Tuần 0 – Chuẩn bị (✅ Hoàn thành)
- [x] Import `GearZone.sql`, cấu hình `DBConnect` (HikariCP), build project NetBeans + Tomcat 10+.
- [x] Tích hợp **PayOS** (`PayOSUtil` + `payos.properties`), test tạo link & webhook thực tế thành công.
- [x] Test gửi mail thực tế qua `EmailUtil` (UC 234, 250 – đã gửi & test thành công với badge màu phân biệt từng trạng thái đơn hàng).

### Tuần 1 – Module A + B (phần 1): Giỏ hàng, đặt hàng (≈ 35h) (✅ 100%)
- [x] Thêm/xóa/cập nhật số lượng giỏ hàng (`CartItemsDAO`, `CartService`, `CartServlet`, `cart.jsp` – UC 216–218).
- [x] Kiểm tra tồn kho khi thêm vào giỏ và trước khi thanh toán (UC 209, 220) qua `inventory`.
- [x] Kiểm tra tương thích linh kiện trong giỏ hàng (UC 219 – `CartService.checkCompatibility`).
- [x] Thêm toàn bộ linh kiện cấu hình PC vào giỏ (UC 221 – `CartService.addBuildToCart`, dùng chung với Minh UC 98).
- [x] Chọn phương thức giao hàng, địa chỉ nhận hàng, tính phí vận chuyển và tổng tiền (UC 222–223, 225–226).

### Tuần 2 – Module B (phần 2): Xác nhận đơn & thanh toán (≈ 25h) (✅ 14/14)
- [x] Áp mã giảm giá (UC 224) – gọi `PromotionService`, phân biệt rõ khuyến mãi/combo tự động với mã nhập tay.
- [x] Xác nhận đặt hàng (UC 227): tạo `orders` + `order_items`, giữ tồn kho qua `InventoryService` (transaction commit/rollback).
- [x] Thanh toán COD (UC 229) và **Thanh toán online PayOS + ghi nhận kết quả (UC 228, 230)**: kiểm tra chữ ký HMAC-SHA256, xử lý idempotent, sync trạng thái khi khách quay về, nút thanh toán lại.
- [x] Hủy đơn hàng & hoàn tồn kho (UC 231), tra cứu trạng thái đơn (UC 232), xác nhận đã nhận hàng (UC 233).
- [x] Gửi thông báo xác nhận đơn trong app (UC 234) và xuất hóa đơn HTML in được (UC 235 – `user/invoice.jsp`).
- [x] Quản lý đơn hàng & vận chuyển phía Admin (`AdminOrderServlet`, `AdminShippingServlet`, `admin/orders.jsp`, `admin/shipping.jsp`).
- [x] Đã test gửi email trạng thái đơn hàng (UC 234) thực tế với badge màu phân biệt từng trạng thái (`PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`).

### Tuần 3 – Module C: Mã giảm giá & khuyến mãi (39h) (✅ 15/15)
- [x] CRUD mã giảm giá, cập nhật giá trị, thời gian, điều kiện danh mục/thương hiệu, đơn tối thiểu (UC 236–240).
- [x] Chương trình giảm giá theo danh mục/thương hiệu/linh kiện, miễn phí vận chuyển (UC 241–242, 245).
- [x] Thiết lập & tự động áp dụng giảm giá Combo (`PART_COMBO`) và trọn bộ PC (`FULL_BUILD`) khi đặt hàng (UC 243–244).
- [x] Kiểm tra điều kiện & hạn dùng mã (UC 246–247), tra cứu lịch sử dùng mã (UC 248), hiển thị linh kiện đang khuyến mãi (UC 249 – `promotion/list.jsp`), gửi thông báo khuyến mãi hàng loạt trong app (UC 250).
- [x] Cung cấp hàm `PromotionService.preview(...)` phục vụ tính tổng chi phí sau khuyến mãi (UC 170).
- [x] Đã tích hợp & test gửi email khuyến mãi hàng loạt (UC 250) qua `EmailUtil`.

### Tuần 4 – Module D: Dịch vụ lắp ráp PC (39,9h) (✅ 20/20 đã có code & đã test)
- [x] Đăng ký dịch vụ lắp ráp, chọn hình thức tại cửa hàng / giao PC tận nơi, đặt & xác nhận lịch (UC 251–255).
- [x] Phân công kỹ thuật viên, ghi linh kiện & serial sử dụng, cập nhật tiến độ `CONFIRMED → IN_PROGRESS → TESTING` (UC 256–258), ghi `staff_audit_logs`.
- [x] Ghi nhận kết quả lắp ráp và 7 hạng mục kiểm tra kỹ thuật (khởi động, nhiệt CPU/GPU nghỉ–tải, độ ổn định, Cinebench, 3DMark – UC 259–266) vào `assembly_test_reports`.
- [x] Upload ảnh PC hoàn thiện (UC 267 – `AssemblyMediaServlet`, `assembly_photos`), xác nhận hoàn tất & bàn giao `COMPLETED → HANDED_OVER` (UC 268).
- [x] Tiếp nhận yêu cầu vệ sinh PC & thay keo tản nhiệt (UC 269–270 – luồng rút gọn).
- [x] Đã test end-to-end toàn bộ 20 UC của Module D trên DB thật.

### Tuần 5 – Module E + G: Bảo hành & Hỏi thông số kỹ thuật (38,8h) (✅ Module E 16/16 & Module G UC 303 đã test full)
- [x] Thiết lập thời hạn bảo hành theo danh mục/linh kiện (UC 271 – `parts.warranty_months`).
- [x] Phát hành phiếu bảo hành điện tử (UC 272 – `e_warranties`, sinh mã bảo hành gắn với serial & đơn hàng).
- [x] Tra cứu thời hạn bảo hành công khai theo mã sản phẩm & số serial (UC 273–274 – `PublicWarrantyServlet`, `support/warranty.jsp`).
- [x] Tiếp nhận yêu cầu bảo hành, ghi tình trạng tiếp nhận, kiểm tra điều kiện, duyệt hoặc từ chối kèm lý do (UC 275–280 – `WarrantyServlet`, `AdminWarrantyServlet`, `user/warranty_claims.jsp`, `admin/warranties.jsp`).
- [x] Chuyển trung tâm bảo hành, cập nhật tiến độ xử lý, ghi kết quả sửa chữa / đổi mới (kèm serial mới), xác nhận hoàn tất (UC 281–284, 286), lưu vết đầy đủ vào `warranty_claim_logs` và gọi `NotificationService` (UC 285).
- [x] Tra cứu lịch sử bảo hành linh kiện (UC 287).
- [x] Đã test end-to-end toàn bộ vòng đời phiếu bảo hành & yêu cầu bảo hành Module E (xác nhận trạng thái `AT_SERVICE_CENTER` khớp `CHECK` constraint DB).
- [x] Gửi câu hỏi về thông số kỹ thuật linh kiện (UC 303 – tạo `support_tickets` loại `PART_QUESTION`, đã test full).

### Tuần 6–7 – Module F: Nâng cấp PC (64,5h) (✅ Đã triển khai code & test end-to-end thành công)
- [x] **Bước 1 – Khai báo PC hiện tại (UC 171–178, 18,9h):** Hoàn thiện `UserCurrentPcsDAO` (`findById`, `findActiveByUserId`, `saveCurrentPc` transaction vô hiệu hóa bản ghi cũ, lưu `current_ram_quantity` & `upgrade_budget`), hỗ trợ chọn nhanh từ lịch sử build và lưu vào `user_current_pcs`.
- [x] **Bước 2 – Kiểm tra khả năng nâng cấp & tương thích (UC 180–185, 10,9h):** Xác định linh kiện giữ lại; kiểm tra tương thích/nguồn qua `CompatibilityService` trong `UpgradePCService`.
- [x] **Bước 3 – Phân tích nghẽn cổ chai & đề xuất 3 phương án (UC 186–191, 21,3h):** Phân tích điểm nghẽn CPU/VGA (`gaming_performance_score`) và sinh 3 gói (`SAVING`, `BALANCED`, `HIGH_PERFORMANCE`) qua `UpgradeRecommendationsDAO`.
- [x] **Bước 4 – So sánh, lưu cấu hình & đẩy vào giỏ (UC 192–196, 13,4h):** Tạo cấu hình mới `after_build_id` trong `pc_builds` + `pc_build_details`, đánh dấu `markSelected`, đẩy linh kiện vào `cart_items` qua `CartService` và hoàn thiện giao diện `buildpc/upgrade.jsp` + `UpgradePCServlet`.
- [x] **Kiểm thử End-to-End:** Đã test thành công luồng khai báo/lấy từ lịch sử build → phân tích nghẽn → chọn gói & tạo cấu hình → thêm vào giỏ hàng trên `user_current_pcs`, `upgrade_recommendations`, `pc_builds`, `cart_items`.

---

## 4. Checklist an toàn & chất lượng

- [x] Webhook PayOS: kiểm tra chữ ký HMAC-SHA256, xử lý idempotent, đối chiếu số tiền thanh toán.
- [x] Giá bán, giảm giá, phí ship và tổng tiền đơn hàng luôn tính lại ở phía server (`OrderService`, `PromotionService`).
- [x] Đặt hàng, thanh toán, hủy đơn sử dụng transaction JDBC (`setAutoCommit(false)` / `commit` / `rollback`).
- [x] Kiểm tra quyền sở hữu (`user_id`) khi khách xem đơn hàng, lịch lắp ráp, phiếu/yêu cầu bảo hành; thao tác admin ghi `staff_audit_logs`.
- [x] Upload ảnh lắp ráp kiểm tra định dạng và dung lượng file (`FileUploadUtil`, `Constants`).
- [x] Toàn bộ truy vấn dùng `PreparedStatement`; cấu hình nhạy cảm tách ra `payos.properties` (không commit khóa thật lên Git).

---

## 5. Phối hợp với thành viên khác

| Thành viên | Nội dung phối hợp | Trạng thái |
|---|---|---|
| **Hà Quang Linh** (`linhhqth08598`) | Dùng chung `loggedUser`, `AdminAuthFilter`, `NotificationService` (UC 234, 250, 285), `SupportService` (UC 303), `ShippingAddressesDAO` (UC 223); cung cấp `OrdersDAO` cho lịch sử mua hàng (UC 11) | ✅ Đã tích hợp |
| **Nguyễn Bá Khánh** (`khanhnb08894`) | Gọi `InventoryService` giữ/hoàn kho khi đặt/hủy đơn (UC 210–211), `CompatibilityService` (UC 219 & sắp tới cho Module F), đọc dữ liệu `parts`, `spec_*`, `part_serials` | 🔄 Đã tích hợp A–E; tiếp tục dùng cho Module F |
| **Trịnh Bình Minh** (`minhtbth08199`) | Dùng chung hàm thêm cấu hình PC vào giỏ (UC 98/221), hàm tính tổng sau khuyến mãi `PromotionService.preview` (UC 170); đọc `upgrade_budget` (UC 179) cho Module F | 🔄 Đã tích hợp A–C; phối hợp view `buildpc/upgrade.jsp` & `user/current_pc.jsp` |

---

## Phụ lục – Danh sách 98 usecase của dungntth08581

- **A – Giỏ hàng (7 UC – ✅):** 209 (kiểm tra tồn kho trước đặt), 216 (thêm giỏ), 217 (xóa giỏ), 218 (cập nhật số lượng), 219 (kiểm tra tương thích giỏ), 220 (kiểm tra tồn kho khi thanh toán), 221 (thêm cả build vào giỏ).
- **B – Đơn hàng & Thanh toán (14 UC – ✅):** 222 (phương thức giao), 223 (địa chỉ nhận), 224 (áp mã giảm giá), 225 (tính tổng tiền), 226 (tính phí ship), 227 (xác nhận đặt hàng), 228 (thanh toán online PayOS), 229 (thanh toán COD), 230 (ghi nhận kết quả thanh toán), 231 (hủy đơn), 232 (tra cứu trạng thái), 233 (xác nhận đã nhận), 234 (thông báo xác nhận đơn), 235 (xuất hóa đơn).
- **C – Khuyến mãi & Mã giảm giá (15 UC – ✅):** 236 (tạo mã), 237 (cập nhật giá trị), 238 (thời gian áp dụng), 239 (điều kiện áp dụng), 240 (đơn tối thiểu), 241 (giảm theo danh mục), 242 (giảm theo thương hiệu), 243 (giảm combo), 244 (ưu đãi trọn bộ PC), 245 (miễn phí vận chuyển), 246 (kiểm tra điều kiện mã), 247 (kiểm tra hạn mã), 248 (lịch sử dùng mã), 249 (hiển thị linh kiện khuyến mãi), 250 (gửi thông báo khuyến mãi).
- **D – Lắp ráp PC (20 UC – ✅ test full):** 251 (đăng ký lắp ráp), 252 (lắp tại cửa hàng), 253 (giao PC lắp sẵn), 254 (đặt lịch), 255 (xác nhận lịch), 256 (phân công KTV), 257 (ghi linh kiện sử dụng), 258 (cập nhật tiến độ), 259 (ghi kết quả lắp ráp), 260 (test khởi động), 261 (test nhiệt CPU), 262 (test nhiệt GPU), 263 (test ổn định), 264 (test hiệu năng CPU), 265 (test hiệu năng GPU), 266 (ghi kết quả kiểm tra), 267 (chụp ảnh PC hoàn thiện), 268 (bàn giao PC), 269 (yêu cầu vệ sinh PC), 270 (yêu cầu thay keo tản nhiệt).
- **E – Bảo hành (16 UC – ✅ test full):** 271 (thiết lập thời hạn BH), 272 (phát hành phiếu BH điện tử), 273 (tra cứu theo mã SP), 274 (tra cứu theo serial), 275 (tiếp nhận yêu cầu BH), 276 (ghi tình trạng tiếp nhận), 277 (kiểm tra điều kiện BH), 278 (xác nhận đủ điều kiện), 279 (từ chối yêu cầu), 280 (ghi lý do từ chối), 281 (chuyển trung tâm BH), 282 (cập nhật tiến độ), 283 (ghi kết quả sửa chữa), 284 (ghi kết quả đổi mới), 286 (xác nhận hoàn tất BH), 287 (tra cứu lịch sử BH). *(Lưu ý: UC 285 thuộc Hà Quang Linh).*
- **F – Nâng cấp PC (25 UC – ✅ test full):** 171 (khai báo PC hiện tại), 172 (nhập CPU), 173 (nhập Mainboard), 174 (nhập RAM), 175 (nhập VGA), 176 (nhập PSU), 177 (nhập Case), 178 (nhập ổ cứng), 180 (linh kiện dùng tiếp), 181 (check nâng cấp CPU), 182 (check nâng cấp RAM), 183 (check nâng cấp VGA), 184 (check công suất nguồn), 185 (check lưu trữ), 186 (xác định linh kiện cần thay), 187 (nguy cơ nghẽn cổ chai), 188 (phân tích điểm nghẽn), 189 (đề xuất nâng cấp phù hợp), 190 (đề xuất tiết kiệm), 191 (đề xuất hiệu năng cao), 192 (so sánh hiệu năng trước–sau), 193 (so sánh chi phí), 194 (tính tổng chi phí nâng cấp), 195 (lưu cấu hình trước–sau), 196 (tạo danh sách linh kiện cần mua). *(Lưu ý: UC 179 thuộc Trịnh Bình Minh).*
- **G – Tư vấn kỹ thuật (1 UC – ✅ test full):** 303 (gửi câu hỏi về thông số kỹ thuật linh kiện).

> **Chú thích:** ✅ = đã hoàn thiện & test full 100%. Cập nhật ngày 25/11/2026.
