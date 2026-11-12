package DAO;

import Models.Roles;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolesDAO {

    public List<Roles> getAll() {
        List<Roles> list = new ArrayList<>();
        String sql = "SELECT * FROM [roles]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Roles r = new Roles();
                    r.setRoleId(rs.getInt("role_id"));
                    r.setRoleCode(rs.getString("role_code"));
                    r.setRoleName(rs.getString("role_name"));
                    r.setDescription(rs.getString("description"));
                    r.setStatus(rs.getString("status"));
                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
