<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/views/components/admin_header.jsp" />
<jsp:include page="/views/components/admin_sidebar.jsp" />

<style>
    .report-menu .list-group-item { border: 0; padding: .45rem .75rem; border-radius: .5rem; font-size: .92rem; }
    .report-menu .list-group-item.active { background: #eff6ff; color: #2563eb; font-weight: 600; }
    .report-menu .group-title { font-size: .72rem; letter-spacing: .06em; text-transform: uppercase; color: #94a3b8; font-weight: 700; padding: .9rem .75rem .25rem; }
</style>

<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
    <h1 class="h2 mb-0"><i class="fas fa-chart-bar me-2"></i>Thống kê chi tiết</h1>
    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary btn-sm">
        <i class="fas fa-arrow-left me-1"></i> Tổng quan
    </a>
</div>

<c:if test="${not empty requestScope.error}">
    <div class="alert alert-danger"><i class="fas fa-exclamation-circle me-1"></i> <c:out value="${requestScope.error}"/></div>
</c:if>

<div class="row g-4">
    <!-- Danh sách báo cáo -->
    <div class="col-lg-3">
        <div class="card shadow-sm border-0 rounded-4">
            <div class="card-body p-2 report-menu">
                <div class="list-group list-group-flush">
                    <c:set var="prevGroup" value="" />
                    <c:forEach var="r" items="${reports}">
                        <c:if test="${r.group != prevGroup}">
                            <div class="group-title"><c:out value="${r.group}"/></div>
                            <c:set var="prevGroup" value="${r.group}" />
                        </c:if>
                        <a class="list-group-item list-group-item-action ${r.key == current.key ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/admin/report?type=${r.key}&from=${from}&to=${to}">
                            <c:out value="${r.title}"/>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <!-- Nội dung báo cáo -->
    <div class="col-lg-9">
        <div class="card shadow-sm border-0 rounded-4">
            <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-3">
                    <div>
                        <h4 class="fw-bold mb-1"><c:out value="${current.title}"/></h4>
                        <p class="text-secondary mb-0"><c:out value="${current.note}"/></p>
                    </div>
                    <a class="btn btn-success"
                       href="${pageContext.request.contextPath}/admin/export?type=${current.key}&from=${from}&to=${to}">
                        <i class="fas fa-file-excel me-1"></i> Xuất Excel
                    </a>
                </div>

                <c:if test="${current.dated}">
                    <form method="GET" action="${pageContext.request.contextPath}/admin/report" class="row g-2 align-items-end mb-4">
                        <input type="hidden" name="type" value="${current.key}">
                        <div class="col-sm-4">
                            <label class="form-label small fw-semibold mb-1">Từ ngày</label>
                            <input type="date" name="from" class="form-control" value="${from}">
                        </div>
                        <div class="col-sm-4">
                            <label class="form-label small fw-semibold mb-1">Đến ngày</label>
                            <input type="date" name="to" class="form-control" value="${to}">
                        </div>
                        <div class="col-sm-4 d-flex gap-2">
                            <button type="submit" class="btn btn-primary"><i class="fas fa-filter me-1"></i> Lọc</button>
                            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/report?type=${current.key}">Xóa lọc</a>
                        </div>
                    </form>
                </c:if>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th style="width: 50px;">#</th>
                                <c:forEach var="h" items="${result.headers}" varStatus="hs">
                                    <th class="${result.numeric[hs.index] ? 'text-end' : ''}"><c:out value="${h}"/></th>
                                </c:forEach>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="row" items="${result.display}" varStatus="rs">
                                <tr>
                                    <td class="text-muted">${rs.count}</td>
                                    <c:forEach var="cell" items="${row}" varStatus="cs">
                                        <td class="${result.numeric[cs.index] ? 'text-end' : ''}"><c:out value="${cell}"/></td>
                                    </c:forEach>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty result.display}">
                                <tr>
                                    <td colspan="${fn:length(result.headers) + 1}" class="text-center text-muted py-5">
                                        <i class="fas fa-inbox fa-2x d-block mb-2"></i>Không có dữ liệu trong khoảng đã chọn.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/components/footer.jsp" />
