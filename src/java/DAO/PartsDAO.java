package DAO;

import Models.Parts;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartsDAO {

    public List<Parts> getAll() {
        List<Parts> list = new ArrayList<>();
        String sql = "SELECT * FROM [parts]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Parts p = new Parts();
                    p.setPartId(rs.getInt("part_id"));
                    p.setCategoryId(rs.getInt("category_id"));
                    p.setBrandId(rs.getInt("brand_id"));
                    p.setSeriesId(rs.getInt("series_id"));
                    p.setProductCode(rs.getString("product_code"));
                    p.setPartName(rs.getString("part_name"));
                    p.setBasePrice(rs.getDouble("base_price"));
                    p.setSalePrice(rs.getDouble("sale_price"));
                    p.setIsRgb(rs.getBoolean("is_rgb"));
                    p.setGamingPerformanceScore(rs.getInt("gaming_performance_score"));
                    p.setDescription(rs.getString("description"));
                    p.setWarrantyMonths(rs.getInt("warranty_months"));
                    p.setViewCount(rs.getInt("view_count"));
                    p.setSoldCount(rs.getInt("sold_count"));
                    p.setAvgCostPrice(rs.getDouble("avg_cost_price"));
                    p.setStatus(rs.getString("status"));
                    p.setCreatedAt(rs.getDate("created_at"));
                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
