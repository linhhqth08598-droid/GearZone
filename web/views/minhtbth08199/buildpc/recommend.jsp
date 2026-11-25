<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />
<div class="container py-5">
 <div class="row justify-content-center"><div class="col-lg-9">
  <div class="p-4 p-md-5 rounded-4 text-white mb-4" style="background:linear-gradient(125deg,#151d2e,#29446c)">
   <span class="badge text-bg-danger mb-2">GEARZONE PC BUILDER</span><h1 class="fw-bold">Tìm cấu hình phù hợp</h1>
   <p class="mb-0 text-white-50">Cho GearZone biết mục đích sử dụng và ngân sách để được gợi ý cấu hình.</p>
  </div>
  <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>
  <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-4 p-md-5">
   <h3 class="fw-bold mb-1">Nhu cầu sử dụng</h3><p class="text-muted mb-4">Dành cho mọi nhu cầu, không cần chọn game cụ thể.</p>
   <form method="post" action="${pageContext.request.contextPath}/recommend-pc"><div class="row g-3">
    <div class="col-md-6"><label class="form-label fw-semibold">Mục đích chính *</label><select class="form-select" name="purpose" required>
     <option value="gaming" ${param.purpose == 'gaming' ? 'selected' : ''}>Gaming</option><option value="design" ${param.purpose == 'design' ? 'selected' : ''}>Đồ họa / thiết kế</option><option value="office" ${param.purpose == 'office' ? 'selected' : ''}>Văn phòng</option><option value="streaming" ${param.purpose == 'streaming' ? 'selected' : ''}>Livestream</option><option value="video" ${param.purpose == 'video' ? 'selected' : ''}>Dựng phim / video</option>
    </select></div>
    <div class="col-md-6"><label class="form-label fw-semibold">Ngân sách (₫) *</label><input class="form-control" type="text" inputmode="numeric" id="budgetInput" name="budget" value="${param.budget}" placeholder="VD: 20.000.000" required><div class="form-text">Từ 3.000.000 đến 500.000.000 đồng.</div></div>
    <div class="col-md-6"><label class="form-label fw-semibold">Độ phân giải mục tiêu</label><select class="form-select" name="resolution"><option value="1080p" ${param.resolution == '1440p' || param.resolution == '2K' || param.resolution == '4K' ? '' : 'selected'}>1080p</option><option value="1440p" ${param.resolution == '1440p' ? 'selected' : ''}>1440p</option><option value="2K" ${param.resolution == '2K' ? 'selected' : ''}>2K</option><option value="4K" ${param.resolution == '4K' ? 'selected' : ''}>4K</option></select></div>
    <div class="col-md-6"><label class="form-label fw-semibold">FPS mong muốn</label><select class="form-select" name="fps" required><c:forEach var="hz" items="${fpsOptions}"><option value="${hz}" ${param.fps == hz ? 'selected' : ''}><c:out value="${hz}"/> Hz</option></c:forEach></select><div class="form-text">Lựa chọn theo tần số màn hình đang còn hàng.</div></div>
    <div class="col-md-6"><label class="form-label fw-semibold">RAM mong muốn</label><select class="form-select" name="ram" required><c:forEach var="ramSize" items="${ramOptions}"><option value="${ramSize}" ${param.ram == ramSize ? 'selected' : ''}><c:out value="${ramSize}"/> GB</option></c:forEach></select><div class="form-text">Cấu hình sẽ chọn đúng dung lượng RAM này.</div></div>
    <div class="col-md-6"><label class="form-label fw-semibold">Ưu tiên</label><select class="form-select" name="priority"><option value="balanced" ${param.priority == 'gpu' || param.priority == 'cpu' || param.priority == 'cost' ? '' : 'selected'}>Cân bằng</option><option value="gpu" ${param.priority == 'gpu' ? 'selected' : ''}>Hiệu năng đồ họa</option><option value="cpu" ${param.priority == 'cpu' ? 'selected' : ''}>Hiệu năng CPU</option><option value="cost" ${param.priority == 'cost' ? 'selected' : ''}>Tối ưu chi phí</option></select></div>
    <div class="col-12"><div class="form-check"><input class="form-check-input" type="checkbox" name="rgb" value="true" id="rgb" ${param.rgb == 'true' ? 'checked' : ''}><label class="form-check-label" for="rgb">Ưu tiên linh kiện có RGB</label></div></div>
    <div class="col-12 d-flex justify-content-end mt-4"><button class="btn btn-danger btn-lg px-4" type="submit"><i class="fas fa-wand-magic-sparkles me-2"></i>Nhận gợi ý cấu hình</button></div>
   </div></form>
  </div></div>
 </div></div>
</div>
<script>
(function () {
    const budgetInput = document.getElementById('budgetInput');
    if (!budgetInput) return;
    function formatDots(raw) {
        const digits = (raw || '').replace(/\D+/g, '');
        if (!digits) return '';
        return digits.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    }
    budgetInput.value = formatDots(budgetInput.value);
    budgetInput.addEventListener('input', function () {
        this.value = formatDots(this.value);
    });
    if (budgetInput.form) {
        budgetInput.form.addEventListener('submit', function () {
            budgetInput.value = (budgetInput.value || '').replace(/\D+/g, '');
        });
    }
})();
</script>
<jsp:include page="/views/components/footer.jsp" />
