package DAO;

import Models.PcBuilds;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PcBuildsDAO {

    public List<PcBuilds> getAll() {
        List<PcBuilds> list = new ArrayList<>();
        String sql = "SELECT * FROM [pc_builds]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PcBuilds pb = new PcBuilds();
                    pb.setBuildId(rs.getInt("build_id"));
                    pb.setUserId(rs.getInt("user_id"));
                    pb.setBuildName(rs.getString("build_name"));
                    pb.setShareToken(rs.getString("share_token"));
                    pb.setProfileId(rs.getInt("profile_id"));
                    pb.setRequirementId(rs.getInt("requirement_id"));
                    pb.setEstimatedBudget(rs.getDouble("estimated_budget"));
                    pb.setTotalPrice(rs.getDouble("total_price"));
                    pb.setCalculatedWatts(rs.getInt("calculated_watts"));
                    pb.setIsCompatible(rs.getBoolean("is_compatible"));
                    pb.setIsPresetRecommendation(rs.getBoolean("is_preset_recommendation"));
                    pb.setRecommendationType(rs.getString("recommendation_type"));
                    pb.setStatus(rs.getString("status"));
                    pb.setCopiedFromBuildId(rs.getInt("copied_from_build_id"));
                    pb.setPerformanceScore(rs.getInt("performance_score"));
                    pb.setReviewRating(rs.getInt("review_rating"));
                    pb.setReviewComment(rs.getString("review_comment"));
                    pb.setCreatedAt(rs.getDate("created_at"));
                    list.add(pb);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
