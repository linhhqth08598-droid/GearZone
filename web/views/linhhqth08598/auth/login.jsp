<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container auth-container my-5" style="max-width: 500px;">
    <div class="auth-card">
        <div class="brand-logo">GEAR<span>ZONE</span></div>
        <h4 class="text-white text-center mb-4">ĐĂNG NHẬP</h4>

        <c:if test="${not empty error}">
            <div class="alert alert-danger"><i class="fas fa-exclamation-circle"></i> ${error}</div>
        </c:if>
        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-success d-flex align-items-center justify-content-center text-center">
                <i class="fas fa-check-circle me-2"></i> 
                <span>${sessionScope.message}</span>
            </div>
            <c:remove var="message" scope="session" />
        </c:if>

        <form action="${pageContext.request.contextPath}/auth" method="POST">
            <input type="hidden" name="action" value="login">

            <div class="mb-3">
                <label class="form-label">Email</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                    <input type="email" name="email" class="form-control" value="${email}" required placeholder="Nhập email của bạn">
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label">Mật khẩu</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-lock"></i></span>
                    <input type="password" id="loginPassword" name="password" class="form-control" required placeholder="Nhập mật khẩu" autocomplete="new-password">
                    <button type="button" class="toggle-password" onclick="togglePassword('loginPassword', this)">
                        <i class="fas fa-eye"></i>
                    </button>
                </div>
            </div>

            <!-- Sửa lại nút Đăng nhập trong login.jsp như sau: -->
            <button type="submit" class="btn btn-primary w-100 mb-3" style="background: linear-gradient(135deg, #ff4757 0%, #ff2a3d 100%) !important; border: none;">Đăng nhập</button>

            <!-- ĐÃ CHUYỂN QUÊN MẬT KHẨU XUỐNG ĐÂY -->
            <div class="d-flex justify-content-between align-items-center mt-3">
                <a href="${pageContext.request.contextPath}/auth?action=forget" class="text-muted" style="text-decoration: none; font-size: 14px;">Quên mật khẩu?</a>
                <a href="${pageContext.request.contextPath}/auth?action=register" style="color: #ff4757; text-decoration: none; font-size: 14px; font-weight: 600;">Đăng ký ngay</a>
            </div>
        </form>
    </div>
</div>

<script>
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