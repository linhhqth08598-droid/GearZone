<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác Thực OTP - GearZone</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css">
</head>
<body>
    <div class="container">
        <div class="form-box">
            <h2 style="color: #fff; margin-bottom: 20px; text-align: center;">Xác Thực Email</h2>
            <p style="color: #cbd5e1; font-size: 14px; text-align: center; margin-bottom: 20px;">
                Mã OTP gồm 6 chữ số đã được gửi tới email <br>
                <b style="color: #ff4757;">${sessionScope.resetEmail}</b>
            </p>

            <% String error = (String) request.getAttribute("error"); 
               if (error != null) { %>
                <p style="color: #ff4757; text-align: center; margin-bottom: 15px;"><%= error %></p>
            <% } %>

            <form action="${pageContext.request.contextPath}/verify-otp" method="post">
                <div class="form-group">
                    <label>Nhập mã OTP</label>
                    <!-- CSS inline để ô nhập OTP to, căn giữa và có khoảng cách chữ dễ nhìn -->
                    <input type="text" name="otpCode" required placeholder="------" maxlength="6" 
                           style="text-align: center; font-size: 24px; letter-spacing: 8px; font-weight: bold;">
                </div>
                <button type="submit" class="btn-submit">Xác Nhận OTP</button>
            </form>
        </div>
    </div>
</body>
</html>