package linhhqth08598.dao;

import model.StaffAuditLogs;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffAuditLogsDAO {

    public List<StaffAuditLogs> getAll() {
        List<StaffAuditLogs> list = new ArrayList<>();
        String sql = "SELECT * FROM [staff_audit_logs]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StaffAuditLogs obj = new StaffAuditLogs();
                obj.setLogId(rs.getInt("log_id"));
                obj.setStaffId(rs.getInt("staff_id"));
                obj.setAction(rs.getString("action"));
                obj.setTargetTable(rs.getString("target_table"));
                obj.setTargetId(rs.getInt("target_id"));
                obj.setDescription(rs.getString("description"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
