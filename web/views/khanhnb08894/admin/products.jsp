<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-box-open me-2"></i>Quản lý Linh kiện</h1>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addProductModal">
        <i class="fas fa-plus me-1"></i> Thêm Linh kiện
    </button>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> ${sessionScope.message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
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
                                <form action="${pageContext.request.contextPath}/admin/product" method="POST" class="m-0">
                                    <input type="hidden" name="action" value="deleteProduct">
                                    <input type="hidden" name="partId" value="${p.partId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc muốn ẩn linh kiện này?');">
                                        <i class="fas fa-eye-slash"></i> Ẩn
                                    </button>
                                </form>
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
            <form action="${pageContext.request.contextPath}/admin/product" method="POST">
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

<jsp:include page="/views/components/footer.jsp" />