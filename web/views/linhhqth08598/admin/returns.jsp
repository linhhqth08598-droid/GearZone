<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<!-- Nội dung chính (Không có thẻ <main> bao ngoài) -->
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-undo-alt me-2"></i>Quản lý Yêu cầu Đổi trả</h1>
</div>

<!-- Thông báo -->
<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="fas fa-check-circle me-1"></i> ${sessionScope.message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Mã YC</th>
                        <th>Mã Đơn</th>
                        <th>Số lượng</th>
                        <th>Lý do khách</th>
                        <th>Trạng thái</th>
                        <th>Ngày gửi</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${returnsList}">
                        <tr>
                            <td>#${r.returnId}</td>
                            <td>#${r.orderId}</td>
                            <td>${r.quantity}</td>
                            <td class="text-wrap" style="max-width: 250px;">${r.reason}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${r.status == 'PENDING'}"><span class="badge bg-warning text-dark">Chờ duyệt</span></c:when>
                                    <c:when test="${r.status == 'APPROVED'}"><span class="badge bg-success">Đã duyệt</span></c:when>
                                    <c:when test="${r.status == 'REJECTED'}"><span class="badge bg-danger">Từ chối</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">${r.status}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                    <td class="text-center">
                        <c:if test="${r.status == 'PENDING'}">
                            <!-- Nút Duyệt -->
                            <form action="${pageContext.request.contextPath}/admin/returns" method="POST" class="d-inline">
                                <input type="hidden" name="returnId" value="${r.returnId}">
                                <input type="hidden" name="action" value="approve">
                                <button type="submit" class="btn btn-sm btn-outline-success" onclick="return confirm('Duyệt yêu cầu này?');">
                                    <i class="fas fa-check"></i> Duyệt
                                </button>
                            </form>

                            <!-- Nút Từ chối (Mở Modal nhập lý do) -->
                            <button type="button" class="btn btn-sm btn-outline-danger ms-1" data-bs-toggle="modal" data-bs-target="#rejectModal-${r.returnId}">
                                <i class="fas fa-times"></i> Từ chối
                            </button>

                            <!-- Modal Từ chối -->
                            <div class="modal fade" id="rejectModal-${r.returnId}" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <form action="${pageContext.request.contextPath}/admin/returns" method="POST">
                                            <div class="modal-header">
                                                <h5 class="modal-title">Từ chối yêu cầu #${r.returnId}</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                            </div>
                                            <div class="modal-body">
                                                <input type="hidden" name="returnId" value="${r.returnId}">
                                                <input type="hidden" name="action" value="reject">
                                                <div class="mb-3">
                                                    <label class="form-label">Lý do từ chối</label>
                                                    <textarea name="reason" class="form-control" rows="3" required placeholder="Nhập lý do từ chối..."></textarea>
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="submit" class="btn btn-danger">Xác nhận từ chối</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/views/components/footer.jsp" />