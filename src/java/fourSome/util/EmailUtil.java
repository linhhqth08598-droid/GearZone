package fourSome.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {

    private static String SENDER_EMAIL;
    private static String SENDER_APP_PASSWORD;
    private static String SENDER_NAME;
    private static String SMTP_HOST;
    private static String SMTP_PORT;

    private static Session mailSession;
    private static Transport sharedTransport;
    private static final Object TRANSPORT_LOCK = new Object();

    // Dùng 1 luồng duy nhất để tái sử dụng tuần tự kết nối Transport mà không bị xung đột
    private static final ExecutorService MAIL_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");

        try (InputStream is = EmailUtil.class.getClassLoader().getResourceAsStream("email.properties")) {
            if (is != null) {
                Properties config = new Properties();
                config.load(new InputStreamReader(is, StandardCharsets.UTF_8));

                SENDER_EMAIL = config.getProperty("mail.sender.email", "").trim();
                SENDER_APP_PASSWORD = config.getProperty("mail.sender.password", "").trim();
                SENDER_NAME = config.getProperty("mail.sender.name", "GearZone Support").trim();
                SMTP_HOST = config.getProperty("mail.smtp.host", "smtp.gmail.com").trim();
                SMTP_PORT = config.getProperty("mail.smtp.port", "465").trim();

                if (isConfigured()) {
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.host", SMTP_HOST);
                    props.put("mail.smtp.port", SMTP_PORT);
                    props.put("mail.smtp.localhost", "localhost");
                    props.put("mail.smtp.connectiontimeout", "10000");
                    props.put("mail.smtp.timeout", "10000");
                    props.put("mail.smtp.writetimeout", "10000");

                    // Tối ưu SSL trực tiếp nếu dùng cổng 465
                    if ("465".equals(SMTP_PORT)) {
                        props.put("mail.smtp.ssl.enable", "true");
                        props.put("mail.smtp.socketFactory.port", "465");
                        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    } else {
                        props.put("mail.smtp.starttls.enable", "true");
                    }

                    mailSession = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
                        }
                    });
                }
            } else {
                System.err.println("[EMAIL] Không tìm thấy file email.properties trong classpath!");
            }
        } catch (Exception e) {
            System.err.println("[EMAIL] Lỗi khi đọc file email.properties:");
            e.printStackTrace();
        }
    }

    private static boolean isConfigured() {
        return SENDER_EMAIL != null && !SENDER_EMAIL.isEmpty()
                && SENDER_APP_PASSWORD != null && !SENDER_APP_PASSWORD.isEmpty();
    }

    public static void sendAsync(String toEmail, String subject, String htmlBody) {
        MAIL_EXECUTOR.submit(() -> send(toEmail, subject, htmlBody));
    }

    public static boolean send(String toEmail, String subject, String htmlBody) {
        if (!isConfigured() || mailSession == null) {
            System.out.println("[EMAIL - CHƯA CẤU HÌNH SMTP] Tới: " + toEmail + " | " + subject);
            return true;
        }

        long start = System.currentTimeMillis();
        String startTime = LocalDateTime.now().format(TIME_FORMATTER);
        System.out.println("[EMAIL - " + startTime + "] Bắt đầu gửi tới " + toEmail + "...");

        synchronized (TRANSPORT_LOCK) {
            try {
                MimeMessage message = new MimeMessage(mailSession);
                message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME, "UTF-8"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject, "UTF-8");
                message.setContent(htmlBody, "text/html; charset=UTF-8");
                message.saveChanges();

                // Giữ kết nối mở sẵn để dùng lại cho các lần gửi tiếp theo
                if (sharedTransport == null || !sharedTransport.isConnected()) {
                    sharedTransport = mailSession.getTransport("smtp");
                    sharedTransport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), SENDER_EMAIL, SENDER_APP_PASSWORD);
                }

                sharedTransport.sendMessage(message, message.getAllRecipients());

                long elapsed = System.currentTimeMillis() - start;
                String endTime = LocalDateTime.now().format(TIME_FORMATTER);
                System.out.println("[EMAIL - " + endTime + "] Đã gửi tới " + toEmail + " (Mất: " + elapsed + " ms)");
                return true;
            } catch (Exception e) {
                // Nếu kết nối cũ bị Gmail ngắt do để quá lâu, đóng và thử kết nối lại 1 lần
                try {
                    if (sharedTransport != null) {
                        sharedTransport.close();
                    }
                    sharedTransport = mailSession.getTransport("smtp");
                    sharedTransport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), SENDER_EMAIL, SENDER_APP_PASSWORD);

                    MimeMessage retryMsg = new MimeMessage(mailSession);
                    retryMsg.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME, "UTF-8"));
                    retryMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                    retryMsg.setSubject(subject, "UTF-8");
                    retryMsg.setContent(htmlBody, "text/html; charset=UTF-8");
                    retryMsg.saveChanges();

                    sharedTransport.sendMessage(retryMsg, retryMsg.getAllRecipients());
                    long elapsed = System.currentTimeMillis() - start;
                    System.out.println("[EMAIL - RETRY OK] Đã gửi tới " + toEmail + " (Mất: " + elapsed + " ms)");
                    return true;
                } catch (Exception retryEx) {
                    long elapsed = System.currentTimeMillis() - start;
                    System.err.println("[EMAIL - LỖI] Gửi thất bại tới " + toEmail + " sau " + elapsed + " ms");
                    retryEx.printStackTrace();
                    return false;
                }
            }
        }
    }

    public static void guiMaOTP(String toEmail, String hoTen, String maXacThuc, int soPhutHieuLuc, String tieuDe, String loiNhan) {
        String subject = "[GearZone] " + tieuDe;
        String body = "<div style='font-family: \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; background-color: #f1f5f9; padding: 30px 20px;'>"
                + "<div style='max-width: 500px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); overflow: hidden;'>"
                + "<div style='background-color: #ffffff; border-bottom: 2px solid #ff4757; padding: 15px; text-align: center;'>"
                + "<h2 style='margin: 0; font-size: 24px; font-weight: 800; color: #0f1117; letter-spacing: 1px;'>GEAR<span style='color: #ff4757;'>ZONE</span></h2>"
                + "</div>"
                + "<div style='padding: 25px 30px; color: #334155;'>"
                + "<p style='margin: 0 0 15px 0; font-size: 15px;'>Xin chào <b style='color: #0f1117;'>" + safe(hoTen) + "</b>,</p>"
                + "<p style='margin: 0 0 20px 0; font-size: 14px; line-height: 1.5;'>" + loiNhan + "</p>"
                + "<div style='background-color: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 6px; padding: 15px; text-align: center; margin-bottom: 20px;'>"
                + "<span style='font-size: 32px; font-weight: 700; letter-spacing: 6px; color: #ff4757; margin-left: 6px;'>" + safe(maXacThuc) + "</span>"
                + "</div>"
                + "<p style='margin: 0 0 10px 0; font-size: 14px;'>Mã xác thực này sẽ hết hạn trong vòng <b style='color: #ff4757;'>" + soPhutHieuLuc + " phút</b>.</p>"
                + "<p style='margin: 0; font-size: 13px; color: #64748b; line-height: 1.5;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này hoặc liên hệ ngay với bộ phận hỗ trợ để bảo vệ tài khoản.</p>"
                + "</div>"
                + "<div style='background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 15px; text-align: center;'>"
                + "<p style='margin: 0; font-size: 12px; color: #94a3b8;'>&copy; 2026 GearZone - Chuyên linh kiện PC & Phụ kiện Gaming.</p>"
                + "<p style='margin: 5px 0 0 0; font-size: 11px; color: #cbd5e1;'>Email được gửi tự động, vui lòng không trả lời.</p>"
                + "</div>"
                + "</div>"
                + "</div>";

        sendAsync(toEmail, subject, body);
    }

    public static void shutdown() {
        try {
            if (sharedTransport != null && sharedTransport.isConnected()) {
                sharedTransport.close();
            }
        } catch (Exception ignored) {
        }
        MAIL_EXECUTOR.shutdownNow();
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace("<", "<").replace(">", ">");
    }

    /**
     * Mở sẵn kết nối tới Gmail ngay khi khởi động server để người dùng đầu tiên
     * không phải chờ
     */
    public static void warmUp() {
        MAIL_EXECUTOR.submit(() -> {
            synchronized (TRANSPORT_LOCK) {
                try {
                    if (isConfigured() && mailSession != null && (sharedTransport == null || !sharedTransport.isConnected())) {
                        long start = System.currentTimeMillis();
                        sharedTransport = mailSession.getTransport("smtp");
                        sharedTransport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), SENDER_EMAIL, SENDER_APP_PASSWORD);
                        System.out.println("[EMAIL] Đã mở sẵn kết nối SMTP (Mất: " + (System.currentTimeMillis() - start) + " ms)");
                    }
                } catch (Exception e) {
                    System.err.println("[EMAIL] Warm-up thất bại: " + e.getMessage());
                }
            }
        });
    }

}
