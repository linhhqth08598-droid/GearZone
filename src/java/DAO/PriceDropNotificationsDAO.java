package DAO;

import Models.PriceDropNotifications;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PriceDropNotificationsDAO {

    public List<PriceDropNotifications> getAll() {
        List<PriceDropNotifications> list = new ArrayList<>();
        String sql = "SELECT * FROM [price_drop_notifications]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PriceDropNotifications pdn = new PriceDropNotifications();
                    pdn.setNotificationId(rs.getInt("notification_id"));
                    pdn.setUserId(rs.getInt("user_id"));
                    pdn.setPartId(rs.getInt("part_id"));
                    pdn.setTargetPrice(rs.getDouble("target_price"));
                    pdn.setIsNotified(rs.getBoolean("is_notified"));
                    pdn.setCreatedAt(rs.getDate("created_at"));
                    pdn.setStatus(rs.getString("status"));
                    list.add(pdn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
