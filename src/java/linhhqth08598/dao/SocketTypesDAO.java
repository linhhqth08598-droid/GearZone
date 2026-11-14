package linhhqth08598.dao;

import fourSome.model.SocketTypes;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocketTypesDAO {
    
    public List<SocketTypes> getAll() {
        List<SocketTypes> list = new ArrayList<>();
        String sql = "SELECT * FROM socket_types ORDER BY socket_id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SocketTypes s = new SocketTypes();
                s.setSocketId(rs.getInt("socket_id"));
                s.setSocketName(rs.getString("socket_name"));
                s.setStatus(rs.getString("status"));
                list.add(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(SocketTypes socket) {
        String sql = "INSERT INTO socket_types (socket_name, status) VALUES (?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, socket.getSocketName());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(SocketTypes socket) {
        String sql = "UPDATE socket_types SET socket_name = ?, status = ? WHERE socket_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, socket.getSocketName());
            ps.setString(2, socket.getStatus());
            ps.setInt(3, socket.getSocketId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean softDelete(int socketId) {
        String sql = "UPDATE socket_types SET status = 'INACTIVE' WHERE socket_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, socketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    
    public boolean checkNameExist(String name, int excludeId) {
        String sql = "SELECT 1 FROM socket_types WHERE socket_name = ? AND socket_id != ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}