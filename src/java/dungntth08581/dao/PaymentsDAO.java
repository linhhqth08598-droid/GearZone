package dungntth08581.dao;

import fourSome.model.Payments;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentsDAO {

    public List<Payments> getAll() {
        List<Payments> list = new ArrayList<>();
        String sql = "SELECT * FROM [payments]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Payments obj = new Payments();
                obj.setPaymentId(rs.getInt("payment_id"));
                obj.setOrderId(rs.getInt("order_id"));
                obj.setPaymentMethod(rs.getString("payment_method"));
                obj.setAmount(rs.getDouble("amount"));
                obj.setTransactionCode(rs.getString("transaction_code"));
                obj.setPaymentStatus(rs.getString("payment_status"));
                obj.setPaidAt(rs.getDate("paid_at"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
