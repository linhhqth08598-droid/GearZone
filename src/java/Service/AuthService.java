package Service;

import DAO.UserDAO;
import DAO.OTPDAO;
import Models.User;
import Models.OTPToken;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.Random;

// Khai báo các thư viện jakarta.mail để gửi email
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class AuthService {
    private UserDAO userDAO;
    private OTPDAO otpDAO;

    // ==== CẤU HÌNH GỬI EMAIL (Nhúng trực tiếp vào Service) ====
    private static final String SENDER_EMAIL = "smartfarmmanage@gmail.com";
    private static final String SENDER_APP_PASSWORD = "grxhduafxftczxjm"; 
    private static final String SENDER_NAME = "GearZone System";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public AuthService() {
        this.userDAO = new UserDAO();
        this.otpDAO = new OTPDAO();
    }

    // Luồng 1: Yêu cầu lấy mã OTP cho quên mật khẩu
    public String processForgotPassword(String email) {
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            return "Email không tồn tại trong hệ thống!";
        }

        // 1. Sinh mã OTP ngẫu nhiên 6 số
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        
        // 2. Thời gian hết hạn: 5 phút tính từ hiện tại
        Timestamp expiration = new Timestamp(System.currentTimeMillis() + (5 * 60 * 1000));
        
        // 3. Lưu OTP vào database
        OTPToken otp = new OTPToken(user.getUserId(), otpCode, "FORGOT_PASSWORD", expiration);
        if (otpDAO.insertOTP(otp)) {
            // 4. Gọi hàm gửi email được viết trực tiếp bên dưới
            boolean isEmailSent = sendEmailOTP(email, user.getFullName(), otpCode, 5);
            if (isEmailSent) {
                return "SUCCESS";
            } else {
                return "Tạo mã OTP thành công nhưng hệ thống gửi email đang gặp lỗi!";
            }
        }
        return "Lỗi hệ thống khi khởi tạo mã OTP trong cơ sở dữ liệu!";
    }

    // Luồng 2: Xác thực mã OTP người dùng nhập vào
    public String verifyOTP(String email, String otpCode) {
        User user = userDAO.getUserByEmail(email);
        if (user == null) return "Lỗi dữ liệu!";

        OTPToken validToken = otpDAO.getValidOTP(user.getUserId(), otpCode, "FORGOT_PASSWORD");
        if (validToken != null) {
            otpDAO.markAsUsed(validToken.getTokenId());
            return "SUCCESS";
        }
        return "Mã OTP không hợp lệ hoặc đã hết hạn!";
    }

    // Luồng 3: Đổi lại mật khẩu mới
    public String resetPassword(String email, String newPassword, String confirm) {
        if (!newPassword.equals(confirm)) {
            return "Mật khẩu xác nhận không khớp!";
        }
        if (userDAO.updatePassword(email, newPassword)) {
            return "SUCCESS";
        }
        return "Lỗi cập nhật mật khẩu mới!";
    }

    // ==== LOGIC GỬI EMAIL (Thực thi SMTP bằng jakarta.mail) ====
    private boolean sendEmailOTP(String toEmail, String hoTen, String maXacThuc, int soPhutHieuLuc) {
        String subject = "[GearZone] Mã xác thực đặt lại mật khẩu";
        // Customize màu sắc giao diện email cho hợp style GearZone
        String body = "<div style='font-family:Arial,sans-serif;max-width:560px;margin:auto;border:1px solid #e9edf4;border-radius:12px;overflow:hidden'>"
                + "<div style='background:#ff4757;color:#fff;padding:20px 24px'><h2 style='margin:0'>🔐 GearZone Security</h2></div>"
                + "<div style='padding:24px;color:#333'>"
                + "<p>Xin chào <b>" + (hoTen != null ? hoTen.replace("<", "&lt;") : "bạn") + "</b>,</p>"
                + "<p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu từ tài khoản của bạn. Mã xác thực (OTP) của bạn là:</p>"
                + "<p style='font-size:32px;letter-spacing:8px;text-align:center;padding:16px;background:#f5f7fa;border-radius:8px;color:#1e2a3a'><b>" + maXacThuc + "</b></p>"
                + "<p>Mã này có hiệu lực trong vòng <b>" + soPhutHieuLuc + " phút</b>. Nếu bạn không thực hiện yêu cầu này, vui lòng phớt lờ email.</p>"
                + "</div>"
                + "<div style='background:#f5f7fa;color:#8aa3c0;padding:14px 24px;font-size:12px'>Email được tạo tự động, vui lòng không trả lời.</div>"
                + "</div>";

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME, "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject, "UTF-8");
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("[GearZone] Đã gửi OTP thành công tới: " + toEmail);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Các hàm Đăng ký và Đăng nhập hiện có của bạn ---
    public String register(String username, String password, String confirm, String email, String fullName, String phone) {
        if (!password.equals(confirm)) {
            return "Mật khẩu xác nhận không khớp!";
        }
        if (userDAO.checkExist(username, email)) {
            return "Tên đăng nhập hoặc Email đã tồn tại!";
        }
        User newUser = new User(4, username, password, email, fullName, phone); 
        if (userDAO.insertUser(newUser)) {
            return "SUCCESS";
        }
        return "Lỗi hệ thống khi đăng ký!";
    }

    public User login(String username, String password) {
        return userDAO.checkLogin(username, password);
    }
}