package DAO;

import Models.PurchaseOrders;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrdersDAO {

    public List<PurchaseOrders> getAll() {
        List<PurchaseOrders> list = new ArrayList<>();
        String sql = "SELECT * FROM [purchase_orders]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrders po = new PurchaseOrders();
                    po.setPoId(rs.getInt("po_id"));
                    po.setPoCode(rs.getString("po_code"));
                    po.setSupplierId(rs.getInt("supplier_id"));
                    po.setCreatedBy(rs.getInt("created_by"));
                    po.setTotalCost(rs.getDouble("total_cost"));
                    po.setStatus(rs.getString("status"));
                    po.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
                    po.setConfirmedAt(rs.getDate("confirmed_at"));
                    po.setReceivedAt(rs.getDate("received_at"));
                    po.setNote(rs.getString("note"));
                    po.setCreatedAt(rs.getDate("created_at"));
                    list.add(po);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
