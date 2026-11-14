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
                                <!-- Nút Yêu cầu đổi trả (Chỉ hiện khi đơn đã giao) -->
                                <c:if test="${item.orderStatus == 'DELIVERED'}">
                                    <button type="button" class="btn btn-sm btn-outline-danger me-3" data-bs-toggle="modal" data-bs-target="#returnModal-${item.orderId}-${item.partId}">
                                        <i class="fas fa-undo"></i> Yêu cầu đổi trả
                                    </button>
                                </c:if>

                                <span class="text-muted me-2">Thành tiền:</span>
                                <span class="text-danger fw-bold fs-5">
                                    <fmt:formatNumber value="${item.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </span>
                            </div>
                        </div>

                        <!-- Modal nhập lý do đổi trả (Đặt ngay trong vòng lặp để lấy đúng ID) -->
                        <div class="modal fade" id="returnModal-${item.orderId}-${item.partId}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <form action="${pageContext.request.contextPath}/user/returns" method="POST">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Yêu cầu đổi trả sản phẩm</h5>
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
                                        <div class="modal-footer">
                                            <button type="submit" class="btn btn-danger">Gửi yêu cầu</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:otherwise>
    </c:choose>


<jsp:include page="/views/components/footer.jsp" />