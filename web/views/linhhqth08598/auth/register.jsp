<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container auth-container my-4" style="max-width: 550px;">
    <div class="auth-card">
        <div class="brand-logo">GEAR<span>ZONE</span></div>
        <h4 class="text-white text-center mb-3">TẠO TÀI KHOẢN</h4>

        <c:if test="${not empty error}">
            <div class="alert alert-danger py-2" style="font-size: 14px;">${error}</div> <!-- Thu nhỏ alert để đỡ chiếm chỗ -->
        </c:if>

        <form action="${pageContext.request.contextPath}/auth" method="POST" autocomplete="off" onsubmit="return validatePassword()">
            <input type="hidden" name="action" value="register">

            <!-- Họ và tên -->
            <div class="mb-2">
                <label class="form-label">Họ và tên</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-user"></i></span>
                    <input type="text" name="fullName" class="form-control" value="${fullName}" required placeholder="VD: Hà Quang Linh">
                </div>
            </div>

            <!-- Số điện thoại (Chỉ nhận 10 số, bắt đầu bằng 0) -->
            <div class="mb-2">
                <label class="form-label">Số điện thoại</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-phone"></i></span>
                    <input type="text" name="phone" class="form-control" value="${phone}" required 
                           placeholder="VD: 0987654321"
                           pattern="0[0-9]{9}" 
                           maxlength="10"
                           title="Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số"
                           oninput="this.value = this.value.replace(/[^0-9]/g, '')">
                </div>
            </div>

            <!-- Email -->
            <div class="mb-2">
                <label class="form-label">Email</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                    <input type="email" name="email" class="form-control" value="${email}" required placeholder="Nhập địa chỉ email hợp lệ">
                </div>
            </div>

            <!-- Mật khẩu -->
            <div class="mb-2">
                <label class="form-label">Mật khẩu</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-lock"></i></span>
                    <input type="password" id="regPassword" name="password" class="form-control" required minlength="8" placeholder="Tối thiểu 8 ký tự" autocomplete="new-password">
                    <button type="button" class="toggle-password" onclick="togglePassword('regPassword', this)">
                        <i class="fas fa-eye"></i>
                    </button>
                </div>
            </div>

            <!-- Xác nhận mật khẩu -->
            <div class="mb-3">
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

            <button type="submit" class="btn btn-primary w-100 mb-3" style="background: linear-gradient(135deg, #ff4757 0%, #ff2a3d 100%) !important; border: none;">Đăng ký</button>

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