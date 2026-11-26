package dungntth08581.service;

import fourSome.model.ShippingAddresses;
import fourSome.url.DBConnect;
import khanhnb08894.dao.PartSerialsDAO;
import khanhnb08894.service.InventoryService;
import linhhqth08598.service.AuditLogService;
import linhhqth08598.service.NotificationService;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Đặt hàng / hủy đơn / cập nhật trạng thái (bản gọn làm hộ Dũng - UC 222-235).
 * Tạo đơn + giữ kho + xóa giỏ nằm trong MỘT giao dịch. Giá luôn lấy lại từ DB.
 * Chưa có: mã giảm giá, khuyến mãi, thanh toán online (VNPAY/ZaloPay) - Dũng
 * làm tiếp.
 */
public class OrderService {

    private final InventoryService inventory = new InventoryService();
    private final PartSerialsDAO serialsDAO = new PartSerialsDAO();
    private final NotificationService notifications = new NotificationService();
    private final PromotionService promotions = new PromotionService();
    private final PaymentService paymentService = new PaymentService();

    // ---------- DTO ----------
    public static class ShippingMethod {

        private int id;
        private String name;
        private double fee;
        private Integer days;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getFee() {
            return fee;
        }

        public Integer getDays() {
            return days;
        }
    }

    public static class OrderRow {

        private int orderId, userId;
        private String orderCode, status, recipient, phone, address, note, cancelReason, trackingCode, customerName;
        private double total, fee, finalAmount, discount;
        private Timestamp createdAt;
        private String couponCode;
        private String paymentMethod, paymentStatus;

        public int getOrderId() {
            return orderId;
        }

        public int getUserId() {
            return userId;
        }

        public String getOrderCode() {
            return orderCode;
        }

        public String getStatus() {
            return status;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public String getNote() {
            return note;
        }

        public String getCancelReason() {
            return cancelReason;
        }

        public String getTrackingCode() {
            return trackingCode;
        }

        public String getCustomerName() {
            return customerName;
        }

        public double getTotal() {
            return total;
        }

        public double getFee() {
            return fee;
        }

        public double getFinalAmount() {
            return finalAmount;
        }

        public double getDiscount() {
            return discount;
        }

        public String getCouponCode() {
            return couponCode;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public Timestamp getCreatedAt() {
            return createdAt;
        }
    }

    public static class OrderLine {

        private int orderItemId, partId;
        private String partName;
        private int quantity;
        private double unitPrice;
        private String serialNumber;

        public int getOrderItemId() {
            return orderItemId;
        }

        public int getPartId() {
            return partId;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public String getPartName() {
            return partName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getLineTotal() {
            return unitPrice * quantity;
        }
    }

    public static class PlaceResult {

        public int orderId;
        public String orderCode;
        public String error;
    }

    // ---------- Đọc ----------
    public List<ShippingMethod> getShippingMethods() {
        List<ShippingMethod> list = new ArrayList<>();
        String sql = "SELECT shipping_method_id, method_name, base_fee, estimated_days FROM shipping_methods WHERE status = 'ACTIVE' ORDER BY base_fee";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ShippingMethod m = new ShippingMethod();
                m.id = rs.getInt(1);
                m.name = rs.getString(2);
                m.fee = rs.getDouble(3);
                int d = rs.getInt(4);
                m.days = rs.wasNull() ? null : d;
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private OrderRow map(ResultSet rs) throws SQLException {
        OrderRow o = new OrderRow();
        o.orderId = rs.getInt("order_id");
        o.userId = rs.getInt("user_id");
        o.orderCode = rs.getString("order_code");
        o.status = rs.getString("order_status");
        o.recipient = rs.getString("recipient_name");
        o.phone = rs.getString("recipient_phone");
        o.address = fourSome.util.FormatUtil.cleanAddress(rs.getString("shipping_address"));
        /* đơn cũ có thể đã lưu ",," */ o.note = rs.getString("note");
        o.cancelReason = rs.getString("cancel_reason");
        o.trackingCode = rs.getString("tracking_code");
        o.total = rs.getDouble("total_amount");
        o.fee = rs.getDouble("shipping_fee");
        o.finalAmount = rs.getDouble("final_amount");
        o.createdAt = rs.getTimestamp("created_at");
        o.discount = rs.getDouble("discount_amount");
        return o;
    }

    /**
     * Đơn của user (null nếu không phải đơn của họ).
     */
    public OrderRow getUserOrder(int userId, int orderId) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT o.*, c.code AS coupon_code FROM orders o LEFT JOIN coupons c ON c.coupon_id = o.coupon_id WHERE o.order_id = ? AND o.user_id = ?")) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderRow o = map(rs);
                    o.couponCode = rs.getString("coupon_code");
                    loadPayment(con, o);
                    return o;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public OrderRow getAnyOrder(int orderId) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM orders WHERE order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OrderRow o = map(rs);
                    loadPayment(con, o);
                    return o;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Nạp phương thức + trạng thái thanh toán mới nhất của đơn.
     */
    private void loadPayment(Connection con, OrderRow o) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT TOP 1 payment_method, payment_status FROM payments WHERE order_id = ? ORDER BY payment_id DESC")) {
            ps.setInt(1, o.orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    o.paymentMethod = rs.getString(1);
                    o.paymentStatus = rs.getString(2);
                }
            }
        }
    }

    public List<OrderLine> getLines(int orderId) {
        List<OrderLine> list = new ArrayList<>();
        String sql = "SELECT p.part_name, oi.quantity, oi.unit_price, oi.serial_number, oi.order_item_id, oi.part_id FROM order_items oi JOIN parts p ON p.part_id = oi.part_id WHERE oi.order_id = ? ORDER BY oi.order_item_id";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderLine l = new OrderLine();
                    l.partName = rs.getString(1);
                    l.quantity = rs.getInt(2);
                    l.unitPrice = rs.getDouble(3);
                    l.serialNumber = rs.getString(4);
                    l.orderItemId = rs.getInt(5);
                    l.partId = rs.getInt(6);
                    list.add(l);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Admin: danh sách đơn, lọc theo trạng thái (null/rỗng = tất cả).
     */
    public List<OrderRow> listForAdmin(String status) {
        List<OrderRow> list = new ArrayList<>();
        boolean f = status != null && !status.isEmpty();
        String sql = "SELECT o.*, u.full_name FROM orders o LEFT JOIN users u ON u.user_id = o.user_id "
                + (f ? "WHERE o.order_status = ? " : "") + "ORDER BY o.created_at DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (f) {
                ps.setString(1, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderRow o = map(rs);
                    o.customerName = rs.getString("full_name");
                    list.add(o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ---------- Đặt hàng (UC 222-227, 229) ----------
    public PlaceResult placeOrder(int userId, ShippingAddresses addr, int shippingMethodId, String note) {
        return placeOrder(userId, addr, shippingMethodId, note, null);
    }

    /**
     * UC 224: couponCode có thể null/rỗng. Giảm giá tính lại ở server; mã
     * sai/hết hạn -> không đặt đơn, báo lỗi để khách sửa.
     */
    public PlaceResult placeOrder(int userId, ShippingAddresses addr, int shippingMethodId, String note, String couponCode) {
        return placeOrder(userId, addr, shippingMethodId, note, couponCode, PaymentService.METHOD_COD);
    }

    /**
     * paymentMethod: COD hoặc PAYOS (UC 228/229). PAYOS: đơn + payments PENDING
     * được tạo, controller gọi PaymentService.createLink để sang trang thanh
     * toán.
     */
    public PlaceResult placeOrder(int userId, ShippingAddresses addr, int shippingMethodId, String note, String couponCode, String paymentMethod) {
        return placeOrder(userId, addr, shippingMethodId, note, couponCode, paymentMethod, null, null);
    }

    public PlaceResult placeOrder(int userId, ShippingAddresses addr, int shippingMethodId, String note, String couponCode, String paymentMethod, Integer buyNowPartId, Integer buyNowQty) {
        PlaceResult res = new PlaceResult();
        final boolean isBuyNow = buyNowPartId != null && buyNowPartId > 0 && buyNowQty != null && buyNowQty > 0;
        final String method = PaymentService.METHOD_PAYOS.equalsIgnoreCase(paymentMethod) ? PaymentService.METHOD_PAYOS : PaymentService.METHOD_COD;
        if (PaymentService.METHOD_PAYOS.equals(method) && !fourSome.util.PayOSUtil.isConfigured()) {
            res.error = "Thanh toán online chưa được cấu hình, vui lòng chọn thanh toán khi nhận hàng!";
            return res;
        }
        if (addr == null) {
            res.error = "Vui lòng chọn địa chỉ nhận hàng!";
            return res;
        }
        if (note != null && note.length() > 500) {
            note = note.substring(0, 500);
        }

        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                // 1. Giỏ hàng hoặc món Mua ngay riêng + giá từ DB
                List<int[]> items = new ArrayList<>();           // {partId, qty}
                List<BigDecimal[]> prices = new ArrayList<>();   // {unit_price, cost_price}
                List<String> names = new ArrayList<>();
                BigDecimal total = BigDecimal.ZERO;
                if (isBuyNow) {
                    String selBuyNow = "SELECT p.part_id, ?, ISNULL(p.sale_price, p.base_price), p.avg_cost_price, p.part_name "
                            + "FROM parts p WHERE p.part_id = ? AND p.status = 'ACTIVE'";
                    try (PreparedStatement ps = con.prepareStatement(selBuyNow)) {
                        ps.setInt(1, buyNowQty);
                        ps.setInt(2, buyNowPartId);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                items.add(new int[]{rs.getInt(1), rs.getInt(2)});
                                BigDecimal unit = rs.getBigDecimal(3);
                                prices.add(new BigDecimal[]{unit, rs.getBigDecimal(4)});
                                names.add(rs.getString(5));
                                total = total.add(unit.multiply(BigDecimal.valueOf(rs.getInt(2))));
                            }
                        }
                    }
                } else {
                    String sel = "SELECT c.part_id, c.quantity, ISNULL(p.sale_price, p.base_price), p.avg_cost_price, p.part_name "
                            + "FROM cart_items c JOIN parts p ON p.part_id = c.part_id "
                            + "WHERE c.user_id = ? AND c.status = 'ACTIVE' AND p.status = 'ACTIVE' AND c.quantity > 0";
                    try (PreparedStatement ps = con.prepareStatement(sel)) {
                        ps.setInt(1, userId);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                items.add(new int[]{rs.getInt(1), rs.getInt(2)});
                                BigDecimal unit = rs.getBigDecimal(3);
                                prices.add(new BigDecimal[]{unit, rs.getBigDecimal(4)});
                                names.add(rs.getString(5));
                                total = total.add(unit.multiply(BigDecimal.valueOf(rs.getInt(2))));
                            }
                        }
                    }
                }
                if (items.isEmpty()) {
                    con.rollback();
                    res.error = isBuyNow ? "Sản phẩm không còn được bán!" : "Giỏ hàng trống hoặc sản phẩm đã ngừng bán!";
                    return res;
                }

                // 2. Phí vận chuyển (tùy chọn)
                BigDecimal fee = BigDecimal.ZERO;
                Integer methodId = null;
                if (shippingMethodId > 0) {
                    try (PreparedStatement ps = con.prepareStatement("SELECT base_fee FROM shipping_methods WHERE shipping_method_id = ? AND status = 'ACTIVE'")) {
                        ps.setInt(1, shippingMethodId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                fee = rs.getBigDecimal(1);
                                methodId = shippingMethodId;
                            }
                        }
                    }
                }
                // 2b. Khuyến mãi tự động + mã giảm giá (UC 224, 241-247), tính trong cùng transaction
                List<PromotionService.Item> promoItems = isBuyNow
                        ? new CartService().toPromotionItems(new CartService().getBuyNowLines(buyNowPartId, buyNowQty))
                        : promotions.loadCartItems(con, userId);
                PromotionService.Result disc = promotions.calculate(con, promoItems, fee, userId, couponCode);
                if (disc.error != null) {
                    con.rollback();
                    res.error = disc.error;
                    return res;
                }
                if (disc.couponId != null && !promotions.reserveCouponUse(con, disc.couponId)) {
                    con.rollback();
                    res.error = "Mã giảm giá đã hết lượt sử dụng!";
                    return res;
                }
                BigDecimal goodsDiscount = disc.getGoodsDiscount().min(total);
                BigDecimal shipFee = fee.subtract(disc.getShippingDiscount()).max(BigDecimal.ZERO);
                BigDecimal finalAmount = total.subtract(goodsDiscount).add(shipFee);

                // 3. Ghi đơn
                String code = "GZ" + new java.text.SimpleDateFormat("yyMMddHHmmss").format(new java.util.Date()) + (100 + new Random().nextInt(900));
                String fullAddress = fourSome.util.FormatUtil.joinAddress(addr.getDetailedAddress(), addr.getWard(), addr.getProvince());   // bỏ dấu phẩy thừa/phần trống
                if (fullAddress.length() > 255) {
                    fullAddress = fullAddress.substring(0, 255);
                }
                int orderId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO orders (order_code, user_id, recipient_name, recipient_phone, shipping_address, shipping_method_id, shipping_fee, total_amount, discount_amount, final_amount, order_status, note, coupon_id, promotion_id, combo_id, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?, ?, ?, ?, SYSDATETIME())", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, code);
                    ps.setInt(2, userId);
                    ps.setString(3, addr.getRecipientName());
                    ps.setString(4, addr.getPhone());
                    ps.setString(5, fullAddress);
                    if (methodId == null) {
                        ps.setNull(6, Types.INTEGER);
                    } else {
                        ps.setInt(6, methodId);
                    }
                    ps.setBigDecimal(7, shipFee);
                    ps.setBigDecimal(8, total);
                    ps.setBigDecimal(9, goodsDiscount);
                    ps.setBigDecimal(10, finalAmount);
                    ps.setString(11, note);
                    if (disc.couponId == null) {
                        ps.setNull(12, Types.INTEGER);
                    } else {
                        ps.setInt(12, disc.couponId);
                    }
                    if (disc.promotionId == null || disc.promoDiscount.signum() == 0) {
                        ps.setNull(13, Types.INTEGER);
                    } else {
                        ps.setInt(13, disc.promotionId);
                    }
                    if (disc.comboId == null || disc.comboDiscount.signum() == 0) {
                        ps.setNull(14, Types.INTEGER);
                    } else {
                        ps.setInt(14, disc.comboId);
                    }
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        orderId = rs.getInt(1);
                    }
                }

                // 4. Chi tiết đơn + giữ kho (thiếu hàng -> hủy cả giao dịch)
                for (int i = 0; i < items.size(); i++) {
                    int partId = items.get(i)[0], qty = items.get(i)[1];
                    if (!inventory.reserve(con, partId, qty, orderId)) {
                        con.rollback();
                        res.error = "Sản phẩm \"" + names.get(i) + "\" không đủ hàng (còn " + inventory.getAvailable(con, partId) + ", bạn cần " + qty + "). Vui lòng điều chỉnh giỏ hàng!";
                        return res;
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO order_items (order_id, part_id, quantity, unit_price, cost_price, status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')")) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, partId);
                        ps.setInt(3, qty);
                        ps.setBigDecimal(4, prices.get(i)[0]);
                        if (prices.get(i)[1] == null) {
                            ps.setNull(5, Types.DECIMAL);
                        } else {
                            ps.setBigDecimal(5, prices.get(i)[1]);
                        }
                        ps.executeUpdate();
                    }
                }

                // 5. Ghi thanh toán (COD hoặc PAYOS, đều PENDING) + xóa giỏ (nếu đặt từ giỏ; chế độ Mua ngay giữ nguyên giỏ hàng)
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO payments (order_id, payment_method, amount, payment_status, created_at) VALUES (?, ?, ?, 'PENDING', SYSDATETIME())")) {
                    ps.setInt(1, orderId);
                    ps.setString(2, method);
                    ps.setBigDecimal(3, finalAmount);
                    ps.executeUpdate();
                }
                if (!isBuyNow) {
                    try (PreparedStatement ps = con.prepareStatement("DELETE FROM cart_items WHERE user_id = ?")) {
                        ps.setInt(1, userId);
                        ps.executeUpdate();
                    }
                }
                con.commit();

                res.orderId = orderId;
                res.orderCode = code;
                notifications.notifyOrder(userId, orderId, "Đơn hàng " + code + " đã được tiếp nhận. Chúng tôi sẽ sớm xác nhận và giao hàng cho bạn.");
                sendOrderEmailAsync(userId, code, "PENDING",
                        "Đơn hàng " + code + " đã được tiếp nhận. Chúng tôi sẽ sớm xác nhận và giao hàng cho bạn.");
                return res;
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.error = "Không đặt được hàng, vui lòng thử lại!";
            return res;
        }
    }

    private void sendOrderEmailAsync(int userId, String orderCode, String statusKey, String bodyText) {
        String sql = "SELECT email, full_name FROM users WHERE user_id = ? AND status = 'ACTIVE'";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;
                String toEmail = rs.getString("email");
                String fullName = rs.getString("full_name");
                if (toEmail == null || toEmail.trim().isEmpty()) return;
                String safeName = (fullName == null || fullName.trim().isEmpty()) ? "Khách hàng" : fullName.trim();

                String badgeBg;
                String badgeText;
                String accentColor;
                String subjectPrefix;
                switch (statusKey == null ? "" : statusKey.toUpperCase()) {
                    case "CONFIRMED":
                        badgeBg = "#dbeafe";
                        badgeText = "#1d4ed8";
                        accentColor = "#2563eb";
                        subjectPrefix = "Đã xác nhận đơn hàng";
                        break;
                    case "SHIPPING":
                        badgeBg = "#fef3c7";
                        badgeText = "#b45309";
                        accentColor = "#d97706";
                        subjectPrefix = "Đơn hàng đang giao";
                        break;
                    case "DELIVERED":
                        badgeBg = "#dcfce7";
                        badgeText = "#15803d";
                        accentColor = "#16a34a";
                        subjectPrefix = "Giao hàng thành công";
                        break;
                    case "CANCELLED":
                        badgeBg = "#fee2e2";
                        badgeText = "#b91c1c";
                        accentColor = "#dc2626";
                        subjectPrefix = "Đơn hàng đã hủy";
                        break;
                    default:
                        badgeBg = "#f1f5f9";
                        badgeText = "#334155";
                        accentColor = "#ff4757";
                        subjectPrefix = "Đặt hàng thành công";
                        break;
                }

                String subject = "[GearZone] " + subjectPrefix + " #" + orderCode;
                String html = "<div style='font-family: \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; background-color: #f1f5f9; padding: 30px 20px;'>"
                        + "<div style='max-width: 520px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); overflow: hidden;'>"
                        + "<div style='background-color: #ffffff; border-bottom: 2px solid #ff4757; padding: 15px; text-align: center;'>"
                        + "<h2 style='margin: 0; font-size: 24px; font-weight: 800; color: #0f1117; letter-spacing: 1px;'>GEAR<span style='color: #ff4757;'>ZONE</span></h2>"
                        + "</div>"
                        + "<div style='padding: 25px 30px; color: #334155;'>"
                        + "<div style='margin-bottom: 14px;'>"
                        + "<span style='display: inline-block; background-color: " + badgeBg + "; color: " + badgeText + "; font-weight: 700; font-size: 12px; padding: 5px 12px; border-radius: 999px; border-left: 3px solid " + accentColor + ";'>"
                        + escapeHtml(subjectPrefix) + "</span>"
                        + "</div>"
                        + "<p style='margin: 0 0 14px 0; font-size: 15px;'>Xin chào <b style='color: #0f1117;'>" + escapeHtml(safeName) + "</b>,</p>"
                        + "<p style='margin: 0 0 18px 0; font-size: 14px; line-height: 1.6;'>" + escapeHtml(bodyText) + "</p>"
                        + "<div style='background-color: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 8px; padding: 12px 16px; margin-bottom: 18px;'>"
                        + "<span style='font-size: 13px; color: #64748b;'>Mã đơn hàng: </span>"
                        + "<b style='font-size: 15px; color: " + accentColor + ";'>" + escapeHtml(orderCode) + "</b>"
                        + "</div>"
                        + "<p style='margin: 0; font-size: 13px; color: #64748b; line-height: 1.5;'>Cảm ơn bạn đã tin tưởng mua sắm linh kiện &amp; phụ kiện tại GearZone.</p>"
                        + "</div>"
                        + "<div style='background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 15px; text-align: center;'>"
                        + "<p style='margin: 0; font-size: 12px; color: #94a3b8;'>&copy; 2026 GearZone - Chuyên linh kiện PC &amp; Phụ kiện Gaming.</p>"
                        + "<p style='margin: 5px 0 0 0; font-size: 11px; color: #cbd5e1;'>Email được gửi tự động, vui lòng không trả lời.</p>"
                        + "</div>"
                        + "</div>"
                        + "</div>";
                fourSome.util.EmailUtil.sendAsync(toEmail.trim(), subject, html, true, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    // ---------- Hủy đơn (UC 231) ----------
    /**
     * Khách hủy đơn của mình (chỉ khi PENDING/CONFIRMED). Trả về null nếu OK.
     */
    public String cancelByUser(int userId, int orderId, String reason) {
        OrderRow o = getUserOrder(userId, orderId);
        if (o == null) {
            return "Không tìm thấy đơn hàng!";
        }
        if (paymentService.isPaidOnline(orderId)) {
            return "Đơn đã thanh toán online. Vui lòng liên hệ cửa hàng để được hỗ trợ hoàn tiền!";
        }
        return cancel(o, reason == null || reason.trim().isEmpty() ? "Khách hàng hủy đơn" : reason.trim());
    }

    /**
     * Khách xác nhận đã nhận hàng: chỉ chủ đơn, chỉ khi đơn đang SHIPPING ->
     * DELIVERED. Trả về null nếu OK.
     */
    public String confirmReceived(int userId, int orderId) {
        OrderRow o = getUserOrder(userId, orderId);
        if (o == null) {
            return "Không tìm thấy đơn hàng!";
        }
        if (!"SHIPPING".equals(o.status)) {
            return "Chỉ xác nhận được khi đơn đang giao!";
        }
        return adminUpdateStatus(orderId, "DELIVERED", null);
    }

    private String cancel(OrderRow o, String reason) {
        if (!"PENDING".equals(o.status) && !"CONFIRMED".equals(o.status)) {
            return "Đơn ở trạng thái hiện tại không thể hủy!";
        }
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                releaseItems(con, o.orderId);
                releaseSerials(con, o.orderId);
                promotions.releaseCouponUse(con, o.orderId);
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE orders SET order_status = 'CANCELLED', cancel_reason = ?, cancelled_at = SYSDATETIME() WHERE order_id = ?")) {
                    ps.setString(1, reason.length() > 500 ? reason.substring(0, 500) : reason);
                    ps.setInt(2, o.orderId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("UPDATE payments SET payment_status = 'FAILED' WHERE order_id = ? AND payment_status = 'PENDING'")) {
                    ps.setInt(1, o.orderId);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Không hủy được đơn, vui lòng thử lại!";
        }
        paymentService.cancelOnlineLink(o.orderId, reason);   // đóng link PayOS còn mở (nếu có)
        notifications.notifyOrder(o.userId, o.orderId, "Đơn hàng " + o.orderCode + " đã được hủy.");
        return null;
    }

    /** CONFIRMED: bốc serial IN_STOCK lâu nhất cho từng dòng, đổi SOLD, ghi vào order_items.serial_number. */
    public void assignSerials(Connection con, int orderId) throws SQLException {
        List<int[]> items = new ArrayList<>();   // {order_item_id, part_id, quantity}
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT order_item_id, part_id, quantity FROM order_items WHERE order_id = ? AND (serial_number IS NULL OR serial_number = '')")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(new int[]{rs.getInt(1), rs.getInt(2), rs.getInt(3)});
            }
        }
        for (int[] it : items) {
            List<String> picked = serialsDAO.pickForSale(con, it[1], it[2]);
            if (picked.isEmpty()) continue;   // dữ liệu cũ / chưa nhập serial: để trống, bảo hành sẽ dùng mã dự phòng
            try (PreparedStatement ps = con.prepareStatement("UPDATE order_items SET serial_number = ? WHERE order_item_id = ?")) {
                ps.setString(1, String.join(", ", picked));
                ps.setInt(2, it[0]);
                ps.executeUpdate();
            }
        }
    }

    /** Trạng thái đơn còn cho phép admin sửa serial (trước khi giao xong, vì giao xong là phát hành bảo hành theo serial). */
    public static boolean canEditSerials(String orderStatus) {
        return "CONFIRMED".equals(orderStatus) || "ASSEMBLING".equals(orderStatus) || "SHIPPING".equals(orderStatus);
    }

    /**
     * Admin sửa tay serial của 1 dòng đơn (nhân viên kho nhặt nhầm chiếc khác trên kệ).
     * Quy tắc: đơn đang CONFIRMED/ASSEMBLING/SHIPPING; nhập đúng số lượng của dòng, không trùng nhau;
     * serial mới phải là hàng IN_STOCK đúng linh kiện (hoặc đang thuộc chính dòng này);
     * serial bị bỏ ra quay về IN_STOCK. Tất cả trong 1 giao dịch. Trả về null nếu OK, ngược lại là lời báo lỗi.
     */
    public String editSerials(int orderId, int orderItemId, String rawSerials, int staffId) {
        List<String> news = PartSerialsDAO.parseInput(rawSerials);
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                String status = null;
                try (PreparedStatement ps = con.prepareStatement("SELECT order_status FROM orders WHERE order_id = ?")) {
                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) status = rs.getString(1); }
                }
                if (status == null) { con.rollback(); return "Không tìm thấy đơn hàng!"; }
                if (!canEditSerials(status)) { con.rollback(); return "Chỉ sửa được serial khi đơn đã xác nhận và chưa giao xong!"; }

                int partId = 0, qty = 0; String oldCsv = null; boolean found = false;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT part_id, quantity, serial_number FROM order_items WITH (UPDLOCK) WHERE order_item_id = ? AND order_id = ?")) {
                    ps.setInt(1, orderItemId); ps.setInt(2, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) { found = true; partId = rs.getInt(1); qty = rs.getInt(2); oldCsv = rs.getString(3); }
                    }
                }
                if (!found) { con.rollback(); return "Không tìm thấy dòng sản phẩm trong đơn!"; }
                if (news.size() != qty) { con.rollback(); return "Dòng này mua " + qty + " chiếc, cần nhập đúng " + qty + " mã serial (đang nhập " + news.size() + ")!"; }

                java.util.Set<String> seen = new java.util.HashSet<>();
                for (String sn : news) {
                    if (sn.length() > 100) { con.rollback(); return "Serial \"" + sn + "\" dài quá 100 ký tự!"; }
                    if (!seen.add(sn.toUpperCase())) { con.rollback(); return "Serial \"" + sn + "\" bị nhập trùng trong danh sách!"; }
                }

                java.util.Map<String, String> oldMap = new java.util.HashMap<>();   // HOA -> mã gốc đang lưu trong DB
                for (String sn : PartSerialsDAO.splitSerials(oldCsv)) oldMap.put(sn.toUpperCase(), sn);

                // Serial đã được kỹ thuật viên ghi trong đơn lắp ráp thì không được gỡ ra nữa
                for (String sn : PartSerialsDAO.splitSerials(oldCsv)) {
                    if (seen.contains(sn.toUpperCase())) continue;
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT 1 FROM assembly_parts_used u JOIN assembly_orders a ON a.assembly_id = u.assembly_id "
                          + "WHERE a.order_id = ? AND (',' + REPLACE(u.serial_number, ' ', '') + ',') LIKE ?")) {
                        ps.setInt(1, orderId); ps.setString(2, "%," + sn + ",%");
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) { con.rollback(); return "Serial \"" + sn + "\" đã được ghi vào đơn lắp ráp, hãy xóa dòng lắp ráp đó trước khi đổi!"; }
                        }
                    }
                }

                // 1) Nhả serial bị bỏ ra
                for (String sn : PartSerialsDAO.splitSerials(oldCsv)) {
                    if (!seen.contains(sn.toUpperCase())) serialsDAO.moveStatus(con, sn, partId, "SOLD", "IN_STOCK");
                }
                // 2) Bốc serial mới (phải đang IN_STOCK, đúng linh kiện)
                for (String sn : news) {
                    if (oldMap.containsKey(sn.toUpperCase())) continue;
                    if (!serialsDAO.moveStatus(con, sn, partId, "IN_STOCK", "SOLD")) {
                        con.rollback();
                        String[] info = serialsDAO.findInfo(sn);
                        if (info == null) return "Serial \"" + sn + "\" không có trong kho!";
                        if (!"IN_STOCK".equals(info[1])) return "Serial \"" + sn + "\" không còn trong kho (trạng thái " + info[1] + ")!";
                        return "Serial \"" + sn + "\" thuộc linh kiện khác (" + info[0] + "), không đúng dòng này!";
                    }
                }
                // 3) Ghi vào dòng đơn
                List<String> finalList = new ArrayList<>();
                for (String sn : news) finalList.add(oldMap.getOrDefault(sn.toUpperCase(), sn));   // giữ đúng chữ hoa/thường của mã cũ
                String newCsv = String.join(", ", finalList);
                try (PreparedStatement ps = con.prepareStatement("UPDATE order_items SET serial_number = ? WHERE order_item_id = ?")) {
                    ps.setString(1, newCsv); ps.setInt(2, orderItemId);
                    ps.executeUpdate();
                }
                con.commit();
                new AuditLogService().logAction(staffId, "EDIT_ORDER_SERIAL", "order_items", orderItemId,
                        "Đơn #" + orderId + ": đổi serial từ [" + (oldCsv == null ? "" : oldCsv) + "] sang [" + newCsv + "]");
                return null;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Không sửa được serial, vui lòng thử lại!";
        }
    }

    /** CANCELLED: nhả serial về IN_STOCK và xóa serial_number trên order_items. */
    private void releaseSerials(Connection con, int orderId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT serial_number FROM order_items WHERE order_id = ? AND serial_number IS NOT NULL")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) serialsDAO.releaseSerials(con, rs.getString(1));
            }
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE order_items SET serial_number = NULL WHERE order_id = ?")) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    private void releaseItems(Connection con, int orderId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT part_id, quantity FROM order_items WHERE order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inventory.release(con, rs.getInt(1), rs.getInt(2), orderId);
                }
            }
        }
    }

    // ---------- Admin cập nhật trạng thái ----------
    /**
     * PENDING -> CONFIRMED -> SHIPPING -> DELIVERED; hủy được khi
     * PENDING/CONFIRMED. DELIVERED: xuất kho, đánh dấu COD đã thanh toán. Trả
     * về null nếu OK.
     */
    public String adminUpdateStatus(int orderId, String newStatus, String trackingOrReason) {
        OrderRow o = getAnyOrder(orderId);
        if (o == null) {
            return "Không tìm thấy đơn hàng!";
        }
        if ("CANCELLED".equals(newStatus)) {
            return cancel(o, trackingOrReason == null || trackingOrReason.trim().isEmpty() ? "Cửa hàng hủy đơn" : trackingOrReason.trim());
        }
        if ("PENDING".equals(o.status) && "CONFIRMED".equals(newStatus)) {
            String[] pay = paymentService.paymentInfo(orderId);
            if (pay != null && "PAYOS".equals(pay[0]) && !"PAID".equals(pay[1])) {
                return "Đơn thanh toán PayOS chưa được thanh toán, chưa thể xác nhận!";
            }
        }
        String expected;
        switch (o.status) {
            case "PENDING":
                expected = "CONFIRMED";
                break;
            case "CONFIRMED":
                expected = "SHIPPING";
                break;
            case "SHIPPING":
                expected = "DELIVERED";
                break;
            default:
                return "Đơn ở trạng thái này không thể chuyển tiếp!";
        }
        if (!expected.equals(newStatus)) {
            return "Chỉ được chuyển sang bước kế tiếp: " + expected + "!";
        }

        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                if ("DELIVERED".equals(newStatus)) {
                    try (PreparedStatement ps = con.prepareStatement("SELECT part_id, quantity FROM order_items WHERE order_id = ?")) {
                        ps.setInt(1, orderId);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                inventory.exportStock(con, rs.getInt(1), rs.getInt(2), orderId);
                            }
                        }
                    }
                    try (PreparedStatement ps = con.prepareStatement("UPDATE payments SET payment_status = 'PAID', paid_at = SYSDATETIME() WHERE order_id = ? AND payment_method = 'COD' AND payment_status = 'PENDING'")) {
                        ps.setInt(1, orderId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement("UPDATE orders SET order_status = 'DELIVERED', delivered_at = SYSDATETIME() WHERE order_id = ?")) {
                        ps.setInt(1, orderId);
                        ps.executeUpdate();
                    }
                } else {
                    if ("CONFIRMED".equals(newStatus)) {
                        assignSerials(con, orderId);
                    }
                    try (PreparedStatement ps = con.prepareStatement("UPDATE orders SET order_status = ?, tracking_code = ISNULL(?, tracking_code) WHERE order_id = ?")) {
                        ps.setString(1, newStatus);
                        String tc = trackingOrReason == null || trackingOrReason.trim().isEmpty() ? null : trackingOrReason.trim();
                        if (tc == null) {
                            ps.setNull(2, Types.VARCHAR);
                        } else {
                            ps.setString(2, tc.length() > 100 ? tc.substring(0, 100) : tc);
                        }
                        ps.setInt(3, orderId);
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Không cập nhật được trạng thái đơn!";
        }
        if ("DELIVERED".equals(newStatus)) {
            try {
                new WarrantyService().issueWarrantiesForOrder(orderId, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        String msg;
        switch (newStatus) {
            case "CONFIRMED":
                msg = "đã được xác nhận.";
                break;
            case "SHIPPING":
                msg = "đang được giao đến bạn.";
                break;
            default:
                msg = "đã giao thành công. Bạn có thể đánh giá sản phẩm hoặc yêu cầu đổi trả trong Lịch sử mua hàng.";
                break;
        }
        notifications.notifyOrder(o.userId, o.orderId, "Đơn hàng " + o.orderCode + " " + msg);
        sendOrderEmailAsync(o.userId, o.orderCode, newStatus,
                "Đơn hàng " + o.orderCode + " " + msg);
        return null;
    }
}   