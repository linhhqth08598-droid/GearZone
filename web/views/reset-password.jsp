<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Đặt Lại Mật Khẩu - GearZone</title>
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
  <div class="container">
    <div class="form-box">
      <h2 style="color: #fff; margin-bottom: 20px; text-align: center;">Tạo Mật Khẩu Mới</h2>
      
      <% String error = (String) request.getAttribute("error"); 
         if (error != null) { %>
          <p style="color: #ff4757; text-align: center; margin-bottom: 15px;"><%= error %></p>
      <% } %>

      <form action="${pageContext.request.contextPath}/reset-password" method="post">
        
        <div class="form-group">
          <label>Mật khẩu mới</label>
          <div class="password-wrapper">
            <input type="password" name="newPassword" id="newPassword" required placeholder="Nhập mật khẩu mới...">
            <i class="fa-regular fa-eye toggle-password" onclick="toggleVisibility('newPassword', this)"></i>
          </div>
        </div>

        <div class="form-group">
          <label>Xác nhận mật khẩu mới</label>
          <div class="password-wrapper">
            <input type="password" name="confirmNewPassword" id="confirmNewPassword" required placeholder="Nhập lại mật khẩu mới...">
            <i class="fa-regular fa-eye toggle-password" onclick="toggleVisibility('confirmNewPassword', this)"></i>
          </div>
        </div>

        <button type="submit" class="btn-submit">Lưu Mật Khẩu Mới</button>
      </form>
    </div>
  </div>

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