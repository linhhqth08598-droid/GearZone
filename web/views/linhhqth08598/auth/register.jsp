<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container auth-container my-4" style="max-width: 650px;">
    <div class="auth-card">
        <div class="brand-logo">GEAR<span>ZONE</span></div>
        <h4 class="text-white text-center mb-4">TẠO TÀI KHOẢN</h4>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth" method="POST" autocomplete="off" onsubmit="return validatePassword()">
            <input type="hidden" name="action" value="register">

            <div class="row">
                <!-- Hàng 1: Họ tên - SĐT -->
                <div class="col-md-6 mb-3">
                    <label class="form-label">Họ và tên</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-user"></i></span>
                        <input type="text" name="fullName" class="form-control" value="${fullName}" required placeholder="VD: Hà Quang Linh">
                    </div>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Số điện thoại</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-phone"></i></span>
                        <input type="text" name="phone" class="form-control" value="${phone}" required placeholder="VD: 0987654321">
                    </div>
                </div>
            </div>

            <!-- Hàng 2: Email -->
            <div class="mb-3">
                <label class="form-label">Email</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                    <input type="email" name="email" class="form-control" value="${email}" required placeholder="Nhập địa chỉ email hợp lệ">
                </div>
            </div>

            <div class="row">
                <!-- Hàng 3: Mật khẩu -->
                <div class="col-md-6 mb-3">
                    <label class="form-label">Mật khẩu</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-lock"></i></span>
                        <input type="password" id="regPassword" name="password" class="form-control" required minlength="8" placeholder="Tối thiểu 8 ký tự" autocomplete="new-password">
                        <button type="button" class="toggle-password" onclick="togglePassword('regPassword', this)">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                </div>
                
                <!-- Hàng 4: Xác nhận mật khẩu -->
                <div class="col-md-6 mb-3">
                    <label class="form-label">Xác nhận mật khẩu</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-lock"></i></span>
                        <input type="password" id="confirmPassword" class="form-control" required placeholder="Nhập lại mật khẩu">
                        <button type="button" class="toggle-password" onclick="togglePassword('confirmPassword', this)">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                    <small id="pwdError" class="text-danger mt-1" style="display: none; font-size: 13px;"><i class="fas fa-exclamation-circle"></i> Mật khẩu không khớp!</small>
                </div>
            </div>

            <button type="submit" class="btn btn-primary w-100 mt-3 mb-4">Đăng ký</button>

            <div class="text-center text-muted" style="font-size: 14px;">
                Đã có tài khoản? <a href="${pageContext.request.contextPath}/auth?action=login" style="color: #ff4757; text-decoration: none; font-weight: 600;">Đăng nhập</a>
            </div>
        </form>
    </div>
</div>

<script>
    function validatePassword() {
        var pass = document.getElementById("regPassword").value;
        var confirmPass = document.getElementById("confirmPassword").value;
        var errorMsg = document.getElementById("pwdError");
        
        if (pass !== confirmPass) {
            errorMsg.style.display = "block";
            document.getElementById("confirmPassword").classList.add("is-invalid"); 
            return false;
        }
        
        errorMsg.style.display = "none";
        document.getElementById("confirmPassword").classList.remove("is-invalid");
        return true;
    }

    // Hàm dùng chung để ẩn/hiện mật khẩu
    function togglePassword(inputId, iconElement) {
        const input = document.getElementById(inputId);
        const icon = iconElement.querySelector('i');
        
        if (input.type === "password") {
            input.type = "text";
            icon.classList.remove("fa-eye");
            icon.classList.add("fa-eye-slash");
        } else {
            input.type = "password";
            icon.classList.remove("fa-eye-slash");
            icon.classList.add("fa-eye");
        }
    }
</script>

<jsp:include page="/views/components/footer.jsp" />