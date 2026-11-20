<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h3 class="fw-bold mb-0"><i class="fas fa-shopping-cart text-primary me-2"></i>Quản lý đơn hàng</h3>
        <form method="GET" action="${pageContext.request.contextPath}/admin/orders" class="d-flex gap-2">
            <select name="status" class="form-select" onchange="this.form.submit()">
                <option value="">Tất cả trạng thái</option>
                <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Chờ xử lý</option>
                <option value="CONFIRMED" ${status == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
                <option value="SHIPPING" ${status == 'SHIPPING' ? 'selected' : ''}>Đang giao</option>
                <option value="DELIVERED" ${status == 'DELIVERED' ? 'selected' : ''}>Hoàn thành</option>
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
                <thead class="table-light"><tr><th>Mã đơn</th><th>Khách hàng</th><th>Người nhận</th><th class="text-end">Tổng tiền</th><th>Trạng thái</th><th>Ngày đặt</th><th style="min-width:230px">Thao tác</th></tr></thead>
                <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td class="fw-bold"><c:out value="${o.orderCode}"/></td>
                            <td><c:out value="${o.customerName}"/></td>
                            <td><c:out value="${o.recipient}"/><div class="small text-muted"><c:out value="${o.phone}"/></div></td>
                            <td class="text-end"><fmt:formatNumber value="${o.finalAmount}" type="number" maxFractionDigits="0"/> ₫</td>
                            <td><jsp:include page="/views/components/order_status_badge.jsp"><jsp:param name="status" value="${o.status}"/></jsp:include></td>
                            <td><fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td>
                                <div class="d-flex gap-1 flex-wrap align-items-center">
                                    <a href="${pageContext.request.contextPath}/admin/order?id=${o.orderId}" class="btn btn-sm btn-outline-secondary text-nowrap" style="height:30px">Chi tiết</a>
                                    <c:set var="nextStatus" value="${o.status == 'PENDING' ? 'CONFIRMED' : (o.status == 'CONFIRMED' ? 'SHIPPING' : (o.status == 'SHIPPING' ? 'DELIVERED' : ''))}"/>
                                    <c:if test="${not empty nextStatus}">
                                        <form action="${pageContext.request.contextPath}/admin/orders" method="POST" class="d-flex gap-1 m-0 align-items-center">
                                            <input type="hidden" name="id" value="${o.orderId}">
                                            <input type="hidden" name="next" value="${nextStatus}">
                                            <input type="hidden" name="status" value="${status}">
                                            <c:if test="${nextStatus == 'SHIPPING'}"><input type="text" name="extra" class="form-control form-control-sm" style="width:130px; height:30px" placeholder="Mã vận đơn"></c:if>
                                            <button type="submit" class="btn btn-sm btn-primary text-nowrap" style="height:30px">
                                                <c:choose>
                                                    <c:when test="${nextStatus == 'CONFIRMED'}">Xác nhận</c:when>
                                                    <c:when test="${nextStatus == 'SHIPPING'}">Giao hàng</c:when>
                                                    <c:otherwise>Hoàn tất</c:otherwise>
                                                </c:choose>
                                            </button>
                                        </form>
                                    </c:if>
                                    <c:if test="${o.status == 'PENDING' || o.status == 'CONFIRMED'}">
                                        <form action="${pageContext.request.contextPath}/admin/orders" method="POST" class="m-0" onsubmit="return confirm('Hủy đơn này?');">
                                            <input type="hidden" name="id" value="${o.orderId}">
                                            <input type="hidden" name="next" value="CANCELLED">
                                            <input type="hidden" name="status" value="${status}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger text-nowrap" style="height:30px">Hủy</button>
                                        </form>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty orders}"><tr><td colspan="7" class="text-center text-muted py-4">Chưa có đơn hàng nào.</td></tr></c:if>
                </tbody>
            </table>
        </div>
        <jsp:include page="/views/components/pagination.jsp"><jsp:param name="url" value="/admin/orders"/></jsp:include>
    </div></div>
</div>

<jsp:include page="/views/components/footer.jsp" />
