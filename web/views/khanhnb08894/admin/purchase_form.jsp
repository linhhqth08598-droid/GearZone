<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-boxes me-2"></i>Chi tiết Đơn Nhập Hàng (PO ID: #${poId})</h1>
    <div class="d-flex gap-2">
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addDetailModal">
            <i class="fas fa-plus me-1"></i> Thêm linh kiện vào đơn
        </button>
        <a href="${pageContext.request.contextPath}/admin/purchases" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách PO
        </a>
    </div>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="fas fa-check-circle me-1"></i> ${sessionScope.message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show">
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
                        <th>ID</th>
                        <th>Mã SKU</th>
                        <th>Tên Linh Kiện</th>
                        <th>SL Nhập</th>
                        <th>Đơn Giá Nhập</th>
                        <th>Thành Tiền</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${details}">
                        <tr>
                            <td>#${item.poDetailId}</td>
                            <td><code>${item.productCode}</code></td>
                            <td class="fw-bold">${item.partName}</td>
                            <td>${item.importQuantity}</td>
                            <td class="text-danger">
                                <fmt:formatNumber value="${item.importUnitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </td>
                            <td class="text-danger fw-semibold">
                                <fmt:formatNumber value="${item.importQuantity * item.importUnitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </td>
                            <td><span class="badge bg-success">${item.status}</span></td>
                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/admin/purchase-detail" method="POST" class="m-0">
                                    <input type="hidden" name="action" value="deleteDetail">
                                    <input type="hidden" name="poDetailId" value="${item.poDetailId}">
                                    <input type="hidden" name="poId" value="${poId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc muốn xóa linh kiện này khỏi đơn?');">
                                        <i class="fas fa-trash-alt"></i> Xóa
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty details}">
                        <tr>
                            <td colspan="8" class="text-center text-muted py-4">Chưa có linh kiện nào trong đơn nhập này.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal Thêm linh kiện vào chi tiết PO -->
<div class="modal fade" id="addDetailModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/purchase-detail" method="POST">
                <input type="hidden" name="action" value="addDetail">
                <input type="hidden" name="poId" value="${poId}">
                <div class="modal-header bg-white border-bottom border-primary border-3 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-plus-circle me-2"></i>Thêm Linh Kiện Vào Đơn Nhập</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Chọn Linh Kiện *</label>
                        <select name="partId" class="form-select" required>
                            <option value="">-- Chọn linh kiện --</option>
                            <c:forEach var="p" items="${partsList}">
                                <option value="${p.partId}">${p.productCode} - ${p.partName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Số lượng nhập *</label>
                        <input type="number" name="importQuantity" class="form-control" required min="1" value="10">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Đơn giá nhập (VNĐ) *</label>
                        <input type="number" step="0.01" name="importUnitPrice" class="form-control" required min="0" placeholder="VD: 2500000">
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light py-3">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Thêm vào đơn</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />