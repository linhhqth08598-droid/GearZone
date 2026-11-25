<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-dolly me-2"></i>Quản lý Đơn Nhập Hàng (PO)</h1>
    <div>
        <!-- Nút bấm điều hướng sang Trả hàng NCC, hoàn toàn độc lập, không sửa sidebar chung -->
        <a href="${pageContext.request.contextPath}/admin/supplier-returns" class="btn btn-outline-danger me-2">
            <i class="fas fa-undo me-1"></i> Quản lý Trả Hàng
        </a>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addPoModal">
            <i class="fas fa-plus me-1"></i> Khởi tạo Đơn Nhanh
        </button>
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
                        <th>Mã Đơn (PO)</th>
                        <th>Nhà Cung Cấp</th>
                        <th>Tổng tiền</th>
                        <th>Ngày tạo</th>
                        <th>Dự kiến giao</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="po" items="${purchases}">
                        <tr>
                            <td>#${po.poId}</td>
                            <!-- Bấm trực tiếp vào Mã PO để xem chi tiết -->
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/purchase-detail?poId=${po.poId}" class="text-decoration-none fw-bold text-primary" title="Xem chi tiết đơn này">
                                    <code class="fs-6">${po.poCode}</code> <i class="fas fa-external-link-alt small ms-1"></i>
                                </a>
                            </td>
                            <td class="fw-bold">${po.note}</td>
                            <td class="text-danger fw-semibold">
                                <fmt:formatNumber value="${po.totalCost}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </td>
                            <td><fmt:formatDate value="${po.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td><fmt:formatDate value="${po.expectedDeliveryDate}" pattern="dd/MM/yyyy"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${po.status == 'PENDING'}"><span class="badge bg-warning text-dark">Chờ xác nhận</span></c:when>
                                    <c:when test="${po.status == 'CONFIRMED'}"><span class="badge bg-primary">Đã chốt</span></c:when>
                                    <c:when test="${po.status == 'RECEIVED'}"><span class="badge bg-success">Đã nhập kho</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">${po.status}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <div class="d-flex gap-1 justify-content-center">
                                    <button type="button" class="btn btn-sm btn-outline-primary btn-edit-po"
                                            data-bs-toggle="modal" data-bs-target="#editStatusModal"
                                            data-id="${po.poId}"
                                            data-code="<c:out value='${po.poCode}'/>"
                                            data-status="${po.status}">
                                        <i class="fas fa-edit"></i> Trạng thái
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty purchases}">
                        <tr>
                            <td colspan="8" class="text-center text-muted py-4">Chưa có đơn nhập hàng nào.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<c:if test="${not empty supplierHistory}">
    <div class="card shadow-sm border-0 rounded-4 mt-4">
        <div class="card-body p-4">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                <h5 class="fw-bold mb-0"><i class="fas fa-receipt me-2 text-primary"></i>Lịch sử giao dịch & mốc xử lý đơn nhập (PO)</h5>
                <form action="${pageContext.request.contextPath}/admin/purchases" method="GET" class="d-flex gap-2 m-0">
                    <select name="supplierId" class="form-select form-select-sm" style="width: 220px;" onchange="this.form.submit()">
                        <option value="">Tất cả nhà cung cấp</option>
                        <c:forEach var="sup" items="${suppliers}">
                            <option value="${sup.supplierId}" ${selectedSupplierId == sup.supplierId ? 'selected' : ''}><c:out value="${sup.supplierName}"/></option>
                        </c:forEach>
                    </select>
                </form>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Mã PO</th>
                            <th>Nhà cung cấp</th>
                            <th>Trạng thái</th>
                            <th>Dự kiến giao</th>
                            <th>Chốt đơn</th>
                            <th>Nhận hàng</th>
                            <th class="text-end">SL đã trả</th>
                            <th class="text-end">Giá trị PO</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="h" items="${supplierHistory}">
                            <tr>
                                <td><code><c:out value="${h['poCode']}"/></code></td>
                                <td class="fw-semibold"><c:out value="${h['supplierName']}"/></td>
                                <td><span class="badge bg-secondary"><c:out value="${h['status']}"/></span></td>
                                <td><fmt:formatDate value="${h['expectedDeliveryDate']}" pattern="dd/MM/yyyy"/></td>
                                <td><small><fmt:formatDate value="${h['confirmedAt']}" pattern="dd/MM/yyyy HH:mm"/></small></td>
                                <td><small><fmt:formatDate value="${h['receivedAt']}" pattern="dd/MM/yyyy HH:mm"/></small></td>
                                <td class="text-end text-danger fw-semibold"><c:out value="${h['returnedQty']}"/></td>
                                <td class="text-end fw-bold"><fmt:formatNumber value="${h['totalCost']}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</c:if>

<!-- Modal Thêm PO -->
<div class="modal fade" id="addPoModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/purchases" method="POST">
                <input type="hidden" name="action" value="save">
                <div class="modal-header bg-white border-bottom border-primary border-3 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-plus-circle me-2"></i>Khởi tạo Đơn Nhập Hàng</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Mã Đơn Hàng (PO Code) *</label>
                        <input type="text" name="poCode" class="form-control" required placeholder="VD: PO-20261101-01">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Chọn Nhà Cung Cấp *</label>
                        <select name="supplierId" class="form-select" required>
                            <option value="">-- Chọn Nhà Cung Cấp --</option>
                            <c:forEach var="sup" items="${suppliers}">
                                <c:if test="${sup.status == 'ACTIVE'}">
                                    <option value="${sup.supplierId}">${sup.supplierName}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Ngày dự kiến giao hàng (ETA)</label>
                        <input type="date" name="expectedDeliveryDate" class="form-control">
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light py-3">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu Đơn</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal Cập nhật trạng thái -->
<div class="modal fade" id="editStatusModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/purchases" method="POST">
                <input type="hidden" name="action" value="updateStatus">
                <input type="hidden" name="poId" id="editPoId">
                <div class="modal-header bg-white border-bottom border-primary border-3 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-edit me-2"></i>Cập nhật Trạng Thái Đơn</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <p class="mb-3">Mã đơn: <strong id="displayPoCode" class="text-danger"></strong></p>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Trạng thái mới</label>
                        <select name="status" id="editPoStatus" class="form-select">
                            <option value="PENDING">Chờ xác nhận (PENDING)</option>
                            <option value="CONFIRMED">Đã chốt (CONFIRMED)</option>
                            <option value="RECEIVED">Đã nhập kho (RECEIVED)</option>
                            <option value="CANCELLED">Đã hủy (CANCELLED)</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light py-3">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Cập nhật</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    document.querySelectorAll('.btn-edit-po').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var d = btn.dataset;
            document.getElementById('editPoId').value = d.id;
            document.getElementById('displayPoCode').innerText = d.code;
            document.getElementById('editPoStatus').value = d.status;
        });
    });
</script>

<jsp:include page="/views/components/footer.jsp" />