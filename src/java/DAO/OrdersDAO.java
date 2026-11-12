package DAO;

import Models.Orders;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersDAO {

    public List<Orders> getAll() {
        List<Orders> list = new ArrayList<>();
        String sql = "SELECT * FROM [orders]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Orders o = new Orders();
                    o.setOrderId(rs.getInt("order_id"));
                    o.setOrderCode(rs.getString("order_code"));
                    o.setUserId(rs.getInt("user_id"));
                    o.setRecipientName(rs.getString("recipient_name"));
                    o.setRecipientPhone(rs.getString("recipient_phone"));
                    o.setShippingAddress(rs.getString("shipping_address"));
                    o.setShippingMethodId(rs.getInt("shipping_method_id"));
                    o.setCouponId(rs.getInt("coupon_id"));
                    o.setPromotionId(rs.getInt("promotion_id"));
                    o.setComboId(rs.getInt("combo_id"));
                    o.setShippingFee(rs.getDouble("shipping_fee"));
                    o.setTotalAmount(rs.getDouble("total_amount"));
                    o.setDiscountAmount(rs.getDouble("discount_amount"));
                    o.setFinalAmount(rs.getDouble("final_amount"));
                    o.setOrderStatus(rs.getString("order_status"));
                    o.setTrackingCode(rs.getString("tracking_code"));
                    o.setCancelReason(rs.getString("cancel_reason"));
                    o.setCancelledAt(rs.getDate("cancelled_at"));
                    o.setDeliveredAt(rs.getDate("delivered_at"));
                    o.setNote(rs.getString("note"));
                    o.setCreatedAt(rs.getDate("created_at"));
                    list.add(o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
