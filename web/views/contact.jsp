<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Liên Hệ - GearZone</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .contact-page-container { max-width: 1200px; margin: 0 auto; display: flex; gap: 40px; background: transparent; padding: 0; }
        .contact-info-panel, .contact-form-panel { flex: 1; background: #1a1d29; padding: 30px; border-radius: 12px; border: 1px solid #2d3748; position: relative;}
        
        .contact-form-panel label { color: #cbd5e1; font-weight: 600; margin-bottom: 8px; font-size: 14px;}
        .contact-form-panel input, .contact-form-panel textarea { background: #0f1117; color: #fff; border: 1px solid #334155; border-radius: 6px; padding: 12px; width: 100%; box-sizing: border-box; outline: none; }
        .contact-form-panel input:focus, .contact-form-panel textarea:focus { border-color: #3b82f6;}
        
        .info-line { display: flex; align-items: flex-start; margin-bottom: 15px; font-size: 15px; color: #94a3b8; }
        .info-line i { width: 25px; color: #ff4757; font-size: 16px; margin-top: 4px; }
        .info-line a { color: #3b82f6; text-decoration: none; }
        .info-line a:hover { text-decoration: underline; color: #60a5fa;}
        .info-line strong { color: #e2e8f0; }
        .form-group { margin-bottom: 15px; }
        
        .breadcrumb { font-size: 14px; margin-bottom: 20px; color: #94a3b8;}
        .breadcrumb a { color: #94a3b8; text-decoration: none; }
        .breadcrumb a:hover { color: #fff; }
        .breadcrumb span { color: #fff; }
        
        /* Chỉnh lại iframe map cho phù hợp */
        .map-container iframe { width: 100% !important; height: 100% !important; }
    </style>
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container main-layout">
        <div class="layout-left"><jsp:include page="includes/sidebar.jsp" /></div>
        
        <div class="layout-right">
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Trang chủ</a> / <span>Liên hệ</span>
            </div>

            <div class="contact-page-container">
                <!-- Phần Trái: Map và Thông Tin (Động từ DB) -->
                <div class="contact-info-panel">
                    <!-- Nút chỉnh sửa cho Admin -->
                    <c:if test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
                        <button onclick="document.getElementById('editContactModal').style.display='flex'" style="position: absolute; top: 15px; right: 15px; background: #3b82f6; color: #fff; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 12px;">
                            <i class="fa-solid fa-pen"></i> Chỉnh sửa
                        </button>
                    </c:if>

                    <h2 style="font-size: 20px; font-weight: bold; margin-bottom: 8px; text-transform: uppercase; color: #fff;">${storeSetting.companyName}</h2>
                    <p style="font-size: 14px; color: #64748b; margin-bottom: 25px; line-height: 1.6;">
                        ${storeSetting.businessLicense}
                    </p>

                    <h3 style="font-size: 16px; font-weight: bold; margin-bottom: 15px; color: #e2e8f0;">
                        <i class="fa-solid fa-store" style="color: #f59e0b; margin-right: 5px;"></i> HỆ THỐNG CỬA HÀNG GEARZONE
                    </h3>

                    <div class="info-line">
                        <i class="fa-solid fa-location-dot"></i>
                        <span><strong>Cơ sở 1:</strong> ${storeSetting.address1}</span>
                    </div>
                    <c:if test="${not empty storeSetting.address2}">
                        <div class="info-line">
                            <i class="fa-solid fa-location-dot"></i>
                            <span><strong>Cơ sở 2:</strong> ${storeSetting.address2}</span>
                        </div>
                    </c:if>
                    
                    <div class="info-line" style="margin-top: 20px;">
                        <i class="fa-solid fa-phone"></i>
                        <span><strong>Hotline:</strong> <span style="color: #ff4757; font-weight: bold;">${storeSetting.hotline}</span></span>
                    </div>
                    <div class="info-line">
                        <i class="fa-solid fa-globe"></i>
                        <span><strong>Website:</strong> <a href="${storeSetting.websiteUrl}">${storeSetting.websiteUrl}</a></span>
                    </div>
                    <div class="info-line">
                        <i class="fa-brands fa-facebook"></i>
                        <span><strong>Fanpage:</strong> <a href="${storeSetting.fanpageUrl}">${storeSetting.fanpageUrl}</a></span>
                    </div>
                    <div class="info-line">
                        <i class="fa-solid fa-envelope"></i>
                        <span><strong>Email:</strong> ${storeSetting.email}</span>
                    </div>

                    <!-- Google Maps Động -->
                    <div class="map-container" style="margin-top: 25px; border-radius: 8px; overflow: hidden; border: 1px solid #334155; height: 250px;">
                        ${storeSetting.googleMapIframe}
                    </div>
                </div>

                <!-- Phần Phải: Form Gửi Tin Nhắn -->
                <div class="contact-form-panel">
                    <p style="font-size: 15px; font-weight: 600; margin-bottom: 20px; color: #fff;">
                        Quý khách vui lòng gửi thông tin theo form sau :
                    </p>
                    <form action="${pageContext.request.contextPath}/submit-contact" method="POST">
                        <div style="display: flex; gap: 15px; margin-bottom: 15px;">
                            <div class="form-group" style="flex: 1; margin-bottom: 0;">
                                <label>Tên đầy đủ <span style="color:#ff4757;">*</span></label>
                                <input type="text" name="fullName" required placeholder="Họ và tên" value="${sessionScope.userAcc != null ? sessionScope.userAcc.fullName : ''}">
                            </div>
                            <div class="form-group" style="flex: 1; margin-bottom: 0;">
                                <label>Điện thoại <span style="color:#ff4757;">*</span></label>
                                <input type="text" name="phone" required placeholder="Số điện thoại" value="${sessionScope.userAcc != null ? sessionScope.userAcc.phoneNumber : ''}">
                            </div>
                        </div>

                        <div class="form-group" style="margin-bottom: 15px;">
                            <label>Email <span style="color:#ff4757;">*</span></label>
                            <input type="email" name="email" required placeholder="Email của bạn" value="${sessionScope.userAcc != null ? sessionScope.userAcc.email : ''}">
                        </div>

                        <div class="form-group" style="margin-bottom: 25px;">
                            <label>Thông tin liên hệ <span style="color:#ff4757;">*</span></label>
                            <textarea name="content" rows="6" required placeholder="Vui lòng nhập nội dung liên hệ của bạn..."></textarea>
                        </div>

                        <button type="submit" class="btn-submit" style="background: #3b82f6; width: auto; padding: 12px 30px; font-size: 15px; border-radius: 6px;">
                            Gửi liên hệ
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- MODAL SỬA THÔNG TIN CỬA HÀNG (Dành cho Admin) -->
    <c:if test="${not empty sessionScope.userAcc and sessionScope.userAcc.roleId <= 3}">
        <div class="modal-overlay" id="editContactModal">
            <div class="modal-content form-box" style="margin: 0; width: 600px; max-height: 90vh; overflow-y: auto;">
                <div class="modal-header">
                    <h3>Cập Nhật Thông Tin Liên Hệ</h3>
                    <i class="fa-solid fa-xmark close-btn" onclick="document.getElementById('editContactModal').style.display='none'"></i>
                </div>
                <form action="${pageContext.request.contextPath}/admin/update-contact-info" method="POST">
                    <input type="hidden" name="settingId" value="${storeSetting.settingId}">
                    
                    <div class="form-group">
                        <label>Tên Công Ty</label>
                        <input type="text" name="companyName" value="${storeSetting.companyName}" required>
                    </div>
                    <div class="form-group">
                        <label>ĐKKD / Giấy phép</label>
                        <input type="text" name="businessLicense" value="${storeSetting.businessLicense}">
                    </div>
                    <div class="form-group">
                        <label>Cơ sở 1 (Trụ sở chính)</label>
                        <input type="text" name="address1" value="${storeSetting.address1}" required>
                    </div>
                    <div class="form-group">
                        <label>Cơ sở 2 (Chi nhánh)</label>
                        <input type="text" name="address2" value="${storeSetting.address2}">
                    </div>
                    <div style="display: flex; gap: 10px;">
                        <div class="form-group" style="flex: 1;">
                            <label>Hotline</label>
                            <input type="text" name="hotline" value="${storeSetting.hotline}">
                        </div>
                        <div class="form-group" style="flex: 1;">
                            <label>Email</label>
                            <input type="email" name="email" value="${storeSetting.email}">
                        </div>
                    </div>
                    <div style="display: flex; gap: 10px;">
                        <div class="form-group" style="flex: 1;">
                            <label>Website URL</label>
                            <input type="text" name="websiteUrl" value="${storeSetting.websiteUrl}">
                        </div>
                        <div class="form-group" style="flex: 1;">
                            <label>Fanpage URL</label>
                            <input type="text" name="fanpageUrl" value="${storeSetting.fanpageUrl}">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Mã nhúng Google Maps (Iframe)</label>
                        <textarea name="googleMapIframe" rows="4" placeholder='<iframe src="..."></iframe>' style="font-family: monospace; font-size: 12px;">${storeSetting.googleMapIframe}</textarea>
                    </div>
                    
                    <button type="submit" class="btn-submit" style="background-color: #10b981;">Lưu Thay Đổi</button>
                </form>
            </div>
        </div>
    </c:if>

    <jsp:include page="includes/footer.jsp" />

    <c:if test="${not empty sessionScope.errorMsg}">
        <script>alert('${sessionScope.errorMsg}');</script>
        <c:remove var="errorMsg" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.successMsg}">
        <script>alert('${sessionScope.successMsg}');</script>
        <c:remove var="successMsg" scope="session" />
    </c:if>
</body>
</html>