<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-handshake me-2"></i>Quản lý Nhà Cung Cấp</h1>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addSupplierModal">
        <i class="fas fa-plus me-1"></i> Thêm Nhà Cung Cấp
    </button>
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

<!-- Form Tìm kiếm & Lọc (Giống products.jsp) -->
<div class="card shadow-sm border-0 rounded-4 mb-4">
    <div class="card-body p-3">
        <form action="${pageContext.request.contextPath}/admin/suppliers" method="GET" class="row g-3 align-items-center">

            <!-- Ô tìm kiếm -->
            <div class="col-md-6">
                <div class="input-group">
                    <span class="input-group-text bg-white"><i class="fas fa-search text-muted"></i></span>
                    <input type="text" name="keyword" value="${paramKeyword}" class="form-control" placeholder="Tìm tên nhà cung cấp, SĐT, Email...">
                </div>
            </div>

            <!-- Lọc theo Trạng thái -->
            <div class="col-md-4">
                <select name="status" class="form-select">
                    <option value="">-- Tất cả trạng thái --</option>
                    <option value="ACTIVE" ${paramStatus == 'ACTIVE' ? 'selected' : ''}>Đang hợp tác</option>
                    <option value="INACTIVE" ${paramStatus == 'INACTIVE' ? 'selected' : ''}>Ngừng hợp tác</option>
                </select>
            </div>

            <!-- Nút Action -->
            <div class="col-md-2 d-flex gap-2">
                <button type="submit" class="btn btn-primary w-100"><i class="fas fa-filter me-1"></i> Lọc</button>
                <a href="${pageContext.request.contextPath}/admin/suppliers" class="btn btn-outline-secondary" title="Hủy lọc"><i class="fas fa-sync-alt"></i></a>
            </div>
        </form>
    </div>
</div>

<!-- Bảng dữ liệu Nhà Cung Cấp -->
<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="text-muted small mb-2">Tìm thấy <strong>${fn:length(suppliers)}</strong> nhà cung cấp</div>
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Tên Nhà Cung Cấp</th>
                        <th>Người Liên Hệ</th>
                        <th>Điện Thoại</th>
                        <th>Email</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${suppliers}">
                        <tr>
                            <td>#${s.supplierId}</td>
                            <td class="fw-bold"><c:out value="${s.supplierName}"/></td>
                            <td><c:out value="${s.contactName}"/></td>
                            <td><c:out value="${s.phone}"/></td>
                            <td><c:out value="${s.email}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${s.status == 'ACTIVE'}"><span class="badge bg-success">Đang hợp tác</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">Ngừng hợp tác</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <button type="button" class="btn btn-sm btn-outline-primary btn-edit-supplier"
                                        data-bs-toggle="modal" data-bs-target="#editSupplierModal"
                                        data-id="${s.supplierId}"
                                        data-name="<c:out value='${s.supplierName}'/>"
                                        data-contact="<c:out value='${s.contactName}'/>"
                                        data-phone="<c:out value='${s.phone}'/>"
                                        data-email="<c:out value='${s.email}'/>"
                                        data-address="<c:out value='${s.address}'/>"
                                        data-status="${s.status}">
                                    <i class="fas fa-edit"></i> Sửa
                                </button>
                                <form action="${pageContext.request.contextPath}/admin/suppliers" method="POST" class="d-inline m-0"
                                      onsubmit="return confirm('Bạn có chắc muốn xóa nhà cung cấp này?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="supplierId" value="${s.supplierId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">
                                        <i class="fas fa-trash"></i> Xóa
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty suppliers}">
                        <tr>
                            <td colspan="7" class="text-center text-muted py-4">Không tìm thấy nhà cung cấp nào phù hợp.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal Thêm mới -->
<div class="modal fade" id="addSupplierModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/suppliers" method="POST">
                <input type="hidden" name="action" value="save">
                <div class="modal-header bg-white border-bottom border-primary border-3 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-plus-circle me-2"></i>Thêm Nhà Cung Cấp</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tên Nhà cung cấp *</label>
                        <input type="text" name="supplierName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Người liên hệ</label>
                        <input type="text" name="contactName" class="form-control">
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Số điện thoại *</label>
                            <input type="text" name="phone" class="form-control" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Email</label>
                            <input type="email" name="email" class="form-control">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Địa chỉ</label>
                        <textarea name="address" class="form-control" rows="2"></textarea>
                    </div>
                </div>
                <div class="modal-footer bg-light py-3">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu thông tin</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal Sửa -->
<div class="modal fade" id="editSupplierModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/suppliers" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="supplierId" id="editId">
                <div class="modal-header bg-white border-bottom border-primary border-3 py-3">
                    <h5 class="modal-title fw-bold text-primary"><i class="fas fa-edit me-2"></i>Cập nhật Nhà Cung Cấp</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tên Nhà cung cấp *</label>
                        <input type="text" name="supplierName" id="editName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Người liên hệ</label>
                        <input type="text" name="contactName" id="editContact" class="form-control">
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Số điện thoại *</label>
                            <input type="text" name="phone" id="editPhone" class="form-control" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Email</label>
                            <input type="email" name="email" id="editEmail" class="form-control">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Địa chỉ</label>
                        <textarea name="address" id="editAddress" class="form-control" rows="2"></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Trạng thái</label>
                        <select name="status" id="editStatus" class="form-select">
                            <option value="ACTIVE">Đang hợp tác</option>
                            <option value="INACTIVE">Ngừng hợp tác</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer bg-light py-3">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    document.querySelectorAll('.btn-edit-supplier').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var d = btn.dataset;
            document.getElementById('editId').value = d.id;
            document.getElementById('editName').value = d.name;
            document.getElementById('editContact').value = d.contact;
            document.getElementById('editPhone').value = d.phone;
            document.getElementById('editEmail').value = d.email;
            document.getElementById('editAddress').value = d.address;
            document.getElementById('editStatus').value = d.status;
        });
    });
</script>

<jsp:include page="/views/components/footer.jsp" />