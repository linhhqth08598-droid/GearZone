package dungntth08581.dao;

import fourSome.model.Orders;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersDAO {

    public List<Orders> getAll() {
        List<Orders> list = new ArrayList<>();
        String sql = "SELECT * FROM [orders]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Orders obj = new Orders();
                obj.setOrderId(rs.getInt("order_id"));
                obj.setOrderCode(rs.getString("order_code"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setRecipientName(rs.getString("recipient_name"));
                obj.setRecipientPhone(rs.getString("recipient_phone"));
                obj.setShippingAddress(rs.getString("shipping_address"));
                obj.setShippingMethodId(rs.getInt("shipping_method_id"));
                obj.setCouponId(rs.getInt("coupon_id"));
                obj.setPromotionId(rs.getInt("promotion_id"));
                obj.setComboId(rs.getInt("combo_id"));
                obj.setShippingFee(rs.getDouble("shipping_fee"));
                obj.setTotalAmount(rs.getDouble("total_amount"));
                obj.setDiscountAmount(rs.getDouble("discount_amount"));
                obj.setFinalAmount(rs.getDouble("final_amount"));
                obj.setOrderStatus(rs.getString("order_status"));
                obj.setTrackingCode(rs.getString("tracking_code"));
                obj.setCancelReason(rs.getString("cancel_reason"));
                obj.setCancelledAt(rs.getDate("cancelled_at"));
                obj.setDeliveredAt(rs.getDate("delivered_at"));
                obj.setNote(rs.getString("note"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
