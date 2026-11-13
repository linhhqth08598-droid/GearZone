package linhhqth08598.dao;

import model.UserRoles;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRolesDAO {

    public List<UserRoles> getAll() {
        List<UserRoles> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_roles]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserRoles obj = new UserRoles();
                obj.setUserId(rs.getInt("user_id"));
                obj.setRoleId(rs.getInt("role_id"));
                obj.setAssignedAt(rs.getDate("assigned_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
