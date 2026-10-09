<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header>
    <div class="logo">
        <a href="${pageContext.request.contextPath}/home">GEAR<span>ZONE</span></a>
    </div>
    
    <c:set var="activeMenu" value="${requestScope.activeMenu}" />
    
    <nav class="main-nav">
        <!-- DROPDOWN MEGA MENU DANH MỤC SẢN PHẨM -->
        <div class="dropdown-category">
            <a href="#" class="cat-toggle"><i class="fa-solid fa-bars"></i> Danh mục sản phẩm <i class="fa-solid fa-chevron-down" style="font-size: 11px;"></i></a>
            <div class="dropdown-menu-box">
                <div class="cat-item">
                    <a href="${pageContext.request.contextPath}/products?cat=laptop"><i class="fa-solid fa-laptop"></i> Laptop Gaming <i class="fa-solid fa-angle-right"></i></a>
                    <div class="sub-menu">
                        <h4>Thương hiệu Laptop</h4>
                        <a href="${pageContext.request.contextPath}/products?brand=asus">ASUS ROG / TUF</a>
                        <a href="${pageContext.request.contextPath}/products?brand=lenovo">Lenovo Legion</a>
                        <a href="${pageContext.request.contextPath}/products?brand=msi">MSI Gaming</a>
                    </div>
                </div>
                <div class="cat-item">
                    <a href="${pageContext.request.contextPath}/products?cat=mouse"><i class="fa-solid fa-computer-mouse"></i> Chuột Gaming <i class="fa-solid fa-angle-right"></i></a>
                    <div class="sub-menu">
                        <h4>Hãng chuột nổi bật</h4>
                        <a href="${pageContext.request.contextPath}/products?brand=attackshark">Attack Shark</a>
                        <a href="${pageContext.request.contextPath}/products?brand=razer">Razer</a>
                        <a href="${pageContext.request.contextPath}/products?brand=logitech">Logitech G</a>
                    </div>
                </div>
                <div class="cat-item">
                    <a href="${pageContext.request.contextPath}/products?cat=keyboard"><i class="fa-solid fa-keyboard"></i> Bàn Phím Cơ <i class="fa-solid fa-angle-right"></i></a>
                    <div class="sub-menu">
                        <h4>Loại bàn phím</h4>
                        <a href="${pageContext.request.contextPath}/products?brand=xinmeng">Xinmeng Custom</a>
                        <a href="${pageContext.request.contextPath}/products?brand=akko">Akko Keyboard</a>
                    </div>
                </div>
                <div class="cat-item">
                    <a href="${pageContext.request.contextPath}/products?cat=pc"><i class="fa-solid fa-microchip"></i> Linh Kiện PC</a>
                </div>
            </div>
        </div>

        <a href="${pageContext.request.contextPath}/home" class="${activeMenu == 'home' ? 'active' : ''}">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/products" class="${activeMenu == 'products' ? 'active' : ''}">Sản phẩm</a>
        <a href="${pageContext.request.contextPath}/promotions" class="${activeMenu == 'promotions' ? 'active' : ''}">Khuyến mãi</a>
        <a href="${pageContext.request.contextPath}/contact" class="${activeMenu == 'contact' ? 'active' : ''}">Liên hệ</a>
    </nav>
    
    <div class="user-nav" id="user-nav">
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