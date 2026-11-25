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
                list.add(mapRow(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<UpgradeRecommendations> findByCurrentPcId(int currentPcId) {
        List<UpgradeRecommendations> list = new ArrayList<>();
        String sql = "SELECT * FROM [upgrade_recommendations] WHERE current_pc_id = ? AND status = 'ACTIVE' ORDER BY recommendation_id ASC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, currentPcId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean replaceRecommendations(int currentPcId, List<UpgradeRecommendations> items) {
        String delSql = "DELETE FROM [upgrade_recommendations] WHERE current_pc_id = ?";
        String insSql = "INSERT INTO [upgrade_recommendations] (current_pc_id, bottleneck_component, replace_part_id, "
                      + "suggested_part_id, plan_type, estimated_performance_gain_percent, estimated_cost, "
                      + "is_selected, after_build_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement del = con.prepareStatement(delSql)) {
                    del.setInt(1, currentPcId);
                    del.executeUpdate();
                }
                if (items != null && !items.isEmpty()) {
                    try (PreparedStatement ins = con.prepareStatement(insSql)) {
                        for (UpgradeRecommendations item : items) {
                            ins.setInt(1, currentPcId);
                            ins.setString(2, item.getBottleneckComponent());
                            setNullableInt(ins, 3, item.getReplacePartId());
                            ins.setInt(4, item.getSuggestedPartId());
                            ins.setString(5, item.getPlanType());
                            if (item.getEstimatedPerformanceGainPercent() == null) {
                                ins.setNull(6, Types.DECIMAL);
                            } else {
                                ins.setDouble(6, item.getEstimatedPerformanceGainPercent());
                            }
                            ins.setDouble(7, item.getEstimatedCost() != null ? item.getEstimatedCost() : 0.0);
                            ins.setBoolean(8, Boolean.TRUE.equals(item.getIsSelected()));
                            setNullableInt(ins, 9, item.getAfterBuildId());
                            ins.addBatch();
                        }
                        ins.executeBatch();
                    }
                }
                con.commit();
                return true;
            } catch (Exception ex) {
                con.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean markSelected(int recommendationId, int currentPcId, boolean selected, Integer afterBuildId) {
        String sql = "UPDATE [upgrade_recommendations] SET is_selected = ?, after_build_id = ? "
                   + "WHERE recommendation_id = ? AND current_pc_id = ? AND status = 'ACTIVE'";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, selected);
            setNullableInt(ps, 2, afterBuildId);
            ps.setInt(3, recommendationId);
            ps.setInt(4, currentPcId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private UpgradeRecommendations mapRow(ResultSet rs) throws SQLException {
        UpgradeRecommendations obj = new UpgradeRecommendations();
        obj.setRecommendationId(rs.getInt("recommendation_id"));
        obj.setCurrentPcId(rs.getInt("current_pc_id"));
        obj.setBottleneckComponent(rs.getString("bottleneck_component"));
        obj.setReplacePartId(getNullableInt(rs, "replace_part_id"));
        obj.setSuggestedPartId(rs.getInt("suggested_part_id"));
        obj.setPlanType(rs.getString("plan_type"));
        double gain = rs.getDouble("estimated_performance_gain_percent");
        obj.setEstimatedPerformanceGainPercent(rs.wasNull() ? null : gain);
        obj.setEstimatedCost(rs.getDouble("estimated_cost"));
        obj.setIsSelected(rs.getBoolean("is_selected"));
        obj.setAfterBuildId(getNullableInt(rs, "after_build_id"));
        obj.setStatus(rs.getString("status"));
        return obj;
    }

    private void setNullableInt(PreparedStatement ps, int idx, Integer val) throws SQLException {
        if (val == null || val <= 0) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, val);
    }

    private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }
}
