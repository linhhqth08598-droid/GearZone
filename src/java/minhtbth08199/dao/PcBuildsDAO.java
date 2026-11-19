package minhtbth08199.dao;

import fourSome.model.PcBuilds;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PcBuildsDAO {

    private PcBuilds map(ResultSet rs) throws SQLException {
        PcBuilds obj = new PcBuilds();
        obj.setBuildId(rs.getInt("build_id"));
        int userId = rs.getInt("user_id"); obj.setUserId(rs.wasNull() ? null : userId);
        obj.setBuildName(rs.getString("build_name"));
        obj.setShareToken(rs.getString("share_token"));
        obj.setTotalPrice(rs.getDouble("total_price"));
        obj.setIsCompatible(rs.getBoolean("is_compatible"));
        obj.setStatus(rs.getString("status"));
        obj.setCreatedAt(rs.getTimestamp("created_at"));
        return obj;
    }

    public List<PcBuilds> getAll() {
        List<PcBuilds> list = new ArrayList<>();
        String sql = "SELECT * FROM [pc_builds]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public PcBuilds findById(int buildId) {
        String sql = "SELECT * FROM pc_builds WHERE build_id = ? AND status <> 'DELETED'";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buildId); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        } catch (SQLException e) { throw new IllegalStateException("Không thể đọc cấu hình", e); }
    }

    public PcBuilds findByShareToken(String token) {
        String sql = "SELECT * FROM pc_builds WHERE share_token = ? AND status <> 'DELETED'";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, token); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        } catch (SQLException e) { throw new IllegalStateException("Không thể đọc cấu hình chia sẻ", e); }
    }

    public int create(Connection con, Integer userId, String name, String token) throws SQLException {
        String sql = "INSERT INTO pc_builds (user_id, build_name, share_token, total_price, is_compatible, status) VALUES (?, ?, ?, 0, 1, 'DRAFT')";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (userId == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, userId);
            ps.setString(2, name); ps.setString(3, token); ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) return keys.getInt(1); }
        }
        throw new SQLException("Không nhận được build_id mới");
    }

    public boolean updateTotal(Connection con, int buildId) throws SQLException {
        String sql = "UPDATE pc_builds SET total_price = COALESCE((SELECT SUM(quantity * unit_price) FROM pc_build_details WHERE build_id = ? AND status = 'ACTIVE'), 0) WHERE build_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) { ps.setInt(1, buildId); ps.setInt(2, buildId); return ps.executeUpdate() == 1; }
    }

    public boolean updateCompatibility(int buildId, boolean compatible) {
        String sql = "UPDATE pc_builds SET is_compatible = ? WHERE build_id = ? AND status <> 'DELETED'";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, compatible);
            ps.setInt(2, buildId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Không thể cập nhật trạng thái tương thích", e);
        }
    }

    public boolean softDelete(int buildId, Integer userId) {
        String sql = "UPDATE pc_builds SET status = 'DELETED' WHERE build_id = ? AND ((user_id = ?) OR (? IS NULL AND user_id IS NULL))";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buildId); if (userId == null) { ps.setNull(2, Types.INTEGER); ps.setNull(3, Types.INTEGER); } else { ps.setInt(2, userId); ps.setInt(3, userId); }
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { throw new IllegalStateException("Không thể xóa cấu hình", e); }
    }
}
