package DAO;

import Url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ContactDAO {
    
    // Lưu tin nhắn từ khách hàng
    public boolean insertMessage(String name, String email, String phone, String content) {
        String sql = "INSERT INTO Contact_Messages (FullName, Email, PhoneNumber, MessageContent) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, content);
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Lấy toàn bộ tin nhắn liên hệ cho Admin (Mới nhất lên đầu)
    public java.util.List<Models.ContactMessage> getAllMessages() {
        java.util.List<Models.ContactMessage> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Contact_Messages ORDER BY CreatedAt DESC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                Models.ContactMessage msg = new Models.ContactMessage();
                msg.setMessageId(rs.getInt("MessageID"));
                msg.setFullName(rs.getString("FullName"));
                msg.setEmail(rs.getString("Email"));
                msg.setPhoneNumber(rs.getString("PhoneNumber"));
                msg.setMessageContent(rs.getString("MessageContent"));
                msg.setCreatedAt(rs.getDate("CreatedAt"));
                msg.setIsRead(rs.getBoolean("IsRead"));
                list.add(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đánh dấu tin nhắn là đã đọc
    public void markAsRead(int messageId) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE Contact_Messages SET IsRead = 1 WHERE MessageID = ?")) {
            ps.setInt(1, messageId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Xóa tin nhắn
    public void deleteMessage(int messageId) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Contact_Messages WHERE MessageID = ?")) {
            ps.setInt(1, messageId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}