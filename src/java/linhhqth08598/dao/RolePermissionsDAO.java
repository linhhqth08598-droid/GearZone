package linhhqth08598.dao;

import fourSome.model.RolePermissions;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolePermissionsDAO {

    public List<RolePermissions> getAll() {
        List<RolePermissions> list = new ArrayList<>();
        String sql = "SELECT * FROM [role_permissions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RolePermissions obj = new RolePermissions();
                obj.setRoleId(rs.getInt("role_id"));
                obj.setPermissionId(rs.getInt("permission_id"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
