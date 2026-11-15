<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<%-- Query string hiện tại (để sau khi sửa / khóa vẫn quay lại đúng trang đang lọc) --%>
<c:url var="currentUrl" value="/admin/users">
    <c:param name="q" value="${fKeyword}"/>
    <c:param name="status" value="${fStatus}"/>
    <c:param name="role" value="${fRole}"/>
    <c:param name="page" value="${pagination.currentPage}"/>
</c:url>
<c:set var="backQuery" value="?${fn:substringAfter(currentUrl, '?')}" />

<div class="d-flex justify-content-between flex-wrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2 mb-0"><i class="fas fa-users-cog me-2"></i>Quản lý Tài khoản & Nhân viên</h1>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createUserModal">
        <i class="fas fa-user-plus me-1"></i> Thêm tài khoản
    </button>
</div>

<!-- Thông báo -->
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
<c:if test="${not empty sessionScope.tempPassword}">
    <div class="alert alert-warning alert-dismissible fade show" role="alert">
        <i class="fas fa-key me-1"></i>
        Mật khẩu tạm của <strong><c:out value="${sessionScope.tempPasswordEmail}"/></strong>:
        <code class="fs-6 user-select-all"><c:out value="${sessionScope.tempPassword}"/></code>
        <div class="small mt-1">Mật khẩu chỉ hiển thị <b>một lần</b>. Hãy gửi cho người dùng và yêu cầu đổi mật khẩu sau khi đăng nhập.</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="tempPassword" scope="session"/>
    <c:remove var="tempPasswordEmail" scope="session"/>
</c:if>

<!-- Bộ lọc -->
<div class="card shadow-sm border-0 rounded-4 mb-3">
    <div class="card-body">
        <form method="GET" action="${pageContext.request.contextPath}/admin/users" class="row g-2 align-items-center">
            <div class="col-md-5">
                <input type="text" name="q" class="form-control" placeholder="Tìm theo tên, email hoặc số điện thoại..." value="<c:out value='${fKeyword}'/>">
            </div>
            <div class="col-md-3">
                <select name="role" class="form-select">
                    <option value="">Tất cả vai trò</option>
                    <c:forEach var="r" items="${allRoles}">
                        <option value="${r.roleCode}" ${fRole == r.roleCode ? 'selected' : ''}><c:out value="${r.roleName}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <select name="status" class="form-select">
                    <option value="">Mọi trạng thái</option>
                    <option value="ACTIVE" ${fStatus == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                    <option value="LOCKED" ${fStatus == 'LOCKED' ? 'selected' : ''}>Đã khóa</option>
                </select>
            </div>
            <div class="col-md-2 d-flex gap-2">
                <button type="submit" class="btn btn-primary flex-fill"><i class="fas fa-search"></i> Lọc</button>
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary" title="Xóa bộ lọc"><i class="fas fa-times"></i></a>
            </div>
        </form>
    </div>
</div>

<!-- Bảng danh sách người dùng -->
<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="text-muted small mb-2">Tìm thấy <strong>${totalUsers}</strong> tài khoản</div>
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Họ tên / Email</th>
                        <th>Số điện thoại</th>
                        <th>Vai trò</th>
                        <th>Xác thực</th>
                        <th>Trạng thái</th>
                        <th>Đăng nhập cuối</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:if test="${empty users}">
                        <tr><td colspan="8" class="text-center text-muted py-4">Không có tài khoản nào phù hợp.</td></tr>
                    </c:if>
                    <c:forEach var="u" items="${users}">
                        <c:set var="isSelf" value="${u.userId == sessionScope.loggedUser.userId}" />
                        <c:set var="rolesOfUser" value="${userRolesMap[u.userId]}" />
                        <c:set var="roleIdCsv" value="" />
                        <c:set var="targetPrivileged" value="false" />
                        <c:forEach var="ro" items="${rolesOfUser}" varStatus="st">
                            <c:set var="roleIdCsv" value="${roleIdCsv}${st.first ? '' : ','}${ro.roleId}" />
                            <c:if test="${ro.roleCode == 'ADMIN' || ro.roleCode == 'OWNER'}"><c:set var="targetPrivileged" value="true" /></c:if>
                        </c:forEach>
                        <c:set var="canEdit" value="${isAdminActor || isSelf || !targetPrivileged}" />

                        <tr>
                            <td>#${u.userId}</td>
                            <td>
                                <div class="fw-bold"><c:out value="${u.fullName}"/> <c:if test="${isSelf}"><span class="badge bg-info text-dark ms-1">Bạn</span></c:if></div>
                                <div class="text-muted small"><c:out value="${u.email}"/></div>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty u.phoneNumber}"><c:out value="${u.phoneNumber}"/></c:when>
                                    <c:otherwise><span class="text-muted fst-italic">Chưa cập nhật</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:forEach var="ro" items="${rolesOfUser}">
                                    <c:choose>
                                        <c:when test="${ro.roleCode == 'ADMIN'}"><c:set var="rc" value="bg-danger"/></c:when>
                                        <c:when test="${ro.roleCode == 'OWNER'}"><c:set var="rc" value="bg-dark"/></c:when>
                                        <c:when test="${ro.roleCode == 'STAFF'}"><c:set var="rc" value="bg-primary"/></c:when>
                                        <c:otherwise><c:set var="rc" value="bg-secondary"/></c:otherwise>
                                    </c:choose>
                                    <span class="badge ${rc} me-1"><c:out value="${ro.roleName}"/></span>
                                </c:forEach>
                                <c:if test="${empty rolesOfUser}"><span class="text-muted fst-italic">Chưa có</span></c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${u.isEmailVerified}"><span class="badge bg-success">Đã xác thực</span></c:when>
                                    <c:otherwise><span class="badge bg-warning text-dark">Chưa xác thực</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${u.status == 'ACTIVE'}"><span class="badge bg-primary">Hoạt động</span></c:when>
                                    <c:otherwise><span class="badge bg-danger">Đã khóa</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="small text-muted">
                                <c:choose>
                                    <c:when test="${not empty u.lastLoginAt}"><fmt:formatDate value="${u.lastLoginAt}" pattern="dd/MM/yyyy HH:mm"/></c:when>
                                    <c:otherwise>—</c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center text-nowrap">
                                <c:if test="${canEdit}">
                                    <!-- Sửa -->
                                    <button type="button" class="btn btn-sm btn-outline-primary btn-edit-user" title="Sửa tài khoản"
                                            data-bs-toggle="modal" data-bs-target="#editUserModal"
                                            data-id="${u.userId}"
                                            data-email="<c:out value='${u.email}'/>"
                                            data-name="<c:out value='${u.fullName}'/>"
                                            data-phone="<c:out value='${u.phoneNumber}'/>"
                                            data-roles="${roleIdCsv}"
                                            data-self="${isSelf}">
                                        <i class="fas fa-pen"></i> Sửa
                                    </button>
                                </c:if>

                                <c:if test="${!isSelf && canEdit}">
                                    <!-- Đặt lại mật khẩu -->
                                    <form action="${pageContext.request.contextPath}/admin/users" method="POST" class="d-inline js-confirm"
                                          data-confirm="Đặt lại mật khẩu cho <c:out value='${u.email}'/>? Mật khẩu cũ sẽ không dùng được nữa.">
                                        <input type="hidden" name="action" value="resetPassword">
                                        <input type="hidden" name="userId" value="${u.userId}">
                                        <input type="hidden" name="back" value="${backQuery}">
                                        <button type="submit" class="btn btn-sm btn-outline-warning" title="Đặt lại mật khẩu"><i class="fas fa-key"></i></button>
                                    </form>

                                    <!-- Khóa / Mở khóa -->
                                    <form action="${pageContext.request.contextPath}/admin/users" method="POST" class="d-inline js-confirm"
                                          data-confirm="${u.status == 'ACTIVE' ? 'KHÓA' : 'MỞ KHÓA'} tài khoản <c:out value='${u.email}'/>?">
                                        <input type="hidden" name="action" value="toggleStatus">
                                        <input type="hidden" name="userId" value="${u.userId}">
                                        <input type="hidden" name="back" value="${backQuery}">
                                        <c:choose>
                                            <c:when test="${u.status == 'ACTIVE'}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Khóa tài khoản"><i class="fas fa-lock"></i> Khóa</button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="submit" class="btn btn-sm btn-outline-success" title="Mở khóa tài khoản"><i class="fas fa-unlock"></i> Mở khóa</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Phân trang -->
        <c:if test="${pagination.totalPages > 1}">
            <nav class="mt-3">
                <ul class="pagination justify-content-center mb-0">
                    <c:url var="prevUrl" value="/admin/users">
                        <c:param name="q" value="${fKeyword}"/><c:param name="status" value="${fStatus}"/><c:param name="role" value="${fRole}"/>
                        <c:param name="page" value="${pagination.currentPage - 1}"/>
                    </c:url>
                    <li class="page-item ${pagination.hasPrevious() ? '' : 'disabled'}"><a class="page-link" href="${prevUrl}">&laquo;</a></li>
                    <c:forEach begin="1" end="${pagination.totalPages}" var="p">
                        <c:url var="pUrl" value="/admin/users">
                            <c:param name="q" value="${fKeyword}"/><c:param name="status" value="${fStatus}"/><c:param name="role" value="${fRole}"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <li class="page-item ${p == pagination.currentPage ? 'active' : ''}"><a class="page-link" href="${pUrl}">${p}</a></li>
                    </c:forEach>
                    <c:url var="nextUrl" value="/admin/users">
                        <c:param name="q" value="${fKeyword}"/><c:param name="status" value="${fStatus}"/><c:param name="role" value="${fRole}"/>
                        <c:param name="page" value="${pagination.currentPage + 1}"/>
                    </c:url>
                    <li class="page-item ${pagination.hasNext() ? '' : 'disabled'}"><a class="page-link" href="${nextUrl}">&raquo;</a></li>
                </ul>
            </nav>
        </c:if>
    </div>
</div>

<!-- ============ MODAL: THÊM TÀI KHOẢN ============ -->
<div class="modal fade" id="createUserModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <form class="modal-content" action="${pageContext.request.contextPath}/admin/users" method="POST">
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="back" value="${backQuery}">
            <div class="modal-header">
                <h5 class="modal-title fw-bold"><i class="fas fa-user-plus me-2"></i>Thêm tài khoản</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label">Email <span class="text-danger">*</span></label>
                    <input type="email" name="email" class="form-control" required maxlength="255">
                </div>
                <div class="mb-3">
                    <label class="form-label">Họ và tên <span class="text-danger">*</span></label>
                    <input type="text" name="fullName" class="form-control" required minlength="2" maxlength="100">
                </div>
                <div class="mb-3">
                    <label class="form-label">Số điện thoại</label>
                    <input type="text" name="phone" class="form-control" maxlength="12" placeholder="VD: 0912345678">
                </div>
                <div class="mb-3">
                    <label class="form-label">Mật khẩu <span class="text-danger">*</span></label>
                    <input type="password" name="password" class="form-control" required minlength="8" autocomplete="new-password">
                    <div class="form-text">Tối thiểu 8 ký tự, gồm chữ hoa, chữ thường và số.</div>
                </div>
                <div class="mb-1">
                    <label class="form-label d-block">Vai trò <span class="text-danger">*</span></label>
                    <c:forEach var="r" items="${allRoles}">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="roleIds" value="${r.roleId}" id="cr_${r.roleId}"
                                   ${r.roleCode == 'STAFF' ? 'checked' : ''}
                                   ${(r.roleCode == 'ADMIN' || r.roleCode == 'OWNER') && !isAdminActor ? 'disabled' : ''}>
                            <label class="form-check-label" for="cr_${r.roleId}">
                                <c:out value="${r.roleName}"/> <small class="text-muted">(${r.roleCode})</small>
                            </label>
                        </div>
                    </c:forEach>
                    <c:if test="${!isAdminActor}"><div class="form-text">Chỉ Quản trị viên mới được cấp vai trò Quản trị viên / Chủ cửa hàng.</div></c:if>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary"><i class="fas fa-save me-1"></i> Tạo tài khoản</button>
            </div>
        </form>
    </div>
</div>

<!-- ============ MODAL: SỬA TÀI KHOẢN ============ -->
<div class="modal fade" id="editUserModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <form class="modal-content" action="${pageContext.request.contextPath}/admin/users" method="POST">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="userId" id="eu_id">
            <input type="hidden" name="back" value="${backQuery}">
            <div class="modal-header">
                <h5 class="modal-title fw-bold"><i class="fas fa-user-edit me-2"></i>Sửa tài khoản</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label class="form-label">Email (không thể thay đổi)</label>
                    <input type="text" id="eu_email" class="form-control bg-light" readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label">Họ và tên <span class="text-danger">*</span></label>
                    <input type="text" name="fullName" id="eu_name" class="form-control" required minlength="2" maxlength="100">
                </div>
                <div class="mb-3">
                    <label class="form-label">Số điện thoại</label>
                    <input type="text" name="phone" id="eu_phone" class="form-control" maxlength="12" placeholder="VD: 0912345678">
                </div>
                <div class="mb-1">
                    <label class="form-label d-block">Vai trò</label>
                    <c:forEach var="r" items="${allRoles}">
                        <div class="form-check">
                            <input class="form-check-input eu-role" type="checkbox" name="roleIds" value="${r.roleId}" id="er_${r.roleId}"
                                   data-code="${r.roleCode}" data-privileged="${r.roleCode == 'ADMIN' || r.roleCode == 'OWNER'}">
                            <label class="form-check-label" for="er_${r.roleId}">
                                <c:out value="${r.roleName}"/> <small class="text-muted">(${r.roleCode})</small>
                            </label>
                        </div>
                    </c:forEach>
                    <div class="form-text" id="eu_roleNote"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" class="btn btn-primary"><i class="fas fa-save me-1"></i> Lưu thay đổi</button>
            </div>
        </form>
    </div>
</div>

<script>
    (function () {
        var isAdminActor = ${isAdminActor ? 'true' : 'false'};

        // Xác nhận trước khi khóa / mở khóa / đặt lại mật khẩu
        document.querySelectorAll('form.js-confirm').forEach(function (f) {
            f.addEventListener('submit', function (e) {
                if (!confirm(f.getAttribute('data-confirm'))) e.preventDefault();
            });
        });

        // Đổ dữ liệu vào modal Sửa
        document.getElementById('editUserModal').addEventListener('show.bs.modal', function (ev) {
            var b = ev.relatedTarget;
            if (!b) return;
            var isSelf = b.getAttribute('data-self') === 'true';
            document.getElementById('eu_id').value = b.getAttribute('data-id');
            document.getElementById('eu_email').value = b.getAttribute('data-email');
            document.getElementById('eu_name').value = b.getAttribute('data-name');
            document.getElementById('eu_phone').value = b.getAttribute('data-phone') || '';

            var current = (b.getAttribute('data-roles') || '').split(',');
            document.querySelectorAll('.eu-role').forEach(function (cb) {
                cb.checked = current.indexOf(cb.value) !== -1;
                var privileged = cb.getAttribute('data-privileged') === 'true';
                // Tự sửa mình: khóa toàn bộ vai trò. Không phải ADMIN: không được đụng vai trò ADMIN/OWNER
                cb.disabled = isSelf || (privileged && !isAdminActor);
            });
            document.getElementById('eu_roleNote').textContent = isSelf
                ? 'Bạn không thể tự thay đổi vai trò của chính mình.'
                : (isAdminActor ? '' : 'Chỉ Quản trị viên mới được cấp vai trò Quản trị viên / Chủ cửa hàng.');
        });
    })();
</script>

<jsp:include page="/views/components/footer.jsp" />
