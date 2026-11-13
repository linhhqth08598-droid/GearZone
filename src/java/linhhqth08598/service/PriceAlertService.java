package linhhqth08598.service;

import fourSome.model.PriceDropNotifications;
import linhhqth08598.dao.PriceDropNotificationsDAO;
import java.util.List;

public class PriceAlertService {
    private PriceDropNotificationsDAO dao = new PriceDropNotificationsDAO();

    public String registerPriceAlert(int userId, int partId, double targetPrice) {
        if (targetPrice <= 0) {
            return "Mức giá mong muốn phải lớn hơn 0!";
        }
        if (dao.insertOrUpdate(userId, partId, targetPrice)) {
            return null;
        }
        return "Lỗi khi đăng ký theo dõi giá!";
    }

    public List<PriceDropNotifications> getAlerts(int userId) {
        return dao.getByUserId(userId);
    }

    public String deleteAlert(int notificationId, int userId) {
        if (dao.delete(notificationId, userId)) {
            return null;
        }
        return "Lỗi khi hủy theo dõi giá!";
    }
}