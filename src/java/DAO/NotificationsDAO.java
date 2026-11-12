package DAO;

import Models.Notifications;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationsDAO {

    public List<Notifications> getAll() {
        List<Notifications> list = new ArrayList<>();
        String sql = "SELECT * FROM [notifications]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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
                    n.setReferenceId(rs.getInt("reference_id"));
                    n.setStatus(rs.getString("status"));
                    n.setIsRead(rs.getBoolean("is_read"));
                    n.setCreatedAt(rs.getDate("created_at"));
                    n.setSentAt(rs.getDate("sent_at"));
                    list.add(n);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
