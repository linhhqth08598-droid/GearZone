<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container my-5" style="min-height: 60vh;">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0"><i class="fas fa-history text-danger me-2"></i>Lịch sử mua hàng</h3>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i> Tiếp tục mua sắm
        </a>
    </div>

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
                    <!-- Lặp qua từng sản phẩm trong lịch sử, gộp chung theo đơn hàng nếu muốn (ở đây hiển thị từng item) -->
                    <c:forEach var="item" items="${historyList}">
                        <div class="card shadow-sm border-0 rounded-4 mb-3">
                            <div class="card-header bg-light d-flex justify-content-between align-items-center py-3 rounded-top-4">
                                <div>
                                    <span class="fw-bold text-dark">Mã đơn: #${item.orderCode}</span>
                                    <span class="text-muted ms-3" style="font-size: 14px;">
                                        <i class="far fa-calendar-alt me-1"></i> <fmt:formatDate value="${item.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </span>
                                </div>
                                <div>
                                    <c:choose>
                                        <c:when test="${item.orderStatus == 'PENDING'}">
                                            <span class="badge bg-warning text-dark">Chờ xử lý</span>
                                        </c:when>
                                        <c:when test="${item.orderStatus == 'SHIPPING'}">
                                            <span class="badge bg-primary">Đang giao</span>
                                        </c:when>
                                        <c:when test="${item.orderStatus == 'DELIVERED'}">
                                            <span class="badge bg-success">Hoàn thành</span>
                                        </c:when>
                                        <c:when test="${item.orderStatus == 'CANCELLED'}">
                                            <span class="badge bg-danger">Đã hủy</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${item.orderStatus}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="card-body p-3">
                                <div class="d-flex align-items-center">
                                    <!-- Ảnh -->
                                    <div class="me-3" style="width: 80px; height: 80px; flex-shrink: 0; border-radius: 8px; overflow: hidden; background: #f8fafc; display: flex; align-items: center; justify-content: center;">
                                        <c:choose>
                                            <c:when test="${not empty item.imageUrl}">
                                                <img src="${item.imageUrl}" alt="${item.partName}" style="max-width: 100%; max-height: 100%; object-fit: cover;">
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-image text-muted fs-3"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <!-- Tên và số lượng -->
                                    <div class="flex-grow-1">
                                        <h6 class="mb-1 fw-bold text-dark">${item.partName}</h6>
                                        <span class="text-muted" style="font-size: 14px;">Số lượng: x${item.quantity}</span>
                                    </div>
                                    <!-- Giá -->
                                    <div class="text-end">
                                        <h6 class="text-danger fw-bold mb-0">
                                            <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </h6>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer bg-white text-end py-2 rounded-bottom-4">
                                <span class="text-muted me-2">Thành tiền:</span>
                                <span class="text-danger fw-bold fs-5">
                                    <fmt:formatNumber value="${item.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </span>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/views/components/footer.jsp" />