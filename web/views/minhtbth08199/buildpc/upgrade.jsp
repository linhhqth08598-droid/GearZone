<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<jsp:include page="/views/components/header.jsp"/>
<jsp:include page="/views/components/sidebar.jsp"/>

<div>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="fw-bold mb-0">Tư vấn & Đề xuất Nâng cấp PC</h2>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/user/pc-build-history" class="btn btn-outline-primary btn-sm">
                <i class="fas fa-history me-1"></i>Lịch sử cấu hình
            </a>
            <a href="${pageContext.request.contextPath}/build-pc" class="btn btn-outline-secondary btn-sm">
                <i class="fas fa-tools me-1"></i>Build PC
            </a>
        </div>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success alert-dismissible fade show">
            <c:out value="${sessionScope.message}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show">
            <c:out value="${sessionScope.error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <jsp:include page="/views/minhtbth08199/user/current_pc.jsp"/>

    <c:if test="${not empty analysis}">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-warning text-dark fw-bold">
                <i class="fas fa-chart-line me-2"></i>Phân tích điểm nghẽn cổ chai & Tương thích
            </div>
            <div class="card-body">
                <div class="row g-3">
                    <div class="col-md-8">
                        <p class="mb-1"><strong>Thành phần ưu tiên nâng cấp:</strong>
                            <span class="badge bg-danger"><c:out value="${analysis.bottleneckCategory}"/></span>
                        </p>
                        <p class="mb-2"><c:out value="${analysis.bottleneckSummary}"/></p>
                        <c:if test="${not empty analysis.reusableComponents}">
                            <div class="small text-muted mb-2">
                                <strong>Linh kiện có thể tiếp tục sử dụng:</strong>
                                <c:out value="${analysis.reusableComponents}"/>
                            </div>
                        </c:if>
                        <c:if test="${not empty analysis.compatibilityNotes}">
                            <div class="alert alert-secondary py-2 mb-0 small">
                                <strong>Lưu ý kiểm tra tương thích / PSU:</strong>
                                <ul class="mb-0 ps-3">
                                    <c:forEach var="note" items="${analysis.compatibilityNotes}">
                                        <li><c:out value="${note}"/></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                    </div>
                    <div class="col-md-4">
                        <div class="p-3 bg-light rounded border">
                            <div class="small text-muted">Giá trị cấu hình hiện tại</div>
                            <div class="fs-5 fw-bold text-dark mb-2">
                                <fmt:formatNumber value="${analysis.currentPcValue}" type="number" groupingUsed="true"/> đ
                            </div>
                            <div class="small text-muted">Điểm hiệu năng hiện tại: <strong>${analysis.currentTotalScore}</strong></div>
                            <div class="small text-muted">Ngân sách nâng cấp:
                                <strong class="text-primary">
                                    <fmt:formatNumber value="${analysis.currentPc.upgradeBudget}" type="number" groupingUsed="true"/> đ
                                </strong>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <h4 class="fw-bold mb-3">So sánh 3 phương án nâng cấp</h4>
        <div class="row g-4">
            <c:forEach var="plan" items="${analysis.plans}">
                <div class="col-md-4">
                    <div class="card h-100 shadow-sm ${plan.selected ? 'border-primary border-2' : 'border-0'}">
                        <div class="card-header fw-bold text-white
                            ${plan.planType == 'SAVING' ? 'bg-success' :
                              (plan.planType == 'HIGH_PERFORMANCE' ? 'bg-danger' : 'bg-primary')}">
                            <c:out value="${plan.planLabel}"/> (${plan.planType})
                        </div>
                        <div class="card-body d-flex flex-column">
                            <div class="small text-muted">Hạng mục nâng cấp:</div>
                            <div class="fw-semibold mb-2"><c:out value="${plan.bottleneckComponent}"/></div>

                            <c:if test="${plan.replacePart != null}">
                                <div class="small text-secondary mb-1">
                                    Thay thế: <c:out value="${plan.replacePart.partName}"/>
                                </div>
                            </c:if>
                            <div class="small text-muted">Linh kiện đề xuất:</div>
                            <div class="fw-bold mb-3">
                                <c:choose>
                                    <c:when test="${plan.suggestedPart != null}">
                                        <c:out value="${plan.suggestedPart.partName}"/>
                                    </c:when>
                                    <c:otherwise>Chưa có linh kiện phù hợp</c:otherwise>
                                </c:choose>
                            </div>

                            <ul class="list-group list-group-flush small mb-3">
                                <li class="list-group-item d-flex justify-content-between px-0">
                                    <span>Chi phí nâng cấp:</span>
                                    <span class="fw-bold text-danger">
                                        <fmt:formatNumber value="${plan.estimatedCost}" type="number" groupingUsed="true"/> đ
                                    </span>
                                </li>
                                <li class="list-group-item d-flex justify-content-between px-0">
                                    <span>Hiệu năng tăng ước tính:</span>
                                    <span class="badge bg-info text-dark">+${plan.estimatedGainPercent}%</span>
                                </li>
                                <li class="list-group-item d-flex justify-content-between px-0">
                                    <span>Điểm trước / sau:</span>
                                    <span>${plan.currentScore} &rarr; <strong>${plan.projectedScore}</strong></span>
                                </li>
                            </ul>

                            <div class="mt-auto">
                                <c:if test="${plan.suggestedPart != null}">
                                    <form action="${pageContext.request.contextPath}/upgrade-pc" method="post" class="d-grid gap-2">
                                        <input type="hidden" name="action" value="applyPlan"/>
                                        <input type="hidden" name="planType" value="${plan.planType}"/>
                                        <button type="submit" name="addToCart" value="true" class="btn btn-primary btn-sm">
                                            <i class="fas fa-cart-plus me-1"></i>Tạo cấu hình & Thêm vào giỏ hàng
                                        </button>
                                    </form>
                                </c:if>
                                <c:if test="${plan.afterBuildId != null}">
                                    <div class="text-center mt-2">
                                        <a href="${pageContext.request.contextPath}/build-pc?buildId=${plan.afterBuildId}" class="small">
                                            Mở cấu hình #${plan.afterBuildId}
                                        </a>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>
</div>

<jsp:include page="/views/components/footer.jsp"/>
