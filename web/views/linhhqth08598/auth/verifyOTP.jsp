<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container auth-container my-5" style="max-width: 500px;">
    <div class="auth-card">
        <h4 class="text-white text-center mb-2">XÁC THỰC TÀI KHOẢN</h4>
        <p class="text-center mb-4 text-muted" style="font-size: 14px;">Vui lòng nhập mã OTP đã được gửi đến thiết bị của bạn để hoàn tất.</p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth" method="POST">
            <input type="hidden" name="action" value="verify">
            <input type="hidden" name="email" value="${email}">

            <div class="mb-4 text-center">
                <label class="form-label text-center w-100 mb-3">Mã Xác Thực (OTP)</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-shield-alt"></i></span>
                    <!-- Dùng class otp-input-new và giới hạn 6 ký tự số -->
                    <input type="text" name="otp" class="form-control text-center" required maxlength="6" pattern="\d{6}" autocomplete="off" placeholder="Nhập 6 số OTP" style="letter-spacing: 5px; font-weight: bold; font-size: 18px;">
                </div>
            </div>

            <button type="submit" class="btn btn-primary w-100 mb-3">Xác Thực</button>

            <div class="text-center text-muted">
                Chưa nhận được mã? <a href="#" style="color: #ff4757; text-decoration: none; font-weight: 600;">Gửi lại</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />