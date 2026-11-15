<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <div>
        <h1 class="h2 mb-3"><i class="fas fa-sliders-h me-2"></i>Cấu hình hệ thống</h1>
        <ul class="nav nav-tabs">
            <li class="nav-item">
                <a class="nav-link ${currentType == 'profile' ? 'active fw-bold' : ''}" href="${pageContext.request.contextPath}/admin/system?type=profile">Tiêu chí hiệu năng &amp; đề xuất</a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${currentType == 'psu' ? 'active fw-bold' : ''}" href="${pageContext.request.contextPath}/admin/system?type=psu">Ngưỡng công suất nguồn</a>
            </li>
        </ul>
    </div>
    <button type="button" class="btn btn-primary" id="btnAddItem" data-bs-toggle="modal" data-bs-target="#${currentType == 'psu' ? 'psuModal' : 'profileModal'}">
        <i class="fas fa-plus me-1"></i> Thêm mới
    </button>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> <c:out value="${sessionScope.message}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> <c:out value="${sessionScope.error}"/><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="card shadow-sm border-0 rounded-4">
    <div class="card-body p-4">
        <div class="table-responsive">

            <c:if test="${currentType == 'psu'}">
                <p class="text-secondary small mb-3">
                    Khi cấu hình PC có tổng công suất từ mức "Công suất hệ thống tối thiểu" trở lên, hệ thống đề xuất nguồn có công suất lớn hơn theo hệ số dự phòng tương ứng.
                </p>
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Công suất hệ thống tối thiểu</th>
                            <th>Dự phòng đề xuất</th>
                            <th>Mô tả</th>
                            <th>Trạng thái</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="r" items="${psuRules}">
                            <tr>
                                <td>#${r.ruleId}</td>
                                <td class="fw-bold">${r.minSystemWattage} W</td>
                                <td>${r.recommendedPsuHeadroomPercent}%</td>
                                <td><c:out value="${r.description}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${r.status == 'ACTIVE'}"><span class="badge bg-success">Hoạt động</span></c:when>
                                        <c:otherwise><span class="badge bg-secondary">Đã tắt</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="d-flex gap-2 justify-content-center">
                                        <button type="button" class="btn btn-sm btn-outline-primary btn-edit-psu"
                                                data-bs-toggle="modal" data-bs-target="#psuModal"
                                                data-id="${r.ruleId}" data-watt="${r.minSystemWattage}"
                                                data-headroom="${r.recommendedPsuHeadroomPercent}"
                                                data-desc="<c:out value='${r.description}'/>">
                                            <i class="fas fa-edit"></i> Sửa
                                        </button>
                                        <form action="${pageContext.request.contextPath}/admin/system" method="POST" class="m-0">
                                            <input type="hidden" name="type" value="psu">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="id" value="${r.ruleId}">
                                            <input type="hidden" name="status" value="${r.status}">
                                            <button type="submit" class="btn btn-sm ${r.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}">
                                                <i class="fas ${r.status == 'ACTIVE' ? 'fa-eye-slash' : 'fa-eye'}"></i> ${r.status == 'ACTIVE' ? 'Tắt' : 'Bật'}
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty psuRules}">
                            <tr><td colspan="6" class="text-center text-muted py-4">Chưa có ngưỡng công suất nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${currentType != 'psu'}">
                <p class="text-secondary small mb-3">
                    Tiêu chí dùng để đề xuất cấu hình theo nhu cầu (gaming, đồ họa, văn phòng...): độ phân giải, FPS mục tiêu và danh sách game.
                </p>
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Tên tiêu chí</th>
                            <th>Độ phân giải</th>
                            <th>FPS mục tiêu</th>
                            <th>Game / phần mềm mục tiêu</th>
                            <th>Trạng thái</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${profiles}">
                            <tr>
                                <td>#${p.profileId}</td>
                                <td class="fw-bold"><c:out value="${p.profileName}"/></td>
                                <td><c:out value="${p.targetResolution}"/></td>
                                <td><c:out value="${p.targetFps}"/></td>
                                <td style="max-width: 280px;"><div class="text-truncate" title="<c:out value='${p.targetGameList}'/>"><c:out value="${p.targetGameList}"/></div></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.status == 'ACTIVE'}"><span class="badge bg-success">Hoạt động</span></c:when>
                                        <c:otherwise><span class="badge bg-secondary">Đã tắt</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="d-flex gap-2 justify-content-center">
                                        <button type="button" class="btn btn-sm btn-outline-primary btn-edit-profile"
                                                data-bs-toggle="modal" data-bs-target="#profileModal"
                                                data-id="${p.profileId}"
                                                data-name="<c:out value='${p.profileName}'/>"
                                                data-res="<c:out value='${p.targetResolution}'/>"
                                                data-fps="<c:out value='${p.targetFps}'/>"
                                                data-games="<c:out value='${p.targetGameList}'/>">
                                            <i class="fas fa-edit"></i> Sửa
                                        </button>
                                        <form action="${pageContext.request.contextPath}/admin/system" method="POST" class="m-0">
                                            <input type="hidden" name="type" value="profile">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="id" value="${p.profileId}">
                                            <input type="hidden" name="status" value="${p.status}">
                                            <button type="submit" class="btn btn-sm ${p.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}">
                                                <i class="fas ${p.status == 'ACTIVE' ? 'fa-eye-slash' : 'fa-eye'}"></i> ${p.status == 'ACTIVE' ? 'Tắt' : 'Bật'}
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty profiles}">
                            <tr><td colspan="7" class="text-center text-muted py-4">Chưa có tiêu chí nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>
</div>

<!-- Modal: ngưỡng công suất nguồn -->
<div class="modal fade" id="psuModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/system" method="POST">
                <input type="hidden" name="type" value="psu">
                <input type="hidden" name="action" value="save">
                <input type="hidden" name="id" id="psuId" value="0">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary" id="psuModalTitle"><i class="fas fa-plug me-2"></i>Ngưỡng công suất nguồn</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Công suất hệ thống tối thiểu (W)</label>
                        <input type="number" name="minSystemWattage" id="psuWatt" class="form-control" min="1" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Dự phòng đề xuất (%)</label>
                        <input type="number" name="headroomPercent" id="psuHeadroom" class="form-control" min="0" max="100" step="0.01" required>
                    </div>
                    <div class="mb-1">
                        <label class="form-label fw-semibold">Mô tả</label>
                        <input type="text" name="description" id="psuDesc" class="form-control" maxlength="255">
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4"><i class="fas fa-save me-1"></i> Lưu</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Modal: tiêu chí hiệu năng -->
<div class="modal fade" id="profileModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/system" method="POST">
                <input type="hidden" name="type" value="profile">
                <input type="hidden" name="action" value="save">
                <input type="hidden" name="id" id="profileId" value="0">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary" id="profileModalTitle"><i class="fas fa-gamepad me-2"></i>Tiêu chí hiệu năng</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tên tiêu chí</label>
                        <input type="text" name="profileName" id="profileName" class="form-control" maxlength="100" required placeholder="VD: Gaming 1080p 144Hz">
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Độ phân giải</label>
                            <input type="text" name="targetResolution" id="profileRes" class="form-control" maxlength="20" placeholder="VD: 1920x1080">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">FPS mục tiêu</label>
                            <input type="text" name="targetFps" id="profileFps" class="form-control" maxlength="20" placeholder="VD: 144">
                        </div>
                    </div>
                    <div class="mb-1">
                        <label class="form-label fw-semibold">Game / phần mềm mục tiêu</label>
                        <textarea name="targetGameList" id="profileGames" class="form-control" rows="3" placeholder="VD: Valorant, CS2, Fortnite"></textarea>
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4"><i class="fas fa-save me-1"></i> Lưu</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function setVal(id, v) { var el = document.getElementById(id); if (el) el.value = v; }

    // Nút "Thêm mới": xóa dữ liệu cũ của modal
    document.getElementById('btnAddItem').addEventListener('click', function () {
        setVal('psuId', 0); setVal('psuWatt', ''); setVal('psuHeadroom', ''); setVal('psuDesc', '');
        setVal('profileId', 0); setVal('profileName', ''); setVal('profileRes', ''); setVal('profileFps', ''); setVal('profileGames', '');
    });

    document.querySelectorAll('.btn-edit-psu').forEach(function (b) {
        b.addEventListener('click', function () {
            var d = b.dataset;
            setVal('psuId', d.id); setVal('psuWatt', d.watt); setVal('psuHeadroom', d.headroom); setVal('psuDesc', d.desc || '');
        });
    });

    document.querySelectorAll('.btn-edit-profile').forEach(function (b) {
        b.addEventListener('click', function () {
            var d = b.dataset;
            setVal('profileId', d.id); setVal('profileName', d.name); setVal('profileRes', d.res || '');
            setVal('profileFps', d.fps || ''); setVal('profileGames', d.games || '');
        });
    });
</script>

<jsp:include page="/views/components/footer.jsp" />
