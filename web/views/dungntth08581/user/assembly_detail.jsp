<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div>
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h4 class="fw-bold mb-0"><i class="fas fa-tools text-danger me-2"></i>Đơn lắp ráp #${a.assemblyId}
            <span class="badge bg-secondary ms-2"><c:out value="${a.status == 'SCHEDULED' ? 'Chờ xác nhận lịch' : (a.status == 'CONFIRMED' ? 'Đã xác nhận lịch' : (a.status == 'IN_PROGRESS' ? 'Đang thực hiện' : (a.status == 'TESTING' ? 'Đang kiểm tra' : (a.status == 'COMPLETED' ? 'Đã hoàn tất' : (a.status == 'HANDED_OVER' ? 'Đã bàn giao' : (a.status == 'CANCELLED' ? 'Đã hủy' : a.status))))))}"/></span></h4>
        <a href="${pageContext.request.contextPath}/assembly" class="btn btn-outline-secondary">Đơn lắp ráp của tôi</a>
    </div>

    <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-4">
        <div><strong>Dịch vụ:</strong> <c:out value="${a.typeLabel}"/> – <fmt:formatNumber value="${a.fee}" type="number" maxFractionDigits="0"/> ₫</div>
        <div><strong>Ngày hẹn:</strong> <fmt:formatDate value="${a.scheduledDate}" pattern="dd/MM/yyyy HH:mm"/></div>
        <div><strong>Kỹ thuật viên:</strong> <c:out value="${empty a.technicianName ? 'chưa phân công' : a.technicianName}"/></div>
        <c:if test="${not empty a.note}"><div><strong>Ghi chú:</strong> <c:out value="${a.note}"/></div></c:if>
    </div></div>

    <c:if test="${not empty used}">
        <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-4">
            <h6 class="fw-bold">Linh kiện đã lắp</h6>
            <table class="table table-sm mb-0">
                <thead class="table-light"><tr><th>Linh kiện</th><th class="text-center">SL</th><th>Serial</th></tr></thead>
                <tbody><c:forEach var="u" items="${used}"><tr><td><c:out value="${u.partName}"/></td><td class="text-center">${u.quantity}</td><td><c:out value="${u.serial}"/></td></tr></c:forEach></tbody>
            </table>
        </div></div>
    </c:if>

    <c:if test="${not isService && not empty report && (a.status == 'COMPLETED' || a.status == 'HANDED_OVER')}">
        <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-4">
            <h6 class="fw-bold">Kết quả kiểm tra <span class="badge ${report.finalResult == 'PASSED' ? 'bg-success' : 'bg-danger'} ms-2">${report.finalResult == 'PASSED' ? 'Đạt' : 'Không đạt'}</span></h6>
            <table class="table table-sm mb-0">
                <tr><td>Khởi động</td><td>${report.bootPassed ? 'Đạt' : 'Không đạt'}</td><td>Ổn định hệ thống</td><td>${report.stabilityPassed ? 'Đạt' : 'Không đạt'} (${report.stabilityMinutes} phút)</td></tr>
                <tr><td>Nhiệt độ CPU (nghỉ / tải)</td><td>${report.cpuIdle}° / ${report.cpuLoad}°C</td><td>Nhiệt độ GPU (nghỉ / tải)</td><td>${report.gpuIdle}° / ${report.gpuLoad}°C</td></tr>
                <tr><td>Cinebench (CPU)</td><td>${empty report.cinebench ? '—' : report.cinebench}</td><td>3DMark (GPU)</td><td>${empty report.dmark ? '—' : report.dmark}</td></tr>
                <c:if test="${not empty report.note}"><tr><td>Ghi chú</td><td colspan="3"><c:out value="${report.note}"/></td></tr></c:if>
            </table>
        </div></div>
    </c:if>

    <c:if test="${not empty photos && (a.status == 'COMPLETED' || a.status == 'HANDED_OVER')}">
        <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-4">
            <h6 class="fw-bold">Ảnh PC hoàn thiện</h6>
            <div class="row g-2">
                <c:forEach var="p" items="${photos}">
                    <div class="col-6 col-md-3"><a href="${pageContext.request.contextPath}/media/${p.url}" target="_blank" rel="noopener">
                        <img src="${pageContext.request.contextPath}/media/${p.url}" alt="Ảnh PC" class="img-fluid rounded-3 border" style="aspect-ratio:1/1;object-fit:cover;width:100%"></a></div>
                </c:forEach>
            </div>
        </div></div>
    </c:if>
</div>

<jsp:include page="/views/components/footer.jsp" />
