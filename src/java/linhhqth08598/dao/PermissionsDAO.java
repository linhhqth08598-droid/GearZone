package linhhqth08598.dao;

import model.Permissions;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionsDAO {

    public List<Permissions> getAll() {
        List<Permissions> list = new ArrayList<>();
        String sql = "SELECT * FROM [permissions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Permissions obj = new Permissions();
                obj.setPermissionId(rs.getInt("permission_id"));
                obj.setPermissionCode(rs.getString("permission_code"));
                obj.setPermissionName(rs.getString("permission_name"));
                obj.setModule(rs.getString("module"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
