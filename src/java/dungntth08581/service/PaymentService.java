package dungntth08581.service;

import fourSome.url.DBConnect;
import fourSome.util.PayOSUtil;
import linhhqth08598.service.NotificationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

/**
 * Thanh toán online qua PayOS (UC 228) + ghi nhận kết quả thanh toán (UC 230).
 *
 * Luồng: đặt đơn (payments: PAYOS/PENDING) -> createLink() -> khách trả tiền trên PayOS ->
 *   (1) PayOS gọi webhook POST /payment-callback  -> handleWebhook() kiểm tra chữ ký -> markPaid()
 *   (2) trình duyệt khách quay về GET /payment-callback -> syncByOrderCode() hỏi lại PayOS -> markPaid()
 * markPaid() idempotent: gọi nhiều lần (webhook trùng, webhook + quay về) chỉ ghi 1 lần.
 * payments.transaction_code lưu orderCode của PayOS (số nguyên) để đối chiếu.
 */
public class PaymentService {

    public static final String METHOD_COD = "COD";
    /** Giá trị này phải khớp CHECK constraint payments.payment_method trong DB (nếu DB dùng tên khác thì sửa ở đây). */
    public static final String METHOD_PAYOS = "PAYOS";

    private static final int LINK_EXPIRE_SECONDS = 15 * 60;

    public static final int NOT_FOUND = 0, PAID_NOW = 1, ALREADY_PAID = 2, AMOUNT_MISMATCH = 3, ERROR = 4;

    private final NotificationService notifications = new NotificationService();

    public static class LinkResult { public String url, error; }

    public static class SyncResult { public Integer orderId; public boolean paid, error; public String message; }

    // =====================================================================
    // TẠO LINK (UC 228)
    // =====================================================================

    /** Tạo (hoặc tạo lại) link PayOS cho đơn của chính user. baseUrl = địa chỉ gốc web, vd http://localhost:8080/gearzone. */
    public LinkResult createLink(int userId, int orderId, String baseUrl) {
        LinkResult r = new LinkResult();
        if (!PayOSUtil.isConfigured()) { r.error = "Cổng thanh toán PayOS chưa được cấu hình!"; return r; }
        String sql = "SELECT TOP 1 p.payment_id, p.amount, p.payment_status, p.transaction_code, o.order_status FROM payments p JOIN orders o ON o.order_id = p.order_id "
                   + "WHERE p.order_id = ? AND o.user_id = ? AND p.payment_method = ? ORDER BY p.payment_id DESC";
        try (Connection con = DBConnect.getConnection()) {
            int paymentId; BigDecimal amount; String payStatus, oldCode, orderStatus;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, orderId); ps.setInt(2, userId); ps.setString(3, METHOD_PAYOS);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { r.error = "Đơn này không thanh toán online!"; return r; }
                    paymentId = rs.getInt(1); amount = rs.getBigDecimal(2); payStatus = rs.getString(3); oldCode = rs.getString(4); orderStatus = rs.getString(5);
                }
            }
            if ("PAID".equals(payStatus)) { r.error = "Đơn hàng đã được thanh toán!"; return r; }
            if (!"PENDING".equals(orderStatus) || !"PENDING".equals(payStatus)) { r.error = "Đơn hàng không còn ở trạng thái chờ thanh toán!"; return r; }

            // Link cũ: nếu khách đã trả rồi thì ghi nhận; nếu còn mở thì hủy để tránh bị trả 2 lần
            if (oldCode != null && oldCode.matches("\\d+")) {
                PayOSUtil.PaymentInfo info = PayOSUtil.getPaymentInfo(Long.parseLong(oldCode));
                if (info.ok) {
                    if ("PAID".equals(info.status)) { markPaid(oldCode, info.amountPaid, info.reference); r.error = "PayOS đã ghi nhận thanh toán cho đơn này!"; return r; }
                    if ("PENDING".equals(info.status) || "PROCESSING".equals(info.status)) PayOSUtil.cancelPaymentLink(Long.parseLong(oldCode), "Tao lai link");
                }
            }

            int money = amount.setScale(0, RoundingMode.HALF_UP).intValueExact();
            if (money <= 0) { r.error = "Đơn 0 đồng không cần thanh toán online!"; return r; }
            long orderCode = System.currentTimeMillis() * 1000L + (orderId % 1000);   // số nguyên dương, không trùng giữa các lần tạo link
            String desc = ("Thanh toan don " + orderId); if (desc.length() > 25) desc = desc.substring(0, 25);
            PayOSUtil.LinkResult lr = PayOSUtil.createPaymentLink(orderCode, money, desc, baseUrl + "/payment-callback", baseUrl + "/payment-callback", LINK_EXPIRE_SECONDS);
            if (!lr.ok) { r.error = "Không tạo được link thanh toán: " + lr.error; return r; }
            try (PreparedStatement ps = con.prepareStatement("UPDATE payments SET transaction_code = ? WHERE payment_id = ?")) {
                ps.setString(1, String.valueOf(orderCode)); ps.setInt(2, paymentId); ps.executeUpdate();
            }
            r.url = lr.checkoutUrl;
        } catch (Exception e) {
            e.printStackTrace();
            r.error = "Không tạo được link thanh toán, vui lòng thử lại!";
        }
        return r;
    }

    // =====================================================================
    // GHI NHẬN KẾT QUẢ (UC 230)
    // =====================================================================

    /** Đánh dấu đã thanh toán (idempotent). Chỉ tin số tiền PayOS báo, kiểm tra >= số tiền đơn. */
    public int markPaid(String payosOrderCode, long amountPaid, String reference) {
        String sel = "SELECT p.payment_id, p.order_id, p.amount, p.payment_status, o.user_id, o.order_code FROM payments p JOIN orders o ON o.order_id = p.order_id "
                   + "WHERE p.transaction_code = ? AND p.payment_method = ?";
        int orderId, userId; String orderCode;
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                int paymentId; BigDecimal amount; String status;
                try (PreparedStatement ps = con.prepareStatement(sel)) {
                    ps.setString(1, payosOrderCode); ps.setString(2, METHOD_PAYOS);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { con.rollback(); return NOT_FOUND; }
                        paymentId = rs.getInt(1); orderId = rs.getInt(2); amount = rs.getBigDecimal(3); status = rs.getString(4); userId = rs.getInt(5); orderCode = rs.getString(6);
                    }
                }
                if ("PAID".equals(status)) { con.rollback(); return ALREADY_PAID; }
                if (BigDecimal.valueOf(amountPaid).compareTo(amount.setScale(0, RoundingMode.HALF_UP)) < 0) {
                    con.rollback();
                    System.out.println("[PayOS] SỐ TIỀN KHÔNG KHỚP orderCode=" + payosOrderCode + " paid=" + amountPaid + " cần=" + amount);
                    return AMOUNT_MISMATCH;
                }
                int n;
                try (PreparedStatement ps = con.prepareStatement("UPDATE payments SET payment_status = 'PAID', paid_at = SYSDATETIME() WHERE payment_id = ? AND payment_status <> 'PAID'")) {
                    ps.setInt(1, paymentId); n = ps.executeUpdate();
                }
                if (n == 0) { con.rollback(); return ALREADY_PAID; }   // webhook khác vừa ghi xong
                try (PreparedStatement ps = con.prepareStatement("UPDATE orders SET order_status = 'CONFIRMED' WHERE order_id = ? AND order_status = 'PENDING'")) {
                    ps.setInt(1, orderId); ps.executeUpdate();
                }
                new OrderService().assignSerials(con, orderId);   // PayOS đã thanh toán = chốt serial luôn
                con.commit();
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        System.out.println("[PayOS] Đã ghi nhận thanh toán orderCode=" + payosOrderCode + " ref=" + reference);
        notifications.notifyOrder(userId, orderId, "Đơn hàng " + orderCode + " đã thanh toán thành công qua PayOS. Cảm ơn bạn!");
        return PAID_NOW;
    }

    /** Webhook từ PayOS. Trả về mã HTTP: 200 = đã xử lý/bỏ qua, 400 = sai chữ ký/body, 500 = lỗi hệ thống (PayOS sẽ gọi lại). */
    public int handleWebhook(String body) {
        if (!PayOSUtil.isConfigured()) return 500;
        PayOSUtil.Webhook w = PayOSUtil.parseWebhook(body);
        if (!w.valid) { System.out.println("[PayOS] Webhook bị từ chối: " + w.error); return 400; }
        if (!w.success) return 200;   // giao dịch không thành công: không có gì để ghi
        String oc = w.data.get("orderCode");
        long amount = 0;
        try { amount = Long.parseLong(w.data.get("amount")); } catch (Exception ignored) { }
        int r = markPaid(oc, amount, w.data.get("reference"));
        if (r == NOT_FOUND) System.out.println("[PayOS] Webhook orderCode=" + oc + " không có trong hệ thống (có thể là lần PayOS gửi thử).");
        return r == ERROR ? 500 : 200;
    }

    /** Khách quay về từ PayOS: hỏi lại PayOS trạng thái thật (không tin tham số trên URL) rồi ghi nhận nếu đã trả. */
    public SyncResult syncByOrderCode(long orderCode) {
        SyncResult s = new SyncResult();
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT order_id FROM payments WHERE transaction_code = ? AND payment_method = ?")) {
            ps.setString(1, String.valueOf(orderCode)); ps.setString(2, METHOD_PAYOS);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) s.orderId = rs.getInt(1); }
        } catch (Exception e) { e.printStackTrace(); }
        if (s.orderId == null) { s.error = true; s.message = "Không tìm thấy giao dịch thanh toán này!"; return s; }

        PayOSUtil.PaymentInfo info = PayOSUtil.getPaymentInfo(orderCode);
        if (!info.ok) {
            s.error = true; s.message = "Chưa xác minh được kết quả với PayOS. Nếu bạn đã chuyển tiền, hệ thống sẽ tự cập nhật khi PayOS thông báo.";
            return s;
        }
        String st = info.status == null ? "" : info.status;
        if ("PAID".equals(st)) {
            int r = markPaid(String.valueOf(orderCode), info.amountPaid, info.reference);
            if (r == PAID_NOW || r == ALREADY_PAID) { s.paid = true; s.message = "Thanh toán thành công! Đơn hàng của bạn đã được ghi nhận."; }
            else { s.error = true; s.message = "Đã nhận tiền nhưng chưa ghi nhận được đơn (mã lỗi " + r + "). Vui lòng liên hệ cửa hàng!"; }
        } else if ("CANCELLED".equals(st)) {
            s.error = true; s.message = "Bạn đã hủy thanh toán. Đơn hàng vẫn được giữ, bạn có thể bấm \"Thanh toán lại\" hoặc hủy đơn.";
        } else if ("EXPIRED".equals(st)) {
            s.error = true; s.message = "Link thanh toán đã hết hạn. Hãy bấm \"Thanh toán lại\" để tạo link mới.";
        } else {
            s.error = true; s.message = "PayOS chưa ghi nhận tiền. Nếu bạn vừa chuyển khoản, hãy đợi vài giây rồi tải lại trang này.";
        }
        return s;
    }

    // =====================================================================
    // HỖ TRỢ CHO ĐƠN HÀNG
    // =====================================================================

    /** Đơn đã thanh toán online chưa (khách không tự hủy được vì phải hoàn tiền). */
    public boolean isPaidOnline(int orderId) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM payments WHERE order_id = ? AND payment_method = ? AND payment_status = 'PAID'")) {
            ps.setInt(1, orderId); ps.setString(2, METHOD_PAYOS);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /** Khi hủy đơn: đóng link PayOS còn mở để khách không trả tiền cho đơn đã hủy (best effort, lỗi thì bỏ qua). */
    public void cancelOnlineLink(int orderId, String reason) {
        if (!PayOSUtil.isConfigured()) return;
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT transaction_code FROM payments WHERE order_id = ? AND payment_method = ? AND payment_status <> 'PAID' AND transaction_code IS NOT NULL")) {
            ps.setInt(1, orderId); ps.setString(2, METHOD_PAYOS);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString(1);
                    if (code != null && code.matches("\\d+")) PayOSUtil.cancelPaymentLink(Long.parseLong(code), reason == null ? "Huy don" : reason.replaceAll("[^A-Za-z0-9 ]", ""));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Thông tin thanh toán của đơn: {phương thức, trạng thái}; null nếu chưa có dòng payments. Ưu tiên dòng PAID. */
    public String[] paymentInfo(int orderId) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT TOP 1 payment_method, payment_status FROM payments WHERE order_id = ? "
              + "ORDER BY CASE payment_status WHEN 'PAID' THEN 0 WHEN 'PENDING' THEN 1 ELSE 2 END")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? new String[]{rs.getString(1), rs.getString(2)} : null; }
        } catch (Exception e) { e.printStackTrace(); return null; }
    }
}