<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="fw-bold mb-0"><i class="fas fa-receipt text-primary me-2"></i>Đơn hàng #<c:out value="${order.orderCode}"/></h3>
        <div class="d-flex gap-2">
            <c:if test="${order.status != 'CANCELLED' && !(payMethod == 'PAYOS' && payStatus != 'PAID')}"><a href="${pageContext.request.contextPath}/order?id=${order.orderId}&action=invoice" target="_blank" class="btn btn-outline-primary"><i class="fas fa-print me-1"></i>Hóa đơn</a></c:if>
            <a href="${pageContext.request.contextPath}/user/order-history" class="btn btn-outline-secondary">Lịch sử mua hàng</a>
        </div>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
        <div class="d-flex justify-content-between mb-2">
            <span>Trạng thái: <jsp:include page="/views/components/order_status_badge.jsp"><jsp:param name="status" value="${order.status}"/></jsp:include></span>
            <span class="text-muted small"><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span>
        </div>
        <div><strong>Người nhận:</strong> <c:out value="${order.recipient}"/> - <c:out value="${order.phone}"/></div>
        <div><strong>Địa chỉ:</strong> <c:out value="${order.address}"/></div>
        <c:if test="${not empty order.trackingCode}"><div><strong>Mã vận đơn:</strong> <c:out value="${order.trackingCode}"/></div></c:if>
        <c:if test="${not empty order.note}"><div><strong>Ghi chú:</strong> <c:out value="${order.note}"/></div></c:if>
        <c:if test="${not empty order.cancelReason}"><div class="text-danger"><strong>Lý do hủy:</strong> <c:out value="${order.cancelReason}"/></div></c:if>
        <div><strong>Thanh toán:</strong>
            <c:choose>
                <c:when test="${payMethod == 'PAYOS' && payStatus == 'PAID'}"><span class="badge bg-success">PayOS - đã thanh toán</span></c:when>
                <c:when test="${payMethod == 'PAYOS'}"><span class="badge bg-warning text-dark">PayOS - chờ thanh toán</span></c:when>
                <c:otherwise>Khi nhận hàng (COD)</c:otherwise>
            </c:choose>
        </div>
        <c:if test="${payMethod == 'PAYOS' && payStatus != 'PAID' && order.status == 'PENDING'}">
            <form action="${pageContext.request.contextPath}/order" method="POST" class="mt-2">
                <input type="hidden" name="id" value="${order.orderId}"><input type="hidden" name="action" value="pay">
                <button type="submit" class="btn btn-success"><i class="fas fa-credit-card me-1"></i>Thanh toán lại</button>
            </form>
        </c:if>
    </div></div>

    <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
        <table class="table align-middle mb-2">
            <thead class="table-light"><tr><th>Sản phẩm</th><th class="text-end">Đơn giá</th><th class="text-center">SL</th><th class="text-end">Thành tiền</th></tr></thead>
            <tbody>
                <c:forEach var="l" items="${lines}">
                    <tr><td><c:out value="${l.partName}"/>
                        <c:choose><c:when test="${not empty l.serialNumber}"><div class="small text-muted">S/N: <strong><c:out value="${l.serialNumber}"/></strong></div></c:when>
                        <c:when test="${order.status == 'PENDING'}"><div class="small text-muted fst-italic">Chờ xuất kho</div></c:when></c:choose></td>
                        <td class="text-end"><fmt:formatNumber value="${l.unitPrice}" type="number" maxFractionDigits="0"/> ₫</td>
                        <td class="text-center">${l.quantity}</td>
                        <td class="text-end"><fmt:formatNumber value="${l.lineTotal}" type="number" maxFractionDigits="0"/> ₫</td></tr>
                </c:forEach>
            </tbody>
        </table>
        <div class="text-end">
            <div>Tạm tính: <fmt:formatNumber value="${order.total}" type="number" maxFractionDigits="0"/> ₫</div>
            <c:if test="${order.discount > 0}"><div class="text-success">Giảm giá<c:if test="${not empty order.couponCode}"> (<c:out value="${order.couponCode}"/>)</c:if>: -<fmt:formatNumber value="${order.discount}" type="number" maxFractionDigits="0"/> ₫</div></c:if>
            <div>Phí vận chuyển: <fmt:formatNumber value="${order.fee}" type="number" maxFractionDigits="0"/> ₫</div>
            <div class="fs-5 fw-bold text-danger">Tổng cộng: <fmt:formatNumber value="${order.finalAmount}" type="number" maxFractionDigits="0"/> ₫</div>
        </div>
    </div></div>

    <c:if test="${order.status == 'SHIPPING'}">
        <form action="${pageContext.request.contextPath}/order" method="POST" onsubmit="return confirm('Xác nhận bạn đã nhận được hàng?');" class="mb-3">
            <input type="hidden" name="action" value="received">
            <input type="hidden" name="id" value="${order.orderId}">
            <button type="submit" class="btn btn-success text-nowrap"><i class="fas fa-check me-1"></i> Đã nhận hàng</button>
        </form>
    </c:if>
    <c:if test="${order.status == 'PENDING' || order.status == 'CONFIRMED'}">
        <form action="${pageContext.request.contextPath}/order" method="POST" onsubmit="return confirm('Bạn chắc chắn muốn hủy đơn hàng này?');" class="d-flex gap-2">
            <input type="hidden" name="action" value="cancel">
            <input type="hidden" name="id" value="${order.orderId}">
            <input type="text" name="reason" class="form-control" maxlength="500" placeholder="Lý do hủy (không bắt buộc)">
            <button type="submit" class="btn btn-outline-danger text-nowrap"><i class="fas fa-times me-1"></i> Hủy đơn</button>
        </form>
    </c:if>
</div>

<jsp:include page="/views/components/footer.jsp" />
