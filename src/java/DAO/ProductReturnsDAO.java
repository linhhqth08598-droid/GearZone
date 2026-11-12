package DAO;

import Models.ProductReturns;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductReturnsDAO {

    public List<ProductReturns> getAll() {
        List<ProductReturns> list = new ArrayList<>();
        String sql = "SELECT * FROM [product_returns]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductReturns pr = new ProductReturns();
                    pr.setReturnId(rs.getInt("return_id"));
                    pr.setOrderId(rs.getInt("order_id"));
                    pr.setPartId(rs.getInt("part_id"));
                    pr.setQuantity(rs.getInt("quantity"));
                    pr.setSerialNumber(rs.getString("serial_number"));
                    pr.setReason(rs.getString("reason"));
                    pr.setRefundAmount(rs.getDouble("refund_amount"));
                    pr.setRefundMethod(rs.getString("refund_method"));
                    pr.setRefundTransactionCode(rs.getString("refund_transaction_code"));
                    pr.setRefundedAt(rs.getDate("refunded_at"));
                    pr.setStatus(rs.getString("status"));
                    pr.setRejectionReason(rs.getString("rejection_reason"));
                    pr.setReceivedAt(rs.getDate("received_at"));
                    pr.setReturnedCondition(rs.getString("returned_condition"));
                    pr.setProcessedBy(rs.getInt("processed_by"));
                    pr.setCreatedAt(rs.getDate("created_at"));
                    list.add(pr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
