<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <title>Đang chuyển hướng...</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="refresh" content="0; url=${pageContext.request.contextPath}/admin/dashboard">
    <script type="text/javascript">
        window.location.replace("${pageContext.request.contextPath}/admin/dashboard");
    </script>
</head>
<body>
    <p>Đang chuyển hướng đến trang quản trị. Nếu trình duyệt không tự động chuyển, vui lòng <a href="${pageContext.request.contextPath}/admin/dashboard">nhấn vào đây</a>.</p>
</body>
</html>
