<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Đăng Nhập - GearZone</title>
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css">
  <!-- Thêm thư viện FontAwesome -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
  <div class="container">
    <div class="form-box">
      <h2 style="color: #fff; margin-bottom: 20px; text-align: center;">Đăng Nhập GearZone</h2>
      <% String error = (String) request.getAttribute("error"); 
         if (error != null) { %>
          <p style="color: #ff4757; text-align: center;"><%= error %></p>
      <% } %>
      <form action="${pageContext.request.contextPath}/login" method="post">
        
        <div class="form-group">
          <label>Tên đăng nhập hoặc Email</label>
          <input type="text" name="username" required placeholder="Nhập tên đăng nhập hoặc email...">
        </div>
        
        <div class="form-group">
          <label>Mật khẩu</label>
          <!-- Bọc input mật khẩu để thêm icon -->
          <div class="password-wrapper">
            <input type="password" name="password" id="password" required placeholder="Nhập mật khẩu...">
            <i class="fa-regular fa-eye toggle-password" onclick="toggleVisibility('password', this)"></i>
          </div>
        </div>
        
        <div style="text-align: right; margin-bottom: 15px;">
            <a href="${pageContext.request.contextPath}/fgpassword" style="color: #94a3b8; text-decoration: none; font-size: 14px;">Quên mật khẩu?</a>
        </div>
        
        <button type="submit" class="btn-submit">Đăng Nhập</button>
        <p style="margin-top: 18px; font-size: 14px; text-align: center; color: #94a3b8;">
          Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register" style="color: #ff4757; text-decoration: none; font-weight: bold;">Đăng ký ngay</a>
        </p>
      </form>
    </div>
  </div>

  <!-- SCRIPT ẨN / HIỆN MẬT KHẨU -->
  <script>
    function toggleVisibility(inputId, iconElement) {
        const input = document.getElementById(inputId);
        if (input.type === "password") {
            input.type = "text";
            iconElement.classList.remove("fa-eye");
            iconElement.classList.add("fa-eye-slash");
        } else {
            input.type = "password";
            iconElement.classList.remove("fa-eye-slash");
            iconElement.classList.add("fa-eye");
        }
    }
  </script>
</body>
</html>