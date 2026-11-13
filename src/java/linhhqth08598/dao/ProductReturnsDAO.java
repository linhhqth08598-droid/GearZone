package linhhqth08598.dao;

import model.ProductReturns;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductReturnsDAO {

    public List<ProductReturns> getAll() {
        List<ProductReturns> list = new ArrayList<>();
        String sql = "SELECT * FROM [product_returns]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProductReturns obj = new ProductReturns();
                obj.setReturnId(rs.getInt("return_id"));
                obj.setOrderId(rs.getInt("order_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setQuantity(rs.getInt("quantity"));
                obj.setSerialNumber(rs.getString("serial_number"));
                obj.setReason(rs.getString("reason"));
                obj.setRefundAmount(rs.getDouble("refund_amount"));
                obj.setRefundMethod(rs.getString("refund_method"));
                obj.setRefundTransactionCode(rs.getString("refund_transaction_code"));
                obj.setRefundedAt(rs.getDate("refunded_at"));
                obj.setStatus(rs.getString("status"));
                obj.setRejectionReason(rs.getString("rejection_reason"));
                obj.setReceivedAt(rs.getDate("received_at"));
                obj.setReturnedCondition(rs.getString("returned_condition"));
                obj.setProcessedBy(rs.getInt("processed_by"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
