<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<c:set var="sidebarFlush" value="true" scope="request" />
<jsp:include page="/views/components/sidebar.jsp" />

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
        <div class="alert alert-success alert-dismissible fade show shadow-lg"
             style="position: fixed; top: 85px; right: 20px; z-index: 9999; min-width: 300px;">
            <i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/>
            <a href="${pageContext.request.contextPath}/cart" class="alert-link ms-2">Xem giỏ hàng</a>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
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
                            <div class="d-flex align-items-center justify-content-center mb-3" style="height:150px;">
                                <c:choose>
                                    <c:when test="${not empty p.imageUrl}"><img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.partName}'/>" style="max-width:100%; max-height:150px; object-fit:contain;"></c:when>
                                    <c:otherwise><i class="fas fa-microchip fa-3x text-secondary"></i></c:otherwise>
                                </c:choose>
                            </div>
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
    <c:if test="${empty homePromos and empty homeCoupons}">
        <div class="text-center text-muted py-5"><p>Hiện chưa có chương trình khuyến mãi nào.</p></div>
    </c:if>
    <div class="row">
        <c:forEach var="c" items="${homeCoupons}">
            <div class="col-md-4 mb-3">
                <div class="card h-100 border-0 shadow-sm rounded-4 border-start border-4 border-warning">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <span class="badge bg-warning text-dark fs-6"><c:out value="${c.code}"/></span>
                            <c:if test="${not empty c.endDate}"><small class="text-muted">HSD: <fmt:formatDate value="${c.endDate}" pattern="dd/MM/yyyy"/></small></c:if>
                            </div>
                            <h6 class="fw-bold mt-2 mb-1"><c:out value="${c.name}"/></h6>
                        <div class="text-danger fw-bold">
                            <c:choose>
                                <c:when test="${c.type == 'PERCENT'}"><fmt:formatNumber value="${c.value}" maxFractionDigits="0"/>%<c:if test="${c.maxDiscount > 0}"> (tối đa <fmt:formatNumber value="${c.maxDiscount}" maxFractionDigits="0"/> ₫)</c:if></c:when>
                                <c:when test="${c.type == 'FREE_SHIP'}">Miễn phí vận chuyển</c:when>
                                <c:otherwise>Giảm <fmt:formatNumber value="${c.value}" maxFractionDigits="0"/> ₫</c:otherwise>
                            </c:choose>
                        </div>
                        <c:if test="${c.minOrder > 0}"><small class="text-muted">Đơn từ <fmt:formatNumber value="${c.minOrder}" maxFractionDigits="0"/> ₫</small></c:if>
                        </div>
                    </div>
                </div>
        </c:forEach>
        <c:forEach var="p" items="${homePromos}">
            <div class="col-md-4 mb-3">
                <div class="card h-100 border-0 shadow-sm rounded-4 border-start border-4 border-danger">
                    <div class="card-body">
                        <h6 class="fw-bold mb-1"><i class="fas fa-fire text-danger me-1"></i><c:out value="${p.name}"/></h6>
                        <c:if test="${not empty p.targetName}"><div class="small text-muted">Áp dụng: <c:out value="${p.targetName}"/></div></c:if>
                            <div class="text-danger fw-bold">
                                Giảm <c:choose>
                                <c:when test="${p.discountType == 'PERCENT'}"><fmt:formatNumber value="${p.value}" maxFractionDigits="0"/>%</c:when>
                                <c:otherwise><fmt:formatNumber value="${p.value}" maxFractionDigits="0"/> ₫</c:otherwise>
                            </c:choose>
                        </div>
                        <c:if test="${not empty p.endDate}"><small class="text-muted">Đến <fmt:formatDate value="${p.endDate}" pattern="dd/MM/yyyy"/></small></c:if>
                        </div>
                    </div>
                </div>
        </c:forEach>
    </div>
    <div class="text-center"><a href="${pageContext.request.contextPath}/promotion" class="btn btn-outline-warning">Xem tất cả khuyến mãi</a></div>
</div>

<jsp:include page="/views/components/footer.jsp" />