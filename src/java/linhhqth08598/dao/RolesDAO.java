package linhhqth08598.dao;

import fourSome.model.Roles;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RolesDAO {
    public Roles findByCode(String roleCode) {
        String sql = "SELECT * FROM roles WHERE role_code = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Roles role = new Roles();
                    role.setRoleId(rs.getInt("role_id"));
                    role.setRoleCode(rs.getString("role_code"));
                    role.setRoleName(rs.getString("role_name"));
                    return role;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}