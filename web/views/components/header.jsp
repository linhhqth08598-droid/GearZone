<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>GearZone - Chuyên linh kiện PC & Gaming</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/components/image/GearZone.png">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/views/components/css/style.css">

        <style>
            /* === FIX LỖI LỆCH TRANG DO THANH CUỘN === */
            html {
                scrollbar-gutter: stable;
            }
            body {
                background-color: #f1f5f9;
                color: #334155;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                display: flex;
                flex-direction: column;
                min-height: 100vh;
                margin: 0;
            }
            main {
                flex: 1;
                display: flex;
                flex-direction: column;
                padding: 20px 0;
            }
            footer {
                padding: 15px 0 !important;
            }
            
            /* === NAVBAR USER === */
            .navbar-custom {
                background-color: #ffffff;
                border-bottom: 2px solid #ff4757;
                padding: 15px 0;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                position: sticky;
                top: 0;
                z-index: 1000;
            }
            .navbar-custom .navbar-brand {
                font-size: 24px;
                font-weight: 900;
                color: #0f1117;
                letter-spacing: 1px;
            }
            .navbar-custom .navbar-brand span {
                color: #ff4757;
            }
            .navbar-custom .nav-link {
                color: #475569;
                font-weight: 600;
                font-size: 15px;
                margin: 0 10px;
                transition: 0.3s;
            }
            .navbar-custom .nav-link:hover {
                color: #ff4757;
            }
            .btn-register {
                background-color: #ff4757;
                color: white !important;
                border-radius: 6px;
                padding: 8px 20px !important;
            }
            .btn-register:hover {
                background-color: #ff2a3d;
            }
        </style>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-custom">
            <div class="container">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/home">GEAR<span>ZONE</span></a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav me-auto">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                        <li class="nav-item"><a class="nav-link" href="#">Linh kiện PC</a></li>
                        <li class="nav-item"><a class="nav-link" href="#">Build PC</a></li>
                    </ul>
                    <ul class="navbar-nav align-items-center">
                        <c:if test="${not empty sessionScope.loggedUser}">
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
                        </c:if>
                        <c:choose>
                            <c:when test="${not empty sessionScope.loggedUser}">
                                <li class="nav-item dropdown">
                                    <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.loggedUser.avatarUrl}">
                                                <img src="${sessionScope.loggedUser.avatarUrl}" alt="Avatar" class="rounded-circle" style="width: 30px; height: 30px; object-fit: cover; border: 2px solid #ff4757;">
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-user-circle fs-4"></i>
                                            </c:otherwise>
                                        </c:choose>
                                        <span>${sessionScope.loggedUser.fullName}</span>
                                    </a>
                                    <ul class="dropdown-menu dropdown-menu-end" style="background-color: #ffffff; border: 1px solid #e2e8f0; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="fas fa-id-badge me-2 text-muted"></i>Hồ sơ cá nhân</a></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/order-history"><i class="fas fa-shopping-bag me-2 text-muted"></i>Lịch sử mua hàng</a></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/wishlist"><i class="fas fa-heart me-2 text-muted"></i>Sản phẩm yêu thích</a></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/pc-build-history"><i class="fas fa-desktop me-2 text-muted"></i>Lịch sử cấu hình PC</a></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/price-alert"><i class="fas fa-bell me-2 text-muted"></i>Theo dõi giá</a></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/notifications"><i class="fas fa-envelope me-2 text-muted"></i>Thông báo</a></li>
                                        <li><hr class="dropdown-divider"></li>
                                        <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/auth?action=logout"><i class="fas fa-sign-out-alt me-2"></i>Đăng xuất</a></li>
                                    </ul>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <c:set var="currentAction" value="${param.action}" />
                                <li class="nav-item">
                                    <a class="nav-link ${currentAction == 'login' ? 'btn-register' : ''} ${currentAction == 'login' ? 'ms-2' : ''}" 
                                       href="${pageContext.request.contextPath}/auth?action=login">Đăng nhập</a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link ${currentAction == 'register' ? 'btn-register' : ''} ${currentAction != 'login' ? 'ms-2' : ''}" 
                                       href="${pageContext.request.contextPath}/auth?action=register">Đăng ký</a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>
        </nav>
        <main class="flex-grow-1 w-100">