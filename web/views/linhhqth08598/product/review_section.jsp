<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<style>
    /* CSS cho thanh trượt chấm điểm sao */
    .rating-wrapper {
        position: relative;
        display: inline-block;
        font-size: 30px;
        color: #ccc;
        cursor: pointer;
        margin-bottom: 10px;
    }
    .rating-wrapper .stars-bg {
        display: flex;
    }
    .rating-wrapper .stars-fg {
        position: absolute;
        top: 0;
        left: 0;
        white-space: nowrap;
        overflow: hidden;
        display: flex;
        color: #ffc107; /* Màu vàng */
    }
    /* Ẩn thanh trượt nhưng vẫn cho phép tương tác */
    .rating-wrapper input[type="range"] {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        opacity: 0;
        cursor: pointer;
        z-index: 2;
        margin: 0;
    }
</style>

<div class="container mt-5" id="reviews">
    <h3 class="fw-bold mb-4"><i class="fas fa-star text-warning me-2"></i>Đánh giá sản phẩm</h3>
    
    <div class="row">
        <!-- Form viết đánh giá -->
        <div class="col-md-5 mb-4">
            <div class="card shadow-sm border-0 rounded-4 p-4">
                <h5 class="fw-bold mb-3">Viết đánh giá của bạn</h5>
                <c:if test="${not empty sessionScope.loggedUser}">
                    <form action="${pageContext.request.contextPath}/review" method="POST" enctype="multipart/form-data" onsubmit="prepareRating()">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="partId" value="${part.partId}">
                        
                        <!-- Input ẩn để lưu số sao (mặc định 5) -->
                        <input type="hidden" name="qualityRating" id="selectedRating" value="5">

                        <!-- KHU VỰC CHẤM SAO (Dùng thanh trượt ẩn) -->
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Chất lượng sản phẩm</label>
                            <div class="rating-wrapper">
                                <!-- Lớp nền: 5 sao xám -->
                                <div class="stars-bg">
                                    <i class="far fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i><i class="far fa-star"></i>
                                </div>
                                <!-- Lớp phủ: 5 sao vàng (sẽ bị cắt theo giá trị) -->
                                <div class="stars-fg" id="stars-fg">
                                    <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i>
                                </div>
                                <!-- Thanh trượt: Min 1, Max 5, Step 0.5 -->
                                <input type="range" min="1" max="5" step="0.5" value="5" id="rating-slider">
                            </div>
                            <span id="rating-text" class="ms-2 fw-bold text-warning fs-5">5.0</span>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Nhận xét của bạn</label>
                            <textarea name="comment" class="form-control" rows="4" required placeholder="Chia sẻ cảm nhận..."></textarea>
                        </div>

                        <!-- CHỌN ẢNH -->
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Đính kèm ảnh (nếu có)</label>
                            <input type="file" name="reviewImage" class="form-control" accept="image/*">
                        </div>

                        <button type="submit" class="btn btn-primary w-100" style="background-color: #3b82f6;">Gửi đánh giá</button>
                    </form>
                </c:if>
                <c:if test="${empty sessionScope.loggedUser}">
                    <p class="text-muted">Vui lòng <a href="${pageContext.request.contextPath}/auth?action=login">đăng nhập</a> để viết đánh giá.</p>
                </c:if>
            </div>
        </div>

        <!-- Danh sách đánh giá -->
        <div class="col-md-7">
            <div class="card shadow-sm border-0 rounded-4 p-4">
                <h5 class="fw-bold mb-3">Khách hàng nhận xét</h5>
                <c:if test="${empty reviews}">
                    <p class="text-center text-muted py-4">Chưa có đánh giá nào cho sản phẩm này.</p>
                </c:if>
                <c:forEach var="r" items="${reviews}">
                    <div class="border-bottom pb-3 mb-3">
                        <div class="d-flex justify-content-between">
                            <strong>Người dùng #${r.userId}</strong>
                            <span class="text-muted" style="font-size: 13px;"><fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy"/></span>
                        </div>
                        
                        <!-- Hiển thị sao đánh giá (hỗ trợ nửa sao khi xem) -->
                        <div class="text-warning my-1">
                            <c:set var="rating" value="${r.qualityRating}" />
                            <c:forEach begin="1" end="5" var="i">
                                <c:choose>
                                    <c:when test="${rating >= i}"><i class="fas fa-star"></i></c:when>
                                    <c:when test="${rating >= i - 0.5}"><i class="fas fa-star-half-alt"></i></c:when>
                                    <c:otherwise><i class="far fa-star"></i></c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </div>
                        
                        <p class="mb-0 text-secondary">${r.comment}</p>
                        
                        <!-- Hiển thị ảnh nếu có -->
                        <c:if test="${not empty r.imageUrl}">
                            <div class="mt-2">
                                <img src="${r.imageUrl}" alt="Review Image" class="img-thumbnail" style="max-height: 100px; cursor: pointer;" onclick="window.open(this.src)">
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<!-- SCRIPT XỬ LÝ THANH TRƯỢT SAO -->
<script>
    document.addEventListener("DOMContentLoaded", function() {
        const slider = document.getElementById('rating-slider');
        const starsFg = document.getElementById('stars-fg');
        const ratingText = document.getElementById('rating-text');
        const hiddenRating = document.getElementById('selectedRating');

        // Hàm cập nhật giao diện khi kéo thanh trượt
        function updateStars(value) {
            // Tính toán phần trăm che phủ của lớp sao vàng
            // Mỗi ngôi sao rộng 20% (tổng 5 sao = 100%)
            // Nếu value = 4.5 thì che 90%
            const percentage = (value / 5) * 100;
            starsFg.style.width = percentage + '%';
            
            // Cập nhật text hiển thị số sao
            ratingText.innerText = parseFloat(value).toFixed(1);
            
            // Cập nhật giá trị input ẩn để gửi về Server
            hiddenRating.value = value;
        }

        // Lắng nghe sự kiện kéo thanh trượt
        slider.addEventListener('input', function() {
            updateStars(this.value);
        });

        // Khởi tạo ban đầu là 5 sao
        updateStars(5);
    });

    // Hàm chuẩn bị trước khi submit form (đảm bảo input ẩn có giá trị)
    function prepareRating() {
        const hiddenRating = document.getElementById('selectedRating');
        const slider = document.getElementById('rating-slider');
        hiddenRating.value = slider.value;
    }
</script>