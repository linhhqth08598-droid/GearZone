<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%-- Dùng chung: <jsp:include page="/views/components/pagination.jsp"><jsp:param name="url" value="/admin/reviews"/></jsp:include>
     Cần request attribute "pagination" (fourSome.util.PaginationUtil). --%>
<c:if test="${pagination.totalPages > 1}">
    <nav class="mt-3">
        <ul class="pagination justify-content-center mb-0">
            <c:url var="prevUrl" value="${param.url}"><c:param name="page" value="${pagination.currentPage - 1}"/></c:url>
            <li class="page-item ${pagination.hasPrevious() ? '' : 'disabled'}"><a class="page-link" href="${prevUrl}">&laquo;</a></li>
            <c:forEach begin="1" end="${pagination.totalPages}" var="p">
                <c:url var="pUrl" value="${param.url}"><c:param name="page" value="${p}"/></c:url>
                <li class="page-item ${p == pagination.currentPage ? 'active' : ''}"><a class="page-link" href="${pUrl}">${p}</a></li>
            </c:forEach>
            <c:url var="nextUrl" value="${param.url}"><c:param name="page" value="${pagination.currentPage + 1}"/></c:url>
            <li class="page-item ${pagination.hasNext() ? '' : 'disabled'}"><a class="page-link" href="${nextUrl}">&raquo;</a></li>
        </ul>
    </nav>
    <div class="text-center text-muted small mt-2">Trang ${pagination.currentPage}/${pagination.totalPages} - ${pagination.totalItems} mục</div>
</c:if>
