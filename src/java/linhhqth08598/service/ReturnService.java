package linhhqth08598.service;

import fourSome.model.ProductReturns;
import fourSome.model.Users;
import linhhqth08598.dao.ProductReturnsDAO;
import java.util.List;

public class ReturnService {
    private ProductReturnsDAO dao = new ProductReturnsDAO();

    public String createReturnRequest(int orderId, int partId, int quantity, String reason) {
        if (quantity <= 0) {
            return "Số lượng trả lại phải lớn hơn 0!";
        }
        if (reason == null || reason.trim().isEmpty()) {
            return "Vui lòng nhập lý do đổi trả!";
        }

        ProductReturns request = new ProductReturns();
        request.setOrderId(orderId);
        request.setPartId(partId);
        request.setQuantity(quantity);
        request.setReason(reason);

        if (dao.insertReturnRequest(request)) {
            return null;
        }
        return "Lỗi hệ thống khi gửi yêu cầu!";
    }

    public List<ProductReturns> getUserReturns(int userId) {
        return dao.getByUserId(userId);
    }
    
        // Admin lấy danh sách
    public List<ProductReturns> getAllReturnsForAdmin() {
        return dao.getAllReturnsForAdmin();
    }

    // Admin duyệt / từ chối
    public String processReturnRequest(int returnId, String status, String rejectionReason, Users adminUser) {
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            return "Trạng thái không hợp lệ!";
        }
        if ("REJECTED".equals(status) && (rejectionReason == null || rejectionReason.trim().isEmpty())) {
            return "Vui lòng nhập lý do từ chối!";
        }

        if (dao.updateReturnStatus(returnId, status, rejectionReason)) {
            // Ghi Audit Log (Gọi AuditLogService của bạn)
            AuditLogService auditLogService = new AuditLogService();
            auditLogService.logAction(adminUser.getUserId(), "UPDATE_RETURN_STATUS", "product_returns", returnId, 
                "Admin " + adminUser.getEmail() + " đã chuyển trạng thái yêu cầu đổi trả thành: " + status);
            return null;
        }
        return "Lỗi cập nhật trạng thái!";
    }
}