package linhhqth08598.dao;

import fourSome.model.PriceDropNotifications;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PriceDropNotificationsDAO {

    public List<PriceDropNotifications> getAll() {
        List<PriceDropNotifications> list = new ArrayList<>();
        String sql = "SELECT * FROM [price_drop_notifications]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PriceDropNotifications obj = new PriceDropNotifications();
                obj.setNotificationId(rs.getInt("notification_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setTargetPrice(rs.getDouble("target_price"));
                obj.setIsNotified(rs.getBoolean("is_notified"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
