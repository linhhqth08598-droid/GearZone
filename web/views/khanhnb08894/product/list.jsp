<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h3 class="fw-bold mb-0"><i class="fas fa-microchip text-danger me-2"></i>Linh kiện PC</h3>
        <form action="${pageContext.request.contextPath}/product" method="GET" class="d-flex gap-2 m-0">
            <input type="text" name="q" value="<c:out value='${param.q}'/>" class="form-control" placeholder="Tìm linh kiện...">
            <button class="btn btn-danger" type="submit"><i class="fas fa-search"></i></button>
        </form>
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

    <%-- 5 thẻ / hàng trên màn hình lớn; 3 thẻ trên tablet; 2 thẻ trên điện thoại --%>
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
</div>

<jsp:include page="/views/components/footer.jsp" />
