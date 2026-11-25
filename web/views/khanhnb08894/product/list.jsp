<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <div>
            <h3 class="fw-bold mb-0"><i class="fas fa-microchip text-danger me-2"></i>Linh kiện PC</h3>
            <small class="text-muted">Tìm thấy <strong><c:out value="${totalFiltered}"/></strong> sản phẩm · Trang <c:out value="${currentPage}"/> / <c:out value="${totalPages}"/></small>
        </div>
        <a href="${pageContext.request.contextPath}/compare" class="btn btn-outline-danger btn-sm">
            <i class="fas fa-balance-scale me-1"></i> So sánh linh kiện
        </a>
    </div>

    <div class="card shadow-sm border-0 rounded-4 mb-4">
        <div class="card-body p-3 p-md-4">
            <form action="${pageContext.request.contextPath}/product" method="GET" class="row g-3 align-items-end">
                <div class="col-md-3">
                    <label class="form-label fw-semibold">Từ khóa</label>
                    <input type="text" name="q" value="<c:out value='${param.q}'/>" class="form-control" placeholder="Tên hoặc mã SKU...">
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-semibold">Danh mục</label>
                    <select name="categoryId" class="form-select">
                        <option value="">Tất cả danh mục</option>
                        <c:forEach var="c" items="${categories}">
                            <option value="${c.categoryId}" ${param.categoryId == c.categoryId ? 'selected' : ''}><c:out value="${c.categoryName}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-semibold">Thương hiệu</label>
                    <select name="brandId" class="form-select">
                        <option value="">Tất cả thương hiệu</option>
                        <c:forEach var="b" items="${brands}">
                            <option value="${b.brandId}" ${param.brandId == b.brandId ? 'selected' : ''}><c:out value="${b.brandName}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-semibold">Sắp xếp</label>
                    <select name="sort" class="form-select">
                        <option value="" ${empty param.sort ? 'selected' : ''}>Mới nhất</option>
                        <option value="price_asc" ${param.sort == 'price_asc' ? 'selected' : ''}>Giá tăng dần</option>
                        <option value="price_desc" ${param.sort == 'price_desc' ? 'selected' : ''}>Giá giảm dần</option>
                        <option value="perf_desc" ${param.sort == 'perf_desc' ? 'selected' : ''}>Hiệu năng cao nhất</option>
                        <option value="popular" ${param.sort == 'popular' ? 'selected' : ''}>Phổ biến nhất</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label fw-semibold">Giá từ (₫)</label>
                    <input type="number" name="minPrice" value="<c:out value='${param.minPrice}'/>" class="form-control" min="0" step="100000" placeholder="0">
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-semibold">Giá đến (₫)</label>
                    <input type="number" name="maxPrice" value="<c:out value='${param.maxPrice}'/>" class="form-control" min="0" step="100000" placeholder="Không giới hạn">
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-semibold">Socket</label>
                    <input type="text" name="socket" value="<c:out value='${param.socket}'/>" class="form-control" placeholder="LGA1700, AM5...">
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-semibold">Chipset / GPU</label>
                    <input type="text" name="chipset" value="<c:out value='${param.chipset}'/>" class="form-control" placeholder="B760, RTX 4060...">
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-semibold">Chuẩn RAM</label>
                    <select name="ramDdr" class="form-select">
                        <option value="">Tất cả</option>
                        <option value="DDR4" ${param.ramDdr == 'DDR4' ? 'selected' : ''}>DDR4</option>
                        <option value="DDR5" ${param.ramDdr == 'DDR5' ? 'selected' : ''}>DDR5</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-semibold">Đánh giá tối thiểu</label>
                    <select name="minRating" class="form-select">
                        <option value="">Tất cả</option>
                        <option value="4" ${param.minRating == '4' ? 'selected' : ''}>Từ 4.0 sao</option>
                        <option value="4.5" ${param.minRating == '4.5' ? 'selected' : ''}>Từ 4.5 sao</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label fw-semibold">Dung lượng RAM (GB)</label>
                    <input type="number" name="ramGb" value="<c:out value='${param.ramGb}'/>" class="form-control" min="1" max="256" placeholder="VD: 16">
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-semibold">VRAM tối thiểu (GB)</label>
                    <input type="number" name="vramGb" value="<c:out value='${param.vramGb}'/>" class="form-control" min="1" max="64" placeholder="VD: 8">
                </div>
                <div class="col-md-4 d-flex align-items-center gap-4 pb-1">
                    <div class="form-check m-0">
                        <input class="form-check-input" type="checkbox" name="inStock" value="1" id="inStockChk" ${param.inStock == '1' || param.inStock == 'true' ? 'checked' : ''}>
                        <label class="form-check-label" for="inStockChk">Chỉ còn hàng</label>
                    </div>
                    <div class="form-check m-0">
                        <input class="form-check-input" type="checkbox" name="saleOnly" value="1" id="saleOnlyChk" ${param.saleOnly == '1' || param.saleOnly == 'true' ? 'checked' : ''}>
                        <label class="form-check-label" for="saleOnlyChk">Đang khuyến mãi</label>
                    </div>
                </div>
                <div class="col-md-4 d-flex gap-2 justify-content-md-end">
                    <button class="btn btn-danger" type="submit"><i class="fas fa-filter me-1"></i> Lọc sản phẩm</button>
                    <a href="${pageContext.request.contextPath}/product" class="btn btn-outline-secondary">Đặt lại</a>
                </div>
            </form>
        </div>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/>
            <a href="${pageContext.request.contextPath}/cart" class="alert-link ms-2">Xem giỏ hàng</a>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger"><c:out value="${sessionScope.error}"/></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="row row-cols-2 row-cols-sm-3 row-cols-lg-5 g-3">
        <c:if test="${empty parts}"><div class="col-12 text-center text-muted py-5">Không có linh kiện phù hợp.</div></c:if>
        <c:forEach var="p" items="${parts}">
            <div class="col">
                <div class="pcard">
                    <a href="${pageContext.request.contextPath}/product?action=detail&id=${p.partId}" class="pcard-link">
                        <div class="pcard-img">
                            <c:choose>
                                <c:when test="${not empty p.imageUrl}"><img src="<c:out value='${p.imageUrl}'/>" alt="<c:out value='${p.partName}'/>" loading="lazy"></c:when>
                                <c:otherwise><i class="fas fa-microchip fa-3x text-secondary"></i></c:otherwise>
                            </c:choose>
                        </div>
                        <h6 class="pcard-name"><c:out value="${p.partName}"/></h6>
                        <p class="pcard-spec"><c:out value="${specSummaries[p.partId]}"/></p>
                        <span class="pcard-price">
                            <fmt:formatNumber value="${not empty p.salePrice and p.salePrice > 0 ? p.salePrice : p.basePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                        </span>
                        <span class="pcard-old">
                            <c:if test="${not empty p.salePrice and p.salePrice > 0 and p.salePrice < p.basePrice}"><fmt:formatNumber value="${p.basePrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></c:if>
                        </span>
                    </a>
                    <div class="pcard-foot">
                        <form action="${pageContext.request.contextPath}/cart" method="POST" class="m-0">
                            <input type="hidden" name="action" value="add"><input type="hidden" name="partId" value="${p.partId}">
                            <button type="submit" class="btn btn-danger btn-sm w-100"><i class="fas fa-cart-plus"></i> Thêm vào giỏ</button>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <c:if test="${totalPages > 1}">
        <nav class="mt-4 d-flex justify-content-center" aria-label="Phân trang linh kiện">
            <ul class="pagination mb-0">
                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                    <a class="page-link" href="${paginationBaseUrl}${currentPage - 1}">Trước</a>
                </li>
                <c:forEach var="pg" begin="1" end="${totalPages}">
                    <li class="page-item ${pg == currentPage ? 'active' : ''}">
                        <a class="page-link" href="${paginationBaseUrl}${pg}"><c:out value="${pg}"/></a>
                    </li>
                </c:forEach>
                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="${paginationBaseUrl}${currentPage + 1}">Sau</a>
                </li>
            </ul>
        </nav>
    </c:if>
</div>

<jsp:include page="/views/components/footer.jsp" />
