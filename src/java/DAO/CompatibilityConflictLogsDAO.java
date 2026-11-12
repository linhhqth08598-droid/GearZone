package DAO;

import Models.CompatibilityConflictLogs;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompatibilityConflictLogsDAO {

    public List<CompatibilityConflictLogs> getAll() {
        List<CompatibilityConflictLogs> list = new ArrayList<>();
        String sql = "SELECT * FROM [compatibility_conflict_logs]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CompatibilityConflictLogs ccl = new CompatibilityConflictLogs();
                    ccl.setLogId(rs.getInt("log_id"));
                    ccl.setUserId(rs.getInt("user_id"));
                    ccl.setBuildId(rs.getInt("build_id"));
                    ccl.setRuleId(rs.getInt("rule_id"));
                    ccl.setCpuId(rs.getInt("cpu_id"));
                    ccl.setMainboardId(rs.getInt("mainboard_id"));
                    ccl.setVgaId(rs.getInt("vga_id"));
                    ccl.setPsuId(rs.getInt("psu_id"));
                    ccl.setCaseId(rs.getInt("case_id"));
                    ccl.setConflictReason(rs.getString("conflict_reason"));
                    ccl.setCreatedAt(rs.getDate("created_at"));
                    ccl.setStatus(rs.getString("status"));
                    list.add(ccl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
