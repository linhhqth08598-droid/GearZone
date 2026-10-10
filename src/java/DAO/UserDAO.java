package DAO;

import Models.User;
import Url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    
    // Đăng ký user mới (Mặc định RoleID = 4 là User)
    public boolean insertUser(User user) {
        String sql = "INSERT INTO Users (RoleID, Username, PasswordHash, Email, FullName, PhoneNumber) VALUES (4, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhoneNumber());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đăng nhập
    public User checkLogin(String usernameOrEmail, String password) {
        String sql = "SELECT * FROM Users WHERE (Username = ? OR Email = ?) AND PasswordHash = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);
            ps.setString(3, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("UserID"));
                u.setRoleId(rs.getInt("RoleID"));
                u.setUsername(rs.getString("Username"));
                u.setFullName(rs.getString("FullName"));
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra trùng lặp
    public boolean checkExist(String username, String email) {
        String sql = "SELECT 1 FROM Users WHERE Username = ? OR Email = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==== THÊM HÀM MỚI CHO QUÊN MẬT KHẨU ====
    // Lấy thông tin user theo Email
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("UserID"));
                u.setRoleId(rs.getInt("RoleID"));
                u.setUsername(rs.getString("Username"));
                u.setEmail(rs.getString("Email"));
                u.setFullName(rs.getString("FullName"));
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật mật khẩu mới
    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}