package DAO;

import Models.WarrantyClaims;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarrantyClaimsDAO {

    public List<WarrantyClaims> getAll() {
        List<WarrantyClaims> list = new ArrayList<>();
        String sql = "SELECT * FROM [warranty_claims]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WarrantyClaims wc = new WarrantyClaims();
                    wc.setClaimId(rs.getInt("claim_id"));
                    wc.setWarrantyId(rs.getInt("warranty_id"));
                    wc.setUserId(rs.getInt("user_id"));
                    wc.setFaultDescription(rs.getString("fault_description"));
                    wc.setStatus(rs.getString("status"));
                    wc.setRejectionReason(rs.getString("rejection_reason"));
                    wc.setReceivedCondition(rs.getString("received_condition"));
                    wc.setServiceCenterName(rs.getString("service_center_name"));
                    wc.setHandledBy(rs.getInt("handled_by"));
                    wc.setResolutionNote(rs.getString("resolution_note"));
                    wc.setReplacementSerialNumber(rs.getString("replacement_serial_number"));
                    wc.setCompletedAt(rs.getDate("completed_at"));
                    wc.setCreatedAt(rs.getDate("created_at"));
                    list.add(wc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
