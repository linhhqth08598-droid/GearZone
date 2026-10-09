<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thanh Toán - GearZone</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <style>
            .checkout-container {
                display: flex;
                gap: 30px;
                margin-top: 20px;
            }
            .checkout-left {
                flex: 1.5;
                background: #1a1d29;
                padding: 30px;
                border-radius: 12px;
                border: 1px solid #2d3748;
            }
            .checkout-right {
                flex: 1;
                background: #1a1d29;
                padding: 30px;
                border-radius: 12px;
                border: 1px solid #2d3748;
                height: fit-content;
            }

            .payment-method {
                display: flex;
                align-items: center;
                padding: 15px;
                border: 1px solid #334155;
                border-radius: 8px;
                margin-bottom: 10px;
                cursor: pointer;
                transition: 0.2s;
            }
            .payment-method:hover {
                border-color: #3b82f6;
                background: rgba(59, 130, 246, 0.05);
            }
            .payment-method input[type="radio"] {
                margin-right: 15px;
                transform: scale(1.3);
                accent-color: #ff4757;
            }
            .payment-method img {
                height: 30px;
                margin-left: auto;
            }

            .order-item {
                display: flex;
                justify-content: space-between;
                margin-bottom: 15px;
                border-bottom: 1px solid #2d3748;
                padding-bottom: 15px;
            }
            .order-item-img {
                width: 50px;
                height: 50px;
                object-fit: contain;
                background: #242938;
                border-radius: 4px;
                margin-right: 15px;
            }
        </style>
    </head>
    <body>
        <jsp:include page="includes/header.jsp" />

        <div class="container" style="max-width: 1200px;">
            <h2 class="section-title" style="margin-top: 20px;"><i class="fa-solid fa-credit-card" style="color: #ff4757;"></i> Thanh Toán Đơn Hàng</h2>

            <form action="${pageContext.request.contextPath}/place-order" method="POST" class="checkout-container">
                <!-- Cột trái: Thông tin giao hàng -->
                <div class="checkout-left">
                    <h3 style="color: #fff; margin-bottom: 20px; font-size: 18px;">Thông Tin Nhận Hàng</h3>

                    <div style="display: flex; gap: 15px;">
                        <div class="form-group" style="flex: 1;">
                            <label>Người nhận <span style="color:#ff4757;">*</span></label>
                            <input type="text" name="shippingName" required value="${sessionScope.userAcc.fullName}" placeholder="Nhập họ tên...">
                        </div>
                        <div class="form-group" style="flex: 1;">
                            <label>Số điện thoại <span style="color:#ff4757;">*</span></label>
                            <input type="text" name="shippingPhone" required value="${sessionScope.userAcc.phoneNumber}" placeholder="Nhập số điện thoại...">
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Địa chỉ giao hàng chi tiết <span style="color:#ff4757;">*</span></label>
                        <textarea name="shippingAddress" rows="3" required placeholder="Số nhà, Tên đường, Phường/Xã, Quận/Huyện, Tỉnh/Thành phố..."></textarea>
                    </div>

                    <h3 style="color: #fff; margin-top: 30px; margin-bottom: 20px; font-size: 18px;">Phương Thức Thanh Toán</h3>

                    <label class="payment-method">
                        <input type="radio" name="paymentMethod" value="COD" checked>
                        <span style="color: #e2e8f0; font-weight: 500;">Thanh toán khi nhận hàng (COD)</span>
                        <i class="fa-solid fa-truck-fast" style="color: #10b981; margin-left: auto; font-size: 24px;"></i>
                    </label>

                    <label class="payment-method">
                        <input type="radio" name="paymentMethod" value="VNPAY">
                        <span style="color: #e2e8f0; font-weight: 500;">Thanh toán qua VNPAY / Thẻ Ngân hàng</span>
                        <i class="fa-solid fa-qrcode" style="color: #3b82f6; margin-left: auto; font-size: 24px;"></i>
                    </label>
                </div>

                <!-- Cột phải: Tóm tắt đơn hàng -->
                <div class="checkout-right">
                    <h3 style="color: #fff; margin-bottom: 20px; font-size: 18px; padding-bottom: 10px; border-bottom: 1px solid #334155;">Tóm Tắt Đơn Hàng</h3>

                    <div style="max-height: 300px; overflow-y: auto; margin-bottom: 20px; padding-right: 5px;">
                        <fmt:setLocale value="vi_VN"/>
                        <c:forEach items="${cartItems}" var="item">
                            <div class="order-item">
                                <div style="display: flex; flex: 1;">
                                    <c:choose>
                                        <c:when test="${not empty item.imageBase64}"><img src="${item.imageBase64}" class="order-item-img"></c:when>
                                        <c:otherwise><div class="order-item-img" style="display:flex;align-items:center;justify-content:center;"><i class="fa-solid fa-box"></i></div></c:otherwise>
                                        </c:choose>
                                    <div>
                                        <div style="color: #e2e8f0; font-size: 14px; font-weight: 500; margin-bottom: 5px;">${item.productName}</div>
                                        <div style="color: #94a3b8; font-size: 13px;">SL: <b style="color: #fff;">${item.quantity}</b></div>
                                    </div>
                                </div>
                                <div style="color: #ff4757; font-weight: bold; font-size: 15px;">
                                    <fmt:formatNumber value="${item.totalPrice}" pattern="#,###"/> ₫
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Tính tiền -->
                    <div style="margin-bottom: 15px;">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                            <span style="color: #94a3b8;">Tạm tính</span>
                            <span style="color: #e2e8f0;"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/> ₫</span>
                        </div>
                        <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                            <span style="color: #94a3b8;">Phí vận chuyển</span>
                            <span style="color: #10b981;">Miễn phí</span>
                        </div>
                        <c:if test="${discountValue > 0}">
                            <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                                <span style="color: #94a3b8;">Khuyến mãi</span>
                                <span style="color: #10b981;">- <fmt:formatNumber value="${discountValue}" pattern="#,###"/> ₫</span>
                            </div>
                        </c:if>
                    </div>

                    <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px dashed #334155; padding-top: 15px; margin-bottom: 25px;">
                        <span style="color: #e2e8f0; font-size: 16px; font-weight: bold;">TỔNG CỘNG</span>
                        <span style="color: #ff4757; font-size: 24px; font-weight: bold;"><fmt:formatNumber value="${finalAmount}" pattern="#,###"/> ₫</span>
                    </div>

                    <button type="submit" class="btn-submit" style="font-size: 18px; padding: 15px; background: #ff4757;">
                        ĐẶT HÀNG NGAY <i class="fa-solid fa-bag-shopping" style="margin-left: 5px;"></i>
                    </button>
                </div>
            </form>
        </div>

        <jsp:include page="includes/footer.jsp" />

        <c:if test="${not empty sessionScope.errorMsg}">
            <script>alert('${sessionScope.errorMsg}');</script>
            <c:remove var="errorMsg" scope="session" />
        </c:if>
    </body>
</html>