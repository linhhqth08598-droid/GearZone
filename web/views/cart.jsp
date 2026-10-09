<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Giỏ Hàng Của Bạn - GearZone</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.1">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <jsp:include page="includes/header.jsp" />

        <div class="container main-layout">
            <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
            <div class="layout-right">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #334155; padding-bottom: 10px;">
                    <h2 class="section-title" style="margin: 0; border: none; padding: 0;"><i class="fa-solid fa-cart-shopping" style="color: #ff4757;"></i> Giỏ Hàng Của Bạn</h2>
                </div>

                <div class="cart-container" style="background: #1a1d29; border-radius: 12px; border: 1px solid #2d3748; padding: 25px;">
                    <c:choose>
                        <c:when test="${empty cartItems}">
                            <div style="text-align: center; padding: 50px 0;">
                                <i class="fa-solid fa-cart-arrow-down" style="font-size: 80px; color: #334155; margin-bottom: 20px;"></i>
                                <h3 style="color: #cbd5e1; margin-bottom: 15px;">Giỏ hàng của bạn đang trống!</h3>
                                <p style="color: #94a3b8; margin-bottom: 25px;">Có vẻ như bạn chưa thêm sản phẩm nào vào giỏ hàng.</p>
                                <a href="${pageContext.request.contextPath}/products" class="btn-submit" style="width: auto; display: inline-block; padding: 12px 30px; text-decoration: none;">Tiếp Tục Mua Sắm</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <table class="cart-table">
                                <thead>
                                    <tr>
                                        <th>Sản Phẩm</th>
                                        <th style="text-align: right;">Đơn Giá</th>
                                        <th style="text-align: center;">Số Lượng</th>
                                        <th style="text-align: right;">Thành Tiền</th>
                                        <th style="text-align: center;">Xóa</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <fmt:setLocale value="vi_VN"/>
                                    <c:forEach items="${cartItems}" var="item">
                                        <tr>
                                            <td>
                                                <div style="display: flex; align-items: center;">
                                                    <c:choose>
                                                        <c:when test="${not empty item.imageBase64}">
                                                            <img src="${item.imageBase64}" alt="${item.productName}" class="cart-item-img">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="cart-item-img" style="display: flex; align-items: center; justify-content: center;">
                                                                <i class="fa-solid fa-box" style="color: #64748b;"></i>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <div style="margin-left: 15px;">
                                                        <h4 style="color: #fff; margin: 0; font-size: 16px;">${item.productName}</h4>
                                                    </div>
                                                </div>
                                            </td>
                                            <td style="text-align: right; color: #cbd5e1; font-weight: 500;">
                                                <fmt:formatNumber value="${item.basePrice}" pattern="#,###"/> ₫
                                            </td>
                                            <td style="text-align: center;">
                                                <form action="${pageContext.request.contextPath}/update-cart" method="POST" style="display: flex; align-items: center; justify-content: center; gap: 5px;">
                                                    <input type="hidden" name="id" value="${item.cartItemId}">

                                                    <!-- Nút giảm -->
                                                    <button type="submit" onclick="this.parentNode.querySelector('input[type=number]').stepDown()" style="background: #334155; color: #fff; border: none; width: 28px; height: 28px; border-radius: 4px; cursor: pointer;">-</button>

                                                    <!-- Ô nhập số lượng (tự động submit khi gõ số khác và nhấn Enter hoặc nhấn ra ngoài) -->
                                                    <input type="number" name="quantity" value="${item.quantity}" min="1" 
                                                           onchange="this.form.submit()"
                                                           style="width: 50px; height: 28px; text-align: center; background: #0f1117; border: 1px solid #334155; color: #fff; border-radius: 4px; font-weight: bold;">

                                                    <!-- Nút tăng -->
                                                    <button type="submit" onclick="this.parentNode.querySelector('input[type=number]').stepUp()" style="background: #334155; color: #fff; border: none; width: 28px; height: 28px; border-radius: 4px; cursor: pointer;">+</button>
                                                </form>
                                            </td>
                                            <td style="text-align: right; color: #ff4757; font-weight: bold; font-size: 16px;">
                                                <fmt:formatNumber value="${item.totalPrice}" pattern="#,###"/> ₫
                                            </td>
                                            <td style="text-align: center;">
                                                <a href="${pageContext.request.contextPath}/remove-cart?id=${item.cartItemId}" 
                                                   onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?')"
                                                   style="color: #ef4444; font-size: 18px; transition: color 0.2s;">
                                                    <i class="fa-solid fa-trash-can"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

                            <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 30px; border-top: 1px solid #334155; padding-top: 20px;">
                                <a href="${pageContext.request.contextPath}/products" style="color: #3b82f6; text-decoration: none; font-weight: 600;"><i class="fa-solid fa-arrow-left"></i> Tiếp tục mua sắm</a>

                                <div style="text-align: right; width: 400px;">

                                    <!-- PHẦN NHẬP MÃ GIẢM GIÁ -->
                                    <div style="background: #0f1117; padding: 15px; border-radius: 8px; border: 1px solid #334155; margin-bottom: 20px; text-align: left;">
                                        <h4 style="color: #e2e8f0; margin-bottom: 10px; font-size: 14px;"><i class="fa-solid fa-ticket" style="color: #3b82f6;"></i> Mã Khuyến Mãi</h4>

                                        <form action="${pageContext.request.contextPath}/apply-promo" method="POST" style="display: flex; gap: 10px;">
                                            <input type="text" name="promoCode" value="${sessionScope.appliedPromo != null ? sessionScope.appliedPromo.promoCode : ''}" placeholder="Nhập mã giảm giá..." 
                                                   style="flex: 1; padding: 10px; background: transparent; border: 1px solid #4a5568; color: #fff; border-radius: 4px; text-transform: uppercase;">
                                            <button type="submit" class="btn-submit" style="width: auto; background: #3b82f6; padding: 10px 20px;">
                                                ${sessionScope.appliedPromo != null ? 'Gỡ mã' : 'Áp dụng'}
                                            </button>
                                        </form>

                                        <c:if test="${not empty sessionScope.appliedPromo}">
                                            <p style="color: #10b981; font-size: 13px; margin-top: 10px;">
                                                <i class="fa-solid fa-circle-check"></i> Đang áp dụng mã: <strong>${sessionScope.appliedPromo.promotionName}</strong>
                                            </p>
                                        </c:if>
                                    </div>

                                    <!-- PHẦN TÍNH TIỀN -->
                                    <div style="margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px dashed #334155;">
                                        <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                                            <span style="color: #94a3b8; font-size: 15px;">Tạm tính:</span>
                                            <span style="color: #e2e8f0; font-size: 16px;"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/> ₫</span>
                                        </div>

                                        <c:if test="${discountValue > 0}">
                                            <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                                                <span style="color: #94a3b8; font-size: 15px;">Khuyến mãi:</span>
                                                <span style="color: #10b981; font-size: 16px;">- <fmt:formatNumber value="${discountValue}" pattern="#,###"/> ₫</span>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
                                        <span style="color: #e2e8f0; font-size: 18px; font-weight: bold;">Tổng thanh toán:</span>
                                        <span style="color: #ff4757; font-size: 26px; font-weight: bold;">
                                            <fmt:formatNumber value="${finalAmount != null ? finalAmount : totalAmount}" pattern="#,###"/> ₫
                                        </span>
                                    </div>

                                    <a href="${pageContext.request.contextPath}/checkout" class="btn-submit" style="display: inline-block; padding: 14px 40px; text-decoration: none; font-size: 18px; background: #10b981;">
                                        Tiến Hành Thanh Toán <i class="fa-solid fa-arrow-right"></i>
                                    </a>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <jsp:include page="includes/footer.jsp" />

        <!-- THÔNG BÁO POPUP THÀNH CÔNG / THẤT BẠI -->
        <c:if test="${not empty sessionScope.errorMsg}">
            <script>alert('${sessionScope.errorMsg}');</script>
            <c:remove var="errorMsg" scope="session" />
        </c:if>

        <c:if test="${not empty sessionScope.successMsg}">
            <script>alert('${sessionScope.successMsg}');</script>
            <c:remove var="successMsg" scope="session" />
        </c:if>
    </body>
</html>