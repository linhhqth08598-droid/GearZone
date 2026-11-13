package linhhqth08598.dao;

import fourSome.model.Permissions;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PermissionsDAO {
    
    public List<Permissions> getAll() {
        List<Permissions> list = new ArrayList<>();
        String sql = "SELECT * FROM permissions WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Permissions p = new Permissions();
                p.setPermissionId(rs.getInt("permission_id"));
                p.setPermissionCode(rs.getString("permission_code"));
                p.setPermissionName(rs.getString("permission_name"));
                p.setModule(rs.getString("module"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}