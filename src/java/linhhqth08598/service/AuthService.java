package linhhqth08598.service;

import fourSome.model.Users;
import fourSome.model.Roles;
import fourSome.model.UserVerifications;
import fourSome.util.PasswordHasher;
import fourSome.util.EmailUtil;
import fourSome.util.OTPGenerator;
import linhhqth08598.dao.UsersDAO;
import linhhqth08598.dao.UserVerificationsDAO;
import linhhqth08598.dao.RolesDAO;
import linhhqth08598.dao.UserRolesDAO;

public class AuthService {

    private UsersDAO usersDAO = new UsersDAO();
    private UserVerificationsDAO userVerificationsDAO = new UserVerificationsDAO();
    private RolesDAO rolesDAO = new RolesDAO();
    private UserRolesDAO userRolesDAO = new UserRolesDAO();

    // 1. Đăng ký tài khoản mới
    public String register(String email, String phone, String password, String fullName) {
        if (usersDAO.findByEmail(email) != null) {
            return "Email đã được sử dụng, vui lòng chọn email khác!";
        }

        Users user = new Users();
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPasswordHash(PasswordHasher.hash(password));
        user.setFullName(fullName);
        user.setIsEmailVerified(false);
        user.setIsPhoneVerified(false);
        user.setStatus("ACTIVE");

        if (usersDAO.insert(user)) {
            Users newUser = usersDAO.findByEmail(email);
            Roles customerRole = rolesDAO.findByCode("CUSTOMER");
            if (customerRole != null) {
                userRolesDAO.assignRole(newUser.getUserId(), customerRole.getRoleId());
            }
            return null; // Đăng ký thành công, không có lỗi
        }
        return "Lỗi hệ thống khi đăng ký. Vui lòng thử lại sau!";
    }

    // 2. Kiểm tra Đăng nhập
    public Users login(String email, String password) {
        Users user = usersDAO.findByEmail(email);
        if (user != null && user.getStatus().equals("ACTIVE")) {
            if (PasswordHasher.verify(password, user.getPasswordHash())) {
                usersDAO.updateLastLogin(user.getUserId());
                return user; // Đăng nhập thành công, trả về Object User để lưu vào Session
            }
        }
        return null;
    }

    // Sửa lại hàm forgotPassword trong AuthService.java
    public java.util.Map<String, Object> forgotPassword(String email) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        Users user = usersDAO.findByEmail(email);
        if (user == null || !user.getStatus().equals("ACTIVE")) {
            result.put("error", "Email không tồn tại hoặc tài khoản đang bị khóa!");
            return result;
        }

        String otp = OTPGenerator.generateOTP();
        if (userVerificationsDAO.insertOTP(user.getUserId(), "RECOVERY", otp, 5)) {

            // TẠO LUỒNG RIÊNG ĐỂ GỬI EMAIL NGẦM (KHÔNG BẮT NGƯỜI DÙNG CHỜ)
            final String userEmail = email;
            final String userName = user.getFullName();
            final String finalOtp = otp;

            new Thread(() -> {
                try {
                    EmailUtil.guiMaOTP(userEmail, userName, finalOtp, 5);
                } catch (Exception e) {
                    System.err.println("Lỗi gửi email ngầm: " + e.getMessage());
                }
            }).start();

            // Lấy thời gian hết hạn từ DB
            java.sql.Timestamp expiresAt = userVerificationsDAO.getLatestOTPExpiry(user.getUserId(), "RECOVERY");
            result.put("expiresAt", expiresAt);
            return result;
        }
        result.put("error", "Không thể tạo mã OTP lúc này!");
        return result;
    }

    // 4. Xác nhận OTP và đặt lại mật khẩu mới
    public String resetPassword(String email, String otp, String newPassword) {
        Users user = usersDAO.findByEmail(email);
        if (user == null) {
            return "Người dùng không hợp lệ!";
        }

        UserVerifications uv = userVerificationsDAO.findValidOTP(user.getUserId(), "RECOVERY", otp);
        if (uv == null) {
            return "Mã OTP không đúng hoặc đã quá hạn 5 phút!";
        }

        String newHash = PasswordHasher.hash(newPassword);
        if (usersDAO.updatePassword(user.getUserId(), newHash)) {
            userVerificationsDAO.markAsVerified(uv.getVerificationId());
            return null;
        }
        return "Xảy ra lỗi khi cập nhật mật khẩu!";
    }
}
