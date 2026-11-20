<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h3 class="fw-bold mb-0"><i class="fas fa-shield-halved text-danger me-2"></i>Bảo hành của tôi</h3>
        <a href="${pageContext.request.contextPath}/warranty-lookup" class="btn btn-outline-secondary btn-sm">
            <i class="fas fa-magnifying-glass me-1"></i>Tra cứu nhanh theo Serial
        </a>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <!-- Form gửi yêu cầu bảo hành (UC 275) -->
    <div class="card border-0 shadow-sm rounded-4 mb-4">
        <div class="card-header bg-white border-bottom py-3 fw-bold">
            <i class="fas fa-paper-plane text-primary me-2"></i>Gửi yêu cầu bảo hành mới
        </div>
        <div class="card-body p-4">
            <c:choose>
                <c:when test="${empty warranties}">
                    <p class="text-muted mb-0">Bạn chưa có phiếu bảo hành điện tử nào được kích hoạt.</p>
                </c:when>
                <c:otherwise>
                    <form method="POST" action="${pageContext.request.contextPath}/warranty" class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Chọn linh kiện / phiếu bảo hành</label>
                            <select name="warrantyId" class="form-select" required>
                                <option value="">-- Chọn phiếu bảo hành --</option>
                                <c:forEach var="w" items="${warranties}">
                                    <option value="${w.warrantyId}">
                                        <c:out value="${w.partName}"/> | S/N: <c:out value="${w.serialNumber}"/> (Hạn: <fmt:formatDate value="${w.endDate}" pattern="dd/MM/yyyy"/>)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Mô tả tình trạng lỗi</label>
                            <textarea name="faultDescription" class="form-control" rows="2" minlength="10"
                                      placeholder="Mô tả chi tiết lỗi gặp phải (ít nhất 10 ký tự)..." required></textarea>
                        </div>
                        <div class="col-12 text-end">
                            <button type="submit" class="btn btn-danger px-4">
                                <i class="fas fa-paper-plane me-1"></i>Gửi yêu cầu
                            </button>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Lịch sử yêu cầu bảo hành (UC 287) -->
    <div class="card border-0 shadow-sm rounded-4">
        <div class="card-header bg-white border-bottom py-3 fw-bold">
            <i class="fas fa-clock-rotate-left text-primary me-2"></i>Lịch sử yêu cầu bảo hành
        </div>
        <div class="card-body p-3">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>#</th>
                            <th>Linh kiện & Serial</th>
                            <th>Mô tả lỗi</th>
                            <th>Trạng thái</th>
                            <th>Kết quả / Ghi chú</th>
                            <th>Tiến trình</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${claims}">
                            <tr>
                                <td class="fw-bold">#${c.claimId}</td>
                                <td>
                                    <div class="fw-semibold"><c:out value="${c.partName}"/></div>
                                    <div class="small text-muted">S/N: <c:out value="${c.serialNumber}"/> &bull; Phiếu: <c:out value="${c.warrantyCode}"/></div>
                                </td>
                                <td><c:out value="${c.faultDescription}"/></td>
                                <td>
                                    <span class="badge ${c.status == 'COMPLETED' ? 'bg-success' : (c.status == 'REJECTED' ? 'bg-danger' : 'bg-info text-dark')}">
                                        <c:out value="${c.status}"/>
                                    </span>
                                </td>
                                <td class="small">
                                    <c:if test="${not empty c.rejectionReason}"><div class="text-danger">Lý do từ chối: <c:out value="${c.rejectionReason}"/></div></c:if>
                                    <c:if test="${not empty c.serviceCenterName}"><div>Trung tâm: <c:out value="${c.serviceCenterName}"/></div></c:if>
                                    <c:if test="${not empty c.resolutionNote}"><div>Kết quả: <c:out value="${c.resolutionNote}"/></div></c:if>
                                    <c:if test="${not empty c.replacementSerialNumber}"><div class="text-success fw-semibold">Serial đổi mới: <c:out value="${c.replacementSerialNumber}"/></div></c:if>
                                </td>
                                <td class="small">
                                    <c:forEach var="l" items="${c.logs}">
                                        <div class="mb-1">
                                            <span class="text-muted">[<fmt:formatDate value="${l.createdAt}" pattern="dd/MM HH:mm"/>]</span>
                                            <strong><c:out value="${l.status}"/>:</strong> <c:out value="${l.note}"/>
                                        </div>
                                    </c:forEach>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty claims}">
                            <tr><td colspan="6" class="text-center text-muted py-4">Bạn chưa có yêu cầu bảo hành nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />