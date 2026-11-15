<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/views/components/header.jsp" />
<jsp:include page="/views/components/sidebar.jsp" />

<h4 class="fw-bold mb-4"><i class="fas fa-tools text-danger me-2"></i>Dịch vụ lắp ráp của tôi</h4>

<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success alert-dismissible fade show"><c:out value='${sessionScope.message}'/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show"><c:out value='${sessionScope.error}'/>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <c:remove var="error" scope="session"/>
</c:if>

<c:choose>
    <c:when test="${empty assemblies}">
        <div class="text-center py-5 bg-white rounded-4 shadow-sm text-muted">Bạn chưa có đơn lắp ráp nào.</div>
    </c:when>
    <c:otherwise>
        <div class="row g-3">
            <c:forEach var="a" items="${assemblies}">
                <div class="col-12">
                    <div class="card border-0 shadow-sm rounded-4">
                        <div class="card-body p-4">
                            <div class="d-flex justify-content-between flex-wrap gap-2">
                                <div>
                                    <h6 class="fw-bold mb-1">Đơn lắp ráp #${a.assemblyId}
                                        <span class="badge bg-secondary ms-2"><c:out value="${a.status}"/></span></h6>
                                    <div class="text-muted small">Loại: <c:out value="${a.type}"/> ·
                                        Ngày hẹn: <fmt:formatDate value="${a.scheduledDate}" pattern="dd/MM/yyyy HH:mm"/></div>
                                </div>
                                <c:if test="${not empty a.rating}">
                                    <div class="text-warning fw-bold">Bạn đã đánh giá: ${a.rating}/5 <i class="fas fa-star"></i></div>
                                </c:if>
                            </div>
                            <c:if test="${not empty a.feedback}"><p class="mt-2 mb-0 text-secondary"><c:out value="${a.feedback}"/></p></c:if>

                            <c:if test="${empty a.rating and (a.status == 'HANDED_OVER' or a.status == 'COMPLETED')}">
                                <!-- UC 301: đánh giá dịch vụ lắp ráp -->
                                <form action="${pageContext.request.contextPath}/review" method="POST" class="row g-2 mt-3">
                                    <input type="hidden" name="action" value="reviewAssembly">
                                    <input type="hidden" name="assemblyId" value="${a.assemblyId}">
                                    <div class="col-md-3">
                                        <select name="rating" class="form-select">
                                            <option value="5">5 sao</option><option value="4">4 sao</option><option value="3">3 sao</option>
                                            <option value="2">2 sao</option><option value="1">1 sao</option>
                                        </select>
                                    </div>
                                    <div class="col-md-7"><input type="text" name="comment" maxlength="2000" class="form-control" placeholder="Nhận xét về dịch vụ lắp ráp"></div>
                                    <div class="col-md-2"><button type="submit" class="btn btn-primary w-100">Gửi đánh giá</button></div>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/views/components/footer.jsp" />
