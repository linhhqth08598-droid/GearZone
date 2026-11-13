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
}
