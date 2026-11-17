<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<%-- Lấy URL hiện tại: gộp URL gốc (servlet, khi forward) + URL của file JSP,
     và đổi "_" thành "-" để tên file (audit_logs.jsp) khớp với URL (/admin/audit-logs) --%>
<c:set var="currentURL" value="${fn:replace(requestScope['jakarta.servlet.forward.request_uri'], '_', '-')} ${fn:replace(pageContext.request.requestURL, '_', '-')}" />

<%-- ADMIN / OWNER thấy tất cả; STAFF chỉ thấy mục được cấp quyền (xem hoặc quản lý) --%>
<c:set var="isFull" value="${sessionScope.userRoles.contains('ADMIN') || sessionScope.userRoles.contains('OWNER')}" />
<c:set var="perms" value="${sessionScope.userPermissions}" />

<%-- Sidebar ẩn/hiện bằng nút 3 gạch (#sidebarToggle) trên admin_header.jsp.
     CSS dùng chung với sidebar user nằm trong css/style.css (.app-sidebar) --%>
<nav class="sidebar-admin app-sidebar d-flex flex-column" id="appSidebar">
    <ul class="nav flex-column">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/dashboard') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-tachometer-alt"></i><span>Tổng quan</span>
            </a>
        </li>
        <c:if test="${isFull || perms.contains('REPORT_VIEW')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/report') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/report">
                <i class="fas fa-chart-bar"></i><span>Thống kê chi tiết</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/users') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users">
                <i class="fas fa-users"></i><span>Quản lý Người dùng</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/roles') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/roles">
                <i class="fas fa-shield-alt"></i><span>Phân quyền & Vai trò</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/audit-logs') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/audit-logs">
                <i class="fas fa-history"></i><span>Lịch sử thao tác</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('ORDER_VIEW') || perms.contains('ORDER_MANAGE') || perms.contains('PROMOTION_VIEW') || perms.contains('PROMOTION_MANAGE') || perms.contains('RETURN_VIEW') || perms.contains('RETURN_MANAGE') || perms.contains('REVIEW_VIEW') || perms.contains('REVIEW_MANAGE') || perms.contains('SUPPORT_VIEW') || perms.contains('SUPPORT_MANAGE')}">
        <li class="nav-item"><span class="sb-group-label">Bán hàng</span></li>
        <c:if test="${isFull || perms.contains('ORDER_VIEW') || perms.contains('ORDER_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/order') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/orders">
                <i class="fas fa-shopping-cart"></i><span>Quản lý Đơn hàng</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('PROMOTION_VIEW') || perms.contains('PROMOTION_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/promotion') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/promotion">
                <i class="fas fa-percent"></i><span>Khuyến mãi & Mã giảm giá</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('PROMOTION_VIEW') || perms.contains('PROMOTION_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/shipping') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/shipping">
                <i class="fas fa-truck"></i><span>Vận chuyển</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('RETURN_VIEW') || perms.contains('RETURN_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/returns') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/returns">
                <i class="fas fa-undo-alt"></i><span>Quản lý Đổi trả</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('REVIEW_VIEW') || perms.contains('REVIEW_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/reviews') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/reviews">
                <i class="fas fa-star"></i><span>Quản lý Đánh giá</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('SUPPORT_VIEW') || perms.contains('SUPPORT_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/support') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/support">
                <i class="fas fa-headset"></i><span>Hỗ trợ khách hàng</span>
            </a>
        </li>
        </c:if>
        </c:if>
        <c:if test="${isFull || perms.contains('PRODUCT_VIEW') || perms.contains('PRODUCT_MANAGE') || perms.contains('INVENTORY_VIEW') || perms.contains('INVENTORY_MANAGE')}">
        <li class="nav-item"><span class="sb-group-label">Linh kiện & Kho</span></li>
        <c:if test="${isFull || perms.contains('PRODUCT_VIEW') || perms.contains('PRODUCT_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/product') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/products">
                <i class="fas fa-box-open"></i><span>Quản lý Linh kiện</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('PRODUCT_VIEW') || perms.contains('PRODUCT_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/game') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/game">
                <i class="fas fa-gamepad"></i><span>Quản lý Trò chơi</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('INVENTORY_VIEW') || perms.contains('INVENTORY_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/inventory') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/inventory">
                <i class="fas fa-warehouse"></i><span>Tồn kho</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('INVENTORY_VIEW') || perms.contains('INVENTORY_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/purchase') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/purchase">
                <i class="fas fa-dolly"></i><span>Nhập hàng</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('INVENTORY_VIEW') || perms.contains('INVENTORY_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/supplier') && !fn:contains(currentURL, '/admin/supplier-return') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/supplier">
                <i class="fas fa-handshake"></i><span>Nhà cung cấp</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('INVENTORY_VIEW') || perms.contains('INVENTORY_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/supplier-return') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/supplier-return">
                <i class="fas fa-truck-ramp-box"></i><span>Trả hàng nhà cung cấp</span>
            </a>
        </li>
        </c:if>
        </c:if>
        <c:if test="${isFull || perms.contains('TECH_VIEW') || perms.contains('TECH_MANAGE') || perms.contains('COMPAT_VIEW') || perms.contains('COMPAT_MANAGE')}">
        <li class="nav-item"><span class="sb-group-label">Dịch vụ kỹ thuật</span></li>
        <c:if test="${isFull || perms.contains('TECH_VIEW') || perms.contains('TECH_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/assembly') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/assembly">
                <i class="fas fa-tools"></i><span>Lắp ráp PC</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('TECH_VIEW') || perms.contains('TECH_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/warrant') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/warranty">
                <i class="fas fa-shield-halved"></i><span>Bảo hành</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('COMPAT_VIEW') || perms.contains('COMPAT_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/compatibility') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/compatibility">
                <i class="fas fa-project-diagram"></i><span>Quy tắc tương thích</span>
            </a>
        </li>
        </c:if>
        </c:if>
        <c:if test="${isFull || perms.contains('CATALOG_VIEW') || perms.contains('CATALOG_MANAGE')}">
        <li class="nav-item"><span class="sb-group-label">Hệ thống</span></li>
        <c:if test="${isFull || perms.contains('CATALOG_VIEW') || perms.contains('CATALOG_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/catalog') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/catalog?type=brand">
                <i class="fas fa-layer-group"></i><span>Quản lý Danh mục</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('CATALOG_VIEW') || perms.contains('CATALOG_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/socket') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/socket">
                <i class="fas fa-microchip"></i><span>Quản lý Socket CPU</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('CATALOG_VIEW') || perms.contains('CATALOG_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/system-catalog') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/system-catalog?type=chipset">
                <i class="fas fa-cogs"></i><span>Danh mục hệ thống</span>
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('CATALOG_VIEW') || perms.contains('CATALOG_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/system ') || fn:contains(currentURL, '/admin/system.jsp') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/system">
                <i class="fas fa-sliders-h"></i><span>Cấu hình hệ thống</span>
            </a>
        </li>
        </c:if>
        </c:if>
    </ul>
</nav>

<!-- Cột phải: nội dung chính (footer.jsp sẽ đóng các thẻ này) -->
<div class="admin-main">
    <main class="admin-content">
