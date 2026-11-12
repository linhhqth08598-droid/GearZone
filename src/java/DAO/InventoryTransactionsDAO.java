package DAO;

import Models.InventoryTransactions;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryTransactionsDAO {

    public List<InventoryTransactions> getAll() {
        List<InventoryTransactions> list = new ArrayList<>();
        String sql = "SELECT * FROM [inventory_transactions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InventoryTransactions it = new InventoryTransactions();
                    it.setTransactionId(rs.getInt("transaction_id"));
                    it.setPartId(rs.getInt("part_id"));
                    it.setTransactionType(rs.getString("transaction_type"));
                    it.setQuantityChange(rs.getInt("quantity_change"));
                    it.setQuantityBefore(rs.getInt("quantity_before"));
                    it.setQuantityAfter(rs.getInt("quantity_after"));
                    it.setReferenceType(rs.getString("reference_type"));
                    it.setReferenceId(rs.getInt("reference_id"));
                    it.setReason(rs.getString("reason"));
                    it.setCreatedBy(rs.getInt("created_by"));
                    it.setCreatedAt(rs.getDate("created_at"));
                    it.setStatus(rs.getString("status"));
                    list.add(it);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
