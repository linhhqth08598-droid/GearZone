<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<style>
    /* Ép chiều cao đồng bộ cho tất cả input, date, button trong thanh tìm kiếm */
    .search-form .form-control,
    .search-form .form-select,
    .search-form .btn {
        height: 42px !important;
        font-size: 14px;
    }
</style>

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-history me-2"></i>Lịch sử Thao tác Nhân viên (Audit Logs)</h1>
</div>

<!-- Thanh tìm kiếm / lọc -->
<div class="card shadow-sm border-0 rounded-4 mb-4">
    <div class="card-body p-3">
        <form action="${pageContext.request.contextPath}/admin/audit-logs" method="GET" class="row g-3 search-form">
            <div class="col-md-3">
                <label class="form-label text-muted" style="font-size: 13px;">Từ khóa</label>
                <input type="text" name="keyword" class="form-control" placeholder="Tìm ID, Action, Bảng..." value="${param.keyword}">
            </div>

            <div class="col-md-2">
                <label class="form-label text-muted" style="font-size: 13px;">Từ ngày</label>
                <input type="date" name="fromDate" class="form-control" value="${param.fromDate}">
            </div>

            <div class="col-md-2">
                <label class="form-label text-muted" style="font-size: 13px;">Đến ngày</label>
                <input type="date" name="toDate" class="form-control" value="${param.toDate}">
            </div>

            <!-- Dùng col-md-5 và align-self-end để nút thẳng hàng với input -->
            <div class="col-md-5 d-flex align-items-end gap-2">
                <!-- Thêm d-inline-flex align-items-center justify-content-center cho cả 2 nút -->
                <button type="submit" class="btn btn-primary d-inline-flex align-items-center justify-content-center" style="height: 42px;">
                    <i class="fas fa-search me-1"></i> Tìm kiếm
                </button>
                <a href="${pageContext.request.contextPath}/admin/audit-logs" class="btn btn-outline-secondary d-inline-flex align-items-center justify-content-center" style="height: 42px;">
                    <i class="fas fa-times me-1"></i> Xóa bộ lọc
                </a>
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
                                        <c:choose>
                                            <c:when test="${log.action.contains('INSERT') || log.action.contains('CREATE')}">
                                                <span class="badge bg-success">${log.action}</span>
                                            </c:when>
                                            <c:when test="${log.action.contains('UPDATE')}">
                                                <span class="badge bg-warning text-dark">${log.action}</span>
                                            </c:when>
                                            <c:when test="${log.action.contains('DELETE')}">
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

<jsp:include page="/views/components/footer.jsp" />