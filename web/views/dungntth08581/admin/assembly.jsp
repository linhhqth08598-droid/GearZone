<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h3 class="fw-bold mb-0"><i class="fas fa-tools text-primary me-2"></i>Quản lý lắp ráp PC</h3>
        <form method="GET" action="${pageContext.request.contextPath}/admin/assembly" class="d-flex gap-2">
            <select name="status" class="form-select" onchange="this.form.submit()">
                <option value="">Tất cả trạng thái</option>
                <option value="SCHEDULED" ${status == 'SCHEDULED' ? 'selected' : ''}>Chờ xác nhận lịch</option>
                <option value="CONFIRMED" ${status == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận lịch</option>
                <option value="IN_PROGRESS" ${status == 'IN_PROGRESS' ? 'selected' : ''}>Đang lắp</option>
                <option value="TESTING" ${status == 'TESTING' ? 'selected' : ''}>Đang kiểm tra</option>
                <option value="COMPLETED" ${status == 'COMPLETED' ? 'selected' : ''}>Hoàn tất</option>
                <option value="HANDED_OVER" ${status == 'HANDED_OVER' ? 'selected' : ''}>Đã bàn giao</option>
                <option value="CANCELLED" ${status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
            </select>
        </form>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light"><tr><th>#</th><th>Khách hàng</th><th>Hình thức</th><th>Nguồn</th><th class="text-end">Phí</th><th>Ngày hẹn</th><th>Kỹ thuật viên</th><th>Trạng thái</th><th style="min-width:320px">Thao tác</th></tr></thead>
                <tbody>
                    <c:forEach var="r" items="${rows}">
                        <tr>
                            <td class="fw-bold">${r.assemblyId}</td>
                            <td><c:out value="${r.customerName}"/><div class="small text-muted"><c:out value="${r.phone}"/></div></td>
                            <td><c:out value="${r.typeLabel}"/><c:if test="${not empty r.note}"><div class="small text-muted"><c:out value="${r.note}"/></div></c:if></td>
                            <td class="small"><c:if test="${not empty r.orderId}">Đơn #${r.orderId}</c:if><c:if test="${not empty r.buildId}"> Cấu hình #${r.buildId}</c:if></td>
                            <td class="text-end"><fmt:formatNumber value="${r.fee}" type="number" maxFractionDigits="0"/> ₫</td>
                            <td><fmt:formatDate value="${r.scheduledDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td><c:out value="${empty r.technicianName ? '—' : r.technicianName}"/></td>
                            <td><span class="badge ${r.status == 'SCHEDULED' ? 'bg-warning text-dark' : (r.status == 'CANCELLED' ? 'bg-danger' : 'bg-info text-dark')}"><c:out value="${r.status}"/></span></td>
                            <td>
                                <a class="btn btn-sm btn-outline-primary mb-1" href="${pageContext.request.contextPath}/admin/assembly?id=${r.assemblyId}">Chi tiết / Tiến độ</a>
                                <c:if test="${r.status == 'SCHEDULED'}">
                                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="d-flex gap-1 m-0 mb-1 align-items-center">
                                        <input type="hidden" name="id" value="${r.assemblyId}"><input type="hidden" name="action" value="confirm"><input type="hidden" name="status" value="${status}">
                                        <input type="datetime-local" name="when" class="form-control form-control-sm" style="width:190px; height:30px" title="Để trống = giữ giờ khách chọn">
                                        <button type="submit" class="btn btn-sm btn-primary text-nowrap" style="height:30px">Xác nhận lịch</button>
                                    </form>
                                </c:if>
                                <c:if test="${r.status == 'SCHEDULED' || r.status == 'CONFIRMED'}">
                                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="d-flex gap-1 m-0 align-items-center" onsubmit="return confirm('Từ chối/hủy lịch này?');">
                                        <input type="hidden" name="id" value="${r.assemblyId}"><input type="hidden" name="action" value="reject"><input type="hidden" name="status" value="${status}">
                                        <input type="text" name="reason" class="form-control form-control-sm" style="width:190px; height:30px" placeholder="Lý do từ chối" required>
                                        <button type="submit" class="btn btn-sm btn-outline-danger text-nowrap" style="height:30px">Từ chối</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty rows}"><tr><td colspan="9" class="text-center text-muted py-4">Chưa có đơn lắp ráp nào.</td></tr></c:if>
                </tbody>
            </table>
        </div>
        <jsp:include page="/views/components/pagination.jsp"><jsp:param name="url" value="/admin/assembly"/></jsp:include>
    </div></div>
</div>

<jsp:include page="/views/components/footer.jsp" />
