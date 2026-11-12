package DAO;

import Models.Payments;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentsDAO {

    public List<Payments> getAll() {
        List<Payments> list = new ArrayList<>();
        String sql = "SELECT * FROM [payments]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payments p = new Payments();
                    p.setPaymentId(rs.getInt("payment_id"));
                    p.setOrderId(rs.getInt("order_id"));
                    p.setPaymentMethod(rs.getString("payment_method"));
                    p.setAmount(rs.getDouble("amount"));
                    p.setTransactionCode(rs.getString("transaction_code"));
                    p.setPaymentStatus(rs.getString("payment_status"));
                    p.setPaidAt(rs.getDate("paid_at"));
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
