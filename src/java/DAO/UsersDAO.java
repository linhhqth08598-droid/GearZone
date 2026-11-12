package DAO;

import Models.Users;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {

    public List<Users> getAll() {
        List<Users> list = new ArrayList<>();
        String sql = "SELECT * FROM [users]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Users u = new Users();
                    u.setUserId(rs.getInt("user_id"));
                    u.setEmail(rs.getString("email"));
                    u.setPhoneNumber(rs.getString("phone_number"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setFullName(rs.getString("full_name"));
                    u.setAvatarUrl(rs.getString("avatar_url"));
                    u.setIsEmailVerified(rs.getBoolean("is_email_verified"));
                    u.setIsPhoneVerified(rs.getBoolean("is_phone_verified"));
                    u.setLastLoginAt(rs.getDate("last_login_at"));
                    u.setStatus(rs.getString("status"));
                    u.setCreatedAt(rs.getDate("created_at"));
                    u.setUpdatedAt(rs.getDate("updated_at"));
                    list.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
