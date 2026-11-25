# KẾ HOẠCH LÀM WEB – GearZone (Phần của minhtbth08199)

| | |
|---|---|
| **Thành viên** | Trịnh Bình Minh – TH08199 (package `minhtbth08199`) |
| **Môn / Nhóm** | JAV102 – Nhóm 2 |
| **Công nghệ** | Java Servlet + JSP (Jakarta EE), SQL Server (JDBC), NetBeans, Controller → Service → DAO → Model |
| **Khối lượng được giao** | **73 usecase – 244,7 giờ (~30,6 ngày công, 25% dự án)** |

---

## 1. Hiện trạng code (cập nhật 25/11/2026)

### Đã triển khai theo code hiện có
- Build PC theo luồng Controller → Service → DAO: tạo cấu hình, chọn/thay/xóa linh kiện, cập nhật số lượng RAM và tính lại giá phía server; có modal tìm linh kiện cho 11 nhóm và nút **Bỏ vào giỏ hàng**.
- Có kiểm tra quyền sửa cấu hình theo tài khoản/phiên khách; build khách được giữ theo session. Giá và tồn kho linh kiện được đọc từ dữ liệu server.
- Có kiểm tra CPU/Mainboard (socket), RAM/Mainboard (DDR) và PSU với CPU/GPU; API Build trả cảnh báo tương thích và cập nhật `is_compatible`. Gợi ý lọc socket và DDR khi dựng build. Chưa có kiểm tra kích thước Case hoặc socket/TDP tản nhiệt.
- Form **Nhờ gợi ý** dùng nhu cầu tổng quát cho người không nhất thiết chơi game: mục đích, ngân sách, độ phân giải, Hz màn hình và dung lượng RAM lấy từ sản phẩm còn hàng, ưu tiên và RGB. Gợi ý lưu nhu cầu, dựng cấu hình rồi chuyển sang Build PC.
- Luồng gợi ý tự chọn CPU, Mainboard, RAM, VGA, SSD, HDD, PSU, tản nhiệt và màn hình; giữ đúng dung lượng RAM/Hz mục tiêu khi kho có sản phẩm phù hợp. Vỏ máy và phụ kiện để khách tự chọn.
- Build PC có thêm **Gợi ý nhanh theo game** (nối danh sách game từ bảng `games` / `/admin/game` và giữ fallback Valorant/LMHT/CS2) với mốc 15/20/25/30 triệu; thuật toán ghép sản phẩm đang có trong kho GearZone. Có liên kết tham khảo HACOM, không đồng bộ giá/sản phẩm trực tiếp từ HACOM.
- DAO có nền tạo/đọc build, chi tiết, tổng giá, token chia sẻ và xóa mềm; `PublicBuildServlet` đã có luồng đọc build theo token.

### Còn thiếu hoặc mới làm một phần
- Tương thích tự động hiện mới kiểm tra socket CPU/Mainboard, DDR RAM/Mainboard và công suất PSU cơ bản. Chưa kiểm tra kích thước Case/VGA, chiều cao tản nhiệt, socket/tản nhiệt, chuẩn NVMe/M.2 hay đầu nối nguồn. Build cho phép giữ lựa chọn không tương thích và hiển thị cảnh báo; trạng thái `is_compatible` được lưu lại.
- Gợi ý là một cấu hình theo quy tắc ngân sách và tồn kho; chưa dùng `game_fps_benchmarks`, chưa đưa ra 2–3 phương án, không có so sánh hiệu năng/FPS thực nghiệm. Nhãn cấu hình game không phải cam kết FPS.
- `saved_builds.jsp`, `current_pc.jsp`, `upgrade.jsp`, `public_view.jsp` hiện vẫn là trang khung. DAO/token/servlet chia sẻ mới là nền, chưa có trải nghiệm hoàn chỉnh để lưu lịch sử, xem cấu hình chia sẻ hay nâng cấp.
- Chưa thấy hoàn thiện sao chép/xuất danh sách, kiểm tra tồn kho cả cấu hình, phí lắp ráp, biểu đồ/phân tích ngân sách, thay thế linh kiện theo UC và áp khuyến mãi vào tổng build.
- Tình trạng biên dịch gần nhất trước các chỉnh sửa đang làm: đã từng biên dịch Java bằng JDK 21. Chưa xác nhận Clean and Build sau các thay đổi ngày 25/11 và chưa chạy kiểm thử end-to-end cho toàn bộ UC.

**Cách đọc trạng thái bên dưới:** `[x]` đã có chức năng trong code; `[~]` có một phần/nền nhưng còn thiếu yêu cầu; `[ ]` chưa triển khai hoặc chưa xác minh. Đây là đánh giá mã nguồn, không đồng nghĩa đã nghiệm thu đủ 73 UC.

## 2. Phân rã công việc theo module (4 module, 73 UC)

| # | Module | UC (STT trong Excel) | Số UC | Effort (giờ) | Bảng DB chính |
|---|---|---|---|---|---|
| A | Xây dựng cấu hình PC (Build PC) | 70–100 | 31 | 133,4 | `pc_builds`, `pc_build_details`, `parts`, `spec_*`, `inventory`, `cart_items` |
| B | Nhu cầu người dùng | 101–119 | 19 | 39,1 | `user_requirements`, `usage_profiles`, `user_requirement_games`, `games` |
| C | Đề xuất cấu hình | 120–130 | 11 | 42,5 | `pc_builds`, `usage_profiles`, `game_fps_benchmarks`, `parts` |
| D | Ngân sách & linh kiện thay thế | 157, 161–170, 179 | 12 | 29,7 | `pc_builds`, `parts`, `promotions` |
| | **Tổng** | | **73** | **244,7** | |

> Module A chiếm **54%** effort. 11 UC “Chọn linh kiện” (72–82) thực chất là **một giao diện chọn linh kiện dùng chung** cho 11 loại → viết một component, truyền loại linh kiện vào.

---

## 3. Lộ trình thực hiện (theo tuần – cập nhật theo code)

~35–40 giờ/tuần → khoảng 6–7 tuần. Trạng thái dưới đây đánh giá chức năng đã có, không xác nhận từng UC đã kiểm thử.

### Tuần 0 – Chuẩn bị (≈ 0,5 tuần)
- [~] Đã xác nhận cấu hình NetBeans/Tomcat, JDK 21 và đọc được dữ liệu kho; 256 file Java biên dịch thành công. Chưa chạy Clean and Build/deploy trong NetBeans.
- [~] Đã rà các tích hợp `loggedUser`, CartService và API tương thích/tồn kho trong source; chưa có xác nhận/phân ranh trực tiếp với các thành viên.
- [~] `custom.jsp` đã hiện thực giao diện Build PC; chưa có wireframe độc lập/duyệt với nhóm. Chưa tạo branch `minhtbth08199` vì môi trường từ chối ghi Git metadata `.git`; branch vẫn là `main`.

### Tuần 1 – Module A (phần 1): Khung Build PC (≈ 38h)
- [x] Có DAO/service cho tạo build, thêm/đổi/xóa chi tiết, đọc cấu hình và tính/cập nhật tổng giá.
- [x] Có `BuildPCServlet`, `BuildApiServlet`, `custom.jsp`, modal chọn linh kiện và 11 slot.
- [x] Có tích hợp nút thêm cấu hình vào giỏ hàng.
- [~] Luồng chính có trong code; đã kiểm tra trực tiếp trên DB bằng giao dịch rollback: tạo build, thêm CPU/Mainboard tương thích, tính tổng, xóa linh kiện và tính lại tổng; chưa kiểm thử servlet/JSP/cart end-to-end.

### Tuần 2 – Module A (phần 2): Chọn đủ 11 loại + tương thích (≈ 38h)
- [x] Có lựa chọn CPU, Mainboard, RAM, VGA, SSD, HDD, PSU, Case, tản nhiệt, màn hình, phụ kiện; RAM có số lượng.
- [~] Có cảnh báo socket, DDR RAM/Mainboard và công suất PSU cơ bản; thiếu nhiều quy tắc tương thích và chưa chặn lựa chọn không tương thích triệt để.
- [~] Có tiến độ các linh kiện bắt buộc; kiểm tra tồn kho theo từng sản phẩm có, chưa xác minh kiểm kê cả cấu hình.
- [ ] Chưa có kiểm tra Case/Mainboard/VGA và giới hạn chiều cao tản nhiệt; cần bổ sung trong package `minhtbth08199` khi schema/spec data cho phép.

### Tuần 3 – Module A (phần 3): Đề xuất tự động, lưu & xuất (≈ 38h)
- [x] Có thuật toán gợi ý linh kiện theo ngân sách/tồn kho; có token và servlet nền để đọc build chia sẻ; thêm cấu hình vào giỏ đã nối.
- [~] Tạo cấu hình gợi ý và chuyển sang Build PC hoạt động theo code; chưa có thay thế tương thích toàn diện.
- [ ] `saved_builds.jsp`, `current_pc.jsp`, `upgrade.jsp`, `public_view.jsp` còn trang khung; thiếu UI lịch sử/chia sẻ hoàn chỉnh và sao chép cấu hình.
- [ ] Xuất danh sách, kiểm kê toàn build và tính phí lắp ráp chưa hoàn tất.

### Tuần 4 – Module B: Nhu cầu người dùng (39,1h)
- [x] Form tổng quát có mục đích, ngân sách, độ phân giải, FPS/Hz, RAM, ưu tiên và RGB; dữ liệu nhu cầu được lưu.
- [~] Mức Hz và dung lượng RAM lấy từ hàng còn kho; luồng gợi ý dùng các lựa chọn này để dựng build.
- [ ] Chưa có wizard nhiều bước, chọn nhiều game từ `games`, mức đồ họa, phong cách, ưu tiên CPU/GPU và toàn bộ trường UC 101–119.
- [ ] Ngân sách nâng cấp UC 179 và phân tích nhu cầu đầy đủ chưa triển khai.

### Tuần 5 – Module C: Đề xuất cấu hình (42,5h)
- [x] Có gợi ý cấu hình tổng quát theo ngân sách và có gợi ý nhanh theo game (nối bảng `games` / `/admin/game` + mốc 15–30 triệu); chọn linh kiện đang có trong kho GearZone.
- [x] Form gợi ý giữ dung lượng RAM đã chọn và tìm màn hình theo Hz mục tiêu khi kho có sản phẩm phù hợp.
- [~] Có điều chỉnh ưu tiên CPU/GPU theo game và lọc tương thích cơ bản; chưa dùng `game_fps_benchmarks`.
- [ ] Chưa có 2–3 phương án, so sánh hiệu năng/FPS, nâng cấp tương lai và đề xuất thay thế giảm giá/tăng hiệu năng.

### Tuần 6 – Module D: Ngân sách & thay thế (29,7h)
- [ ] Chưa triển khai đầy đủ so sánh phân khúc, biểu đồ phân bổ, cảnh báo ngân sách và thay thế linh kiện UC 157, 161–170, 179.

### Tuần 7 – Hoàn thiện (buffer)
- [~] Có tích hợp một phần với `CompatibilityService` và `CartService`.
- [ ] Chưa xác nhận tích hợp hoàn chỉnh với các thành viên khác, kiểm thử 73 UC, tối ưu cuối và demo.

## 4. Khuôn làm mỗi usecase

1. **DAO** – `PreparedStatement`, test riêng bằng `main`.
2. **Service** – nghiệp vụ, validate; thao tác lưu build nhiều bảng dùng transaction.
3. **Servlet** – nhận tham số → service → `forward` JSP (hoặc trả JSON cho `BuildApiServlet`); POST thành công thì `redirect`.
4. **JSP + JS** – JSTL/EL, `fetch()` gọi API, escape dữ liệu hiển thị.
5. **Test tay** – 1 case đúng + 1–2 case lỗi (thiếu linh kiện, lệch socket, vượt ngân sách).
6. **Commit** – 1 usecase / 1 commit (`feat(UC85): tự động cập nhật tổng giá`).

## 5. Checklist an toàn & chất lượng

- [x] Giá linh kiện/tổng tiền được đọc và tính lại phía server khi thay đổi build.
- [~] Có kiểm tra chủ sở hữu/phiên khách khi thao tác build và token ngẫu nhiên để đọc chia sẻ; luồng chia sẻ chỉ đọc chưa có giao diện hoàn chỉnh để nghiệm thu.
- [x] Cột cập nhật động của `user_requirements` dùng whitelist.
- [~] DAO dùng truy vấn tham số hóa ở các luồng đã rà; cần rà toàn bộ project trước khi kết luận mọi SQL đều dùng `PreparedStatement`.
- [~] API có phản hồi lỗi tiếng Việt; phần escape JSON đã xử lý ký tự điều khiển ở endpoint Build, nhưng cần rà mã lỗi và escape nhất quán toàn bộ endpoint.
- [x] Có hỗ trợ khách chưa đăng nhập dựng cấu hình tạm theo session.
- [ ] Chưa có debounce cho các thao tác gọi API nếu người dùng đổi lựa chọn liên tục; cần kiểm tra/tối ưu trước khi chốt.

## 6. Phối hợp với thành viên khác

| Với ai | Nội dung | Khi nào |
|---|---|---|
| **Khánh** | `CompatibilityService.checkBuild` / `CompatibilityApiServlet`, hàm lọc & tìm linh kiện, `InventoryService.getAvailable` | Tuần 0, 2, 3 |
| **Dũng** | `CartService` (UC 98 = UC 221), `PromotionService.applyDiscount` (UC 170), UC 179 (ngân sách nâng cấp) | Tuần 0, 3, 6 |
| **Linh** | `loggedUser`; trang lịch sử/chia sẻ cấu hình gọi hàm của bạn; cấu hình ngưỡng PSU, tiêu chí hiệu năng/ngân sách (UC 341–343) | Tuần 0, 3, 5 |

## 7. Rủi ro & cách giảm

| Rủi ro | Cách xử lý |
|---|---|
| `custom.jsp` quá phức tạp, ngốn thời gian | Làm khung + 2 loại linh kiện trước, nhân bản; chốt wireframe từ Tuần 0 |
| Khánh chưa xong tương thích khi bạn cần | Dùng hàm giả trả `OK`, đổi sang thật khi Khánh phát hành |
| Thuật toán đề xuất cho kết quả kỳ quặc | Dùng quy tắc đơn giản (tỷ lệ ngân sách), thêm bộ cấu hình mẫu `is_preset_recommendation` làm dự phòng |
| Dữ liệu `game_fps_benchmarks` thiếu | Nhập tay 5–10 game mẫu sớm |
| Trùng việc với Linh/Dũng | Chốt phân ranh ở Tuần 0 (mục 1) |

## 8. Bảng theo dõi tiến độ

| Module | Kế hoạch | Trạng thái | Ghi chú |
|---|---|---|---|
| Tuần 0 – Chuẩn bị | 0,5 tuần | ◐ Một phần | Đã xác nhận config NetBeans/Tomcat, DB đọc được, 256 file Java compile và WAR thành công bằng Ant. Target Clean and Build mặc định còn lỗi Javadoc ở namespace khác; chưa deploy Tomcat/chốt trực tiếp với nhóm; không tạo được branch do `.git` read-only. |
| A1 – Khung Build PC | Tuần 1 | ◐ Một phần | DAO/service và giỏ hàng có; DB rollback xác nhận tạo, thêm CPU/Mainboard, tổng giá và xóa. Chưa kiểm thử servlet/JSP/cart end-to-end. |
| A2 – Chọn 11 loại + tương thích | Tuần 2 | ◐ Một phần | Đủ 11 nhóm; có socket/DDR/PSU cơ bản. Chưa có kiểm tra Case/VGA/tản nhiệt và các ràng buộc lưu trữ/đầu nối. |
| A3 – Đề xuất tự động, lưu, xuất | Tuần 3 | ◐ Một phần | Có đề xuất và token/servlet nền chia sẻ; các trang lịch sử, build hiện tại, nâng cấp, xem chia sẻ vẫn là khung; thiếu sao chép/xuất/kiểm kê/phí ráp. |
| B – Nhu cầu người dùng | Tuần 4 | ◐ Một phần | Có form tổng quát và lưu yêu cầu; Hz/RAM lấy từ kho. Chưa có wizard/game selector và đủ trường UC 101–119. |
| C – Đề xuất cấu hình | Tuần 5 | ◐ Một phần | Có cấu hình gợi ý tổng quát, quick build theo game (nối bảng `games`) ở mốc 15–30 triệu; cần rà đủ 7/7 bắt buộc, độ phân giải 2K, Hz/RAM & định dạng ngân sách. |
| D – Ngân sách & thay thế | Tuần 6 | ☐ Chưa hoàn tất | Chưa triển khai đầy đủ so sánh, phân bổ ngân sách, thay thế linh kiện và khuyến mãi UC 157, 161–170, 179. |
| Tích hợp, test, hoàn thiện | Tuần 7 | ◐ Một phần | Có tích hợp một phần CompatibilityService/CartService; compile JDK 21 từng thành công trước đây, chưa xác nhận build mới nhất hoặc test end-to-end 73 UC. |

### Nhật ký cập nhật 19/11/2026
- Đối chiếu kế hoạch với code trong `src/java/minhtbth08199`, các JSP build PC và servlet liên quan; trạng thái được ghi nhận theo phần đã có, phần một phần và phần còn thiếu.
- Ghi nhận form **Nhờ gợi ý** tổng quát vẫn phục vụ người không chơi game; khung **Gợi ý nhanh theo game** riêng trên trang Build PC có Valorant, LMHT, CS2 và mốc 15–30 triệu.
- Ghi nhận `CompatibilityService` đang được giữ nguyên; không tiếp tục sửa file ngoài `minhtbth08199`. Các kiểm tra kích thước Case và socket/TDP tản nhiệt vẫn chưa có.
- Xác nhận các trang `saved_builds.jsp`, `current_pc.jsp`, `upgrade.jsp`, `public_view.jsp` hiện vẫn là trang khung, dù đã có một phần DAO/servlet phía sau.
- Biên dịch thành công 256 file Java bằng JDK 21; chưa Clean and Build/deploy Tomcat hoặc kiểm thử end-to-end. Không đánh dấu hoàn thành toàn bộ usecase chỉ từ bước biên dịch.

## Phụ lục – Danh sách 73 usecase của minhtbth08199

**A (70–100):** Cảnh báo vượt kích thước Case · Khởi tạo cấu hình · Chọn CPU / Mainboard / RAM / VGA / SSD / HDD / PSU / Case / tản nhiệt / màn hình / phụ kiện Gaming · Thay đổi linh kiện · Xóa linh kiện · Tự động cập nhật tổng giá · Hiển thị trạng thái tương thích · Linh kiện còn thiếu · Kiểm tra đủ linh kiện cơ bản · Đề xuất PSU / Case / tản nhiệt · Cảnh báo linh kiện không tương thích · Thay thế linh kiện không tương thích · Lưu cấu hình · Sao chép · Xóa cấu hình · Xuất danh sách linh kiện · Thêm cả cấu hình vào giỏ · Kiểm tra tồn kho cả cấu hình · Tính chi phí lắp ráp

**B (101–119):** Khai báo nhu cầu · Mục đích (Gaming, đồ họa, văn phòng, livestream, thiết kế/dựng video) · Độ phân giải · FPS · Danh sách game · Mức đồ họa · Ngân sách · Ưu tiên CPU / GPU · Dung lượng RAM · Dung lượng lưu trữ · Phong cách thiết kế · RGB · Tối ưu chi phí · Phân tích nhu cầu

**C (120–130):** Đề xuất theo nhu cầu · ngân sách · game · độ phân giải & FPS · cân bằng CPU–GPU · khả năng nâng cấp tương lai · Nhiều phương án theo ngân sách · So sánh hiệu năng phương án · Điều chỉnh theo ngân sách thiếu · Thay thế giảm chi phí · Thay thế tăng hiệu năng

**D (157, 161–170, 179):** So sánh giá cùng phân khúc · Tổng chi phí theo ngân sách · Phân bổ ngân sách · Tỷ trọng lớn nhất · Cảnh báo vượt ngân sách · Linh kiện rẻ hơn hiệu năng tương đương · Linh kiện tốt hơn cùng ngân sách · Mức tiết kiệm · Chênh lệch giá giữa phương án · Nâng cấp theo ngân sách dư · Tổng chi phí sau khuyến mãi · Ngân sách dự kiến nâng cấp PC


### Nhật ký cập nhật 20/11/2026
- Rà lỗi gợi ý thiếu Mainboard: nguyên nhân là chưa ràng buộc bước chọn CPU với nền tảng có Mainboard tương thích RAM DDR đang chọn; cập nhật thuật toán trong RecommendPCService và BuildPartsDAO để chọn RAM trước, lọc CPU theo socket/DDR có sẵn trong kho, sau đó chọn Mainboard.
- Kiểm tra trực tiếp dữ liệu kho hiện tại với RAM 8 GB, màn hình từ 165 Hz và ngân sách gaming 15/20/25/30 triệu: cả bốn mức đều tạo đủ CPU, Mainboard, RAM, VGA, SSD, PSU, màn hình và tổng tiền không vượt ngân sách. Biên dịch hai file Java liên quan thành công.
- Chưa kiểm tra giao diện qua Tomcat trên trình duyệt; các kiểm tra tương thích Case, tản nhiệt và toàn bộ 73 UC còn ở trạng thái chưa hoàn tất.


### Rà soát theo thứ tự – Tuần 0 (20/11/2026)
- Xác nhận cấu hình project khai báo Java 21, web platform 10 và Tomcat; db.properties có trong output và truy vấn kho thực tế đọc được sản phẩm.
- Biên dịch toàn bộ 256 file Java thành công bằng javac --release 21; đây là kiểm tra compile, chưa thay thế NetBeans Clean and Build/JSP deploy.
- Không thể tạo nhánh minhtbth08199: Git từ chối ghi .git/refs vì metadata .git ở chế độ chỉ đọc trong môi trường này. Không sửa file của thành viên khác; các điểm cần chốt trực tiếp với nhóm vẫn chờ xác nhận.

- A1: kiểm tra giao dịch trực tiếp bằng cặp Intel Core i3-12100F / MSI PRO B760M-A WiFi DDR4; xác nhận tổng tiền sau thêm và sau xóa, rollback nên không để lại build thử.

### Rà lỗi Nhờ gợi ý – 20/11/2026
- Tái hiện tầng RecommendPCService trực tiếp trên kho dữ liệu hiện tại với cấu hình giống ảnh (Gaming, 8 GB RAM, màn hình tối thiểu 165 Hz, ngân sách 300 triệu): đề xuất chọn được Mainboard tương thích với RAM và trả về đủ linh kiện bắt buộc. Đây là kiểm tra service/DB, chưa phải kiểm thử HTTP/JSP trên Tomcat.
- Sửa tiêu chí hòa điểm trong RecommendPCService: khi kho chưa có điểm hiệu năng phân biệt, chọn linh kiện rẻ hơn để không tiêu hết ngân sách trước các linh kiện bắt buộc.
- Chuyển bước tạo/lưu yêu cầu xuống sau khi đã ghép được cấu hình đầy đủ; lỗi đề xuất không còn tạo bản ghi yêu cầu dở dang.
- Kiểm tra ma trận 12 trường hợp quick build Valorant/LMHT/CS2 ở 15/20/25/30 triệu với RAM 8 GB, màn hình từ 165 Hz: cả 12 tạo đủ CPU, Mainboard, RAM, VGA, SSD, PSU, màn hình và không vượt ngân sách. Tổng giá quan sát từ 14,229,000 đến 19,719,000 đồng.
- Biên dịch toàn bộ 256 file Java bằng JDK 21 thành công. Kiểm thử dùng truy vấn kho thật, không ghi dữ liệu. Ngân sách 3 triệu và một số cấu hình văn phòng không đủ tổng giá linh kiện nên vẫn báo thiếu phù hợp; không xem đó là lỗi khi ngân sách thực sự thấp.
- Chạy Ant của NetBeans 29: `clean dist compile-jsps` thành công; biên dịch 256 Java và tạo `dist/GearZone.war`. Xác nhận WAR chứa RecommendPCServlet, RecommendPCService và JSP Nhờ gợi ý; chạy lớp vừa build với kho thật chọn được Mainboard MSI PRO B760M-A WiFi DDR4 và đủ slot bắt buộc.


### Kiểm tra NetBeans/Tomcat – 20/11/2026
- Chạy `clean default` theo target mặc định của NetBeans: Java compile và WAR thành công, nhưng bước Javadoc làm target kết thúc lỗi do chú thích HTML/Javadoc ở các file ngoài namespace minhtbth08199 (AdminShippingServlet, AdminExportServlet, AdminReportServlet, BudgetTierService, ReportService). Không sửa các file này theo phạm vi đã thống nhất.
- Chạy `clean dist compile-jsps`: BUILD SUCCESSFUL; 256 file Java được biên dịch và WAR được tạo. `compile-jsps` trong project không thực hiện biên dịch JSP riêng; chưa deploy/chạy HTTP trên Tomcat.
- Kiểm tra WAR có các class RecommendPCServlet/RecommendPCService và trang recommend.jsp; gọi service từ chính class vừa build với kho hiện tại thành công, có Mainboard phù hợp và đủ các slot bắt buộc.
- Thử target NetBeans `run-deploy`: bị chặn trước deploy vì file cấu hình thiếu thuộc tính `tomcat.password`; Tomcat không chạy và chưa có GearZone trong webapps. Không đọc/đổi mật khẩu hoặc sửa file ngoài namespace minhtbth08199.
- Việc còn lại để nghiệm thu end-to-end: xử lý lỗi Javadoc ngoài namespace được phép sửa hoặc cấu hình build bỏ qua Javadoc; bổ sung mật khẩu Tomcat trong cấu hình máy của chủ project; sau đó deploy và thử POST từ trình duyệt.
