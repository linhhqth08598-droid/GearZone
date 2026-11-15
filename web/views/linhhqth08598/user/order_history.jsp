<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<jsp:include page="/views/components/sidebar.jsp" />
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0"><i class="fas fa-history text-danger me-2"></i>Lịch sử mua hàng</h3>
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
        <c:when test="${empty historyList}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <img src="https://cdn-icons-png.flaticon.com/512/11329/11329060.png" alt="Empty" style="width: 120px; opacity: 0.5;" class="mb-3">
                <h5 class="text-muted">Bạn chưa có đơn hàng nào.</h5>
                <p class="text-muted mb-4">Hãy khám phá cửa hàng và đặt mua linh kiện nhé!</p>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary px-4 py-2" style="border-radius: 8px;">Mua sắm ngay</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <div class="col-12">
                    <c:forEach var="group" items="${historyList}">
                        <c:set var="head" value="${group[0]}"/>
                        <div class="card shadow-sm border-0 rounded-4 mb-3">
                            <div class="card-header bg-light d-flex justify-content-between align-items-center py-3 rounded-top-4">
                                <div>
                                    <a href="${pageContext.request.contextPath}/order?id=${head.orderId}" class="fw-bold text-dark text-decoration-none">Mã đơn: #${head.orderCode}</a>
                                    <span class="text-muted ms-3" style="font-size: 14px;">
                                        <i class="far fa-calendar-alt me-1"></i> <fmt:formatDate value="${head.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </span>
                                </div>
                                <div><jsp:include page="/views/components/order_status_badge.jsp"><jsp:param name="status" value="${head.orderStatus}"/></jsp:include></div>
                            </div>
                            <div class="card-body p-3">
                                <c:forEach var="item" items="${group}" varStatus="st">
                                    <div class="d-flex align-items-center ${st.last ? '' : 'pb-3 mb-3 border-bottom'}">
                                        <div class="me-3" style="width: 80px; height: 80px; flex-shrink: 0; border-radius: 8px; overflow: hidden; background: #f8fafc; display: flex; align-items: center; justify-content: center;">
                                            <c:choose>
                                                <c:when test="${not empty item.imageUrl}">
                                                    <img src="${item.imageUrl}" alt="<c:out value='${item.partName}'/>" style="max-width: 100%; max-height: 100%; object-fit: cover;">
                                                </c:when>
                                                <c:otherwise><i class="fas fa-image text-muted fs-3"></i></c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="flex-grow-1">
                                            <h6 class="mb-1 fw-bold text-dark"><c:out value='${item.partName}'/></h6>
                                            <span class="text-muted" style="font-size: 14px;">Số lượng: x${item.quantity}</span>
                                        </div>
                                        <div class="text-end">
                                            <h6 class="text-danger fw-bold mb-2">
                                                <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </h6>
                                            <c:if test="${item.orderStatus == 'DELIVERED'}">
                                                <button type="button" class="btn btn-sm btn-outline-danger text-nowrap" data-bs-toggle="modal" data-bs-target="#returnModal-${item.orderId}-${item.partId}">
                                                    <i class="fas fa-undo me-1"></i>Đổi trả
                                                </button>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="card-footer bg-white d-flex justify-content-between align-items-center py-2 rounded-bottom-4">
                                <div class="d-flex gap-2">
                                    <a href="${pageContext.request.contextPath}/order?id=${head.orderId}" class="btn btn-sm btn-outline-secondary text-nowrap">Chi tiết</a>
                                    <c:if test="${head.orderStatus == 'SHIPPING'}">
                                        <form action="${pageContext.request.contextPath}/order" method="POST" class="m-0" onsubmit="return confirm('Xác nhận bạn đã nhận được hàng?');">
                                            <input type="hidden" name="action" value="received">
                                            <input type="hidden" name="id" value="${head.orderId}">
                                            <input type="hidden" name="back" value="history">
                                            <button type="submit" class="btn btn-sm btn-success text-nowrap"><i class="fas fa-check me-1"></i>Đã nhận hàng</button>
                                        </form>
                                    </c:if>
                                </div>
                                <div>
                                    <span class="text-muted me-2">Thành tiền:</span>
                                    <span class="text-danger fw-bold fs-5"><fmt:formatNumber value="${head.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                                </div>
                            </div>
                        </div>

                        <c:if test="${head.orderStatus == 'DELIVERED'}">
                        <c:forEach var="item" items="${group}">
                        <div class="modal fade" id="returnModal-${item.orderId}-${item.partId}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <form action="${pageContext.request.contextPath}/user/returns" method="POST">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Yêu cầu đổi trả: <c:out value="${item.partName}"/></h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body">
                                            <input type="hidden" name="orderId" value="${item.orderId}">
                                            <input type="hidden" name="partId" value="${item.partId}">
                                            <div class="mb-3">
                                                <label class="form-label">Số lượng muốn trả</label>
                                                <input type="number" name="quantity" class="form-control" max="${item.quantity}" value="${item.quantity}" required min="1">
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">Lý do đổi trả</label>
                                                <textarea name="reason" class="form-control" rows="3" required placeholder="Sản phẩm bị lỗi, giao sai hàng..."></textarea>
                                            </div>
                                        </div>
                                        <div class="modal-footer"><button type="submit" class="btn btn-danger">Gửi yêu cầu</button></div>
                                    </form>
                                </div>
                            </div>
                        </div>
                        </c:forEach>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
            <jsp:include page="/views/components/pagination.jsp"><jsp:param name="url" value="/user/order-history"/></jsp:include>
        </c:otherwise>
    </c:choose>


<jsp:include page="/views/components/footer.jsp" />