<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-plus-circle me-2"></i>Thêm Chi tiết Linh kiện PC</h1>
    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách
    </a>
</div>

<div class="card shadow-sm border-0 rounded-4 mb-4">
    <div class="card-body p-4">
        <form id="productDetailForm" action="${pageContext.request.contextPath}/admin/product-form" method="post" enctype="multipart/form-data">
            <input type="hidden" name="action" id="detailFormAction" value="save"/>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Mã sản phẩm (SKU):</label>
                    <input type="text" name="productCode" required class="form-control" placeholder="VD: CPU-I5-13400"/>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Tên linh kiện:</label>
                    <input type="text" name="partName" required class="form-control" placeholder="VD: Intel Core i5 13400F"/>
                </div>
            </div>

            <!-- Liên kết Danh mục và Thương hiệu động từ CSDL -->
            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Danh mục:</label>
                    <select name="categoryId" id="categorySelect" class="form-select" required>
                        <option value="">-- Chọn danh mục --</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.categoryId}">${cat.categoryName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Thương hiệu:</label>
                    <select name="brandId" class="form-select" required>
                        <option value="">-- Chọn thương hiệu --</option>
                        <c:forEach var="brand" items="${brands}">
                            <option value="${brand.brandId}">${brand.brandName}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Giá niêm yết (VNĐ):</label>
                    <input type="number" name="basePrice" required class="form-control" min="0"/>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Giá khuyến mãi (VNĐ):</label>
                    <input type="number" name="salePrice" class="form-control" min="0"/>
                </div>
            </div>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Trạng thái bán:</label>
                    <select name="status" class="form-select">
                        <option value="ACTIVE">Công khai (Đang bán)</option>
                        <option value="HIDDEN">Ẩn khỏi danh sách bán</option>
                        <option value="DISCONTINUED">Ngừng kinh doanh</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Bảo hành (Tháng):</label>
                    <input type="number" name="warrantyMonths" class="form-control" value="12" min="0"/>
                </div>
            </div>

            <!-- ================= KHU VỰC THÔNG SỐ KỸ THUẬT ĐỘNG ================= -->
            
            <!-- 1. Thông số CPU -->
            <div id="cpuSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-microchip me-1"></i> Thông số CPU</h5>
                <div class="row g-2">
                    <div class="col-md-4"><input type="number" name="socketId" placeholder="Socket ID (VD: 1, 2)" class="form-control"/></div>
                    <div class="col-md-4"><input type="number" name="cores" placeholder="Số nhân (Cores)" class="form-control"/></div>
                    <div class="col-md-4"><input type="number" name="threads" placeholder="Số luồng (Threads)" class="form-control"/></div>
                    <div class="col-md-4 mt-2"><input type="text" name="baseClock" placeholder="Xung cơ bản (GHz)" class="form-control"/></div>
                    <div class="col-md-4 mt-2"><input type="text" name="boostClock" placeholder="Xung tối đa (GHz)" class="form-control"/></div>
                    <div class="col-md-4 mt-2"><input type="number" name="tdp" placeholder="TDP (W)" class="form-control"/></div>
                </div>
            </div>

            <!-- 2. Thông số Mainboard -->
            <div id="mainboardSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-chess-board me-1"></i> Thông số Mainboard</h5>
                <div class="row g-2">
                    <div class="col-md-4"><input type="number" name="socketId" placeholder="Socket ID" class="form-control"/></div>
                    <div class="col-md-4"><input type="text" name="chipset" placeholder="Chipset (VD: B760, Z790)" class="form-control"/></div>
                    <div class="col-md-4"><input type="text" name="formFactor" placeholder="Form Factor (ATX / mATX)" class="form-control"/></div>
                    <div class="col-md-6 mt-2"><input type="text" name="ramStandard" placeholder="Chuẩn RAM (DDR4 / DDR5)" class="form-control"/></div>
                    <div class="col-md-6 mt-2"><input type="number" name="ramSlots" placeholder="Số khe RAM" class="form-control"/></div>
                </div>
            </div>

            <!-- 3. Thông số RAM -->
            <div id="ramSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-memory me-1"></i> Thông số RAM</h5>
                <div class="row g-2">
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Chuẩn RAM (DDR)</label>
                        <input type="text" name="ddrType" placeholder="DDR4 hoặc DDR5" class="form-control"/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Dung lượng (GB)</label>
                        <input type="number" name="capacityGb" placeholder="VD: 16, 32" class="form-control"/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Tốc độ Bus (MHz)</label>
                        <input type="number" name="busSpeedMhz" placeholder="VD: 3200, 6000" class="form-control"/>
                    </div>
                    <div class="col-md-6 mt-2">
                        <label class="form-label small text-muted mb-1">Số thanh (Module)</label>
                        <input type="number" name="moduleCount" placeholder="VD: 1, 2" class="form-control"/>
                    </div>
                    <div class="col-md-6 mt-2">
                        <label class="form-label small text-muted mb-1">Độ trễ CAS</label>
                        <input type="text" name="casLatency" placeholder="VD: CL16, CL32" class="form-control"/>
                    </div>
                </div>
            </div>

            <!-- 4. Thông số VGA / GPU -->
            <div id="vgaSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-tv me-1"></i> Thông số VGA / GPU (Card đồ họa)</h5>
                <div class="row g-2">
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">GPU Chipset</label>
                        <input type="text" name="gpuChipset" placeholder="VD: RTX 4060, RTX 4070 SUPER" class="form-control"/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Dung lượng VRAM (GB)</label>
                        <input type="number" name="vramCapacityGb" placeholder="VD: 8, 12, 16" class="form-control"/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Nguồn đề xuất (W)</label>
                        <input type="number" name="recommendedPsuWatts" placeholder="VD: 550, 650, 750" class="form-control"/>
                    </div>
                </div>
            </div>

            <!-- 5. Thông số SSD / HDD -->
            <div id="storageSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-hdd me-1"></i> Thông số Ổ cứng (SSD / HDD)</h5>
                <div class="row g-2">
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">Loại ổ cứng</label>
                        <input type="text" name="storageType" placeholder="SSD hoặc HDD" class="form-control"/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">Form factor</label>
                        <input type="text" name="formFactor" placeholder="M.2 2280 / 2.5 inch / 3.5 inch" class="form-control"/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">Chuẩn giao tiếp</label>
                        <input type="text" name="interface" placeholder="NVMe PCIe Gen4 / SATA III" class="form-control"/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">Dung lượng (GB)</label>
                        <input type="number" name="capacityGb" placeholder="VD: 512, 1024" class="form-control"/>
                    </div>
                </div>
            </div>

            <!-- 6. Thông số PSU (Nguồn) -->
            <div id="psuSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-bolt me-1"></i> Thông số Nguồn (PSU)</h5>
                <div class="row g-2">
                    <div class="col-md-4"><input type="number" name="wattage" placeholder="Công suất (W)" class="form-control"/></div>
                    <div class="col-md-4"><input type="text" name="efficiencyRating" placeholder="Chứng nhận (80 Plus Bronze/Gold)" class="form-control"/></div>
                    <div class="col-md-4"><input type="text" name="modularType" placeholder="Loại cáp (Full Modular...)" class="form-control"/></div>
                </div>
            </div>
            <!-- 7. Thông số Vỏ máy tính (Case) -->
            <div id="caseSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-desktop me-1"></i> Thông số Vỏ máy tính (Case)</h5>
                <div class="row g-2">
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Mainboard hỗ trợ</label>
                        <input type="text" name="caseFormFactor" placeholder="ATX / mATX / ITX" class="form-control"/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">VGA dài tối đa (mm)</label>
                        <input type="number" name="maxVgaLengthMm" placeholder="VD: 340" class="form-control"/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Tản CPU cao tối đa (mm)</label>
                        <input type="number" name="maxCpuCoolerHeightMm" placeholder="VD: 165" class="form-control"/>
                    </div>
                </div>
            </div>

            <!-- 8. Thông số Tản nhiệt (Cooler) -->
            <div id="coolerSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-fan me-1"></i> Thông số Tản nhiệt (Cooler)</h5>
                <div class="row g-2">
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">Loại tản</label>
                        <input type="text" name="coolerType" placeholder="AIO_LIQUID hoặc AIR" class="form-control"/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">Chiều cao (mm)</label>
                        <input type="number" name="heightMm" placeholder="VD: 155" class="form-control"/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">Radiator (mm)</label>
                        <input type="number" name="radiatorSizeMm" placeholder="VD: 240, 360" class="form-control"/>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small text-muted mb-1">TDP tối đa (W)</label>
                        <input type="number" name="maxTdp" placeholder="VD: 250" class="form-control"/>
                    </div>
                    <div class="col-md-12 mt-2">
                        <label class="form-label small text-muted mb-1">Socket hỗ trợ</label>
                        <input type="text" name="supportedSockets" placeholder="VD: LGA1700, AM5, AM4" class="form-control"/>
                    </div>
                </div>
            </div>
            <!-- 9. Thông số Màn hình Gaming -->
            <div id="monitorSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-desktop me-1"></i> Thông số Màn hình Gaming</h5>
                <div class="row g-2">
                    <div class="col-md-3"><input type="text" name="screenSize" placeholder="Kích thước (VD: 27 inch)" class="form-control"/></div>
                    <div class="col-md-3"><input type="text" name="resolution" placeholder="Độ phân giải (VD: 2K 1440p)" class="form-control"/></div>
                    <div class="col-md-3"><input type="number" name="refreshRate" placeholder="Tần số quét (Hz) (VD: 165)" class="form-control"/></div>
                    <div class="col-md-3"><input type="text" name="panelType" placeholder="Tấm nền (IPS / VA / OLED)" class="form-control"/></div>
                </div>
            </div>

            <!-- 10. Thông số Gaming Gear (Phím / Chuột / Tai nghe) -->
            <div id="gearSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-headset me-1"></i> Thông số Gaming Gear</h5>
                <div class="row g-2">
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Loại thiết bị</label>
                        <input type="text" name="gearType" placeholder="KEYBOARD / MOUSE / HEADSET / MOUSEPAD / OTHER" class="form-control"/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Kiểu kết nối</label>
                        <input type="text" name="connectivity" placeholder="WIRED / WIRELESS / BOTH" class="form-control"/>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small text-muted mb-1">Switch / DPI</label>
                        <input type="text" name="switchOrDpi" placeholder="Switch phím hoặc DPI chuột" class="form-control"/>
                    </div>
                </div>
            </div>
            <!-- ================= KẾT THÚC KHU VỰC THÔNG SỐ ================= -->

            <!-- Đã chỉnh sửa phần tải ảnh tại đây -->
            <div class="mb-3 mt-3">
                <label class="form-label fw-semibold">Hình ảnh chính:</label>
                <input type="file" name="productImage" class="form-control mb-2"/>
            </div>

            <div class="d-flex flex-wrap gap-2">
                <button type="submit" id="btnSaveAndList" class="btn btn-primary px-4"
                        onclick="document.getElementById('detailFormAction').value='save';">
                    <i class="fas fa-save me-1"></i> Lưu &amp; Về danh sách
                </button>
                <button type="submit" id="btnSaveAndContinue" class="btn btn-success px-4"
                        onclick="document.getElementById('detailFormAction').value='saveAndContinue';">
                    <i class="fas fa-plus-circle me-1"></i> Lưu &amp; Thêm tiếp linh kiện khác
                </button>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary px-4">Hủy</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />
<script>
    // Chỉ gửi field của section đang hiển thị (input disabled không nằm trong request).
    // Tránh vượt maxPartCount (mặc định 50) của Tomcat với multipart và tránh trùng tên giữa các section.
    function syncSpecDisabled() {
        document.querySelectorAll('.spec-section').forEach(function (sec) {
            var hidden = window.getComputedStyle(sec).display === 'none';
            sec.querySelectorAll('input, select, textarea').forEach(function (f) {
                f.disabled = hidden;
            });
        });
    }
    // Đăng ký TRƯỚC listener hiện/ẩn bên dưới nên dùng setTimeout để chạy sau khi section đã đổi trạng thái.
    document.getElementById('categorySelect').addEventListener('change', function() {
        setTimeout(syncSpecDisabled, 0);
    });
    syncSpecDisabled();

    document.getElementById('categorySelect').addEventListener('change', function() {
        var selectElement = this;
        var selectedText = selectElement.options[selectElement.selectedIndex].text.toLowerCase();
        
        // Ẩn toàn bộ các khung thông số có class spec-section
        document.querySelectorAll('.spec-section').forEach(function(el) {
            el.style.display = 'none';
        });
        
        // Hiển thị khung tương ứng dựa vào tên danh mục
        if (selectedText.includes('cooler') || selectedText.includes('tản nhiệt')) {
            document.getElementById('coolerSpecs').style.display = 'block';
        } else if (selectedText.includes('mainboard') || selectedText.includes('bo mạch')) {
            document.getElementById('mainboardSpecs').style.display = 'block';
        } else if (selectedText.includes('ram')) {
            document.getElementById('ramSpecs').style.display = 'block';
        } else if (selectedText.includes('vga') || selectedText.includes('đồ họa')) {
            document.getElementById('vgaSpecs').style.display = 'block';
        } else if (selectedText.includes('ssd') || selectedText.includes('hdd') || selectedText.includes('ổ cứng')) {
            document.getElementById('storageSpecs').style.display = 'block';
        } else if (selectedText.includes('psu') || selectedText.includes('nguồn')) {
            document.getElementById('psuSpecs').style.display = 'block';
        } else if (selectedText.includes('case') || selectedText.includes('vỏ')) {
            document.getElementById('caseSpecs').style.display = 'block';
        } else if (selectedText.includes('màn hình') || selectedText.includes('monitor')) {
            document.getElementById('monitorSpecs').style.display = 'block';
        } else if (selectedText.includes('gear') || selectedText.includes('phím') || selectedText.includes('chuột') || selectedText.includes('tai nghe')) {
            document.getElementById('gearSpecs').style.display = 'block';
        } else if (selectedText.includes('cpu')) {
            document.getElementById('cpuSpecs').style.display = 'block';
        }
    });
</script>