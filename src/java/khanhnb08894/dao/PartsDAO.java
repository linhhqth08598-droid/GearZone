package khanhnb08894.dao;

import fourSome.model.Parts;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartsDAO {

    public List<Parts> getAll() {
        List<Parts> list = new ArrayList<>();
        String sql = "SELECT * FROM [parts] ORDER BY part_id DESC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Parts obj = new Parts();
                obj.setPartId(rs.getInt("part_id"));
                obj.setCategoryId(rs.getInt("category_id"));
                obj.setBrandId(rs.getInt("brand_id"));
                obj.setSeriesId(rs.getInt("series_id"));
                obj.setProductCode(rs.getString("product_code"));
                obj.setPartName(rs.getString("part_name"));
                obj.setBasePrice(rs.getDouble("base_price"));
                obj.setSalePrice(rs.getDouble("sale_price"));
                obj.setIsRgb(rs.getBoolean("is_rgb"));
                obj.setGamingPerformanceScore(rs.getInt("gaming_performance_score"));
                obj.setDescription(rs.getString("description"));
                obj.setWarrantyMonths(rs.getInt("warranty_months"));
                obj.setViewCount(rs.getInt("view_count"));
                obj.setSoldCount(rs.getInt("sold_count"));
                obj.setAvgCostPrice(rs.getDouble("avg_cost_price"));
                obj.setStatus(rs.getString("status"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Thêm linh kiện mới
    public boolean insert(Parts part) {
        String sql = "INSERT INTO parts (category_id, brand_id, product_code, part_name, base_price, sale_price, is_rgb, gaming_performance_score, description, warranty_months, view_count, sold_count, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, part.getCategoryId());
            ps.setInt(2, part.getBrandId());
            ps.setString(3, part.getProductCode());
            ps.setString(4, part.getPartName());
            ps.setDouble(5, part.getBasePrice());
            
            if (part.getSalePrice() != null) {
                ps.setDouble(6, part.getSalePrice());
            } else {
                ps.setNull(6, java.sql.Types.DECIMAL);
            }
            
            ps.setBoolean(7, part.getIsRgb() != null ? part.getIsRgb() : false);
            ps.setInt(8, part.getGamingPerformanceScore() != null ? part.getGamingPerformanceScore() : 0);
            ps.setString(9, part.getDescription());
            ps.setInt(10, part.getWarrantyMonths());
            ps.setInt(11, part.getViewCount() != null ? part.getViewCount() : 0);
            ps.setInt(12, part.getSoldCount() != null ? part.getSoldCount() : 0);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Ẩn linh kiện (Xóa mềm)
    public boolean softDelete(int partId) {
        String sql = "UPDATE parts SET status = 'HIDDEN' WHERE part_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
        // Tìm sản phẩm theo ID
    public Parts findById(int partId) {
        String sql = "SELECT * FROM parts WHERE part_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Parts obj = new Parts();
                    obj.setPartId(rs.getInt("part_id"));
                    obj.setCategoryId(rs.getInt("category_id"));
                    obj.setBrandId(rs.getInt("brand_id"));
                    obj.setSeriesId(rs.getInt("series_id"));
                    obj.setProductCode(rs.getString("product_code"));
                    obj.setPartName(rs.getString("part_name"));
                    obj.setBasePrice(rs.getDouble("base_price"));
                    obj.setSalePrice(rs.getDouble("sale_price"));
                    obj.setIsRgb(rs.getBoolean("is_rgb"));
                    obj.setGamingPerformanceScore(rs.getInt("gaming_performance_score"));
                    obj.setDescription(rs.getString("description"));
                    obj.setWarrantyMonths(rs.getInt("warranty_months"));
                    obj.setViewCount(rs.getInt("view_count"));
                    obj.setSoldCount(rs.getInt("sold_count"));
                    obj.setAvgCostPrice(rs.getDouble("avg_cost_price"));
                    obj.setStatus(rs.getString("status"));
                    obj.setCreatedAt(rs.getDate("created_at"));
                    return obj;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}