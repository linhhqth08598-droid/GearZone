<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container my-5">
    <div class="row">
        <!-- Ảnh sản phẩm -->
        <div class="col-md-6 mb-4">
            <div class="card shadow-sm border-0 rounded-4 position-relative">
                <div class="card-body p-5 text-center">
                    <img src="https://via.placeholder.com/500x400?text=GearZone+Product" alt="${part.partName}" class="img-fluid rounded">
                </div>
                
                <!-- NÚT YÊU THÍCH -->
                <button type="button" id="wishlistBtn" onclick="toggleWishlist(${part.partId})" 
                        class="btn btn-light rounded-circle shadow-sm position-absolute" 
                        style="top: 15px; right: 15px; width: 45px; height: 45px; color: ${isWishlisted ? '#ff4757' : '#ccc'};">
                    <i class="fas fa-heart fs-5"></i>
                </button>
            </div>
        </div>
        
        <!-- Thông tin sản phẩm -->
        <div class="col-md-6">
            <h1 class="fw-bold mb-3">${part.partName}</h1>
            <p class="text-muted mb-4">Mã sản phẩm: <code>${part.productCode}</code></p>
            
            <h3 class="text-danger fw-bold mb-4">
                <fmt:formatNumber value="${part.salePrice != null ? part.salePrice : part.basePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
            </h3>
            
            <div class="mb-4 p-3 bg-light rounded-3">
                <p class="mb-2"><strong><i class="fas fa-shield-alt text-success me-2"></i> Bảo hành:</strong> ${part.warrantyMonths} tháng</p>
                <p class="mb-0"><strong><i class="fas fa-info-circle text-primary me-2"></i> Trạng thái:</strong> 
                    <c:choose>
                        <c:when test="${part.status == 'ACTIVE'}"><span class="text-success">Còn hàng</span></c:when>
                        <c:otherwise><span class="text-danger">Hết hàng</span></c:otherwise>
                    </c:choose>
                </p>
            </div>
            
            <div class="mb-5">
                <h5 class="fw-bold">Mô tả sản phẩm:</h5>
                <p class="text-secondary">${part.description != null ? part.description : 'Đang cập nhật mô tả...'}</p>
            </div>
            
            <div class="d-flex gap-3">
                <button class="btn btn-primary btn-lg flex-grow-1" style="background-color: #ff4757; border: none;">
                    <i class="fas fa-cart-plus me-2"></i> Thêm vào giỏ hàng
                </button>
                
                <!-- NÚT THEO DÕI GIÁ -->
                <button type="button" id="priceAlertBtn" onclick="togglePriceAlert(${part.partId})" 
                        class="btn btn-outline-warning btn-lg ${isPriceAlerted ? 'active' : ''}" 
                        data-bs-toggle="${isPriceAlerted ? '' : 'modal'}" 
                        data-bs-target="#priceAlertModal">
                    <i class="fas fa-bell"></i> <span id="priceAlertText">${isPriceAlerted ? 'Hủy theo dõi' : 'Theo dõi giá'}</span>
                </button>
            </div>
        </div>
    </div>
</div>

<!-- MODAL THEO DÕI GIÁ -->
<div class="modal fade" id="priceAlertModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/user/price-alert" method="POST">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-bell me-2"></i>Theo dõi giá sản phẩm</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <input type="hidden" name="action" value="register">
                    <input type="hidden" name="partId" value="${part.partId}">
                    <p>Sản phẩm: <b class="text-dark">${part.partName}</b></p>
                    <p>Giá hiện tại: <b class="text-danger"><fmt:formatNumber value="${part.salePrice != null ? part.salePrice : part.basePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></b></p>
                    
                    <div class="input-group mb-3">
                        <span class="input-group-text bg-light"><i class="fas fa-tag text-warning"></i></span>
                        <input type="number" class="form-control" name="targetPrice" required min="1000" step="1000" placeholder="Nhập số tiền bạn muốn mua...">
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4">Đăng ký</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- PHẦN ĐÁNH GIÁ -->
<jsp:include page="/views/linhhqth08598/product/review_section.jsp" />

<!-- SCRIPT AJAX -->
<script>
    function toggleWishlist(partId) {
        fetch('${pageContext.request.contextPath}/user/wishlist?action=toggle&partId=' + partId, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    const btn = document.getElementById('wishlistBtn');
                    btn.style.color = data.isWishlisted ? '#ff4757' : '#ccc';
                }
            });
    }

    function togglePriceAlert(partId) {
        const btn = document.getElementById('priceAlertBtn');
        if (btn.classList.contains('active')) {
            // Huỷ theo dõi
            fetch('${pageContext.request.contextPath}/user/price-alert?action=cancel&partId=' + partId, { method: 'POST' })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        btn.classList.remove('active');
                        document.getElementById('priceAlertText').innerText = 'Theo dõi giá';
                    }
                });
        } else {
            // Mở modal để nhập giá (Bootstrap sẽ tự lo)
        }
    }
</script>

<jsp:include page="/views/components/footer.jsp" />