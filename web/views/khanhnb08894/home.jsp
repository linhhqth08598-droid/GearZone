<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<style>
    .carousel-item img {
        height: 500px;
        object-fit: cover;
    }
    .carousel-caption {
        background: rgba(0,0,0,0.5);
        padding: 20px;
        border-radius: 12px;
    }
</style>

<!-- SLIDESHOW -->
<div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
    <div class="carousel-indicators">
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="0" class="active"></button>
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="1"></button>
        <button type="button" data-bs-target="#heroCarousel" data-bs-slide-to="2"></button>
    </div>
    <div class="carousel-inner">
        <div class="carousel-item active">
            <img src="https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=2070&auto=format&fit=crop" class="d-block w-100" alt="Banner 1">
            <div class="carousel-caption d-none d-md-block">
                <h2 class="fw-bold">Chào mừng đến với GearZone</h2>
                <p>Chuyên cung cấp linh kiện PC & Phụ kiện Gaming chính hãng.</p>
            </div>
        </div>
        <div class="carousel-item">
            <img src="https://images.unsplash.com/photo-1593640408182-31c70c8268f5?q=80&w=2042&auto=format&fit=crop" class="d-block w-100" alt="Banner 2">
            <div class="carousel-caption d-none d-md-block">
                <h2 class="fw-bold">Build PC theo yêu cầu</h2>
                <p>Đội ngũ kỹ thuật viên giàu kinh nghiệm.</p>
            </div>
        </div>
        <div class="carousel-item">
            <img src="https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=2070&auto=format&fit=crop" class="d-block w-100" alt="Banner 3">
            <div class="carousel-caption d-none d-md-block">
                <h2 class="fw-bold">Khuyến mãi cực sốc</h2>
                <p>Hàng ngàn ưu đãi hấp dẫn đang chờ bạn.</p>
            </div>
        </div>
    </div>
    <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev"><span class="carousel-control-prev-icon"></span></button>
    <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next"><span class="carousel-control-next-icon"></span></button>
</div>

<!-- SẢN PHẨM NỔI BẬT -->
<div class="container my-5">
    <h3 class="fw-bold mb-4"><i class="fas fa-fire text-danger me-2"></i>Sản phẩm nổi bật</h3>
    <c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/>
        <a href="${pageContext.request.contextPath}/cart" class="alert-link ms-2">Xem giỏ hàng</a>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <c:remove var="message" scope="session"/>
</c:if>
    <c:if test="${not empty sessionScope.error}"><div class="alert alert-danger"><c:out value="${sessionScope.error}"/></div><c:remove var="error" scope="session"/></c:if>
    <div class="row">
        <c:if test="${empty featured}"><div class="col-12 text-center text-muted py-5"><p>Chưa có sản phẩm nào đang bán.</p></div></c:if>
<c:forEach var="p" items="${featured}">
    <div class="col-6 col-md-3 mb-4">
        <div class="card h-100 border-0 shadow-sm rounded-4">
            <a href="${pageContext.request.contextPath}/product?action=detail&id=${p.partId}" class="text-decoration-none text-dark">
                <div class="card-body text-center p-4">
                    <i class="fas fa-microchip fa-3x text-secondary mb-3"></i>
                    <h6 class="fw-bold" style="min-height:40px"><c:out value="${p.partName}"/></h6>
                    <div class="text-danger fw-bold">
                        <fmt:formatNumber value="${not empty p.salePrice and p.salePrice > 0 ? p.salePrice : p.basePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </div>
                </div>
            </a>
            <div class="card-footer bg-white border-0 pb-3">
                <form action="${pageContext.request.contextPath}/cart" method="POST" class="m-0">
                    <input type="hidden" name="action" value="add"><input type="hidden" name="partId" value="${p.partId}">
                    <button type="submit" class="btn btn-danger btn-sm w-100"><i class="fas fa-cart-plus me-1"></i> Thêm vào giỏ</button>
                </form>
            </div>
        </div>
    </div>
</c:forEach>
    </div>
    <div class="text-center"><a href="${pageContext.request.contextPath}/product" class="btn btn-outline-danger">Xem tất cả linh kiện</a></div>
</div>

<!-- KHUYẾN MÃI -->
<div class="container my-5">
    <h3 class="fw-bold mb-4"><i class="fas fa-tags text-warning me-2"></i>Khuyến mãi hấp dẫn</h3>
    <div class="row">
        <div class="col-12 text-center text-muted py-5">
            <p>Các chương trình khuyến mãi sẽ được hiển thị tại đây.</p>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />