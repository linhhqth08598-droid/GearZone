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
}