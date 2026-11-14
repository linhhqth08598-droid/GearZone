<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
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
            body {
                background-color: #f1f5f9;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                min-height: 100vh;
                display: flex;
                flex-direction: column;
            }
            /* Navbar cho Admin */
            .navbar-admin {
                background-color: #ffffff;
                border-bottom: 2px solid #3b82f6; /* Màu xanh dương chủ đạo của Admin */
                padding: 12px 0;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                position: sticky;
                top: 0;
                z-index: 1000;
            }
            .navbar-admin .navbar-brand {
                font-size: 22px;
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
            }
            .navbar-admin .nav-link:hover {
                color: #3b82f6;
            }

                        /* Cấu trúc Sidebar + Main */
            .admin-wrapper {
                display: flex;
                flex: 1;
                width: 100%;
                margin-top: 65px; /* Đẩy nội dung xuống dưới navbar */
            }
            .sidebar-admin {
                width: 250px;
                height: calc(100vh - 65px);
                position: fixed; /* CỐ ĐỊNH SIDEBAR */
                top: 65px;
                left: 0;
                overflow-y: auto;
                background-color: #ffffff;
                border-right: 1px solid #e2e8f0;
                z-index: 100;
            }
            .sidebar-admin .nav-link {
                color: #475569;
                font-weight: 500;
                padding: 12px 20px;
                border-left: 3px solid transparent;
                transition: all 0.2s ease;
            }
            .sidebar-admin .nav-link:hover, .sidebar-admin .nav-link.active {
                background-color: #f8fafc;
                color: #3b82f6;
                border-left-color: #3b82f6;
            }
            .sidebar-admin .nav-link i {
                width: 25px;
                text-align: center;
                margin-right: 8px;
            }
        </style>
    </head>
    <body>
        <!-- Navbar Admin -->
        <nav class="navbar navbar-expand-lg navbar-admin">
            <div class="container-fluid px-4">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">GEAR<span>ZONE</span> ADMIN</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="adminNavbar">
                    <ul class="navbar-nav ms-auto align-items-center">
                        <li class="nav-item me-3">
                            <a class="nav-link position-relative" href="${pageContext.request.contextPath}/user/notifications" title="Thông báo">
                                <i class="fas fa-bell fs-5"></i>
                            </a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="adminDropdown" role="button" data-bs-toggle="dropdown">
                                <i class="fas fa-user-shield me-1"></i> ${sessionScope.loggedUser.fullName}
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/home"><i class="fas fa-home me-2 text-muted"></i>Về trang chủ</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth?action=logout"><i class="fas fa-sign-out-alt me-2"></i>Đăng xuất</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

                <!-- Wrapper chứa Sidebar và Nội dung chính -->
        <div class="admin-wrapper">
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>