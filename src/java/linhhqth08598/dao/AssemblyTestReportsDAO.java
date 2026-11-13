package linhhqth08598.dao;

import model.AssemblyTestReports;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssemblyTestReportsDAO {

    public List<AssemblyTestReports> getAll() {
        List<AssemblyTestReports> list = new ArrayList<>();
        String sql = "SELECT * FROM [assembly_test_reports]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AssemblyTestReports obj = new AssemblyTestReports();
                obj.setReportId(rs.getInt("report_id"));
                obj.setAssemblyId(rs.getInt("assembly_id"));
                obj.setBootTestPassed(rs.getBoolean("boot_test_passed"));
                obj.setCpuIdleTempC(rs.getInt("cpu_idle_temp_c"));
                obj.setCpuLoadTempC(rs.getInt("cpu_load_temp_c"));
                obj.setGpuIdleTempC(rs.getInt("gpu_idle_temp_c"));
                obj.setGpuLoadTempC(rs.getInt("gpu_load_temp_c"));
                obj.setCinebenchScore(rs.getInt("cinebench_score"));
                obj.set_3dmarkScore(rs.getInt("3dmark_score"));
                obj.setStabilityTestPassed(rs.getBoolean("stability_test_passed"));
                obj.setStabilityTestMinutes(rs.getInt("stability_test_minutes"));
                obj.setNote(rs.getString("note"));
                obj.setFinalResult(rs.getString("final_result"));
                obj.setCompletedAt(rs.getDate("completed_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
