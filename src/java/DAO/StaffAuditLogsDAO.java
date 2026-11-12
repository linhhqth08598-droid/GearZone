package DAO;

import Models.StaffAuditLogs;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffAuditLogsDAO {

    public List<StaffAuditLogs> getAll() {
        List<StaffAuditLogs> list = new ArrayList<>();
        String sql = "SELECT * FROM [staff_audit_logs]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffAuditLogs sal = new StaffAuditLogs();
                    sal.setLogId(rs.getInt("log_id"));
                    sal.setStaffId(rs.getInt("staff_id"));
                    sal.setAction(rs.getString("action"));
                    sal.setTargetTable(rs.getString("target_table"));
                    sal.setTargetId(rs.getInt("target_id"));
                    sal.setDescription(rs.getString("description"));
                    sal.setCreatedAt(rs.getDate("created_at"));
                    sal.setStatus(rs.getString("status"));
                    list.add(sal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
