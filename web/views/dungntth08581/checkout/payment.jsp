<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div>
    <h3 class="fw-bold mb-4"><i class="fas fa-credit-card text-primary me-2"></i>Đặt hàng</h3>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/checkout" method="POST">
        <div class="row g-3">
            <div class="col-lg-7">
                <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
                    <h5 class="fw-bold mb-3">Địa chỉ nhận hàng</h5>
                    <c:choose>
                        <c:when test="${empty addresses}">
                            <p class="text-muted mb-2">Bạn chưa có địa chỉ giao hàng.</p>
                            <a href="${pageContext.request.contextPath}/user?action=address" class="btn btn-outline-primary">Thêm địa chỉ</a>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${addresses}">
                                <label class="d-flex gap-2 border rounded-3 p-3 mb-2" style="cursor:pointer">
                                    <input type="radio" name="addressId" value="${a.addressId}" class="form-check-input mt-1" ${a.isDefault ? 'checked' : ''} required>
                                    <span>
                                        <span class="fw-semibold"><c:out value="${a.recipientName}"/></span> - <c:out value="${a.phone}"/>
                                        <c:if test="${a.isDefault}"><span class="badge bg-success ms-1">Mặc định</span></c:if><br>
                                        <span class="text-muted small"><c:out value="${a.detailedAddress}"/>, <c:out value="${a.ward}"/>, <c:out value="${a.province}"/></span>
                                    </span>
                                </label>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div></div>

                <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
                    <h5 class="fw-bold mb-3">Vận chuyển</h5>
                    <c:forEach var="m" items="${shippingMethods}" varStatus="st">
                        <label class="d-flex gap-2 border rounded-3 p-3 mb-2" style="cursor:pointer">
                            <input type="radio" name="shippingMethodId" value="${m.id}" class="form-check-input mt-1" ${st.first ? 'checked' : ''}>
                            <span><c:out value="${m.name}"/> - <fmt:formatNumber value="${m.fee}" type="number" maxFractionDigits="0"/> ₫
                                <c:if test="${m.days != null}"><span class="text-muted small">(dự kiến ${m.days} ngày)</span></c:if></span>
                        </label>
                    </c:forEach>
                    <c:if test="${empty shippingMethods}"><p class="text-muted mb-0">Miễn phí vận chuyển.</p></c:if>
                </div></div>

                <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
                    <h5 class="fw-bold mb-3">Thanh toán & ghi chú</h5>
                    <div class="border rounded-3 p-3 mb-3"><i class="fas fa-money-bill-wave text-success me-2"></i>Thanh toán khi nhận hàng (COD)</div>
                    <textarea name="note" class="form-control" rows="2" maxlength="500" placeholder="Ghi chú cho đơn hàng (không bắt buộc)"></textarea>
                </div></div>
            </div>

            <div class="col-lg-5">
                <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
                    <h5 class="fw-bold mb-3">Đơn hàng của bạn</h5>
                    <c:forEach var="l" items="${cartLines}">
                        <div class="d-flex justify-content-between border-bottom py-2 small">
                            <span><c:out value="${l.partName}"/> <span class="text-muted">x${l.quantity}</span></span>
                            <span><fmt:formatNumber value="${l.lineTotal}" type="number" maxFractionDigits="0"/> ₫</span>
                        </div>
                    </c:forEach>
                    <div class="d-flex justify-content-between fs-5 fw-bold mt-3">
                        <span>Tạm tính</span><span class="text-danger"><fmt:formatNumber value="${cartTotal}" type="number" maxFractionDigits="0"/> ₫</span>
                    </div>
                    <div class="form-text">Phí vận chuyển được cộng khi đặt hàng.</div>
                    <button type="submit" class="btn btn-success w-100 mt-3" ${empty addresses ? 'disabled' : ''}><i class="fas fa-check me-1"></i> Xác nhận đặt hàng</button>
                    <a href="${pageContext.request.contextPath}/cart" class="btn btn-link w-100">Quay lại giỏ hàng</a>
                </div></div>
            </div>
        </div>
    </form>
</div>

<jsp:include page="/views/components/footer.jsp" />
