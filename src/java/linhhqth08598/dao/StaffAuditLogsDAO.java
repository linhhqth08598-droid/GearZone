package linhhqth08598.dao;

import fourSome.model.StaffAuditLogs;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffAuditLogsDAO {

    public List<StaffAuditLogs> getAll() {
        List<StaffAuditLogs> list = new ArrayList<>();
        String sql = "SELECT * FROM staff_audit_logs ORDER BY created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StaffAuditLogs log = new StaffAuditLogs();
                log.setLogId(rs.getInt("log_id"));
                log.setStaffId(rs.getInt("staff_id"));
                log.setAction(rs.getString("action"));
                log.setTargetTable(rs.getString("target_table"));
                log.setTargetId(rs.getInt("target_id"));
                log.setDescription(rs.getString("description"));
                log.setCreatedAt(rs.getTimestamp("created_at"));
                log.setStatus(rs.getString("status"));
                list.add(log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(StaffAuditLogs log) {
        String sql = "INSERT INTO staff_audit_logs (staff_id, action, target_table, target_id, description, created_at, status) " +
                     "VALUES (?, ?, ?, ?, ?, GETDATE(), 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, log.getStaffId());
            ps.setString(2, log.getAction());
            ps.setString(3, log.getTargetTable());
            
            if (log.getTargetId() != null && log.getTargetId() > 0) {
                ps.setInt(4, log.getTargetId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, log.getDescription());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
        // Thêm hàm này vào StaffAuditLogsDAO.java
    public List<StaffAuditLogs> search(String keyword, String fromDate, String toDate) {
        List<StaffAuditLogs> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM staff_audit_logs WHERE 1=1 ");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (action LIKE ? OR target_table LIKE ? OR description LIKE ?) ");
        }
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            sql.append("AND created_at >= ? ");
        }
        if (toDate != null && !toDate.trim().isEmpty()) {
            sql.append("AND created_at <= ? ");
        }
        sql.append("ORDER BY created_at DESC");

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                ps.setString(paramIndex++, fromDate + " 00:00:00");
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                ps.setString(paramIndex++, toDate + " 23:59:59");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffAuditLogs log = new StaffAuditLogs();
                    log.setLogId(rs.getInt("log_id"));
                    log.setStaffId(rs.getInt("staff_id"));
                    log.setAction(rs.getString("action"));
                    log.setTargetTable(rs.getString("target_table"));
                    log.setTargetId(rs.getInt("target_id"));
                    log.setDescription(rs.getString("description"));
                    log.setCreatedAt(rs.getTimestamp("created_at"));
                    log.setStatus(rs.getString("status"));
                    list.add(log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}