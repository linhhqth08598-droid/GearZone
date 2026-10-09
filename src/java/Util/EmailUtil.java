package Util;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Tiện ích gửi email qua SMTP (Gmail) bằng thư viện jakarta.mail.
 * Phục vụ gửi mã OTP xác nhận quên mật khẩu cho hệ thống GearZone.
 */
public class EmailUtil {

    // ==== CẤU HÌNH TÀI KHOẢN GỬI ====
    private static final String SENDER_EMAIL = "smartfarmmanage@gmail.com";
    private static final String SENDER_APP_PASSWORD = "grxhduafxftczxjm"; // App Password Gmail
    private static final String SENDER_NAME = "GearZone Support";

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private static boolean isConfigured() {
        return SENDER_EMAIL != null && !SENDER_EMAIL.isEmpty()
                && SENDER_APP_PASSWORD != null && !SENDER_APP_PASSWORD.isEmpty();
    }

    /**
     * Gửi email dạng HTML. Trả về true nếu gửi thành công.
     */
    public static boolean send(String toEmail, String subject, String htmlBody) {
        if (!isConfigured()) {
            System.out.println("======================================================");
            System.out.println("[EMAIL - CHẾ ĐỘ DEV, CHƯA CẤU HÌNH SMTP]");
            System.out.println("Gửi tới : " + toEmail);
            System.out.println("Tiêu đề : " + subject);
            System.out.println("Nội dung:\n" + htmlBody.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim());
            System.out.println("======================================================");
            return true;
        }

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
            message.setContent(htmlBody, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("[EMAIL] Đã gửi email tới " + toEmail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[EMAIL - GỬI LỖI, IN RA CONSOLE] tới " + toEmail + " | " + subject);
            return false;
        }
    }

    /** 
     * Gửi mã OTP xác nhận quên mật khẩu (GearZone Theme). 
     */
    public static void guiMaOTP(String toEmail, String hoTen, String maXacThuc, int soPhutHieuLuc) {
        String subject = "[GearZone] Mã Xác Thực Đặt Lại Mật Khẩu";
        
        // CSS Style đồng bộ với giao diện Dark Mode của web GearZone
        String body = "<div style='font-family: \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; background-color: #0f1117; padding: 40px 20px;'>"
                + "<div style='max-width: 500px; margin: 0 auto; background-color: #1a1d29; border: 1px solid #2d3748; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.4); overflow: hidden;'>"
                
                // Header Email (Logo GearZone)
                + "<div style='background-color: #1a1d29; border-bottom: 2px solid #ff4757; padding: 20px; text-align: center;'>"
                + "<h2 style='margin: 0; font-size: 26px; font-weight: 800; color: #fff; letter-spacing: 1px;'>GEAR<span style='color: #ff4757;'>ZONE</span></h2>"
                + "</div>"
                
                // Nội dung chính
                + "<div style='padding: 30px 35px; color: #cbd5e1;'>"
                + "<p style='margin-bottom: 15px; font-size: 15px;'>Xin chào <b style='color: #fff;'>" + safe(hoTen) + "</b>,</p>"
                + "<p style='margin-bottom: 20px; font-size: 14px; line-height: 1.6;'>Hệ thống nhận được yêu cầu đặt lại mật khẩu cho tài khoản liên kết với địa chỉ email này. Dưới đây là mã xác thực OTP của bạn:</p>"
                
                // Hộp hiển thị mã OTP
                + "<div style='background-color: #0f1117; border: 1px solid #334155; border-radius: 6px; padding: 20px; text-align: center; margin-bottom: 25px;'>"
                + "<span style='font-size: 32px; font-weight: 700; letter-spacing: 8px; color: #ff4757;'>" + safe(maXacThuc) + "</span>"
                + "</div>"
                
                + "<p style='margin-bottom: 10px; font-size: 14px;'>Mã xác thực này sẽ hết hạn trong vòng <b style='color: #ff4757;'>" + soPhutHieuLuc + " phút</b>.</p>"
                + "<p style='font-size: 13px; color: #94a3b8; line-height: 1.5;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này hoặc liên hệ ngay với bộ phận hỗ trợ để bảo vệ tài khoản.</p>"
                + "</div>"
                
                // Footer
                + "<div style='background-color: #141722; border-top: 1px solid #242938; padding: 20px; text-align: center;'>"
                + "<p style='margin: 0; font-size: 12px; color: #64748b;'>&copy; 2026 GearZone - Chuyên linh kiện PC & Phụ kiện Gaming.</p>"
                + "<p style='margin: 5px 0 0 0; font-size: 11px; color: #4a5568;'>Email được gửi tự động, vui lòng không trả lời.</p>"
                + "</div>"
                
                + "</div>"
                + "</div>";
                
        send(toEmail, subject, body);
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace("<", "&lt;").replace(">", "&gt;");
    }
}