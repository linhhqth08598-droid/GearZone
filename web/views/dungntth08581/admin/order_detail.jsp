<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="fw-bold mb-0"><i class="fas fa-receipt text-primary me-2"></i>Đơn hàng <c:out value="${order.orderCode}"/></h3>
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-secondary"><i class="fas fa-arrow-left me-1"></i>Quay lại</a>
    </div>

    <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
        <div class="d-flex justify-content-between mb-2">
            <span>Trạng thái: <jsp:include page="/views/components/order_status_badge.jsp"><jsp:param name="status" value="${order.status}"/></jsp:include></span>
            <span class="text-muted small"><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span>
        </div>
        <div><strong>Khách hàng:</strong> <c:out value="${order.customerName}"/></div>
        <div><strong>Người nhận:</strong> <c:out value="${order.recipient}"/> - <c:out value="${order.phone}"/></div>
        <div><strong>Địa chỉ:</strong> <c:out value="${order.address}"/></div>
        <c:if test="${not empty order.trackingCode}"><div><strong>Mã vận đơn:</strong> <c:out value="${order.trackingCode}"/></div></c:if>
        <c:if test="${not empty order.note}"><div><strong>Ghi chú:</strong> <c:out value="${order.note}"/></div></c:if>
        <c:if test="${not empty order.cancelReason}"><div class="text-danger"><strong>Lý do hủy:</strong> <c:out value="${order.cancelReason}"/></div></c:if>
        <div><strong>Thanh toán:</strong>
            <c:choose>
                <c:when test="${payMethod == 'PAYOS' && payStatus == 'PAID'}"><span class="badge bg-success">PayOS - đã thanh toán</span></c:when>
                <c:when test="${payMethod == 'PAYOS'}"><span class="badge bg-warning text-dark">PayOS - chưa thanh toán</span></c:when>
                <c:otherwise>Khi nhận hàng (COD)</c:otherwise>
            </c:choose>
        </div>
    </div></div>

    <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
        <h5 class="fw-bold mb-3">Sản phẩm khách đặt</h5>
        <div class="table-responsive">
            <table class="table align-middle mb-2">
                <thead class="table-light"><tr><th>Sản phẩm</th><th>Số Serial (S/N)</th><th class="text-end">Đơn giá</th><th class="text-center">SL</th><th class="text-end">Thành tiền</th></tr></thead>
                <tbody>
                    <c:forEach var="l" items="${lines}">
                        <tr>
                            <td><c:out value="${l.partName}"/></td>
                            <td><c:choose>
                                <c:when test="${not empty l.serialNumber}"><code><c:out value="${l.serialNumber}"/></code></c:when>
                                <c:otherwise><span class="text-muted fst-italic">Chưa xuất kho</span></c:otherwise>
                            </c:choose></td>
                            <td class="text-end"><fmt:formatNumber value="${l.unitPrice}" type="number" maxFractionDigits="0"/> ₫</td>
                            <td class="text-center">${l.quantity}</td>
                            <td class="text-end"><fmt:formatNumber value="${l.lineTotal}" type="number" maxFractionDigits="0"/> ₫</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="text-end">
            <div>Tạm tính: <fmt:formatNumber value="${order.total}" type="number" maxFractionDigits="0"/> ₫</div>
            <c:if test="${order.discount > 0}"><div>Giảm giá: -<fmt:formatNumber value="${order.discount}" type="number" maxFractionDigits="0"/> ₫</div></c:if>
            <div>Phí vận chuyển: <fmt:formatNumber value="${order.fee}" type="number" maxFractionDigits="0"/> ₫</div>
            <div class="fw-bold text-danger fs-5">Tổng cộng: <fmt:formatNumber value="${order.finalAmount}" type="number" maxFractionDigits="0"/> ₫</div>
        </div>
    </div></div>
</div>

<jsp:include page="/views/components/footer.jsp" />
