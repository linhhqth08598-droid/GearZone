<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container auth-container my-5" style="max-width: 500px;">
    <div class="auth-card">
        <h4 class="text-center mb-2">QUÊN MẬT KHẨU</h4>
        <p class="text-center mb-4 text-muted" style="font-size: 14px;">Nhập email của bạn để nhận mã OTP khôi phục.</p>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth" method="POST" autocomplete="off">
            <input type="hidden" name="action" value="forget">

            <div class="mb-4">
                <label class="form-label">Email đăng nhập</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                    <input type="email" name="email" class="form-control" required placeholder="Nhập địa chỉ Email đăng nhập" autocomplete="off">
                </div>
            </div>

            <button type="submit" class="btn btn-primary w-100 mb-3">Gửi mã xác thực</button>

            <div class="text-center mt-3">
                <a href="${pageContext.request.contextPath}/auth?action=login" class="btn btn-outline-secondary w-100" style="border-radius: 10px; padding: 12px; font-weight: 500;">
                    <i class="fas fa-arrow-left me-2"></i> Quay lại đăng nhập
                </a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />