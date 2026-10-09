<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
                <div class="product-price" style="font-size: 30px; margin-bottom: 20px;">${product.basePrice} ₫</div>
                
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

                <div style="background: #0f1117; padding: 20px; border-radius: 8px; border: 1px solid #334155; margin-bottom: 30px;">
                    <h4 style="color: #e2e8f0; margin-bottom: 10px; border-bottom: 1px solid #2d3748; padding-bottom: 10px;">Thông số & Mô tả</h4>
                    <p style="color: #94a3b8; line-height: 1.8; white-space: pre-line; font-size: 14px;">${product.description}</p>
                </div>

                <!-- FORM THÊM VÀO GIỎ HÀNG -->
                <form action="${pageContext.request.contextPath}/add-to-cart" method="POST" style="display: flex; gap: 15px; align-items: center;">
                    <input type="hidden" name="productId" value="${product.productId}">
                    
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
    </div>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>