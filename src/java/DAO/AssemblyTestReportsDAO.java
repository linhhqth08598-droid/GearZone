package DAO;

import Models.AssemblyTestReports;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssemblyTestReportsDAO {

    public List<AssemblyTestReports> getAll() {
        List<AssemblyTestReports> list = new ArrayList<>();
        String sql = "SELECT * FROM [assembly_test_reports]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssemblyTestReports atr = new AssemblyTestReports();
                    atr.setReportId(rs.getInt("report_id"));
                    atr.setAssemblyId(rs.getInt("assembly_id"));
                    atr.setBootTestPassed(rs.getBoolean("boot_test_passed"));
                    atr.setCpuIdleTempC(rs.getInt("cpu_idle_temp_c"));
                    atr.setCpuLoadTempC(rs.getInt("cpu_load_temp_c"));
                    atr.setGpuIdleTempC(rs.getInt("gpu_idle_temp_c"));
                    atr.setGpuLoadTempC(rs.getInt("gpu_load_temp_c"));
                    atr.setCinebenchScore(rs.getInt("cinebench_score"));
                    atr.set_3dmarkScore(rs.getInt("3dmark_score"));
                    atr.setStabilityTestPassed(rs.getBoolean("stability_test_passed"));
                    atr.setStabilityTestMinutes(rs.getInt("stability_test_minutes"));
                    atr.setNote(rs.getString("note"));
                    atr.setFinalResult(rs.getString("final_result"));
                    atr.setCompletedAt(rs.getDate("completed_at"));
                    atr.setStatus(rs.getString("status"));
                    list.add(atr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
