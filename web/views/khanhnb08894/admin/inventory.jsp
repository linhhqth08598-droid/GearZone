<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
    <h1 class="h2 mb-0"><i class="fas fa-warehouse me-2 text-primary"></i>Quản lý Tồn kho Linh kiện</h1>
    <div class="d-flex gap-2">
        <a href="${pageContext.request.contextPath}/admin/inventory-transactions" class="btn btn-outline-secondary">
            <i class="fas fa-history me-1"></i> Lịch sử giao dịch
        </a>
        <a href="${pageContext.request.contextPath}/admin/purchases" class="btn btn-outline-primary">
            <i class="fas fa-dolly me-1"></i> Đơn Nhập Hàng
        </a>
    </div>
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

<div class="card shadow-sm border-0 rounded-4 mb-4">
    <div class="card-body p-4">
        <h5 class="card-title fw-bold text-primary mb-3"><i class="fas fa-sliders-h me-2"></i>Điều chỉnh tồn kho / Kiểm kê / Hỏng & thất thoát</h5>
        <form action="${pageContext.request.contextPath}/admin/inventory" method="POST" class="row g-3 align-items-end">
            <input type="hidden" name="action" value="adjustStock">
            <div class="col-md-4">
                <label class="form-label fw-semibold">Linh kiện *</label>
                <select name="partId" class="form-select" required>
                    <option value="">-- Chọn linh kiện --</option>
                    <c:forEach var="p" items="${partsList}">
                        <option value="${p.partId}"><c:out value="${p.productCode}"/> - <c:out value="${p.partName}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label fw-semibold">Loại điều chỉnh *</label>
                <select name="txType" class="form-select" required>
                    <option value="ADJUSTMENT">Kiểm kê / Điều chỉnh (+/-)</option>
                    <option value="DAMAGED">Ghi nhận lỗi / hư hỏng (-)</option>
                    <option value="LOST">Ghi nhận thất thoát (-)</option>
                </select>
            </div>
            <div class="col-md-2">
                <label class="form-label fw-semibold">Số lượng *</label>
                <input type="number" name="deltaQty" class="form-control" required placeholder="VD: 5 hoặc -2">
            </div>
            <div class="col-md-3">
                <label class="form-label fw-semibold">Lý do</label>
                <input type="text" name="reason" class="form-control" placeholder="Kiểm kê định kỳ, rơi vỡ...">
            </div>
            <div class="col-12">
                <button type="submit" class="btn btn-primary"><i class="fas fa-save me-1"></i> Cập nhật tồn kho</button>
            </div>
        </form>
    </div>
</div>

<div class="card shadow-sm border-0 rounded-4 mb-4">
    <div class="card-body p-4">
        <h5 class="fw-bold mb-3">Danh sách tồn kho chi tiết</h5>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Linh kiện</th>
                        <th>Tồn kho</th>
                        <th>Đang giữ</th>
                        <th>Khả dụng</th>
                        <th>Hỏng / Lỗi</th>
                        <th>Ngưỡng báo</th>
                        <th class="text-center">Trạng thái</th>
                        <th class="text-center">Serial</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${inventoryList}">
                        <c:set var="avail" value="${item.stockQuantity - item.reservedQuantity}"/>
                        <tr>
                            <td>#${item.inventoryId}</td>
                            <td>
                                <c:set var="matchedName" value=""/>
                                <c:forEach var="p" items="${partsList}">
                                    <c:if test="${p.partId == item.partId}">
                                        <c:set var="matchedName" value="${p.productCode} - ${p.partName}"/>
                                    </c:if>
                                </c:forEach>
                                <span class="fw-semibold"><c:out value="${not empty matchedName ? matchedName : 'Part #' += item.partId}"/></span>
                                <small class="text-muted">(#<c:out value="${item.partId}"/>)</small>
                            </td>
                            <td class="fw-bold text-primary"><c:out value="${item.stockQuantity}"/></td>
                            <td><c:out value="${item.reservedQuantity}"/></td>
                            <td class="fw-semibold text-success"><c:out value="${avail > 0 ? avail : 0}"/></td>
                            <td class="text-danger"><c:out value="${item.damagedQuantity}"/></td>
                            <td><c:out value="${item.lowStockThreshold}"/></td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${item.stockQuantity > item.lowStockThreshold}"><span class="badge bg-success">Ổn định</span></c:when>
                                    <c:when test="${item.stockQuantity > 0}"><span class="badge bg-warning text-dark">Sắp hết</span></c:when>
                                    <c:otherwise><span class="badge bg-danger">Hết hàng</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center text-nowrap">
                                <a class="btn btn-sm btn-outline-dark" href="${pageContext.request.contextPath}/admin/part-serials?partId=${item.partId}">
                                    <i class="fas fa-barcode"></i> Xem Serial
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty inventoryList}">
                        <tr><td colspan="9" class="text-center text-muted py-4">Chưa có dữ liệu tồn kho.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<c:if test="${not empty categorySummary}">
    <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-4">
            <h5 class="fw-bold mb-3"><i class="fas fa-chart-bar me-2 text-primary"></i>Thống kê tồn kho theo danh mục</h5>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Danh mục</th>
                            <th class="text-end">Số mã linh kiện</th>
                            <th class="text-end">Tổng tồn kho</th>
                            <th class="text-end">Đang giữ</th>
                            <th class="text-end">Khả dụng</th>
                            <th class="text-end">Hỏng / lỗi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cs" items="${categorySummary}">
                            <tr>
                                <td class="fw-semibold"><c:out value="${cs['categoryName']}"/></td>
                                <td class="text-end"><c:out value="${cs['partCount']}"/></td>
                                <td class="text-end fw-bold"><c:out value="${cs['totalStock']}"/></td>
                                <td class="text-end"><c:out value="${cs['totalReserved']}"/></td>
                                <td class="text-end text-success fw-semibold"><c:out value="${cs['availableStock']}"/></td>
                                <td class="text-end text-danger"><c:out value="${cs['totalDamaged']}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</c:if>

<jsp:include page="/views/components/footer.jsp" />
