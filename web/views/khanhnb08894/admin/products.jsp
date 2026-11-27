<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-box-open me-2"></i>Quản lý Linh kiện</h1>
    <div class="d-flex gap-2">
        <!-- Nút thêm bằng form riêng mới -->
        <a href="${pageContext.request.contextPath}/admin/product-form" class="btn btn-success">
            <i class="fas fa-plus me-1"></i> Thêm chi tiết (Form riêng)
        </a>
        <!-- Nút mở modal cũ -->
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addProductModal">
            <i class="fas fa-plus me-1"></i> Thêm Nhanh (Modal)
        </button>
    </div>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> ${sessionScope.message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>
<!-- Form Tìm kiếm & Lọc -->
<div class="card shadow-sm border-0 rounded-4 mb-4">
    <div class="card-body p-3">
        <form action="${pageContext.request.contextPath}/admin/products" method="GET" class="row g-3 align-items-center">

            <!-- Ô tìm kiếm Tên / Mã SKU -->
            <div class="col-md-4">
                <div class="input-group">
                    <span class="input-group-text bg-white"><i class="fas fa-search text-muted"></i></span>
                    <input type="text" name="keyword" value="${paramKeyword}" class="form-control" placeholder="Tìm tên linh kiện, mã SKU...">
                </div>
            </div>

            <!-- Lọc theo Danh mục -->
            <div class="col-md-3">
                <select name="categoryId" class="form-select">
                    <option value="">-- Tất cả danh mục --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.categoryId}" ${paramCategoryId == cat.categoryId ? 'selected' : ''}>
                            ${cat.categoryName}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Lọc theo Thương hiệu -->
            <div class="col-md-3">
                <select name="brandId" class="form-select">
                    <option value="">-- Tất cả thương hiệu --</option>
                    <c:forEach var="brand" items="${brands}">
                        <option value="${brand.brandId}" ${paramBrandId == brand.brandId ? 'selected' : ''}>
                            ${brand.brandName}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Nút Action -->
            <div class="col-md-2 d-flex gap-2">
                <button type="submit" class="btn btn-primary w-100"><i class="fas fa-filter me-1"></i> Lọc</button>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary" title="Hủy lọc"><i class="fas fa-sync-alt"></i></a>
            </div>
        </form>
    </div>
</div>
<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th class="text-center">Ảnh</th>
                        <th>Mã SP</th>
                        <th>Tên Linh Kiện</th>
                        <th>Giá Bán</th>
                        <th>Bảo Hành</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${parts}">
                        <tr>
                            <td>#${p.partId}</td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${not empty p.imageUrl}">
                                        <img src="${p.imageUrl}" alt="img" class="rounded border" style="width: 45px; height: 45px; object-fit: contain;">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="bg-light rounded d-flex align-items-center justify-content-center text-secondary mx-auto" style="width: 45px; height: 45px;">
                                            <i class="fas fa-microchip"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><code>${p.productCode}</code></td>
                            <td class="fw-bold">${p.partName}</td>
                            <td class="text-danger fw-semibold">
                                <fmt:formatNumber value="${p.salePrice != null ? p.salePrice : p.basePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </td>
                            <td>${p.warrantyMonths} tháng</td>
                            <td>
                                <c:choose>
                                    <c:when test="${p.status == 'ACTIVE'}"><span class="badge bg-success">Hoạt động</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">Đã ẩn</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <div class="d-flex gap-2 justify-content-center">
                                    <!-- Nút Sửa (Modal có kèm thông số chi tiết theo danh mục) -->
                                    <button type="button" class="btn btn-sm btn-outline-primary btn-edit-part"
                                            data-bs-toggle="modal" data-bs-target="#editProductModal"
                                            data-id="${p.partId}"
                                            data-category="${p.categoryId}"
                                            data-brand="${p.brandId}"
                                            data-code="<c:out value='${p.productCode}'/>"
                                            data-name="<c:out value='${p.partName}'/>"
                                            data-base="${p.basePrice != null ? p.basePrice.longValue() : ''}"
                                            data-sale="${p.salePrice != null ? p.salePrice.longValue() : ''}"
                                            data-warranty="${p.warrantyMonths}"
                                            data-desc="<c:out value='${p.description}'/>">
                                        <i class="fas fa-edit"></i> Sửa
                                    </button>

                                    <!-- Form Ẩn / Hiện -->
                                    <form action="${pageContext.request.contextPath}/admin/products" method="POST" class="m-0">
                                        <input type="hidden" name="partId" value="${p.partId}">
                                        <c:choose>
                                            <c:when test="${p.status == 'ACTIVE'}">
                                                <input type="hidden" name="action" value="deleteProduct">
                                                <button type="submit" class="btn btn-sm btn-outline-warning" onclick="return confirm('Bạn có chắc muốn ẩn linh kiện này?');" title="Ẩn linh kiện">
                                                    <i class="fas fa-eye-slash"></i> Ẩn
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="hidden" name="action" value="restoreProduct">
                                                <button type="submit" class="btn btn-sm btn-outline-success" title="Hiện lại linh kiện">
                                                    <i class="fas fa-eye"></i> Hiện
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>

                                    <!-- Nút Xóa vĩnh viễn (nếu Servlet của bạn có hỗ trợ action delete cứng) -->
                                    <form action="${pageContext.request.contextPath}/admin/products" method="POST" class="m-0">
                                        <input type="hidden" name="partId" value="${p.partId}">
                                        <input type="hidden" name="action" value="hardDeleteProduct">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('CẢNH BÁO: Thao tác này sẽ xóa vĩnh viễn linh kiện và không thể khôi phục. Bạn có chắc chắn muốn xóa?');" title="Xóa vĩnh viễn">
                                            <i class="fas fa-trash-alt"></i> Xóa
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal Thêm mới -->
<div class="modal fade" id="addProductModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/products" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="action" value="saveProduct">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-plus-circle me-2"></i>Thêm Linh kiện mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="row mb-3">
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Danh mục</label>
                            <select name="categoryId" class="form-select" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.categoryId}">${cat.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Thương hiệu</label>
                            <select name="brandId" class="form-select" required>
                                <option value="">-- Chọn --</option>
                                <c:forEach var="brand" items="${brands}">
                                    <option value="${brand.brandId}">${brand.brandName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Mã Sản phẩm (Code)</label>
                            <input type="text" name="productCode" class="form-control" required placeholder="VD: CPU-I5-13400">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tên Linh kiện</label>
                        <input type="text" name="partName" class="form-control" required placeholder="VD: Intel Core i5 13400F">
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Giá gốc (VNĐ)</label>
                            <input type="number" step="0.01" name="basePrice" class="form-control" required min="0">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Giá khuyến mãi (Nếu có)</label>
                            <input type="number" step="0.01" name="salePrice" class="form-control" min="0">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Bảo hành (Tháng)</label>
                            <input type="number" name="warrantyMonths" class="form-control" value="12" required min="0">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Ảnh sản phẩm (Tải lên)</label>
                        <input type="file" name="imageFile" class="form-control" accept="image/png,image/jpeg,image/webp,image/gif">
                        <div class="form-text">Hỗ trợ định dạng: jpg, png, webp, gif (Tối đa 2MB).</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Mô tả</label>
                        <textarea name="description" class="form-control" rows="3" placeholder="Nhập mô tả chi tiết..."></textarea>
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4"><i class="fas fa-save me-1"></i> Lưu Linh kiện</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal Sửa -->
<div class="modal fade" id="editProductModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/products" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="action" value="updateProduct">
                <input type="hidden" name="partId" id="editPartId">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-edit me-2"></i>Sửa Linh kiện</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="row mb-3">
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Danh mục</label>
                            <select name="categoryId" id="editCategory" class="form-select" required>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.categoryId}">${cat.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Thương hiệu</label>
                            <select name="brandId" id="editBrand" class="form-select" required>
                                <c:forEach var="brand" items="${brands}">
                                    <option value="${brand.brandId}">${brand.brandName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Mã Sản phẩm (Code)</label>
                            <input type="text" name="productCode" id="editCode" class="form-control" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tên Linh kiện</label>
                        <input type="text" name="partName" id="editName" class="form-control" required>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Giá gốc (VNĐ)</label>
                            <input type="number" step="0.01" name="basePrice" id="editBase" class="form-control" required min="0">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Giá khuyến mãi (Nếu có)</label>
                            <input type="number" step="0.01" name="salePrice" id="editSale" class="form-control" min="0">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Bảo hành (Tháng)</label>
                            <input type="number" name="warrantyMonths" id="editWarranty" class="form-control" required min="0">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Ảnh sản phẩm mới (Tải lên)</label>
                        <input type="file" name="imageFile" class="form-control" accept="image/png,image/jpeg,image/webp,image/gif">
                        <div class="form-text">Để trống nếu không muốn thay đổi ảnh cũ.</div>
                    </div>

                    <!-- Thông số chi tiết theo danh mục ngay trong form Sửa -->
                    <div id="editCpuSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-microchip me-1"></i> Thông số chi tiết CPU</h6>
                        <div class="row g-2">
                            <div class="col-md-4"><input type="number" name="socketId" placeholder="Socket ID" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="cores" placeholder="Số nhân" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="threads" placeholder="Số luồng" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="baseClock" placeholder="Xung cơ bản (GHz)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="boostClock" placeholder="Xung tối đa (GHz)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="tdp" placeholder="TDP (W)" class="form-control form-control-sm"/></div>
                        </div>
                    </div>

                    <div id="editMainboardSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-chess-board me-1"></i> Thông số chi tiết Mainboard</h6>
                        <div class="row g-2">
                            <div class="col-md-4"><input type="number" name="socketId" placeholder="Socket ID" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="chipset" placeholder="Chipset (B760, Z790...)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="formFactor" placeholder="Form Factor (ATX / mATX)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="ramStandard" placeholder="Chuẩn RAM (DDR4 / DDR5)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="ramSlots" placeholder="Số khe RAM" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="maxRamSpeedMhz" placeholder="Bus RAM tối đa (MHz)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="maxRamCapacityGb" placeholder="RAM tối đa (GB)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="m2SlotsCount" placeholder="Số khe M.2" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="sataPortsCount" placeholder="Số cổng SATA" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="pcieGeneration" placeholder="PCIe (PCIe 4.0 / 5.0)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="cpuPowerConnectors" placeholder="Đầu nguồn CPU (8-pin...)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="supportedCpuGenerations" placeholder="Thế hệ CPU hỗ trợ" class="form-control form-control-sm"/></div>
                            <div class="col-md-12">
                                <div class="form-check mt-1">
                                    <input class="form-check-input" type="checkbox" name="supportsNvme" value="1" id="editSupportsNvme">
                                    <label class="form-check-label small" for="editSupportsNvme">Hỗ trợ ổ cứng NVMe</label>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="editRamSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-memory me-1"></i> Thông số chi tiết RAM</h6>
                        <div class="row g-2">
                            <div class="col-md-4"><input type="text" name="ddrType" placeholder="Chuẩn RAM (DDR4 / DDR5)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="capacityGb" placeholder="Dung lượng (GB)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="busSpeedMhz" placeholder="Bus (MHz)" class="form-control form-control-sm"/></div>
                            <div class="col-md-6"><input type="number" name="moduleCount" placeholder="Số thanh" class="form-control form-control-sm"/></div>
                            <div class="col-md-6"><input type="text" name="casLatency" placeholder="Độ trễ CAS (CL16, CL32...)" class="form-control form-control-sm"/></div>
                        </div>
                    </div>

                    <div id="editVgaSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-tv me-1"></i> Thông số chi tiết VGA / GPU</h6>
                        <div class="row g-2">
                            <div class="col-md-4"><input type="text" name="gpuChipset" placeholder="GPU Chipset" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="vramCapacityGb" placeholder="VRAM (GB)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="vramType" placeholder="Loại VRAM (GDDR6 / GDDR6X)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="recommendedPsuWatts" placeholder="Nguồn đề xuất (W)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="tgpWatts" placeholder="TGP (W)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="lengthMm" placeholder="Chiều dài (mm)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="powerConnectors" placeholder="Đầu nguồn (8-pin, 12VHPWR...)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="pcieInterface" placeholder="Giao tiếp PCIe" class="form-control form-control-sm"/></div>
                            <div class="col-md-2"><input type="number" name="hdmiPorts" placeholder="HDMI" class="form-control form-control-sm"/></div>
                            <div class="col-md-2"><input type="number" name="displayportPorts" placeholder="DP" class="form-control form-control-sm"/></div>
                        </div>
                    </div>

                    <div id="editStorageSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-hdd me-1"></i> Thông số chi tiết Ổ cứng</h6>
                        <div class="row g-2">
                            <div class="col-md-3"><input type="text" name="storageType" placeholder="SSD / HDD" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="text" name="formFactor" placeholder="M.2 2280 / 2.5 / 3.5" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="text" name="interface" placeholder="NVMe / SATA III" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="capacityGb" placeholder="Dung lượng (GB)" class="form-control form-control-sm"/></div>
                        </div>
                    </div>

                    <div id="editPsuSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-bolt me-1"></i> Thông số chi tiết PSU</h6>
                        <div class="row g-2">
                            <div class="col-md-4"><input type="number" name="wattage" placeholder="Công suất (W)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="efficiencyRating" placeholder="Chứng nhận (80 Plus...)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="modularType" placeholder="Kiểu cáp (Full Modular...)" class="form-control form-control-sm"/></div>
                        </div>
                    </div>

                    <div id="editCaseSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-desktop me-1"></i> Thông số chi tiết Case</h6>
                        <div class="row g-2">
                            <div class="col-md-4"><input type="text" name="caseFormFactor" placeholder="Mainboard hỗ trợ" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="maxVgaLengthMm" placeholder="VGA dài tối đa (mm)" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="number" name="maxCpuCoolerHeightMm" placeholder="Tản CPU cao tối đa (mm)" class="form-control form-control-sm"/></div>
                        </div>
                    </div>

                    <div id="editCoolerSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-fan me-1"></i> Thông số chi tiết Tản nhiệt</h6>
                        <div class="row g-2">
                            <div class="col-md-3"><input type="text" name="coolerType" placeholder="AIO_LIQUID / AIR" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="heightMm" placeholder="Chiều cao (mm)" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="radiatorSizeMm" placeholder="Radiator (mm)" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="maxTdp" placeholder="TDP tối đa (W)" class="form-control form-control-sm"/></div>
                            <div class="col-md-12"><input type="text" name="supportedSockets" placeholder="Socket hỗ trợ" class="form-control form-control-sm"/></div>
                        </div>
                    </div>

                    <div id="editMonitorSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-desktop me-1"></i> Thông số chi tiết Màn hình</h6>
                        <div class="row g-2">
                            <div class="col-md-3"><input type="text" name="screenSize" placeholder="Kích thước (inch)" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="text" name="resolution" placeholder="Độ phân giải" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="refreshRate" placeholder="Tần số quét (Hz)" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="text" name="panelType" placeholder="Tấm nền" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" step="0.1" name="responseTimeMs" placeholder="Phản hồi (ms)" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="text" name="adaptiveSync" placeholder="Adaptive Sync" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="hdmiPorts" placeholder="Số cổng HDMI" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="displayportPorts" placeholder="Số cổng DP" class="form-control form-control-sm"/></div>
                            <div class="col-md-6"><input type="text" name="hdmiVersion" placeholder="Chuẩn HDMI (2.0 / 2.1)" class="form-control form-control-sm"/></div>
                            <div class="col-md-6"><input type="text" name="displayportVersion" placeholder="Chuẩn DP (1.4 / 2.1)" class="form-control form-control-sm"/></div>
                        </div>
                    </div>

                    <div id="editGearSpecs" class="edit-spec-section card p-3 border bg-light mb-3" style="display:none;">
                        <h6 class="fw-bold text-secondary mb-2"><i class="fas fa-headset me-1"></i> Thông số chi tiết Gaming Gear</h6>
                        <div class="row g-2">
                            <div class="col-md-4"><input type="text" name="gearType" placeholder="KEYBOARD / MOUSE / HEADSET..." class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="connectivity" placeholder="WIRED / WIRELESS / BOTH" class="form-control form-control-sm"/></div>
                            <div class="col-md-4"><input type="text" name="switchOrDpi" placeholder="Switch / DPI" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="text" name="layout" placeholder="Layout (TKL, 65%...)" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="dpiMax" placeholder="DPI tối đa" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="pollingRateHz" placeholder="Polling (Hz)" class="form-control form-control-sm"/></div>
                            <div class="col-md-3"><input type="number" name="weightG" placeholder="Trọng lượng (g)" class="form-control form-control-sm"/></div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Mô tả</label>
                        <textarea name="description" id="editDesc" class="form-control" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4"><i class="fas fa-save me-1"></i> Lưu thay đổi</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    var EXISTING_PART_SPECS = ${not empty partSpecsJson ? partSpecsJson : '{}'};

    function toggleEditSpecSection() {
        var catSelect = document.getElementById('editCategory');
        var selectedText = catSelect && catSelect.options[catSelect.selectedIndex]
                ? catSelect.options[catSelect.selectedIndex].text.toLowerCase() : '';
        document.querySelectorAll('.edit-spec-section').forEach(function (el) {
            el.style.display = 'none';
        });
        if (selectedText.includes('cooler') || selectedText.includes('tản nhiệt')) {
            document.getElementById('editCoolerSpecs').style.display = 'block';
        } else if (selectedText.includes('mainboard') || selectedText.includes('bo mạch')) {
            document.getElementById('editMainboardSpecs').style.display = 'block';
        } else if (selectedText.includes('ram')) {
            document.getElementById('editRamSpecs').style.display = 'block';
        } else if (selectedText.includes('vga') || selectedText.includes('đồ họa')) {
            document.getElementById('editVgaSpecs').style.display = 'block';
        } else if (selectedText.includes('ssd') || selectedText.includes('hdd') || selectedText.includes('ổ cứng')) {
            document.getElementById('editStorageSpecs').style.display = 'block';
        } else if (selectedText.includes('psu') || selectedText.includes('nguồn')) {
            document.getElementById('editPsuSpecs').style.display = 'block';
        } else if (selectedText.includes('case') || selectedText.includes('vỏ')) {
            document.getElementById('editCaseSpecs').style.display = 'block';
        } else if (selectedText.includes('màn hình') || selectedText.includes('monitor')) {
            document.getElementById('editMonitorSpecs').style.display = 'block';
        } else if (selectedText.includes('gear') || selectedText.includes('phím') || selectedText.includes('chuột') || selectedText.includes('tai nghe')) {
            document.getElementById('editGearSpecs').style.display = 'block';
        } else if (selectedText.includes('cpu')) {
            document.getElementById('editCpuSpecs').style.display = 'block';
        }
    }

    // Chỉ gửi field của section đang hiển thị. Input bị disabled sẽ không nằm trong request,
    // tránh vượt giới hạn maxPartCount (mặc định 50) của Tomcat cho multipart/form-data
    // và tránh trùng tên (socketId, formFactor, capacityGb, hdmiPorts...) giữa các section.
    function syncSpecDisabled() {
        document.querySelectorAll('.edit-spec-section').forEach(function (sec) {
            var hidden = window.getComputedStyle(sec).display === 'none';
            sec.querySelectorAll('input, select, textarea').forEach(function (f) {
                f.disabled = hidden;
            });
        });
    }

    function fillEditSpecFields(partId) {
        var modalEl = document.getElementById('editProductModal');
        if (!modalEl) return;
        modalEl.querySelectorAll('.edit-spec-section input').forEach(function (inp) {
            inp.value = '';
        });
        var spec = EXISTING_PART_SPECS[String(partId)] || EXISTING_PART_SPECS[partId];
        if (!spec) return;

        var setVal = function (sectionId, fieldName, val) {
            if (val === undefined || val === null) return;
            var sec = document.getElementById(sectionId);
            if (!sec) return;
            var inp = sec.querySelector('input[name="' + fieldName + '"]');
            if (inp) inp.value = val;
        };

        setVal('editCpuSpecs', 'socketId', spec.socket_id);
        setVal('editCpuSpecs', 'cores', spec.cores);
        setVal('editCpuSpecs', 'threads', spec.threads);
        setVal('editCpuSpecs', 'baseClock', spec.base_clock_ghz);
        setVal('editCpuSpecs', 'boostClock', spec.boost_clock_ghz);
        setVal('editCpuSpecs', 'tdp', spec.tdp_watts);

        setVal('editMainboardSpecs', 'socketId', spec.socket_id);
        setVal('editMainboardSpecs', 'chipset', spec.chipset);
        setVal('editMainboardSpecs', 'formFactor', spec.form_factor);
        setVal('editMainboardSpecs', 'ramStandard', spec.ram_standard);
        setVal('editMainboardSpecs', 'ramSlots', spec.ram_slots);
        setVal('editMainboardSpecs', 'maxRamSpeedMhz', spec.max_ram_speed_mhz);
        setVal('editMainboardSpecs', 'maxRamCapacityGb', spec.max_ram_capacity_gb);
        setVal('editMainboardSpecs', 'm2SlotsCount', spec.m2_slots_count);
        setVal('editMainboardSpecs', 'sataPortsCount', spec.sata_ports_count);
        setVal('editMainboardSpecs', 'pcieGeneration', spec.pcie_generation);
        setVal('editMainboardSpecs', 'cpuPowerConnectors', spec.cpu_power_connectors);
        setVal('editMainboardSpecs', 'supportedCpuGenerations', spec.supported_cpu_generations);
        var nvmeChk = document.getElementById('editSupportsNvme');
        if (nvmeChk) {
            nvmeChk.checked = (spec.supports_nvme === '1' || spec.supports_nvme === 'true');
        }

        setVal('editRamSpecs', 'ddrType', spec.ddr_type);
        setVal('editRamSpecs', 'capacityGb', spec.capacity_gb);
        setVal('editRamSpecs', 'busSpeedMhz', spec.bus_speed_mhz);
        setVal('editRamSpecs', 'moduleCount', spec.module_count);
        setVal('editRamSpecs', 'casLatency', spec.cas_latency);

        setVal('editVgaSpecs', 'gpuChipset', spec.gpu_chipset);
        setVal('editVgaSpecs', 'vramCapacityGb', spec.vram_capacity_gb);
        setVal('editVgaSpecs', 'vramType', spec.vram_type);
        setVal('editVgaSpecs', 'recommendedPsuWatts', spec.recommended_psu_watts);
        setVal('editVgaSpecs', 'tgpWatts', spec.tgp_watts);
        setVal('editVgaSpecs', 'lengthMm', spec.length_mm);
        setVal('editVgaSpecs', 'powerConnectors', spec.power_connectors);
        setVal('editVgaSpecs', 'pcieInterface', spec.pcie_interface);
        setVal('editVgaSpecs', 'hdmiPorts', spec.hdmi_ports);
        setVal('editVgaSpecs', 'displayportPorts', spec.displayport_ports);

        setVal('editStorageSpecs', 'storageType', spec.storage_type);
        setVal('editStorageSpecs', 'formFactor', spec.form_factor);
        setVal('editStorageSpecs', 'interface', spec.interface);
        setVal('editStorageSpecs', 'capacityGb', spec.capacity_gb);

        setVal('editPsuSpecs', 'wattage', spec.wattage);
        setVal('editPsuSpecs', 'efficiencyRating', spec.efficiency_rating);
        setVal('editPsuSpecs', 'modularType', spec.modularity);

        setVal('editCaseSpecs', 'caseFormFactor', spec.supported_motherboards);
        setVal('editCaseSpecs', 'maxVgaLengthMm', spec.max_vga_length_mm);
        setVal('editCaseSpecs', 'maxCpuCoolerHeightMm', spec.max_cpu_cooler_height_mm);

        setVal('editCoolerSpecs', 'coolerType', spec.cooler_type);
        setVal('editCoolerSpecs', 'heightMm', spec.height_mm);
        setVal('editCoolerSpecs', 'radiatorSizeMm', spec.radiator_size_mm);
        setVal('editCoolerSpecs', 'maxTdp', spec.max_tdp_cooling_watts);
        setVal('editCoolerSpecs', 'supportedSockets', spec.supported_sockets);

        setVal('editMonitorSpecs', 'screenSize', spec.screen_size_inch);
        setVal('editMonitorSpecs', 'resolution', spec.resolution);
        setVal('editMonitorSpecs', 'refreshRate', spec.refresh_rate_hz);
        setVal('editMonitorSpecs', 'panelType', spec.panel_type);
        setVal('editMonitorSpecs', 'responseTimeMs', spec.response_time_ms);
        setVal('editMonitorSpecs', 'adaptiveSync', spec.adaptive_sync);
        setVal('editMonitorSpecs', 'hdmiPorts', spec.hdmi_ports);
        setVal('editMonitorSpecs', 'displayportPorts', spec.displayport_ports);
        setVal('editMonitorSpecs', 'hdmiVersion', spec.hdmi_version);
        setVal('editMonitorSpecs', 'displayportVersion', spec.displayport_version);

        setVal('editGearSpecs', 'gearType', spec.peripheral_type);
        setVal('editGearSpecs', 'connectivity', spec.connection_type);
        setVal('editGearSpecs', 'switchOrDpi', spec.switch_type);
        setVal('editGearSpecs', 'layout', spec.layout);
        setVal('editGearSpecs', 'dpiMax', spec.dpi_max);
        setVal('editGearSpecs', 'pollingRateHz', spec.polling_rate_hz);
        setVal('editGearSpecs', 'weightG', spec.weight_g);
    }

    var editCatEl = document.getElementById('editCategory');
    if (editCatEl) {
        editCatEl.addEventListener('change', function () {
            toggleEditSpecSection();
            syncSpecDisabled();
        });
    }

    document.querySelectorAll('.btn-edit-part').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var d = btn.dataset;
            document.getElementById('editPartId').value = d.id;
            document.getElementById('editCategory').value = d.category;
            document.getElementById('editBrand').value = d.brand;
            document.getElementById('editCode').value = d.code;
            document.getElementById('editName').value = d.name;
            document.getElementById('editBase').value = d.base;
            document.getElementById('editSale').value = d.sale;
            document.getElementById('editWarranty').value = d.warranty;
            document.getElementById('editDesc').value = d.desc || '';
            toggleEditSpecSection();
            fillEditSpecFields(d.id);
            syncSpecDisabled();
        });
    });
</script>

<jsp:include page="/views/components/footer.jsp" />