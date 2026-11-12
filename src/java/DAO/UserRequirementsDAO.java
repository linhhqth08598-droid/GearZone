package DAO;

import Models.UserRequirements;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRequirementsDAO {

    public List<UserRequirements> getAll() {
        List<UserRequirements> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_requirements]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserRequirements ur = new UserRequirements();
                    ur.setRequirementId(rs.getInt("requirement_id"));
                    ur.setUserId(rs.getInt("user_id"));
                    ur.setProfileId(rs.getInt("profile_id"));
                    ur.setTargetResolution(rs.getString("target_resolution"));
                    ur.setTargetFps(rs.getString("target_fps"));
                    ur.setGraphicsPreset(rs.getString("graphics_preset"));
                    ur.setBudget(rs.getDouble("budget"));
                    ur.setCpuPriority(rs.getInt("cpu_priority"));
                    ur.setGpuPriority(rs.getInt("gpu_priority"));
                    ur.setDesiredRamGb(rs.getInt("desired_ram_gb"));
                    ur.setDesiredStorageGb(rs.getInt("desired_storage_gb"));
                    ur.setDesignStyle(rs.getString("design_style"));
                    ur.setWantsRgb(rs.getBoolean("wants_rgb"));
                    ur.setOptimizeCost(rs.getBoolean("optimize_cost"));
                    ur.setAnalysisNote(rs.getString("analysis_note"));
                    ur.setAnalyzedBy(rs.getInt("analyzed_by"));
                    ur.setCreatedAt(rs.getDate("created_at"));
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
