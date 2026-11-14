<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 class="h2"><i class="fas fa-shield-alt me-2"></i>Phân quyền & Vai trò hệ thống</h1>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-1"></i> ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <div class="row">
        <!-- Cột chọn Vai trò -->
        <div class="col-md-4">
            <div class="card shadow-sm border-0 rounded-4 mb-4">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold text-dark">Danh sách Vai trò (Roles)</h5>
                </div>
                <div class="list-group list-group-flush rounded-bottom-4">
                    <c:forEach var="r" items="${roles}">
                        <a href="${pageContext.request.contextPath}/admin/roles?roleId=${r.roleId}" 
                           class="list-group-item list-group-item-action d-flex justify-content-between align-items-center py-3 ${selectedRoleId == r.roleId ? 'active bg-danger text-white border-danger' : ''}">
                            <div>
                                <div class="fw-bold">${r.roleName}</div>
                                <small class="${selectedRoleId == r.roleId ? 'text-light' : 'text-muted'}">${r.description}</small>
                            </div>
                            <span class="badge bg-light text-dark rounded-pill">${r.roleCode}</span>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>

        <!-- Cột cấu hình Quyền (Permissions Matrix) -->
        <div class="col-md-8">
            <div class="card shadow-sm border-0 rounded-4">
                <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                    <h5 class="mb-0 fw-bold text-dark">Ma trận Quyền hạn chi tiết</h5>
                </div>
                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/admin/roles" method="POST">
                        <input type="hidden" name="roleId" value="${selectedRoleId}">
                        
                        <div class="table-responsive">
                            <table class="table table-striped align-middle">
                                <thead>
                                    <tr>
                                        <th style="width: 10%;">Chọn</th>
                                        <th style="width: 30%;">Mã quyền</th>
                                        <th style="width: 35%;">Tên chức năng</th>
                                        <th style="width: 25%;">Module</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${permissions}">
                                        <tr>
                                            <td class="text-center">
                                                <input class="form-check-input" type="checkbox" name="permissions" value="${p.permissionId}" 
                                                       <c:if test="${assignedPermissions.contains(p.permissionId)}">checked</c:if>>
                                            </td>
                                            <td><code class="text-danger">${p.permissionCode}</code></td>
                                            <td class="fw-semibold">${p.permissionName}</td>
                                            <td><span class="badge bg-secondary">${p.module}</span></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="text-end mt-4">
                            <button type="submit" class="btn btn-primary px-4 py-2">
                                <i class="fas fa-save me-1"></i> Lưu thay đổi quyền
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

<jsp:include page="/views/components/footer.jsp" />