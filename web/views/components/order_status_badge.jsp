<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%-- Dùng: <jsp:include page="/views/components/order_status_badge.jsp"><jsp:param name="status" value="${o.status}"/></jsp:include> --%>
<c:choose>
    <c:when test="${param.status == 'PENDING'}"><span class="badge bg-warning text-dark">Chờ xử lý</span></c:when>
    <c:when test="${param.status == 'CONFIRMED'}"><span class="badge bg-info text-dark">Đã xác nhận</span></c:when>
    <c:when test="${param.status == 'ASSEMBLING'}"><span class="badge bg-secondary">Đang lắp ráp</span></c:when>
    <c:when test="${param.status == 'SHIPPING'}"><span class="badge bg-primary">Đang giao</span></c:when>
    <c:when test="${param.status == 'DELIVERED'}"><span class="badge bg-success">Hoàn thành</span></c:when>
    <c:when test="${param.status == 'CANCELLED'}"><span class="badge bg-danger">Đã hủy</span></c:when>
    <c:otherwise><span class="badge bg-secondary"><c:out value="${param.status}"/></span></c:otherwise>
</c:choose>
