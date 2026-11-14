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
}