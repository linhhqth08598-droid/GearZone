package DAO;

import Models.RolePermissions;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolePermissionsDAO {

    public List<RolePermissions> getAll() {
        List<RolePermissions> list = new ArrayList<>();
        String sql = "SELECT * FROM [role_permissions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RolePermissions rp = new RolePermissions();
                    rp.setRoleId(rs.getInt("role_id"));
                    rp.setPermissionId(rs.getInt("permission_id"));
                    rp.setStatus(rs.getString("status"));
                    list.add(rp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
