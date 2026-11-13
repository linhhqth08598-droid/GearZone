package khanhnb08894.dao;

import fourSome.model.InventoryTransactions;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryTransactionsDAO {

    public List<InventoryTransactions> getAll() {
        List<InventoryTransactions> list = new ArrayList<>();
        String sql = "SELECT * FROM [inventory_transactions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                InventoryTransactions obj = new InventoryTransactions();
                obj.setTransactionId(rs.getInt("transaction_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setTransactionType(rs.getString("transaction_type"));
                obj.setQuantityChange(rs.getInt("quantity_change"));
                obj.setQuantityBefore(rs.getInt("quantity_before"));
                obj.setQuantityAfter(rs.getInt("quantity_after"));
                obj.setReferenceType(rs.getString("reference_type"));
                obj.setReferenceId(rs.getInt("reference_id"));
                obj.setReason(rs.getString("reason"));
                obj.setCreatedBy(rs.getInt("created_by"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
