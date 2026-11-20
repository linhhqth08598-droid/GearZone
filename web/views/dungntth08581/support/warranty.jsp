<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div class="container py-3" style="max-width: 960px;">
    <div class="text-center mb-4">
        <h3 class="fw-bold"><i class="fas fa-shield-halved text-danger me-2"></i>Tra cứu bảo hành điện tử</h3>
        <p class="text-muted mb-0">Nhập <strong>Số Serial (S/N)</strong>, <strong>Mã phiếu bảo hành</strong> hoặc <strong>Mã sản phẩm</strong> để kiểm tra thời hạn bảo hành.</p>
    </div>

    <div class="card border-0 shadow-sm rounded-4 mb-4">
        <div class="card-body p-4">
            <form method="GET" action="${pageContext.request.contextPath}/warranty-lookup" class="row g-2 align-items-center">
                <div class="col-md-9">
                    <input type="text" name="keyword" class="form-control form-control-lg"
                           value="<c:out value='${keyword}'/>"
                           placeholder="Ví dụ: SN-11-2-1, EW-20261120-11-1 hoặc mã sản phẩm..." required>
                </div>
                <div class="col-md-3 d-grid">
                    <button type="submit" class="btn btn-danger btn-lg fw-semibold">
                        <i class="fas fa-magnifying-glass me-1"></i> Tra cứu
                    </button>
                </div>
            </form>
        </div>
    </div>

    <c:if test="${searched}">
        <c:choose>
            <c:when test="${empty results and not empty serialInfo}">
                <div class="card border-0 shadow-sm rounded-4 border-start border-4 border-info">
                    <div class="card-body p-4">
                        <h5 class="fw-bold mb-1"><c:out value="${serialInfo[0]}"/></h5>
                        <div class="small text-muted mb-2">Mã SP: <strong><c:out value="${serialInfo[2]}"/></strong> &bull; S/N: <strong><c:out value="${keyword}"/></strong></div>
                        <c:choose>
                            <c:when test="${serialInfo[1] == 'IN_STOCK'}"><span class="badge bg-secondary">Serial hợp lệ - hàng còn trong kho, chưa bán</span></c:when>
                            <c:when test="${serialInfo[1] == 'SOLD'}"><span class="badge bg-warning text-dark">Đã bán - phiếu bảo hành sẽ được kích hoạt khi đơn giao thành công</span></c:when>
                            <c:otherwise><span class="badge bg-dark"><c:out value="${serialInfo[1]}"/></span></c:otherwise>
                        </c:choose>
                        <p class="small text-muted mt-2 mb-0">Chưa có phiếu bảo hành điện tử cho số Serial này.</p>
                    </div>
                </div>
            </c:when>
            <c:when test="${empty results and not empty partPolicy}">
                <div class="card border-0 shadow-sm rounded-4 border-start border-4 border-primary">
                    <div class="card-body p-4">
                        <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-2">
                            <div>
                                <h5 class="fw-bold mb-1"><c:out value="${partPolicy.partName}"/></h5>
                                <div class="small text-muted">
                                    Mã SP: <strong><c:out value="${partPolicy.productCode}"/></strong> &bull;
                                    Danh mục: <c:out value="${partPolicy.categoryName}"/> &bull;
                                    Hãng: <c:out value="${partPolicy.brandName}"/>
                                </div>
                            </div>
                            <span class="badge bg-primary fs-6">Bảo hành ${partPolicy.warrantyMonths} tháng</span>
                        </div>
                        <p class="small text-muted mb-0">
                            <i class="fas fa-circle-info me-1"></i>Đây là thông tin chính sách bảo hành tiêu chuẩn của sản phẩm. Để kiểm tra hạn bảo hành cụ thể cho sản phẩm bạn đã mua, vui lòng nhập <strong>Số Serial (S/N)</strong> hoặc <strong>Mã phiếu bảo hành</strong>.
                        </p>
                    </div>
                </div>
            </c:when>
            <c:when test="${empty results}">
                <div class="alert alert-warning rounded-4 shadow-sm">
                    <i class="fas fa-circle-exclamation me-2"></i>Không tìm thấy thông tin bảo hành nào khớp với <strong><c:out value="${keyword}"/></strong>.
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-3">
                    <c:forEach var="w" items="${results}">
                        <div class="col-12">
                            <div class="card border-0 shadow-sm rounded-4">
                                <div class="card-body p-4">
                                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-3">
                                        <div>
                                            <h5 class="fw-bold mb-1"><c:out value="${w.partName}"/></h5>
                                            <div class="small text-muted">
                                                Mã SP: <strong><c:out value="${w.productCode}"/></strong> &bull;
                                                Mã phiếu BH: <strong><c:out value="${w.warrantyCode}"/></strong> &bull;
                                                Đơn hàng: <strong><c:out value="${w.orderCode}"/></strong>
                                            </div>
                                        </div>
                                        <span class="badge fs-6 ${w.isValid ? 'bg-success' : 'bg-secondary'}">
                                            ${w.isValid ? 'Còn hạn bảo hành' : 'Hết hạn / Không hiệu lực'}
                                        </span>
                                    </div>
                                    <div class="row g-2 small">
                                        <div class="col-md-4"><span class="text-muted">Số Serial:</span> <strong><c:out value="${w.serialNumber}"/></strong></div>
                                        <div class="col-md-4"><span class="text-muted">Ngày kích hoạt:</span> <strong><fmt:formatDate value="${w.startDate}" pattern="dd/MM/yyyy"/></strong></div>
                                        <div class="col-md-4"><span class="text-muted">Hạn bảo hành đến:</span> <strong class="text-danger"><fmt:formatDate value="${w.endDate}" pattern="dd/MM/yyyy"/></strong> (${w.warrantyMonths} tháng)</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </c:if>
</div>

<jsp:include page="/views/components/footer.jsp" />