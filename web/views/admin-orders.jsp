<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Đơn Hàng - Admin GearZone</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .admin-table { width: 100%; border-collapse: collapse; background: #1a1d29; border-radius: 8px; overflow: hidden;}
        .admin-table th, .admin-table td { padding: 12px; border-bottom: 1px solid #2d3748; text-align: left; font-size: 14px;}
        .admin-table th { background-color: #242938; color: #94a3b8; text-transform: uppercase; font-size: 13px;}
        .admin-table tr:hover { background-color: #1e2336; }
        
        .status-select { background: #0f1117; color: #fff; border: 1px solid #334155; padding: 6px; border-radius: 4px; outline: none; }
        .status-select:focus { border-color: #3b82f6; }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container main-layout">
        <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
        
        <div class="layout-right">
            <h2 class="section-title"><i class="fa-solid fa-boxes-packing" style="color: #ff4757;"></i> Quản Lý Toàn Bộ Đơn Hàng</h2>

            <div style="background: #1a1d29; border-radius: 12px; border: 1px solid #2d3748; padding: 20px; overflow-x: auto;">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Mã Đơn</th>
                            <th>Khách Hàng</th>
                            <th>SĐT Nhận</th>
                            <th>Tổng Tiền</th>
                            <th>Ngày Đặt</th>
                            <th>Thanh Toán</th>
                            <th>Trạng Thái</th>
                            <th>Cập Nhật</th>
                        </tr>
                    </thead>
                    <tbody>
                        <fmt:setLocale value="vi_VN"/>
                        <c:forEach items="${adminOrderList}" var="o">
                            <tr>
                                <td style="font-weight: bold; color: #3b82f6;">${o.orderCode}</td>
                                <td style="color: #e2e8f0;">${o.shippingName}</td>
                                <td style="color: #94a3b8;">${o.shippingPhone}</td>
                                <td style="color: #ff4757; font-weight: bold;"><fmt:formatNumber value="${o.totalAmount}" pattern="#,###"/> ₫</td>
                                <td style="color: #94a3b8;"><fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td style="color: #10b981;">${o.paymentMethod}</td>
                                <td>
                                    <!-- Form cập nhật trạng thái -->
                                    <form action="${pageContext.request.contextPath}/admin/update-order-status" method="POST" style="display: flex; gap: 8px;">
                                        <input type="hidden" name="orderId" value="${o.orderId}">
                                        <select name="status" class="status-select">
                                            <option value="Chờ xác nhận" ${o.orderStatus == 'Chờ xác nhận' ? 'selected' : ''}>Chờ xác nhận</option>
                                            <option value="Đang đóng gói" ${o.orderStatus == 'Đang đóng gói' ? 'selected' : ''}>Đang đóng gói</option>
                                            <option value="Đang giao hàng" ${o.orderStatus == 'Đang giao hàng' ? 'selected' : ''}>Đang giao hàng</option>
                                            <option value="Hoàn thành" ${o.orderStatus == 'Hoàn thành' ? 'selected' : ''}>Hoàn thành</option>
                                            <option value="Đã hủy" ${o.orderStatus == 'Đã hủy' ? 'selected' : ''}>Đã hủy</option>
                                        </select>
                                        <button type="submit" style="background: #3b82f6; color: #fff; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer;">Lưu</button>
                                    </form>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/order-detail?id=${o.orderId}" style="color: #a855f7; text-decoration: none; font-weight: 500;"><i class="fa-solid fa-eye"></i> Xem</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty adminOrderList}">
                            <tr><td colspan="8" style="text-align: center; color: #94a3b8; padding: 20px;">Chưa có đơn hàng nào trong hệ thống.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp" />
    <c:if test="${not empty sessionScope.successMsg}">
        <script>alert('${sessionScope.successMsg}');</script>
        <c:remove var="successMsg" scope="session" />
    </c:if>
</body>
</html>