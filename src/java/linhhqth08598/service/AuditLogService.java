package linhhqth08598.service;

import fourSome.model.StaffAuditLogs;
import linhhqth08598.dao.StaffAuditLogsDAO;
import java.util.List;

public class AuditLogService {
    
    private StaffAuditLogsDAO auditLogsDAO = new StaffAuditLogsDAO();

    public List<StaffAuditLogs> getAllLogs() {
        return auditLogsDAO.getAll();
    }

    // Hàm gọi để ghi log (Sử dụng luồng ngầm)
    public void logAction(int staffId, String action, String targetTable, Integer targetId, String description) {
        StaffAuditLogs log = new StaffAuditLogs();
        log.setStaffId(staffId);
        log.setAction(action);
        log.setTargetTable(targetTable);
        log.setTargetId(targetId);
        log.setDescription(description);
        
        new Thread(() -> {
            try {
                auditLogsDAO.insert(log);
            } catch (Exception e) {
                System.err.println("Lỗi ghi Audit Log ngầm: " + e.getMessage());
            }
        }).start();
    }
}