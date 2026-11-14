<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-microchip me-2"></i>Quản lý Socket CPU</h1>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addSocketModal">
        <i class="fas fa-plus me-1"></i> Thêm Socket
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
                        <th>Tên Socket</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${sockets}">
                        <tr>
                            <td>#${s.socketId}</td>
                            <td class="fw-bold">${s.socketName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${s.status == 'ACTIVE'}"><span class="badge bg-success">Hoạt động</span></c:when>
                                    <c:otherwise><span class="badge bg-danger">Đã ẩn</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <div class="d-flex justify-content-center align-items-center gap-2">
                                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editSocketModal-${s.socketId}">
                                        <i class="fas fa-edit"></i> Sửa
                                    </button>
                                    <form action="${pageContext.request.contextPath}/admin/socket" method="POST" class="m-0">
                                        <input type="hidden" name="action" value="deleteSocket">
                                        <input type="hidden" name="socketId" value="${s.socketId}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc muốn ẩn Socket này?');">
                                            <i class="fas fa-eye-slash"></i> Ẩn
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>

                        <!-- Modal Sửa Socket -->
                        <div class="modal fade" id="editSocketModal-${s.socketId}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content border-0 shadow-lg rounded-4">
                                    <form action="${pageContext.request.contextPath}/admin/socket" method="POST">
                                        <input type="hidden" name="action" value="saveSocket">
                                        <input type="hidden" name="socketId" value="${s.socketId}">
                                        <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                                            <h5 class="modal-title fw-bold text-primary"><i class="fas fa-edit me-2"></i>Sửa Socket #${s.socketId}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body p-4">
                                            <div class="mb-4">
                                                <label class="form-label fw-semibold text-dark">Tên Socket</label>
                                                <div class="input-group">
                                                    <span class="input-group-text"><i class="fas fa-microchip"></i></span>
                                                    <input type="text" name="socketName" class="form-control" value="${s.socketName}" required>
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

<!-- Modal Thêm mới Socket -->
<div class="modal fade" id="addSocketModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/socket" method="POST">
                <input type="hidden" name="action" value="saveSocket">
                <input type="hidden" name="socketId" value="0">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-plus-circle me-2"></i>Thêm Socket mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-4">
                        <label class="form-label fw-semibold text-dark">Tên Socket</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-microchip"></i></span>
                            <input type="text" name="socketName" class="form-control" required placeholder="VD: LGA 1700, AM4, AM5...">
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