<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Hóa đơn <c:out value="${order.orderCode}"/> | GearZone</title>
    <style>
        body { font-family: Arial, Helvetica, sans-serif; color: #222; max-width: 800px; margin: 24px auto; padding: 0 16px; }
        h1 { margin: 0; font-size: 26px; font-weight: 900; letter-spacing: 1px; color: #0f1117; } h1 span { color: #ff4757; }
        .brand { display: flex; align-items: center; gap: 12px; } .logo { width: 56px; height: 56px; object-fit: contain; } .muted { color: #666; font-size: 13px; }
        table { width: 100%; border-collapse: collapse; margin-top: 16px; }
        th, td { border-bottom: 1px solid #ddd; padding: 8px; text-align: left; font-size: 14px; }
        th.r, td.r { text-align: right; } .total td { font-weight: bold; font-size: 16px; border-bottom: none; }
        .head { display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 2px solid #222; padding-bottom: 12px; }
        .actions { margin-top: 20px; } button { padding: 8px 16px; cursor: pointer; }
        @media print { * { -webkit-print-color-adjust: exact; print-color-adjust: exact; } .actions { display: none; } body { margin: 0; } }
    </style>
</head>
<body>
    <div class="head">
        <div class="brand">
            <img src="${pageContext.request.contextPath}/views/components/image/GearZone.png" alt="GearZone" class="logo">
            <div><h1>GEAR<span>ZONE</span></h1><div class="muted">Hóa đơn bán hàng</div></div>
        </div>
        <div style="text-align:right">
            <div><strong>Số: <c:out value="${order.orderCode}"/></strong></div>
            <div class="muted">Ngày đặt: <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></div>
        </div>
    </div>

    <p><strong>Người nhận:</strong> <c:out value="${order.recipient}"/> - <c:out value="${order.phone}"/><br>
       <strong>Địa chỉ:</strong> <c:out value="${order.address}"/><br>
       <strong>Thanh toán:</strong> <c:choose><c:when test="${payMethod == 'PAYOS'}">PayOS - đã thanh toán</c:when><c:otherwise>Khi nhận hàng (COD)</c:otherwise></c:choose></p>

    <table>
        <thead><tr><th>#</th><th>Sản phẩm</th><th class="r">Đơn giá</th><th class="r">SL</th><th class="r">Thành tiền</th></tr></thead>
        <tbody>
            <c:forEach var="l" items="${lines}" varStatus="st">
                <tr><td>${st.count}</td><td><c:out value="${l.partName}"/>
                        <c:choose><c:when test="${not empty l.serialNumber}"><br><small><strong>S/N: <c:out value="${l.serialNumber}"/></strong></small></c:when>
                        <c:when test="${order.status == 'PENDING'}"><br><small><em>Chờ xuất kho</em></small></c:when></c:choose></td>
                    <td class="r"><fmt:formatNumber value="${l.unitPrice}" maxFractionDigits="0"/> ₫</td><td class="r">${l.quantity}</td>
                    <td class="r"><fmt:formatNumber value="${l.lineTotal}" maxFractionDigits="0"/> ₫</td></tr>
            </c:forEach>
            <tr><td colspan="4" class="r">Tạm tính</td><td class="r"><fmt:formatNumber value="${order.total}" maxFractionDigits="0"/> ₫</td></tr>
            <c:if test="${order.discount > 0}"><tr><td colspan="4" class="r">Giảm giá<c:if test="${not empty order.couponCode}"> (<c:out value="${order.couponCode}"/>)</c:if></td><td class="r">-<fmt:formatNumber value="${order.discount}" maxFractionDigits="0"/> ₫</td></tr></c:if>
            <tr><td colspan="4" class="r">Phí vận chuyển</td><td class="r"><fmt:formatNumber value="${order.fee}" maxFractionDigits="0"/> ₫</td></tr>
            <tr class="total"><td colspan="4" class="r">Tổng thanh toán</td><td class="r"><fmt:formatNumber value="${order.finalAmount}" maxFractionDigits="0"/> ₫</td></tr>
        </tbody>
    </table>

    <p class="muted">Cảm ơn bạn đã mua hàng tại GearZone.</p>
    <div class="actions"><button onclick="window.print()">In hóa đơn</button></div>
</body>
</html>
