package khanhnb08894.dao;

import fourSome.model.PurchaseOrders;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrdersDAO {

    public List<PurchaseOrders> getAll() {
        List<PurchaseOrders> list = new ArrayList<>();
        String sql = "SELECT * FROM [purchase_orders]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PurchaseOrders obj = new PurchaseOrders();
                obj.setPoId(rs.getInt("po_id"));
                obj.setPoCode(rs.getString("po_code"));
                obj.setSupplierId(rs.getInt("supplier_id"));
                obj.setCreatedBy(rs.getInt("created_by"));
                obj.setTotalCost(rs.getDouble("total_cost"));
                obj.setStatus(rs.getString("status"));
                obj.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
                obj.setConfirmedAt(rs.getDate("confirmed_at"));
                obj.setReceivedAt(rs.getDate("received_at"));
                obj.setNote(rs.getString("note"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
