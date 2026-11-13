<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/views/components/header.jsp" />

<div class="container auth-container my-5" style="max-width: 500px;">
    <div class="auth-card">
        <h4 class="text-center mb-2">QUÊN MẬT KHẨU</h4>
        <p class="text-center mb-3 text-muted" style="font-size: 14px;">Mã OTP đã được gửi đến email <b class="text-dark">${email}</b>.</p>

        <!-- ĐỒNG HỒ ĐẾM NGƯỢC OTP -->
        <div class="text-center mb-4">
            <span class="badge bg-danger fs-6 p-2" id="countdown-timer">
                <i class="fas fa-clock me-1"></i> <span id="time-display">00:00</span>
            </span>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-success">${sessionScope.message}</div>
            <c:remove var="message" scope="session" />
        </c:if>

        <form id="reset-form" action="${pageContext.request.contextPath}/auth" method="POST" onsubmit="return validateReset()" autocomplete="off">
            <input type="hidden" name="action" value="reset">
            <input type="hidden" name="email" value="${email}">

            <!-- 6 ô OTP -->
            <div class="mb-4 text-center">
                <label class="form-label text-center w-100 mb-3">Mã Xác Thực (OTP)</label>
                <div class="d-flex justify-content-center gap-2" id="otp-container">
                    <input type="text" class="otp-box form-control" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box form-control" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box form-control" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box form-control" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box form-control" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box form-control" maxlength="1" pattern="\d" inputmode="numeric" required>
                </div>
                <input type="hidden" name="otp" id="otp-hidden">
            </div>

            <div class="mb-3">
                <label class="form-label">Mật khẩu mới</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-lock"></i></span>
                    <input type="password" id="newPassword" name="newPassword" class="form-control" required minlength="8" placeholder="Tối thiểu 8 ký tự" autocomplete="new-password">
                    <button type="button" class="toggle-password" onclick="togglePassword('newPassword', this)">
                        <i class="fas fa-eye"></i>
                    </button>
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label">Xác nhận mật khẩu mới</label>
                <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-lock"></i></span>
                    <input type="password" id="confirmNewPassword" class="form-control" required placeholder="Nhập lại mật khẩu mới">
                    <button type="button" class="toggle-password" onclick="togglePassword('confirmNewPassword', this)">
                        <i class="fas fa-eye"></i>
                    </button>
                </div>
                <small id="pwdResetError" class="text-danger mt-1" style="display: none; font-size: 13px;"><i class="fas fa-exclamation-circle"></i> Mật khẩu không khớp!</small>
            </div>

            <button type="submit" class="btn btn-primary w-100 mb-3" style="background: linear-gradient(135deg, #ff4757 0%, #ff2a3d 100%) !important; border: none;">Xác nhận đổi mật khẩu</button>
        </form>

        <!-- NÚT GỬI LẠI MÃ OTP -->
        <div class="text-center mt-3 border-top pt-3">
            <span class="text-muted" style="font-size: 14px;">Chưa nhận được mã?</span>
            <form action="${pageContext.request.contextPath}/auth" method="POST" class="d-inline" id="resend-form">
                <input type="hidden" name="action" value="forget">
                <input type="hidden" name="email" value="${email}">
                <button type="submit" id="btn-resend" class="btn btn-link text-decoration-none fw-bold" style="color: #ff4757; padding: 0;" disabled>
                    Gửi lại mã <span id="resend-timer"></span>
                </button>
            </form>
        </div>
    </div>
</div>

<script>
    function togglePassword(inputId, iconElement) {
        const input = document.getElementById(inputId);
        const icon = iconElement.querySelector('i');
        
        if (input.type === "password") {
            input.type = "text";
            icon.classList.remove("fa-eye");
            icon.classList.add("fa-eye-slash");
        } else {
            input.type = "password";
            icon.classList.remove("fa-eye-slash");
            icon.classList.add("fa-eye");
        }
    }

    document.addEventListener("DOMContentLoaded", function () {
        // =======================================================
        // 1. XỬ LÝ ĐỒNG HỒ ĐẾM NGƯỢC CỦA OTP & KHÓA Ô NHẬP
        // =======================================================
        let rawExpiresAt = ${expiresAt != null ? expiresAt : 0};
        // Lấy thời gian hiện tại của Server (truyền từ JSP xuống)
        let serverNow = ${serverTime != null ? serverTime : System.currentTimeMillis()};
        
        let otpExpiresAt = (rawExpiresAt === 0) ? 0 : rawExpiresAt; 
        
        const timeDisplay = document.getElementById('time-display');
        const countdownTimer = document.getElementById('countdown-timer');
        const btnSubmit = document.getElementById('btn-submit');
        const otpInputs = document.querySelectorAll('.otp-box');
        const hiddenInput = document.getElementById('otp-hidden');
        const btnResend = document.getElementById('btn-resend');

        function lockAllInputs() {
            timeDisplay.innerHTML = "Hết hạn";
            countdownTimer.classList.replace('bg-danger', 'bg-secondary');
            btnSubmit.disabled = true;
            
            document.getElementById('newPassword').disabled = true;
            document.getElementById('confirmNewPassword').disabled = true;
            
            otpInputs.forEach(input => {
                input.disabled = true;
                input.style.backgroundColor = "#e2e8f0"; 
                input.style.cursor = "not-allowed";
            });

            btnResend.disabled = false;
            document.getElementById('resend-timer').innerHTML = "";
        }

        function updateOtpTimer() {
            if (otpExpiresAt === 0) {
                lockAllInputs();
                return;
            }

            // Tính toán dựa trên thời gian Server
            let distance = otpExpiresAt - serverNow;

            if (distance <= 0) {
                clearInterval(intervalOtp);
                lockAllInputs();
            } else {
                let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                let seconds = Math.floor((distance % (1000 * 60)) / 1000);
                timeDisplay.innerHTML = (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
                
                // Cập nhật lại thời gian Server (cộng thêm 1 giây mỗi lần chạy)
                serverNow += 1000;
            }
        }
        
        updateOtpTimer();
        const intervalOtp = setInterval(updateOtpTimer, 1000);

        // =======================================================
        // 2. XỬ LÝ NÚT GỬI LẠI MÃ (Chống Spam 60s)
        // =======================================================
        const resendTimerSpan = document.getElementById('resend-timer');
        let resendStartTime = sessionStorage.getItem('resendStartTime');
        
        if (!resendStartTime) {
            resendStartTime = new Date().getTime();
            sessionStorage.setItem('resendStartTime', resendStartTime);
        } else {
            resendStartTime = parseInt(resendStartTime);
        }
        
        let resendExpiresAt = resendStartTime + 60000;

        function updateResendTimer() {
            let now = new Date().getTime();
            let distance = resendExpiresAt - now;

            if (distance <= 0) {
                clearInterval(intervalResend);
                btnResend.disabled = false;
                resendTimerSpan.innerHTML = "";
            } else {
                let secondsLeft = Math.floor(distance / 1000);
                resendTimerSpan.innerHTML = "(" + secondsLeft + "s)";
            }
        }
        
        if (otpExpiresAt > serverNow) {
            updateResendTimer();
            var intervalResend = setInterval(updateResendTimer, 1000);
        }

        document.getElementById('resend-form').addEventListener('submit', function() {
            sessionStorage.setItem('resendStartTime', new Date().getTime());
        });

        // =======================================================
        // 3. XỬ LÝ NHẬP 6 Ô OTP
        // =======================================================
        function updateHiddenInput() {
            let otpValue = '';
            otpInputs.forEach(input => {
                otpValue += input.value;
            });
            hiddenInput.value = otpValue;
        }

        otpInputs.forEach((input, index) => {
            input.addEventListener('input', function (e) {
                this.value = this.value.replace(/[^0-9]/g, '');
                if (this.value.length === 1 && index < otpInputs.length - 1) {
                    otpInputs[index + 1].focus();
                }
                updateHiddenInput();
            });

            input.addEventListener('keydown', function (e) {
                if (e.key === 'Backspace' && this.value.length === 0 && index > 0) {
                    otpInputs[index - 1].focus();
                }
            });

            input.addEventListener('paste', function (e) {
                e.preventDefault();
                const pasteData = (e.clipboardData || window.clipboardData).getData('text');
                const digits = pasteData.replace(/[^0-9]/g, '').slice(0, 6).split('');

                otpInputs.forEach(inp => inp.value = ''); 

                digits.forEach((digit, i) => {
                    if (i < otpInputs.length) {
                        otpInputs[i].value = digit;
                    }
                });

                const focusIndex = digits.length < 6 ? digits.length : 5;
                otpInputs[focusIndex].focus();
                updateHiddenInput();
            });
        });

        document.getElementById('reset-form').addEventListener('submit', function () {
            updateHiddenInput();
        });
    });

    function validateReset() {
        var pass = document.getElementById("newPassword").value;
        var confirmPass = document.getElementById("confirmNewPassword").value;
        if (pass !== confirmPass) {
            document.getElementById("pwdResetError").style.display = "block";
            return false;
        }
        return true;
    }
</script>

<jsp:include page="/views/components/footer.jsp" />