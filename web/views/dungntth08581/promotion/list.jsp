<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div>
    <h3 class="fw-bold mb-4"><i class="fas fa-tags text-danger me-2"></i>Khuyến mãi đang diễn ra</h3>

    <c:choose>
        <c:when test="${empty promotions}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm"><p class="text-muted mb-0">Hiện chưa có chương trình khuyến mãi nào.</p></div>
        </c:when>
        <c:otherwise>
            <div class="row g-3 mb-4">
                <c:forEach var="p" items="${promotions}">
                    <div class="col-md-4"><div class="card border-0 shadow-sm rounded-4 h-100"><div class="card-body p-3">
                        <div class="fw-bold mb-1"><c:out value="${p.name}"/></div>
                        <div class="text-muted small mb-2">Áp dụng: <c:out value="${p.targetName}"/></div>
                        <div class="fs-4 fw-bold text-danger">
                            <c:choose><c:when test="${p.discountType == 'PERCENT'}">-${p.value}%</c:when>
                                <c:otherwise>-<fmt:formatNumber value="${p.value}" maxFractionDigits="0"/> ₫</c:otherwise></c:choose>
                        </div>
                        <c:if test="${p.minOrder > 0}"><div class="small text-muted">Đơn từ <fmt:formatNumber value="${p.minOrder}" maxFractionDigits="0"/> ₫</div></c:if>
                        <c:if test="${not empty p.endDate}"><div class="small text-muted">Đến hết <fmt:formatDate value="${p.endDate}" pattern="dd/MM/yyyy"/></div></c:if>
                    </div></div></div>
                </c:forEach>
            </div>

            <c:if test="${not empty parts}">
                <h5 class="fw-bold mb-3">Linh kiện đang được giảm giá</h5>
                <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3"><div class="table-responsive">
                    <table class="table align-middle mb-0">
                        <thead class="table-light"><tr><th>Linh kiện</th><th>Chương trình</th><th class="text-end">Giá gốc</th><th class="text-end">Giá sau giảm</th></tr></thead>
                        <tbody>
                            <c:forEach var="x" items="${parts}">
                                <tr><td><c:out value="${x.partName}"/></td><td class="small text-muted"><c:out value="${x.promotionName}"/></td>
                                    <td class="text-end text-decoration-line-through text-muted"><fmt:formatNumber value="${x.price}" maxFractionDigits="0"/> ₫</td>
                                    <td class="text-end fw-bold text-danger"><fmt:formatNumber value="${x.salePrice}" maxFractionDigits="0"/> ₫</td></tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div></div></div>
            </c:if>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/views/components/footer.jsp" />
