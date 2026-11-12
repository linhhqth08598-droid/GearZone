package DAO;

import Models.Coupons;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouponsDAO {

    public List<Coupons> getAll() {
        List<Coupons> list = new ArrayList<>();
        String sql = "SELECT * FROM [coupons]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Coupons c = new Coupons();
                    c.setCouponId(rs.getInt("coupon_id"));
                    c.setCode(rs.getString("code"));
                    c.setCouponName(rs.getString("coupon_name"));
                    c.setDiscountType(rs.getString("discount_type"));
                    c.setDiscountValue(rs.getDouble("discount_value"));
                    c.setMinOrderValue(rs.getDouble("min_order_value"));
                    c.setStartDate(rs.getDate("start_date"));
                    c.setEndDate(rs.getDate("end_date"));
                    c.setUsageLimit(rs.getInt("usage_limit"));
                    c.setPerUserLimit(rs.getInt("per_user_limit"));
                    c.setUsedCount(rs.getInt("used_count"));
                    c.setMaxDiscountAmount(rs.getDouble("max_discount_amount"));
                    c.setApplicableCategoryId(rs.getInt("applicable_category_id"));
                    c.setApplicableBrandId(rs.getInt("applicable_brand_id"));
                    c.setStatus(rs.getString("status"));
                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
