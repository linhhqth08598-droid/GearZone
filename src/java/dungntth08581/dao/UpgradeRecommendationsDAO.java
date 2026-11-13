package dungntth08581.dao;

import fourSome.model.UpgradeRecommendations;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UpgradeRecommendationsDAO {

    public List<UpgradeRecommendations> getAll() {
        List<UpgradeRecommendations> list = new ArrayList<>();
        String sql = "SELECT * FROM [upgrade_recommendations]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UpgradeRecommendations obj = new UpgradeRecommendations();
                obj.setRecommendationId(rs.getInt("recommendation_id"));
                obj.setCurrentPcId(rs.getInt("current_pc_id"));
                obj.setBottleneckComponent(rs.getString("bottleneck_component"));
                obj.setReplacePartId(rs.getInt("replace_part_id"));
                obj.setSuggestedPartId(rs.getInt("suggested_part_id"));
                obj.setPlanType(rs.getString("plan_type"));
                obj.setEstimatedPerformanceGainPercent(rs.getDouble("estimated_performance_gain_percent"));
                obj.setEstimatedCost(rs.getDouble("estimated_cost"));
                obj.setIsSelected(rs.getBoolean("is_selected"));
                obj.setAfterBuildId(rs.getInt("after_build_id"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
