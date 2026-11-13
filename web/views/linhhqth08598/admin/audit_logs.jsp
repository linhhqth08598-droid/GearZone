<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
    <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <h1 class="h2"><i class="fas fa-history me-2"></i>Lịch sử Thao tác Nhân viên (Audit Logs)</h1>
    </div>

    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-4">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>ID Log</th>
                            <th>Mã nhân viên (Staff ID)</th>
                            <th>Hành động (Action)</th>
                            <th>Bảng tác động</th>
                            <th>ID Mục tiêu</th>
                            <th>Mô tả chi tiết</th>
                            <th>Thời gian</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="log" items="${auditLogs}">
                            <tr>
                                <td>#${log.logId}</td>
                                <td><span class="badge bg-dark">Staff #${log.staffId}</span></td>
                                <td><code class="text-primary fw-bold">${log.action}</code></td>
                                <td>${log.targetTable}</td>
                                <td>${log.targetId != 0 ? log.targetId : '-'}</td>
                                <td>${log.description}</td>
                                <td><fmt:formatDate value="${log.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/views/components/footer.jsp" />