package minhtbth08199.dao;

import fourSome.model.Parts;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.*;
import minhtbth08199.model.BuildPartItem;

/** Danh mục dùng chung cho 11 slot Build PC; chỉ trả linh kiện còn bán và còn tồn. */
public class BuildPartsDAO {
    public List<Parts> findAvailable(int categoryId, String keyword, int limit) {
        if (categoryId < 1 || categoryId > 11) {
            throw new IllegalArgumentException("Loại linh kiện không hợp lệ");
        }
        List<Parts> result = new ArrayList<>();
        String sql = "SELECT TOP (?) p.* FROM parts p JOIN categories c ON c.category_id=p.category_id "
                + "JOIN inventory i ON i.part_id=p.part_id "
                + "WHERE c.category_id=? AND c.status='ACTIVE' AND p.status='ACTIVE' AND i.status='ACTIVE' "
                + "AND i.stock_quantity-i.reserved_quantity>0 AND (?='' OR p.part_name LIKE ?) "
                + "ORDER BY COALESCE(p.sale_price,p.base_price), p.part_name";
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, Math.min(limit, 5000)));
            ps.setInt(2, categoryId);
            String q = keyword == null ? "" : keyword.trim();
            ps.setString(3, q);
            ps.setString(4, "%" + q + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Không tải được danh sách linh kiện", e);
        }
        return result;
    }

    /** Chỉ trả về linh kiện còn có thể bán; tránh tin partId/giá do trình duyệt gửi lên. */
    public Parts findAvailableById(int partId) {
        String sql = "SELECT p.* FROM parts p JOIN categories c ON c.category_id=p.category_id JOIN inventory i ON i.part_id = p.part_id "
                + "WHERE p.part_id = ? AND c.status='ACTIVE' AND p.status = 'ACTIVE' AND i.status = 'ACTIVE' "
                + "AND i.stock_quantity - i.reserved_quantity > 0";
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể kiểm tra linh kiện", e);
        }
    }

    public boolean isCpuMainboardCompatible(int cpuId, int mainboardId) {
        return exists("SELECT 1 FROM spec_cpu c JOIN spec_mainboard m ON m.socket_id=c.socket_id WHERE c.part_id=? AND m.part_id=?", cpuId, mainboardId);
    }

    public boolean isRamMainboardCompatible(int ramId, int mainboardId) {
        return exists("SELECT 1 FROM spec_ram r JOIN spec_mainboard m ON UPPER(LTRIM(RTRIM(m.ram_standard)))=UPPER(LTRIM(RTRIM(r.ddr_type))) WHERE r.part_id=? AND m.part_id=?", ramId, mainboardId);
    }

    /** True when this CPU and RAM have at least one in-stock motherboard platform in common. */
    public boolean hasCompatibleMainboardFor(int cpuId, int ramId) {
        String boardDdr = "COALESCE(NULLIF(UPPER(LTRIM(RTRIM(m.ram_standard))),''), CASE "
                + "WHEN UPPER(mp.part_name) LIKE '%DDR5%' THEN 'DDR5' WHEN UPPER(mp.part_name) LIKE '%DDR4%' THEN 'DDR4' END)";
        String memoryDdr = "COALESCE(NULLIF(UPPER(LTRIM(RTRIM(r.ddr_type))),''), CASE "
                + "WHEN UPPER(rp.part_name) LIKE '%DDR5%' THEN 'DDR5' WHEN UPPER(rp.part_name) LIKE '%DDR4%' THEN 'DDR4' END)";
        String sql = "SELECT TOP 1 1 FROM spec_cpu cpu "
                + "JOIN parts mp ON mp.category_id=2 AND mp.status='ACTIVE' "
                + "JOIN categories mc ON mc.category_id=mp.category_id AND mc.status='ACTIVE' "
                + "JOIN inventory mi ON mi.part_id=mp.part_id AND mi.status='ACTIVE' AND mi.stock_quantity-mi.reserved_quantity>0 "
                + "LEFT JOIN spec_mainboard m ON m.part_id=mp.part_id "
                + "LEFT JOIN spec_ram r ON r.part_id=? JOIN parts rp ON rp.part_id=? "
                + "WHERE cpu.part_id=? AND (m.part_id IS NULL OR m.socket_id IS NULL OR cpu.socket_id IS NULL OR m.socket_id=cpu.socket_id) "
                + "AND (m.part_id IS NULL OR " + boardDdr + " IS NULL OR " + memoryDdr + " IS NULL OR " + boardDdr + "=" + memoryDdr + ")";
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ramId); ps.setInt(2, ramId); ps.setInt(3, cpuId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể kiểm tra nền tảng CPU/RAM", e);
        }
    }

    public boolean isPsuEnough(int psuId, int cpuId, int vgaId) {
        String sql = "SELECT p.wattage, COALESCE(c.tdp_watts,0)+COALESCE(v.tgp_watts,0) AS system_watts "
                + "FROM spec_psu p LEFT JOIN spec_cpu c ON c.part_id=? LEFT JOIN spec_vga v ON v.part_id=? WHERE p.part_id=?";
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, cpuId); ps.setInt(2, vgaId); ps.setInt(3, psuId);
            try (ResultSet rs = ps.executeQuery()) { return !rs.next() || rs.getInt("wattage") >= Math.ceil(rs.getInt("system_watts") * 1.2); }
        } catch (SQLException e) { throw new IllegalStateException("Không thể kiểm tra công suất PSU", e); }
    }

    public boolean hasCompatibilitySpecs(int categoryId, int partId) {
        String table = switch (categoryId) {
            case 1 -> "spec_cpu"; case 2 -> "spec_mainboard"; case 3 -> "spec_ram";
            case 4 -> "spec_vga"; case 7 -> "spec_psu"; case 8 -> "spec_case";
            case 9 -> "spec_cooler"; default -> null;
        };
        if (table == null) return true;
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT 1 FROM " + table + " WHERE part_id=?")) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new IllegalStateException("Không thể đọc thông số tương thích", e); }
    }

    private boolean exists(String sql, int firstId, int secondId) {
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, firstId); ps.setInt(2, secondId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new IllegalStateException("Không thể kiểm tra tương thích linh kiện", e); }
    }

    public List<BuildPartItem> findSelectedByBuildId(int buildId) {
        String sql = "SELECT d.part_id, p.category_id, p.part_name, d.quantity, d.unit_price "
                + "FROM pc_build_details d JOIN parts p ON p.part_id = d.part_id "
                + "WHERE d.build_id = ? AND d.status = 'ACTIVE' ORDER BY p.category_id, p.part_name";
        List<BuildPartItem> result = new ArrayList<>();
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, buildId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BuildPartItem item = new BuildPartItem();
                    item.setPartId(rs.getInt("part_id")); item.setCategoryId(rs.getInt("category_id"));
                    item.setPartName(rs.getString("part_name")); item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price")); result.add(item);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể đọc linh kiện đã chọn", e);
        }
        return result;
    }

    private Parts map(ResultSet r) throws SQLException {
        Parts p = new Parts();
        p.setPartId(r.getInt("part_id"));
        p.setCategoryId(r.getInt("category_id"));
        p.setPartName(r.getString("part_name"));
        p.setBasePrice(r.getDouble("base_price"));
        double sale = r.getDouble("sale_price");
        p.setSalePrice(r.wasNull() ? null : sale);
        p.setIsRgb(r.getBoolean("is_rgb"));
        p.setGamingPerformanceScore(r.getInt("gaming_performance_score"));
        p.setStatus(r.getString("status"));
        return p;
    }
}
