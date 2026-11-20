package dungntth08581.dao;

import fourSome.model.WarrantyClaims;
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

public class WarrantyClaimsDAO {

    private WarrantyClaims mapRow(ResultSet rs) throws SQLException {
        WarrantyClaims c = new WarrantyClaims();
        c.setClaimId(rs.getInt("claim_id"));
        c.setWarrantyId(rs.getInt("warranty_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setFaultDescription(rs.getString("fault_description"));
        c.setStatus(rs.getString("status"));
        c.setRejectionReason(rs.getString("rejection_reason"));
        c.setReceivedCondition(rs.getString("received_condition"));
        c.setServiceCenterName(rs.getString("service_center_name"));
        int handledBy = rs.getInt("handled_by");
        c.setHandledBy(rs.wasNull() ? null : handledBy);
        c.setResolutionNote(rs.getString("resolution_note"));
        c.setReplacementSerialNumber(rs.getString("replacement_serial_number"));
        c.setCompletedAt(rs.getTimestamp("completed_at"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }

    public List<WarrantyClaims> getAll() {
        List<WarrantyClaims> list = new ArrayList<>();
        String sql = "SELECT * FROM warranty_claims ORDER BY created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public WarrantyClaims findById(int claimId) {
        String sql = "SELECT * FROM warranty_claims WHERE claim_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, claimId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasActiveClaim(int warrantyId) {
        String sql = "SELECT COUNT(*) FROM warranty_claims "
                + "WHERE warranty_id = ? AND status NOT IN ('COMPLETED', 'REJECTED', 'CANCELLED')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warrantyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy danh sách yêu cầu bảo hành kèm thông tin chi tiết cho Admin hoặc User (UC 287)
     */
    public List<HashMap<String, Object>> findWithDetails(Integer userId, String statusFilter) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, w.warranty_code, w.serial_number, w.start_date, w.end_date, "
                + "p.part_name, p.product_code, u.full_name AS customer_name, u.phone_number AS customer_phone, "
                + "s.full_name AS handler_name "
                + "FROM warranty_claims c "
                + "JOIN e_warranties w ON c.warranty_id = w.warranty_id "
                + "JOIN parts p ON w.part_id = p.part_id "
                + "JOIN users u ON c.user_id = u.user_id "
                + "LEFT JOIN users s ON c.handled_by = s.user_id "
                + "WHERE 1=1 "
        );
        if (userId != null) sql.append("AND c.user_id = ? ");
        if (statusFilter != null && !statusFilter.trim().isEmpty()) sql.append("AND c.status = ? ");
        sql.append("ORDER BY c.created_at DESC");

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (userId != null) ps.setInt(idx++, userId);
            if (statusFilter != null && !statusFilter.trim().isEmpty()) ps.setString(idx++, statusFilter.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("claimId", rs.getInt("claim_id"));
                    row.put("warrantyId", rs.getInt("warranty_id"));
                    row.put("warrantyCode", rs.getString("warranty_code"));
                    row.put("serialNumber", rs.getString("serial_number"));
                    row.put("startDate", rs.getTimestamp("start_date"));
                    row.put("endDate", rs.getTimestamp("end_date"));
                    row.put("partName", rs.getString("part_name"));
                    row.put("productCode", rs.getString("product_code"));
                    row.put("userId", rs.getInt("user_id"));
                    row.put("customerName", rs.getString("customer_name"));
                    row.put("customerPhone", rs.getString("customer_phone"));
                    row.put("faultDescription", rs.getString("fault_description"));
                    row.put("status", rs.getString("status"));
                    row.put("rejectionReason", rs.getString("rejection_reason"));
                    row.put("receivedCondition", rs.getString("received_condition"));
                    row.put("serviceCenterName", rs.getString("service_center_name"));
                    int hId = rs.getInt("handled_by");
                    row.put("handledBy", rs.wasNull() ? null : hId);
                    row.put("handlerName", rs.getString("handler_name"));
                    row.put("resolutionNote", rs.getString("resolution_note"));
                    row.put("replacementSerialNumber", rs.getString("replacement_serial_number"));
                    row.put("completedAt", rs.getTimestamp("completed_at"));
                    row.put("createdAt", rs.getTimestamp("created_at"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(Connection conn, WarrantyClaims c) throws SQLException {
        String sql = "INSERT INTO warranty_claims "
                + "(warranty_id, user_id, fault_description, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getWarrantyId());
            ps.setInt(2, c.getUserId());
            ps.setString(3, c.getFaultDescription());
            ps.setString(4, c.getStatus());
            ps.setTimestamp(5, new Timestamp(c.getCreatedAt().getTime()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean update(Connection conn, WarrantyClaims c) throws SQLException {
        String sql = "UPDATE warranty_claims SET "
                + "status = ?, rejection_reason = ?, received_condition = ?, "
                + "service_center_name = ?, handled_by = ?, resolution_note = ?, "
                + "replacement_serial_number = ?, completed_at = ? "
                + "WHERE claim_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getStatus());
            ps.setString(2, c.getRejectionReason());
            ps.setString(3, c.getReceivedCondition());
            ps.setString(4, c.getServiceCenterName());
            if (c.getHandledBy() != null) ps.setInt(5, c.getHandledBy());
            else ps.setNull(5, Types.INTEGER);
            ps.setString(6, c.getResolutionNote());
            ps.setString(7, c.getReplacementSerialNumber());
            if (c.getCompletedAt() != null) ps.setTimestamp(8, new Timestamp(c.getCompletedAt().getTime()));
            else ps.setNull(8, Types.TIMESTAMP);
            ps.setInt(9, c.getClaimId());
            return ps.executeUpdate() > 0;
        }
    }
}