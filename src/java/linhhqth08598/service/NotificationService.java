package linhhqth08598.service;

import fourSome.model.Notifications;
import linhhqth08598.dao.NotificationsDAO;
import java.util.List;

public class NotificationService {
    private NotificationsDAO dao = new NotificationsDAO();

    public List<Notifications> getNotifications(int userId) {
        return dao.getByUserId(userId);
    }

    public void markAllRead(int userId) {
        dao.markAllAsRead(userId);
    }

    public int getUnreadCount(int userId) {
        return dao.countUnread(userId);
    }

    // ===== Hàm cho thành viên khác gọi khi trạng thái thay đổi (không ném lỗi: thông báo hỏng không được làm hỏng nghiệp vụ chính) =====

    /** Gửi thông báo chung. */
    public boolean send(int userId, String type, String title, String content, String refType, Integer refId) {
        try {
            return dao.insert(userId, type, title, content, refType, refId);
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /** UC 285 - Dũng gọi khi đổi trạng thái yêu cầu bảo hành (warranty_claims). statusText ví dụ: "Đã tiếp nhận", "Đang sửa chữa", "Hoàn tất". */
    public boolean notifyWarrantyProgress(int userId, int claimId, String statusText) {
        return send(userId, "WARRANTY", "Cập nhật yêu cầu bảo hành #" + claimId,
                "Yêu cầu bảo hành #" + claimId + " của bạn hiện ở trạng thái: " + statusText + ".", "WARRANTY_CLAIM", claimId);
    }

    /** Dũng gọi khi đặt hàng/đổi trạng thái đơn (UC 234). */
    public boolean notifyOrder(int userId, int orderId, String message) {
        return send(userId, "ORDER", "Đơn hàng #" + orderId, message, "ORDER", orderId);
    }

    /** Dũng gọi khi gửi khuyến mãi hàng loạt (UC 250). */
    public boolean notifyPromotion(int userId, String title, String content) {
        return send(userId, "PROMOTION", title, content, null, null);
    }
}
