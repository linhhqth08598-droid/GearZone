<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!-- Nạp Header và Sidebar của Admin -->
<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 class="h2"><i class="fas fa-history me-2"></i>Lịch sử Thao tác Nhân viên (Audit Logs)</h1>
    </div>

    <!-- Thanh tìm kiếm / lọc -->
    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body p-3">
            <form action="${pageContext.request.contextPath}/admin/audit-logs" method="GET" class="row g-3 align-items-center">
                <div class="col-auto">
                    <input type="text" name="keyword" class="form-control" placeholder="Tìm ID, Action, Bảng..." value="${param.keyword}">
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-primary"><i class="fas fa-search me-1"></i> Lọc dữ liệu</button>
                    <a href="${pageContext.request.contextPath}/admin/audit-logs" class="btn btn-outline-secondary">Xóa bộ lọc</a>
                </div>
            </form>
        </div>
    </div>

    <!-- Bảng danh sách Audit Logs -->
    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-4">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>ID Log</th>
                            <th>Mã NV (Staff ID)</th>
                            <th>Hành động (Action)</th>
                            <th>Bảng tác động</th>
                            <th>ID Mục tiêu</th>
                            <th>Mô tả chi tiết</th>
                            <th>Thời gian</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty auditLogs}">
                                <tr>
                                    <td colspan="7" class="text-center text-muted py-4">
                                        <i class="fas fa-info-circle me-1"></i> Không có dữ liệu lịch sử thao tác nào.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="log" items="${auditLogs}">
                                    <tr>
                                        <td>#${log.logId}</td>
                                        <td><span class="badge bg-dark">Staff #${log.staffId}</span></td>
                                        <td>
                                            <!-- Đổi màu badge tùy theo Action để dễ nhìn -->
                                            <c:choose>
                                                <c:when test="${log.action == 'INSERT' || log.action == 'CREATE'}">
                                                    <span class="badge bg-success">${log.action}</span>
                                                </c:when>
                                                <c:when test="${log.action == 'UPDATE'}">
                                                    <span class="badge bg-warning text-dark">${log.action}</span>
                                                </c:when>
                                                <c:when test="${log.action == 'DELETE'}">
                                                    <span class="badge bg-danger">${log.action}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <code class="text-primary fw-bold">${log.action}</code>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><span class="badge bg-secondary">${log.targetTable}</span></td>
                                        <td>${log.targetId != 0 ? log.targetId : '-'}</td>
                                        <td class="text-wrap" style="max-width: 250px;">${log.description}</td>
                                        <td><fmt:formatDate value="${log.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/views/components/footer.jsp" />