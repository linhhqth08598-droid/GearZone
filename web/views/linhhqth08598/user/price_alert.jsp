<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<jsp:include page="/views/components/sidebar.jsp" />
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0"><i class="fas fa-bell text-warning me-2"></i>Theo dõi giá sản phẩm</h3>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i> Tiếp tục mua sắm
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
        <c:when test="${empty alerts}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <img src="https://cdn-icons-png.flaticon.com/512/1170/1170678.png" alt="Empty" style="width: 120px; opacity: 0.5;" class="mb-3">
                <h5 class="text-muted">Bạn chưa theo dõi sản phẩm nào.</h5>
                <p class="text-muted mb-4">Hãy thêm sản phẩm vào danh sách theo dõi để nhận thông báo khi giá giảm!</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <c:forEach var="alert" items="${alerts}">
                    <div class="col-md-6 mb-4">
                        <div class="card shadow-sm border-0 rounded-4 h-100">
                            <div class="card-body p-3">
                                <div class="d-flex align-items-center">
                                    <div class="me-3" style="width: 80px; height: 80px; flex-shrink: 0; border-radius: 8px; overflow: hidden; background: #f8fafc; display: flex; align-items: center; justify-content: center;">
                                        <c:choose>
                                            <c:when test="${not empty alert.imageUrl}">
                                                <img src="${alert.imageUrl}" alt="${alert.partName}" style="max-width: 100%; max-height: 100%; object-fit: cover;">
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-image text-muted fs-3"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="flex-grow-1">
                                        <h6 class="mb-1 fw-bold text-dark">${alert.partName}</h6>
                                        <div class="d-flex justify-content-between align-items-center mt-2">
                                            <div>
                                                <span class="text-muted d-block" style="font-size: 13px;">Giá hiện tại:</span>
                                                <span class="fw-bold text-dark">
                                                    <fmt:formatNumber value="${alert.currentPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </span>
                                            </div>
                                            <div class="text-end">
                                                <span class="text-muted d-block" style="font-size: 13px;">Giá mong muốn:</span>
                                                <span class="fw-bold text-danger">
                                                    <fmt:formatNumber value="${alert.targetPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer bg-white border-top-0 text-end pb-3">
                                <form action="${pageContext.request.contextPath}/user/price-alert" method="POST" class="d-inline">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="notificationId" value="${alert.notificationId}">
                                    <button type="submit" class="btn btn-outline-danger btn-sm" onclick="return confirm('Bạn có chắc muốn hủy theo dõi sản phẩm này?');">
                                        <i class="fas fa-trash-alt me-1"></i> Hủy theo dõi
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>


<jsp:include page="/views/components/footer.jsp" />