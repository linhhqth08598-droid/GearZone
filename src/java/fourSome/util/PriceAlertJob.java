package fourSome.util;

import fourSome.url.DBConnect;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PriceAlertJob {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void start() {
        // Chạy quét mỗi 1 phút một lần (để test, khi thật thì đổi thành 1 giờ)
        scheduler.scheduleAtFixedRate(this::scanAndNotify, 0, 1, TimeUnit.MINUTES);
        System.out.println(">>> [PRICE ALERT JOB] Đã khởi động trình quét giá ngầm.");
    }

    private void scanAndNotify() {
        String sql = "SELECT n.notification_id, n.user_id, n.part_id, n.target_price, p.part_name, p.sale_price "
                   + "FROM price_drop_notifications n "
                   + "JOIN parts p ON n.part_id = p.part_id "
                   + "WHERE n.is_notified = 0 AND n.status = 'ACTIVE' AND p.sale_price <= n.target_price AND p.sale_price > 0";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int notificationId = rs.getInt("notification_id");
                int userId = rs.getInt("user_id");
                String partName = rs.getString("part_name");
                double currentPrice = rs.getDouble("sale_price");
                double targetPrice = rs.getDouble("target_price");

                // 1. Cập nhật trạng thái đã thông báo
                markAsNotified(notificationId);

                // 2. Tạo thông báo (chèn vào bảng notifications)
                createNotification(userId, partName, currentPrice, targetPrice);

                // 3. (Tùy chọn) Gửi Email thông báo
                // Bạn có thể gọi EmailUtil.send() ở đây nếu muốn.

                System.out.println(">>> [PRICE ALERT] Đã thông báo cho User ID " + userId + " về sản phẩm " + partName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markAsNotified(int notificationId) {
        String sql = "UPDATE price_drop_notifications SET is_notified = 1 WHERE notification_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotification(int userId, String partName, double currentPrice, double targetPrice) {
        String title = "Giá giảm cho sản phẩm bạn theo dõi!";
        String content = "Sản phẩm '" + partName + "' hiện đang được bán với giá " 
                       + String.format("%,.0f", currentPrice) + " VNĐ (bằng hoặc thấp hơn mức giá bạn mong muốn " 
                       + String.format("%,.0f", targetPrice) + " VNĐ). Mua ngay kẻo lỡ!";
        
        String sql = "INSERT INTO notifications (user_id, notification_type, title, content, channel, status, is_read, created_at) "
                   + "VALUES (?, 'PRICE_DROP', ?, ?, 'IN_APP', 'PENDING', 0, GETDATE())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}