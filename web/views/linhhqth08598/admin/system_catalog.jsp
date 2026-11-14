<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <div>
        <h1 class="h2 mb-3"><i class="fas fa-cogs me-2"></i>Danh mục hệ thống</h1>
        <ul class="nav nav-tabs">
            <li class="nav-item">
                <a class="nav-link ${currentType == 'chipset' ? 'active fw-bold' : ''}" href="${pageContext.request.contextPath}/admin/system-catalog?type=chipset">Chipset Mainboard</a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${currentType == 'ram' ? 'active fw-bold' : ''}" href="${pageContext.request.contextPath}/admin/system-catalog?type=ram">Chuẩn RAM</a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${currentType == 'storage' ? 'active fw-bold' : ''}" href="${pageContext.request.contextPath}/admin/system-catalog?type=storage">Chuẩn Ổ cứng</a>
            </li>
        </ul>
    </div>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addModal">
        <i class="fas fa-plus me-1"></i> Thêm mới
    </button>
</div>

<!-- Thông báo -->
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
                        <th>Tên Thuộc Tính</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${listItems}">
                        <tr>
                            <td>#${item.id}</td>
                            <td class="fw-bold">${item.name}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.status == 'ACTIVE'}"><span class="badge bg-success">Hoạt động</span></c:when>
                                    <c:otherwise><span class="badge bg-danger">Đã ẩn</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <div class="d-flex justify-content-center align-items-center gap-2">
                                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editModal-${item.id}"><i class="fas fa-edit"></i> Sửa</button>
                                    <form action="${pageContext.request.contextPath}/admin/system-catalog" method="POST" class="m-0">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="type" value="${currentType}">
                                        <input type="hidden" name="id" value="${item.id}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc muốn ẩn?');"><i class="fas fa-eye-slash"></i> Ẩn</button>
                                    </form>
                                </div>
                            </td>
                        </tr>

                        <!-- Modal Sửa -->
                        <div class="modal fade" id="editModal-${item.id}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content border-0 shadow-lg rounded-4">
                                    <form action="${pageContext.request.contextPath}/admin/system-catalog" method="POST">
                                        <input type="hidden" name="action" value="save">
                                        <input type="hidden" name="type" value="${currentType}">
                                        <input type="hidden" name="id" value="${item.id}">
                                        <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                                            <h5 class="modal-title fw-bold text-primary">Sửa Thuộc Tính #${item.id}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body p-4">
                                            <div class="mb-4">
                                                <label class="form-label fw-semibold text-dark">Tên</label>
                                                <input type="text" name="name" class="form-control" value="${item.name}" required>
                                            </div>
                                        </div>
                                        <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                                            <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                                            <button type="submit" class="btn btn-primary px-4">Lưu thay đổi</button>
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

<!-- Modal Thêm mới -->
<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/system-catalog" method="POST">
                <input type="hidden" name="action" value="save">
                <input type="hidden" name="type" value="${currentType}">
                <input type="hidden" name="id" value="0">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary">Thêm Thuộc Tính Mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-4">
                        <label class="form-label fw-semibold text-dark">Tên</label>
                        <input type="text" name="name" class="form-control" required placeholder="Nhập tên...">
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4">Thêm mới</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />