<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

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
                                    <!-- Nút Sửa -->
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
        });
    });
</script>

<jsp:include page="/views/components/footer.jsp" />