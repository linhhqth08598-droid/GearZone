<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h3 class="fw-bold mb-0"><i class="fas fa-tools text-primary me-2"></i>Đơn lắp ráp #${a.assemblyId}
            <span class="badge ${a.status == 'CANCELLED' ? 'bg-danger' : 'bg-info text-dark'} fs-6 ms-2"><c:out value="${a.status}"/></span></h3>
        <a href="${pageContext.request.contextPath}/admin/assembly" class="btn btn-outline-secondary">Quay lại danh sách</a>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="row g-3">
        <div class="col-lg-5">
            <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
                <div><strong>Khách:</strong> <c:out value="${a.customerName}"/> – <c:out value="${a.phone}"/></div>
                <div><strong>Hình thức:</strong> <c:out value="${a.typeLabel}"/></div>
                <div><strong>Nguồn:</strong> <c:if test="${not empty a.orderId}">Đơn #${a.orderId} </c:if><c:if test="${not empty a.buildId}">Cấu hình #${a.buildId}</c:if></div>
                <div><strong>Ngày hẹn:</strong> <fmt:formatDate value="${a.scheduledDate}" pattern="dd/MM/yyyy HH:mm"/></div>
                <div><strong>Phí:</strong> <fmt:formatNumber value="${a.fee}" type="number" maxFractionDigits="0"/> ₫</div>
                <c:if test="${not empty a.note}"><div><strong>Ghi chú:</strong> <c:out value="${a.note}"/></div></c:if>
            </div></div>

            <!-- UC 256: phân công kỹ thuật viên -->
            <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
                <h6 class="fw-bold">Kỹ thuật viên: <c:out value="${empty a.technicianName ? 'chưa phân công' : a.technicianName}"/></h6>
                <c:if test="${a.status == 'CONFIRMED' || a.status == 'IN_PROGRESS' || a.status == 'TESTING'}">
                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="d-flex gap-2 m-0">
                        <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="assign"><input type="hidden" name="detail" value="1">
                        <select name="technicianId" class="form-select" required>
                            <option value="">-- Chọn kỹ thuật viên --</option>
                            <c:forEach var="t" items="${technicians}"><option value="${t.id}" ${t.id == a.technicianId ? 'selected' : ''}><c:out value="${t.label}"/></option></c:forEach>
                        </select>
                        <button type="submit" class="btn btn-primary text-nowrap">Phân công</button>
                    </form>
                </c:if>
            </div></div>

            <!-- UC 258: cập nhật tiến độ -->
            <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
                <h6 class="fw-bold">Tiến độ</h6>
                <div class="small text-muted mb-2">Đã xác nhận lịch → Đang lắp → Đang kiểm tra → Hoàn tất → Bàn giao (2 bước cuối ở đợt D3)</div>
                <c:if test="${a.status == 'CONFIRMED'}">
                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="m-0">
                        <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="advance"><input type="hidden" name="next" value="IN_PROGRESS"><input type="hidden" name="detail" value="1">
                        <button type="submit" class="btn btn-success">Bắt đầu lắp</button>
                    </form>
                </c:if>
                <c:if test="${a.status == 'IN_PROGRESS'}">
                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="m-0">
                        <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="advance"><input type="hidden" name="next" value="TESTING"><input type="hidden" name="detail" value="1">
                        <button type="submit" class="btn btn-success">Lắp xong – chuyển sang kiểm tra</button>
                    </form>
                </c:if>
                <c:if test="${a.status == 'SCHEDULED'}"><div class="text-muted small">Cần xác nhận lịch ở danh sách trước.</div></c:if>
            </div></div>
        </div>

        <!-- UC 257: linh kiện sử dụng -->
        <div class="col-lg-7">
            <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
                <h6 class="fw-bold">Linh kiện cần lắp</h6>
                <table class="table table-sm align-middle mb-0">
                    <thead class="table-light"><tr><th>Linh kiện</th><th class="text-center">Cần</th></tr></thead>
                    <tbody>
                        <c:forEach var="p" items="${source}"><tr><td><c:out value="${p.partName}"/></td><td class="text-center">${p.quantity}</td></tr></c:forEach>
                        <c:if test="${empty source}"><tr><td colspan="2" class="text-center text-muted">Đơn này không gắn đơn hàng/cấu hình có linh kiện.</td></tr></c:if>
                    </tbody>
                </table>
            </div></div>

            <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
                <h6 class="fw-bold">Linh kiện đã dùng</h6>
                <table class="table table-sm align-middle">
                    <thead class="table-light"><tr><th>Linh kiện</th><th class="text-center">SL</th><th>Serial</th><th></th></tr></thead>
                    <tbody>
                        <c:forEach var="u" items="${used}">
                            <tr><td><c:out value="${u.partName}"/></td><td class="text-center">${u.quantity}</td><td><c:out value="${u.serial}"/></td>
                                <td class="text-end">
                                    <c:if test="${a.status == 'IN_PROGRESS' || a.status == 'TESTING'}">
                                        <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="m-0" onsubmit="return confirm('Xóa dòng này?');">
                                            <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="removePart"><input type="hidden" name="usedId" value="${u.usedId}"><input type="hidden" name="detail" value="1">
                                            <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
                                        </form>
                                    </c:if>
                                </td></tr>
                        </c:forEach>
                        <c:if test="${empty used}"><tr><td colspan="4" class="text-center text-muted">Chưa ghi linh kiện nào.</td></tr></c:if>
                    </tbody>
                </table>
                <c:if test="${a.status == 'IN_PROGRESS' || a.status == 'TESTING'}">
                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="row g-2 m-0">
                        <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="addPart"><input type="hidden" name="detail" value="1">
                        <div class="col-md-5"><select name="partId" class="form-select" required>
                            <option value="">-- Chọn linh kiện --</option>
                            <c:forEach var="p" items="${source}"><option value="${p.partId}"><c:out value="${p.partName}"/></option></c:forEach>
                        </select></div>
                        <div class="col-md-2"><input type="number" name="qty" value="1" min="1" class="form-control" required></div>
                        <div class="col-md-3"><input type="text" name="serial" maxlength="100" class="form-control" placeholder="Số serial (nếu có)"></div>
                        <div class="col-md-2"><button type="submit" class="btn btn-primary w-100">Ghi</button></div>
                    </form>
                </c:if>
            </div></div>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />
