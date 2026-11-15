<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-project-diagram me-2"></i>Quy tắc Tương thích</h1>
    <button class="btn btn-primary" id="btnAddRule" data-bs-toggle="modal" data-bs-target="#ruleModal">
        <i class="fas fa-plus me-1"></i> Thêm Quy tắc
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
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Tên Quy Tắc</th>
                        <th>Loại Kiểm Tra</th>
                        <th>Mức Độ</th>
                        <th>Thông Báo Lỗi</th>
                        <th>Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${rules}">
                        <tr>
                            <td>#${r.ruleId}</td>
                            <td class="fw-bold"><c:out value="${r.ruleName}"/></td>
                            <td><code><c:out value="${r.checkType}"/></code></td>
                            <td>
                                <c:choose>
                                    <c:when test="${r.severity == 'ERROR'}"><span class="badge bg-danger">Lỗi (ERROR)</span></c:when>
                                    <c:otherwise><span class="badge bg-warning text-dark">Cảnh báo</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-wrap" style="max-width: 250px;"><c:out value="${r.errorMessage}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${r.status == 'ACTIVE'}"><span class="badge bg-success">Hoạt động</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">Đã ẩn</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center text-nowrap">
                                <button type="button" class="btn btn-sm btn-outline-primary btn-edit-rule"
                                        data-bs-toggle="modal" data-bs-target="#ruleModal"
                                        data-id="${r.ruleId}" data-name="<c:out value='${r.ruleName}'/>"
                                        data-primary="${r.primaryCategoryId}" data-target="${r.targetCategoryId}"
                                        data-type="<c:out value='${r.checkType}'/>"
                                        data-message="<c:out value='${r.errorMessage}'/>"
                                        data-severity="${r.severity}">
                                    <i class="fas fa-edit"></i> Sửa
                                </button>
                                <form action="${pageContext.request.contextPath}/admin/compatibility" method="POST" class="m-0 d-inline">
                                    <input type="hidden" name="ruleId" value="${r.ruleId}">
                                    <c:choose>
                                        <c:when test="${r.status == 'ACTIVE'}">
                                            <input type="hidden" name="action" value="delete">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc muốn ẩn quy tắc này?');">
                                                <i class="fas fa-eye-slash"></i> Ẩn
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="hidden" name="action" value="restore">
                                            <button type="submit" class="btn btn-sm btn-outline-success"><i class="fas fa-eye"></i> Hiện</button>
                                        </c:otherwise>
                                    </c:choose>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal Thêm / Sửa (dùng chung) -->
<div class="modal fade" id="ruleModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/compatibility" method="POST" accept-charset="UTF-8">
                <input type="hidden" name="action" id="ruleAction" value="save">
                <input type="hidden" name="ruleId" id="ruleId">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary" id="ruleModalTitle">Thêm Quy tắc Tương thích</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tên Quy Tắc</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-tag"></i></span>
                            <input type="text" name="ruleName" id="ruleName" class="form-control" required maxlength="200" placeholder="VD: Kiểm tra Socket CPU - Mainboard">
                        </div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Danh mục Gốc (Primary)</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-microchip"></i></span>
                                <select name="primaryCategoryId" id="rulePrimary" class="form-select" required>
                                    <option value="">-- Chọn danh mục --</option>
                                    <c:forEach var="cat" items="${categories}">
                                        <option value="${cat.categoryId}"><c:out value="${cat.categoryName}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Danh mục Đích (Target)</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-bullseye"></i></span>
                                <select name="targetCategoryId" id="ruleTarget" class="form-select" required>
                                    <option value="">-- Chọn danh mục --</option>
                                    <c:forEach var="cat" items="${categories}">
                                        <option value="${cat.categoryId}"><c:out value="${cat.categoryName}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Loại Kiểm Tra (Check Type)</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-code"></i></span>
                            <input type="text" name="checkType" id="ruleType" class="form-control" required maxlength="100" placeholder="VD: SOCKET_MATCH, RAM_MATCH...">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Thông báo lỗi</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-comment-dots"></i></span>
                            <textarea name="errorMessage" id="ruleMessage" class="form-control" rows="2" required maxlength="500" placeholder="Nhập thông báo khi sai..."></textarea>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Mức độ</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-exclamation-triangle"></i></span>
                            <select name="severity" id="ruleSeverity" class="form-select">
                                <option value="ERROR">ERROR (Chặn không cho chọn)</option>
                                <option value="WARNING">WARNING (Chỉ cảnh báo)</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4" id="ruleSubmit">Lưu Quy tắc</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function () {
    var $ = function (id) { return document.getElementById(id); };
    function fill(d) {
        $('ruleAction').value = d ? 'update' : 'save';
        $('ruleId').value = d ? d.id : '';
        $('ruleName').value = d ? d.name : '';
        $('rulePrimary').value = d ? d.primary : '';
        $('ruleTarget').value = d ? d.target : '';
        $('ruleType').value = d ? d.type : '';
        $('ruleMessage').value = d ? d.message : '';
        $('ruleSeverity').value = d ? d.severity : 'ERROR';
        $('ruleModalTitle').textContent = d ? 'Sửa Quy tắc #' + d.id : 'Thêm Quy tắc Tương thích';
        $('ruleSubmit').textContent = d ? 'Cập nhật' : 'Lưu Quy tắc';
    }
    $('btnAddRule').addEventListener('click', function () { fill(null); });
    document.querySelectorAll('.btn-edit-rule').forEach(function (b) {
        b.addEventListener('click', function () { fill(b.dataset); });
    });
});
</script>

<jsp:include page="/views/components/footer.jsp" />