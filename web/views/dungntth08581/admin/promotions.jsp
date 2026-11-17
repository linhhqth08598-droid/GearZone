<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<c:set var="base" value="${pageContext.request.contextPath}/admin/promotion"/>
<div class="container-fluid">
    <h3 class="fw-bold mb-3"><i class="fas fa-tags text-primary me-2"></i>Khuyến mãi & mã giảm giá</h3>

    <ul class="nav nav-pills mb-3">
        <li class="nav-item"><a class="nav-link ${tab == 'coupons' || tab == 'usage' ? 'active' : ''}" href="${base}?tab=coupons">Mã giảm giá</a></li>
        <li class="nav-item"><a class="nav-link ${tab == 'promotions' ? 'active' : ''}" href="${base}?tab=promotions">Khuyến mãi</a></li>
        <li class="nav-item"><a class="nav-link ${tab == 'combos' ? 'active' : ''}" href="${base}?tab=combos">Combo</a></li>
        <li class="nav-item"><a class="nav-link ${tab == 'broadcast' ? 'active' : ''}" href="${base}?tab=broadcast">Gửi thông báo</a></li>
    </ul>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show"><c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show"><c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <%-- ================= MÃ GIẢM GIÁ ================= --%>
    <c:if test="${tab == 'coupons'}">
        <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
            <h5 class="fw-bold mb-2" id="couponFormTitle">Tạo mã giảm giá</h5>
            <div class="small text-muted mb-2"><i class="fas fa-keyboard me-1"></i>Chỉ <b>mã giảm giá</b> mới được khách <b>nhập tay</b> ở ô \"Mã giảm giá\" khi đặt hàng. Khuyến mãi và combo thì tự áp dụng.</div>
            <form action="${base}" method="POST" id="couponForm" class="row g-2">
                <input type="hidden" name="action" value="saveCoupon">
                <input type="hidden" name="id" id="cId" value="">
                <div class="col-md-3"><label class="form-label small mb-1">Mã *</label><input name="code" id="cCode" class="form-control text-uppercase" maxlength="30" required placeholder="GEAR10"></div>
                <div class="col-md-5"><label class="form-label small mb-1">Tên chương trình *</label><input name="name" id="cName" class="form-control" maxlength="150" required></div>
                <div class="col-md-2"><label class="form-label small mb-1">Loại *</label>
                    <select name="type" id="cType" class="form-select"><option value="PERCENT">Giảm %</option><option value="FIXED">Giảm tiền</option><option value="FREE_SHIP">Miễn phí vận chuyển</option></select></div>
                <div class="col-md-2"><label class="form-label small mb-1">Giá trị</label><input name="value" id="cValue" class="form-control" type="number" step="any" min="0"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Đơn tối thiểu (₫)</label><input name="minOrder" id="cMin" class="form-control" type="number" min="0"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Giảm tối đa (₫)</label><input name="maxDiscount" id="cMax" class="form-control" type="number" min="0" placeholder="0 = không giới hạn"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Bắt đầu</label><input name="startDate" id="cStart" class="form-control" type="date"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Kết thúc</label><input name="endDate" id="cEnd" class="form-control" type="date"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Tổng lượt dùng</label><input name="usageLimit" id="cLimit" class="form-control" type="number" min="0" placeholder="0 = không giới hạn"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Lượt / khách</label><input name="perUserLimit" id="cPer" class="form-control" type="number" min="0" placeholder="0 = không giới hạn"></div>
                <div class="col-md-3"><label class="form-label small mb-1">Chỉ áp dụng danh mục</label>
                    <select name="categoryId" id="cCat" class="form-select"><option value="">Tất cả</option>
                        <c:forEach var="ca" items="${categories}"><option value="${ca.categoryId}"><c:out value="${ca.categoryName}"/></option></c:forEach></select></div>
                <div class="col-md-3"><label class="form-label small mb-1">Chỉ áp dụng thương hiệu</label>
                    <select name="brandId" id="cBrand" class="form-select"><option value="">Tất cả</option>
                        <c:forEach var="b" items="${brands}"><option value="${b.brandId}"><c:out value="${b.brandName}"/></option></c:forEach></select></div>
                <div class="col-12 d-flex gap-2">
                    <button class="btn btn-primary" type="submit"><i class="fas fa-save me-1"></i>Lưu</button>
                    <button class="btn btn-outline-secondary" type="button" onclick="resetCouponForm()">Làm mới</button>
                </div>
            </form>
        </div></div>

        <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3"><div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light"><tr><th>Mã</th><th>Chương trình</th><th>Giảm</th><th>Điều kiện</th><th>Thời gian</th><th>Đã dùng</th><th>Trạng thái</th><th style="min-width:200px">Thao tác</th></tr></thead>
                <tbody>
                <c:forEach var="c" items="${coupons}">
                    <tr>
                        <td class="fw-bold"><c:out value="${c.code}"/></td>
                        <td><c:out value="${c.name}"/>
                            <c:if test="${not empty c.categoryName}"><div class="small text-muted">Danh mục: <c:out value="${c.categoryName}"/></div></c:if>
                            <c:if test="${not empty c.brandName}"><div class="small text-muted">Thương hiệu: <c:out value="${c.brandName}"/></div></c:if></td>
                        <td><c:choose>
                            <c:when test="${c.type == 'PERCENT'}">${c.value}%<c:if test="${c.maxDiscount > 0}"> <span class="small text-muted">(tối đa <fmt:formatNumber value="${c.maxDiscount}" maxFractionDigits="0"/> ₫)</span></c:if></c:when>
                            <c:when test="${c.type == 'FIXED'}"><fmt:formatNumber value="${c.value}" maxFractionDigits="0"/> ₫</c:when>
                            <c:otherwise>Miễn phí vận chuyển</c:otherwise></c:choose></td>
                        <td class="small">Đơn từ <fmt:formatNumber value="${c.minOrder}" maxFractionDigits="0"/> ₫<br>${c.perUserLimit > 0 ? c.perUserLimit : '∞'} lượt/khách</td>
                        <td class="small"><fmt:formatDate value="${c.startDate}" pattern="dd/MM/yyyy"/> - <fmt:formatDate value="${c.endDate}" pattern="dd/MM/yyyy"/></td>
                        <td>${c.usedCount}<c:if test="${c.usageLimit > 0}"> / ${c.usageLimit}</c:if></td>
                        <td><span class="badge ${c.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">${c.status == 'ACTIVE' ? 'Đang bật' : 'Đã tắt'}</span></td>
                        <td><div class="d-flex gap-1 flex-wrap">
                            <button type="button" class="btn btn-sm btn-outline-primary btn-edit-coupon"
                                data-id="${c.id}" data-code="${c.code}" data-name="${fn:escapeXml(c.name)}" data-type="${c.type}" data-value="${c.value}" data-min="${c.minOrder}"
                                data-max="${c.maxDiscount}" data-start="<fmt:formatDate value='${c.startDate}' pattern='yyyy-MM-dd'/>" data-end="<fmt:formatDate value='${c.endDate}' pattern='yyyy-MM-dd'/>"
                                data-limit="${c.usageLimit}" data-per="${c.perUserLimit}" data-cat="${c.categoryId}" data-brand="${c.brandId}">Sửa</button>
                            <a class="btn btn-sm btn-outline-info" href="${base}?tab=usage&id=${c.id}">Lịch sử</a>
                            <form action="${base}" method="POST" class="m-0"><input type="hidden" name="action" value="toggleCoupon"><input type="hidden" name="id" value="${c.id}">
                                <button class="btn btn-sm ${c.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}" type="submit">${c.status == 'ACTIVE' ? 'Tắt' : 'Bật'}</button></form>
                        </div></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty coupons}"><tr><td colspan="8" class="text-center text-muted py-4">Chưa có mã giảm giá nào.</td></tr></c:if>
                </tbody>
            </table>
        </div></div></div>
    </c:if>

    <%-- ================= LỊCH SỬ DÙNG MÃ (UC 248) ================= --%>
    <c:if test="${tab == 'usage'}">
        <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h5 class="fw-bold mb-0">Lịch sử dùng mã <c:out value="${coupon.code}"/></h5>
                <a href="${base}?tab=coupons" class="btn btn-outline-secondary btn-sm">Quay lại</a>
            </div>
            <div class="table-responsive"><table class="table align-middle">
                <thead class="table-light"><tr><th>Mã đơn</th><th>Khách hàng</th><th class="text-end">Số tiền giảm</th><th>Ngày đặt</th><th>Trạng thái đơn</th></tr></thead>
                <tbody>
                <c:forEach var="u" items="${usage}">
                    <tr><td class="fw-bold"><c:out value="${u.orderCode}"/></td><td><c:out value="${u.customerName}"/></td>
                        <td class="text-end"><fmt:formatNumber value="${u.discount}" maxFractionDigits="0"/> ₫</td>
                        <td><fmt:formatDate value="${u.usedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                        <td><jsp:include page="/views/components/order_status_badge.jsp"><jsp:param name="status" value="${u.orderStatus}"/></jsp:include></td></tr>
                </c:forEach>
                <c:if test="${empty usage}"><tr><td colspan="5" class="text-center text-muted py-4">Mã này chưa được dùng.</td></tr></c:if>
                </tbody>
            </table></div>
        </div></div>
    </c:if>

    <%-- ================= KHUYẾN MÃI ================= --%>
    <c:if test="${tab == 'promotions'}">
        <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
            <h5 class="fw-bold mb-2">Tạo khuyến mãi (giảm tự động)</h5>
            <div class="alert alert-info py-2 small"><i class="fas fa-info-circle me-1"></i>Khuyến mãi <b>không có mã để nhập</b>: khi còn hạn và giỏ hàng có đúng đối tượng, hệ thống <b>tự trừ</b> vào đơn ở bước thanh toán. Cột <b>Mã</b> (KM-số) chỉ để quản trị nhận biết. Muốn khách nhập mã, hãy tạo ở tab <a href="${base}?tab=coupons">Mã giảm giá</a>.</div>
            <form action="${base}" method="POST" class="row g-2">
                <input type="hidden" name="action" value="savePromotion">
                <div class="col-md-4"><label class="form-label small mb-1">Tên khuyến mãi *</label><input name="name" class="form-control" maxlength="150" required></div>
                <div class="col-md-2"><label class="form-label small mb-1">Áp dụng cho *</label>
                    <select name="targetKind" id="pKind" class="form-select"><option value="CATEGORY">Danh mục</option><option value="BRAND">Thương hiệu</option><option value="PART">Linh kiện (nhập mã)</option></select></div>
                <div class="col-md-3"><label class="form-label small mb-1">Đối tượng *</label>
                    <div class="pTarget" id="pCatBox"><select id="pCat" class="form-select"><c:forEach var="ca" items="${categories}"><option value="${ca.categoryId}"><c:out value="${ca.categoryName}"/></option></c:forEach></select></div>
                    <div class="pTarget d-none" id="pBrandBox"><select id="pBrand" class="form-select"><c:forEach var="b" items="${brands}"><option value="${b.brandId}"><c:out value="${b.brandName}"/></option></c:forEach></select></div>
                    <div class="pTarget d-none" id="pPartBox"><input id="pPart" type="number" min="1" class="form-control" placeholder="part_id của linh kiện"></div>
                    <input type="hidden" name="targetId" id="pTargetId"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Loại *</label>
                    <select name="type" class="form-select"><option value="PERCENT">Giảm %</option><option value="FIXED">Giảm tiền</option></select></div>
                <div class="col-md-1"><label class="form-label small mb-1">Giá trị *</label><input name="value" type="number" step="any" min="0" class="form-control" required></div>
                <div class="col-md-2"><label class="form-label small mb-1">Đơn tối thiểu (₫)</label><input name="minOrder" type="number" min="0" class="form-control"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Bắt đầu</label><input name="startDate" type="date" class="form-control"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Kết thúc</label><input name="endDate" type="date" class="form-control"></div>
                <div class="col-12"><button class="btn btn-primary" type="submit"><i class="fas fa-save me-1"></i>Tạo khuyến mãi</button></div>
            </form>
        </div></div>
        <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3"><div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light"><tr><th>Mã</th><th>Tên</th><th>Đối tượng</th><th>Giảm</th><th>Thời gian</th><th>Trạng thái</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="p" items="${promotions}">
                    <tr><td class="fw-bold text-nowrap">KM-${p.id}<div><span class="badge bg-info text-dark fw-normal">Tự động</span></div></td><td class="fw-semibold"><c:out value="${p.name}"/></td>
                        <td><span class="badge bg-light text-dark border"><c:out value="${p.type}"/></span> <c:out value="${p.targetName}"/></td>
                        <td><c:choose><c:when test="${p.discountType == 'PERCENT'}">${p.value}%</c:when><c:otherwise><fmt:formatNumber value="${p.value}" maxFractionDigits="0"/> ₫</c:otherwise></c:choose></td>
                        <td class="small"><fmt:formatDate value="${p.startDate}" pattern="dd/MM/yyyy"/> - <fmt:formatDate value="${p.endDate}" pattern="dd/MM/yyyy"/></td>
                        <td><span class="badge ${p.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">${p.status == 'ACTIVE' ? 'Đang bật' : 'Đã tắt'}</span></td>
                        <td><form action="${base}" method="POST" class="m-0"><input type="hidden" name="action" value="togglePromotion"><input type="hidden" name="id" value="${p.id}">
                            <button class="btn btn-sm ${p.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}" type="submit">${p.status == 'ACTIVE' ? 'Tắt' : 'Bật'}</button></form></td></tr>
                </c:forEach>
                <c:if test="${empty promotions}"><tr><td colspan="7" class="text-center text-muted py-4">Chưa có khuyến mãi nào.</td></tr></c:if>
                </tbody>
            </table>
        </div></div></div>
    </c:if>

    <%-- ================= COMBO ================= --%>
    <c:if test="${tab == 'combos'}">
        <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
            <h5 class="fw-bold mb-2">Tạo combo / ưu đãi trọn bộ PC</h5>
            <div class="alert alert-info py-2 small"><i class="fas fa-info-circle me-1"></i>Combo <b>không có mã để nhập</b>: khi giỏ đủ điều kiện (đủ số món; \"Trọn bộ PC\" cần thêm CPU + Mainboard + RAM + Nguồn) hệ thống <b>tự trừ</b> vào đơn, lấy combo giảm nhiều nhất. Cột <b>Mã</b> (CB-số) chỉ để quản trị nhận biết.</div>
            <form action="${base}" method="POST" class="row g-2">
                <input type="hidden" name="action" value="saveCombo">
                <div class="col-md-4"><label class="form-label small mb-1">Tên combo *</label><input name="name" class="form-control" maxlength="150" required></div>
                <div class="col-md-2"><label class="form-label small mb-1">Loại *</label>
                    <select name="type" class="form-select"><option value="PART_COMBO">Combo linh kiện</option><option value="FULL_BUILD">Trọn bộ PC</option></select></div>
                <div class="col-md-2"><label class="form-label small mb-1">Giảm (%) *</label><input name="percent" type="number" step="any" min="0" max="100" class="form-control" required></div>
                <div class="col-md-2"><label class="form-label small mb-1">Tối thiểu (món) *</label><input name="minParts" type="number" min="2" value="2" class="form-control" required></div>
                <div class="col-md-2"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Bắt đầu</label><input name="startDate" type="date" class="form-control"></div>
                <div class="col-md-2"><label class="form-label small mb-1">Kết thúc</label><input name="endDate" type="date" class="form-control"></div>
                <div class="col-12"><button class="btn btn-primary" type="submit"><i class="fas fa-save me-1"></i>Tạo combo</button>
                    <span class="small text-muted ms-2">Combo được tự trừ vào đơn khi khách thanh toán.</span></div>
            </form>
        </div></div>
        <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3"><div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light"><tr><th>Mã</th><th>Tên</th><th>Loại</th><th>Giảm</th><th>Tối thiểu</th><th>Thời gian</th><th>Trạng thái</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="cb" items="${combos}">
                    <tr><td class="fw-bold text-nowrap">CB-${cb.id}<div><span class="badge bg-info text-dark fw-normal">Tự động</span></div></td><td class="fw-semibold"><c:out value="${cb.name}"/></td><td><c:out value="${cb.type == 'FULL_BUILD' ? 'Trọn bộ PC' : 'Combo linh kiện'}"/></td><td>${cb.percent}%</td><td>${cb.minParts} món</td>
                        <td class="small"><fmt:formatDate value="${cb.startDate}" pattern="dd/MM/yyyy"/> - <fmt:formatDate value="${cb.endDate}" pattern="dd/MM/yyyy"/></td>
                        <td><span class="badge ${cb.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">${cb.status == 'ACTIVE' ? 'Đang bật' : 'Đã tắt'}</span></td>
                        <td><form action="${base}" method="POST" class="m-0"><input type="hidden" name="action" value="toggleCombo"><input type="hidden" name="id" value="${cb.id}">
                            <button class="btn btn-sm ${cb.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}" type="submit">${cb.status == 'ACTIVE' ? 'Tắt' : 'Bật'}</button></form></td></tr>
                </c:forEach>
                <c:if test="${empty combos}"><tr><td colspan="8" class="text-center text-muted py-4">Chưa có combo nào.</td></tr></c:if>
                </tbody>
            </table>
        </div></div></div>
    </c:if>

    <%-- ================= GỬI THÔNG BÁO (UC 250) ================= --%>
    <c:if test="${tab == 'broadcast'}">
        <div class="card border-0 shadow-sm rounded-4" style="max-width:720px"><div class="card-body p-3">
            <h5 class="fw-bold mb-3">Gửi thông báo khuyến mãi đến tất cả khách hàng</h5>
            <form action="${base}" method="POST" onsubmit="return confirm('Gửi thông báo này đến toàn bộ khách hàng?');">
                <input type="hidden" name="action" value="broadcast">
                <div class="mb-2"><label class="form-label small mb-1">Tiêu đề *</label><input name="title" class="form-control" maxlength="150" required></div>
                <div class="mb-3"><label class="form-label small mb-1">Nội dung *</label><textarea name="content" class="form-control" rows="4" maxlength="1000" required></textarea></div>
                <button class="btn btn-primary" type="submit"><i class="fas fa-paper-plane me-1"></i>Gửi</button>
            </form>
        </div></div>
    </c:if>
</div>

<script>
(function () {
    var $ = function (id) { return document.getElementById(id); };
    window.resetCouponForm = function () {
        var f = $('couponForm'); if (!f) return;
        f.reset(); $('cId').value = ''; $('cCode').readOnly = false; $('couponFormTitle').textContent = 'Tạo mã giảm giá';
    };
    document.querySelectorAll('.btn-edit-coupon').forEach(function (b) {
        b.addEventListener('click', function () {
            var d = b.dataset;
            $('cId').value = d.id; $('cCode').value = d.code; $('cCode').readOnly = true; $('cName').value = d.name; $('cType').value = d.type;
            $('cValue').value = d.value; $('cMin').value = d.min; $('cMax').value = d.max; $('cStart').value = d.start; $('cEnd').value = d.end;
            $('cLimit').value = d.limit; $('cPer').value = d.per; $('cCat').value = d.cat || ''; $('cBrand').value = d.brand || '';
            $('couponFormTitle').textContent = 'Sửa mã ' + d.code;
            window.scrollTo({top: 0, behavior: 'smooth'});
        });
    });
    var kind = $('pKind');
    if (kind) {
        var sync = function () {
            var map = {CATEGORY: 'pCat', BRAND: 'pBrand', PART: 'pPart'}, box = {CATEGORY: 'pCatBox', BRAND: 'pBrandBox', PART: 'pPartBox'};
            document.querySelectorAll('.pTarget').forEach(function (e) { e.classList.add('d-none'); });
            var cur = $(map[kind.value]); $(box[kind.value]).classList.remove('d-none');
            $('pTargetId').value = cur.value;
            cur.oninput = cur.onchange = function () { $('pTargetId').value = cur.value; };
        };
        kind.addEventListener('change', sync); sync();
    }
})();
</script>

<jsp:include page="/views/components/footer.jsp" />
