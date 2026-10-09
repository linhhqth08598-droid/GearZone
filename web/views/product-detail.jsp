<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>${product.productName} - GearZone</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <jsp:include page="includes/header.jsp" />

        <div class="container" style="margin-top: 40px; margin-bottom: 60px;">
            <!-- Nút quay lại -->
            <div style="margin-bottom: 20px;">
                <a href="javascript:history.back()" style="color: #94a3b8; text-decoration: none; font-size: 15px;">
                    <i class="fa-solid fa-arrow-left"></i> Quay lại
                </a>
            </div>

            <div class="detail-layout">
                <!-- CỘT TRÁI: HÌNH ẢNH SẢN PHẨM -->
                <div class="detail-img">
                    <c:choose>
                        <c:when test="${not empty product.imageBase64}">
                            <img src="${product.imageBase64}" alt="${product.productName}" style="max-height: 350px;">
                        </c:when>
                        <c:otherwise>
                            <i class="fa-solid fa-image" style="font-size: 100px; color: #4a5568;"></i>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- CỘT PHẢI: THÔNG TIN VÀ NÚT MUA -->
                <div class="detail-info">
                    <h1 style="color: #fff; font-size: 28px; margin-bottom: 15px;">${product.productName}</h1>
                    <div class="product-price" style="font-size: 30px; margin-bottom: 20px;">
                        <fmt:setLocale value="vi_VN"/>
                        <fmt:formatNumber value="${product.basePrice}" pattern="#,###"/> ₫
                    </div>

                    <div style="margin-bottom: 25px;">
                        <p style="color: #cbd5e1; margin-bottom: 10px; font-size: 15px;">
                            <i class="fa-solid fa-box"></i> Tình trạng: 
                            <c:choose>
                                <c:when test="${product.stockQuantity > 0}">
                                    <span style="color: #10b981; font-weight: bold;">Còn hàng (${product.stockQuantity})</span>
                                </c:when>
                                <c:otherwise>
                                    <span style="color: #ef4444; font-weight: bold;">Hết hàng</span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <p style="color: #cbd5e1; font-size: 15px;">
                            <i class="fa-solid fa-shield-halved"></i> Bảo hành chính hãng 24 tháng
                        </p>
                    </div>

                    <!-- BỔ SUNG BẢNG THÔNG SỐ KỸ THUẬT VÀO ĐÂY -->
                    <div style="background: #0f1117; padding: 20px; border-radius: 8px; border: 1px solid #334155; margin-bottom: 30px;">
                        <h4 style="color: #e2e8f0; margin-bottom: 10px; border-bottom: 1px solid #2d3748; padding-bottom: 10px;">Thông số kỹ thuật</h4>
                        <table class="specs-table" style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
                            <c:forEach items="${productSpecs}" var="spec">
                                <tr style="border-bottom: 1px solid #1e2336;">
                                    <td style="padding: 10px 0; color: #94a3b8; width: 40%;">${spec.specName}</td>
                                    <td style="padding: 10px 0; color: #fff; font-weight: 500;">${spec.specValue}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty productSpecs}">
                                <tr><td colspan="2" style="padding: 10px 0; color: #94a3b8; font-style: italic;">Đang cập nhật thông số...</td></tr>
                            </c:if>
                        </table>

                        <h4 style="color: #e2e8f0; margin-bottom: 10px; border-bottom: 1px solid #2d3748; padding-bottom: 10px; margin-top: 20px;">Đặc điểm nổi bật</h4>
                        <p style="color: #94a3b8; line-height: 1.8; white-space: pre-line; font-size: 14px;">${product.description}</p>
                    </div>

                    <!-- FORM THÊM VÀO GIỎ HÀNG -->
                    <form action="${pageContext.request.contextPath}/add-to-cart" method="POST" style="display: flex; gap: 15px; align-items: center;">
                        <input type="hidden" name="id" value="${product.productId}">

                        <div style="display: flex; flex-direction: column;">
                            <label style="color: #94a3b8; font-size: 12px; margin-bottom: 5px;">Số lượng</label>
                            <input type="number" name="quantity" value="1" min="1" max="${product.stockQuantity}" 
                                   style="width: 80px; height: 48px; background: #0f1117; border: 1px solid #334155; color: #fff; border-radius: 6px; outline: none; text-align: center; font-size: 16px;">
                        </div>

                        <button type="submit" class="btn-submit" style="flex: 1; height: 48px; font-size: 16px; align-self: flex-end;" ${product.stockQuantity == 0 ? 'disabled style="background: #4a5568; cursor: not-allowed;"' : ''}>
                            <i class="fa-solid fa-cart-shopping"></i> THÊM VÀO GIỎ HÀNG
                        </button>
                    </form>

                </div>
            </div>

            <!-- KHU VỰC ĐÁNH GIÁ SẢN PHẨM -->
            <div style="background: #1a1d29; border-radius: 12px; border: 1px solid #2d3748; padding: 30px; margin-top: 35px;">
                <h3 style="color: #fff; margin-bottom: 20px; border-left: 4px solid #ff4757; padding-left: 10px; font-size: 20px;">Đánh Giá Sản Phẩm</h3>

                <!-- Form Gửi Đánh Giá -->
                <form action="${pageContext.request.contextPath}/submit-review" method="POST" style="margin-bottom: 30px; background: #0f1117; padding: 20px; border-radius: 8px; border: 1px dashed #334155;">
                    <input type="hidden" name="productId" value="${product.productId}">

                    <div style="margin-bottom: 15px;">
                        <label style="color: #cbd5e1; display: block; margin-bottom: 8px; font-weight: 600;">Chất lượng sản phẩm:</label>
                        <select name="rating" style="padding: 10px; background: #1a1d29; color: #ff4757; border: 1px solid #334155; border-radius: 4px; font-weight: bold; outline: none;">
                            <option value="5">⭐⭐⭐⭐⭐ (5 Sao - Tuyệt vời)</option>
                            <option value="4">⭐⭐⭐⭐ (4 Sao - Rất tốt)</option>
                            <option value="3">⭐⭐⭐ (3 Sao - Bình thường)</option>
                            <option value="2">⭐⭐ (2 Sao - Kém)</option>
                            <option value="1">⭐ (1 Sao - Quá tệ)</option>
                        </select>
                    </div>

                    <div style="margin-bottom: 15px;">
                        <textarea name="comment" rows="3" required placeholder="Nhập nhận xét của bạn về sản phẩm này..." style="width: 100%; padding: 12px; background: #1a1d29; color: #fff; border: 1px solid #334155; border-radius: 4px; outline: none;"></textarea>
                    </div>

                    <button type="submit" style="background: #3b82f6; color: #fff; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">
                        Gửi Đánh Giá
                    </button>
                </form>

                <!-- Danh sách các đánh giá cũ -->
                <div>
                    <c:forEach items="${productReviews}" var="rv">
                        <div style="border-bottom: 1px solid #2d3748; padding-bottom: 15px; margin-bottom: 15px;">
                            <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                                <strong style="color: #3b82f6;"><i class="fa-solid fa-circle-user"></i> ${rv.customerName}</strong>
                                <span style="color: #94a3b8; font-size: 13px;"><fmt:formatDate value="${rv.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span>
                            </div>
                            <div style="color: #ff4757; margin-bottom: 5px; font-size: 14px;">
                                <c:forEach begin="1" end="${rv.rating}">⭐</c:forEach>
                                </div>
                                <p style="color: #e2e8f0; font-size: 14px; line-height: 1.5;">${rv.comment}</p>
                        </div>
                    </c:forEach>
                    <c:if test="${empty productReviews}">
                        <p style="color: #94a3b8; font-style: italic;">Chưa có đánh giá nào cho sản phẩm này. Hãy trở thành người đầu tiên đánh giá!</p>
                    </c:if>
                </div>
            </div>

        </div>

        <jsp:include page="includes/footer.jsp" />

        <!-- POPUP THÔNG BÁO -->
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