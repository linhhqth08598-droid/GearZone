package linhhqth08598.dao;

import fourSome.model.PriceDropNotifications;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PriceDropNotificationsDAO {

    // 1. Lấy danh sách theo dõi giá của user
    public List<PriceDropNotifications> getByUserId(int userId) {
        List<PriceDropNotifications> list = new ArrayList<>();
        String sql = "SELECT n.notification_id, n.user_id, n.part_id, n.target_price, n.is_notified, n.created_at, n.status, "
                   + "p.part_name, p.sale_price AS current_price, m.media_url AS image_url "
                   + "FROM price_drop_notifications n "
                   + "JOIN parts p ON n.part_id = p.part_id "
                   + "LEFT JOIN part_media m ON p.part_id = m.part_id AND m.is_primary = 1 "
                   + "WHERE n.user_id = ? AND n.status = 'ACTIVE' "
                   + "ORDER BY n.created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PriceDropNotifications obj = new PriceDropNotifications();
                    obj.setNotificationId(rs.getInt("notification_id"));
                    obj.setUserId(rs.getInt("user_id"));
                    obj.setPartId(rs.getInt("part_id"));
                    obj.setTargetPrice(rs.getDouble("target_price"));
                    obj.setIsNotified(rs.getBoolean("is_notified"));
                    obj.setCreatedAt(rs.getTimestamp("created_at"));
                    obj.setStatus(rs.getString("status"));
                    obj.setPartName(rs.getString("part_name"));
                    obj.setCurrentPrice(rs.getDouble("current_price"));
                    obj.setImageUrl(rs.getString("image_url"));
                    list.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Kiểm tra đã theo dõi chưa
    public boolean checkExist(int userId, int partId) {
        String sql = "SELECT 1 FROM price_drop_notifications WHERE user_id = ? AND part_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, partId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. Thêm mới hoặc cập nhật giá mong muốn
    public boolean insertOrUpdate(int userId, int partId, double targetPrice) {
        if (checkExist(userId, partId)) {
            // Nếu đã tồn tại, cập nhật lại giá và reset trạng thái thông báo
            String sql = "UPDATE price_drop_notifications SET target_price = ?, is_notified = 0 WHERE user_id = ? AND part_id = ? AND status = 'ACTIVE'";
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, targetPrice);
                ps.setInt(2, userId);
                ps.setInt(3, partId);
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Nếu chưa có, thêm mới
            String sql = "INSERT INTO price_drop_notifications (user_id, part_id, target_price, is_notified, created_at, status) "
                       + "VALUES (?, ?, ?, 0, GETDATE(), 'ACTIVE')";
            try (Connection conn = DBConnect.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setInt(2, partId);
                ps.setDouble(3, targetPrice);
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 4. Hủy theo dõi
    public boolean delete(int notificationId, int userId) {
        String sql = "UPDATE price_drop_notifications SET status = 'INACTIVE' WHERE notification_id = ? AND user_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}