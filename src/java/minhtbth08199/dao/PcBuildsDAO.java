package minhtbth08199.dao;

import model.PcBuilds;
import url.DBConnect;
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
}
