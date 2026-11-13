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
                    <input type="text" class="otp-box" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box" maxlength="1" pattern="\d" inputmode="numeric" required>
                    <input type="text" class="otp-box" maxlength="1" pattern="\d" inputmode="numeric" required>
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

            <button type="submit" id="btn-submit" class="btn btn-primary w-100 mb-3">Xác nhận đổi mật khẩu</button>
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
    // Hàm dùng chung để ẩn/hiện mật khẩu
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
        // 1. XỬ LÝ ĐỒNG HỒ ĐẾM NGƯỢC CỦA OTP
        // =======================================================
        let otpExpiresAt = ${expiresAt != null ? expiresAt : (System.currentTimeMillis() + 300000)};
        const timeDisplay = document.getElementById('time-display');
        const countdownTimer = document.getElementById('countdown-timer');
        const btnSubmit = document.getElementById('btn-submit');

        function updateOtpTimer() {
            let now = new Date().getTime();
            let distance = otpExpiresAt - now;

            if (distance <= 0) {
                clearInterval(intervalOtp);
                timeDisplay.innerHTML = "Hết hạn";
                countdownTimer.classList.replace('bg-danger', 'bg-secondary');
                btnSubmit.disabled = true;
            } else {
                let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                let seconds = Math.floor((distance % (1000 * 60)) / 1000);
                timeDisplay.innerHTML = (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
            }
        }
        updateOtpTimer();
        const intervalOtp = setInterval(updateOtpTimer, 1000);

        // =======================================================
        // 2. XỬ LÝ NÚT GỬI LẠI MÃ (Chống Spam 60s)
        // =======================================================
        const btnResend = document.getElementById('btn-resend');
        const resendTimerSpan = document.getElementById('resend-timer');
        
        let resendExpiresAt = sessionStorage.getItem('resendExpiresAt');
        
        if (!resendExpiresAt) {
            resendExpiresAt = new Date().getTime() + 60000;
            sessionStorage.setItem('resendExpiresAt', resendExpiresAt);
        } else {
            resendExpiresAt = parseInt(resendExpiresAt);
        }

        function updateResendTimer() {
            let now = new Date().getTime();
            let distance = resendExpiresAt - now;

            if (distance <= 0) {
                clearInterval(intervalResend);
                btnResend.disabled = false;
                resendTimerSpan.innerHTML = "";
                sessionStorage.removeItem('resendExpiresAt');
            } else {
                let secondsLeft = Math.floor(distance / 1000);
                resendTimerSpan.innerHTML = "(" + secondsLeft + "s)";
            }
        }
        updateResendTimer();
        const intervalResend = setInterval(updateResendTimer, 1000);

        // =======================================================
        // 3. XỬ LÝ NHẬP 6 Ô OTP
        // =======================================================
        const otpInputs = document.querySelectorAll('.otp-box');
        const hiddenInput = document.getElementById('otp-hidden');

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

                digits.forEach((digit, i) => {
                    if (index + i < otpInputs.length) {
                        otpInputs[index + i].value = digit;
                    }
                });

                const nextIndex = Math.min(index + digits.length, otpInputs.length - 1);
                otpInputs[nextIndex].focus();
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