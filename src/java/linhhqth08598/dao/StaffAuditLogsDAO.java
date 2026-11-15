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

    // ---------- Phân trang (PaginationUtil, ADMIN_PAGE_SIZE) ----------

    private String buildWhere(String keyword, String fromDate, String toDate) {
        StringBuilder w = new StringBuilder(" WHERE 1=1 ");
        if (keyword != null && !keyword.trim().isEmpty()) w.append("AND (action LIKE ? OR target_table LIKE ? OR description LIKE ?) ");
        if (fromDate != null && !fromDate.trim().isEmpty()) w.append("AND created_at >= ? ");
        if (toDate != null && !toDate.trim().isEmpty()) w.append("AND created_at <= ? ");
        return w.toString();
    }

    private int bindFilters(PreparedStatement ps, String keyword, String fromDate, String toDate) throws SQLException {
        int i = 1;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String pattern = "%" + keyword.trim() + "%";
            ps.setString(i++, pattern);
            ps.setString(i++, pattern);
            ps.setString(i++, pattern);
        }
        if (fromDate != null && !fromDate.trim().isEmpty()) ps.setString(i++, fromDate + " 00:00:00");
        if (toDate != null && !toDate.trim().isEmpty()) ps.setString(i++, toDate + " 23:59:59");
        return i;
    }

    /** Tổng số bản ghi khớp bộ lọc (để tính số trang). */
    public int count(String keyword, String fromDate, String toDate) {
        String sql = "SELECT COUNT(*) FROM staff_audit_logs" + buildWhere(keyword, fromDate, toDate);
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            bindFilters(ps, keyword, fromDate, toDate);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** Một trang kết quả: ORDER BY ... OFFSET ? ROWS FETCH NEXT ? ROWS ONLY. */
    public List<StaffAuditLogs> searchPaged(String keyword, String fromDate, String toDate, int offset, int pageSize) {
        List<StaffAuditLogs> list = new ArrayList<>();
        String sql = "SELECT * FROM staff_audit_logs" + buildWhere(keyword, fromDate, toDate)
                   + "ORDER BY created_at DESC, log_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = bindFilters(ps, keyword, fromDate, toDate);
            ps.setInt(i++, offset);
            ps.setInt(i, pageSize);
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