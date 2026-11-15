package minhtbth08199.service;

import fourSome.url.DBConnect;
import fourSome.model.CompatibilityResult;
import khanhnb08894.service.CompatibilityService;
import minhtbth08199.dao.PcBuildsDAO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Build PC (UC 71-85, 94) - bản gọn, đủ để Lưu/Chia sẻ cấu hình (UC 16-17 của Linh) chạy end-to-end.
 * - Mỗi loại linh kiện (categories) chọn 1 món; RAM/ổ cứng... cho phép số lượng 1-8.
 * - Giá luôn lấy từ DB (sale_price nếu có, không thì base_price); tổng tiền tính lại ở server.
 * - Tương thích: gọi CompatibilityService.checkBuild của Khánh (CPU-Mainboard, RAM, nguồn) và cập nhật pc_builds.is_compatible.
 */
public class BuildPCService {

    private final PcBuildsDAO buildsDAO = new PcBuildsDAO();
    private final CompatibilityService compatibilityService = new CompatibilityService();

    // ---------- DTO hiển thị ----------
    public static class Slot {
        private final int categoryId; private final String categoryName; private final boolean required;
        public Slot(int id, String name, boolean required) { this.categoryId = id; this.categoryName = name; this.required = required; }
        public int getCategoryId() { return categoryId; }
        public String getCategoryName() { return categoryName; }
        public boolean isRequired() { return required; }
    }

    public static class Line {
        private int detailId, partId, categoryId, quantity, available;
        private String partName, categoryName, categoryCode;
        private double unitPrice;
        public int getDetailId() { return detailId; }
        public int getPartId() { return partId; }
        public int getCategoryId() { return categoryId; }
        public int getQuantity() { return quantity; }
        public String getPartName() { return partName; }
        public String getCategoryName() { return categoryName; }
        public String getCategoryCode() { return categoryCode; }
        public int getAvailable() { return available; }
        public boolean isShortage() { return available < quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getLineTotal() { return unitPrice * quantity; }
    }

    public static class PartOption {
        private int partId, available;
        private String partName, brandName;
        private double price;
        public int getPartId() { return partId; }
        public int getAvailable() { return available; }
        public String getPartName() { return partName; }
        public String getBrandName() { return brandName; }
        public double getPrice() { return price; }
    }

    // ---------- Nghiệp vụ ----------

    /** UC 71: lấy cấu hình đang làm dở, chưa có thì tạo mới. Trả về build_id. */
    public int getOrCreateDraft(int userId) {
        int id = buildsDAO.findLatestDraft(userId);
        return id > 0 ? id : buildsDAO.createDraft(userId, "Cấu hình mới");
    }

    public int newDraft(int userId) { return buildsDAO.createDraft(userId, "Cấu hình mới"); }

    public boolean canEdit(int buildId, int userId) { return buildsDAO.isEditableOwner(buildId, userId); }

    public String getBuildName(int buildId) { return buildsDAO.getName(buildId); }

    public List<Slot> getSlots() {
        List<Slot> list = new ArrayList<>();
        String sql = "SELECT category_id, category_name, is_required_in_build FROM categories WHERE status = 'ACTIVE' ORDER BY is_required_in_build DESC, category_id";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Slot(rs.getInt(1), rs.getString(2), rs.getBoolean(3)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Line> getLines(int buildId) {
        List<Line> list = new ArrayList<>();
        String sql = "SELECT bd.build_detail_id, bd.part_id, bd.quantity, bd.unit_price, p.part_name, c.category_id, c.category_name, c.category_code, "
                   + "ISNULL(i.stock_quantity - i.reserved_quantity, 0) AS available "
                   + "FROM pc_build_details bd JOIN parts p ON p.part_id = bd.part_id JOIN categories c ON c.category_id = p.category_id "
                   + "LEFT JOIN inventory i ON i.part_id = bd.part_id "
                   + "WHERE bd.build_id = ? AND bd.status = 'ACTIVE' ORDER BY c.category_id";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buildId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Line l = new Line();
                    l.detailId = rs.getInt(1); l.partId = rs.getInt(2); l.quantity = rs.getInt(3);
                    l.unitPrice = rs.getDouble(4); l.partName = rs.getString(5);
                    l.categoryId = rs.getInt(6); l.categoryName = rs.getString(7); l.categoryCode = rs.getString(8); l.available = rs.getInt(9);
                    list.add(l);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public double getTotal(int buildId) {
        String sql = "SELECT total_price FROM pc_builds WHERE build_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buildId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    /** UC 72-82: danh sách linh kiện của 1 loại (có tìm kiếm theo tên), tối đa 30 món. */
    public List<PartOption> searchParts(int categoryId, String keyword) {
        List<PartOption> list = new ArrayList<>();
        boolean hasKey = keyword != null && !keyword.trim().isEmpty();
        String sql = "SELECT TOP 30 p.part_id, p.part_name, b.brand_name, ISNULL(p.sale_price, p.base_price) AS price, "
                   + "ISNULL(i.stock_quantity - i.reserved_quantity, 0) AS available "
                   + "FROM parts p JOIN brands b ON b.brand_id = p.brand_id LEFT JOIN inventory i ON i.part_id = p.part_id "
                   + "WHERE p.status = 'ACTIVE' AND p.category_id = ? " + (hasKey ? "AND p.part_name LIKE ? " : "")
                   + "ORDER BY price";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            if (hasKey) ps.setString(2, "%" + keyword.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PartOption o = new PartOption();
                    o.partId = rs.getInt(1); o.partName = rs.getString(2); o.brandName = rs.getString(3);
                    o.price = rs.getDouble(4); o.available = rs.getInt(5);
                    list.add(o);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** UC 72-82 + 83 + 85: chọn/đổi linh kiện của một loại. Trả về null nếu OK, ngược lại là thông báo lỗi. */
    public String setPart(int buildId, int userId, int partId, int quantity) {
        if (!canEdit(buildId, userId)) return "Không tìm thấy cấu hình hoặc bạn không có quyền sửa!";
        if (quantity < 1 || quantity > 8) return "Số lượng phải từ 1 đến 8!";

        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                int categoryId; BigDecimal price;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT category_id, ISNULL(sale_price, base_price) FROM parts WHERE part_id = ? AND status = 'ACTIVE'")) {
                    ps.setInt(1, partId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { con.rollback(); return "Linh kiện không tồn tại hoặc đã ngừng bán!"; }
                        categoryId = rs.getInt(1); price = rs.getBigDecimal(2);
                    }
                }
                // mỗi loại chỉ giữ 1 món: xóa món cùng loại đang chọn
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM pc_build_details WHERE build_id = ? AND part_id IN (SELECT part_id FROM parts WHERE category_id = ?)")) {
                    ps.setInt(1, buildId); ps.setInt(2, categoryId); ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO pc_build_details (build_id, part_id, quantity, unit_price, status) VALUES (?, ?, ?, ?, 'ACTIVE')")) {
                    ps.setInt(1, buildId); ps.setInt(2, partId); ps.setInt(3, quantity); ps.setBigDecimal(4, price);
                    ps.executeUpdate();
                }
                recalcTotal(con, buildId);
                con.commit();
                refreshCompatibility(buildId);
                return null;
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) {
            e.printStackTrace();
            return "Không chọn được linh kiện, vui lòng thử lại!";
        }
    }

    /** UC 84 + 85: xóa một dòng linh kiện khỏi cấu hình. */
    public String removeLine(int buildId, int userId, int detailId) {
        if (!canEdit(buildId, userId)) return "Không tìm thấy cấu hình hoặc bạn không có quyền sửa!";
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM pc_build_details WHERE build_detail_id = ? AND build_id = ?")) {
                    ps.setInt(1, detailId); ps.setInt(2, buildId); ps.executeUpdate();
                }
                recalcTotal(con, buildId);
                con.commit();
                refreshCompatibility(buildId);
                return null;
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) {
            e.printStackTrace();
            return "Không xóa được linh kiện!";
        }
    }

    /** UC 85: tính lại tổng giá từ chi tiết (server tự tính, không tin dữ liệu từ trình duyệt). */
    private void recalcTotal(Connection con, int buildId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE pc_builds SET total_price = (SELECT ISNULL(SUM(unit_price * quantity), 0) FROM pc_build_details WHERE build_id = ? AND status = 'ACTIVE') WHERE build_id = ?")) {
            ps.setInt(1, buildId); ps.setInt(2, buildId); ps.executeUpdate();
        }
    }

    /** UC 87-88: các loại bắt buộc (is_required_in_build) còn thiếu. */
    public List<String> getMissingRequired(List<Slot> slots, List<Line> lines) {
        List<String> missing = new ArrayList<>();
        for (Slot s : slots) {
            if (!s.isRequired()) continue;
            boolean has = false;
            for (Line l : lines) if (l.getCategoryId() == s.getCategoryId()) { has = true; break; }
            if (!has) missing.add(s.getCategoryName());
        }
        return missing;
    }

    // ---------- UC 86, 92: tương thích (dùng CompatibilityService của Khánh) ----------

    private static String slotKey(Line l) {
        String t = ((l.categoryCode == null ? "" : l.categoryCode) + " " + (l.categoryName == null ? "" : l.categoryName)).toUpperCase();
        if (t.contains("COOL") || t.contains("TẢN")) return null;
        if (t.contains("PSU") || t.contains("NGUỒN") || t.contains("POWER")) return "psu";
        if (t.contains("VGA") || t.contains("GPU") || t.contains("ĐỒ HỌA")) return "vga";
        if (t.contains("MAIN") || t.contains("MẠCH CHỦ")) return "main";
        if (t.contains("RAM")) return "ram";
        if (t.contains("CPU") || t.contains("VI XỬ LÝ")) return "cpu";
        return null;
    }

    public CompatibilityResult checkCompatibility(List<Line> lines) {
        Map<String, Integer> parts = new HashMap<>();
        for (Line l : lines) {
            String k = slotKey(l);
            if (k != null) parts.put(k, l.getPartId());
        }
        return compatibilityService.checkBuild(parts);
    }

    /** Cập nhật pc_builds.is_compatible theo kết quả kiểm tra (lỗi = không tương thích). */
    private void refreshCompatibility(int buildId) {
        try {
            boolean ok = checkCompatibility(getLines(buildId)).getErrors().isEmpty();
            try (Connection con = DBConnect.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE pc_builds SET is_compatible = ? WHERE build_id = ?")) {
                ps.setBoolean(1, ok); ps.setInt(2, buildId); ps.executeUpdate();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
