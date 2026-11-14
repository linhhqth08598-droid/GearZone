<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<jsp:include page="/views/components/sidebar.jsp" />
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0"><i class="fas fa-exchange-alt text-danger me-2"></i>Yêu cầu đổi trả</h3>
        <a href="${pageContext.request.contextPath}/user/order-history" class="btn btn-outline-secondary">
            <i class="fas fa-history me-1"></i> Lịch sử mua hàng
        </a>
    </div>

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
        <c:when test="${empty returnsList}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <img src="https://cdn-icons-png.flaticon.com/512/11329/11329060.png" alt="Empty" style="width: 120px; opacity: 0.5;" class="mb-3">
                <h5 class="text-muted">Bạn chưa có yêu cầu đổi trả nào.</h5>
                <p class="text-muted mb-4">Nếu sản phẩm gặp lỗi, hãy tạo yêu cầu đổi trả tại đây.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <c:forEach var="item" items="${returnsList}">
                    <div class="col-md-6 mb-4">
                        <div class="card shadow-sm border-0 rounded-4 h-100">
                            <div class="card-body p-4">
                                <div class="d-flex justify-content-between align-items-start mb-3">
                                    <h6 class="fw-bold text-dark mb-0">Mã đơn: #${item.orderId}</h6>
                                    <c:choose>
                                        <c:when test="${item.status == 'PENDING'}"><span class="badge bg-warning text-dark">Chờ duyệt</span></c:when>
                                        <c:when test="${item.status == 'APPROVED'}"><span class="badge bg-primary">Đã duyệt</span></c:when>
                                        <c:when test="${item.status == 'REJECTED'}"><span class="badge bg-danger">Từ chối</span></c:when>
                                        <c:when test="${item.status == 'REFUNDED'}"><span class="badge bg-success">Đã hoàn tiền</span></c:when>
                                        <c:otherwise><span class="badge bg-secondary">${item.status}</span></c:otherwise>
                                    </c:choose>
                                </div>
                                <p class="mb-1"><span class="text-muted">Số lượng:</span> ${item.quantity}</p>
                                <p class="mb-1"><span class="text-muted">Lý do:</span> ${item.reason}</p>
                                <p class="text-muted fst-italic" style="font-size: 13px;">
                                    <i class="far fa-clock me-1"></i> Ngày gửi: <fmt:formatDate value="${item.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </p>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>


<jsp:include page="/views/components/footer.jsp" />