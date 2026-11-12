package DAO;

import Models.UserVerifications;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserVerificationsDAO {

    public List<UserVerifications> getAll() {
        List<UserVerifications> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_verifications]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserVerifications uv = new UserVerifications();
                    uv.setVerificationId(rs.getInt("verification_id"));
                    uv.setUserId(rs.getInt("user_id"));
                    uv.setType(rs.getString("type"));
                    uv.setCode(rs.getString("code"));
                    uv.setExpiresAt(rs.getDate("expires_at"));
                    uv.setIsVerified(rs.getBoolean("is_verified"));
                    uv.setStatus(rs.getString("status"));
                    list.add(uv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
