<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
    <h1 class="h2 mb-0"><i class="fas fa-undo me-2 text-danger"></i>Quản lý Trả hàng Nhà Cung Cấp</h1>
    <div>
        <a href="${pageContext.request.contextPath}/admin/purchases" class="btn btn-outline-primary me-2">
            <i class="fas fa-dolly me-1"></i> Đơn Nhập Hàng
        </a>
        <a href="${pageContext.request.contextPath}/admin/inventory" class="btn btn-outline-secondary">
            <i class="fas fa-warehouse me-1"></i> Tồn Kho
        </a>
    </div>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show">
        <i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Mã Phiếu</th>
                        <th>NCC</th>
                        <th>Đơn PO</th>
                        <th>Linh kiện</th>
                        <th>SL Trả</th>
                        <th>Loại lỗi</th>
                        <th>Lý do</th>
                        <th>Trạng thái</th>
                        <th>Ngày tạo</th>
                        <th class="text-center">Cập nhật</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${returnList}">
                        <tr>
                            <td>#${item.supplierReturnId}</td>
                            <td><code><c:out value="${item.returnCode}"/></code></td>
                            <td class="fw-semibold">NCC #<c:out value="${item.supplierId}"/></td>
                            <td>PO #<c:out value="${item.poId}"/></td>
                            <td>Part #<c:out value="${item.partId}"/></td>
                            <td class="fw-bold text-danger">-<c:out value="${item.quantity}"/></td>
                            <td><span class="badge bg-secondary"><c:out value="${item.defectType}"/></span></td>
                            <td><small><c:out value="${item.reason}"/></small></td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.status == 'COMPLETED'}"><span class="badge bg-success">Hoàn thành</span></c:when>
                                    <c:when test="${item.status == 'SENT'}"><span class="badge bg-primary">Đã gửi NCC</span></c:when>
                                    <c:when test="${item.status == 'REJECTED'}"><span class="badge bg-danger">Từ chối</span></c:when>
                                    <c:otherwise><span class="badge bg-warning text-dark"><c:out value="${item.status}"/></span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><small><fmt:formatDate value="${item.createdAt}" pattern="dd/MM/yyyy"/></small></td>
                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/admin/supplier-returns" method="POST" class="d-flex gap-1 justify-content-center">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="returnId" value="${item.supplierReturnId}">
                                    <select name="status" class="form-select form-select-sm" style="width: 130px;">
                                        <option value="PENDING" ${item.status == 'PENDING' ? 'selected' : ''}>PENDING</option>
                                        <option value="SENT" ${item.status == 'SENT' ? 'selected' : ''}>SENT</option>
                                        <option value="COMPLETED" ${item.status == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                                        <option value="REJECTED" ${item.status == 'REJECTED' ? 'selected' : ''}>REJECTED</option>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-outline-primary">Lưu</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty returnList}">
                        <tr><td colspan="11" class="text-center text-muted py-4">Chưa có phiếu trả hàng nhà cung cấp nào.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />
