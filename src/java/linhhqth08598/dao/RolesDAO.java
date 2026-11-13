package linhhqth08598.dao;

import fourSome.model.Roles;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolesDAO {

    public List<Roles> getAll() {
        List<Roles> list = new ArrayList<>();
        String sql = "SELECT * FROM [roles]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Roles obj = new Roles();
                obj.setRoleId(rs.getInt("role_id"));
                obj.setRoleCode(rs.getString("role_code"));
                obj.setRoleName(rs.getString("role_name"));
                obj.setDescription(rs.getString("description"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
