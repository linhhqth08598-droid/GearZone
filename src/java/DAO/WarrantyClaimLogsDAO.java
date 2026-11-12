package DAO;

import Models.WarrantyClaimLogs;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarrantyClaimLogsDAO {

    public List<WarrantyClaimLogs> getAll() {
        List<WarrantyClaimLogs> list = new ArrayList<>();
        String sql = "SELECT * FROM [warranty_claim_logs]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WarrantyClaimLogs wcl = new WarrantyClaimLogs();
                    wcl.setLogId(rs.getInt("log_id"));
                    wcl.setClaimId(rs.getInt("claim_id"));
                    wcl.setStatus(rs.getString("status"));
                    wcl.setNote(rs.getString("note"));
                    wcl.setUpdatedBy(rs.getInt("updated_by"));
                    wcl.setCreatedAt(rs.getDate("created_at"));
                    list.add(wcl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
