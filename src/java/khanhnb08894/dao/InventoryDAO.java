package khanhnb08894.dao;

import model.Inventory;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    public List<Inventory> getAll() {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT * FROM [inventory]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Inventory obj = new Inventory();
                obj.setInventoryId(rs.getInt("inventory_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setStockQuantity(rs.getInt("stock_quantity"));
                obj.setReservedQuantity(rs.getInt("reserved_quantity"));
                obj.setDamagedQuantity(rs.getInt("damaged_quantity"));
                obj.setLowStockThreshold(rs.getInt("low_stock_threshold"));
                obj.setUpdatedAt(rs.getDate("updated_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
