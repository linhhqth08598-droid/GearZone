package DAO;

import Models.UpgradeRecommendations;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpgradeRecommendationsDAO {

    public List<UpgradeRecommendations> getAll() {
        List<UpgradeRecommendations> list = new ArrayList<>();
        String sql = "SELECT * FROM [upgrade_recommendations]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UpgradeRecommendations ur = new UpgradeRecommendations();
                    ur.setRecommendationId(rs.getInt("recommendation_id"));
                    ur.setCurrentPcId(rs.getInt("current_pc_id"));
                    ur.setBottleneckComponent(rs.getString("bottleneck_component"));
                    ur.setReplacePartId(rs.getInt("replace_part_id"));
                    ur.setSuggestedPartId(rs.getInt("suggested_part_id"));
                    ur.setPlanType(rs.getString("plan_type"));
                    ur.setEstimatedPerformanceGainPercent(rs.getDouble("estimated_performance_gain_percent"));
                    ur.setEstimatedCost(rs.getDouble("estimated_cost"));
                    ur.setIsSelected(rs.getBoolean("is_selected"));
                    ur.setAfterBuildId(rs.getInt("after_build_id"));
                    ur.setStatus(rs.getString("status"));
                    list.add(ur);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
