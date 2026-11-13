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

    public String register(String email, String phone, String password, String fullName) {
        if (phone == null || !phone.matches("0\\d{9}")) {
            return "Số điện thoại không hợp lệ! Vui lòng nhập 10 chữ số và bắt đầu bằng số 0.";
        }
        if (usersDAO.findByEmail(email) != null) {
            return "Email đã được sử dụng, vui lòng chọn email khác!";
        }
        if (usersDAO.findByPhone(phone) != null) {
            return "Số điện thoại này đã được đăng ký bởi tài khoản khác!";
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
            return null;
        }
        return "Lỗi hệ thống khi đăng ký. Vui lòng thử lại sau!";
    }

    public Users login(String email, String password) {
        Users user = usersDAO.findByEmail(email);
        if (user != null && user.getStatus().equals("ACTIVE")) {
            if (PasswordHasher.verify(password, user.getPasswordHash())) {
                usersDAO.updateLastLogin(user.getUserId());
                return user;
            }
        }
        return null;
    }

    public java.util.Map<String, Object> forgotPassword(String email) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        Users user = usersDAO.findByEmail(email);
        if (user == null || !user.getStatus().equals("ACTIVE")) {
            result.put("error", "Email không tồn tại hoặc tài khoản đang bị khóa!");
            return result;
        }
        userVerificationsDAO.deactivateOldOTPs(user.getUserId(), "RECOVERY");
        String otp = OTPGenerator.generateOTP();
        if (userVerificationsDAO.insertOTP(user.getUserId(), "RECOVERY", otp, 5)) {
            final String userEmail = email;
            final String userName = user.getFullName();
            final String finalOtp = otp;

            new Thread(() -> {
                try {
                    EmailUtil.guiMaOTP(userEmail, userName, finalOtp, 5,
                            "Mã Xác Thực Đặt Lại Mật Khẩu",
                            "Hệ thống nhận được yêu cầu đặt lại mật khẩu cho tài khoản liên kết với địa chỉ email này. Dưới đây là mã xác thực OTP của bạn:");
                } catch (Exception e) {
                    System.err.println("Lỗi gửi email ngầm: " + e.getMessage());
                }
            }).start();

            java.sql.Timestamp expiresAt = userVerificationsDAO.getLatestOTPExpiry(user.getUserId(), "RECOVERY");
            if (expiresAt == null) {
                result.put("expiresAt", System.currentTimeMillis() + (5 * 60 * 1000));
            } else {
                result.put("expiresAt", expiresAt.getTime());
            }
            return result;
        }
        result.put("error", "Không thể tạo mã OTP lúc này!");
        return result;
    }

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

    // Đã thêm tham số type để dùng chung cho RECOVERY và EMAIL
    public java.sql.Timestamp getOTPExpiry(String email, String type) {
        if (email == null) {
            return null;
        }
        Users user = usersDAO.findByEmail(email);
        if (user != null) {
            return userVerificationsDAO.getLatestOTPExpiry(user.getUserId(), type);
        }
        return null;
    }

    // --- BỔ SUNG: HÀM GỬI OTP XÁC THỰC TÀI KHOẢN ---
    public String sendVerificationOTP(String email) {
        Users user = usersDAO.findByEmail(email);
        if (user == null) {
            return "Tài khoản không tồn tại!";
        }
        if (user.getIsEmailVerified()) {
            return "Tài khoản đã được xác thực!";
        }
        userVerificationsDAO.deactivateOldOTPs(user.getUserId(), "EMAIL");
        String otp = OTPGenerator.generateOTP();
        if (userVerificationsDAO.insertOTP(user.getUserId(), "EMAIL", otp, 5)) {
            final String userEmail = email;
            final String userName = user.getFullName();
            final String finalOtp = otp;

            new Thread(() -> {
                try {
                    EmailUtil.guiMaOTP(userEmail, userName, finalOtp, 5,
                            "Mã Xác Thực Tài Khoản",
                            "Cảm ơn bạn đã đăng ký tài khoản tại GearZone. Vui lòng sử dụng mã OTP dưới đây để hoàn tất việc xác thực email:");
                } catch (Exception e) {
                    System.err.println("Lỗi gửi email ngầm: " + e.getMessage());
                }
            }).start();
            return null;
        }
        return "Không thể tạo mã xác thực lúc này!";
    }

    // --- BỔ SUNG: HÀM CHECK OTP XÁC THỰC ---
    public String verifyAccount(String email, String otp) {
        Users user = usersDAO.findByEmail(email);
        if (user == null) {
            return "Người dùng không hợp lệ!";
        }

        UserVerifications uv = userVerificationsDAO.findValidOTP(user.getUserId(), "EMAIL", otp);
        if (uv == null) {
            return "Mã OTP không đúng hoặc đã quá hạn 5 phút!";
        }

        if (usersDAO.markEmailVerified(user.getUserId())) {
            userVerificationsDAO.markAsVerified(uv.getVerificationId());
            return null;
        }
        return "Xảy ra lỗi khi cập nhật trạng thái xác thực!";
    }
}
