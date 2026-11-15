<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="container-fluid">
    <h3 class="fw-bold mb-3"><i class="fas fa-truck text-primary me-2"></i>Phương thức vận chuyển</h3>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
        <h5 class="fw-bold mb-3" id="shipTitle">Thêm phương thức</h5>
        <form action="${pageContext.request.contextPath}/admin/shipping" method="POST" id="shipForm" class="row g-2">
            <input type="hidden" name="id" id="sId" value="">
            <div class="col-md-2"><label class="form-label small mb-1">Mã *</label><input name="code" id="sCode" class="form-control text-uppercase" maxlength="30" required placeholder="GHN_STD"></div>
            <div class="col-md-3"><label class="form-label small mb-1">Tên *</label><input name="name" id="sName" class="form-control" maxlength="100" required></div>
            <div class="col-md-2"><label class="form-label small mb-1">Đơn vị vận chuyển</label><input name="carrier" id="sCarrier" class="form-control" maxlength="30"></div>
            <div class="col-md-2"><label class="form-label small mb-1">Phí (₫) *</label><input name="fee" id="sFee" type="number" min="0" class="form-control" required></div>
            <div class="col-md-1"><label class="form-label small mb-1">Số ngày</label><input name="days" id="sDays" type="number" min="0" max="60" class="form-control"></div>
            <div class="col-md-2 d-flex align-items-end gap-1">
                <button class="btn btn-primary" type="submit">Lưu</button>
                <button class="btn btn-outline-secondary" type="button" id="sReset">Làm mới</button>
            </div>
        </form>
    </div></div>

    <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3"><div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-light"><tr><th>Mã</th><th>Tên</th><th>Đơn vị</th><th class="text-end">Phí</th><th>Dự kiến</th><th>Trạng thái</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="m" items="${methods}">
                <tr>
                    <td class="fw-bold"><c:out value="${m.code}"/></td><td><c:out value="${m.name}"/></td><td><c:out value="${m.carrier}"/></td>
                    <td class="text-end"><fmt:formatNumber value="${m.fee}" maxFractionDigits="0"/> ₫</td>
                    <td><c:if test="${m.days != null}">${m.days} ngày</c:if></td>
                    <td><span class="badge ${m.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">${m.status == 'ACTIVE' ? 'Đang bật' : 'Đã tắt'}</span></td>
                    <td><div class="d-flex gap-1">
                        <button type="button" class="btn btn-sm btn-outline-primary btn-edit-ship" data-id="${m.id}" data-code="${m.code}" data-name="${fn:escapeXml(m.name)}" data-carrier="${m.carrier}" data-fee="${m.fee}" data-days="${m.days}">Sửa</button>
                        <form action="${pageContext.request.contextPath}/admin/shipping" method="POST" class="m-0"><input type="hidden" name="action" value="toggle"><input type="hidden" name="id" value="${m.id}">
                            <button class="btn btn-sm ${m.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}" type="submit">${m.status == 'ACTIVE' ? 'Tắt' : 'Bật'}</button></form>
                    </div></td>
                </tr>
            </c:forEach>
            <c:if test="${empty methods}"><tr><td colspan="7" class="text-center text-muted py-4">Chưa có phương thức vận chuyển nào.</td></tr></c:if>
            </tbody>
        </table>
    </div></div></div>
</div>

<script>
(function () {
    var $ = function (id) { return document.getElementById(id); };
    function reset() { $('shipForm').reset(); $('sId').value = ''; $('sCode').readOnly = false; $('shipTitle').textContent = 'Thêm phương thức'; }
    $('sReset').addEventListener('click', reset);
    document.querySelectorAll('.btn-edit-ship').forEach(function (b) {
        b.addEventListener('click', function () {
            var d = b.dataset;
            $('sId').value = d.id; $('sCode').value = d.code; $('sCode').readOnly = true; $('sName').value = d.name;
            $('sCarrier').value = d.carrier; $('sFee').value = d.fee; $('sDays').value = d.days;
            $('shipTitle').textContent = 'Sửa ' + d.code; window.scrollTo({top: 0, behavior: 'smooth'});
        });
    });
})();
</script>

<jsp:include page="/views/components/footer.jsp" />
