<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-shield-alt me-2"></i>Phân quyền & Vai trò hệ thống</h1>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="row">
    <!-- Cột chọn Vai trò -->
    <div class="col-lg-4 col-md-5">
        <div class="card shadow-sm border-0 rounded-4 mb-4">
            <div class="card-header bg-white py-3">
                <h5 class="mb-0 fw-bold text-dark">Danh sách Vai trò (Roles)</h5>
            </div>
            <div class="list-group list-group-flush rounded-bottom-4">
                <c:forEach var="r" items="${roles}">
                    <a href="${pageContext.request.contextPath}/admin/roles?roleId=${r.roleId}"
                       class="list-group-item list-group-item-action d-flex justify-content-between align-items-center py-3 ${selectedRoleId == r.roleId ? 'active bg-danger text-white border-danger' : ''}">
                        <div>
                            <div class="fw-bold"><c:out value="${r.roleName}"/></div>
                            <small class="${selectedRoleId == r.roleId ? 'text-light' : 'text-muted'}"><c:out value="${r.description}"/></small>
                        </div>
                        <span class="badge bg-light text-dark rounded-pill ms-2"><c:out value="${r.roleCode}"/></span>
                    </a>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- Cột cấu hình Quyền (Permissions Matrix) -->
    <div class="col-lg-8 col-md-7">
        <div class="card shadow-sm border-0 rounded-4">
            <div class="card-header bg-white py-3">
                <h5 class="mb-0 fw-bold text-dark">
                    Ma trận Quyền hạn chi tiết
                    <c:if test="${not empty selectedRole}"> — <span class="text-primary"><c:out value="${selectedRole.roleName}"/></span></c:if>
                </h5>
            </div>
            <div class="card-body p-4">

                <c:choose>
                    <%-- Chưa có dữ liệu quyền trong DB --%>
                    <c:when test="${empty permissions}">
                        <div class="alert alert-warning mb-0">
                            <i class="fas fa-database me-1"></i>
                            Bảng <code>permissions</code> đang trống nên chưa có quyền nào để cấp.
                            Hãy chạy file <code>sql/seed_permissions.sql</code> trong SQL Server rồi tải lại trang.
                        </div>
                    </c:when>

                    <c:otherwise>
                        <c:if test="${lockedRole}">
                            <div class="alert alert-info">
                                <i class="fas fa-lock me-1"></i>
                                Vai trò <strong><c:out value="${selectedRole.roleName}"/></strong> luôn có <b>toàn quyền</b> trên hệ thống
                                nên không cần (và không thể) chỉnh từng quyền.
                            </div>
                        </c:if>
                        <c:if test="${!lockedRole}">
                            <p class="text-muted small mb-3">
                                Tích chọn các chức năng mà vai trò này được phép dùng, rồi bấm <b>Lưu thay đổi quyền</b>.
                                <b>Xem</b>: được mở trang. <b>Quản lý/Xử lý</b>: được thêm, sửa, duyệt (gửi form).
                                Thay đổi có hiệu lực ngay ở lần tải trang kế tiếp của nhân viên.
                            </p>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/admin/roles" method="POST">
                            <input type="hidden" name="roleId" value="${selectedRoleId}">

                            <c:forEach var="entry" items="${groupedPermissions}">
                                <div class="border rounded-3 mb-3">
                                    <div class="d-flex justify-content-between align-items-center px-3 py-2 bg-light rounded-top-3">
                                        <strong>
                                            <c:choose>
                                                <c:when test="${not empty moduleLabels[entry.key]}"><c:out value="${moduleLabels[entry.key]}"/></c:when>
                                                <c:otherwise><c:out value="${entry.key}"/></c:otherwise>
                                            </c:choose>
                                        </strong>
                                        <c:if test="${!lockedRole}">
                                            <div class="form-check mb-0">
                                                <input class="form-check-input js-check-all" type="checkbox" id="all_${entry.key}" data-group="${entry.key}">
                                                <label class="form-check-label small" for="all_${entry.key}">Chọn tất cả</label>
                                            </div>
                                        </c:if>
                                    </div>
                                    <ul class="list-group list-group-flush">
                                        <c:forEach var="p" items="${entry.value}">
                                            <li class="list-group-item d-flex align-items-center">
                                                <input class="form-check-input me-3 perm-cb" type="checkbox" name="permissions"
                                                       value="${p.permissionId}" id="perm_${p.permissionId}" data-group="${entry.key}"
                                                       ${assignedPermissions.contains(p.permissionId) || lockedRole ? 'checked' : ''}
                                                       ${lockedRole ? 'disabled' : ''}>
                                                <label class="flex-grow-1 mb-0" for="perm_${p.permissionId}">
                                                    <span class="fw-semibold"><c:out value="${p.permissionName}"/></span>
                                                </label>
                                                <code class="ms-2"><c:out value="${p.permissionCode}"/></code>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:forEach>

                            <c:if test="${!lockedRole}">
                                <div class="text-end mt-4">
                                    <button type="submit" class="btn btn-primary px-4 py-2">
                                        <i class="fas fa-save me-1"></i> Lưu thay đổi quyền
                                    </button>
                                </div>
                            </c:if>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script>
    // "Chọn tất cả" theo từng nhóm; tự đồng bộ trạng thái ô tổng
    (function () {
        function sync(group) {
            var cbs = document.querySelectorAll('.perm-cb[data-group="' + group + '"]');
            var all = document.querySelector('.js-check-all[data-group="' + group + '"]');
            if (!all) return;
            var checked = Array.prototype.filter.call(cbs, function (c) { return c.checked; }).length;
            all.checked = checked === cbs.length;
            all.indeterminate = checked > 0 && checked < cbs.length;
        }
        document.querySelectorAll('.js-check-all').forEach(function (all) {
            var g = all.getAttribute('data-group');
            sync(g);
            all.addEventListener('change', function () {
                document.querySelectorAll('.perm-cb[data-group="' + g + '"]').forEach(function (c) { c.checked = all.checked; });
            });
        });
        document.querySelectorAll('.perm-cb').forEach(function (c) {
            c.addEventListener('change', function () { sync(c.getAttribute('data-group')); });
        });
    })();
</script>

<jsp:include page="/views/components/footer.jsp" />
