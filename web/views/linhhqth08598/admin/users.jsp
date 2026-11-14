<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-users-cog me-2"></i>Quản lý Tài khoản & Nhân viên</h1>
</div>

<!-- Thông báo thành công / lỗi -->
<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-1"></i> ${sessionScope.message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<!-- Bảng danh sách người dùng -->
<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Họ tên</th>
                        <th>Email</th>
                        <th>Số điện thoại</th>
                        <th>Xác thực</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${users}">
                        <tr>
                            <td>#${u.userId}</td>
                            <td class="fw-bold">${u.fullName}</td>
                            <td>${u.email}</td>
                            <td>${u.phoneNumber != null ? u.phoneNumber : '<span class="text-muted italic">Chưa cập nhật</span>'}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${u.isEmailVerified}">
                                        <span class="badge bg-success">Đã xác thực</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-warning text-dark">Chưa xác thực</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${u.status == 'ACTIVE'}">
                                        <span class="badge bg-primary">Hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger">Đã khóa</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <!-- Nút Khóa / Mở khóa -->
                                <form action="${pageContext.request.contextPath}/admin/users" method="POST" class="d-inline">
                                    <input type="hidden" name="action" value="toggleStatus">
                                    <input type="hidden" name="userId" value="${u.userId}">
                                    <c:choose>
                                        <c:when test="${u.status == 'ACTIVE'}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc chắn muốn KHÓA tài khoản ${u.email} này không?');" title="Khóa tài khoản">
                                                <i class="fas fa-lock"></i> Khóa
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="submit" class="btn btn-sm btn-outline-success" onclick="return confirm('Bạn có chắc chắn muốn MỞ KHÓA tài khoản ${u.email} này không?');" title="Mở khóa tài khoản">
                                                <i class="fas fa-unlock"></i> Mở khóa
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />