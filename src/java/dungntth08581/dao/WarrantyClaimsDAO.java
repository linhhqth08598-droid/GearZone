package dungntth08581.dao;

import model.WarrantyClaims;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarrantyClaimsDAO {

    public List<WarrantyClaims> getAll() {
        List<WarrantyClaims> list = new ArrayList<>();
        String sql = "SELECT * FROM [warranty_claims]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                WarrantyClaims obj = new WarrantyClaims();
                obj.setClaimId(rs.getInt("claim_id"));
                obj.setWarrantyId(rs.getInt("warranty_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setFaultDescription(rs.getString("fault_description"));
                obj.setStatus(rs.getString("status"));
                obj.setRejectionReason(rs.getString("rejection_reason"));
                obj.setReceivedCondition(rs.getString("received_condition"));
                obj.setServiceCenterName(rs.getString("service_center_name"));
                obj.setHandledBy(rs.getInt("handled_by"));
                obj.setResolutionNote(rs.getString("resolution_note"));
                obj.setReplacementSerialNumber(rs.getString("replacement_serial_number"));
                obj.setCompletedAt(rs.getDate("completed_at"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
