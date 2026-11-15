<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<%-- Lấy URL hiện tại: gộp URL gốc (servlet, khi forward) + URL của file JSP,
     và đổi "_" thành "-" để tên file (audit_logs.jsp) khớp với URL (/admin/audit-logs) --%>
<c:set var="currentURL" value="${fn:replace(requestScope['jakarta.servlet.forward.request_uri'], '_', '-')} ${fn:replace(pageContext.request.requestURL, '_', '-')}" />

<%-- ADMIN / OWNER thấy tất cả; STAFF chỉ thấy mục được cấp quyền (xem hoặc quản lý) --%>
<c:set var="isFull" value="${sessionScope.userRoles.contains('ADMIN') || sessionScope.userRoles.contains('OWNER')}" />
<c:set var="perms" value="${sessionScope.userPermissions}" />

<nav class="sidebar-admin d-flex flex-column">
    <ul class="nav flex-column">
        <!-- TỔNG QUAN -->
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/dashboard') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-tachometer-alt"></i> Tổng quan
            </a>
        </li>

        <!-- THỐNG KÊ CHI TIẾT -->
        <c:if test="${isFull || perms.contains('REPORT_VIEW')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/report') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/report">
                <i class="fas fa-chart-bar"></i> Thống kê chi tiết
            </a>
        </li>
        </c:if>

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
        <c:if test="${isFull || perms.contains('ORDER_VIEW') || perms.contains('ORDER_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/orders') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/orders">
                <i class="fas fa-shopping-cart"></i> Quản lý Đơn hàng
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('RETURN_VIEW') || perms.contains('RETURN_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/returns') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/returns">
                <i class="fas fa-undo-alt"></i> Quản lý Đổi trả
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('REVIEW_VIEW') || perms.contains('REVIEW_MANAGE')}">
        <li class="nav-item">
            <!-- Sửa /admin/reviews thành /admin/reviews -->
            <a class="nav-link ${fn:contains(currentURL, '/admin/reviews') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/reviews">
                <i class="fas fa-star"></i> Quản lý Đánh giá
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('PRODUCT_VIEW') || perms.contains('PRODUCT_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/product') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/products">
                <i class="fas fa-box-open"></i> Quản lý Linh kiện
            </a>
        </li>
        </c:if>
        <c:if test="${isFull || perms.contains('SUPPORT_VIEW') || perms.contains('SUPPORT_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/support') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/support">
                <i class="fas fa-headset"></i> Hỗ trợ khách hàng
            </a>
        </li>
        </c:if>

        <!-- HỆ THỐNG -->
        <li class="nav-item mt-3">
            <span class="text-muted px-3 text-uppercase fw-bold" style="font-size: 11px; letter-spacing: 1px;">Hệ thống</span>
        </li>

        <!-- Quản lý Danh mục -->
        <c:if test="${isFull || perms.contains('CATALOG_VIEW') || perms.contains('CATALOG_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/catalog') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/catalog?type=brand">
                <i class="fas fa-tags"></i> Quản lý Danh mục
            </a>
        </li>
        </c:if>

        <!-- Quản lý Socket -->
        <c:if test="${isFull || perms.contains('CATALOG_VIEW') || perms.contains('CATALOG_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/socket') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/socket">
                <i class="fas fa-microchip"></i> Quản lý Socket CPU
            </a>
        </li>
        </c:if>

        <!-- Danh mục hệ thống -->
        <c:if test="${isFull || perms.contains('CATALOG_VIEW') || perms.contains('CATALOG_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/system-catalog') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/system-catalog?type=chipset">
                <i class="fas fa-cogs"></i> Danh mục hệ thống
            </a>
        </li>
        </c:if>

        <!-- Cấu hình hệ thống (ngưỡng nguồn PSU, tiêu chí gaming) -->
        <c:if test="${isFull || perms.contains('CATALOG_VIEW') || perms.contains('CATALOG_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/system ') || fn:contains(currentURL, '/admin/system.jsp') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/system">
                <i class="fas fa-sliders-h"></i> Cấu hình hệ thống
            </a>
        </li>
        </c:if>

        <!-- Quy tắc tương thích -->
        <c:if test="${isFull || perms.contains('COMPAT_VIEW') || perms.contains('COMPAT_MANAGE')}">
        <li class="nav-item">
            <a class="nav-link ${fn:contains(currentURL, '/admin/compatibility') ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/compatibility">
                <i class="fas fa-project-diagram"></i> Quy tắc tương thích
            </a>
        </li>
        </c:if>
    </ul>
</nav> <!-- Đóng thẻ nav của Sidebar -->

<!-- Cột phải: nội dung chính (footer.jsp sẽ đóng các thẻ này) -->
<div class="admin-main">
    <main class="admin-content">
