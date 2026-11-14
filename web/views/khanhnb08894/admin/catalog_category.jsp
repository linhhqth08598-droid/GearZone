<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <div>
        <h1 class="h2 mb-3"><i class="fas fa-list-alt me-2"></i>Quản lý Danh mục</h1>
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
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCategoryModal">
        <i class="fas fa-plus me-1"></i> Thêm danh mục
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
                        <th>Mã Danh mục</th>
                        <th>Tên Danh mục</th>
                        <th>Mô tả</th>
                        <th>Bắt buộc</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cat" items="${categories}">
                        <tr>
                            <td>#${cat.categoryId}</td>
                            <td><code>${cat.categoryCode}</code></td>
                            <td class="fw-bold">${cat.categoryName}</td>
                            <td class="text-wrap" style="max-width: 200px;">${cat.description}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${cat.isRequiredInBuild}"><span class="badge bg-primary">Có</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">Không</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${cat.status == 'ACTIVE'}"><span class="badge bg-success">Hoạt động</span></c:when>
                                    <c:otherwise><span class="badge bg-danger">Đã ẩn</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <div class="d-flex justify-content-center align-items-center gap-2">
                                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editCategoryModal-${cat.categoryId}">
                                        <i class="fas fa-edit"></i> Sửa
                                    </button>
                                    <form action="${pageContext.request.contextPath}/admin/catalog" method="POST" class="m-0">
                                        <input type="hidden" name="action" value="deleteCategory">
                                        <input type="hidden" name="categoryId" value="${cat.categoryId}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc muốn ẩn danh mục này?');">
                                            <i class="fas fa-eye-slash"></i> Ẩn
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>

                        <!-- Modal Sửa Category - ĐÃ ĐƯỢC ĐƯA RA NGOÀI THẺ TR -->
                        <div class="modal fade" id="editCategoryModal-${cat.categoryId}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content border-0 shadow-lg rounded-4">
                                    <form action="${pageContext.request.contextPath}/admin/catalog" method="POST">
                                        <input type="hidden" name="action" value="saveCategory">
                                        <input type="hidden" name="categoryId" value="${cat.categoryId}">
                                        <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                                            <h5 class="modal-title fw-bold text-primary"><i class="fas fa-edit me-2"></i>Sửa Danh mục #${cat.categoryId}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body p-4">
                                            <div class="mb-4">
                                                <label class="form-label fw-semibold text-dark">Mã danh mục</label>
                                                <div class="input-group">
                                                    <span class="input-group-text"><i class="fas fa-code"></i></span>
                                                    <input type="text" name="categoryCode" class="form-control" value="${cat.categoryCode}" required>
                                                </div>
                                            </div>
                                            <div class="mb-4">
                                                <label class="form-label fw-semibold text-dark">Tên danh mục</label>
                                                <div class="input-group">
                                                    <span class="input-group-text"><i class="fas fa-tag"></i></span>
                                                    <input type="text" name="categoryName" class="form-control" value="${cat.categoryName}" required>
                                                </div>
                                            </div>
                                            <div class="mb-4">
                                                <label class="form-label fw-semibold text-dark">Mô tả</label>
                                                <textarea name="description" class="form-control" rows="3">${cat.description}</textarea>
                                            </div>
                                            <div class="form-check mb-2">
                                                <input class="form-check-input" type="checkbox" name="isRequired" id="isRequired-${cat.categoryId}" ${cat.isRequiredInBuild ? 'checked' : ''}>
                                                <label class="form-check-label fw-semibold text-dark" for="isRequired-${cat.categoryId}">
                                                    Bắt buộc phải có khi Build PC
                                                </label>
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

<!-- Modal Thêm mới Category -->
<div class="modal fade" id="addCategoryModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/catalog" method="POST">
                <input type="hidden" name="action" value="saveCategory">
                <input type="hidden" name="categoryId" value="0">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-plus-circle me-2"></i>Thêm Danh mục mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-4">
                        <label class="form-label fw-semibold text-dark">Mã danh mục</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-code"></i></span>
                            <input type="text" name="categoryCode" class="form-control" required placeholder="VD: CPU, VGA, RAM...">
                        </div>
                    </div>
                    <div class="mb-4">
                        <label class="form-label fw-semibold text-dark">Tên danh mục</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-tag"></i></span>
                            <input type="text" name="categoryName" class="form-control" required placeholder="VD: Bộ vi xử lý...">
                        </div>
                    </div>
                    <div class="mb-4">
                        <label class="form-label fw-semibold text-dark">Mô tả</label>
                        <textarea name="description" class="form-control" rows="3" placeholder="Mô tả ngắn..."></textarea>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="checkbox" name="isRequired" id="isRequiredAdd">
                        <label class="form-check-label fw-semibold text-dark" for="isRequiredAdd">
                            Bắt buộc phải có khi Build PC
                        </label>
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