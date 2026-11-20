<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<%-- Từ ngữ theo loại dịch vụ: lắp ráp / vệ sinh PC / thay keo tản nhiệt --%>
<c:choose>
    <c:when test="${a.type == 'PC_CLEANING'}">
        <c:set var="tTitle" value="Đơn vệ sinh PC"/><c:set var="tStart" value="Bắt đầu vệ sinh"/><c:set var="tDone" value="Hoàn tất vệ sinh"/>
        <c:set var="tFlow" value="Đã xác nhận lịch → Đang vệ sinh → Hoàn tất → Bàn giao (không cần kiểm tra/ảnh)"/>
        <c:set var="tNeed" value="Linh kiện của PC cần vệ sinh"/><c:set var="tUsed" value="Linh kiện đã vệ sinh"/>
        <c:set var="tConfirm" value="Xác nhận đã vệ sinh xong PC?"/>
    </c:when>
    <c:when test="${a.type == 'THERMAL_PASTE_REPLACEMENT'}">
        <c:set var="tTitle" value="Đơn thay keo tản nhiệt"/><c:set var="tStart" value="Bắt đầu thay keo"/><c:set var="tDone" value="Hoàn tất thay keo"/>
        <c:set var="tFlow" value="Đã xác nhận lịch → Đang thay keo → Hoàn tất → Bàn giao (không cần kiểm tra/ảnh)"/>
        <c:set var="tNeed" value="Linh kiện của PC cần thay keo"/><c:set var="tUsed" value="Linh kiện đã thay keo"/>
        <c:set var="tConfirm" value="Xác nhận đã thay keo tản nhiệt xong?"/>
    </c:when>
    <c:otherwise>
        <c:set var="tTitle" value="Đơn lắp ráp"/><c:set var="tStart" value="Bắt đầu lắp"/><c:set var="tDone" value="Hoàn tất"/>
        <c:set var="tFlow" value="Đã xác nhận lịch → Đang lắp → Đang kiểm tra → Hoàn tất → Bàn giao"/>
        <c:set var="tNeed" value="Linh kiện cần lắp"/><c:set var="tUsed" value="Linh kiện đã lắp"/>
        <c:set var="tConfirm" value="Hoàn tất đơn lắp ráp này?"/>
    </c:otherwise>
</c:choose>

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
        <h3 class="fw-bold mb-0"><i class="fas fa-tools text-primary me-2"></i>${tTitle} #${a.assemblyId}
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
                <div class="small text-muted mb-2"><c:out value="${tFlow}"/></div>
                <c:if test="${a.status == 'CONFIRMED'}">
                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="m-0">
                        <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="advance"><input type="hidden" name="next" value="IN_PROGRESS"><input type="hidden" name="detail" value="1">
                        <button type="submit" class="btn btn-success">${tStart}</button>
                    </form>
                </c:if>
                <c:if test="${a.status == 'IN_PROGRESS'}">
                    <c:choose>
                        <c:when test="${isService}">
                            <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="m-0" onsubmit="return confirm('${tConfirm}');">
                                <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="complete"><input type="hidden" name="detail" value="1">
                                <button type="submit" class="btn btn-success">${tDone}</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="m-0">
                                <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="advance"><input type="hidden" name="next" value="TESTING"><input type="hidden" name="detail" value="1">
                                <button type="submit" class="btn btn-success">Lắp xong – chuyển sang kiểm tra</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <c:if test="${a.status == 'TESTING'}">
                    <div class="small text-muted mb-2">Nhập báo cáo kiểm tra (kết luận Đạt) rồi bấm Hoàn tất.</div>
                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="m-0" onsubmit="return confirm('Hoàn tất đơn lắp ráp này?');">
                        <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="complete"><input type="hidden" name="detail" value="1">
                        <button type="submit" class="btn btn-success">Hoàn tất</button>
                    </form>
                </c:if>
                <c:if test="${a.status == 'COMPLETED'}">
                    <div class="small text-muted mb-2"><c:if test="${!isService}">Cần ít nhất 1 ảnh PC hoàn thiện mới bàn giao được.</c:if></div>
                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="m-0" onsubmit="return confirm('Xác nhận đã bàn giao PC cho khách?');">
                        <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="handOver"><input type="hidden" name="detail" value="1">
                        <button type="submit" class="btn btn-primary">Bàn giao cho khách</button>
                    </form>
                </c:if>
                <c:if test="${a.status == 'HANDED_OVER'}"><div class="text-success fw-bold"><i class="fas fa-check-circle me-1"></i>Đã bàn giao cho khách.</div></c:if>
                <c:if test="${a.status == 'SCHEDULED'}"><div class="text-muted small">Cần xác nhận lịch ở danh sách trước.</div></c:if>
            </div></div>
        </div>

        <!-- UC 257: linh kiện sử dụng -->
        <div class="col-lg-7">
            <div class="card border-0 shadow-sm rounded-4 mb-3"><div class="card-body p-3">
                <h6 class="fw-bold">${tNeed}</h6>
                <table class="table table-sm align-middle mb-0">
                    <thead class="table-light"><tr><th>Linh kiện</th><th class="text-center">Cần</th><th class="text-center">Đã ghi</th><th>Serial gợi ý (từ đơn hàng)</th></tr></thead>
                    <tbody>
                        <c:forEach var="p" items="${source}">
                            <tr><td><c:out value="${p.partName}"/></td><td class="text-center">${p.quantity}</td><td class="text-center">${p.usedQty}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.suggestQty > 0}">
                                            <code><c:out value="${p.suggest}"/></code>
                                            <c:if test="${a.status == 'IN_PROGRESS' || a.status == 'TESTING'}">
                                                <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="d-inline m-0 ms-1">
                                                    <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="addPart"><input type="hidden" name="detail" value="1">
                                                    <input type="hidden" name="partId" value="${p.partId}"><input type="hidden" name="qty" value="${p.suggestQty}">
                                                    <input type="hidden" name="serial" value="<c:out value='${p.suggest}'/>">
                                                    <button type="submit" class="btn btn-sm btn-outline-success py-0">Ghi theo gợi ý</button>
                                                </form>
                                            </c:if>
                                        </c:when>
                                        <c:when test="${p.hasPool && empty p.remaining}"><span class="text-success small">Đã ghi đủ serial</span></c:when>
                                        <c:when test="${p.hasPool}"><span class="text-warning small">Đủ số lượng nhưng chưa ghi serial: <c:forEach var="sn" items="${p.remaining}" varStatus="vs"><code><c:out value="${sn}"/></code><c:if test="${!vs.last}">, </c:if></c:forEach></span></c:when>
                                        <c:otherwise><span class="text-muted small fst-italic">Không có (đơn chưa gán serial)</span></c:otherwise>
                                    </c:choose>
                                </td></tr>
                        </c:forEach>
                        <c:if test="${empty source}"><tr><td colspan="4" class="text-center text-muted">Đơn này không gắn đơn hàng/cấu hình có linh kiện.</td></tr></c:if>
                    </tbody>
                </table>
            </div></div>

            <div class="card border-0 shadow-sm rounded-4"><div class="card-body p-3">
                <h6 class="fw-bold">${tUsed}</h6>
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
                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="row g-2 m-0" id="partForm">
                        <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="addPart"><input type="hidden" name="detail" value="1">
                        <div class="col-md-6"><select name="partId" id="partSelect" class="form-select" required>
                            <option value="">-- Chọn linh kiện để ghi --</option>
                            <c:forEach var="p" items="${source}"><option value="${p.partId}"><c:out value="${p.partName}"/></option></c:forEach>
                        </select></div>
                        <div class="col-md-2"><input type="number" name="qty" value="1" min="1" class="form-control" title="Số lượng" required></div>
                        <div class="col-md-4"><input type="text" name="serial" id="serialInput" maxlength="500" class="form-control" placeholder="Số serial (nếu có, nhiều mã cách nhau dấu phẩy)"></div>
                        <div class="col-12 small text-muted">Nhập số lượng/serial (nếu cần) rồi chọn linh kiện – hệ thống tự ghi luôn, không cần bấm nút.</div>
                    </form>
                </c:if>
            </div></div>

            <c:if test="${!isService && (a.status == 'TESTING' || a.status == 'COMPLETED' || a.status == 'HANDED_OVER')}">
            <!-- UC 259–266: báo cáo kiểm tra sau lắp -->
            <div class="card border-0 shadow-sm rounded-4 mt-3"><div class="card-body p-3">
                <h6 class="fw-bold">Báo cáo kiểm tra
                    <c:if test="${not empty report}"><span class="badge ${report.finalResult == 'PASSED' ? 'bg-success' : 'bg-danger'} ms-2">${report.finalResult == 'PASSED' ? 'Đạt' : 'Không đạt'}</span></c:if></h6>
                <c:choose>
                <c:when test="${a.status == 'TESTING'}">
                <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="row g-2 m-0">
                    <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="saveReport"><input type="hidden" name="detail" value="1">
                    <div class="col-md-6"><label class="form-label small text-muted mb-1">Khởi động (UC 260)</label>
                        <select name="boot" class="form-select"><option value="1" ${empty report || report.bootPassed ? 'selected' : ''}>Đạt</option><option value="0" ${not empty report && !report.bootPassed ? 'selected' : ''}>Không đạt</option></select></div>
                    <div class="col-md-6"><label class="form-label small text-muted mb-1">Test ổn định (UC 263)</label>
                        <div class="input-group"><select name="stable" class="form-select"><option value="1" ${empty report || report.stabilityPassed ? 'selected' : ''}>Đạt</option><option value="0" ${not empty report && !report.stabilityPassed ? 'selected' : ''}>Không đạt</option></select>
                        <input type="number" name="stableMin" min="0" max="1440" class="form-control" placeholder="Số phút" value="${report.stabilityMinutes}" required></div></div>
                    <div class="col-6 col-md-3"><label class="form-label small text-muted mb-1">CPU nghỉ °C</label><input type="number" name="cpuIdle" min="0" max="150" class="form-control" value="${report.cpuIdle}" required></div>
                    <div class="col-6 col-md-3"><label class="form-label small text-muted mb-1">CPU tải °C</label><input type="number" name="cpuLoad" min="0" max="150" class="form-control" value="${report.cpuLoad}" required></div>
                    <div class="col-6 col-md-3"><label class="form-label small text-muted mb-1">GPU nghỉ °C</label><input type="number" name="gpuIdle" min="0" max="150" class="form-control" value="${report.gpuIdle}" required></div>
                    <div class="col-6 col-md-3"><label class="form-label small text-muted mb-1">GPU tải °C</label><input type="number" name="gpuLoad" min="0" max="150" class="form-control" value="${report.gpuLoad}" required></div>
                    <div class="col-md-6"><label class="form-label small text-muted mb-1">Cinebench – CPU (UC 264)</label><input type="number" name="cinebench" min="0" class="form-control" value="${report.cinebench}"></div>
                    <div class="col-md-6"><label class="form-label small text-muted mb-1">3DMark – GPU (UC 265)</label><input type="number" name="dmark" min="0" class="form-control" value="${report.dmark}"></div>
                    <div class="col-12"><label class="form-label small text-muted mb-1">Ghi chú (tối đa 500 ký tự)</label><input type="text" name="note" maxlength="500" class="form-control" value="<c:out value='${report.note}'/>"></div>
                    <div class="col-md-6"><label class="form-label small text-muted mb-1">Kết luận (UC 266)</label>
                        <select name="finalResult" class="form-select" required><option value="PASSED" ${empty report || report.finalResult == 'PASSED' ? 'selected' : ''}>Đạt</option><option value="FAILED" ${report.finalResult == 'FAILED' ? 'selected' : ''}>Không đạt</option></select></div>
                    <div class="col-md-6 d-flex align-items-end"><button type="submit" class="btn btn-primary w-100">Lưu báo cáo</button></div>
                </form>
                </c:when>
                <c:otherwise>
                    <c:if test="${empty report}"><div class="text-muted small">Chưa có báo cáo kiểm tra.</div></c:if>
                    <c:if test="${not empty report}">
                    <table class="table table-sm mb-0">
                        <tr><td>Khởi động</td><td>${report.bootPassed ? 'Đạt' : 'Không đạt'}</td><td>Ổn định</td><td>${report.stabilityPassed ? 'Đạt' : 'Không đạt'} (${report.stabilityMinutes} phút)</td></tr>
                        <tr><td>CPU nghỉ / tải</td><td>${report.cpuIdle}° / ${report.cpuLoad}°C</td><td>GPU nghỉ / tải</td><td>${report.gpuIdle}° / ${report.gpuLoad}°C</td></tr>
                        <tr><td>Cinebench</td><td>${empty report.cinebench ? '—' : report.cinebench}</td><td>3DMark</td><td>${empty report.dmark ? '—' : report.dmark}</td></tr>
                        <c:if test="${not empty report.note}"><tr><td>Ghi chú</td><td colspan="3"><c:out value="${report.note}"/></td></tr></c:if>
                    </table></c:if>
                </c:otherwise>
                </c:choose>
            </div></div>

            <!-- UC 267: ảnh PC hoàn thiện -->
            <div class="card border-0 shadow-sm rounded-4 mt-3"><div class="card-body p-3">
                <h6 class="fw-bold">Ảnh PC hoàn thiện <span class="text-muted fw-normal small">(${photos.size()}/10)</span></h6>
                <div class="row g-2 mb-2">
                    <c:forEach var="p" items="${photos}">
                        <div class="col-6 col-md-4 text-center">
                            <a href="${pageContext.request.contextPath}/media/${p.url}" target="_blank" rel="noopener"><img src="${pageContext.request.contextPath}/media/${p.url}" alt="Ảnh PC" class="img-fluid rounded-3 border" style="aspect-ratio:1/1;object-fit:cover;width:100%"></a>
                            <c:if test="${a.status == 'TESTING' || a.status == 'COMPLETED'}">
                                <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" class="m-0 mt-1" onsubmit="return confirm('Xóa ảnh này?');">
                                    <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="removePhoto"><input type="hidden" name="photoId" value="${p.photoId}"><input type="hidden" name="detail" value="1">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
                                </form>
                            </c:if>
                        </div>
                    </c:forEach>
                    <c:if test="${empty photos}"><div class="col-12 text-muted small">Chưa có ảnh nào.</div></c:if>
                </div>
                <c:if test="${a.status == 'TESTING' || a.status == 'COMPLETED'}">
                    <form action="${pageContext.request.contextPath}/admin/assembly" method="POST" enctype="multipart/form-data" class="m-0" id="photoForm">
                        <input type="hidden" name="id" value="${a.assemblyId}"><input type="hidden" name="action" value="addPhoto"><input type="hidden" name="detail" value="1">
                        <input type="file" id="photoInput" name="photos" accept="image/jpeg,image/png,image/webp,image/gif" multiple class="form-control" required>
                    </form>
                    <div class="small text-muted mt-1" id="photoHint">Chọn ảnh là tự động tải lên. Tối đa 10 ảnh/đơn, mỗi ảnh ≤ 10 MB (jpg, png, webp, gif).</div>
                </c:if>
            </div></div>
            </c:if>
        </div>
    </div>
</div>


<script>
    // Ô số: chỉ cho nhập chữ số (chặn e, E, +, -, . mà ô type=number mặc định cho phép)
    (function () {
        var nums = document.querySelectorAll('input[type=number]');
        nums.forEach(function (n) {
            n.setAttribute('inputmode', 'numeric');
            n.addEventListener('keydown', function (e) {
                if (['e', 'E', '+', '-', '.', ','].indexOf(e.key) !== -1) e.preventDefault();
            });
            n.addEventListener('paste', function (e) {
                var t = (e.clipboardData || window.clipboardData).getData('text');
                if (!/^\d+$/.test(t.trim())) e.preventDefault();
            });
        });
    })();
    // Chọn linh kiện là ghi luôn
    (function () {
        var f = document.getElementById('partForm'), sel = document.getElementById('partSelect');
        if (!f || !sel) return;
        sel.addEventListener('change', function () {
            if (!sel.value) return;
            var q = parseInt(f.elements['qty'].value, 10);
            if (isNaN(q) || q < 1) { alert('Số lượng phải từ 1 trở lên!'); sel.value = ''; return; }
            sel.disabled = false;
            f.submit();
        });
    })();
    (function () {
        var f = document.getElementById('photoForm'), inp = document.getElementById('photoInput');
        if (!f || !inp) return;
        // Chọn tệp xong là tự tải lên luôn (không cần bấm nút "Tải lên")
        inp.addEventListener('change', function () {
            var files = inp.files, total = 0, maxLeft = 10 - ${photos.size()};
            if (!files.length) return;
            for (var i = 0; i < files.length; i++) {
                if (files[i].size > 10 * 1024 * 1024) { alert('Ảnh "' + files[i].name + '" lớn hơn 10 MB!'); inp.value = ''; return; }
                total += files[i].size;
            }
            if (files.length > maxLeft) { alert('Chỉ còn thêm được ' + maxLeft + ' ảnh (tối đa 10 ảnh/đơn)!'); inp.value = ''; return; }
            if (total > 30 * 1024 * 1024) { alert('Tổng dung lượng ' + (total / 1048576).toFixed(1) + ' MB quá lớn, hãy chọn ít ảnh hơn mỗi lần (≤ 30 MB)!'); inp.value = ''; return; }
            inp.disabled = false;
            var hint = document.getElementById('photoHint');
            if (hint) hint.textContent = 'Đang tải ' + files.length + ' ảnh lên...';
            f.submit();
        });
    })();
</script>
<jsp:include page="/views/components/footer.jsp" />
