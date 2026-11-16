<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
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

    <div class="row">
        <c:if test="${empty parts}"><div class="col-12 text-center text-muted py-5">Không có linh kiện phù hợp.</div></c:if>
<c:forEach var="p" items="${parts}">
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
</div>

<jsp:include page="/views/components/footer.jsp" />
