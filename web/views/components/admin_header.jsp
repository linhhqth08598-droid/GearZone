<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="adminLayout" value="true" scope="request" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>GearZone Admin - Quản trị hệ thống</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/components/image/GearZone.png">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/views/components/css/style.css">

        <style>
            /* === FIX LỖI LỆCH TRANG DO THANH CUỘN === */
            html {
                scrollbar-gutter: stable;
            }
            :root { --admin-nav-h: 64px; --admin-sidebar-w: 250px; }
            body {
                background-color: #f1f5f9;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                min-height: 100vh;
                display: flex;
                flex-direction: column;
                margin: 0;
            }
            
            /* === NAVBAR ADMIN (Giống hệt User, chỉ đổi màu) === */
            .navbar-admin {
                background-color: #ffffff;
                border-bottom: 2px solid #3b82f6; /* Màu xanh Admin */
                padding: 15px 0; 
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                position: sticky;
                top: 0;
                z-index: 1000;
            }
            .navbar-admin .navbar-brand {
                font-size: 24px; 
                font-weight: 900;
                color: #0f1117;
                letter-spacing: 1px;
            }
            .navbar-admin .navbar-brand span {
                color: #3b82f6;
            }
            .navbar-admin .nav-link {
                color: #475569;
                font-weight: 600;
                font-size: 15px; 
                margin: 0 10px; 
                transition: 0.3s;
            }
            .navbar-admin .nav-link:hover {
                color: #3b82f6;
            }

            /* === CẤU TRÚC SIDEBAR + MAIN === */
            .admin-wrapper {
                display: flex;
                flex: 1;
                width: 100%;
            }
            .sidebar-admin {
                flex: 0 0 var(--admin-sidebar-w);
                width: var(--admin-sidebar-w);
                position: sticky;
                top: var(--admin-nav-h);
                align-self: flex-start;
                height: calc(100vh - var(--admin-nav-h));
                overflow-y: auto;
                background-color: #ffffff;
                border-right: 1px solid #e2e8f0;
                z-index: 100;
            }
            .admin-main {
                flex: 1;
                min-width: 0;
                display: flex;
                flex-direction: column;
            }
            .admin-content {
                flex: 1;
                padding: 24px;
            }
            /* Tiêu đề trang admin cùng cỡ chữ với trang user (h3.fw-bold) */
            .admin-content h1,
            .admin-content h1.h2 {
                font-size: calc(1.3rem + 0.6vw);
                font-weight: 700;
            }
            @media (min-width: 1200px) {
                .admin-content h1,
                .admin-content h1.h2 { font-size: 1.75rem; }
            }
            .admin-footer {
                background: #fff;
                border-top: 1px solid #e2e8f0;
                padding: 15px 0;
                text-align: center;
            }
            .sidebar-admin .nav-link {
                color: #475569;
                font-weight: 500;
                padding: 12px 20px;
                border-left: 3px solid transparent;
                transition: all 0.2s ease;
            }
            .sidebar-admin .nav-link:hover {
                background-color: #f8fafc;
                color: #3b82f6;
                border-left-color: #3b82f6;
            }
            .sidebar-admin .nav-link.active {
                background-color: #eff6ff;
                color: #3b82f6;
                border-left-color: #3b82f6;
                font-weight: 600;
            }
            .sidebar-admin .nav-link i {
                width: 25px;
                text-align: center;
                margin-right: 8px;
            }
        </style>
    </head>
    <body>
        <!-- Navbar Admin: Dùng container để căn giữa giống User -->
        <nav class="navbar navbar-expand-lg navbar-admin">
            <div class="container">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">GEAR<span>ZONE</span> ADMIN</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="adminNavbar">
                    <!-- Menu bên trái (giống User) -->
                    <ul class="navbar-nav me-auto">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Về trang chủ</a></li>
                    </ul>
                    
                    <!-- Menu bên phải (Thông báo, Avatar, Tên) -->
                    <ul class="navbar-nav align-items-center">
                        <li class="nav-item me-2">
                            <a class="nav-link position-relative" href="${pageContext.request.contextPath}/user/notifications" title="Thông báo">
                                <i class="fas fa-bell fs-5"></i>
                                <c:set var="unreadCount" value="${sessionScope.unreadCount}" />
                                <c:if test="${unreadCount > 0}">
                                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 10px;">
                                        ${unreadCount}
                                    </span>
                                </c:if>
                            </a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" id="adminDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.loggedUser.avatarUrl}">
                                        <img src="${sessionScope.loggedUser.avatarUrl}" alt="Avatar" class="rounded-circle" style="width: 30px; height: 30px; object-fit: cover; border: 2px solid #3b82f6;">
                                    </c:when>
                                    <c:otherwise>
                                        <i class="fas fa-user-shield fs-4"></i>
                                    </c:otherwise>
                                </c:choose>
                                <span>${sessionScope.loggedUser.fullName}</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" style="background-color: #ffffff; border: 1px solid #e2e8f0; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/home"><i class="fas fa-home me-2 text-muted"></i>Về trang chủ</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth?action=logout"><i class="fas fa-sign-out-alt me-2"></i>Đăng xuất</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <script>
    /* Đo chiều cao thật của navbar để sidebar dính đúng dưới navbar, không gây tràn trang */
    (function () {
        var nav = document.querySelector('.navbar-admin');
        function setH() { if (nav) document.documentElement.style.setProperty('--admin-nav-h', nav.offsetHeight + 'px'); }
        setH();
        window.addEventListener('resize', setH);
        window.addEventListener('load', setH);
    })();
</script>
<div class="admin-wrapper">