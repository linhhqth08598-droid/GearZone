<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<%-- Báo cho footer.jsp biết đây là layout user có sidebar (để đóng đúng các thẻ) --%>
<c:set var="userSidebarLayout" value="true" scope="request" />

<%-- URL hiện tại (gộp URL servlet gốc + URL file JSP, "_" -> "-") để tô sáng mục đang xem --%>
<c:set var="currentURL" value="${fn:replace(requestScope['jakarta.servlet.forward.request_uri'], '_', '-')} ${fn:replace(pageContext.request.requestURL, '_', '-')}" />

<style>
    /* Layout: sidebar sát mép trái + nội dung bên phải (giống trang Admin) */
    main:has(> .user-wrapper) { padding: 0 !important; }
    .user-wrapper {
        display: flex;
        flex: 1;
        width: 100%;
    }
    .sidebar-user {
        flex: 0 0 250px;
        width: 250px;
        position: sticky;
        top: var(--user-nav-h, 78px);
        align-self: flex-start;
        height: calc(100vh - var(--user-nav-h, 78px));
        overflow-y: auto;
        background-color: #ffffff;
        border-right: 1px solid #e2e8f0;
        z-index: 100;
    }
    .user-main {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
    }
    .user-content {
        flex: 1;
        padding: 24px;
    }
    .user-footer {
        background: #fff;
        border-top: 1px solid #e2e8f0;
        padding: 15px 0;
        text-align: center;
    }
    .sidebar-user .nav-link {
        color: #475569;
        font-weight: 500;
        padding: 12px 20px;
        border-left: 3px solid transparent;
        transition: all 0.2s ease;
    }
    .sidebar-user .nav-link:hover {
        background-color: #f8fafc;
        color: #ff4757;
        border-left-color: #ff4757;
    }
    .sidebar-user .nav-link.active {
        background-color: #fff1f2;
        color: #ff4757;
        border-left-color: #ff4757;
        font-weight: 600;
    }
    .sidebar-user .nav-link i {
        width: 25px;
        text-align: center;
        margin-right: 8px;
    }
    @media (max-width: 767.98px) {
        .user-wrapper { flex-direction: column; }
        .sidebar-user { position: static; width: 100%; flex-basis: auto; height: auto; border-right: 0; border-bottom: 1px solid #e2e8f0; }
    }
</style>


<script>
    /* Đo chiều cao thật của navbar để sidebar dính đúng dưới navbar, không gây tràn trang */
    (function () {
        var nav = document.querySelector('.navbar-custom');
        function setH() { if (nav) document.documentElement.style.setProperty('--user-nav-h', nav.offsetHeight + 'px'); }
        setH();
        window.addEventListener('resize', setH);
        window.addEventListener('load', setH);
    })();
</script>

<div class="user-wrapper">
    <nav class="sidebar-user d-flex flex-column">
        <ul class="nav flex-column">
            <li class="nav-item border-bottom mb-1 pb-1">
                <a class="nav-link" href="${pageContext.request.contextPath}/home">
                    <i class="fas fa-store"></i> Trang chủ - Mua sắm
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/order-history') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/order-history">
                    <i class="fas fa-shopping-bag"></i> Lịch sử mua hàng
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/wishlist') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/wishlist">
                    <i class="fas fa-heart"></i> Sản phẩm yêu thích
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/returns') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/returns">
                    <i class="fas fa-undo-alt"></i> Yêu cầu đổi trả
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/price-alert') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/price-alert">
                    <i class="fas fa-bell"></i> Theo dõi giá
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/notifications') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/notifications">
                    <i class="fas fa-envelope"></i> Thông báo
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/pc-build-history') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/pc-build-history">
                    <i class="fas fa-desktop"></i> Lịch sử cấu hình PC
                </a>
            </li>
            <li class="nav-item mt-2 border-top pt-2">
                <a class="nav-link text-danger" href="${pageContext.request.contextPath}/auth?action=logout">
                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/support') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/support">
                    <i class="fas fa-headset"></i> Hỗ trợ khách hàng
                </a>
            </li>
        </ul>
    </nav>

    <!-- Cột phải: nội dung chính (footer.jsp sẽ đóng các thẻ này) -->
    <div class="user-main">
        <div class="user-content">
