package linhhqth08598.dao;

import fourSome.model.PcBuildDetailItem;
import fourSome.model.PcBuildHistoryItem;
import fourSome.url.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * UC 16 - Lưu cấu hình PC | UC 17 - Chia sẻ cấu hình qua link (share_token).
 * Chỉ thao tác trên bảng pc_builds / pc_build_details. Phần tạo cấu hình (Build PC) là của minhtbth08199.
 */
public class PcBuildShareDAO {

    public static String newToken() {
        // 2 UUID ngẫu nhiên (SecureRandom) ghép lại = 64 ký tự hex, không đoán được
        return (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "");
    }

    private PcBuildHistoryItem map(ResultSet rs) throws java.sql.SQLException {
        PcBuildHistoryItem item = new PcBuildHistoryItem();
        item.setBuildId(rs.getInt("build_id"));
        item.setBuildName(rs.getString("build_name"));
        item.setTotalPrice(rs.getDouble("total_price"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        item.setStatus(rs.getString("status"));
        item.setShareToken(rs.getString("share_token"));
        return item;
    }

    public List<PcBuildDetailItem> getDetails(int buildId) {
        List<PcBuildDetailItem> list = new ArrayList<>();
        String sql = "SELECT p.part_name, bd.quantity, bd.unit_price FROM pc_build_details bd "
                   + "JOIN parts p ON bd.part_id = p.part_id WHERE bd.build_id = ? AND bd.status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, buildId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PcBuildDetailItem d = new PcBuildDetailItem();
                    d.setPartName(rs.getString("part_name"));
                    d.setQuantity(rs.getInt("quantity"));
                    d.setUnitPrice(rs.getDouble("unit_price"));
                    list.add(d);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Cấu hình thuộc về user (DRAFT hoặc SAVED). */
    public PcBuildHistoryItem findOwned(int buildId, int userId) {
        String sql = "SELECT build_id, build_name, total_price, created_at, status, share_token FROM pc_builds "
                   + "WHERE build_id = ? AND user_id = ? AND status IN ('DRAFT','SAVED')";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, buildId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PcBuildHistoryItem item = map(rs);
                    item.setDetails(getDetails(item.getBuildId()));
                    return item;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Tìm cấu hình đã LƯU theo token chia sẻ (xem công khai, chỉ đọc). */
    public PcBuildHistoryItem findByToken(String token) {
        String sql = "SELECT build_id, build_name, total_price, created_at, status, share_token FROM pc_builds "
                   + "WHERE share_token = ? AND status = 'SAVED'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PcBuildHistoryItem item = map(rs);
                    item.setDetails(getDetails(item.getBuildId()));
                    return item;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public String getOwnerName(int buildId) {
        String sql = "SELECT u.full_name FROM pc_builds b LEFT JOIN users u ON u.user_id = b.user_id WHERE b.build_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, buildId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Lưu cấu hình: DRAFT -> SAVED kèm đặt tên (chỉ áp dụng cho cấu hình đã có ít nhất 1 linh kiện đang ACTIVE). */
    public boolean saveBuild(int buildId, int userId, String name) {
        String sql = "UPDATE pc_builds SET status = 'SAVED', build_name = ? "
                   + "WHERE build_id = ? AND user_id = ? AND status = 'DRAFT' "
                   + "AND EXISTS (SELECT 1 FROM pc_build_details WHERE build_id = pc_builds.build_id AND status = 'ACTIVE')";
        return exec(sql, name, buildId, userId);
    }

    public boolean rename(int buildId, int userId, String name) {
        String sql = "UPDATE pc_builds SET build_name = ? WHERE build_id = ? AND user_id = ? AND status IN ('DRAFT','SAVED')";
        return exec(sql, name, buildId, userId);
    }

    /** Xóa mềm (không xóa cứng vì còn liên kết với đơn lắp ráp, log tương thích...). */
    public boolean softDelete(int buildId, int userId) {
        String sql = "UPDATE pc_builds SET status = 'DELETED' WHERE build_id = ? AND user_id = ? AND status IN ('DRAFT','SAVED')";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, buildId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Tạo link mới - link cũ lập tức vô hiệu (chỉ cho cấu hình đã SAVED). */
    public String regenerateToken(int buildId, int userId) {
        String token = newToken();
        String sql = "UPDATE pc_builds SET share_token = ? WHERE build_id = ? AND user_id = ? AND status = 'SAVED'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, buildId);
            ps.setInt(3, userId);
            if (ps.executeUpdate() > 0) return token;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Sao chép cấu hình (của mình hoặc từ link chia sẻ) thành bản SAVED mới của newUserId. Trả về build_id mới (0 nếu lỗi). */
    public int duplicate(int sourceBuildId, int newUserId, String newName) {
        String insBuild =
            "INSERT INTO pc_builds (user_id, build_name, share_token, profile_id, requirement_id, estimated_budget, total_price, "
          + "calculated_watts, is_compatible, is_preset_recommendation, recommendation_type, status, copied_from_build_id, performance_score, created_at) "
          + "OUTPUT INSERTED.build_id "
          + "SELECT ?, ?, ?, profile_id, requirement_id, estimated_budget, total_price, calculated_watts, is_compatible, 0, NULL, 'SAVED', build_id, performance_score, GETDATE() "
          + "FROM pc_builds WHERE build_id = ? AND status IN ('DRAFT','SAVED')";
        String insDetail =
            "INSERT INTO pc_build_details (build_id, part_id, quantity, unit_price, status) "
          + "SELECT ?, part_id, quantity, unit_price, status FROM pc_build_details WHERE build_id = ? AND status = 'ACTIVE'";

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int newId = 0;
                try (PreparedStatement ps = conn.prepareStatement(insBuild)) {
                    ps.setInt(1, newUserId);
                    ps.setString(2, newName);
                    ps.setString(3, newToken());
                    ps.setInt(4, sourceBuildId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) newId = rs.getInt(1);
                    }
                }
                if (newId == 0) { conn.rollback(); return 0; }
                try (PreparedStatement ps = conn.prepareStatement(insDetail)) {
                    ps.setInt(1, newId);
                    ps.setInt(2, sourceBuildId);
                    ps.executeUpdate();
                }
                conn.commit();
                return newId;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private boolean exec(String sql, String name, int buildId, int userId) {
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, buildId);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
