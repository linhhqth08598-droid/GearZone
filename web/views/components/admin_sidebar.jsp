<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<%-- Lấy URL hiện tại: gộp URL gốc (servlet, khi forward) + URL của file JSP,
     và đổi "_" thành "-" để tên file (audit_logs.jsp) khớp với URL (/admin/audit-logs) --%>
<c:set var="currentURL" value="${fn:replace(requestScope['jakarta.servlet.forward.request_uri'], '_', '-')} ${fn:replace(pageContext.request.requestURL, '_', '-')}" />

<nav class="sidebar-admin d-flex flex-column">
    <ul class="nav flex-column">
        <!-- TỔNG QUAN -->
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/dashboard') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-tachometer-alt"></i> Tổng quan
            </a>
        </li>

        <c:if test="${sessionScope.userRoles.contains('ADMIN') || sessionScope.userRoles.contains('OWNER')}">
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/admin/users') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users">
                    <i class="fas fa-users"></i> Quản lý Người dùng
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/admin/roles') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/roles">
                    <i class="fas fa-shield-alt"></i> Phân quyền & Vai trò
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${fn:contains(currentURL, '/admin/audit-logs') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/audit-logs">
                    <i class="fas fa-history"></i> Lịch sử thao tác
                </a>
            </li>
        </c:if>

        <!-- BÁN HÀNG -->
        <li class="nav-item mt-3">
            <span class="text-muted px-3 text-uppercase fw-bold" style="font-size: 11px; letter-spacing: 1px;">Bán hàng</span>
        </li>
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/orders') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/orders">
                <i class="fas fa-shopping-cart"></i> Quản lý Đơn hàng
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/returns') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/returns">
                <i class="fas fa-undo-alt"></i> Quản lý Đổi trả
            </a>
        </li>
        <li class="nav-item">
            <!-- Sửa /admin/reviews thành /admin/reviews -->
            <a class="nav-link ${fn:contains(currentURL, '/admin/reviews') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/reviews">
                <i class="fas fa-star"></i> Quản lý Đánh giá
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/product') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/products">
                <i class="fas fa-box-open"></i> Quản lý Linh kiện
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/support') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/support">
                <i class="fas fa-headset"></i> Hỗ trợ khách hàng
            </a>
        </li>

        <!-- HỆ THỐNG -->
        <li class="nav-item mt-3">
            <span class="text-muted px-3 text-uppercase fw-bold" style="font-size: 11px; letter-spacing: 1px;">Hệ thống</span>
        </li>

        <!-- Quản lý Danh mục -->
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/catalog') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/catalog?type=brand">
                <i class="fas fa-tags"></i> Quản lý Danh mục
            </a>
        </li>

        <!-- Quản lý Socket -->
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/socket') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/socket">
                <i class="fas fa-microchip"></i> Quản lý Socket CPU
            </a>
        </li>

        <!-- Danh mục hệ thống -->
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/system-catalog') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/system-catalog?type=chipset">
                <i class="fas fa-cogs"></i> Danh mục hệ thống
            </a>
        </li>

        <!-- Quy tắc tương thích -->
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/compatibility') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/compatibility">
                <i class="fas fa-project-diagram"></i> Quy tắc tương thích
            </a>
        </li>
    </ul>
</nav> <!-- Đóng thẻ nav của Sidebar -->

<!-- Cột phải: nội dung chính (footer.jsp sẽ đóng các thẻ này) -->
<div class="admin-main">
    <main class="admin-content">
