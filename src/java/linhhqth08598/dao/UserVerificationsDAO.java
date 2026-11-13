package linhhqth08598.dao;

import fourSome.model.UserVerifications;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserVerificationsDAO {

    public boolean insertOTP(int userId, String type, String code, int expireMinutes) {
        String sql = "INSERT INTO user_verifications (user_id, type, code, expires_at, is_verified, status) "
                + "VALUES (?, ?, ?, DATEADD(minute, ?, GETDATE()), 0, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, code);
            ps.setInt(4, expireMinutes);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserVerifications findValidOTP(int userId, String type, String code) {
        String sql = "SELECT * FROM user_verifications "
                + "WHERE user_id = ? AND type = ? AND code = ? "
                + "AND is_verified = 0 AND expires_at >= GETDATE() AND status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setString(3, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserVerifications uv = new UserVerifications();
                    uv.setVerificationId(rs.getInt("verification_id"));
                    uv.setUserId(rs.getInt("user_id"));
                    uv.setType(rs.getString("type"));
                    uv.setCode(rs.getString("code"));
                    uv.setExpiresAt(rs.getTimestamp("expires_at"));
                    uv.setIsVerified(rs.getBoolean("is_verified"));
                    uv.setStatus(rs.getString("status"));
                    return uv;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean markAsVerified(int verificationId) {
        String sql = "UPDATE user_verifications SET is_verified = 1, status = 'INACTIVE' WHERE verification_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, verificationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Thêm hàm này vào cuối class UserVerificationsDAO
    public java.sql.Timestamp getLatestOTPExpiry(int userId, String type) {
        String sql = "SELECT TOP 1 expires_at FROM user_verifications "
                + "WHERE user_id = ? AND type = ? AND is_verified = 0 AND status = 'ACTIVE' "
                + "ORDER BY expires_at DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("expires_at");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
