package linhhqth08598.dao;

import fourSome.model.Notifications;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationsDAO {

    public List<Notifications> getAll() {
        List<Notifications> list = new ArrayList<>();
        String sql = "SELECT * FROM [notifications]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Notifications obj = new Notifications();
                obj.setNotificationId(rs.getInt("notification_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setNotificationType(rs.getString("notification_type"));
                obj.setTitle(rs.getString("title"));
                obj.setContent(rs.getString("content"));
                obj.setChannel(rs.getString("channel"));
                obj.setReferenceType(rs.getString("reference_type"));
                obj.setReferenceId(rs.getInt("reference_id"));
                obj.setStatus(rs.getString("status"));
                obj.setIsRead(rs.getBoolean("is_read"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setSentAt(rs.getDate("sent_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
        // Lấy danh sách thông báo của user (mới nhất xếp trước)
    public List<Notifications> getByUserId(int userId) {
        List<Notifications> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notifications n = new Notifications();
                    n.setNotificationId(rs.getInt("notification_id"));
                    n.setUserId(rs.getInt("user_id"));
                    n.setNotificationType(rs.getString("notification_type"));
                    n.setTitle(rs.getString("title"));
                    n.setContent(rs.getString("content"));
                    n.setChannel(rs.getString("channel"));
                    n.setReferenceType(rs.getString("reference_type"));
                    n.setReferenceId(rs.getObject("reference_id", Integer.class));
                    n.setStatus(rs.getString("status"));
                    n.setIsRead(rs.getBoolean("is_read"));
                    n.setCreatedAt(rs.getTimestamp("created_at"));
                    n.setSentAt(rs.getTimestamp("sent_at"));
                    list.add(n);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đánh dấu đã đọc tất cả thông báo của user
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đếm số thông báo chưa đọc (dùng để hiện số đỏ trên chuông)
    public int countUnread(int userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
