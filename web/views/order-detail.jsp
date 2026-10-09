<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi Tiết Đơn Hàng - GearZone</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container main-layout">
        <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
        
        <div class="layout-right">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                <h2 class="section-title" style="margin: 0; border: none; padding: 0;"><i class="fa-solid fa-file-invoice" style="color: #ff4757;"></i> Chi Tiết Đơn Hàng #${orderInfo.orderCode}</h2>
                <a href="${pageContext.request.contextPath}/my-orders" style="color: #94a3b8; text-decoration: none;"><i class="fa-solid fa-arrow-left"></i> Quay lại lịch sử</a>
            </div>

            <div style="background: #1a1d29; border-radius: 12px; border: 1px solid #2d3748; padding: 25px; margin-bottom: 25px;">
                <h3 style="color: #fff; font-size: 16px; border-bottom: 1px solid #334155; padding-bottom: 10px; margin-bottom: 15px;">THÔNG TIN ĐƠN HÀNG</h3>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
                    <div>
                        <p style="color: #94a3b8; margin-bottom: 8px;">Trạng thái: 
                            <span style="color: ${orderInfo.orderStatus == 'Hoàn thành' ? '#10b981' : '#f59e0b'}; font-weight: bold; background: rgba(255,255,255,0.1); padding: 3px 8px; border-radius: 4px; margin-left: 5px;">${orderInfo.orderStatus}</span>
                        </p>
                        <p style="color: #94a3b8; margin-bottom: 8px;">Ngày đặt: <span style="color: #e2e8f0;"><fmt:formatDate value="${orderInfo.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span></p>
                        <p style="color: #94a3b8;">Phương thức: <span style="color: #e2e8f0;">${orderInfo.paymentMethod}</span></p>
                    </div>
                    <div style="border-left: 1px solid #334155; padding-left: 20px;">
                        <p style="color: #e2e8f0; font-weight: bold; margin-bottom: 5px;">${orderInfo.shippingName}</p>
                        <p style="color: #94a3b8; margin-bottom: 5px;"><i class="fa-solid fa-phone" style="width: 20px;"></i> ${orderInfo.shippingPhone}</p>
                        <p style="color: #94a3b8;"><i class="fa-solid fa-location-dot" style="width: 20px;"></i> ${orderInfo.shippingAddress}</p>
                    </div>
                </div>
            </div>

            <div style="background: #1a1d29; border-radius: 12px; border: 1px solid #2d3748; padding: 25px;">
                <h3 style="color: #fff; font-size: 16px; border-bottom: 1px solid #334155; padding-bottom: 10px; margin-bottom: 15px;">SẢN PHẨM ĐÃ MUA</h3>
                
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 1px solid #334155;">
                            <th style="text-align: left; padding: 10px; color: #94a3b8;">Sản phẩm</th>
                            <th style="text-align: center; padding: 10px; color: #94a3b8;">Đơn giá</th>
                            <th style="text-align: center; padding: 10px; color: #94a3b8;">SL</th>
                            <th style="text-align: right; padding: 10px; color: #94a3b8;">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <fmt:setLocale value="vi_VN"/>
                        <c:forEach items="${orderDetails}" var="d">
                            <tr style="border-bottom: 1px solid #2d3748;">
                                <td style="padding: 15px 10px; color: #e2e8f0; font-weight: 500;">${d.productName}</td>
                                <td style="text-align: center; padding: 15px 10px; color: #cbd5e1;"><fmt:formatNumber value="${d.unitPrice}" pattern="#,###"/> ₫</td>
                                <td style="text-align: center; padding: 15px 10px; color: #fff; font-weight: bold;">${d.quantity}</td>
                                <td style="text-align: right; padding: 15px 10px; color: #ff4757; font-weight: bold;"><fmt:formatNumber value="${d.unitPrice * d.quantity}" pattern="#,###"/> ₫</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <div style="text-align: right; margin-top: 20px; padding-top: 15px; border-top: 1px dashed #334155;">
                    <span style="color: #94a3b8; font-size: 16px; margin-right: 15px;">Tổng cộng:</span>
                    <span style="color: #ff4757; font-size: 24px; font-weight: bold;"><fmt:formatNumber value="${orderInfo.totalAmount}" pattern="#,###"/> ₫</span>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>