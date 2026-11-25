package fourSome.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.SendFailedException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Tiện ích gửi email cho GearZone (tối ưu tái sử dụng kết nối SMTP, hàng đợi có giới hạn,
 * tự đóng kết nối nguội & thử lại 1 lần, kèm phần chữ thuần khi gửi HTML).
 */
public class EmailUtil {

    private static final long STALE_AFTER_MS = 45_000L;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final Object TRANSPORT_LOCK = new Object();

    private static final ThreadLocal<String> THREAD_LAST_ERROR = ThreadLocal.withInitial(() -> "");
    private static volatile String backgroundLastError = "";

    private static String senderEmail = "";
    private static String senderAppPassword = "";
    private static String senderName = "GearZone Support";
    private static String smtpHost = "smtp.gmail.com";
    private static int smtpPort = 587;

    private static volatile Session mailSession;
    private static Transport sharedTransport;
    private static long transportLastUsedMs;

    private static final ThreadPoolExecutor MAIL_EXECUTOR = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            r -> {
                Thread t = new Thread(r, "gearzone-mail");
                t.setDaemon(true);
                return t;
            });

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        loadConfigFromClasspath();
    }

    private static void loadConfigFromClasspath() {
        try (InputStream is = EmailUtil.class.getClassLoader().getResourceAsStream("email.properties")) {
            if (is != null) {
                Properties config = new Properties();
                config.load(new InputStreamReader(is, StandardCharsets.UTF_8));
                configure(
                        config.getProperty("mail.sender.email", ""),
                        config.getProperty("mail.sender.password", ""),
                        config.getProperty("mail.smtp.host", "smtp.gmail.com"),
                        parsePort(config.getProperty("mail.smtp.port", "587")),
                        config.getProperty("mail.sender.name", "GearZone Support")
                );
            } else {
                System.err.println("[EMAIL] Không tìm thấy file email.properties trong classpath!");
            }
        } catch (Exception e) {
            System.err.println("[EMAIL] Lỗi khi đọc file email.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int parsePort(String raw) {
        try {
            int p = Integer.parseInt(raw.trim());
            return p > 0 ? p : 587;
        } catch (Exception e) {
            return 587;
        }
    }

    public static void configure(String email, String appPassword, String host, Integer port, String displayName) {
        synchronized (TRANSPORT_LOCK) {
            senderEmail = email == null ? "" : email.trim();
            senderAppPassword = appPassword == null ? "" : appPassword.replace(" ", "");
            if (host != null && !host.isBlank()) {
                smtpHost = host.trim();
            }
            if (port != null && port > 0) {
                smtpPort = port;
            }
            if (displayName != null && !displayName.isBlank()) {
                senderName = displayName.trim();
            }
            mailSession = null;
            closeTransportQuietly();
        }
        THREAD_LAST_ERROR.set("");
    }

    public static boolean isConfigured() {
        return !senderEmail.isEmpty() && !senderAppPassword.isEmpty();
    }

    public static String getLastError() {
        String localErr = THREAD_LAST_ERROR.get();
        return (localErr != null && !localErr.isEmpty()) ? localErr : backgroundLastError;
    }

    public static void sendAsync(String toEmail, String subject, String htmlBody) {
        sendAsync(toEmail, subject, htmlBody, true, null);
    }

    public static boolean sendAsync(String toEmail, String subject, String content, boolean html, List<String> attachments) {
        if (!isConfigured()) {
            String msg = "Chưa cấu hình hộp thư SMTP trong email.properties.";
            THREAD_LAST_ERROR.set(msg);
            System.out.println("[EMAIL - CHƯA CẤU HÌNH SMTP] Tới: " + toEmail + " | " + subject);
            return false;
        }
        final List<String> files = attachments == null ? List.of() : List.copyOf(attachments);
        try {
            MAIL_EXECUTOR.execute(() -> {
                String err = doSend(toEmail, subject, content, html, files);
                backgroundLastError = (err == null) ? "" : err;
            });
            return true;
        } catch (RejectedExecutionException ex) {
            String err = "Hàng đợi gửi email đang đầy (500 thư chờ). Vui lòng thử lại sau.";
            THREAD_LAST_ERROR.set(err);
            System.err.println("[EMAIL] " + err);
            return false;
        }
    }

    public static boolean send(String toEmail, String subject, String htmlBody) {
        return send(toEmail, subject, htmlBody, true, null);
    }

    public static boolean send(String toEmail, String subject, String content, boolean html, List<String> attachments) {
        String err = doSend(toEmail, subject, content, html, attachments);
        THREAD_LAST_ERROR.set(err == null ? "" : err);
        return err == null;
    }

    private static String doSend(String toEmail, String subject, String content, boolean html, List<String> attachments) {
        if (!isConfigured()) {
            System.out.println("[EMAIL - CHƯA CẤU HÌNH SMTP] Tới: " + toEmail + " | " + subject);
            return null;
        }
        long start = System.currentTimeMillis();
        String startTime = LocalDateTime.now().format(TIME_FORMATTER);
        System.out.println("[EMAIL - " + startTime + "] Bắt đầu gửi tới " + toEmail + "...");

        synchronized (TRANSPORT_LOCK) {
            try {
                MimeMessage message = buildMessage(toEmail, subject, content, html, attachments);
                try {
                    sendOnce(message);
                } catch (SendFailedException badRecipient) {
                    throw badRecipient;
                } catch (MessagingException staleOrDropped) {
                    closeTransportQuietly();
                    MimeMessage retryMessage = buildMessage(toEmail, subject, content, html, attachments);
                    sendOnce(retryMessage);
                }
                long elapsed = System.currentTimeMillis() - start;
                String endTime = LocalDateTime.now().format(TIME_FORMATTER);
                System.out.println("[EMAIL - " + endTime + "] Đã gửi tới " + toEmail + " (Mất: " + elapsed + " ms)");
                return null;
            } catch (Exception e) {
                long elapsed = System.currentTimeMillis() - start;
                String err = e.getMessage() == null ? e.toString() : e.getMessage();
                System.err.println("[EMAIL - LỖI] Gửi thất bại tới " + toEmail + " sau " + elapsed + " ms: " + err);
                e.printStackTrace();
                return err;
            }
        }
    }

    private static void sendOnce(MimeMessage msg) throws MessagingException {
        ensureConnected();
        sharedTransport.sendMessage(msg, msg.getAllRecipients());
        transportLastUsedMs = System.currentTimeMillis();
    }

    private static void ensureConnected() throws MessagingException {
        long now = System.currentTimeMillis();
        if (sharedTransport != null && sharedTransport.isConnected() && (now - transportLastUsedMs) > STALE_AFTER_MS) {
            closeTransportQuietly();
        }
        if (sharedTransport == null || !sharedTransport.isConnected()) {
            Session s = getOrCreateSession();
            sharedTransport = s.getTransport("smtp");
            sharedTransport.connect(smtpHost, smtpPort, senderEmail, senderAppPassword);
            transportLastUsedMs = System.currentTimeMillis();
        }
    }

    private static Session getOrCreateSession() {
        Session current = mailSession;
        if (current != null) {
            return current;
        }
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        props.put("mail.smtp.localhost", "localhost");
        props.put("mail.from", senderEmail);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.writetimeout", "15000");

        if (smtpPort == 465) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
        }

        final String u = senderEmail;
        final String pw = senderAppPassword;
        Session created = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(u, pw);
            }
        });
        mailSession = created;
        return created;
    }

    private static MimeMessage buildMessage(String toEmail, String subject, String content, boolean html, List<String> attachments)
            throws Exception {
        MimeMessage message = new MimeMessage(getOrCreateSession());
        try {
            message.setFrom(new InternetAddress(senderEmail, senderName.isEmpty() ? senderEmail : senderName, "UTF-8"));
        } catch (Exception e) {
            message.setFrom(new InternetAddress(senderEmail));
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject, "UTF-8");

        Multipart bodyPartContainer = null;
        if (html) {
            MimeMultipart alt = new MimeMultipart("alternative");
            MimeBodyPart plainPart = new MimeBodyPart();
            plainPart.setText(htmlToText(content), "UTF-8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=UTF-8");

            alt.addBodyPart(plainPart);
            alt.addBodyPart(htmlPart);
            bodyPartContainer = alt;
        }

        boolean hasFiles = false;
        MimeMultipart mixed = new MimeMultipart("mixed");
        if (attachments != null) {
            for (String path : attachments) {
                if (path == null || path.isBlank()) {
                    continue;
                }
                File f = new File(path);
                if (!f.isFile()) {
                    continue;
                }
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.attachFile(f);
                mixed.addBodyPart(attachPart);
                hasFiles = true;
            }
        }

        if (!hasFiles) {
            if (html && bodyPartContainer != null) {
                message.setContent(bodyPartContainer);
            } else {
                message.setText(content == null ? "" : content, "UTF-8");
            }
        } else {
            MimeBodyPart mainBody = new MimeBodyPart();
            if (html && bodyPartContainer != null) {
                mainBody.setContent(bodyPartContainer);
            } else {
                mainBody.setText(content == null ? "" : content, "UTF-8");
            }
            mixed.addBodyPart(mainBody, 0);
            message.setContent(mixed);
        }

        message.saveChanges();
        return message;
    }

    static String htmlToText(String html) {
        if (html == null) {
            return "";
        }
        String text = html.replaceAll("(?is)<(script|style|head)[^>]*>.*?</\\1>", " ")
                .replaceAll("(?i)<br\\s*/?>|</(p|div|tr|h[1-6]|li)>", "\n")
                .replaceAll("(?i)</t[dh]>", "  ")
                .replaceAll("<[^>]+>", "")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&copy;", "(c)");
        return text.replaceAll("[ \\t]+", " ")
                .replaceAll("(?m)^ +| +$", "")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private static void closeTransportQuietly() {
        try {
            if (sharedTransport != null) {
                sharedTransport.close();
            }
        } catch (Exception ignored) {
        }
        sharedTransport = null;
    }

    public static void guiMaOTP(String toEmail, String hoTen, String maXacThuc, int soPhutHieuLuc, String tieuDe, String loiNhan) {
        String subject = "[GearZone] " + tieuDe;
        String body = "<div style='font-family: \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; background-color: #f1f5f9; padding: 30px 20px;'>"
                + "<div style='max-width: 500px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); overflow: hidden;'>"
                + "<div style='background-color: #ffffff; border-bottom: 2px solid #ff4757; padding: 15px; text-align: center;'>"
                + "<h2 style='margin: 0; font-size: 24px; font-weight: 800; color: #0f1117; letter-spacing: 1px;'>GEAR<span style='color: #ff4757;'>ZONE</span></h2>"
                + "</div>"
                + "<div style='padding: 25px 30px; color: #334155;'>"
                + "<p style='margin: 0 0 15px 0; font-size: 15px;'>Xin chào <b style='color: #0f1117;'>" + escapeHtml(hoTen) + "</b>,</p>"
                + "<p style='margin: 0 0 20px 0; font-size: 14px; line-height: 1.5;'>" + loiNhan + "</p>"
                + "<div style='background-color: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 6px; padding: 15px; text-align: center; margin-bottom: 20px;'>"
                + "<span style='font-size: 32px; font-weight: 700; letter-spacing: 6px; color: #ff4757; margin-left: 6px;'>" + escapeHtml(maXacThuc) + "</span>"
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

    public static void warmUp() {
        if (!isConfigured()) {
            return;
        }
        try {
            MAIL_EXECUTOR.submit(() -> {
                synchronized (TRANSPORT_LOCK) {
                    try {
                        long start = System.currentTimeMillis();
                        ensureConnected();
                        System.out.println("[EMAIL] Đã mở sẵn kết nối SMTP (Mất: " + (System.currentTimeMillis() - start) + " ms)");
                    } catch (Exception e) {
                        System.err.println("[EMAIL] Warm-up thất bại: " + e.getMessage());
                    }
                }
            });
        } catch (RejectedExecutionException ignored) {
        }
    }

    public static void shutdown() {
        MAIL_EXECUTOR.shutdown();
        try {
            if (!MAIL_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                MAIL_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException ignored) {
            MAIL_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
        synchronized (TRANSPORT_LOCK) {
            closeTransportQuietly();
        }
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
