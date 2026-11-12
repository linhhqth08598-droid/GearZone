package DAO;

import Models.Promotions;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionsDAO {

    public List<Promotions> getAll() {
        List<Promotions> list = new ArrayList<>();
        String sql = "SELECT * FROM [promotions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Promotions p = new Promotions();
                    p.setPromotionId(rs.getInt("promotion_id"));
                    p.setPromotionName(rs.getString("promotion_name"));
                    p.setPromotionType(rs.getString("promotion_type"));
                    p.setDiscountType(rs.getString("discount_type"));
                    p.setDiscountValue(rs.getDouble("discount_value"));
                    p.setMinOrderValue(rs.getDouble("min_order_value"));
                    p.setTargetPartId(rs.getInt("target_part_id"));
                    p.setTargetCategoryId(rs.getInt("target_category_id"));
                    p.setTargetBrandId(rs.getInt("target_brand_id"));
                    p.setStartDate(rs.getDate("start_date"));
                    p.setEndDate(rs.getDate("end_date"));
                    p.setStatus(rs.getString("status"));
                    p.setCreatedBy(rs.getInt("created_by"));
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
