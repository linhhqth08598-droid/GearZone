package DAO;

import Models.Inventory;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    public List<Inventory> getAll() {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT * FROM [inventory]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Inventory i = new Inventory();
                    i.setInventoryId(rs.getInt("inventory_id"));
                    i.setPartId(rs.getInt("part_id"));
                    i.setStockQuantity(rs.getInt("stock_quantity"));
                    i.setReservedQuantity(rs.getInt("reserved_quantity"));
                    i.setDamagedQuantity(rs.getInt("damaged_quantity"));
                    i.setLowStockThreshold(rs.getInt("low_stock_threshold"));
                    i.setUpdatedAt(rs.getDate("updated_at"));
                    i.setStatus(rs.getString("status"));
                    list.add(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
