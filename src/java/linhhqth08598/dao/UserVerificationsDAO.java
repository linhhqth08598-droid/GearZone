package linhhqth08598.dao;

import fourSome.model.UserVerifications;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserVerificationsDAO {

    public List<UserVerifications> getAll() {
        List<UserVerifications> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_verifications]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserVerifications obj = new UserVerifications();
                obj.setVerificationId(rs.getInt("verification_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setType(rs.getString("type"));
                obj.setCode(rs.getString("code"));
                obj.setExpiresAt(rs.getDate("expires_at"));
                obj.setIsVerified(rs.getBoolean("is_verified"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
