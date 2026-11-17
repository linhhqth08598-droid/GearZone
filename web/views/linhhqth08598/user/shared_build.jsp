<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<div class="container my-5" style="min-height: 60vh; max-width: 900px;">

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger"><c:out value="${sessionScope.error}"/></div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${notFound}">
            <div class="text-center py-5 bg-white rounded-4 shadow-sm">
                <i class="fas fa-link-slash text-muted mb-3" style="font-size: 56px; opacity: .4;"></i>
                <h4 class="fw-bold">Link không tồn tại hoặc đã bị thu hồi</h4>
                <p class="text-muted">Chủ cấu hình có thể đã xóa cấu hình hoặc tạo link mới.</p>
                <a href="${pageContext.request.contextPath}/build-pc" class="btn btn-primary px-4">Tự build PC của bạn</a>
            </div>
        </c:when>

        <c:otherwise>
            <div class="card shadow-sm border-0 rounded-4">
                <div class="card-body p-4">
                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-3">
                        <div>
                            <span class="badge bg-info text-dark mb-2"><i class="fas fa-share-alt me-1"></i>Cấu hình được chia sẻ</span>
                            <h3 class="fw-bold mb-1"><c:out value="${build.buildName}"/></h3>
                            <div class="text-muted small">
                                <c:if test="${not empty ownerName}">Bởi <strong><c:out value="${ownerName}"/></strong> · </c:if>
                                <fmt:formatDate value="${build.createdAt}" pattern="dd/MM/yyyy"/>
                            </div>
                        </div>
                        <h2 class="text-danger fw-bold mb-0">
                            <fmt:formatNumber value="${build.totalPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                        </h2>
                    </div>

                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead class="table-light"><tr><th>Linh kiện</th><th class="text-center">SL</th><th class="text-end">Đơn giá</th></tr></thead>
                            <tbody>
                                <c:forEach var="d" items="${build.details}">
                                    <tr>
                                        <td class="fw-semibold"><c:out value="${d.partName}"/></td>
                                        <td class="text-center">${d.quantity}</td>
                                        <td class="text-end text-danger fw-bold">
                                            <fmt:formatNumber value="${d.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty build.details}"><tr><td colspan="3" class="text-center text-muted">Cấu hình chưa có linh kiện.</td></tr></c:if>
                            </tbody>
                        </table>
                    </div>
                    <p class="text-muted small mb-3">Giá là giá tại thời điểm cấu hình được lưu, có thể khác giá hiện tại.</p>

                    <form action="${pageContext.request.contextPath}/build-share" method="POST" class="d-flex flex-wrap gap-2">
                        <input type="hidden" name="token" value="<c:out value='${token}'/>">
                        <button type="submit" class="btn btn-danger px-4">
                            <i class="fas fa-download me-1"></i> Lưu về tài khoản của tôi
                        </button>
                        <a href="${pageContext.request.contextPath}/build-pc" class="btn btn-outline-primary px-4">Tự build PC</a>
                    </form>
                    <c:if test="${empty sessionScope.loggedUser}">
                        <div class="form-text mt-2">Bạn cần đăng nhập để lưu cấu hình này.</div>
                    </c:if>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/views/components/footer.jsp" />
