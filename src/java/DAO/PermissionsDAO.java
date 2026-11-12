package DAO;

import Models.Permissions;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionsDAO {

    public List<Permissions> getAll() {
        List<Permissions> list = new ArrayList<>();
        String sql = "SELECT * FROM [permissions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Permissions p = new Permissions();
                    p.setPermissionId(rs.getInt("permission_id"));
                    p.setPermissionCode(rs.getString("permission_code"));
                    p.setPermissionName(rs.getString("permission_name"));
                    p.setModule(rs.getString("module"));
                    p.setStatus(rs.getString("status"));
                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
