package khanhnb08894.dao;

import fourSome.model.CompatibilityConflictLogs;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompatibilityConflictLogsDAO {

    public List<CompatibilityConflictLogs> getAll() {
        List<CompatibilityConflictLogs> list = new ArrayList<>();
        String sql = "SELECT * FROM [compatibility_conflict_logs]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CompatibilityConflictLogs obj = new CompatibilityConflictLogs();
                obj.setLogId(rs.getInt("log_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setBuildId(rs.getInt("build_id"));
                obj.setRuleId(rs.getInt("rule_id"));
                obj.setCpuId(rs.getInt("cpu_id"));
                obj.setMainboardId(rs.getInt("mainboard_id"));
                obj.setVgaId(rs.getInt("vga_id"));
                obj.setPsuId(rs.getInt("psu_id"));
                obj.setCaseId(rs.getInt("case_id"));
                obj.setConflictReason(rs.getString("conflict_reason"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
