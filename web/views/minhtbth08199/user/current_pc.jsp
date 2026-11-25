<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@taglib uri="jakarta.tags.fmt" prefix="fmt"%>

<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
        <h5 class="mb-0"><i class="fas fa-desktop me-2"></i>Khai báo cấu hình PC hiện tại</h5>
        <span class="badge bg-info text-dark">UC 171–178</span>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/upgrade-pc" method="post">
            <input type="hidden" name="action" value="saveCurrentPc"/>
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label fw-semibold">CPU đang dùng</label>
                    <select name="currentCpuId" class="form-select">
                        <option value="">-- Chưa chọn / Bỏ qua --</option>
                        <c:forEach var="p" items="${cpuList}">
                            <option value="${p.partId}" ${currentPc != null && currentPc.currentCpuId == p.partId ? 'selected' : ''}>
                                <c:out value="${p.partName}"/> (Điểm: ${p.gamingPerformanceScore})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Mainboard đang dùng</label>
                    <select name="currentMainboardId" class="form-select">
                        <option value="">-- Chưa chọn / Bỏ qua --</option>
                        <c:forEach var="p" items="${mainList}">
                            <option value="${p.partId}" ${currentPc != null && currentPc.currentMainboardId == p.partId ? 'selected' : ''}>
                                <c:out value="${p.partName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-semibold">RAM đang dùng</label>
                    <select name="currentRamId" class="form-select">
                        <option value="">-- Chưa chọn / Bỏ qua --</option>
                        <c:forEach var="p" items="${ramList}">
                            <option value="${p.partId}" ${currentPc != null && currentPc.currentRamId == p.partId ? 'selected' : ''}>
                                <c:out value="${p.partName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-semibold">Số lượng RAM</label>
                    <input type="number" name="currentRamQuantity" class="form-control" min="1" max="32"
                           value="${currentPc != null && currentPc.currentRamQuantity != null ? currentPc.currentRamQuantity : 1}"/>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">VGA đang dùng</label>
                    <select name="currentVgaId" class="form-select">
                        <option value="">-- Chưa chọn / Bỏ qua --</option>
                        <c:forEach var="p" items="${vgaList}">
                            <option value="${p.partId}" ${currentPc != null && currentPc.currentVgaId == p.partId ? 'selected' : ''}>
                                <c:out value="${p.partName}"/> (Điểm: ${p.gamingPerformanceScore})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Ổ cứng (SSD/HDD) đang dùng</label>
                    <select name="currentStorageId" class="form-select">
                        <option value="">-- Chưa chọn / Bỏ qua --</option>
                        <c:forEach var="p" items="${ssdList}">
                            <option value="${p.partId}" ${currentPc != null && currentPc.currentStorageId == p.partId ? 'selected' : ''}>
                                <c:out value="${p.partName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Nguồn (PSU) đang dùng</label>
                    <select name="currentPsuId" class="form-select">
                        <option value="">-- Chưa chọn / Bỏ qua --</option>
                        <c:forEach var="p" items="${psuList}">
                            <option value="${p.partId}" ${currentPc != null && currentPc.currentPsuId == p.partId ? 'selected' : ''}>
                                <c:out value="${p.partName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Case đang dùng</label>
                    <select name="currentCaseId" class="form-select">
                        <option value="">-- Chưa chọn / Bỏ qua --</option>
                        <c:forEach var="p" items="${caseList}">
                            <option value="${p.partId}" ${currentPc != null && currentPc.currentCaseId == p.partId ? 'selected' : ''}>
                                <c:out value="${p.partName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">Tản nhiệt CPU đang dùng</label>
                    <select name="currentCoolerId" class="form-select">
                        <option value="">-- Chưa chọn / Bỏ qua --</option>
                        <c:forEach var="p" items="${coolerList}">
                            <option value="${p.partId}" ${currentPc != null && currentPc.currentCoolerId == p.partId ? 'selected' : ''}>
                                <c:out value="${p.partName}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold text-primary">Ngân sách dự kiến nâng cấp (VNĐ)</label>
                    <input type="number" name="upgradeBudget" class="form-control border-primary" min="0" max="500000000" step="100000"
                           value="${currentPc != null && currentPc.upgradeBudget != null ? currentPc.upgradeBudget.longValue() : 10000000}" required/>
                </div>
            </div>
            <div class="mt-4 text-end">
                <button type="submit" class="btn btn-primary px-4">
                    <i class="fas fa-save me-2"></i>Lưu cấu hình & Phân tích nâng cấp
                </button>
            </div>
        </form>
    </div>
</div>
