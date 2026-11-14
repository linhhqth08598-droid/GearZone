<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container my-5" style="min-height: 60vh;">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0"><i class="fas fa-bell text-primary me-2"></i>Thông báo của bạn</h3>
        <a href="${pageContext.request.contextPath}/user/notifications?action=markRead" class="btn btn-outline-secondary">
            <i class="fas fa-check-double me-1"></i> Đánh dấu tất cả đã đọc
        </a>
    </div>

    <c:choose>
        <c:when test="${empty notifications}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <img src="https://cdn-icons-png.flaticon.com/512/3602/3602129.png" alt="Empty" style="width: 120px; opacity: 0.5;" class="mb-3">
                <h5 class="text-muted">Bạn không có thông báo nào.</h5>
                <p class="text-muted mb-4">Các thông báo về đơn hàng, bảo hành và khuyến mãi sẽ xuất hiện tại đây.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <div class="col-12">
                    <c:forEach var="n" items="${notifications}">
                        <div class="card shadow-sm border-0 rounded-3 mb-3 ${n.isRead ? '' : 'border-start border-4 border-primary'}">
                            <div class="card-body p-3">
                                <div class="d-flex justify-content-between align-items-start">
                                    <div class="flex-grow-1">
                                        <h6 class="fw-bold text-dark mb-1">
                                            <c:choose>
                                                <c:when test="${n.notificationType == 'ORDER'}"><i class="fas fa-shopping-bag text-primary me-1"></i></c:when>
                                                <c:when test="${n.notificationType == 'WARRANTY'}"><i class="fas fa-shield-alt text-success me-1"></i></c:when>
                                                <c:when test="${n.notificationType == 'PROMOTION'}"><i class="fas fa-tags text-warning me-1"></i></c:when>
                                                <c:when test="${n.notificationType == 'PRICE_DROP'}"><i class="fas fa-arrow-down text-danger me-1"></i></c:when>
                                                <c:otherwise><i class="fas fa-info-circle text-secondary me-1"></i></c:otherwise>
                                            </c:choose>
                                            ${n.title}
                                        </h6>
                                        <p class="text-secondary mb-2">${n.content}</p>
                                        <small class="text-muted fst-italic">
                                            <i class="far fa-clock me-1"></i> <fmt:formatDate value="${n.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/views/components/footer.jsp" />