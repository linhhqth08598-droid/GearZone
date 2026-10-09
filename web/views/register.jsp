<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đăng Ký Tài Khoản Mới - GearZone</title>
        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/views/images/GearZone.png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/views/css/style.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <body>
        <!-- Header của bạn giữ nguyên ở đây -->

        <div class="container">
            <div class="form-box">
                <h2 style="color: #fff; margin-bottom: 20px; text-align: center;">Tạo Tài Khoản GearZone</h2>
                <% String error = (String) request.getAttribute("error");
                    if (error != null) {%>
                <p style="color: #ff4757; text-align: center;"><%= error%></p>
                <% }%>
                <form action="${pageContext.request.contextPath}/register" method="post">

                    <div class="form-group">
                        <label>Họ và tên</label>
                        <input type="text" name="fullName" id="fullName" required placeholder="Nhập họ và tên...">
                    </div>

                    <div class="form-group">
                        <label>Số điện thoại</label>
                        <!-- Giới hạn max 10 ký tự, validate pattern bắt buộc đầu là 0 -->
                        <input type="tel" name="phone" id="phone" required placeholder="Ví dụ: 0987654321..." maxlength="10" pattern="0[0-9]{9}" title="Số điện thoại phải có đúng 10 số và bắt đầu bằng số 0">
                    </div>

                    <div class="form-group">
                        <label>Địa chỉ Email</label>
                        <input type="email" name="email" required placeholder="Nhập email...">
                    </div>

                    <div class="form-group">
                        <label>Tên đăng nhập</label>
                        <input type="text" name="username" id="username" required placeholder="Nhập tên đăng nhập...">
                    </div>

                    <div class="form-group">
                        <label>Mật khẩu</label>
                        <div class="password-wrapper">
                            <input type="password" name="password" id="password" required placeholder="Nhập mật khẩu (tối thiểu 6 ký tự)...">
                            <i class="fa-regular fa-eye toggle-password" onclick="toggleVisibility('password', this)"></i>
                        </div>

                        <!-- Thanh hiển thị độ mạnh mật khẩu -->
                        <div id="password-strength" style="margin-top: 8px; font-size: 13px; display: none;">
                            Độ bảo mật: <span id="strength-text" style="font-weight: bold;"></span>
                            <div style="height: 5px; background: #334155; margin-top: 5px; border-radius: 3px; overflow: hidden;">
                                <div id="strength-bar" style="height: 100%; width: 0%; transition: all 0.3s ease;"></div>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Xác nhận mật khẩu</label>
                        <div class="password-wrapper">
                            <input type="password" name="confirmPassword" id="confirmPassword" required placeholder="Nhập lại mật khẩu (tối thiểu 6 ký tự)...">
                            <i class="fa-regular fa-eye toggle-password" onclick="toggleVisibility('confirmPassword', this)"></i>
                        </div>
                    </div>

                    <button type="submit" class="btn-submit">Đăng Ký Thành Viên</button>
                    <p style="margin-top: 18px; font-size: 14px; text-align: center; color: #94a3b8;">
                        Đã có tài khoản? <a href="${pageContext.request.contextPath}/login" style="color: #ff4757; text-decoration: none; font-weight: bold;">Đăng nhập tại đây</a>
                    </p>
                </form>
            </div>
        </div>

        <script>
            // ==========================================
            // 1. KIỂM SOÁT NHẬP SỐ ĐIỆN THOẠI (Chỉ nhập số, Max 10, Bắt đầu bằng 0)
            // ==========================================
            const phoneInput = document.getElementById('phone');
            phoneInput.addEventListener('input', function (e) {
                // Xóa mọi ký tự không phải là số
                this.value = this.value.replace(/[^0-9]/g, '');

                // Nếu ký tự đầu tiên được nhập không phải là '0' thì xóa đi
                if (this.value.length > 0 && this.value[0] !== '0') {
                    this.value = '';
                }
            });

            // ==========================================
            // 2. TỰ ĐỘNG TẠO TÊN ĐĂNG NHẬP
            // ==========================================
            const fullNameInput = document.getElementById('fullName');
            const usernameInput = document.getElementById('username');
            let isUsernameEdited = false;

            usernameInput.addEventListener('input', function () {
                isUsernameEdited = true;
            });

            fullNameInput.addEventListener('blur', function () {
                if (!isUsernameEdited && this.value.trim() !== '') {
                    let str = this.value.trim().toLowerCase();
                    str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
                    str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
                    str = str.replace(/ì|í|ị|ỉ|ĩ/g, "i");
                    str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
                    str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
                    str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
                    str = str.replace(/đ/g, "d");

                    const words = str.split(/\s+/);
                    if (words.length > 1) {
                        let generatedUsername = words[words.length - 1];
                        for (let i = 0; i < words.length - 1; i++) {
                            generatedUsername += words[i].charAt(0);
                        }
                        usernameInput.value = generatedUsername;
                    } else {
                        usernameInput.value = words[0];
                    }
                }
            });

            // ==========================================
            // 3. ẨN / HIỆN MẬT KHẨU
            // ==========================================
            function toggleVisibility(inputId, iconElement) {
                const input = document.getElementById(inputId);
                if (input.type === "password") {
                    input.type = "text";
                    // Bỏ icon mắt mở, thêm icon mắt nhắm chéo
                    iconElement.classList.remove("fa-eye");
                    iconElement.classList.add("fa-eye-slash");
                } else {
                    input.type = "password";
                    // Bỏ icon mắt nhắm, thêm lại icon mắt mở
                    iconElement.classList.remove("fa-eye-slash");
                    iconElement.classList.add("fa-eye");
                }
            }

            // ==========================================
            // 4. KIỂM TRA ĐỘ MẠNH YẾU MẬT KHẨU
            // ==========================================
            const passwordInput = document.getElementById('password');
            const strengthDiv = document.getElementById('password-strength');
            const strengthText = document.getElementById('strength-text');
            const strengthBar = document.getElementById('strength-bar');

            passwordInput.addEventListener('input', function () {
                const val = this.value;
                if (val.length === 0) {
                    strengthDiv.style.display = 'none';
                    return;
                }
                strengthDiv.style.display = 'block';

                let strengthPoint = 0;
                if (val.length >= 6)
                    strengthPoint += 1;
                if (val.length >= 10)
                    strengthPoint += 1;
                if (/[A-Z]/.test(val))
                    strengthPoint += 1;
                if (/[0-9]/.test(val))
                    strengthPoint += 1;
                if (/[^A-Za-z0-9]/.test(val))
                    strengthPoint += 1;

                if (strengthPoint <= 2) {
                    strengthText.textContent = 'Yếu';
                    strengthText.style.color = '#ff4757';
                    strengthBar.style.width = '33%';
                    strengthBar.style.backgroundColor = '#ff4757';
                } else if (strengthPoint === 3 || strengthPoint === 4) {
                    strengthText.textContent = 'Trung bình';
                    strengthText.style.color = '#ffa502';
                    strengthBar.style.width = '66%';
                    strengthBar.style.backgroundColor = '#ffa502';
                } else {
                    strengthText.textContent = 'Mạnh';
                    strengthText.style.color = '#2ed573';
                    strengthBar.style.width = '100%';
                    strengthBar.style.backgroundColor = '#2ed573';
                }
            });
        </script>
    </body>
</html>