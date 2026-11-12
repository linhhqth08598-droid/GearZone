package DAO;

import Models.UserRoles;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRolesDAO {

    public List<UserRoles> getAll() {
        List<UserRoles> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_roles]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserRoles ur = new UserRoles();
                    ur.setUserId(rs.getInt("user_id"));
                    ur.setRoleId(rs.getInt("role_id"));
                    ur.setAssignedAt(rs.getDate("assigned_at"));
                    ur.setStatus(rs.getString("status"));
                    list.add(ur);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
