<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-plus-circle me-2"></i>Thêm / Cập nhật Thông số Linh kiện PC</h1>
    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách
    </a>
</div>

<div class="card shadow-sm border-0 rounded-4 mb-4">
    <div class="card-body p-4">
        <form action="${pageContext.request.contextPath}/admin/product-form" method="post" enctype="multipart/form-data">
            <input type="hidden" name="action" value="save"/>

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
            <div id="cpuSpecs" class="spec-section card p-3 border bg-light mb-3">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-microchip me-1"></i> Thông số CPU</h5>
                <div class="row g-2">
                    <div class="col-md-4"><input type="number" name="socketId" placeholder="Socket ID (VD: 1700)" class="form-control"/></div>
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
                    <div class="col-md-4"><input type="text" name="ddrType" placeholder="Loại (DDR4 / DDR5)" class="form-control"/></div>
                    <div class="col-md-4"><input type="number" name="capacityGb" placeholder="Dung lượng (GB)" class="form-control"/></div>
                    <div class="col-md-4"><input type="number" name="busSpeedMhz" placeholder="Bus Speed (MHz)" class="form-control"/></div>
                </div>
            </div>

            <!-- 4. Thông số VGA -->
            <div id="vgaSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-tv me-1"></i> Thông số VGA (Card đồ họa)</h5>
                <div class="row g-2">
                    <div class="col-md-6"><input type="text" name="gpuChipset" placeholder="GPU Chipset (VD: RTX 4060)" class="form-control"/></div>
                    <div class="col-md-3"><input type="number" name="vramCapacityGb" placeholder="VRAM (GB)" class="form-control"/></div>
                    <div class="col-md-3"><input type="number" name="recommendedPsuWatts" placeholder="Nguồn đề xuất (W)" class="form-control"/></div>
                </div>
            </div>

            <!-- 5. Thông số SSD / HDD -->
            <div id="storageSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-hdd me-1"></i> Thông số Ổ cứng (SSD / HDD)</h5>
                <div class="row g-2">
                    <div class="col-md-6"><input type="text" name="storageType" placeholder="Loại (NVMe / SATA / HDD)" class="form-control"/></div>
                    <div class="col-md-6"><input type="number" name="capacityGb" placeholder="Dung lượng (GB)" class="form-control"/></div>
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
                    <div class="col-md-12"><input type="text" name="caseFormFactor" placeholder="Hỗ trợ Mainboard (ATX / mATX / ITX)" class="form-control"/></div>
                </div>
            </div>

            <!-- 8. Thông số Tản nhiệt (Cooler) -->
            <div id="coolerSpecs" class="spec-section card p-3 border bg-light mb-3" style="display:none;">
                <h5 class="card-title text-secondary fs-6 mb-3"><i class="fas fa-fan me-1"></i> Thông số Tản nhiệt (Cooler)</h5>
                <div class="row g-2">
                    <div class="col-md-6"><input type="text" name="coolerType" placeholder="Loại tản (Tản khí / AIO 240 / AIO 360)" class="form-control"/></div>
                    <div class="col-md-6"><input type="number" name="maxTdp" placeholder="Hỗ trợ TDP tối đa (W) (VD: 250)" class="form-control"/></div>
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
                    <div class="col-md-4"><input type="text" name="gearType" placeholder="Loại (Bàn phím / Chuột / Tai nghe)" class="form-control"/></div>
                    <div class="col-md-4"><input type="text" name="connectivity" placeholder="Kết nối (Có dây / Wireless / Bluetooth)" class="form-control"/></div>
                    <div class="col-md-4"><input type="text" name="switchOrDpi" placeholder="Switch phím / Độ phân giải DPI chuột" class="form-control"/></div>
                </div>
            </div>
            <!-- ================= KẾT THÚC KHU VỰC THÔNG SỐ ================= -->

            <div class="mb-3 mt-3">
                <label class="form-label fw-semibold">Hình ảnh chính & Video giới thiệu:</label>
                <input type="file" name="productImage" class="form-control mb-2"/>
                <input type="text" name="videoUrl" placeholder="Link video YouTube giới thiệu" class="form-control"/>
            </div>

            <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary px-4"><i class="fas fa-save me-1"></i> Lưu linh kiện</button>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary px-4">Hủy</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />
<script>
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