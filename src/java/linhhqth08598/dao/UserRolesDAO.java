package linhhqth08598.dao;

import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;

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
}