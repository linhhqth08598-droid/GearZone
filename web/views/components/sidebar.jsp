<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<%-- Chỉ hiện sidebar khi đã đăng nhập (khách xem trang công khai như bình thường, footer tự đóng đúng thẻ) --%>
<c:if test="${not empty sessionScope.loggedUser}">
<%-- Báo cho footer.jsp biết đây là layout user có sidebar (để đóng đúng các thẻ) --%>
<c:set var="userSidebarLayout" value="true" scope="request" />

<%-- URL hiện tại (gộp URL servlet gốc + URL file JSP, "_" -> "-") để tô sáng mục đang xem --%>
<c:set var="currentURL" value="${fn:replace(requestScope['jakarta.servlet.forward.request_uri'], '_', '-')} ${fn:replace(pageContext.request.requestURL, '_', '-')}" />

<style>
    /* Layout: sidebar sát mép trái + nội dung bên phải (giống trang Admin).
       CSS của sidebar dùng chung với admin nằm trong css/style.css (.app-sidebar) */
    main:has(> .user-wrapper) { padding: 0 !important; }
    .user-wrapper { display: flex; flex: 1; width: 100%; }
    .user-main { flex: 1; min-width: 0; display: flex; flex-direction: column; }
    .user-content { flex: 1; padding: 24px; }
    .user-footer { background: #fff; border-top: 1px solid #e2e8f0; padding: 15px 0; text-align: center; }
</style>

<script>
    /* Trang có sidebar -> hiện nút 3 gạch trên header (trang không có sidebar thì nút vẫn ẩn) */
    (function () {
        var btn = document.getElementById('sidebarToggle');
        if (btn) btn.hidden = false;
    })();
</script>

<div class="user-wrapper">
    <nav class="sidebar-user app-sidebar d-flex flex-column" id="appSidebar">
        <ul class="nav flex-column">
            <li class="nav-item"><span class="sb-group-label">Mua sắm</span></li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/home">
                    <i class="fas fa-store"></i><span>Trang chủ</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/product') && !fn:contains(currentURL, '/compare') ? 'active' : ''}" href="${pageContext.request.contextPath}/product">
                    <i class="fas fa-microchip"></i><span>Linh kiện PC</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/compare') ? 'active' : ''}" href="${pageContext.request.contextPath}/compare">
                    <i class="fas fa-balance-scale"></i><span>So sánh linh kiện</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/build-pc') ? 'active' : ''}" href="${pageContext.request.contextPath}/build-pc">
                    <i class="fas fa-screwdriver-wrench"></i><span>Build PC</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/promotion') ? 'active' : ''}" href="${pageContext.request.contextPath}/promotion">
                    <i class="fas fa-percent"></i><span>Khuyến mãi</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/cart') ? 'active' : ''}" href="${pageContext.request.contextPath}/cart">
                    <i class="fas fa-shopping-cart"></i><span>Giỏ hàng</span>
                </a>
            </li>
            <li class="nav-item"><span class="sb-group-label">Tài khoản</span></li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/profile') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/profile">
                    <i class="fas fa-id-badge"></i><span>Hồ sơ cá nhân</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/address') ? 'active' : ''}" href="${pageContext.request.contextPath}/user?action=address">
                    <i class="fas fa-map-marker-alt"></i><span>Quản lý địa chỉ</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/notifications') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/notifications">
                    <i class="fas fa-envelope"></i><span>Thông báo</span>
                </a>
            </li>
            <li class="nav-item"><span class="sb-group-label">Đơn hàng & Dịch vụ</span></li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/order-history') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/order-history">
                    <i class="fas fa-shopping-bag"></i><span>Lịch sử mua hàng</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/returns') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/returns">
                    <i class="fas fa-undo-alt"></i><span>Yêu cầu đổi trả</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/assembly') ? 'active' : ''}" href="${pageContext.request.contextPath}/assembly">
                    <i class="fas fa-tools"></i><span>Lắp ráp PC</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/warranty') && !fn:contains(currentURL, '/warranty-lookup') ? 'active' : ''}" href="${pageContext.request.contextPath}/warranty">
                    <i class="fas fa-shield-halved"></i><span>Bảo hành</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/warranty-lookup') ? 'active' : ''}" href="${pageContext.request.contextPath}/warranty-lookup">
                    <i class="fas fa-magnifying-glass"></i><span>Tra cứu bảo hành</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/upgrade-pc') ? 'active' : ''}" href="${pageContext.request.contextPath}/upgrade-pc">
                    <i class="fas fa-arrow-up-right-dots"></i><span>Nâng cấp PC</span>
                </a>
            </li>
            <li class="nav-item"><span class="sb-group-label">Cá nhân hóa</span></li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/wishlist') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/wishlist">
                    <i class="fas fa-heart"></i><span>Sản phẩm yêu thích</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/price-alert') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/price-alert">
                    <i class="fas fa-bell"></i><span>Theo dõi giá</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/pc-build-history') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/pc-build-history">
                    <i class="fas fa-desktop"></i><span>Lịch sử cấu hình PC</span>
                </a>
            </li>
            <li class="nav-item"><span class="sb-group-label">Hỗ trợ</span></li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/user/support') ? 'active' : ''}" href="${pageContext.request.contextPath}/user/support">
                    <i class="fas fa-headset"></i><span>Hỗ trợ khách hàng</span>
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-danger" href="${pageContext.request.contextPath}/auth?action=logout">
                    <i class="fas fa-sign-out-alt"></i><span>Đăng xuất</span>
                </a>
            </li>
        </ul>
    </nav>

    <!-- Cột phải: nội dung chính (footer.jsp sẽ đóng các thẻ này) -->
    <div class="user-main">
        <div class="user-content ${requestScope.sidebarFlush ? 'p-0' : ''}">
</c:if>
