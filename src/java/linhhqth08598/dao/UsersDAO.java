package linhhqth08598.dao;

import model.Users;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {

    public List<Users> getAll() {
        List<Users> list = new ArrayList<>();
        String sql = "SELECT * FROM [users]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Users obj = new Users();
                obj.setUserId(rs.getInt("user_id"));
                obj.setEmail(rs.getString("email"));
                obj.setPhoneNumber(rs.getString("phone_number"));
                obj.setPasswordHash(rs.getString("password_hash"));
                obj.setFullName(rs.getString("full_name"));
                obj.setAvatarUrl(rs.getString("avatar_url"));
                obj.setIsEmailVerified(rs.getBoolean("is_email_verified"));
                obj.setIsPhoneVerified(rs.getBoolean("is_phone_verified"));
                obj.setLastLoginAt(rs.getDate("last_login_at"));
                obj.setStatus(rs.getString("status"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setUpdatedAt(rs.getDate("updated_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
