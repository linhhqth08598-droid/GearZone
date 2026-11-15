package dungntth08581.service;

import fourSome.model.ShippingAddresses;
import fourSome.url.DBConnect;
import khanhnb08894.service.InventoryService;
import linhhqth08598.service.NotificationService;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Đặt hàng / hủy đơn / cập nhật trạng thái (bản gọn làm hộ Dũng - UC 222-235).
 * Tạo đơn + giữ kho + xóa giỏ nằm trong MỘT giao dịch. Giá luôn lấy lại từ DB.
 * Chưa có: mã giảm giá, khuyến mãi, thanh toán online (VNPAY/ZaloPay) - Dũng làm tiếp.
 */
public class OrderService {

    private final InventoryService inventory = new InventoryService();
    private final NotificationService notifications = new NotificationService();
    private final PromotionService promotions = new PromotionService();

    // ---------- DTO ----------
    public static class ShippingMethod {
        private int id; private String name; private double fee; private Integer days;
        public int getId() { return id; }
        public String getName() { return name; }
        public double getFee() { return fee; }
        public Integer getDays() { return days; }
    }

    public static class OrderRow {
        private int orderId, userId; private String orderCode, status, recipient, phone, address, note, cancelReason, trackingCode, customerName;
        private double total, fee, finalAmount, discount; private Timestamp createdAt; private String couponCode;
        public int getOrderId() { return orderId; }
        public int getUserId() { return userId; }
        public String getOrderCode() { return orderCode; }
        public String getStatus() { return status; }
        public String getRecipient() { return recipient; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public String getNote() { return note; }
        public String getCancelReason() { return cancelReason; }
        public String getTrackingCode() { return trackingCode; }
        public String getCustomerName() { return customerName; }
        public double getTotal() { return total; }
        public double getFee() { return fee; }
        public double getFinalAmount() { return finalAmount; }
        public double getDiscount() { return discount; }
        public String getCouponCode() { return couponCode; }
        public Timestamp getCreatedAt() { return createdAt; }
    }

    public static class OrderLine {
        private String partName; private int quantity; private double unitPrice;
        public String getPartName() { return partName; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getLineTotal() { return unitPrice * quantity; }
    }

    public static class PlaceResult {
        public int orderId; public String orderCode; public String error;
    }

    // ---------- Đọc ----------
    public List<ShippingMethod> getShippingMethods() {
        List<ShippingMethod> list = new ArrayList<>();
        String sql = "SELECT shipping_method_id, method_name, base_fee, estimated_days FROM shipping_methods WHERE status = 'ACTIVE' ORDER BY base_fee";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ShippingMethod m = new ShippingMethod();
                m.id = rs.getInt(1); m.name = rs.getString(2); m.fee = rs.getDouble(3);
                int d = rs.getInt(4); m.days = rs.wasNull() ? null : d;
                list.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private OrderRow map(ResultSet rs) throws SQLException {
        OrderRow o = new OrderRow();
        o.orderId = rs.getInt("order_id"); o.userId = rs.getInt("user_id"); o.orderCode = rs.getString("order_code");
        o.status = rs.getString("order_status"); o.recipient = rs.getString("recipient_name"); o.phone = rs.getString("recipient_phone");
        o.address = rs.getString("shipping_address"); o.note = rs.getString("note"); o.cancelReason = rs.getString("cancel_reason");
        o.trackingCode = rs.getString("tracking_code"); o.total = rs.getDouble("total_amount"); o.fee = rs.getDouble("shipping_fee");
        o.finalAmount = rs.getDouble("final_amount"); o.createdAt = rs.getTimestamp("created_at"); o.discount = rs.getDouble("discount_amount");
        return o;
    }

    /** Đơn của user (null nếu không phải đơn của họ). */
    public OrderRow getUserOrder(int userId, int orderId) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT o.*, c.code AS coupon_code FROM orders o LEFT JOIN coupons c ON c.coupon_id = o.coupon_id WHERE o.order_id = ? AND o.user_id = ?")) {
            ps.setInt(1, orderId); ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { OrderRow o = map(rs); o.couponCode = rs.getString("coupon_code"); return o; } }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public OrderRow getAnyOrder(int orderId) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM orders WHERE order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public List<OrderLine> getLines(int orderId) {
        List<OrderLine> list = new ArrayList<>();
        String sql = "SELECT p.part_name, oi.quantity, oi.unit_price FROM order_items oi JOIN parts p ON p.part_id = oi.part_id WHERE oi.order_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderLine l = new OrderLine();
                    l.partName = rs.getString(1); l.quantity = rs.getInt(2); l.unitPrice = rs.getDouble(3);
                    list.add(l);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Admin: danh sách đơn, lọc theo trạng thái (null/rỗng = tất cả). */
    public List<OrderRow> listForAdmin(String status) {
        List<OrderRow> list = new ArrayList<>();
        boolean f = status != null && !status.isEmpty();
        String sql = "SELECT o.*, u.full_name FROM orders o LEFT JOIN users u ON u.user_id = o.user_id "
                   + (f ? "WHERE o.order_status = ? " : "") + "ORDER BY o.created_at DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (f) ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { OrderRow o = map(rs); o.customerName = rs.getString("full_name"); list.add(o); }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ---------- Đặt hàng (UC 222-227, 229) ----------
    public PlaceResult placeOrder(int userId, ShippingAddresses addr, int shippingMethodId, String note) {
        return placeOrder(userId, addr, shippingMethodId, note, null);
    }

    /** UC 224: couponCode có thể null/rỗng. Giảm giá tính lại ở server; mã sai/hết hạn -> không đặt đơn, báo lỗi để khách sửa. */
    public PlaceResult placeOrder(int userId, ShippingAddresses addr, int shippingMethodId, String note, String couponCode) {
        PlaceResult res = new PlaceResult();
        if (addr == null) { res.error = "Vui lòng chọn địa chỉ nhận hàng!"; return res; }
        if (note != null && note.length() > 500) note = note.substring(0, 500);

        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                // 1. Giỏ hàng + giá từ DB
                List<int[]> items = new ArrayList<>();           // {partId, qty}
                List<BigDecimal[]> prices = new ArrayList<>();   // {unit_price, cost_price}
                List<String> names = new ArrayList<>();
                BigDecimal total = BigDecimal.ZERO;
                String sel = "SELECT c.part_id, c.quantity, ISNULL(p.sale_price, p.base_price), p.avg_cost_price, p.part_name "
                           + "FROM cart_items c JOIN parts p ON p.part_id = c.part_id "
                           + "WHERE c.user_id = ? AND c.status = 'ACTIVE' AND p.status = 'ACTIVE'";
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
                if (items.isEmpty()) { con.rollback(); res.error = "Giỏ hàng trống hoặc sản phẩm đã ngừng bán!"; return res; }

                // 2. Phí vận chuyển (tùy chọn)
                BigDecimal fee = BigDecimal.ZERO; Integer methodId = null;
                if (shippingMethodId > 0) {
                    try (PreparedStatement ps = con.prepareStatement("SELECT base_fee FROM shipping_methods WHERE shipping_method_id = ? AND status = 'ACTIVE'")) {
                        ps.setInt(1, shippingMethodId);
                        try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { fee = rs.getBigDecimal(1); methodId = shippingMethodId; } }
                    }
                }
                // 2b. Khuyến mãi tự động + mã giảm giá (UC 224, 241-247), tính trong cùng transaction
                PromotionService.Result disc = promotions.calculate(con, promotions.loadCartItems(con, userId), fee, userId, couponCode);
                if (disc.error != null) { con.rollback(); res.error = disc.error; return res; }
                if (disc.couponId != null && !promotions.reserveCouponUse(con, disc.couponId)) {
                    con.rollback(); res.error = "Mã giảm giá đã hết lượt sử dụng!"; return res;
                }
                BigDecimal goodsDiscount = disc.getGoodsDiscount().min(total);
                BigDecimal shipFee = fee.subtract(disc.getShippingDiscount()).max(BigDecimal.ZERO);
                BigDecimal finalAmount = total.subtract(goodsDiscount).add(shipFee);

                // 3. Ghi đơn
                String code = "GZ" + new java.text.SimpleDateFormat("yyMMddHHmmss").format(new java.util.Date()) + (100 + new Random().nextInt(900));
                String fullAddress = addr.getDetailedAddress() + ", " + addr.getWard() + ", " + addr.getProvince();
                if (fullAddress.length() > 255) fullAddress = fullAddress.substring(0, 255);
                int orderId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO orders (order_code, user_id, recipient_name, recipient_phone, shipping_address, shipping_method_id, shipping_fee, total_amount, discount_amount, final_amount, order_status, note, coupon_id, promotion_id, created_at) "
                      + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?, ?, ?, SYSDATETIME())", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, code); ps.setInt(2, userId); ps.setString(3, addr.getRecipientName()); ps.setString(4, addr.getPhone());
                    ps.setString(5, fullAddress);
                    if (methodId == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, methodId);
                    ps.setBigDecimal(7, shipFee); ps.setBigDecimal(8, total); ps.setBigDecimal(9, goodsDiscount); ps.setBigDecimal(10, finalAmount); ps.setString(11, note);
                    if (disc.couponId == null) ps.setNull(12, Types.INTEGER); else ps.setInt(12, disc.couponId);
                    if (disc.promotionId == null || disc.promoDiscount.signum() == 0) ps.setNull(13, Types.INTEGER); else ps.setInt(13, disc.promotionId);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); orderId = rs.getInt(1); }
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
                        ps.setInt(1, orderId); ps.setInt(2, partId); ps.setInt(3, qty);
                        ps.setBigDecimal(4, prices.get(i)[0]);
                        if (prices.get(i)[1] == null) ps.setNull(5, Types.DECIMAL); else ps.setBigDecimal(5, prices.get(i)[1]);
                        ps.executeUpdate();
                    }
                }

                // 5. Thanh toán khi nhận hàng (COD) + xóa giỏ
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO payments (order_id, payment_method, amount, payment_status, created_at) VALUES (?, 'COD', ?, 'PENDING', SYSDATETIME())")) {
                    ps.setInt(1, orderId); ps.setBigDecimal(2, finalAmount); ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM cart_items WHERE user_id = ?")) {
                    ps.setInt(1, userId); ps.executeUpdate();
                }
                con.commit();

                res.orderId = orderId; res.orderCode = code;
                notifications.notifyOrder(userId, orderId, "Đơn hàng " + code + " đã được tiếp nhận. Chúng tôi sẽ sớm xác nhận và giao hàng cho bạn.");
                return res;
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) {
            e.printStackTrace();
            res.error = "Không đặt được hàng, vui lòng thử lại!";
            return res;
        }
    }

    // ---------- Hủy đơn (UC 231) ----------
    /** Khách hủy đơn của mình (chỉ khi PENDING/CONFIRMED). Trả về null nếu OK. */
    public String cancelByUser(int userId, int orderId, String reason) {
        OrderRow o = getUserOrder(userId, orderId);
        if (o == null) return "Không tìm thấy đơn hàng!";
        return cancel(o, reason == null || reason.trim().isEmpty() ? "Khách hàng hủy đơn" : reason.trim());
    }

    /** Khách xác nhận đã nhận hàng: chỉ chủ đơn, chỉ khi đơn đang SHIPPING -> DELIVERED. Trả về null nếu OK. */
    public String confirmReceived(int userId, int orderId) {
        OrderRow o = getUserOrder(userId, orderId);
        if (o == null) return "Không tìm thấy đơn hàng!";
        if (!"SHIPPING".equals(o.status)) return "Chỉ xác nhận được khi đơn đang giao!";
        return adminUpdateStatus(orderId, "DELIVERED", null);
    }

    private String cancel(OrderRow o, String reason) {
        if (!"PENDING".equals(o.status) && !"CONFIRMED".equals(o.status)) return "Đơn ở trạng thái hiện tại không thể hủy!";
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                releaseItems(con, o.orderId);
                promotions.releaseCouponUse(con, o.orderId);
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE orders SET order_status = 'CANCELLED', cancel_reason = ?, cancelled_at = SYSDATETIME() WHERE order_id = ?")) {
                    ps.setString(1, reason.length() > 500 ? reason.substring(0, 500) : reason); ps.setInt(2, o.orderId); ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("UPDATE payments SET payment_status = 'FAILED' WHERE order_id = ? AND payment_status = 'PENDING'")) {
                    ps.setInt(1, o.orderId); ps.executeUpdate();
                }
                con.commit();
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) { e.printStackTrace(); return "Không hủy được đơn, vui lòng thử lại!"; }
        notifications.notifyOrder(o.userId, o.orderId, "Đơn hàng " + o.orderCode + " đã được hủy.");
        return null;
    }

    private void releaseItems(Connection con, int orderId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT part_id, quantity FROM order_items WHERE order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) inventory.release(con, rs.getInt(1), rs.getInt(2), orderId); }
        }
    }

    // ---------- Admin cập nhật trạng thái ----------
    /**
     * PENDING -> CONFIRMED -> SHIPPING -> DELIVERED; hủy được khi PENDING/CONFIRMED.
     * DELIVERED: xuất kho, đánh dấu COD đã thanh toán. Trả về null nếu OK.
     */
    public String adminUpdateStatus(int orderId, String newStatus, String trackingOrReason) {
        OrderRow o = getAnyOrder(orderId);
        if (o == null) return "Không tìm thấy đơn hàng!";
        if ("CANCELLED".equals(newStatus)) {
            return cancel(o, trackingOrReason == null || trackingOrReason.trim().isEmpty() ? "Cửa hàng hủy đơn" : trackingOrReason.trim());
        }
        String expected;
        switch (o.status) {
            case "PENDING": expected = "CONFIRMED"; break;
            case "CONFIRMED": expected = "SHIPPING"; break;
            case "SHIPPING": expected = "DELIVERED"; break;
            default: return "Đơn ở trạng thái này không thể chuyển tiếp!";
        }
        if (!expected.equals(newStatus)) return "Chỉ được chuyển sang bước kế tiếp: " + expected + "!";

        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                if ("DELIVERED".equals(newStatus)) {
                    try (PreparedStatement ps = con.prepareStatement("SELECT part_id, quantity FROM order_items WHERE order_id = ?")) {
                        ps.setInt(1, orderId);
                        try (ResultSet rs = ps.executeQuery()) { while (rs.next()) inventory.exportStock(con, rs.getInt(1), rs.getInt(2), orderId); }
                    }
                    try (PreparedStatement ps = con.prepareStatement("UPDATE payments SET payment_status = 'PAID', paid_at = SYSDATETIME() WHERE order_id = ? AND payment_method = 'COD' AND payment_status = 'PENDING'")) {
                        ps.setInt(1, orderId); ps.executeUpdate();
                    }
                    try (PreparedStatement ps = con.prepareStatement("UPDATE orders SET order_status = 'DELIVERED', delivered_at = SYSDATETIME() WHERE order_id = ?")) {
                        ps.setInt(1, orderId); ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE orders SET order_status = ?, tracking_code = ISNULL(?, tracking_code) WHERE order_id = ?")) {
                        ps.setString(1, newStatus);
                        String tc = trackingOrReason == null || trackingOrReason.trim().isEmpty() ? null : trackingOrReason.trim();
                        if (tc == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, tc.length() > 100 ? tc.substring(0, 100) : tc);
                        ps.setInt(3, orderId); ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) { e.printStackTrace(); return "Không cập nhật được trạng thái đơn!"; }

        String msg;
        switch (newStatus) {
            case "CONFIRMED": msg = "đã được xác nhận."; break;
            case "SHIPPING": msg = "đang được giao đến bạn."; break;
            default: msg = "đã giao thành công. Bạn có thể đánh giá sản phẩm hoặc yêu cầu đổi trả trong Lịch sử mua hàng."; break;
        }
        notifications.notifyOrder(o.userId, o.orderId, "Đơn hàng " + o.orderCode + " " + msg);
        return null;
    }
}
