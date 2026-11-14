<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<!-- Nội dung chính -->
<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-headset me-2"></i>Hỗ trợ Khách hàng</h1>
</div>

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
                        <th>Mã Ticket</th>
                        <th>Khách hàng</th>
                        <th>Tiêu đề</th>
                        <th>Loại</th>
                        <th>Trạng thái</th>
                        <th>Ngày gửi</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="t" items="${ticketsList}">
                        <tr>
                            <td><strong>#${t.ticketCode}</strong></td>
                            <td>
                                <div class="fw-bold">${t.userId}</div>
                                <!-- Nếu có join full_name thì hiển thị -->
                            </td>
                            <td class="text-wrap" style="max-width: 200px;">${t.subject}</td>
                            <td><span class="badge bg-info text-dark">${t.ticketType}</span></td>
                            <td>
                                <c:choose>
                                    <c:when test="${t.status == 'OPEN'}"><span class="badge bg-warning text-dark">Mới (Open)</span></c:when>
                                    <c:when test="${t.status == 'IN_PROGRESS'}"><span class="badge bg-primary">Đang xử lý</span></c:when>
                                    <c:when test="${t.status == 'RESOLVED'}"><span class="badge bg-success">Đã giải quyết</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">Đã đóng</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><fmt:formatDate value="${t.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td class="text-center">
                                <button type="button" class="btn btn-sm btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#ticketModal-${t.ticketId}">
                                    <i class="fas fa-eye"></i> Xem
                                </button>
                            </td>
                        </tr>

                        <!-- Modal chi tiết Ticket (Đặt ngay trong vòng lặp để lấy đúng ID) -->
                        <div class="modal fade" id="ticketModal-${t.ticketId}" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-lg modal-dialog-centered">
                                <div class="modal-content border-0 shadow-lg rounded-4">
                                    <form action="${pageContext.request.contextPath}/admin/support" method="POST">
                                        <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                                            <h5 class="modal-title fw-bold text-primary"><i class="fas fa-ticket-alt me-2"></i>Chi tiết Ticket: #${t.ticketCode}</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>
                                        <div class="modal-body p-4">
                                            <input type="hidden" name="ticketId" value="${t.ticketId}">
                                            
                                            <div class="mb-4">
                                                <h6 class="fw-bold text-dark mb-2">Tiêu đề:</h6>
                                                <p class="text-secondary mb-0">${t.subject}</p>
                                            </div>
                                            
                                            <div class="mb-4">
                                                <h6 class="fw-bold text-dark mb-2">Nội dung:</h6>
                                                <div class="bg-light p-3 rounded border" style="white-space: pre-wrap;">${t.content}</div>
                                            </div>

                                            <div class="mb-2">
                                                <h6 class="fw-bold text-dark mb-2">Cập nhật trạng thái:</h6>
                                                <select name="status" class="form-select form-control">
                                                    <option value="OPEN" ${t.status == 'OPEN' ? 'selected' : ''}>Mới (Open)</option>
                                                    <option value="IN_PROGRESS" ${t.status == 'IN_PROGRESS' ? 'selected' : ''}>Đang xử lý</option>
                                                    <option value="RESOLVED" ${t.status == 'RESOLVED' ? 'selected' : ''}>Đã giải quyết</option>
                                                    <option value="CLOSED" ${t.status == 'CLOSED' ? 'selected' : ''}>Đóng</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                                            <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                                            <button type="submit" class="btn btn-primary px-4"><i class="fas fa-save me-1"></i> Lưu thay đổi</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />