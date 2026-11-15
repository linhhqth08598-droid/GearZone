<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<!-- NỘI DUNG CHÍNH (Đặt trực tiếp, không bọc row/container vì đã có user-content) -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <h4 class="fw-bold mb-0"><i class="fas fa-map-marker-alt text-danger me-2"></i>Địa chỉ giao hàng</h4>
    <button class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#addAddressModal">
        <i class="fas fa-plus me-1"></i> Thêm địa chỉ mới
    </button>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="fas fa-check-circle me-1"></i> <c:out value='${sessionScope.message}'/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show">
        <i class="fas fa-exclamation-circle me-1"></i> <c:out value='${sessionScope.error}'/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<c:choose>
    <c:when test="${empty addresses}">
        <div class="text-center py-5 bg-white rounded-4 shadow-sm">
            <img src="https://cdn-icons-png.flaticon.com/512/11329/11329060.png" alt="Empty" style="width: 100px; opacity: 0.5;" class="mb-3">
            <h5 class="text-muted">Bạn chưa có địa chỉ giao hàng nào.</h5>
            <p class="text-muted">Hãy thêm địa chỉ để việc mua sắm trở nên dễ dàng hơn.</p>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-3">
            <c:forEach var="addr" items="${addresses}">
                <div class="col-12">
                    <div class="card shadow-sm border-0 rounded-4">
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between align-items-start">
                                <div class="flex-grow-1">
                                    <h6 class="fw-bold text-dark mb-1">
                                        <c:out value='${addr.recipientName}'/>
                                        <c:if test="${addr.isDefault}">
                                            <span class="badge bg-success ms-2">Mặc định</span>
                                        </c:if>
                                    </h6>
                                    <p class="text-muted mb-1"><i class="fas fa-phone me-1"></i> <c:out value='${addr.phone}'/></p>
                                    <p class="text-secondary mb-0">
                                        <i class="fas fa-map-marker-alt me-1"></i> 
                                        <c:out value='${addr.detailedAddress}'/>, <c:out value='${addr.ward}'/>, <c:out value='${addr.province}'/>
                                    </p>
                                </div>
                                <div class="d-flex gap-2">
                                    <c:if test="${not addr.isDefault}">
                                        <form action="${pageContext.request.contextPath}/user" method="POST" class="m-0">
                                            <input type="hidden" name="action" value="setDefaultAddress">
                                            <input type="hidden" name="id" value="${addr.addressId}">
                                            <button type="submit" class="btn btn-sm btn-outline-success" title="Đặt làm mặc định">
                                                <i class="fas fa-check-circle"></i>
                                            </button>
                                        </form>
                                    </c:if>
                                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editAddressModal-${addr.addressId}">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <form action="${pageContext.request.contextPath}/user" method="POST" class="m-0">
                                        <input type="hidden" name="action" value="deleteAddress">
                                        <input type="hidden" name="id" value="${addr.addressId}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Xóa địa chỉ này?');">
                                            <i class="fas fa-trash-alt"></i>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Modal Sửa Địa Chỉ -->
                <div class="modal fade" id="editAddressModal-${addr.addressId}" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0 shadow-lg rounded-4">
                            <form action="${pageContext.request.contextPath}/user" method="POST" class="address-form">
                                <input type="hidden" name="action" value="saveAddress">
                                <input type="hidden" name="addressId" value="${addr.addressId}">
                                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                                    <h5 class="modal-title fw-bold text-primary">Sửa địa chỉ</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                </div>
                                <div class="modal-body p-4">
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Tên người nhận</label>
                                        <input type="text" name="recipientName" class="form-control" value="<c:out value='${addr.recipientName}'/>" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Số điện thoại</label>
                                        <input type="text" name="phone" class="form-control" value="<c:out value='${addr.phone}'/>" required>
                                    </div>
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <label class="form-label fw-semibold">Tỉnh/Thành</label>
                                            <input type="text" name="province" class="form-control" maxlength="100" placeholder="VD: Hà Nội" value="<c:out value='${addr.province}'/>" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-semibold">Phường/Xã</label>
                                            <input type="text" name="ward" class="form-control" maxlength="100" placeholder="VD: Phường Dịch Vọng" value="<c:out value='${addr.ward}'/>" required>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Số nhà / Thôn / Xóm</label>
                                        <input type="text" name="detailedAddress" class="form-control" maxlength="255" placeholder="VD: Số 12, thôn Đông" value="<c:out value='${addr.detailedAddress}'/>" required>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">Tên đường <span class="text-muted fw-normal">(không bắt buộc)</span></label>
                                        <input type="text" data-street class="form-control" maxlength="100" placeholder="VD: Đường Nguyễn Trãi">
                                        <div class="form-text">Nếu nhập, tên đường sẽ được ghép vào cuối địa chỉ chi tiết.</div>
                                    </div>
                                    <div class="form-check mb-2">
                                        <input class="form-check-input" type="checkbox" name="isDefault" id="isDefault-${addr.addressId}" ${addr.isDefault ? 'checked' : ''}>
                                        <label class="form-check-label fw-semibold" for="isDefault-${addr.addressId}">Đặt làm mặc định</label>
                                    </div>
                                </div>
                                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                                    <button type="submit" class="btn btn-primary px-4">Lưu thay đổi</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<!-- Modal Thêm Địa Chỉ Mới -->
<div class="modal fade" id="addAddressModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/user" method="POST" class="address-form">
                <input type="hidden" name="action" value="saveAddress">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary">Thêm địa chỉ mới</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tên người nhận</label>
                        <input type="text" name="recipientName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Số điện thoại</label>
                        <input type="text" name="phone" class="form-control" required>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Tỉnh/Thành</label>
                            <input type="text" name="province" class="form-control" maxlength="100" placeholder="VD: Hà Nội" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Phường/Xã</label>
                            <input type="text" name="ward" class="form-control" maxlength="100" placeholder="VD: Phường Dịch Vọng" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Số nhà / Thôn / Xóm</label>
                        <input type="text" name="detailedAddress" class="form-control" maxlength="255" placeholder="VD: Số 12, thôn Đông" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tên đường <span class="text-muted fw-normal">(không bắt buộc)</span></label>
                        <input type="text" data-street class="form-control" maxlength="100" placeholder="VD: Đường Nguyễn Trãi">
                        <div class="form-text">Nếu nhập, tên đường sẽ được ghép vào cuối địa chỉ chi tiết.</div>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="checkbox" name="isDefault" id="isDefaultAdd">
                        <label class="form-check-label fw-semibold" for="isDefaultAdd">Đặt làm mặc định</label>
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4">Thêm mới</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    // Gộp "Tên đường" (không bắt buộc) vào cuối "Số nhà / Thôn / Xóm" trước khi gửi.
    // detailed_address trong DB là nvarchar(255) NOT NULL nên không cần thêm cột mới.
    document.querySelectorAll('form.address-form').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            var detail = form.querySelector('[name="detailedAddress"]');
            var street = form.querySelector('[data-street]');
            var value = detail.value.trim();
            var st = street.value.trim();
            if (st) value = value + ', ' + st;
            if (value.length > 255) {
                e.preventDefault();
                alert('Địa chỉ chi tiết quá dài (tối đa 255 ký tự).');
                return;
            }
            detail.value = value;
            street.value = '';
        });
    });
</script>

<jsp:include page="/views/components/footer.jsp" />