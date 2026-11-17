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
            ps.setInt(1, Math.max(1, Math.min(limit, 100)));
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
