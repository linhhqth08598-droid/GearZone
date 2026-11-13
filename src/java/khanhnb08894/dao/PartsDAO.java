package khanhnb08894.dao;

import model.Parts;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartsDAO {

    public List<Parts> getAll() {
        List<Parts> list = new ArrayList<>();
        String sql = "SELECT * FROM [parts]";

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
}
