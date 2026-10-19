<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh Toán QR - GearZone</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css?v=2.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <jsp:include page="includes/header.jsp" />

    <div class="container" style="max-width: 600px; text-align: center; margin-top: 50px;">
        <div style="background: #1a1d29; border-radius: 12px; border: 1px solid #2d3748; padding: 40px;">
            <i class="fa-solid fa-qrcode" style="font-size: 50px; color: #3b82f6; margin-bottom: 20px;"></i>
            <h2 style="color: #fff; margin-bottom: 10px;">Quét Mã Thanh Toán</h2>
            <p style="color: #94a3b8; margin-bottom: 15px;">Vui lòng sử dụng App Ngân hàng để quét mã dưới đây.</p>
            
            <div id="countdown-timer" style="color: #ff4757; font-size: 22px; font-weight: bold; margin-bottom: 20px;">
                Thời gian còn lại: 05:00
            </div>
            
            <div style="background: #fff; padding: 20px; border-radius: 12px; display: inline-block; margin-bottom: 30px; min-height: 250px; min-width: 400px; display: flex; align-items: center; justify-content: center;">
                <!-- Link ảnh QR tĩnh với tham số động -->
                <img id="qr-img" src="https://vietqr.app/img?acc=03339990999&bank=MBBank&amount=${param.amount}&des=${param.code}&fullacc=true&holder=HA+QUANG+LINH&store=GearZone&template=compact&showinfo=true" alt="QR Thanh Toán" style="width: 400px; border-radius: 8px;">
                
                <div id="expired-message" style="display: none; color: #ff4757; font-size: 16px; font-weight: bold; text-align: center;">
                    <i class="fa-solid fa-circle-xmark" style="font-size: 40px; margin-bottom: 10px;"></i><br>
                    Mã QR đã hủy / Hết hạn.<br>Vui lòng đặt lại đơn hàng.
                </div>
            </div>

            <div style="background: #0f1117; padding: 15px; border-radius: 8px; border: 1px dashed #334155; text-align: left; margin-bottom: 30px;">
                <p style="color: #cbd5e1; margin-bottom: 5px;">Mã Đơn Hàng: <strong style="color: #ff4757;">${param.code}</strong></p>
                <p style="color: #cbd5e1;">Số Tiền: <strong style="color: #10b981;"><fmt:formatNumber value="${param.amount}" pattern="#,###"/> ₫</strong></p>
            </div>

            <!-- Nút bấm chuyển trang đơn giản -->
            <a href="${pageContext.request.contextPath}/my-orders" id="btn-submit" class="btn-submit" style="display: block; text-decoration: none; padding: 15px; transition: all 0.3s ease;">
                Tôi đã thanh toán xong
            </a>
        </div>
    </div>
    
    <jsp:include page="includes/footer.jsp" />

    <!-- Script đếm ngược 5 phút siêu nhẹ -->
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            let timeInSecs = 5 * 60; // 5 phút = 300 giây
            const timerElement = document.getElementById('countdown-timer');
            const qrImage = document.getElementById('qr-img');
            const submitBtn = document.getElementById('btn-submit');
            const expiredMsg = document.getElementById('expired-message');

            const timer = setInterval(() => {
                timeInSecs--;
                let m = Math.floor(timeInSecs / 60);
                let s = timeInSecs % 60;
                
                timerElement.innerText = "Thời gian còn lại: " + (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;

                if (timeInSecs <= 0) {
                    clearInterval(timer);
                    
                    // Ẩn ảnh QR, hiện thông báo lỗi
                    qrImage.style.display = 'none';
                    expiredMsg.style.display = 'block';
                    timerElement.style.display = 'none';
                    
                    // Vô hiệu hóa nút bấm và xóa link
                    submitBtn.style.pointerEvents = 'none';
                    submitBtn.style.opacity = '0.5';
                    submitBtn.style.background = '#475569';
                    submitBtn.innerText = 'Giao dịch đã hủy do quá thời gian';
                    submitBtn.removeAttribute("href");
                }
            }, 1000);
        });
    </script>
</body>
</html>