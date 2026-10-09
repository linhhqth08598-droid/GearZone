<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch Sử Đơn Hàng - GearZone</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .order-table { width: 100%; border-collapse: collapse; background: #1a1d29; border-radius: 8px; overflow: hidden; border: 1px solid #2d3748;}
        .order-table th, .order-table td { padding: 15px; border-bottom: 1px solid #2d3748; text-align: left; }
        .order-table th { background-color: #242938; color: #94a3b8; text-transform: uppercase; font-size: 13px; }
        .order-table tr:hover { background-color: #1e2336; }
        .status-badge { padding: 5px 12px; border-radius: 20px; font-size: 12px; font-weight: bold; }
        .status-pending { background: rgba(245, 158, 11, 0.2); color: #f59e0b; }
        .status-success { background: rgba(16, 185, 129, 0.2); color: #10b981; }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container main-layout">
        <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
        
        <div class="layout-right">
            <h2 class="section-title"><i class="fa-solid fa-box-open" style="color: #ff4757;"></i> Lịch Sử Đơn Hàng Của Bạn</h2>

            <c:choose>
                <c:when test="${empty orderList}">
                    <div style="text-align: center; padding: 50px 0; background: #1a1d29; border-radius: 12px; border: 1px solid #2d3748;">
                        <i class="fa-solid fa-receipt" style="font-size: 80px; color: #334155; margin-bottom: 20px;"></i>
                        <h3 style="color: #cbd5e1; margin-bottom: 15px;">Bạn chưa có đơn hàng nào!</h3>
                        <a href="${pageContext.request.contextPath}/products" class="btn-submit" style="width: auto; display: inline-block; padding: 10px 25px; text-decoration: none;">Bắt Đầu Mua Sắm</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="order-table">
                        <thead>
                            <tr>
                                <th>Mã Đơn</th>
                                <th>Ngày Đặt</th>
                                <th>Tổng Tiền</th>
                                <th>Thanh Toán</th>
                                <th>Trạng Thái</th>
                                <th>Hành Động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <fmt:setLocale value="vi_VN"/>
                            <c:forEach items="${orderList}" var="o">
                                <tr>
                                    <td style="color: #fff; font-weight: bold;">${o.orderCode}</td>
                                    <td style="color: #94a3b8;"><fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td style="color: #ff4757; font-weight: bold;"><fmt:formatNumber value="${o.totalAmount}" pattern="#,###"/> ₫</td>
                                    <td style="color: #e2e8f0;">${o.paymentMethod}</td>
                                    <td>
                                        <span class="status-badge ${o.orderStatus == 'Hoàn thành' ? 'status-success' : 'status-pending'}">
                                            ${o.orderStatus}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/order-detail?id=${o.orderId}" style="color: #3b82f6; text-decoration: none; font-weight: 500;">Chi tiết <i class="fa-solid fa-arrow-right" style="font-size: 11px;"></i></a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp" />

    <c:if test="${not empty sessionScope.successMsg}">
        <script>alert('${sessionScope.successMsg}');</script>
        <c:remove var="successMsg" scope="session" />
    </c:if>
</body>
</html>