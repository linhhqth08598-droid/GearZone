<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!-- Dùng JSTL để dễ dàng lấy biến session (nếu bạn có dùng JSTL) -->
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header>
    <div class="logo">
        <a href="${pageContext.request.contextPath}/home">GEAR<span>ZONE</span></a>
    </div>
    <nav>
        <a href="${pageContext.request.contextPath}/home" class="active">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/products">Sản phẩm</a>
        <a href="${pageContext.request.contextPath}/promotions">Khuyến mãi</a>
        <a href="${pageContext.request.contextPath}/contact">Liên hệ</a>
    </nav>
    <div class="user-nav" id="user-nav">
        <!-- Kiểm tra xem người dùng đã đăng nhập chưa dựa vào session "userAcc" -->
        <c:choose>
            <c:when test="${not empty sessionScope.userAcc}">
                <span class="user-greeting">Chào, ${sessionScope.userAcc.fullName}</span>
                <a href="${pageContext.request.contextPath}/cart" style="margin-right:15px; color:#94a3b8; text-decoration:none;">
                    <i class="fa-solid fa-cart-shopping"></i> <span class="cart-badge">0</span>
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn-logout"><i class="fa-solid fa-right-from-bracket"></i> Đăng xuất</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="btn-login">Đăng nhập</a>
                <a href="${pageContext.request.contextPath}/register" class="btn-register">Đăng ký</a>
            </c:otherwise>
        </c:choose>
    </div>
</header>