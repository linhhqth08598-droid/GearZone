<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container my-5" style="min-height: 60vh;">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold mb-0"><i class="fas fa-desktop text-primary me-2"></i>Lịch sử cấu hình PC</h3>
        <a href="${pageContext.request.contextPath}/build-pc" class="btn btn-outline-primary">
            <i class="fas fa-plus me-1"></i> Tạo cấu hình mới
        </a>
    </div>

    <c:choose>
        <c:when test="${empty buildHistoryList}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <img src="https://cdn-icons-png.flaticon.com/512/1067/1067357.png" alt="Empty" style="width: 120px; opacity: 0.5;" class="mb-3">
                <h5 class="text-muted">Bạn chưa có cấu hình PC nào được lưu.</h5>
                <p class="text-muted mb-4">Hãy thử sức với công cụ Build PC để tạo cho mình một dàn máy ưng ý nhé!</p>
                <a href="${pageContext.request.contextPath}/build-pc" class="btn btn-primary px-4 py-2" style="border-radius: 8px;">Build PC ngay</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <c:forEach var="build" items="${buildHistoryList}">
                    <div class="col-md-6 mb-4">
                        <div class="card shadow-sm border-0 rounded-4 h-100">
                            <div class="card-body p-4">
                                <div class="d-flex justify-content-between align-items-start mb-3">
                                    <h5 class="fw-bold text-dark mb-0">${build.buildName}</h5>
                                    <c:choose>
                                        <c:when test="${build.status == 'SAVED'}">
                                            <span class="badge bg-success">Đã lưu</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-warning text-dark">Bản nháp</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <p class="text-muted mb-2" style="font-size: 14px;">
                                    <i class="far fa-calendar-alt me-1"></i> Ngày tạo: <fmt:formatDate value="${build.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </p>
                                
                                <h4 class="text-danger fw-bold mb-3">
                                    <fmt:formatNumber value="${build.totalPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </h4>

                                <!-- Nút xem chi tiết linh kiện -->
                                <button class="btn btn-outline-secondary btn-sm w-100 mb-3" type="button" data-bs-toggle="collapse" data-bs-target="#details-${build.buildId}" aria-expanded="false">
                                    <i class="fas fa-list me-1"></i> Xem chi tiết linh kiện
                                </button>
                                
                                <div class="collapse" id="details-${build.buildId}">
                                    <ul class="list-group list-group-flush mb-3">
                                        <c:forEach var="detail" items="${build.details}">
                                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                                <div>
                                                    <span class="fw-semibold">${detail.partName}</span>
                                                    <small class="text-muted">x${detail.quantity}</small>
                                                </div>
                                                <span class="text-danger fw-bold">
                                                    <fmt:formatNumber value="${detail.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                            <div class="card-footer bg-white border-top-0 text-end pb-3">
                                <a href="${pageContext.request.contextPath}/build-pc?id=${build.buildId}" class="btn btn-outline-primary btn-sm me-2">
                                    <i class="fas fa-eye me-1"></i> Xem chi tiết
                                </a>
                                <c:if test="${not empty build.shareToken}">
                                    <button class="btn btn-outline-secondary btn-sm" onclick="copyShareLink('${build.shareToken}')">
                                        <i class="fas fa-share-alt me-1"></i> Chia sẻ
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
    function copyShareLink(token) {
        const url = window.location.origin + "${pageContext.request.contextPath}/build-pc/share?token=" + token;
        navigator.clipboard.writeText(url).then(() => {
            alert("Đã sao chép link chia sẻ cấu hình!");
        }).catch(err => {
            console.error('Lỗi khi copy: ', err);
        });
    }
</script>

<jsp:include page="/views/components/footer.jsp" />