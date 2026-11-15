<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div>
    <h3 class="fw-bold mb-4"><i class="fas fa-shopping-cart text-primary me-2"></i>Giỏ hàng</h3>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${empty cartLines}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <p class="text-muted mb-3">Giỏ hàng đang trống.</p>
                <a href="${pageContext.request.contextPath}/build-pc" class="btn btn-primary">Xây dựng cấu hình PC</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead class="table-light"><tr><th>Linh kiện</th><th class="text-end">Đơn giá</th><th style="width:170px">Số lượng</th><th class="text-end">Thành tiền</th><th></th></tr></thead>
                        <tbody>
                            <c:forEach var="l" items="${cartLines}">
                                <tr>
                                    <td>
                                        <c:out value="${l.partName}"/>
                                        <c:if test="${l.shortage}"><div class="small text-danger"><i class="fas fa-exclamation-triangle me-1"></i>Chỉ còn ${l.available} trong kho</div></c:if>
                                    </td>
                                    <td class="text-end"><fmt:formatNumber value="${l.unitPrice}" type="number" maxFractionDigits="0"/> ₫</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart" method="POST" class="d-flex gap-1 m-0">
                                            <input type="hidden" name="action" value="update">
                                            <input type="hidden" name="cartId" value="${l.cartId}">
                                            <input type="number" name="quantity" value="${l.quantity}" min="1" max="99" class="form-control form-control-sm" style="width:70px">
                                            <button type="submit" class="btn btn-sm btn-outline-primary">Cập nhật</button>
                                        </form>
                                    </td>
                                    <td class="text-end fw-semibold"><fmt:formatNumber value="${l.lineTotal}" type="number" maxFractionDigits="0"/> ₫</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/cart" method="POST" class="m-0">
                                            <input type="hidden" name="action" value="remove">
                                            <input type="hidden" name="cartId" value="${l.cartId}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa"><i class="fas fa-trash"></i></button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="d-flex justify-content-between align-items-center mt-2">
                    <span class="fs-5 fw-bold">Tổng: <span class="text-danger"><fmt:formatNumber value="${cartTotal}" type="number" maxFractionDigits="0"/> ₫</span></span>
                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success">Tiến hành đặt hàng</a>
                </div>
            </div></div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/views/components/footer.jsp" />
