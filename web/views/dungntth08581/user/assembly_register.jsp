<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="fw-bold mb-0"><i class="fas fa-tools text-danger me-2"></i>Đăng ký dịch vụ lắp ráp</h4>
        <a href="${pageContext.request.contextPath}/assembly" class="btn btn-outline-secondary">Đơn lắp ráp của tôi</a>
    </div>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-4">
        <form action="${pageContext.request.contextPath}/assembly" method="POST">
            <input type="hidden" name="action" value="register">

            <h6 class="fw-bold">1. Chọn hình thức</h6>
            <div class="row g-2 mb-4">
                <div class="col-md-6"><label class="border rounded-3 p-3 d-block h-100">
                    <input type="radio" name="type" value="AT_STORE" ${empty type || type == 'AT_STORE' ? 'checked' : ''}> <strong>Lắp tại cửa hàng</strong> – 150.000 ₫
                    <div class="small text-muted">Bạn mang/đặt linh kiện, kỹ thuật viên lắp tại GearZone, hẹn ngày đến lấy.</div></label></div>
                <div class="col-md-6"><label class="border rounded-3 p-3 d-block h-100">
                    <input type="radio" name="type" value="DELIVER_ASSEMBLED" ${type == 'DELIVER_ASSEMBLED' ? 'checked' : ''}> <strong>Lắp xong, giao PC tận nơi</strong> – 250.000 ₫
                    <div class="small text-muted">Lắp, kiểm tra xong rồi giao PC về địa chỉ của bạn.</div></label></div>
            </div>

            <h6 class="fw-bold">2. Chọn PC cần lắp <span class="text-muted fw-normal small">(chọn ít nhất 1)</span></h6>
            <div class="row g-3 mb-4">
                <div class="col-md-6">
                    <label class="form-label small text-muted">Từ đơn hàng đã đặt</label>
                    <select name="orderId" class="form-select">
                        <option value="">-- Không chọn --</option>
                        <c:forEach var="o" items="${orders}"><option value="${o.id}"><c:out value="${o.label}"/></option></c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Hoặc từ cấu hình PC đã lưu</label>
                    <select name="buildId" class="form-select">
                        <option value="">-- Không chọn --</option>
                        <c:forEach var="b" items="${builds}"><option value="${b.id}"><c:out value="${b.label}"/></option></c:forEach>
                    </select>
                </div>
            </div>

            <h6 class="fw-bold">3. Đặt lịch</h6>
            <div class="row g-3 mb-4">
                <div class="col-md-6">
                    <label class="form-label small text-muted">Ngày giờ hẹn (08:00–19:00, sau hiện tại ít nhất 2 giờ)</label>
                    <input type="datetime-local" name="when" class="form-control" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label small text-muted">Ghi chú (không bắt buộc)</label>
                    <input type="text" name="note" maxlength="500" class="form-control" placeholder="Yêu cầu riêng, tên người liên hệ...">
                </div>
            </div>
            <button type="submit" class="btn btn-danger"><i class="fas fa-calendar-check me-1"></i>Đăng ký &amp; đặt lịch</button>
        </form>
    </div></div>
</div>

<jsp:include page="/views/components/footer.jsp" />
