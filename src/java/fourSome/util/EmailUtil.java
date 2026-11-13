package fourSome.util;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Tiện ích gửi email qua SMTP (Gmail) bằng thư viện jakarta.mail. Phục vụ gửi
 * mã OTP xác nhận quên mật khẩu cho hệ thống GearZone.
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
            System.out.println("[EMAIL - CHƯA CẤU HÌNH SMTP]");
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
            System.out.println("[EMAIL] Đa gui email toi " + toEmail);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[EMAIL - GUI LOI, IN RA CONSOLE] toi " + toEmail + " | " + subject);
            return false;
        }
    }

    /**
     * Gửi mã OTP xác nhận quên mật khẩu (GearZone Theme).
     */
    public static void guiMaOTP(String toEmail, String hoTen, String maXacThuc, int soPhutHieuLuc) {
        String subject = "[GearZone] Mã Xác Thực Đặt Lại Mật Khẩu";

        String body = "<div style='font-family: \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; background-color: #f1f5f9; padding: 30px 20px;'>"
                + "<div style='max-width: 500px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); overflow: hidden;'>"
                // Header Email (Logo GearZone) - Thu gọn padding
                + "<div style='background-color: #ffffff; border-bottom: 2px solid #ff4757; padding: 15px; text-align: center;'>"
                + "<h2 style='margin: 0; font-size: 24px; font-weight: 800; color: #0f1117; letter-spacing: 1px;'>GEAR<span style='color: #ff4757;'>ZONE</span></h2>"
                + "</div>"
                // Nội dung chính
                + "<div style='padding: 25px 30px; color: #334155;'>"
                + "<p style='margin: 0 0 15px 0; font-size: 15px;'>Xin chào <b style='color: #0f1117;'>" + safe(hoTen) + "</b>,</p>"
                + "<p style='margin: 0 0 20px 0; font-size: 14px; line-height: 1.5;'>Hệ thống nhận được yêu cầu đặt lại mật khẩu cho tài khoản liên kết với địa chỉ email này. Dưới đây là mã xác thực OTP của bạn:</p>"
                // Hộp hiển thị mã OTP - Căn giữa hoàn hảo
                + "<div style='background-color: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 6px; padding: 15px; text-align: center; margin-bottom: 20px;'>"
                + "<span style='font-size: 32px; font-weight: 700; letter-spacing: 6px; color: #ff4757; margin-left: 6px;'>" + safe(maXacThuc) + "</span>"
                + "</div>"
                + "<p style='margin: 0 0 10px 0; font-size: 14px;'>Mã xác thực này sẽ hết hạn trong vòng <b style='color: #ff4757;'>" + soPhutHieuLuc + " phút</b>.</p>"
                + "<p style='margin: 0; font-size: 13px; color: #64748b; line-height: 1.5;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này hoặc liên hệ ngay với bộ phận hỗ trợ để bảo vệ tài khoản.</p>"
                + "</div>"
                // Footer
                + "<div style='background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 15px; text-align: center;'>"
                + "<p style='margin: 0; font-size: 12px; color: #94a3b8;'>&copy; 2026 GearZone - Chuyên linh kiện PC & Phụ kiện Gaming.</p>"
                + "<p style='margin: 5px 0 0 0; font-size: 11px; color: #cbd5e1;'>Email được gửi tự động, vui lòng không trả lời.</p>"
                + "</div>"
                + "</div>"
                + "</div>";

        send(toEmail, subject, body);
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace("<", "&lt;").replace(">", "&gt;");
    }
}
