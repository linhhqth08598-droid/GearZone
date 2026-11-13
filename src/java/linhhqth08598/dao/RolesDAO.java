package linhhqth08598.dao;

import fourSome.model.Roles;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RolesDAO {
    
    public List<Roles> getAll() {
        List<Roles> list = new ArrayList<>();
        String sql = "SELECT * FROM roles WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Roles role = new Roles();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleCode(rs.getString("role_code"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                list.add(role);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

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