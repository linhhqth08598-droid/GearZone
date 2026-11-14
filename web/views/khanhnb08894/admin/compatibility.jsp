<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2"><i class="fas fa-project-diagram me-2"></i>Quy tắc Tương thích</h1>
    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addRuleModal">
        <i class="fas fa-plus me-1"></i> Thêm Quy tắc
    </button>
</div>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-1"></i> ${sessionScope.message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-circle me-1"></i> ${sessionScope.error}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
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
                            <td class="fw-bold">${r.ruleName}</td>
                            <td><code>${r.checkType}</code></td>
                            <td>
                                <c:choose>
                                    <c:when test="${r.severity == 'ERROR'}"><span class="badge bg-danger">Lỗi (ERROR)</span></c:when>
                                    <c:otherwise><span class="badge bg-warning text-dark">Cảnh báo</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-wrap" style="max-width: 250px;">${r.errorMessage}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${r.status == 'ACTIVE'}"><span class="badge bg-success">Hoạt động</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary">Đã ẩn</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/admin/compatibility" method="POST" class="m-0">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="ruleId" value="${r.ruleId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc muốn ẩn quy tắc này?');">
                                        <i class="fas fa-eye-slash"></i> Ẩn
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal Thêm mới -->
<div class="modal fade" id="addRuleModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4">
            <form action="${pageContext.request.contextPath}/admin/compatibility" method="POST">
                <input type="hidden" name="action" value="save">
                <div class="modal-header bg-white border-bottom border-primary border-3 rounded-top-4 py-3">
                    <h5 class="modal-title fw-bold text-primary">Thêm Quy tắc Tương thích</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tên Quy Tắc</label>
                        <input type="text" name="ruleName" class="form-control" required placeholder="VD: Kiểm tra Socket...">
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Danh mục Gốc (Primary)</label>
                            <select name="primaryCategoryId" class="form-select" required>
                                <option value="">-- Chọn danh mục --</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.categoryId}">${cat.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Danh mục Đích (Target)</label>
                            <select name="targetCategoryId" class="form-select" required>
                                <option value="">-- Chọn danh mục --</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.categoryId}">${cat.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Loại Kiểm Tra (Check Type)</label>
                        <input type="text" name="checkType" class="form-control" required placeholder="VD: SOCKET_MATCH, RAM_MATCH...">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Thông báo lỗi</label>
                        <textarea name="errorMessage" class="form-control" rows="2" required placeholder="Nhập thông báo khi sai..."></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Mức độ</label>
                        <select name="severity" class="form-select">
                            <option value="ERROR">ERROR (Chặn không cho chọn)</option>
                            <option value="WARNING">WARNING (Chỉ cảnh báo)</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer border-0 bg-light rounded-bottom-4 py-3">
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary px-4">Lưu Quy tắc</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />