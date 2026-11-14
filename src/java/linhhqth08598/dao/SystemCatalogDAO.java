// File: SystemCatalogDAO.java
package linhhqth08598.dao;

import fourSome.model.SystemCatalog;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemCatalogDAO {
    
    // Hàm dùng chung, truyền tên bảng và cột vào để tái sử dụng
    public List<SystemCatalog> getAll(String tableName, String idCol, String nameCol) {
        List<SystemCatalog> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " ORDER BY " + idCol + " DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new SystemCatalog(rs.getInt(idCol), rs.getString(nameCol), rs.getString("status")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(String tableName, String nameCol, String name) {
        String sql = "INSERT INTO " + tableName + " (" + nameCol + ", status) VALUES (?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean update(String tableName, String idCol, String nameCol, int id, String name, String status) {
        String sql = "UPDATE " + tableName + " SET " + nameCol + " = ?, status = ? WHERE " + idCol + " = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, status);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean softDelete(String tableName, String idCol, int id) {
        String sql = "UPDATE " + tableName + " SET status = 'INACTIVE' WHERE " + idCol + " = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}