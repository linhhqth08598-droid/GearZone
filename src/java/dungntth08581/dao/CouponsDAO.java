package dungntth08581.dao;

import model.Coupons;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouponsDAO {

    public List<Coupons> getAll() {
        List<Coupons> list = new ArrayList<>();
        String sql = "SELECT * FROM [coupons]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Coupons obj = new Coupons();
                obj.setCouponId(rs.getInt("coupon_id"));
                obj.setCode(rs.getString("code"));
                obj.setCouponName(rs.getString("coupon_name"));
                obj.setDiscountType(rs.getString("discount_type"));
                obj.setDiscountValue(rs.getDouble("discount_value"));
                obj.setMinOrderValue(rs.getDouble("min_order_value"));
                obj.setStartDate(rs.getDate("start_date"));
                obj.setEndDate(rs.getDate("end_date"));
                obj.setUsageLimit(rs.getInt("usage_limit"));
                obj.setPerUserLimit(rs.getInt("per_user_limit"));
                obj.setUsedCount(rs.getInt("used_count"));
                obj.setMaxDiscountAmount(rs.getDouble("max_discount_amount"));
                obj.setApplicableCategoryId(rs.getInt("applicable_category_id"));
                obj.setApplicableBrandId(rs.getInt("applicable_brand_id"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
