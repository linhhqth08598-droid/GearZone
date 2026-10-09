<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="DAO.BrandDAO" %>
<%@ page import="Models.Brand" %>
<%@ page import="java.util.List" %>

<%
    // Lấy dữ liệu Brands động từ Database
    BrandDAO brandDAO = new BrandDAO();
    request.setAttribute("laptopBrands", brandDAO.getBrandsByCategory(1));
    request.setAttribute("mouseBrands", brandDAO.getBrandsByCategory(2));
    request.setAttribute("keyboardBrands", brandDAO.getBrandsByCategory(3));
    request.setAttribute("pcBrands", brandDAO.getBrandsByCategory(4));
%>

<style>
/* CSS Menu Dropdown chống vỡ layout */
.main-nav { display: flex; align-items: center; position: relative; }
.dropdown-category { position: relative; }
.dropdown-menu-box { display: none; position: absolute; top: 100%; left: 0; background-color: #1e293b; width: 280px; box-shadow: 0 10px 25px rgba(0,0,0,0.5); z-index: 9999; border-radius: 6px; border: 1px solid #334155; }
.dropdown-category:hover .dropdown-menu-box { display: block; }
.cat-item { position: relative; border-bottom: 1px solid #334155; }
.cat-item:last-child { border-bottom: none; }
.cat-item > a { color: #e2e8f0 !important; display: flex !important; justify-content: space-between; padding: 14px 18px !important; margin: 0 !important; font-size: 14px; }
.cat-item:hover > a { background-color: #0f172a; color: #ff4757 !important; }
.sub-menu { display: none; position: absolute; top: -1px; left: 100%; background-color: #1e293b; width: 240px; box-shadow: 0 10px 25px rgba(0,0,0,0.5); border-radius: 6px; border: 1px solid #334155; padding: 10px 0; }
.cat-item:hover .sub-menu { display: flex; flex-direction: column; }
.sub-menu h4 { color: #94a3b8; font-size: 12px; padding: 8px 18px; margin: 0; text-transform: uppercase; border-bottom: 1px solid #334155; margin-bottom: 5px; }
.sub-menu a { color: #e2e8f0 !important; padding: 10px 18px !important; margin: 0 !important; display: block; font-weight: normal; font-size: 14px; }
.sub-menu a:hover { color: #ff4757 !important; background-color: #0f172a; padding-left: 24px !important; transition: 0.2s; }
</style>

<header>
    <div class="logo">
        <a href="${pageContext.request.contextPath}/home">GEAR<span>ZONE</span></a>
    </div>
    
    <c:set var="activeMenu" value="${requestScope.activeMenu}" />
    
    <nav class="main-nav">
        <div class="dropdown-category">
            <a href="#" class="cat-toggle" style="margin: 0 14px;"><i class="fa-solid fa-bars"></i> Danh mục sản phẩm <i class="fa-solid fa-chevron-down" style="font-size: 11px; margin-left: 5px;"></i></a>
            <div class="dropdown-menu-box">
                <!-- Vòng lặp lấy Data Laptop -->
                <div class="cat-item">
                    <a href="${pageContext.request.contextPath}/products?categoryId=1"><i class="fa-solid fa-laptop"></i> Máy Tính Xách Tay <i class="fa-solid fa-angle-right"></i></a>
                    <div class="sub-menu">
                        <h4>Thương hiệu Laptop</h4>
                        <c:forEach items="${laptopBrands}" var="b">
                            <a href="${pageContext.request.contextPath}/products?brandId=${b.brandId}">${b.brandName}</a>
                        </c:forEach>
                    </div>
                </div>
                <!-- Vòng lặp lấy Data Chuột -->
                <div class="cat-item">
                    <a href="${pageContext.request.contextPath}/products?categoryId=2"><i class="fa-solid fa-computer-mouse"></i> Chuột <i class="fa-solid fa-angle-right"></i></a>
                    <div class="sub-menu">
                        <h4>Hãng Chuột</h4>
                        <c:forEach items="${mouseBrands}" var="b">
                            <a href="${pageContext.request.contextPath}/products?brandId=${b.brandId}">${b.brandName}</a>
                        </c:forEach>
                    </div>
                </div>
                <!-- Vòng lặp lấy Data Bàn phím -->
                <div class="cat-item">
                    <a href="${pageContext.request.contextPath}/products?categoryId=3"><i class="fa-solid fa-keyboard"></i> Bàn Phím <i class="fa-solid fa-angle-right"></i></a>
                    <div class="sub-menu">
                        <h4>Hãng Bàn Phím</h4>
                        <c:forEach items="${keyboardBrands}" var="b">
                            <a href="${pageContext.request.contextPath}/products?brandId=${b.brandId}">${b.brandName}</a>
                        </c:forEach>
                    </div>
                </div>
                <!-- Vòng lặp lấy Data Linh kiện -->
                <div class="cat-item">
                    <a href="${pageContext.request.contextPath}/products?categoryId=4"><i class="fa-solid fa-microchip"></i> Linh Kiện PC <i class="fa-solid fa-angle-right"></i></a>
                    <div class="sub-menu">
                        <h4>Linh Kiện Máy Tính</h4>
                        <c:forEach items="${pcBrands}" var="b">
                            <a href="${pageContext.request.contextPath}/products?brandId=${b.brandId}">${b.brandName}</a>
                        </c:forEach>
                    </div>
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
                
                <!-- BỔ SUNG: NẾU LÀ ADMIN THÌ HIỆN NÚT QUẢN LÝ -->
                <c:if test="${sessionScope.userAcc.roleId <= 3}">
                    <a href="${pageContext.request.contextPath}/admin/system" style="margin-right:15px; color:#ff4757; text-decoration:none; font-weight: bold;">
                        <i class="fa-solid fa-gears"></i> Quản Lý
                    </a>
                </c:if>

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