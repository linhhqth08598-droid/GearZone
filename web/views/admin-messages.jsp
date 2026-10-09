<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản Lý Lời Nhắn - GearZone Admin</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .msg-table { width: 100%; border-collapse: collapse; background: #1a1d29; border-radius: 8px; overflow: hidden; }
        .msg-table th, .msg-table td { padding: 15px; border-bottom: 1px solid #2d3748; text-align: left; font-size: 14px; }
        .msg-table th { background-color: #242938; color: #94a3b8; text-transform: uppercase; }
        .msg-table tr:hover { background-color: #1e2336; }
        .unread { border-left: 4px solid #ff4757; font-weight: bold; }
        .read { border-left: 4px solid #10b981; opacity: 0.8; }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container main-layout">
        <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
        
        <div class="layout-right">
            <h2 class="section-title"><i class="fa-solid fa-envelope-open-text" style="color: #ff4757;"></i> Quản Lý Tin Nhắn Khách Hàng</h2>

            <div style="background: #1a1d29; border-radius: 12px; border: 1px solid #2d3748; padding: 20px;">
                <table class="msg-table">
                    <thead>
                        <tr>
                            <th>Khách Hàng</th>
                            <th>Thông Tin Liên Hệ</th>
                            <th style="width: 40%;">Nội Dung Lời Nhắn</th>
                            <th>Ngày Gửi</th>
                            <th style="text-align: center;">Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${messageList}" var="msg">
                            <tr class="${msg.isRead ? 'read' : 'unread'}">
                                <td>
                                    <div style="color: #fff; font-size: 15px;">${msg.fullName}</div>
                                    <c:if test="${!msg.isRead}">
                                        <span style="background: #ff4757; color: white; padding: 2px 6px; border-radius: 4px; font-size: 11px;">MỚI</span>
                                    </c:if>
                                </td>
                                <td>
                                    <div style="color: #cbd5e1;"><i class="fa-solid fa-phone" style="width: 15px;"></i> ${msg.phoneNumber}</div>
                                    <div style="color: #cbd5e1; margin-top: 5px;"><i class="fa-solid fa-envelope" style="width: 15px;"></i> ${msg.email}</div>
                                </td>
                                <td style="color: #e2e8f0; line-height: 1.5;">${msg.messageContent}</td>
                                <td style="color: #94a3b8;"><fmt:formatDate value="${msg.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td style="text-align: center;">
                                    <div style="display: flex; gap: 8px; justify-content: center;">
                                        <c:if test="${!msg.isRead}">
                                            <a href="${pageContext.request.contextPath}/admin/mark-read?id=${msg.messageId}" title="Đánh dấu đã xử lý" style="background: #10b981; color: #fff; padding: 6px 10px; border-radius: 4px; text-decoration: none;">
                                                <i class="fa-solid fa-check"></i>
                                            </a>
                                        </c:if>
                                        <a href="${pageContext.request.contextPath}/admin/delete-message?id=${msg.messageId}" onclick="return confirm('Bạn có chắc chắn muốn xóa tin nhắn này?')" title="Xóa" style="background: #ef4444; color: #fff; padding: 6px 10px; border-radius: 4px; text-decoration: none;">
                                            <i class="fa-solid fa-trash"></i>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty messageList}">
                            <tr>
                                <td colspan="5" style="text-align: center; padding: 30px; color: #94a3b8;">Chưa có lời nhắn nào từ khách hàng.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <jsp:include page="includes/footer.jsp" />
    
    <c:if test="${not empty sessionScope.successMsg}">
        <script>alert('${sessionScope.successMsg}');</script>
        <c:remove var="successMsg" scope="session" />
    </c:if>
</body>
</html>