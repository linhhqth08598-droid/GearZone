package minhtbth08199.dao;

import fourSome.model.UserRequirements;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRequirementsDAO {

    public List<UserRequirements> getAll() {
        List<UserRequirements> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_requirements]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserRequirements obj = new UserRequirements();
                obj.setRequirementId(rs.getInt("requirement_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setProfileId(rs.getInt("profile_id"));
                obj.setTargetResolution(rs.getString("target_resolution"));
                obj.setTargetFps(rs.getString("target_fps"));
                obj.setGraphicsPreset(rs.getString("graphics_preset"));
                obj.setBudget(rs.getDouble("budget"));
                obj.setCpuPriority(rs.getInt("cpu_priority"));
                obj.setGpuPriority(rs.getInt("gpu_priority"));
                obj.setDesiredRamGb(rs.getInt("desired_ram_gb"));
                obj.setDesiredStorageGb(rs.getInt("desired_storage_gb"));
                obj.setDesignStyle(rs.getString("design_style"));
                obj.setWantsRgb(rs.getBoolean("wants_rgb"));
                obj.setOptimizeCost(rs.getBoolean("optimize_cost"));
                obj.setAnalysisNote(rs.getString("analysis_note"));
                obj.setAnalyzedBy(rs.getInt("analyzed_by"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
