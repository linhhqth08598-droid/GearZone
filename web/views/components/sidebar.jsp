<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<style>
    .user-sidebar {
        background-color: #ffffff;
        border-radius: 16px;
        border: 1px solid #e2e8f0;
        overflow: hidden;
        box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
    }
    .user-sidebar .user-info {
        padding: 25px 20px;
        text-align: center;
        border-bottom: 1px solid #f1f5f9;
    }
    .user-sidebar .avatar-circle {
        width: 70px;
        height: 70px;
        background: #ff4757;
        color: white;
        font-size: 28px;
        font-weight: bold;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        margin: 0 auto 15px auto;
        box-shadow: 0 4px 10px rgba(255, 71, 87, 0.3);
    }
    .user-sidebar .nav-link {
        color: #475569;
        font-weight: 500;
        padding: 12px 20px;
        border-left: 3px solid transparent;
        transition: all 0.2s ease;
        display: flex;
        align-items: center;
    }
    .user-sidebar .nav-link:hover, .user-sidebar .nav-link.active {
        background-color: #f8fafc;
        color: #ff4757;
        border-left-color: #ff4757;
    }
    .user-sidebar .nav-link i {
        width: 25px;
        text-align: center;
        margin-right: 10px;
    }
</style>

<div class="user-sidebar mb-4">
    <!-- Phần thông tin user -->
    <div class="user-info">
        <c:choose>
            <c:when test="${not empty sessionScope.loggedUser.avatarUrl}">
                <img src="${sessionScope.loggedUser.avatarUrl}" alt="Avatar" class="rounded-circle mb-3" style="width: 70px; height: 70px; object-fit: cover; border: 3px solid #ff4757;">
            </c:when>
            <c:otherwise>
                <div class="avatar-circle">
                    ${sessionScope.loggedUser.fullName.substring(0, 1).toUpperCase()}
                </div>
            </c:otherwise>
        </c:choose>
        <h6 class="fw-bold mb-1">${sessionScope.loggedUser.fullName}</h6>
        <p class="text-muted mb-0" style="font-size: 13px;">${sessionScope.loggedUser.email}</p>
    </div>

    <!-- Danh sách menu -->
    <ul class="nav flex-column py-2">
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/user/profile">
                <i class="fas fa-id-badge"></i> Hồ sơ cá nhân
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/user/order-history">
                <i class="fas fa-shopping-bag"></i> Lịch sử mua hàng
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/user/wishlist">
                <i class="fas fa-heart"></i> Sản phẩm yêu thích
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/user/returns">
                <i class="fas fa-undo-alt"></i> Yêu cầu đổi trả
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/user/price-alert">
                <i class="fas fa-bell"></i> Theo dõi giá
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/user/notifications">
                <i class="fas fa-envelope"></i> Thông báo
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/user/pc-build-history">
                <i class="fas fa-desktop"></i> Lịch sử cấu hình PC
            </a>
        </li>
        <li class="nav-item mt-2 border-top pt-2">
            <a class="nav-link text-danger" href="${pageContext.request.contextPath}/auth?action=logout">
                <i class="fas fa-sign-out-alt"></i> Đăng xuất
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/user/support">
                <i class="fas fa-headset"></i> Hỗ trợ khách hàng
            </a>
        </li>
    </ul>
</div>