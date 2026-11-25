# Tiến độ & Kế hoạch – GearZone (`khanhnb08894`)

| | |
|---|---|
| **Thành viên** | Nguyễn Bá Khánh – TH08894 (package `khanhnb08894`) |
| **Môn / Nhóm** | JAV102 – Nhóm 2 |
| **Công nghệ** | Java Servlet + JSP (Jakarta EE), SQL Server (JDBC), NetBeans, Controller → Service → DAO → Model |
| **Khối lượng được giao** | **114 usecase – 244,6 giờ (~30,6 ngày công, ~25% dự án)** |

---

## 1. Hiện trạng code (cập nhật 25/11/2026)

**Đã có sẵn & đã cập nhật**
- Model cho toàn bộ bảng, util: `FileUploadUtil`, `PaginationUtil`, `FormatUtil`, `Validator`, `Constants`.
- 26 DAO + `PurchaseOrderDetailDTO`: `PartsDAO`, `CategoriesDAO`, `BrandsDAO`, `ProductSeriesDAO`, `PartMediaDAO`, `PartSerialsDAO`, 10 bảng thông số (`SpecCpuDAO`, `SpecMainboardDAO`, `SpecRamDAO`, `SpecVgaDAO`, `SpecStorageDAO`, `SpecPsuDAO`, `SpecCaseDAO`, `SpecCoolerDAO`, `SpecMonitorDAO`, `SpecPeripheralDAO`), `CompatibilityConflictLogsDAO`, `ContactDAO`, `GamesDAO`, `GameFpsBenchmarksDAO`, `InventoryDAO`, `InventoryTransactionsDAO`, `SuppliersDAO`, `PurchaseOrdersDAO`, `PurchaseOrderDetailsDAO`, `SupplierReturnsDAO`.
- 18 Servlet (`HomeServlet`, `ProductServlet`, `CompareServlet`, `CompatibilityApiServlet`, `UploadApiServlet`, `ContactServlet`, `MediaServlet`, `AdminProductServlet`, `AdminCatalogServlet`, `AdminCompatibilityServlet`, `AdminGameServlet`, `AdminInventoryServlet`, `AdminInventoryTransactionsServlet`, `AdminPartSerialsServlet`, `AdminPurchaseServlet`, `AdminPurchaseDetailServlet`, `AdminPurchaseReturnsServlet`, `AdminSupplierServlet`) và 7 Service (`ProductService`, `CatalogService`, `CompatibilityService`, `InventoryService`, `SupplierService`, `GameService`, `SpecService`).
- 16 JSP trong `web/views/khanhnb08894/`: `home.jsp`, `contact.jsp`, `product/` (`list.jsp`, `detail.jsp`, `compare.jsp`), `admin/` (`products.jsp`, `product_form.jsp`, `catalog_brand.jsp`, `catalog_category.jsp`, `compatibility.jsp`, `games.jsp`, `inventory.jsp`, `purchases.jsp`, `purchase_form.jsp`, `suppliers.jsp`, `supplier_returns.jsp`).

**Lưu ý phân công**
1. Phần của bạn là **“xương sống dữ liệu”**: sản phẩm, tương thích, tồn kho. Minh (Build PC), Dũng (giỏ hàng/nâng cấp) và Linh (thống kê) đều gọi vào `CompatibilityService`, `InventoryService`, `ProductService` → **nên làm đầu tiên và công bố hàm sớm**.
2. Danh mục thương hiệu, socket, chipset, chuẩn RAM… (UC 335–339) và quy tắc tương thích (UC 340) là của **Linh**. `CompatibilityRulesDAO` **đã chuyển sang package của Linh**; bạn `import` để **đọc** quy tắc khi kiểm tra tương thích. UC 337–339 (chipset, chuẩn RAM, chuẩn ổ cứng) hiện không có bảng riêng, chỉ là cột trong `spec_*` → cần chốt với Linh.
3. `DBConnect` đang hard-code IP `26.62.109.68`, `sa/123` → dùng cấu hình DB chung của nhóm.
4. `PartReviewsDAO`, `PartReviewImagesDAO`, `ProductReturnsDAO` **đã chuyển sang Linh** (UC đánh giá/đổi trả của Linh). Khi làm lọc theo đánh giá (UC 152) và hiển thị đánh giá ở `detail.jsp`, bạn gọi `PartReviewsDAO` của Linh. **Quy ước DAO:** DAO của bảng nào thuộc người Thêm/Sửa bảng đó, người khác chỉ `import`.

---

## 2. Phân rã công việc theo module (5 module, 114 UC)

| # | Module | UC (STT trong Excel) | Số UC | Effort (giờ) | Bảng DB chính |
|---|---|---|---|---|---|
| A | Danh mục, thương hiệu, sản phẩm & thông số | 19–40 | 22 | 59,2 | `categories`, `brands`, `product_series`, `parts`, `spec_*`, `part_media` |
| B | Kiểm tra tương thích | 41–69 | 29 | 53,6 | `compatibility_rules`, `compatibility_conflict_logs`, `psu_power_rules`, `spec_*` |
| C | Lọc, sắp xếp, so sánh | 131–156, 158–160 | 29 | 47,3 | `parts`, `spec_*`, `part_reviews`, `game_fps_benchmarks` |
| D | Quản lý kho | 197–208, 210–215, 345 | 19 | 49,4 | `inventory`, `inventory_transactions`, `parts`, `orders` |
| E | Nhà cung cấp & đơn nhập hàng | 311–325 | 15 | 35,1 | `suppliers`, `purchase_orders`, `purchase_order_details`, `supplier_returns` |
| | **Tổng** | | **114** | **244,6** | |

---

## 3. Lộ trình thực hiện (theo tuần – cập nhật 25/11/2026)

### Tuần 0 – Chuẩn bị (≈ 0,5 tuần)
- [x] Import `GearZone.sql`, chạy được `DBConnect`, build NetBeans + Tomcat 10+.
- [ ] Chốt với nhóm: session `loggedUser`, quy ước URL, layout, **chữ ký các hàm service dùng chung** (xem mục 6).
- [ ] Chuẩn bị dữ liệu mẫu: ít nhất 5–10 sản phẩm/loại linh kiện, có đủ cột `spec_*` (CPU–Mainboard cùng socket, RAM DDR4/DDR5…) để test tương thích.
- [ ] Tạo nhánh Git `khanhnb08894`.

### Tuần 1 – Module A (phần 1): Danh mục & sản phẩm (≈ 30h)
- [x] `CategoriesDAO`, `BrandsDAO`, `ProductSeriesDAO` + `CatalogService`: thêm danh mục, sửa tên, thêm thương hiệu (UC 19–22).
- [x] `PartsDAO` + 10 `Spec*DAO` + `SpecService` + `ProductService` (đã triển khai các hàm tìm/lọc/đọc spec/thêm/sửa/đổi trạng thái).
- [ ] `AdminProductServlet` + `products.jsp`, `product_form.jsp`; `AdminCatalogServlet` + `categories.jsp`: rà và hoàn thiện đầy đủ các trường `spec_*` khi cập nhật.

### Tuần 2 – Module A (phần 2) + hiển thị cho khách (≈ 29h)
- [ ] Cập nhật thông số (UC 33), cập nhật ảnh & video (UC 34–35), giá & giá khuyến mãi (UC 36–37), công khai/ẩn linh kiện (UC 38–39), tra cứu theo mã sản phẩm (UC 40).
- [x] `ProductServlet` + `list.jsp` + `detail.jsp`: đã có bộ lọc đa tiêu chí, sắp xếp và phân trang (`PaginationUtil`).

### Tuần 3 – Module B: Kiểm tra tương thích (53,6h)
- [x] `CompatibilityService` đã có engine kiểm tra nhiều cặp linh kiện, gợi ý thay thế và ghi log xung đột.
- [ ] Rà và đối chiếu đầy đủ UC 41–69 với `compatibility_rules`, `psu_power_rules`, `CompatibilityApiServlet` và `AdminCompatibilityServlet` + `compatibility.jsp`.

### Tuần 4 – Module C: Lọc, sắp xếp, so sánh (47,3h)
- [x] `ProductService.filterAndSortParts(...)` + `ProductServlet` + `list.jsp`: câu SQL lọc động theo danh mục, thương hiệu, giá, từ khóa, còn hàng, khuyến mãi, đánh giá, socket, chipset/GPU, chuẩn RAM, dung lượng RAM, VRAM tối thiểu + sắp xếp + phân trang giữ tham số (UC 131–156).
- [x] `GameService` + `AdminGameServlet` + `games.jsp` + `CompareServlet` + `compare.jsp` + mục sidebar **So sánh linh kiện**: so sánh theo từng loại danh mục, hiệu năng/giá và FPS benchmark VGA (UC 158–160).

### Tuần 5 – Module D: Quản lý kho (49,4h)
- [x] `InventoryService` + `AdminInventoryServlet` + `inventory.jsp`: đã có `getAvailable`, `reserve`, `release`, `exportStock`, `importStock`, điều chỉnh kiểm kê/lỗi/thất thoát (`adjustStock`), thống kê tồn kho theo danh mục (`getCategoryStockSummary` – UC 345) và luồng Serial cơ bản.
- [ ] Bổ sung cảnh báo sắp hết/hết hàng tự động, tồn lâu ngày và test các kịch bản PO/Serial chưa test.

### Tuần 6 – Module E: Nhà cung cấp & đơn nhập (35,1h)
- [x] `SupplierService`, `AdminSupplierServlet`, `AdminPurchaseServlet`, `AdminPurchaseDetailServlet`, `AdminPurchaseReturnsServlet` và `supplier_returns.jsp` đã kết nối quản lý NCC, PO và phiếu trả hàng NCC.
- [ ] Hoàn thiện/test toàn bộ quy trình nhập, đối chiếu số lượng, lịch sử giao dịch NCC và trạng thái trả hàng.

### Tuần 7 – Hoàn thiện (buffer)
- [ ] Tích hợp với Minh/Dũng/Linh, test toàn bộ 114 UC, dọn code, chuẩn bị demo.

---

## 4. Khuôn làm mỗi usecase

1. **DAO** – `PreparedStatement`, test riêng bằng `main`.
2. **Service** – nghiệp vụ, validate, transaction khi đổi nhiều bảng (kho, đơn nhập).
3. **Servlet** – nhận tham số → service → `forward` JSP; POST thành công thì `redirect`. Servlet API trả JSON thì set `Content-Type: application/json; charset=UTF-8`.
4. **JSP** – JSTL/EL, include `header.jsp`/`footer.jsp`, `<c:out>` chống XSS.
5. **Test tay** – 1 case đúng + 1–2 case lỗi (hết hàng, socket lệch, thiếu thông số).
6. **Commit** – 1 usecase / 1 commit (`feat(UC41): kiểm tra CPU-Mainboard`).

## 5. Checklist an toàn & chất lượng

- [x] SQL lọc động dùng `PreparedStatement` và whitelist `ORDER BY`.
- [ ] Upload: đuôi `jpg/jpeg/png/webp/gif`, video `mp4/webm/mov`; giới hạn 10MB / 50MB (`Constants`).
- [x] Thay đổi tồn kho qua `InventoryService` (có transaction và ghi `inventory_transactions`).
- [ ] `/admin/*` qua `AdminAuthFilter` (Linh), thao tác admin ghi `staff_audit_logs`.
- [ ] Xóa mềm bằng `status` thay vì xóa cứng với `parts`, `suppliers`.
- [x] Phân trang `PaginationUtil` trên danh sách sản phẩm khách hàng.

## 6. Hàm dùng chung cần công bố sớm (cho 3 bạn kia)

| Hàm | Ai gọi | Hạn |
|---|---|---|
| `CompatibilityService.checkBuild(...)` + `CompatibilityApiServlet` | Minh (Build PC, đề xuất), Dũng (giỏ hàng UC 219, nâng cấp) | Cuối Tuần 3 |
| `InventoryService.reserve(partId, qty)` / `release(...)` / `getAvailable(...)` | Dũng (đặt hàng, hủy đơn, kiểm tra tồn) | Cuối Tuần 3 (bản tạm), hoàn chỉnh Tuần 5 |
| `ProductService.findById/search(...)`, `getSpec(...)` | Minh, Dũng, Linh (thống kê, wishlist) | Cuối Tuần 2 |
| Cấu trúc bảng `compatibility_rules` | Linh (UC 340 – cấu hình quy tắc) | Tuần 0 |

## 7. Rủi ro & cách giảm

| Rủi ro | Cách xử lý |
|---|---|
| Dữ liệu `spec_*` thiếu → không test được tương thích | Chuẩn bị dữ liệu mẫu ở Tuần 0 |
| 29 UC tương thích rất dễ sót | Lập bảng test (mỗi UC 1 cặp linh kiện đúng + 1 cặp sai) |
| Cả nhóm chờ hàm của bạn | Phát hành **bản tạm trả dữ liệu cứng** sớm, thay thật sau |
| Câu lọc động phức tạp, chậm | Index cột hay lọc (`price`, `brand_id`, `category_id`), phân trang ở SQL |
| Trùng/lệch với bảng danh mục của Linh | Chốt cấu trúc bảng trước khi code (Tuần 0) |

## 8. Bảng theo dõi tiến độ

| Module | Kế hoạch | Trạng thái | Ghi chú |
|---|---|---|---|
| Tuần 0 – Chuẩn bị | 0,5 tuần | ◐ Một phần | Build/DB đã chạy; còn chốt nhóm & branch |
| A1 – Danh mục, thêm sản phẩm | Tuần 1 | ◐ Một phần | `CatalogService`, `ProductService` đã viết; cần rà đủ spec/form |
| A2 – Cập nhật & hiển thị sản phẩm | Tuần 2 | ◐ Một phần | `ProductServlet` + `list.jsp` đã có lọc/sắp xếp/phân trang; cần rà cập nhật thông số/media |
| B – Tương thích | Tuần 3 | ◐ Một phần | Đã có engine kiểm tra; cần rà đủ UC 41–69 & quy tắc DB |
| C – Lọc, sắp xếp, so sánh | Tuần 4 | 🔄 Gần xong | Đã làm lọc/sắp xếp/phân trang + `GameService` + `AdminGameServlet` + `CompareServlet` + `compare.jsp` |
| D – Kho | Tuần 5 | ◐ Một phần | Đã có `InventoryService.adjustStock`, thống kê danh mục & luồng Serial; còn cảnh báo tự động/tồn lâu ngày |
| E – NCC & đơn nhập | Tuần 6 | ◐ Một phần | Đã làm `SupplierService`, `AdminPurchaseReturnsServlet`, `supplier_returns.jsp` (chờ test) |
| Tích hợp, test, hoàn thiện | Tuần 7 | ☐ | |

---

## Phụ lục – Danh sách 114 usecase của khanhnb08894

**A (19–40):** Thêm danh mục · Sửa tên danh mục · Thêm thương hiệu · Thêm dòng sản phẩm · Thêm thông tin CPU / Mainboard / RAM / VGA / SSD-HDD / PSU / Case / tản nhiệt / màn hình / bàn phím-chuột · Cập nhật thông số kỹ thuật · Cập nhật hình ảnh · Thêm video · Cập nhật giá bán · Thiết lập giá khuyến mãi · Công khai / Ẩn linh kiện · Tra cứu theo mã sản phẩm

**B (41–69):** CPU–Mainboard · socket · chipset · thế hệ CPU · RAM–Mainboard · DDR4/DDR5 · tốc độ RAM · dung lượng RAM tối đa · số khe RAM · VGA–Mainboard · VGA–Case · tản nhiệt–Case · tản nhiệt–socket · Mainboard–Case · chuẩn PSU · tổng công suất · PSU đáp ứng · đầu cấp PCIe · đầu cấp Mainboard/CPU · chuẩn SSD · NVMe · khe M2 · màn hình–VGA · HDMI/DisplayPort · tương thích toàn cấu hình · phát hiện xung đột · hiển thị nguyên nhân · đề xuất thay thế · cảnh báo thiếu công suất

**C (131–156, 158–160):** Lọc theo giá · CPU (nhân/luồng, xung, socket) · Mainboard (chipset, socket, chuẩn RAM) · RAM (dung lượng, bus, DDR) · VGA (VRAM, hiệu năng, điện) · SSD (dung lượng, chuẩn) · PSU · Case · tản nhiệt · thương hiệu · còn hàng · giá khuyến mãi · đánh giá · Sắp xếp (giá tăng/giảm, hiệu năng, phổ biến) · So sánh hiệu năng CPU · VGA · hiệu năng/giá

**D (197–208, 210–215, 345):** Nhập lô · Ghi số lượng nhập · Ghi giá nhập · Cập nhật tồn · Điều chỉnh sau kiểm kê · Ghi lỗi/hư hỏng · Ghi thất thoát · Xác nhận đủ điều kiện kinh doanh · Trạng thái còn/hết hàng · Cảnh báo sắp hết/đã hết · Giữ tồn khi đặt hàng · Hoàn tồn khi hủy · Lịch sử nhập/xuất kho · Tồn khả dụng · Tồn lâu ngày · Thống kê theo danh mục

**E (311–325):** Thêm/Cập nhật nhà cung cấp · Ghi liên hệ · Phiếu đề nghị nhập · Đơn đặt hàng NCC · Xác nhận đơn · Thời gian giao dự kiến · Xác nhận tiếp nhận · Đối chiếu số lượng · Ghi nhập thiếu / sai chủng loại / hư hỏng · Yêu cầu trả hàng NCC · Theo dõi đơn nhập · Lịch sử giao dịch với NCC
