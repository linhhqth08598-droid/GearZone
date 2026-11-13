package linhhqth08598.dao;

import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserRolesDAO {
    
    public boolean assignRole(int userId, int roleId) {
        String sql = "INSERT INTO user_roles (user_id, role_id, assigned_at, status) VALUES (?, ?, GETDATE(), 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách các mã quyền (role_code) của 1 user
    public List<String> getRoleCodesByUserId(int userId) {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT r.role_code FROM user_roles ur " +
                     "JOIN roles r ON ur.role_id = r.role_id " +
                     "WHERE ur.user_id = ? AND ur.status = 'ACTIVE' AND r.status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("role_code"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }
}