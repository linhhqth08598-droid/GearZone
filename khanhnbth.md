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
- [ ] Chốt với nhóm: session `loggedUser`, quy ước URL, layout, **chữ ký các hàm service dùng chung**.
- [ ] Chuẩn bị dữ liệu mẫu: ít nhất 5–10 sản phẩm/loại linh kiện, có đủ cột `spec_*` để test tương thích.
- [ ] Tạo nhánh Git `khanhnb08894`.

### Tuần 1 – Module A (phần 1): Danh mục & sản phẩm (≈ 30h)
- [x] `CategoriesDAO`, `BrandsDAO`, `ProductSeriesDAO` + `CatalogService`: thêm danh mục, sửa tên, thêm thương hiệu và thêm dòng sản phẩm (`product_series`) (UC 19–22).
- [x] `PartsDAO` + 10 `Spec*DAO` + `SpecService` + `ProductService` (đã triển khai các hàm tìm/lọc/đọc spec/thêm/sửa/đổi trạng thái).
- [x] `AdminProductServlet` + `products.jsp`, `product_form.jsp`: cập nhật giá/trạng thái bán (`ACTIVE`/`HIDDEN`/`DISCONTINUED`), ghi nhật ký thao tác admin (`UPDATE_PRODUCT` vào `staff_audit_logs`) và đã test ẩn/hiện/cập nhật linh kiện trên web.
- [ ] Rà và hoàn thiện thêm đầy đủ các trường `spec_*` khi cập nhật (`updateProduct`).

### Tuần 2 – Module A (phần 2) + hiển thị cho khách (≈ 29h)
- [x] `UploadApiServlet` (`/api/upload`): hỗ trợ upload ảnh (`<= 10MB`), video (`<= 50MB`) hoặc link media vào bảng `part_media` và đặt ảnh chính (`is_primary`) (UC 34–35); đã test `GET` và `POST` thành công.
- [ ] Cập nhật thông số (UC 33), giá & giá khuyến mãi (UC 36–37), tra cứu theo mã sản phẩm (UC 40).
- [x] `ProductServlet` + `list.jsp` + `detail.jsp`: đã có bộ lọc đa tiêu chí, sắp xếp và phân trang (`PaginationUtil`).

### Tuần 3 – Module B: Kiểm tra tương thích (53,6h)
- [x] `CompatibilityService` đã có engine kiểm tra nhiều cặp linh kiện, áp dụng quy tắc động `compatibility_rules`, gợi ý thay thế, hỗ trợ `checkBuild(List<Integer>)` và ghi log xung đột vào `compatibility_conflict_logs`.
- [x] `CompatibilityApiServlet` + `AdminCompatibilityServlet` + `compatibility.jsp`: đã kết nối kiểm tra tương thích JSON và quản lý quy tắc; đã test thêm quy tắc (`SOCKET_MATCH`, `RAM_DDR_MATCH`, `CASE_VGA_LENGTH`, `CASE_COOLER_HEIGHT`, `COOLER_SOCKET`) và gọi `/api/compatibility`.

### Tuần 4 – Module C: Lọc, sắp xếp, so sánh (47,3h)
- [x] `ProductService.filterAndSortParts(...)` + `ProductServlet` + `list.jsp`: câu SQL lọc động + sắp xếp + phân trang giữ tham số (UC 131–156).
- [x] `GameService` + `AdminGameServlet` + `games.jsp` + `CompareServlet` + `compare.jsp` + mục sidebar **So sánh linh kiện**: so sánh theo từng loại danh mục, hiệu năng/giá và FPS benchmark VGA (UC 158–160); đã test thêm game & benchmark trên `/admin/game`.

### Tuần 5 – Module D: Quản lý kho (49,4h)
- [x] `InventoryService` + `AdminInventoryServlet` + `inventory.jsp` + `AdminInventoryTransactionsServlet`: đã có `getAvailable`, `reserve`, `release`, `exportStock`, `importStock`, điều chỉnh kiểm kê/lỗi/thất thoát (`adjustStock`), cảnh báo sắp hết/hết hàng (`getLowOrOutOfStockItems`), tồn lâu ngày (`getAgingStockItems`), thống kê tồn kho theo danh mục (`getCategoryStockSummary` – UC 345), luồng Serial cơ bản và đã cập nhật header/sidebar/footer admin cho trang Lịch sử biến động kho.
- [ ] Gắn hiển thị cảnh báo sắp hết/hết hàng & tồn lâu ngày lên giao diện và test các kịch bản PO/Serial chưa test.

### Tuần 6 – Module E: Nhà cung cấp & đơn nhập (35,1h)
- [x] `SupplierService`, `AdminSupplierServlet`, `AdminPurchaseServlet`, `AdminPurchaseDetailServlet`, `AdminPurchaseReturnsServlet`, `purchases.jsp` và `supplier_returns.jsp` đã kết nối quản lý NCC, PO, mốc thời gian chốt/nhận hàng, lịch sử giao dịch theo NCC và phiếu trả hàng NCC.
- [ ] Test toàn bộ quy trình nhập, đối chiếu số lượng và trạng thái trả hàng trên DB thật.

### Tuần 7 – Hoàn thiện (buffer)
- [ ] Tích hợp với Minh/Dũng/Linh, test toàn bộ 114 UC, dọn code, chuẩn bị demo.

---

## 4. Bảng theo dõi tiến độ

| Module | Kế hoạch | Trạng thái | Ghi chú |
|---|---|---|---|
| Tuần 0 – Chuẩn bị | 0,5 tuần | ◐ Một phần | Build/DB đã chạy; còn chốt nhóm & branch |
| A1 – Danh mục, thêm sản phẩm | Tuần 1 | 🔄 Gần xong | Đã cập nhật/test trạng thái & audit log sản phẩm; còn rà full spec khi sửa |
| A2 – Cập nhật & hiển thị sản phẩm | Tuần 2 | ◐ Một phần | Đã làm & test `UploadApiServlet` + lọc/sắp xếp/phân trang |
| B – Tương thích | Tuần 3 | 🔄 Gần xong | Đã làm & test quy tắc động + `/api/compatibility` |
| C – Lọc, sắp xếp, so sánh | Tuần 4 | 🔄 Gần xong | Đã làm & test `/admin/game`, so sánh theo loại & bộ lọc/phân trang |
| D – Kho | Tuần 5 | 🔄 Gần xong | Đã thêm layout admin cho lịch sử biến động kho, cảnh báo & tồn lâu ngày |
| E – NCC & đơn nhập | Tuần 6 | ◐ Một phần | Đã làm `SupplierService`, lịch sử giao dịch PO, phiếu trả hàng NCC (chờ test) |
| Tích hợp, test, hoàn thiện | Tuần 7 | ☐ | |
