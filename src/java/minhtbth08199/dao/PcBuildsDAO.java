package minhtbth08199.dao;

import fourSome.model.PcBuilds;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PcBuildsDAO {

    public List<PcBuilds> getAll() {
        List<PcBuilds> list = new ArrayList<>();
        String sql = "SELECT * FROM [pc_builds]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PcBuilds obj = new PcBuilds();
                obj.setBuildId(rs.getInt("build_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setBuildName(rs.getString("build_name"));
                obj.setShareToken(rs.getString("share_token"));
                obj.setProfileId(rs.getInt("profile_id"));
                obj.setRequirementId(rs.getInt("requirement_id"));
                obj.setEstimatedBudget(rs.getDouble("estimated_budget"));
                obj.setTotalPrice(rs.getDouble("total_price"));
                obj.setCalculatedWatts(rs.getInt("calculated_watts"));
                obj.setIsCompatible(rs.getBoolean("is_compatible"));
                obj.setIsPresetRecommendation(rs.getBoolean("is_preset_recommendation"));
                obj.setRecommendationType(rs.getString("recommendation_type"));
                obj.setStatus(rs.getString("status"));
                obj.setCopiedFromBuildId(rs.getInt("copied_from_build_id"));
                obj.setPerformanceScore(rs.getInt("performance_score"));
                obj.setReviewRating(rs.getInt("review_rating"));
                obj.setReviewComment(rs.getString("review_comment"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ================= Build PC (UC 71, 85, 94): cấu hình nháp =================

    /** Tạo cấu hình nháp (DRAFT) cho user, trả về build_id (0 nếu lỗi). share_token sinh ngẫu nhiên, không đoán được. */
    public int createDraft(int userId, String name) {
        String sql = "INSERT INTO pc_builds (user_id, build_name, share_token, total_price, is_compatible, is_preset_recommendation, status, created_at) "
                   + "VALUES (?, ?, ?, 0, 1, 0, 'DRAFT', SYSDATETIME())";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, linhhqth08598.dao.PcBuildShareDAO.newToken());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    /** Cấu hình nháp mới nhất của user (0 nếu chưa có). */
    public int findLatestDraft(int userId) {
        String sql = "SELECT TOP 1 build_id FROM pc_builds WHERE user_id = ? AND status = 'DRAFT' ORDER BY created_at DESC, build_id DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    /** Cấu hình có thuộc user và còn chỉnh sửa được (DRAFT/SAVED) không? */
    public boolean isEditableOwner(int buildId, int userId) {
        String sql = "SELECT COUNT(*) FROM pc_builds WHERE build_id = ? AND user_id = ? AND status IN ('DRAFT','SAVED')";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buildId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public String getName(int buildId) {
        String sql = "SELECT build_name FROM pc_builds WHERE build_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buildId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
