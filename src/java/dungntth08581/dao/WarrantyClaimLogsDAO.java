package dungntth08581.dao;

import fourSome.model.WarrantyClaimLogs;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarrantyClaimLogsDAO {

    public List<WarrantyClaimLogs> getAll() {
        List<WarrantyClaimLogs> list = new ArrayList<>();
        String sql = "SELECT * FROM [warranty_claim_logs]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                WarrantyClaimLogs obj = new WarrantyClaimLogs();
                obj.setLogId(rs.getInt("log_id"));
                obj.setClaimId(rs.getInt("claim_id"));
                obj.setStatus(rs.getString("status"));
                obj.setNote(rs.getString("note"));
                obj.setUpdatedBy(rs.getInt("updated_by"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
