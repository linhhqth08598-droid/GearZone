package linhhqth08598.service;

import fourSome.url.DBConnect;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Thống kê & báo cáo (Module I + J).
 * - Các hàm getTotalRevenue()... dùng cho KPI/biểu đồ ở dashboard.
 * - REPORTS: danh mục 24 báo cáo dùng chung cho trang /admin/report và xuất Excel /admin/export.
 *
 * Quy ước: doanh thu / lợi nhuận chỉ tính đơn DELIVERED; số lượng bán tính đơn chưa bị hủy.
 * Báo cáo "dated" luôn có đúng 2 dấu ? (từ ngày, đến ngày) trong câu SQL.
 */
public class ReportService {

    // ===================== Định nghĩa báo cáo =====================

    public static class Report {
        private final String key;
        private final String group;
        private final String title;
        private final String note;
        private final String[] headers;
        private final boolean dated;
        private final String sql;

        Report(String key, String group, String title, String note, boolean dated, String[] headers, String sql) {
            this.key = key;
            this.group = group;
            this.title = title;
            this.note = note;
            this.dated = dated;
            this.headers = headers;
            this.sql = sql;
        }

        public String getKey() { return key; }
        public String getGroup() { return group; }
        public String getTitle() { return title; }
        public String getNote() { return note; }
        public boolean isDated() { return dated; }
        public String[] getHeaders() { return headers; }
    }

    public static class ReportResult {
        private final String[] headers;
        private final List<Object[]> rows = new ArrayList<>();
        private final List<String[]> display = new ArrayList<>();
        private boolean[] numeric;

        ReportResult(String[] headers) {
            this.headers = headers;
            this.numeric = new boolean[headers.length];
        }

        public String[] getHeaders() { return headers; }
        public List<Object[]> getRows() { return rows; }
        public List<String[]> getDisplay() { return display; }
        public boolean[] getNumeric() { return numeric; }
    }

    private static final String DONE = "o.order_status = 'DELIVERED'";
    private static final String NOT_CANCELLED = "o.order_status <> 'CANCELLED'";
    private static final String G_STOCK = "Linh kiện & kho";
    private static final String G_REVENUE = "Doanh thu & lợi nhuận";
    private static final String G_BUILD = "Cấu hình PC";
    private static final String G_ORDER = "Đơn hàng & bảo hành";
    private static final String G_SERVICE = "Khuyến mãi, tư vấn, lắp ráp";

    private static final Map<String, Report> REPORTS = new LinkedHashMap<>();

    private static void add(String key, String group, String title, String note, boolean dated, String[] headers, String sql) {
        REPORTS.put(key, new Report(key, group, title, note, dated, headers, sql));
    }

    static {
        // ---------- Linh kiện & kho ----------
        add("brand_parts", G_STOCK, "Linh kiện theo thương hiệu",
            "Số linh kiện đang bán và tổng tồn kho của từng thương hiệu.", false,
            new String[]{"Thương hiệu", "Số linh kiện", "Tổng tồn kho"},
            "SELECT b.brand_name, COUNT(DISTINCT p.part_id), ISNULL(SUM(i.stock_quantity), 0) "
          + "FROM brands b LEFT JOIN parts p ON p.brand_id = b.brand_id AND p.status = 'ACTIVE' "
          + "LEFT JOIN inventory i ON i.part_id = p.part_id "
          + "GROUP BY b.brand_name ORDER BY 2 DESC, 1");

        add("in_stock", G_STOCK, "Linh kiện còn hàng",
            "Khả dụng = tồn kho - đã giữ chỗ (lớn hơn 0).", false,
            new String[]{"Linh kiện", "Tồn kho", "Đã giữ", "Khả dụng"},
            "SELECT p.part_name, i.stock_quantity, i.reserved_quantity, i.stock_quantity - i.reserved_quantity "
          + "FROM inventory i JOIN parts p ON p.part_id = i.part_id "
          + "WHERE i.stock_quantity - i.reserved_quantity > 0 ORDER BY 4 DESC, 1");

        add("out_of_stock", G_STOCK, "Linh kiện hết hàng",
            "Khả dụng nhỏ hơn hoặc bằng 0.", false,
            new String[]{"Linh kiện", "Tồn kho", "Đã giữ", "Ngưỡng cảnh báo"},
            "SELECT p.part_name, i.stock_quantity, i.reserved_quantity, i.low_stock_threshold "
          + "FROM inventory i JOIN parts p ON p.part_id = i.part_id "
          + "WHERE i.stock_quantity - i.reserved_quantity <= 0 ORDER BY 1");

        add("best_seller", G_STOCK, "Linh kiện bán chạy (Top 20)",
            "Tính các đơn chưa bị hủy trong khoảng ngày đã chọn.", true,
            new String[]{"Linh kiện", "Số lượng bán"},
            "SELECT TOP 20 p.part_name, SUM(oi.quantity) FROM order_items oi "
          + "JOIN orders o ON o.order_id = oi.order_id JOIN parts p ON p.part_id = oi.part_id "
          + "WHERE " + NOT_CANCELLED + " AND o.created_at >= ? AND o.created_at < ? "
          + "GROUP BY p.part_name ORDER BY 2 DESC, 1");

        add("slow_seller", G_STOCK, "Linh kiện bán chậm (Top 20)",
            "Linh kiện đang bán nhưng có ít đơn nhất trong khoảng ngày đã chọn.", true,
            new String[]{"Linh kiện", "Số lượng bán"},
            "SELECT TOP 20 p.part_name, ISNULL(SUM(CASE WHEN o.order_id IS NOT NULL THEN oi.quantity END), 0) "
          + "FROM parts p LEFT JOIN order_items oi ON oi.part_id = p.part_id "
          + "LEFT JOIN orders o ON o.order_id = oi.order_id AND " + NOT_CANCELLED + " AND o.created_at >= ? AND o.created_at < ? "
          + "WHERE p.status = 'ACTIVE' GROUP BY p.part_name ORDER BY 2 ASC, 1");

        add("inventory_all", G_STOCK, "Báo cáo tồn kho (toàn bộ)",
            "Dùng để xuất Excel báo cáo tồn kho.", false,
            new String[]{"Linh kiện", "Tồn kho", "Đã giữ", "Hỏng", "Khả dụng"},
            "SELECT p.part_name, i.stock_quantity, i.reserved_quantity, i.damaged_quantity, "
          + "i.stock_quantity - i.reserved_quantity FROM inventory i JOIN parts p ON p.part_id = i.part_id ORDER BY 1");

        // ---------- Doanh thu & lợi nhuận ----------
        add("rev_category", G_REVENUE, "Doanh thu theo danh mục",
            "Chỉ tính đơn đã giao (DELIVERED).", true,
            new String[]{"Danh mục", "Số lượng", "Doanh thu"},
            "SELECT c.category_name, SUM(oi.quantity), SUM(oi.unit_price * oi.quantity) FROM order_items oi "
          + "JOIN orders o ON o.order_id = oi.order_id JOIN parts p ON p.part_id = oi.part_id "
          + "JOIN categories c ON c.category_id = p.category_id "
          + "WHERE " + DONE + " AND o.created_at >= ? AND o.created_at < ? GROUP BY c.category_name ORDER BY 3 DESC");

        add("rev_brand", G_REVENUE, "Doanh thu theo thương hiệu",
            "Chỉ tính đơn đã giao (DELIVERED).", true,
            new String[]{"Thương hiệu", "Số lượng", "Doanh thu"},
            "SELECT b.brand_name, SUM(oi.quantity), SUM(oi.unit_price * oi.quantity) FROM order_items oi "
          + "JOIN orders o ON o.order_id = oi.order_id JOIN parts p ON p.part_id = oi.part_id "
          + "JOIN brands b ON b.brand_id = p.brand_id "
          + "WHERE " + DONE + " AND o.created_at >= ? AND o.created_at < ? GROUP BY b.brand_name ORDER BY 3 DESC");

        add("rev_time", G_REVENUE, "Doanh thu theo thời gian (theo tháng)",
            "Chỉ tính đơn đã giao. Dùng để xuất báo cáo kinh doanh theo thời gian.", true,
            new String[]{"Tháng", "Số đơn", "Doanh thu"},
            "SELECT CONVERT(varchar(7), o.created_at, 120), COUNT(*), SUM(o.final_amount) FROM orders o "
          + "WHERE " + DONE + " AND o.created_at >= ? AND o.created_at < ? "
          + "GROUP BY CONVERT(varchar(7), o.created_at, 120) ORDER BY 1");

        add("profit_part", G_REVENUE, "Lợi nhuận theo linh kiện",
            "Lợi nhuận = (giá bán - giá vốn) x số lượng. Nếu order_items.cost_price để trống thì coi giá vốn = 0.", true,
            new String[]{"Linh kiện", "Số lượng", "Doanh thu", "Lợi nhuận"},
            "SELECT TOP 50 p.part_name, SUM(oi.quantity), SUM(oi.unit_price * oi.quantity), "
          + "SUM((oi.unit_price - ISNULL(oi.cost_price, 0)) * oi.quantity) FROM order_items oi "
          + "JOIN orders o ON o.order_id = oi.order_id JOIN parts p ON p.part_id = oi.part_id "
          + "WHERE " + DONE + " AND o.created_at >= ? AND o.created_at < ? GROUP BY p.part_name ORDER BY 4 DESC");

        add("revenue_orders", G_REVENUE, "Báo cáo doanh thu (danh sách đơn)",
            "Dùng để xuất Excel báo cáo doanh thu, tính các đơn đã giao.", true,
            new String[]{"Mã đơn", "Ngày đặt", "Tổng tiền", "Giảm giá", "Thành tiền"},
            "SELECT o.order_code, CONVERT(varchar(16), o.created_at, 120), o.total_amount, ISNULL(o.discount_amount, 0), o.final_amount "
          + "FROM orders o WHERE " + DONE + " AND o.created_at >= ? AND o.created_at < ? ORDER BY o.created_at DESC");

        // ---------- Cấu hình PC ----------
        add("build_count", G_BUILD, "Số lượng cấu hình PC theo tháng",
            "Tổng số cấu hình được tạo và số cấu hình tương thích.", true,
            new String[]{"Tháng", "Số cấu hình", "Tương thích"},
            "SELECT CONVERT(varchar(7), b.created_at, 120), COUNT(*), SUM(CASE WHEN b.is_compatible = 1 THEN 1 ELSE 0 END) "
          + "FROM pc_builds b WHERE b.created_at >= ? AND b.created_at < ? "
          + "GROUP BY CONVERT(varchar(7), b.created_at, 120) ORDER BY 1");

        add("build_popular", G_BUILD, "Cấu hình được chọn nhiều",
            "Số lần một cấu hình mẫu được sao chép / chọn lại.", true,
            new String[]{"Cấu hình", "Số lần chọn"},
            "SELECT TOP 20 s.build_name, COUNT(*) FROM pc_builds c JOIN pc_builds s ON s.build_id = c.copied_from_build_id "
          + "WHERE c.created_at >= ? AND c.created_at < ? GROUP BY s.build_name ORDER BY 2 DESC");

        add("build_parts", G_BUILD, "Linh kiện hay được chọn trong cấu hình",
            "Số cấu hình có chứa linh kiện đó.", true,
            new String[]{"Linh kiện", "Số cấu hình"},
            "SELECT TOP 20 p.part_name, COUNT(DISTINCT d.build_id) FROM pc_build_details d "
          + "JOIN pc_builds b ON b.build_id = d.build_id JOIN parts p ON p.part_id = d.part_id "
          + "WHERE b.created_at >= ? AND b.created_at < ? GROUP BY p.part_name ORDER BY 2 DESC");

        add("build_budget", G_BUILD, "Cấu hình theo ngân sách",
            "Chỉ tính cấu hình có nhập ngân sách dự kiến.", true,
            new String[]{"Mức ngân sách", "Số cấu hình", "Giá trung bình"},
            "SELECT bucket, COUNT(*), AVG(total_price) FROM ("
          + "SELECT CASE WHEN estimated_budget < 10000000 THEN 1 WHEN estimated_budget < 20000000 THEN 2 "
          + "WHEN estimated_budget < 30000000 THEN 3 WHEN estimated_budget < 50000000 THEN 4 ELSE 5 END AS ord, "
          + "CASE WHEN estimated_budget < 10000000 THEN N'Dưới 10 triệu' WHEN estimated_budget < 20000000 THEN N'10 - 20 triệu' "
          + "WHEN estimated_budget < 30000000 THEN N'20 - 30 triệu' WHEN estimated_budget < 50000000 THEN N'30 - 50 triệu' "
          + "ELSE N'Từ 50 triệu' END AS bucket, total_price FROM pc_builds "
          + "WHERE estimated_budget IS NOT NULL AND created_at >= ? AND created_at < ?) t GROUP BY ord, bucket ORDER BY ord");

        add("part_replaced", G_BUILD, "Linh kiện hay được đề xuất thay thế",
            "Theo bảng gợi ý nâng cấp (upgrade_recommendations).", false,
            new String[]{"Linh kiện bị thay", "Số lần đề xuất thay"},
            "SELECT TOP 20 p.part_name, COUNT(*) FROM upgrade_recommendations u JOIN parts p ON p.part_id = u.replace_part_id "
          + "WHERE u.replace_part_id IS NOT NULL GROUP BY p.part_name ORDER BY 2 DESC");

        add("conflicts", G_BUILD, "Số lần không tương thích",
            "Theo nhật ký xung đột tương thích, nhóm theo quy tắc.", true,
            new String[]{"Quy tắc", "Số lần xung đột"},
            "SELECT ISNULL(r.rule_name, N'Quy tắc khác'), COUNT(*) FROM compatibility_conflict_logs l "
          + "LEFT JOIN compatibility_rules r ON r.rule_id = l.rule_id "
          + "WHERE l.created_at >= ? AND l.created_at < ? GROUP BY r.rule_name ORDER BY 2 DESC");

        add("over_budget", G_BUILD, "Tỷ lệ cấu hình vượt ngân sách",
            "Cấu hình có tổng giá lớn hơn ngân sách dự kiến.", true,
            new String[]{"Cấu hình có ngân sách", "Vượt ngân sách", "Tỷ lệ vượt (%)"},
            "SELECT COUNT(*), ISNULL(SUM(CASE WHEN total_price > estimated_budget THEN 1 ELSE 0 END), 0), "
          + "CAST(ISNULL(100.0 * SUM(CASE WHEN total_price > estimated_budget THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 0) AS decimal(5,2)) "
          + "FROM pc_builds WHERE estimated_budget IS NOT NULL AND created_at >= ? AND created_at < ?");

        // ---------- Đơn hàng & bảo hành ----------
        add("orders_status", G_ORDER, "Đơn hàng theo trạng thái",
            "Tổng quan toàn bộ đơn trong khoảng ngày.", true,
            new String[]{"Trạng thái", "Số đơn", "Tổng tiền"},
            "SELECT o.order_status, COUNT(*), SUM(o.final_amount) FROM orders o "
          + "WHERE o.created_at >= ? AND o.created_at < ? GROUP BY o.order_status ORDER BY 2 DESC");

        add("orders_success", G_ORDER, "Đơn hàng thành công theo tháng",
            "Đơn đã giao (DELIVERED).", true,
            new String[]{"Tháng", "Số đơn thành công", "Giá trị"},
            "SELECT CONVERT(varchar(7), o.created_at, 120), COUNT(*), SUM(o.final_amount) FROM orders o "
          + "WHERE " + DONE + " AND o.created_at >= ? AND o.created_at < ? "
          + "GROUP BY CONVERT(varchar(7), o.created_at, 120) ORDER BY 1");

        add("orders_cancelled", G_ORDER, "Đơn hàng bị hủy theo lý do",
            "Đơn có trạng thái CANCELLED.", true,
            new String[]{"Lý do hủy", "Số đơn", "Giá trị"},
            "SELECT ISNULL(o.cancel_reason, N'(Không ghi lý do)'), COUNT(*), SUM(o.final_amount) FROM orders o "
          + "WHERE o.order_status = 'CANCELLED' AND o.created_at >= ? AND o.created_at < ? "
          + "GROUP BY ISNULL(o.cancel_reason, N'(Không ghi lý do)') ORDER BY 2 DESC");

        add("warranty_count", G_ORDER, "Số yêu cầu bảo hành",
            "Theo trạng thái xử lý.", true,
            new String[]{"Trạng thái", "Số yêu cầu"},
            "SELECT c.status, COUNT(*) FROM warranty_claims c WHERE c.created_at >= ? AND c.created_at < ? "
          + "GROUP BY c.status ORDER BY 2 DESC");

        add("warranty_rate", G_ORDER, "Tỷ lệ bảo hành theo linh kiện",
            "Tỷ lệ = số yêu cầu bảo hành / số phiếu bảo hành điện tử đã cấp.", false,
            new String[]{"Linh kiện", "Phiếu bảo hành", "Yêu cầu bảo hành", "Tỷ lệ (%)"},
            "SELECT TOP 20 p.part_name, COUNT(DISTINCT w.warranty_id), COUNT(c.claim_id), "
          + "CAST(100.0 * COUNT(c.claim_id) / NULLIF(COUNT(DISTINCT w.warranty_id), 0) AS decimal(6,2)) "
          + "FROM e_warranties w JOIN parts p ON p.part_id = w.part_id "
          + "LEFT JOIN warranty_claims c ON c.warranty_id = w.warranty_id GROUP BY p.part_name ORDER BY 4 DESC, 1");

        // ---------- Khuyến mãi, tư vấn, lắp ráp ----------
        add("promo_effect", G_SERVICE, "Hiệu quả khuyến mãi",
            "So sánh số đơn, tiền giảm và doanh thu theo từng chương trình (đơn chưa bị hủy).", true,
            new String[]{"Chương trình", "Số đơn", "Tiền giảm", "Doanh thu"},
            "SELECT ISNULL(pr.promotion_name, N'Không áp dụng khuyến mãi'), COUNT(*), ISNULL(SUM(o.discount_amount), 0), SUM(o.final_amount) "
          + "FROM orders o LEFT JOIN promotions pr ON pr.promotion_id = o.promotion_id "
          + "WHERE " + NOT_CANCELLED + " AND o.created_at >= ? AND o.created_at < ? GROUP BY pr.promotion_name ORDER BY 2 DESC");

        add("consult_effect", G_SERVICE, "Hiệu quả tư vấn",
            "Tỷ lệ phiếu hỗ trợ đã xử lý (RESOLVED / CLOSED) theo loại.", true,
            new String[]{"Loại phiếu", "Tổng phiếu", "Đã xử lý", "Tỷ lệ (%)"},
            "SELECT t.ticket_type, COUNT(*), SUM(CASE WHEN t.status IN ('RESOLVED', 'CLOSED') THEN 1 ELSE 0 END), "
          + "CAST(100.0 * SUM(CASE WHEN t.status IN ('RESOLVED', 'CLOSED') THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0) AS decimal(5,2)) "
          + "FROM support_tickets t WHERE t.created_at >= ? AND t.created_at < ? GROUP BY t.ticket_type ORDER BY 2 DESC");

        add("assembly_effect", G_SERVICE, "Hiệu quả dịch vụ lắp ráp",
            "Số đơn, điểm đánh giá trung bình và phí lắp ráp theo trạng thái.", true,
            new String[]{"Trạng thái", "Số đơn", "Điểm TB", "Phí lắp ráp"},
            "SELECT a.status, COUNT(*), CAST(AVG(CAST(a.customer_rating AS decimal(5,2))) AS decimal(4,2)), ISNULL(SUM(a.assembly_fee), 0) "
          + "FROM assembly_orders a WHERE a.created_at >= ? AND a.created_at < ? GROUP BY a.status ORDER BY 2 DESC");
    }

    public static Map<String, Report> getReports() { return REPORTS; }

    public static Report getReport(String key) { return key == null ? null : REPORTS.get(key); }

    /** Chạy 1 báo cáo. from/to có thể null (không giới hạn). to được tính gồm cả ngày đó. */
    public ReportResult run(String key, LocalDate from, LocalDate to) throws Exception {
        Report r = REPORTS.get(key);
        if (r == null) throw new IllegalArgumentException("Báo cáo không tồn tại");

        ReportResult result = new ReportResult(r.headers);
        DecimalFormat nf = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(new Locale("vi", "VN")));

        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) throw new IllegalStateException("Không kết nối được cơ sở dữ liệu");
            try (PreparedStatement ps = conn.prepareStatement(r.sql)) {
                if (r.dated) {
                    LocalDate f = from != null ? from : LocalDate.of(2000, 1, 1);
                    LocalDate t = (to != null ? to : LocalDate.of(2100, 1, 1)).plusDays(1);
                    ps.setTimestamp(1, Timestamp.valueOf(f.atStartOfDay()));
                    ps.setTimestamp(2, Timestamp.valueOf(t.atStartOfDay()));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    int cols = Math.min(rs.getMetaData().getColumnCount(), r.headers.length);
                    while (rs.next()) {
                        Object[] raw = new Object[cols];
                        String[] disp = new String[cols];
                        for (int i = 0; i < cols; i++) {
                            Object v = rs.getObject(i + 1);
                            if (v instanceof Number) {
                                result.numeric[i] = true;
                                raw[i] = (v instanceof BigDecimal) ? ((BigDecimal) v).doubleValue() : v;
                                disp[i] = nf.format(v);
                            } else {
                                raw[i] = v == null ? "" : v.toString();
                                disp[i] = v == null ? "" : v.toString();
                            }
                        }
                        result.rows.add(raw);
                        result.display.add(disp);
                    }
                }
            }
        }
        return result;
    }

    // ===================== KPI / biểu đồ cho dashboard =====================

    public double getTotalRevenue() {
        double total = 0;
        String sql = "SELECT SUM(final_amount) FROM orders WHERE order_status != 'CANCELLED'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    public int getTotalOrders() {
        int total = 0;
        String sql = "SELECT COUNT(order_id) FROM orders";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    public int getTotalPartsSold() {
        int total = 0;
        String sql = "SELECT SUM(oi.quantity) FROM order_items oi JOIN orders o ON oi.order_id = o.order_id WHERE o.order_status != 'CANCELLED'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return total;
    }

    // Doanh thu theo từng danh mục (biểu đồ tròn). LinkedHashMap giữ thứ tự giảm dần.
    public Map<String, Double> getRevenueByCategory() {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = "SELECT c.category_name, SUM(oi.unit_price * oi.quantity) AS revenue "
                   + "FROM order_items oi JOIN parts p ON oi.part_id = p.part_id "
                   + "JOIN categories c ON p.category_id = c.category_id "
                   + "JOIN orders o ON oi.order_id = o.order_id "
                   + "WHERE o.order_status != 'CANCELLED' GROUP BY c.category_name ORDER BY revenue DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("category_name"), rs.getDouble("revenue"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    // Top 5 linh kiện bán chạy (đã sắp xếp giảm dần, trước đây dùng HashMap nên mất thứ tự)
    public Map<String, Integer> getTopSellingParts() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT TOP 5 p.part_name, SUM(oi.quantity) AS qty "
                   + "FROM order_items oi JOIN parts p ON oi.part_id = p.part_id "
                   + "JOIN orders o ON oi.order_id = o.order_id "
                   + "WHERE o.order_status != 'CANCELLED' GROUP BY p.part_name ORDER BY qty DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("part_name"), rs.getInt("qty"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }
}
