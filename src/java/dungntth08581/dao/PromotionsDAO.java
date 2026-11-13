package dungntth08581.dao;

import fourSome.model.Promotions;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionsDAO {

    public List<Promotions> getAll() {
        List<Promotions> list = new ArrayList<>();
        String sql = "SELECT * FROM [promotions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Promotions obj = new Promotions();
                obj.setPromotionId(rs.getInt("promotion_id"));
                obj.setPromotionName(rs.getString("promotion_name"));
                obj.setPromotionType(rs.getString("promotion_type"));
                obj.setDiscountType(rs.getString("discount_type"));
                obj.setDiscountValue(rs.getDouble("discount_value"));
                obj.setMinOrderValue(rs.getDouble("min_order_value"));
                obj.setTargetPartId(rs.getInt("target_part_id"));
                obj.setTargetCategoryId(rs.getInt("target_category_id"));
                obj.setTargetBrandId(rs.getInt("target_brand_id"));
                obj.setStartDate(rs.getDate("start_date"));
                obj.setEndDate(rs.getDate("end_date"));
                obj.setStatus(rs.getString("status"));
                obj.setCreatedBy(rs.getInt("created_by"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
