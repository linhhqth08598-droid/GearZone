package dungntth08581.dao;

import fourSome.model.WarrantyClaimLogs;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarrantyClaimLogsDAO {

    private WarrantyClaimLogs mapRow(ResultSet rs) throws SQLException {
        WarrantyClaimLogs log = new WarrantyClaimLogs();
        log.setLogId(rs.getInt("log_id"));
        log.setClaimId(rs.getInt("claim_id"));
        log.setStatus(rs.getString("status"));
        log.setNote(rs.getString("note"));
        int updatedBy = rs.getInt("updated_by");
        log.setUpdatedBy(rs.wasNull() ? null : updatedBy);
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }

    public List<WarrantyClaimLogs> getAll() {
        List<WarrantyClaimLogs> list = new ArrayList<>();
        String sql = "SELECT * FROM warranty_claim_logs ORDER BY created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<HashMap<String, Object>> findByClaimIdWithUser(int claimId) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        String sql = "SELECT l.*, u.full_name AS updater_name "
                + "FROM warranty_claim_logs l "
                + "LEFT JOIN users u ON l.updated_by = u.user_id "
                + "WHERE l.claim_id = ? "
                + "ORDER BY l.created_at ASC, l.log_id ASC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, claimId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("logId", rs.getInt("log_id"));
                    row.put("claimId", rs.getInt("claim_id"));
                    row.put("status", rs.getString("status"));
                    row.put("note", rs.getString("note"));
                    int uId = rs.getInt("updated_by");
                    row.put("updatedBy", rs.wasNull() ? null : uId);
                    row.put("updaterName", rs.getString("updater_name"));
                    row.put("createdAt", rs.getTimestamp("created_at"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(Connection conn, WarrantyClaimLogs log) throws SQLException {
        String sql = "INSERT INTO warranty_claim_logs (claim_id, status, note, updated_by, created_at) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, log.getClaimId());
            ps.setString(2, log.getStatus());
            ps.setString(3, log.getNote());
            if (log.getUpdatedBy() != null) ps.setInt(4, log.getUpdatedBy());
            else ps.setNull(4, Types.INTEGER);
            ps.setTimestamp(5, new Timestamp(log.getCreatedAt().getTime()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }
}