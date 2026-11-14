<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <div>
        <h1 class="h2 mb-3"><i class="fas fa-trademark me-2"></i>Quản lý Thương hiệu</h1>
        <ul class="nav nav-tabs">
            <li class="nav-item">
                <a class="nav-link ${param.type == 'brand' || empty param.type ? 'active fw-bold' : ''}" 
                   href="${pageContext.request.contextPath}/admin/catalog?type=brand">
                    <i class="fas fa-trademark me-1"></i> Thương hiệu
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${param.type == 'category' ? 'active fw-bold' : ''}" 
                   href="${pageContext.request.contextPath}/admin/catalog?type=category">
                    <i class="fas fa-list-alt me-1"></i> Danh mục
                </a>
            </li>
        </ul>
    </div>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addBrandModal">
        <i class="fas fa-plus me-1"></i> Thêm thương hiệu
    </button>
</div>

<!-- Thông báo -->
<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="fas fa-check-circle me-1"></i> ${sessionScope.message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show">
        <i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Logo</th>
                        <th>Tên Thương hiệu</th>
                        <th>Quốc gia</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="brand" items="${brands}">
                        <tr>
                            <td>#${brand.brandId}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty brand.logoUrl}">
                                        <img src="${brand.logoUrl}" alt="${brand.brandName}" style="width: 40px; height: 40px; object-fit: contain; border-radius: 4px;">
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted"><i class="fas fa-image"></i> Trống</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="fw-bold">${brand.brandName}</td>
                            <td>${brand.country}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${brand.status == 'ACTIVE'}"><span class="badge bg-success">Hoạt động</span></c:when>
                                    <c:otherwise><span class="badge bg-danger">Đã ẩn</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <div class="d-flex justify-content-center align-items-center gap-2">
                                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editBrandModal-${brand.brandId}">
                                        <i class="fas fa-edit"></i> Sửa
                                    </button>
                                    <form action="${pageContext.request.contextPath}/admin/catalog" method="POST" class="m-0">
                                        <input type="hidden" name="action" value="deleteBrand">
                                        <input type="hidden" name="brandId" value="${brand.brandId}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc muốn ẩn thương hiệu này?');">
                                            <i class="fas fa-eye-slash"></i> Ẩn
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>

                        <!-- Modal Sửa Brand - ĐÃ ĐƯỢC KÉO RA NGOÀI THẺ TR -->
                        <div class="modal fade" id="editBrandModal-${brand.brandId}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content border-0 shadow-lg rounded-4">
                                    <form action="${pageContext.request.contextPath}/admin/catalog" method="POST">
                                        <input type="hidden" name="action" value="saveBrand">
                                        <input type="hidden" name="brandId" value="${brand.brandId}">
                                        <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                                            <h5 class="modal-title fw-bold text-primary"><i class="fas fa-edit me-2"></i>Sửa Thương hiệu #${brand.brandId}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body p-4">
                                            <div class="mb-4">
                                                <label class="form-label fw-semibold text-dark">Tên thương hiệu</label>
                                                <div class="input-group">
                                                    <span class="input-group-text"><i class="fas fa-tag"></i></span>
                                                    <input type="text" name="brandName" class="form-control" value="${brand.brandName}" required>
                                                </div>
                                            </div>
                                            <div class="mb-4">
                                                <label class="form-label fw-semibold text-dark">Quốc gia</label>
                                                <div class="input-group">
                                                    <span class="input-group-text"><i class="fas fa-globe"></i></span>
                                                    <input type="text" name="country" class="form-control" value="${brand.country}">
                                                </div>
                                            </div>
                                            <div class="mb-4">
                                                <label class="form-label fw-semibold text-dark">Logo URL</label>
                                                <div class="input-group">
                                                    <span class="input-group-text"><i class="fas fa-link"></i></span>
                                                    <input type="text" name="logoUrl" class="form-control" value="${brand.logoUrl}">
                                                </div>
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
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal Thêm mới Brand -->
<div class="modal fade" id="addBrandModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/catalog" method="POST">
                <input type="hidden" name="action" value="saveBrand">
                <input type="hidden" name="brandId" value="0">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-plus-circle me-2"></i>Thêm Thương hiệu mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-4">
                        <label class="form-label fw-semibold text-dark">Tên thương hiệu</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-tag"></i></span>
                            <input type="text" name="brandName" class="form-control" required placeholder="VD: Asus, MSI...">
                        </div>
                    </div>
                    <div class="mb-4">
                        <label class="form-label fw-semibold text-dark">Quốc gia</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-globe"></i></span>
                            <input type="text" name="country" class="form-control" placeholder="VD: Đài Loan, Mỹ...">
                        </div>
                    </div>
                    <div class="mb-4">
                        <label class="form-label fw-semibold text-dark">Logo URL</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-link"></i></span>
                            <input type="text" name="logoUrl" class="form-control" placeholder="https://...">
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4"><i class="fas fa-plus me-1"></i> Thêm mới</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />