package linhhqth08598.service;

import fourSome.model.SupportTickets;
import fourSome.model.Users;
import linhhqth08598.dao.SupportTicketsDAO;
import java.util.List;
import java.util.UUID;

public class SupportService {
    private SupportTicketsDAO dao = new SupportTicketsDAO();

    public String createTicket(int userId, String type, String subject, String content) {
        if (subject == null || subject.trim().isEmpty()) {
            return "Vui lòng nhập tiêu đề!";
        }
        if (content == null || content.trim().isEmpty()) {
            return "Vui lòng nhập nội dung!";
        }

        SupportTickets ticket = new SupportTickets();
        // Tạo mã ticket tự động (VD: TK-12345678)
        String ticketCode = "TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        ticket.setTicketCode(ticketCode);
        ticket.setUserId(userId);
        ticket.setTicketType(type);
        ticket.setSubject(subject);
        ticket.setContent(content);

        if (dao.insertTicket(ticket)) {
            return null;
        }
        return "Lỗi hệ thống khi gửi yêu cầu!";
    }

    public List<SupportTickets> getUserTickets(int userId) {
        return dao.getByUserId(userId);
    }
    
        // Admin lấy danh sách
    public List<SupportTickets> getAllTicketsForAdmin() {
        return dao.getAllTicketsForAdmin();
    }

    // Admin cập nhật trạng thái
    public String processTicket(int ticketId, String status, Users adminUser) {
        if (!"IN_PROGRESS".equals(status) && !"RESOLVED".equals(status) && !"CLOSED".equals(status)) {
            return "Trạng thái không hợp lệ!";
        }

        if (dao.updateTicketStatus(ticketId, status)) {
            // Ghi Audit Log
            AuditLogService auditLogService = new AuditLogService();
            auditLogService.logAction(adminUser.getUserId(), "UPDATE_TICKET_STATUS", "support_tickets", ticketId, 
                "Admin " + adminUser.getEmail() + " đã chuyển trạng thái ticket thành: " + status);
            return null;
        }
        return "Lỗi cập nhật trạng thái!";
    }
}