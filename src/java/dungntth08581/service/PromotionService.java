package dungntth08581.service;

import fourSome.url.DBConnect;
import linhhqth08598.service.NotificationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Mã giảm giá + khuyến mãi (Module C, UC 236-250) và tính giảm giá cho giỏ hàng (UC 224, 245-247).
 *
 * Minh (UC 170) gọi: new PromotionService().preview(userId, couponCode, shippingFee)
 * Checkout gọi: calculate(con, items, fee, userId, code) trong cùng transaction đặt hàng.
 *
 * !!! Các giá trị enum dưới đây là GIẢ ĐỊNH (chưa có GearZone.sql) - nếu CHECK constraint của DB khác thì chỉ cần sửa tại đây.
 */
public class PromotionService {

    // ----- giả định giá trị enum trong DB (sửa nếu khác) -----
    public static final String PERCENT = "PERCENT";        // giảm theo %
    public static final String FIXED = "FIXED";            // giảm số tiền cố định
    public static final String FREE_SHIP = "FREE_SHIP";    // miễn phí vận chuyển (chỉ coupon)
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String PROMO_DISCOUNT = "DISCOUNT";   // khớp CHECK promotions.promotion_type (DISCOUNT / FREE_SHIPPING); đối tượng áp dụng nằm ở target_*_id
    public static final String COMBO_FULL_PC = "FULL_BUILD", COMBO_PARTS = "PART_COMBO";   // khớp CHECK combo_promotions.combo_type

    // ---------- DTO ----------
    public static class Item {            // 1 dòng trong giỏ để tính giảm giá
        public int partId, qty; public Integer categoryId, brandId; public BigDecimal unitPrice; public String slot;   // slot: cpu/main/ram/vga/psu hoặc null
        public BigDecimal total() { return unitPrice.multiply(BigDecimal.valueOf(qty)); }
    }

    /** Kết quả tính giảm giá. error != null => mã không dùng được (khuyến mãi tự động vẫn hợp lệ). */
    public static class Result {
        public java.util.List<String[]> lines = new ArrayList<>();   // chi tiết giảm giá: {loại, tên, số tiền}
        public java.util.List<String[]> getLines() { return lines; }
        public BigDecimal comboDiscount = BigDecimal.ZERO; public Integer comboId; public String comboName;
        public BigDecimal getComboDiscount() { return comboDiscount; } public Integer getComboId() { return comboId; } public String getComboName() { return comboName; }
        public BigDecimal subtotal = BigDecimal.ZERO, promoDiscount = BigDecimal.ZERO, couponDiscount = BigDecimal.ZERO, shippingDiscount = BigDecimal.ZERO;
        public Integer couponId, promotionId; public String couponCode, error;
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getPromoDiscount() { return promoDiscount; }
        public BigDecimal getCouponDiscount() { return couponDiscount; }
        public BigDecimal getShippingDiscount() { return shippingDiscount; }
        /** Giảm trên tiền hàng (ghi vào orders.discount_amount). */
        public BigDecimal getGoodsDiscount() { return promoDiscount.add(comboDiscount).add(couponDiscount); }
        public String getError() { return error; }
    }

    public static class CouponRow {
        public int id, usageLimit, perUserLimit, usedCount; public Integer categoryId, brandId;
        public String code, name, type, status, categoryName, brandName;
        public double value, minOrder, maxDiscount; public Date startDate, endDate;
        public int getId() { return id; } public String getCode() { return code; } public String getName() { return name; }
        public String getType() { return type; } public String getStatus() { return status; } public double getValue() { return value; }
        public double getMinOrder() { return minOrder; } public double getMaxDiscount() { return maxDiscount; }
        public Date getStartDate() { return startDate; } public Date getEndDate() { return endDate; }
        public int getUsageLimit() { return usageLimit; } public int getPerUserLimit() { return perUserLimit; } public int getUsedCount() { return usedCount; }
        public Integer getCategoryId() { return categoryId; } public Integer getBrandId() { return brandId; }
        public String getCategoryName() { return categoryName; } public String getBrandName() { return brandName; }
    }

    public static class PromoRow {
        public int id; public String name, type, discountType, status, targetName; public double value, minOrder; public Date startDate, endDate;
        public int getId() { return id; } public String getName() { return name; } public String getType() { return type; }
        public String getDiscountType() { return discountType; } public String getStatus() { return status; } public String getTargetName() { return targetName; }
        public double getValue() { return value; } public double getMinOrder() { return minOrder; }
        public Date getStartDate() { return startDate; } public Date getEndDate() { return endDate; }
    }

    public static class ComboRow {
        public int id, minParts; public String name, type, status; public double percent; public Date startDate, endDate;
        public int getId() { return id; } public String getName() { return name; } public String getType() { return type; }
        public String getStatus() { return status; } public double getPercent() { return percent; } public int getMinParts() { return minParts; }
        public Date getStartDate() { return startDate; } public Date getEndDate() { return endDate; }
    }

    public static class UsageRow {
        public String orderCode, customerName, orderStatus; public double discount; public Timestamp usedAt;
        public String getOrderCode() { return orderCode; } public String getCustomerName() { return customerName; }
        public double getDiscount() { return discount; } public Timestamp getUsedAt() { return usedAt; } public String getOrderStatus() { return orderStatus; }
    }

    /** Dòng hiển thị linh kiện đang khuyến mãi (UC 249). */
    public static class DiscountedPart {
        public int partId; public String partName, promotionName; public double price, salePrice;
        public int getPartId() { return partId; } public String getPartName() { return partName; } public String getPromotionName() { return promotionName; }
        public double getPrice() { return price; } public double getSalePrice() { return salePrice; }
    }

    private static Integer intOrNull(ResultSet rs, String col) throws SQLException { int v = rs.getInt(col); return rs.wasNull() ? null : v; }
    private static BigDecimal money(BigDecimal v) { return v.setScale(0, RoundingMode.HALF_UP); }
    private static boolean blank(String s) { return s == null || s.trim().isEmpty(); }

    private static final String RUNNING_P = "(p.start_date IS NULL OR p.start_date <= CAST(SYSDATETIME() AS DATE)) AND (p.end_date IS NULL OR p.end_date >= CAST(SYSDATETIME() AS DATE))";

    // =====================================================================
    // TÍNH GIẢM GIÁ (UC 224, 241-242, 245-247)
    // =====================================================================

    /** Nhận diện loại linh kiện từ mã/tên danh mục (giống BuildPCService.slotKey). */
    static String slotKey(String code, String name) {
        String t = ((code == null ? "" : code) + " " + (name == null ? "" : name)).toUpperCase();
        if (t.contains("COOL") || t.contains("TẢN")) return null;
        if (t.contains("PSU") || t.contains("NGUỒN") || t.contains("POWER")) return "psu";
        if (t.contains("VGA") || t.contains("GPU") || t.contains("ĐỒ HỌA")) return "vga";
        if (t.contains("MAIN") || t.contains("MẠCH CHỦ")) return "main";
        if (t.contains("RAM")) return "ram";
        if (t.contains("CPU") || t.contains("VI XỬ LÝ")) return "cpu";
        return null;
    }

    /** Lấy giỏ hàng ACTIVE của user (kèm danh mục/thương hiệu) để tính giảm giá. */
    public List<Item> loadCartItems(Connection con, int userId) throws SQLException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT c.part_id, c.quantity, p.category_id, p.brand_id, ISNULL(p.sale_price, p.base_price) AS price, ca.category_code, ca.category_name "
                   + "FROM cart_items c JOIN parts p ON p.part_id = c.part_id LEFT JOIN categories ca ON ca.category_id = p.category_id WHERE c.user_id = ? AND c.status = 'ACTIVE' AND p.status = 'ACTIVE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Item it = new Item(); it.partId = rs.getInt("part_id"); it.qty = rs.getInt("quantity");
                    it.categoryId = intOrNull(rs, "category_id"); it.brandId = intOrNull(rs, "brand_id"); it.unitPrice = rs.getBigDecimal("price"); it.slot = slotKey(rs.getString("category_code"), rs.getString("category_name"));
                    list.add(it);
                }
            }
        }
        return list;
    }

    /** Xem trước giảm giá cho giỏ hiện tại (checkout + Minh UC 170). code có thể null. */
    public Result preview(int userId, String code, double shippingFee) {
        try (Connection con = DBConnect.getConnection()) {
            return calculate(con, loadCartItems(con, userId), BigDecimal.valueOf(shippingFee), userId, code);
        } catch (Exception e) {
            e.printStackTrace();
            Result r = new Result(); r.error = "Không tính được giảm giá, vui lòng thử lại!"; return r;
        }
    }

    /**
     * Tính: khuyến mãi tự động (theo linh kiện/danh mục/thương hiệu, lấy mức giảm tốt nhất cho từng dòng), rồi mã giảm giá.
     * Giá luôn lấy từ DB, không tin client. Không ghi gì vào DB.
     */
    public Result calculate(Connection con, List<Item> items, BigDecimal shippingFee, int userId, String couponCode) throws SQLException {
        Result r = new Result();
        for (Item it : items) r.subtotal = r.subtotal.add(it.total());

        // 1. Khuyến mãi tự động đang chạy: {id, partId, categoryId, brandId, discountType, value, minOrder}
        List<Object[]> promos = new ArrayList<>();
        String sql = "SELECT promotion_name, promotion_id, target_part_id, target_category_id, target_brand_id, discount_type, discount_value, ISNULL(min_order_value,0) AS min_order "
                   + "FROM promotions p WHERE p.status = ? AND " + RUNNING_P;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ACTIVE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) promos.add(new Object[]{rs.getInt("promotion_id"), intOrNull(rs, "target_part_id"), intOrNull(rs, "target_category_id"),
                        intOrNull(rs, "target_brand_id"), rs.getString("discount_type"), rs.getBigDecimal("discount_value"), rs.getBigDecimal("min_order"), rs.getString("promotion_name")});
            }
        }
        BigDecimal biggest = BigDecimal.ZERO;
        java.util.Map<String, BigDecimal> promoByName = new java.util.LinkedHashMap<>();
        for (Item it : items) {
            BigDecimal best = BigDecimal.ZERO; Integer bestId = null; String bestName = null;
            for (Object[] p : promos) {
                Integer tp = (Integer) p[1], tc = (Integer) p[2], tb = (Integer) p[3];
                boolean match = (tp != null && tp == it.partId) || (tc != null && tc.equals(it.categoryId)) || (tb != null && tb.equals(it.brandId));
                if (!match || r.subtotal.compareTo((BigDecimal) p[6]) < 0) continue;
                BigDecimal d = discountOf((String) p[4], (BigDecimal) p[5], it.total(), null);
                if (d.compareTo(best) > 0) { best = d; bestId = (Integer) p[0]; bestName = (String) p[7]; }
            }
            r.promoDiscount = r.promoDiscount.add(best);
            if (bestName != null) promoByName.merge(bestName, best, BigDecimal::add);
            if (best.compareTo(biggest) > 0) { biggest = best; r.promotionId = bestId; }   // orders.promotion_id chỉ lưu được 1 khuyến mãi: lấy cái giảm nhiều nhất
        }
        r.promoDiscount = money(r.promoDiscount);
        for (java.util.Map.Entry<String, BigDecimal> e : promoByName.entrySet()) r.lines.add(new String[]{"Khuyến mãi", e.getKey(), money(e.getValue()).toPlainString()});

        // 1b. Combo / trọn bộ PC (UC 243-244): lấy combo giảm nhiều nhất mà giỏ đủ điều kiện
        java.util.Set<String> slots = new java.util.HashSet<>();
        for (Item it : items) if (it.slot != null) slots.add(it.slot);
        boolean fullBuild = slots.containsAll(java.util.Arrays.asList("cpu", "main", "ram", "psu"));
        BigDecimal comboBase = r.subtotal.subtract(r.promoDiscount).max(BigDecimal.ZERO);
        String csql = "SELECT combo_id, combo_name, discount_percentage, min_parts_count, combo_type FROM combo_promotions p WHERE p.status = ? AND " + RUNNING_P;
        try (PreparedStatement ps = con.prepareStatement(csql)) {
            ps.setString(1, ACTIVE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ctype = rs.getString("combo_type");
                    if (items.size() < rs.getInt("min_parts_count")) continue;
                    if (COMBO_FULL_PC.equals(ctype) && !fullBuild) continue;     // trọn bộ PC: cần đủ CPU, Mainboard, RAM, nguồn
                    BigDecimal d = money(comboBase.multiply(rs.getBigDecimal("discount_percentage")).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP));
                    if (d.compareTo(r.comboDiscount) > 0) { r.comboDiscount = d; r.comboId = rs.getInt("combo_id"); r.comboName = rs.getString("combo_name"); }
                }
            }
        }

        if (r.comboId != null) r.lines.add(new String[]{"Combo", r.comboName, r.comboDiscount.toPlainString()});

        // 2. Mã giảm giá
        if (!blank(couponCode)) {
            String err = applyCoupon(con, r, items, shippingFee, userId, couponCode.trim());
            if (err != null) {
                r.error = err; r.couponId = null; r.couponCode = null;
                r.couponDiscount = BigDecimal.ZERO; r.shippingDiscount = BigDecimal.ZERO;
            } else {
                BigDecimal amt = r.couponDiscount.signum() > 0 ? r.couponDiscount : r.shippingDiscount;
                r.lines.add(new String[]{r.shippingDiscount.signum() > 0 ? "Mã miễn phí vận chuyển" : "Mã giảm giá", r.couponCode, amt.toPlainString()});
            }
        }
        return r;
    }

    private BigDecimal discountOf(String type, BigDecimal value, BigDecimal base, BigDecimal cap) {
        BigDecimal d = PERCENT.equals(type) ? base.multiply(value).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP) : value;
        if (cap != null && cap.signum() > 0 && d.compareTo(cap) > 0) d = cap;
        if (d.compareTo(base) > 0) d = base;
        return d.signum() < 0 ? BigDecimal.ZERO : d;
    }

    /** Kiểm tra điều kiện + thời hạn mã (UC 246-247) rồi tính giảm. Trả về thông báo lỗi hoặc null. */
    private String applyCoupon(Connection con, Result r, List<Item> items, BigDecimal fee, int userId, String code) throws SQLException {
        String sql = "SELECT coupon_id, discount_type, discount_value, ISNULL(min_order_value,0) AS min_order, start_date, end_date, ISNULL(usage_limit,0) AS ulimit, "
                   + "ISNULL(per_user_limit,0) AS per_user, ISNULL(used_count,0) AS used, ISNULL(max_discount_amount,0) AS max_disc, "
                   + "applicable_category_id, applicable_brand_id, status FROM coupons WHERE code = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    // người dùng hay nhầm tên khuyến mãi/combo (tự động áp dụng) với mã giảm giá -> báo rõ
                    for (String[] l : r.lines) {
                        if (("Khuyến mãi".equals(l[0]) || "Combo".equals(l[0])) && l[1] != null && l[1].equalsIgnoreCase(code))
                            return "\"" + l[1] + "\" là " + (l[0].equals("Combo") ? "combo" : "khuyến mãi") + " tự động, đã được áp dụng sẵn vào đơn, không cần nhập mã. Ô này chỉ dành cho mã tạo ở mục \"Mã giảm giá\"!";
                    }
                    if (nameExists(con, code))
                        return "\"" + code + "\" là tên khuyến mãi/combo (tự áp dụng khi đủ điều kiện), không phải mã giảm giá. Hãy tạo mã ở Admin → Mã giảm giá!";
                    return "Mã giảm giá không tồn tại!";
                }
                int id = rs.getInt("coupon_id"); String type = rs.getString("discount_type");
                BigDecimal value = rs.getBigDecimal("discount_value"), min = rs.getBigDecimal("min_order"), cap = rs.getBigDecimal("max_disc");
                Date start = rs.getDate("start_date"), end = rs.getDate("end_date");
                int limit = rs.getInt("ulimit"), perUser = rs.getInt("per_user"), used = rs.getInt("used");
                Integer catId = intOrNull(rs, "applicable_category_id"), brandId = intOrNull(rs, "applicable_brand_id");
                if (!ACTIVE.equals(rs.getString("status"))) return "Mã giảm giá đã ngừng áp dụng!";
                long now = System.currentTimeMillis();
                if (start != null && start.getTime() > now) return "Mã giảm giá chưa đến thời gian áp dụng!";
                if (end != null && end.getTime() + 86400000L <= now) return "Mã giảm giá đã hết hạn!";
                if (limit > 0 && used >= limit) return "Mã giảm giá đã hết lượt sử dụng!";
                if (perUser > 0 && countUserUses(con, userId, id) >= perUser) return "Bạn đã dùng hết số lần cho phép của mã này!";
                if (r.subtotal.compareTo(min) < 0) return "Đơn hàng tối thiểu " + money(min).toPlainString() + " ₫ mới dùng được mã này!";

                // phần tiền hàng đủ điều kiện (giới hạn theo danh mục/thương hiệu) - UC 241-242
                BigDecimal eligible = BigDecimal.ZERO;
                for (Item it : items) {
                    boolean ok = (catId == null || catId.equals(it.categoryId)) && (brandId == null || brandId.equals(it.brandId));
                    if (ok) eligible = eligible.add(it.total());
                }
                if (eligible.signum() == 0) return "Mã này chỉ áp dụng cho một số danh mục/thương hiệu mà giỏ hàng của bạn chưa có.";

                if (FREE_SHIP.equals(type)) {                                   // UC 245
                    BigDecimal d = fee;
                    if (cap != null && cap.signum() > 0 && d.compareTo(cap) > 0) d = cap;
                    r.shippingDiscount = money(d);
                } else {
                    BigDecimal base = eligible.subtract(r.promoDiscount).subtract(r.comboDiscount).max(BigDecimal.ZERO);   // không giảm chồng vượt giá trị hàng
                    r.couponDiscount = money(discountOf(type, value, base, cap));
                }
                r.couponId = id; r.couponCode = code.toUpperCase();
                return null;
            }
        }
    }

    /** Có khuyến mãi/combo nào mang tên này không (để gợi ý khi nhập nhầm vào ô mã giảm giá). */
    private boolean nameExists(Connection con, String name) throws SQLException {
        String sql = "SELECT (SELECT COUNT(*) FROM promotions WHERE promotion_name = ?) + (SELECT COUNT(*) FROM combo_promotions WHERE combo_name = ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }

    private int countUserUses(Connection con, int userId, int couponId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM orders WHERE user_id = ? AND coupon_id = ? AND order_status <> 'CANCELLED'")) {
            ps.setInt(1, userId); ps.setInt(2, couponId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    /** Giữ 1 lượt dùng mã trong transaction đặt hàng (chống 2 người cùng dùng lượt cuối). false = hết lượt. */
    public boolean reserveCouponUse(Connection con, int couponId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE coupons SET used_count = ISNULL(used_count,0) + 1 WHERE coupon_id = ? AND (ISNULL(usage_limit,0) <= 0 OR ISNULL(used_count,0) < usage_limit)")) {
            ps.setInt(1, couponId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Hoàn lượt dùng mã khi hủy đơn (gọi trong transaction hủy, trước khi đổi trạng thái đơn). */
    public void releaseCouponUse(Connection con, int orderId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE coupons SET used_count = CASE WHEN ISNULL(used_count,0) > 0 THEN used_count - 1 ELSE 0 END "
              + "WHERE coupon_id = (SELECT coupon_id FROM orders WHERE order_id = ?)")) {
            ps.setInt(1, orderId); ps.executeUpdate();
        }
    }

    // =====================================================================
    // QUẢN LÝ MÃ GIẢM GIÁ (UC 236-240, 248)
    // =====================================================================

    public List<CouponRow> listCoupons() {
        List<CouponRow> list = new ArrayList<>();
        String sql = "SELECT c.*, ca.category_name, b.brand_name FROM coupons c LEFT JOIN categories ca ON ca.category_id = c.applicable_category_id "
                   + "LEFT JOIN brands b ON b.brand_id = c.applicable_brand_id ORDER BY c.coupon_id DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CouponRow c = new CouponRow();
                c.id = rs.getInt("coupon_id"); c.code = rs.getString("code"); c.name = rs.getString("coupon_name"); c.type = rs.getString("discount_type");
                c.value = rs.getDouble("discount_value"); c.minOrder = rs.getDouble("min_order_value"); c.startDate = rs.getDate("start_date"); c.endDate = rs.getDate("end_date");
                c.usageLimit = rs.getInt("usage_limit"); c.perUserLimit = rs.getInt("per_user_limit"); c.usedCount = rs.getInt("used_count");
                c.maxDiscount = rs.getDouble("max_discount_amount"); c.categoryId = intOrNull(rs, "applicable_category_id"); c.brandId = intOrNull(rs, "applicable_brand_id");
                c.status = rs.getString("status"); c.categoryName = rs.getString("category_name"); c.brandName = rs.getString("brand_name");
                list.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public CouponRow findCoupon(int id) {
        for (CouponRow c : listCoupons()) if (c.id == id) return c;
        return null;
    }

    /** Tạo/cập nhật mã. id = 0 -> tạo mới. Trả về null nếu OK, ngược lại là lỗi. */
    public String saveCoupon(int id, String code, String name, String type, String valueS, String minS, String startS, String endS,
                             String limitS, String perUserS, String maxS, String catS, String brandS) {
        try {
            name = name == null ? "" : name.trim();
            if (name.isEmpty() || name.length() > 150) return "Tên mã không được để trống (tối đa 150 ký tự)!";
            if (!PERCENT.equals(type) && !FIXED.equals(type) && !FREE_SHIP.equals(type)) return "Loại giảm giá không hợp lệ!";
            BigDecimal value = FREE_SHIP.equals(type) && blank(valueS) ? BigDecimal.ZERO : new BigDecimal(valueS.trim());
            if (!FREE_SHIP.equals(type) && value.signum() <= 0) return "Giá trị giảm phải lớn hơn 0!";
            if (PERCENT.equals(type) && value.compareTo(BigDecimal.valueOf(100)) > 0) return "Giảm theo % không được vượt quá 100!";
            BigDecimal min = blank(minS) ? BigDecimal.ZERO : new BigDecimal(minS.trim());
            BigDecimal max = blank(maxS) ? BigDecimal.ZERO : new BigDecimal(maxS.trim());
            int limit = blank(limitS) ? 0 : Integer.parseInt(limitS.trim()), perUser = blank(perUserS) ? 0 : Integer.parseInt(perUserS.trim());
            if (min.signum() < 0 || max.signum() < 0 || limit < 0 || perUser < 0) return "Các giá trị không được âm!";
            Date start = blank(startS) ? new Date(System.currentTimeMillis()) : Date.valueOf(startS.trim()), end = blank(endS) ? Date.valueOf("2099-12-31") : Date.valueOf(endS.trim());   // DB: start_date/end_date NOT NULL
            if (start != null && end != null && end.before(start)) return "Ngày kết thúc phải sau ngày bắt đầu!";
            Integer cat = blank(catS) ? null : Integer.valueOf(catS.trim()), brand = blank(brandS) ? null : Integer.valueOf(brandS.trim());

            try (Connection con = DBConnect.getConnection()) {
                if (id == 0) {
                    code = code == null ? "" : code.trim().toUpperCase();
                    if (!code.matches("[A-Z0-9_-]{3,30}")) return "Mã chỉ gồm chữ/số/gạch (3-30 ký tự)!";
                    try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM coupons WHERE code = ?")) {
                        ps.setString(1, code);
                        try (ResultSet rs = ps.executeQuery()) { rs.next(); if (rs.getInt(1) > 0) return "Mã \"" + code + "\" đã tồn tại!"; }
                    }
                    String sql = "INSERT INTO coupons (code, coupon_name, discount_type, discount_value, min_order_value, start_date, end_date, usage_limit, per_user_limit, "
                               + "used_count, max_discount_amount, applicable_category_id, applicable_brand_id, status) VALUES (?,?,?,?,?,?,?,?,?,0,?,?,?,?)";
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        int i = 1;
                        ps.setString(i++, code);
                        bindCommon(ps, i, name, type, value, min, start, end, limit, perUser, max, cat, brand);
                        ps.setString(13, ACTIVE);
                        ps.executeUpdate();
                    }
                } else {
                    String sql = "UPDATE coupons SET coupon_name=?, discount_type=?, discount_value=?, min_order_value=?, start_date=?, end_date=?, usage_limit=?, per_user_limit=?, "
                               + "max_discount_amount=?, applicable_category_id=?, applicable_brand_id=? WHERE coupon_id=?";
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        bindCommon(ps, 1, name, type, value, min, start, end, limit, perUser, max, cat, brand);
                        ps.setInt(12, id);
                        ps.executeUpdate();
                    }
                }
            }
            return null;
        } catch (java.time.DateTimeException | NullPointerException | IllegalArgumentException e) {
            return "Dữ liệu nhập không hợp lệ (số hoặc ngày sai định dạng)!";
        } catch (Exception e) { e.printStackTrace(); return "Không lưu được mã giảm giá (kiểm tra danh mục/thương hiệu có tồn tại)!"; }
    }

    /** Gán 11 tham số chung từ vị trí i (name ... brand). */
    private void bindCommon(PreparedStatement ps, int i, String name, String type, BigDecimal value, BigDecimal min, Date start, Date end,
                            int limit, int perUser, BigDecimal max, Integer cat, Integer brand) throws SQLException {
        ps.setString(i++, name); ps.setString(i++, type); ps.setBigDecimal(i++, value); ps.setBigDecimal(i++, min);
        if (start == null) ps.setNull(i++, Types.DATE); else ps.setDate(i++, start);
        if (end == null) ps.setNull(i++, Types.DATE); else ps.setDate(i++, end);
        ps.setInt(i++, limit); ps.setInt(i++, perUser); ps.setBigDecimal(i++, max);
        if (cat == null) ps.setNull(i++, Types.INTEGER); else ps.setInt(i++, cat);
        if (brand == null) ps.setNull(i++, Types.INTEGER); else ps.setInt(i++, brand);
    }

    /** Bật/tắt mã (không xóa cứng để giữ lịch sử đơn). */
    public boolean toggleCoupon(int id) {
        return exec("UPDATE coupons SET status = CASE WHEN status = '" + ACTIVE + "' THEN '" + INACTIVE + "' ELSE '" + ACTIVE + "' END WHERE coupon_id = ?", id);
    }

    /** UC 248: lịch sử các đơn đã dùng mã. */
    public List<UsageRow> couponUsage(int couponId) {
        List<UsageRow> list = new ArrayList<>();
        String sql = "SELECT o.order_code, u.full_name, o.discount_amount, o.created_at, o.order_status FROM orders o LEFT JOIN users u ON u.user_id = o.user_id "
                   + "WHERE o.coupon_id = ? ORDER BY o.created_at DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsageRow u = new UsageRow(); u.orderCode = rs.getString(1); u.customerName = rs.getString(2); u.discount = rs.getDouble(3);
                    u.usedAt = rs.getTimestamp(4); u.orderStatus = rs.getString(5); list.add(u);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // =====================================================================
    // KHUYẾN MÃI THEO LINH KIỆN / DANH MỤC / THƯƠNG HIỆU (UC 241-242, 249)
    // =====================================================================

    public List<PromoRow> listPromotions(boolean onlyRunning) {
        List<PromoRow> list = new ArrayList<>();
        String sql = "SELECT p.*, pt.part_name, ca.category_name, b.brand_name FROM promotions p LEFT JOIN parts pt ON pt.part_id = p.target_part_id "
                   + "LEFT JOIN categories ca ON ca.category_id = p.target_category_id LEFT JOIN brands b ON b.brand_id = p.target_brand_id "
                   + (onlyRunning ? "WHERE p.status = '" + ACTIVE + "' AND " + RUNNING_P + " " : "") + "ORDER BY p.promotion_id DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PromoRow p = new PromoRow();
                p.id = rs.getInt("promotion_id"); p.name = rs.getString("promotion_name"); p.type = rs.getString("part_name") != null ? "Linh kiện" : rs.getString("category_name") != null ? "Danh mục" : "Thương hiệu"; p.discountType = rs.getString("discount_type");
                p.value = rs.getDouble("discount_value"); p.minOrder = rs.getDouble("min_order_value"); p.startDate = rs.getDate("start_date"); p.endDate = rs.getDate("end_date");
                p.status = rs.getString("status");
                String t = rs.getString("part_name"); if (t == null) t = rs.getString("category_name"); if (t == null) t = rs.getString("brand_name");
                p.targetName = t; list.add(p);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** UC 249: linh kiện đang được khuyến mãi (giá gốc, giá sau giảm ước tính). */
    public List<DiscountedPart> discountedParts(int limit) {
        List<DiscountedPart> list = new ArrayList<>();
        String sql = "SELECT TOP (?) pt.part_id, pt.part_name, ISNULL(pt.sale_price, pt.base_price) AS price, p.discount_type, p.discount_value, p.promotion_name "
                   + "FROM promotions p JOIN parts pt ON pt.status = 'ACTIVE' AND (pt.part_id = p.target_part_id OR pt.category_id = p.target_category_id OR pt.brand_id = p.target_brand_id) "
                   + "WHERE p.status = '" + ACTIVE + "' AND " + RUNNING_P + " ORDER BY pt.part_name";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal price = rs.getBigDecimal("price"), d = discountOf(rs.getString("discount_type"), rs.getBigDecimal("discount_value"), price, null);
                    DiscountedPart dp = new DiscountedPart(); dp.partId = rs.getInt("part_id"); dp.partName = rs.getString("part_name");
                    dp.price = price.doubleValue(); dp.salePrice = price.subtract(d).doubleValue(); dp.promotionName = rs.getString("promotion_name");
                    list.add(dp);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public String savePromotion(String name, String targetKind, String targetIdS, String type, String valueS, String minS, String startS, String endS, int adminId) {
        try {
            name = name == null ? "" : name.trim();
            if (name.isEmpty() || name.length() > 150) return "Tên khuyến mãi không được để trống (tối đa 150 ký tự)!";
            if (!PERCENT.equals(type) && !FIXED.equals(type)) return "Loại giảm giá không hợp lệ!";
            BigDecimal value = new BigDecimal(valueS.trim());
            if (value.signum() <= 0) return "Giá trị giảm phải lớn hơn 0!";
            if (PERCENT.equals(type) && value.compareTo(BigDecimal.valueOf(100)) > 0) return "Giảm theo % không được vượt quá 100!";
            BigDecimal min = blank(minS) ? BigDecimal.ZERO : new BigDecimal(minS.trim());
            int target = Integer.parseInt(targetIdS.trim());
            String col, ptype;
            switch (targetKind == null ? "" : targetKind) {
                case "PART": col = "target_part_id"; ptype = PROMO_DISCOUNT; break;
                case "CATEGORY": col = "target_category_id"; ptype = PROMO_DISCOUNT; break;
                case "BRAND": col = "target_brand_id"; ptype = PROMO_DISCOUNT; break;
                default: return "Đối tượng khuyến mãi không hợp lệ!";
            }
            Date start = blank(startS) ? new Date(System.currentTimeMillis()) : Date.valueOf(startS.trim()), end = blank(endS) ? Date.valueOf("2099-12-31") : Date.valueOf(endS.trim());   // DB: start_date/end_date NOT NULL
            if (start != null && end != null && end.before(start)) return "Ngày kết thúc phải sau ngày bắt đầu!";
            String sql = "INSERT INTO promotions (promotion_name, promotion_type, discount_type, discount_value, min_order_value, " + col + ", start_date, end_date, status, created_by, created_at) "
                       + "VALUES (?,?,?,?,?,?,?,?,?,?,SYSDATETIME())";
            try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name); ps.setString(2, ptype); ps.setString(3, type); ps.setBigDecimal(4, value); ps.setBigDecimal(5, min); ps.setInt(6, target);
                if (start == null) ps.setNull(7, Types.DATE); else ps.setDate(7, start);
                if (end == null) ps.setNull(8, Types.DATE); else ps.setDate(8, end);
                ps.setString(9, ACTIVE); ps.setInt(10, adminId);
                ps.executeUpdate();
            }
            return null;
        } catch (java.time.DateTimeException | NullPointerException | IllegalArgumentException e) {
            return "Dữ liệu nhập không hợp lệ!";
        } catch (Exception e) { e.printStackTrace(); return "Không lưu được khuyến mãi (kiểm tra mã linh kiện/danh mục/thương hiệu có tồn tại)!"; }
    }

    public boolean togglePromotion(int id) {
        return exec("UPDATE promotions SET status = CASE WHEN status = '" + ACTIVE + "' THEN '" + INACTIVE + "' ELSE '" + ACTIVE + "' END WHERE promotion_id = ?", id);
    }

    // =====================================================================
    // COMBO / ƯU ĐÃI TRỌN BỘ PC (UC 243-244) - mới quản lý, chưa tự áp dụng vào đơn
    // =====================================================================

    public List<ComboRow> listCombos() {
        List<ComboRow> list = new ArrayList<>();
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM combo_promotions ORDER BY combo_id DESC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ComboRow c = new ComboRow(); c.id = rs.getInt("combo_id"); c.name = rs.getString("combo_name"); c.percent = rs.getDouble("discount_percentage");
                c.minParts = rs.getInt("min_parts_count"); c.type = rs.getString("combo_type"); c.status = rs.getString("status");
                c.startDate = rs.getDate("start_date"); c.endDate = rs.getDate("end_date"); list.add(c);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public String saveCombo(String name, String type, String percentS, String minPartsS, String startS, String endS) {
        try {
            name = name == null ? "" : name.trim();
            if (name.isEmpty() || name.length() > 150) return "Tên combo không được để trống (tối đa 150 ký tự)!";
            if (!COMBO_FULL_PC.equals(type) && !COMBO_PARTS.equals(type)) return "Loại combo không hợp lệ!";
            BigDecimal pct = new BigDecimal(percentS.trim());
            if (pct.signum() <= 0 || pct.compareTo(BigDecimal.valueOf(100)) > 0) return "Phần trăm giảm phải từ 0 đến 100!";
            int minParts = Integer.parseInt(minPartsS.trim());
            if (minParts < 2) return "Combo cần tối thiểu 2 linh kiện!";
            Date start = blank(startS) ? new Date(System.currentTimeMillis()) : Date.valueOf(startS.trim()), end = blank(endS) ? Date.valueOf("2099-12-31") : Date.valueOf(endS.trim());   // DB: start_date/end_date NOT NULL
            if (start != null && end != null && end.before(start)) return "Ngày kết thúc phải sau ngày bắt đầu!";
            try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO combo_promotions (combo_name, discount_percentage, min_parts_count, combo_type, status, start_date, end_date) VALUES (?,?,?,?,?,?,?)")) {
                ps.setString(1, name); ps.setBigDecimal(2, pct); ps.setInt(3, minParts); ps.setString(4, type); ps.setString(5, ACTIVE);
                if (start == null) ps.setNull(6, Types.DATE); else ps.setDate(6, start);
                if (end == null) ps.setNull(7, Types.DATE); else ps.setDate(7, end);
                ps.executeUpdate();
            }
            return null;
        } catch (java.time.DateTimeException | NullPointerException | IllegalArgumentException e) {
            return "Dữ liệu nhập không hợp lệ!";
        } catch (Exception e) { e.printStackTrace(); return "Không lưu được combo!"; }
    }

    public boolean toggleCombo(int id) {
        return exec("UPDATE combo_promotions SET status = CASE WHEN status = '" + ACTIVE + "' THEN '" + INACTIVE + "' ELSE '" + ACTIVE + "' END WHERE combo_id = ?", id);
    }

    // =====================================================================
    // THÔNG BÁO KHUYẾN MÃI HÀNG LOẠT (UC 250)
    // =====================================================================

    /** Gửi thông báo trong ứng dụng + email (nếu có SMTP) cho mọi khách hàng ACTIVE (bỏ qua nhân viên). Trả về số người đã gửi, -1 nếu thiếu nội dung. */
    public int broadcast(String title, String content) {
        if (blank(title) || blank(content)) return -1;
        if (title.length() > 150) title = title.substring(0, 150);
        NotificationService ns = new NotificationService();
        String sql = "SELECT u.user_id, u.email, u.full_name FROM users u WHERE u.status = 'ACTIVE' AND NOT EXISTS "
                   + "(SELECT 1 FROM user_roles ur JOIN roles r ON r.role_id = ur.role_id WHERE ur.user_id = u.user_id AND r.role_code IN ('ADMIN','OWNER','STAFF'))";
        List<String[]> targets = new ArrayList<>();
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                targets.add(new String[]{String.valueOf(rs.getInt(1)), rs.getString(2), rs.getString(3)});
            }
        } catch (Exception e) { e.printStackTrace(); return 0; }
        int sent = 0;
        String cleanTitle = title.trim();
        String cleanContent = content.trim();
        for (String[] t : targets) {
            int uid = Integer.parseInt(t[0]);
            if (ns.notifyPromotion(uid, cleanTitle, cleanContent)) {
                sent++;
                sendPromoEmailAsync(t[1], t[2], cleanTitle, cleanContent);
            }
        }
        return sent;
    }

    private void sendPromoEmailAsync(String toEmail, String fullName, String title, String content) {
        if (toEmail == null || toEmail.trim().isEmpty()) return;
        String safeName = (fullName == null || fullName.trim().isEmpty()) ? "Khách hàng" : fullName.trim();
        String html = "<div style='font-family:Segoe UI,Arial,sans-serif;line-height:1.5'>"
                + "<p>Xin chào <b>" + escapeHtmlPromo(safeName) + "</b>,</p>"
                + "<h3>" + escapeHtmlPromo(title) + "</h3>"
                + "<p>" + escapeHtmlPromo(content) + "</p>"
                + "<p>Trân trọng,<br/>GearZone</p>"
                + "</div>";
        fourSome.util.EmailUtil.sendAsync(toEmail.trim(), "GearZone - " + title, html, true, null);
    }

    private static String escapeHtmlPromo(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private boolean exec(String sql, int id) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}