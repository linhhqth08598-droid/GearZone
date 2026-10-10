package DAO;

import Models.OTPToken;
import Url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OTPDAO {
    
    // Lưu mã OTP mới vào database
    public boolean insertOTP(OTPToken otp) {
        String query = "INSERT INTO OTP_Tokens (UserID, OTPCode, TokenType, ExpirationTime, IsUsed, CreatedAt) "
                     + "VALUES (?, ?, ?, ?, 0, GETDATE())";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, otp.getUserId());
            ps.setString(2, otp.getOtpCode());
            ps.setString(3, otp.getTokenType());
            ps.setTimestamp(4, otp.getExpirationTime());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra OTP hợp lệ (Chưa hết hạn, chưa sử dụng và đúng mã)
    public OTPToken getValidOTP(int userId, String otpCode, String tokenType) {
        String query = "SELECT TOP 1 * FROM OTP_Tokens "
                     + "WHERE UserID = ? AND OTPCode = ? AND TokenType = ? AND IsUsed = 0 AND ExpirationTime > GETDATE() "
                     + "ORDER BY CreatedAt DESC";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, otpCode);
            ps.setString(3, tokenType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                OTPToken token = new OTPToken();
                token.setTokenId(rs.getInt("TokenID"));
                token.setUserId(rs.getInt("UserID"));
                token.setOtpCode(rs.getString("OTPCode"));
                return token;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Đánh dấu OTP đã được sử dụng
    public void markAsUsed(int tokenId) {
        String query = "UPDATE OTP_Tokens SET IsUsed = 1 WHERE TokenID = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, tokenId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}