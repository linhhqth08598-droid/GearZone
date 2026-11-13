<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container my-5" style="min-height: 60vh;">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0"><i class="fas fa-heart text-danger me-2"></i>Sản phẩm yêu thích</h3>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i> Tiếp tục mua sắm
        </a>
    </div>

    <!-- Hiển thị thông báo -->
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle me-1"></i> ${sessionScope.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${empty wishlist}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <img src="https://cdn-icons-png.flaticon.com/512/11329/11329060.png" alt="Empty Wishlist" style="width: 120px; opacity: 0.5;" class="mb-3">
                <h5 class="text-muted">Danh sách yêu thích của bạn đang trống.</h5>
                <p class="text-muted mb-4">Hãy dạo quanh cửa hàng và thêm những linh kiện bạn thích nhé!</p>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary px-4 py-2" style="border-radius: 8px;">Khám phá ngay</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <c:forEach var="item" items="${wishlist}">
                    <div class="col-md-3 col-sm-6">
                        <div class="card h-100 shadow-sm border-0 rounded-4 product-card">
                            <!-- Ảnh sản phẩm -->
                            <div class="p-3 text-center bg-light rounded-top-4" style="height: 200px; display: flex; align-items: center; justify-content: center;">
                                <c:choose>
                                    <c:when test="${not empty item.primaryImageUrl}">
                                        <img src="${item.primaryImageUrl}" alt="${item.partName}" class="img-fluid" style="max-height: 100%; object-fit: contain;">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="https://via.placeholder.com/200x150?text=No+Image" alt="No Image" class="img-fluid rounded">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <!-- Nội dung -->
                            <div class="card-body d-flex flex-column p-3">
                                <small class="text-muted mb-1">Mã: ${item.productCode}</small>
                                <h6 class="card-title fw-bold text-dark mb-2" style="height: 40px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;">
                                    ${item.partName}
                                </h6>
                                
                                <div class="mt-auto">
                                    <h5 class="text-danger fw-bold mb-3">
                                        <fmt:formatNumber value="${item.salePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </h5>
                                    
                                    <div class="d-flex gap-2">
                                        <a href="${pageContext.request.contextPath}/product/detail?id=${item.partId}" class="btn btn-outline-primary btn-sm flex-grow-1">
                                            <i class="fas fa-eye"></i> Xem
                                        </a>
                                        
                                        <!-- Form xóa khỏi wishlist -->
                                        <form action="${pageContext.request.contextPath}/user/wishlist" method="POST" class="d-inline">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="partId" value="${item.partId}">
                                            <button type="submit" class="btn btn-outline-danger btn-sm" onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này khỏi danh sách yêu thích?');" title="Xóa khỏi yêu thích">
                                                <i class="fas fa-trash-alt"></i>
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- CSS tùy chỉnh nhỏ cho thẻ sản phẩm -->
<style>
    .product-card {
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }
    .product-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
    }
</style>

<jsp:include page="/views/components/footer.jsp" />