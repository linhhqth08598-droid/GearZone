<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
    <h1 class="h2 mb-0"><i class="fas fa-gamepad me-2 text-primary"></i>Quản lý Game & Benchmark FPS</h1>
    <a href="${pageContext.request.contextPath}/compare" class="btn btn-outline-primary">
        <i class="fas fa-balance-scale me-1"></i> Xem trang So sánh
    </a>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show">
        <i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show">
        <i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="row g-4 mb-4">
    <div class="col-lg-6">
        <div class="card shadow-sm border-0 rounded-4 h-100">
            <div class="card-body p-4">
                <h5 class="card-title fw-bold text-primary mb-3">Thêm / Cập nhật Game</h5>
                <form action="${pageContext.request.contextPath}/admin/game" method="POST" class="row g-3">
                    <input type="hidden" name="action" value="saveGame">
                    <div class="col-md-7">
                        <label class="form-label fw-semibold">Tên Game *</label>
                        <input type="text" name="gameName" class="form-control" required placeholder="VD: Cyberpunk 2077">
                    </div>
                    <div class="col-md-5">
                        <label class="form-label fw-semibold">Thể loại</label>
                        <input type="text" name="genre" class="form-control" placeholder="FPS / RPG / Esports">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">RAM tối thiểu (GB)</label>
                        <input type="number" name="minRamGb" class="form-control" value="8" min="1">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">RAM khuyến nghị (GB)</label>
                        <input type="number" name="recommendedRamGb" class="form-control" value="16" min="1">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Điểm CPU tối thiểu / khuyến nghị</label>
                        <div class="input-group">
                            <input type="number" name="minCpuScore" class="form-control" value="0" min="0" placeholder="Min">
                            <input type="number" name="recommendedCpuScore" class="form-control" value="0" min="0" placeholder="Rec">
                        </div>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Điểm GPU tối thiểu / khuyến nghị</label>
                        <div class="input-group">
                            <input type="number" name="minGpuScore" class="form-control" value="0" min="0" placeholder="Min">
                            <input type="number" name="recommendedGpuScore" class="form-control" value="0" min="0" placeholder="Rec">
                        </div>
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save me-1"></i> Lưu Game</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="col-lg-6">
        <div class="card shadow-sm border-0 rounded-4 h-100">
            <div class="card-body p-4">
                <h5 class="card-title fw-bold text-primary mb-3">Thêm FPS Benchmark cho VGA</h5>
                <form action="${pageContext.request.contextPath}/admin/game" method="POST" class="row g-3">
                    <input type="hidden" name="action" value="addBenchmark">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Chọn Game *</label>
                        <select name="gameId" class="form-select" required>
                            <option value="">-- Chọn Game --</option>
                            <c:forEach var="g" items="${games}">
                                <option value="${g.gameId}"><c:out value="${g.gameName}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Chọn VGA *</label>
                        <select name="vgaPartId" class="form-select" required>
                            <option value="">-- Chọn VGA --</option>
                            <c:forEach var="v" items="${vgaParts}">
                                <option value="${v.partId}"><c:out value="${v.productCode}"/> - <c:out value="${v.partName}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Độ phân giải</label>
                        <input type="text" name="resolution" class="form-control" value="1080p" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Mức đồ họa</label>
                        <select name="graphicsPreset" class="form-select">
                            <option value="LOW">LOW</option>
                            <option value="MEDIUM">MEDIUM</option>
                            <option value="HIGH" selected>HIGH</option>
                            <option value="ULTRA">ULTRA</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">FPS trung bình *</label>
                        <input type="number" name="avgFps" class="form-control" required min="1" placeholder="VD: 120">
                    </div>
                    <div class="col-12">
                        <button type="submit" class="btn btn-success"><i class="fas fa-plus me-1"></i> Thêm Benchmark</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="card shadow-sm border-0 rounded-4 mb-4">
    <div class="card-body p-4">
        <h5 class="fw-bold mb-3">Danh sách Game</h5>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Tên Game</th>
                        <th>Thể loại</th>
                        <th>RAM (Min/Rec)</th>
                        <th>CPU Score (Min/Rec)</th>
                        <th>GPU Score (Min/Rec)</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="g" items="${games}">
                        <tr>
                            <td>#${g.gameId}</td>
                            <td class="fw-semibold"><c:out value="${g.gameName}"/></td>
                            <td><c:out value="${g.genre}"/></td>
                            <td><c:out value="${g.minRamGb}"/>GB / <c:out value="${g.recommendedRamGb}"/>GB</td>
                            <td><c:out value="${g.minCpuScore}"/> / <c:out value="${g.recommendedCpuScore}"/></td>
                            <td><c:out value="${g.minGpuScore}"/> / <c:out value="${g.recommendedGpuScore}"/></td>
                            <td>
                                <span class="badge ${g.status == 'ACTIVE' ? 'bg-success' : 'bg-secondary'}"><c:out value="${g.status}"/></span>
                            </td>
                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/admin/game" method="POST" class="d-inline">
                                    <input type="hidden" name="action" value="toggleStatus">
                                    <input type="hidden" name="gameId" value="${g.gameId}">
                                    <button type="submit" class="btn btn-sm btn-outline-secondary">Đổi trạng thái</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty games}">
                        <tr><td colspan="8" class="text-center text-muted py-3">Chưa có game nào.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <h5 class="fw-bold mb-3">Danh sách FPS Benchmark</h5>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Game</th>
                        <th>VGA</th>
                        <th>Độ phân giải</th>
                        <th>Preset</th>
                        <th class="text-end">FPS TB</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="b" items="${benchmarks}">
                        <tr>
                            <td>#<c:out value="${b['benchmarkId']}"/></td>
                            <td class="fw-semibold"><c:out value="${b['gameName']}"/></td>
                            <td><c:out value="${b['partName']}"/></td>
                            <td><c:out value="${b['resolution']}"/></td>
                            <td><span class="badge bg-info text-dark"><c:out value="${b['graphicsPreset']}"/></span></td>
                            <td class="text-end fw-bold text-primary"><c:out value="${b['avgFps']}"/> FPS</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty benchmarks}">
                        <tr><td colspan="6" class="text-center text-muted py-3">Chưa có dữ liệu benchmark.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />
