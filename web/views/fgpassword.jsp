<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quên Mật Khẩu - GearZone</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="container">
        <div class="form-box">
            <h2 style="color: #fff; margin-bottom: 20px; text-align: center;">Quên Mật Khẩu</h2>
            <p style="color: #cbd5e1; font-size: 14px; text-align: center; margin-bottom: 20px;">
                Vui lòng nhập địa chỉ email tài khoản của bạn để nhận mã xác thực (OTP).
            </p>

            <% String error = (String) request.getAttribute("error"); 
               if (error != null) { %>
                <p style="color: #ff4757; text-align: center; margin-bottom: 15px;"><%= error %></p>
            <% } %>

            <form action="${pageContext.request.contextPath}/fgpassword" method="post">
                <div class="form-group">
                    <label>Địa chỉ Email</label>
                    <input type="email" name="email" required placeholder="Nhập email của bạn...">
                </div>
                <button type="submit" class="btn-submit">Nhận Mã OTP</button>
            </form>
            
            <div style="text-align: center; margin-top: 20px;">
                <a href="${pageContext.request.contextPath}/login" style="color: #94a3b8; font-size: 14px; text-decoration: none;">
                    Quay lại đăng nhập
                </a>
            </div>
        </div>
    </div>
</body>
</html>