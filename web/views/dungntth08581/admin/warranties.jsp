<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h3 class="fw-bold mb-0"><i class="fas fa-shield-halved text-primary me-2"></i>Quản lý Bảo hành</h3>
        <form method="GET" action="${pageContext.request.contextPath}/admin/warranty" class="d-flex gap-2">
            <select name="status" class="form-select" onchange="this.form.submit()">
                <option value="">Tất cả trạng thái</option>
                <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Chờ tiếp nhận (PENDING)</option>
                <option value="APPROVED" ${status == 'APPROVED' ? 'selected' : ''}>Đã duyệt (APPROVED)</option>
                <option value="AT_SERVICE_CENTER" ${status == 'AT_SERVICE_CENTER' ? 'selected' : ''}>Đang ở trung tâm BH</option>
                <option value="REPAIRED" ${status == 'REPAIRED' ? 'selected' : ''}>Đã sửa chữa (REPAIRED)</option>
                <option value="REPLACED" ${status == 'REPLACED' ? 'selected' : ''}>Đã đổi mới (REPLACED)</option>
                <option value="COMPLETED" ${status == 'COMPLETED' ? 'selected' : ''}>Hoàn tất (COMPLETED)</option>
                <option value="REJECTED" ${status == 'REJECTED' ? 'selected' : ''}>Từ chối (REJECTED)</option>
            </select>
        </form>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <!-- Thanh công cụ nhanh: UC 271 (Hạn BH theo danh mục) & UC 272 (Phát hành phiếu BH cho đơn hàng) -->
    <div class="row g-3 mb-4">
        <div class="col-lg-6">
            <div class="card border-0 shadow-sm rounded-4 h-100">
                <div class="card-body p-3">
                    <h6 class="fw-bold mb-2"><i class="fas fa-calendar-check text-primary me-1"></i>Thiết lập hạn bảo hành theo danh mục (UC 271)</h6>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/warranty" class="d-flex gap-2 flex-wrap">
                        <input type="hidden" name="action" value="setCategoryPolicy">
                        <input type="hidden" name="statusFilter" value="${status}">
                        <select name="categoryId" class="form-select form-select-sm" style="max-width: 220px;" required>
                            <option value="">-- Chọn danh mục --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.categoryId}"><c:out value="${cat.categoryName}"/></option>
                            </c:forEach>
                        </select>
                        <input type="number" name="warrantyMonths" class="form-control form-control-sm" style="max-width: 110px;" min="0" max="120" value="24" placeholder="Tháng" required>
                        <button type="submit" class="btn btn-sm btn-primary">Lưu chính sách</button>
                    </form>
                </div>
            </div>
        </div>
        <div class="col-lg-6">
            <div class="card border-0 shadow-sm rounded-4 h-100">
                <div class="card-body p-3">
                    <h6 class="fw-bold mb-2"><i class="fas fa-file-circle-plus text-success me-1"></i>Phát hành phiếu bảo hành điện tử theo Đơn hàng (UC 272)</h6>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/warranty" class="d-flex gap-2">
                        <input type="hidden" name="action" value="issueOrder">
                        <input type="hidden" name="statusFilter" value="${status}">
                        <input type="number" name="orderId" class="form-control form-control-sm" style="max-width: 180px;" min="1" placeholder="Nhập Order ID..." required>
                        <button type="submit" class="btn btn-sm btn-success">Phát hành phiếu BH</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Danh sách yêu cầu bảo hành -->
    <div class="card border-0 shadow-sm rounded-4">
        <div class="card-body p-3">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>#</th>
                            <th>Khách hàng</th>
                            <th>Linh kiện & Serial</th>
                            <th>Lỗi & Tình trạng</th>
                            <th>Trạng thái</th>
                            <th style="min-width: 340px;">Xử lý (UC 276–286)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${claims}">
                            <tr>
                                <td class="fw-bold">#${c.claimId}</td>
                                <td>
                                    <div class="fw-semibold"><c:out value="${c.customerName}"/></div>
                                    <div class="small text-muted"><c:out value="${c.customerPhone}"/></div>
                                </td>
                                <td>
                                    <div class="fw-semibold"><c:out value="${c.partName}"/></div>
                                    <div class="small text-muted">S/N: <c:out value="${c.serialNumber}"/></div>
                                    <div class="small text-muted">Hạn: <fmt:formatDate value="${c.endDate}" pattern="dd/MM/yyyy"/></div>
                                </td>
                                <td class="small">
                                    <div><strong>Lỗi:</strong> <c:out value="${c.faultDescription}"/></div>
                                    <c:if test="${not empty c.receivedCondition}"><div><strong>Tiếp nhận:</strong> <c:out value="${c.receivedCondition}"/></div></c:if>
                                    <c:if test="${not empty c.serviceCenterName}"><div><strong>TTBH:</strong> <c:out value="${c.serviceCenterName}"/></div></c:if>
                                    <c:if test="${not empty c.resolutionNote}"><div><strong>Kết quả:</strong> <c:out value="${c.resolutionNote}"/></div></c:if>
                                    <c:if test="${not empty c.rejectionReason}"><div class="text-danger"><strong>Từ chối:</strong> <c:out value="${c.rejectionReason}"/></div></c:if>
                                </td>
                                <td>
                                    <span class="badge ${c.status == 'COMPLETED' ? 'bg-success' : (c.status == 'REJECTED' ? 'bg-danger' : 'bg-warning text-dark')}">
                                        <c:out value="${c.status}"/>
                                    </span>
                                </td>
                                <td>
                                    <!-- Bước 1: Tiếp nhận & Duyệt / Từ chối -->
                                    <c:if test="${c.status == 'PENDING'}">
                                        <form method="POST" action="${pageContext.request.contextPath}/admin/warranty" class="mb-1">
                                            <input type="hidden" name="action" value="inspect">
                                            <input type="hidden" name="claimId" value="${c.claimId}">
                                            <input type="hidden" name="statusFilter" value="${status}">
                                            <input type="text" name="receivedCondition" class="form-control form-control-sm mb-1" placeholder="Tình trạng tiếp nhận (tem, ngoại hình...)" required>
                                            <input type="text" name="rejectionReason" class="form-control form-control-sm mb-1" placeholder="Lý do từ chối (nếu từ chối)">
                                            <div class="d-flex gap-1">
                                                <button type="submit" name="eligible" value="true" class="btn btn-sm btn-primary">Duyệt BH</button>
                                                <button type="submit" name="eligible" value="false" class="btn btn-sm btn-outline-danger">Từ chối</button>
                                            </div>
                                        </form>
                                    </c:if>

                                    <!-- Bước 2: Chuyển trung tâm BH -->
                                    <c:if test="${c.status == 'APPROVED'}">
                                        <form method="POST" action="${pageContext.request.contextPath}/admin/warranty" class="d-flex gap-1 mb-1">
                                            <input type="hidden" name="action" value="sendCenter">
                                            <input type="hidden" name="claimId" value="${c.claimId}">
                                            <input type="hidden" name="statusFilter" value="${status}">
                                            <input type="text" name="serviceCenterName" class="form-control form-control-sm" placeholder="Tên TTBH / Hãng..." required>
                                            <button type="submit" class="btn btn-sm btn-info text-white text-nowrap">Chuyển TTBH</button>
                                        </form>
                                    </c:if>

                                    <!-- Bước 3: Ghi kết quả Sửa chữa / Đổi mới -->
                                    <c:if test="${c.status == 'APPROVED' || c.status == 'AT_SERVICE_CENTER'}">
                                        <form method="POST" action="${pageContext.request.contextPath}/admin/warranty" class="mb-1">
                                            <input type="hidden" name="action" value="resolve">
                                            <input type="hidden" name="claimId" value="${c.claimId}">
                                            <input type="hidden" name="statusFilter" value="${status}">
                                            <div class="d-flex gap-1 mb-1">
                                                <select name="resolutionType" class="form-select form-select-sm" style="max-width: 120px;">
                                                    <option value="REPAIRED">Sửa chữa</option>
                                                    <option value="REPLACED">Đổi mới</option>
                                                </select>
                                                <input type="text" name="replacementSerial" class="form-control form-control-sm" placeholder="Serial mới (nếu đổi)">
                                            </div>
                                            <div class="d-flex gap-1">
                                                <input type="text" name="resolutionNote" class="form-control form-control-sm" placeholder="Ghi chú kết quả sửa/đổi..." required>
                                                <button type="submit" class="btn btn-sm btn-warning text-nowrap">Lưu kết quả</button>
                                            </div>
                                        </form>
                                    </c:if>

                                    <!-- Bước 4: Hoàn tất & bàn giao -->
                                    <c:if test="${c.status == 'REPAIRED' || c.status == 'REPLACED'}">
                                        <form method="POST" action="${pageContext.request.contextPath}/admin/warranty" class="d-flex gap-1">
                                            <input type="hidden" name="action" value="complete">
                                            <input type="hidden" name="claimId" value="${c.claimId}">
                                            <input type="hidden" name="statusFilter" value="${status}">
                                            <input type="text" name="finalNote" class="form-control form-control-sm" placeholder="Ghi chú bàn giao...">
                                            <button type="submit" class="btn btn-sm btn-success text-nowrap">Hoàn tất</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty claims}">
                            <tr><td colspan="6" class="text-center text-muted py-4">Chưa có yêu cầu bảo hành nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />