<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh Toán VNPAY - GearZone</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container" style="max-width: 600px; text-align: center; margin-top: 50px;">
        <div style="background: #1a1d29; border-radius: 12px; border: 1px solid #2d3748; padding: 40px;">
            <i class="fa-solid fa-qrcode" style="font-size: 50px; color: #3b82f6; margin-bottom: 20px;"></i>
            <h2 style="color: #fff; margin-bottom: 10px;">Quét Mã Thanh Toán</h2>
            <p style="color: #94a3b8; margin-bottom: 30px;">Vui lòng sử dụng App Ngân hàng hoặc VNPAY để quét mã dưới đây.</p>
            
            <div style="background: #fff; padding: 20px; border-radius: 12px; display: inline-block; margin-bottom: 30px;">
                <!-- Mã QR Tự động (Thay MB và 0987654321 thành ngân hàng và STK của bạn) -->
                <img src="https://img.vietqr.io/image/MB-0987654321-compact2.png?amount=${param.amount}&addInfo=${param.code}&accountName=GEARZONE" alt="QR Thanh Toán" style="width: 250px; border-radius: 8px;">
                
                <!-- NẾU BẠN MUỐN DÙNG ẢNH TĨNH CỦA RIÊNG BẠN, HÃY XÓA THẺ IMG Ở TRÊN VÀ BỎ COMMENT THẺ DƯỚI ĐÂY -->
                <!-- <img src="${pageContext.request.contextPath}/views/images/my-qr-code.png" style="width: 250px;"> -->
            </div>

            <div style="background: #0f1117; padding: 15px; border-radius: 8px; border: 1px dashed #334155; text-align: left; margin-bottom: 30px;">
                <p style="color: #cbd5e1; margin-bottom: 5px;">Mã Đơn Hàng: <strong style="color: #ff4757;">${param.code}</strong></p>
                <p style="color: #cbd5e1;">Số Tiền: <strong style="color: #10b981;"><fmt:formatNumber value="${param.amount}" pattern="#,###"/> ₫</strong></p>
            </div>

            <a href="${pageContext.request.contextPath}/my-orders" class="btn-submit" style="display: block; text-decoration: none; padding: 15px;">
                Tôi đã thanh toán xong
            </a>
        </div>
    </div>
    
    <jsp:include page="includes/footer.jsp" />
</body>
</html>