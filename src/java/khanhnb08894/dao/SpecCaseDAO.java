package khanhnb08894.dao;

import fourSome.model.SpecCase;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecCaseDAO {

    public List<SpecCase> getAll() {
        List<SpecCase> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_case]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecCase obj = new SpecCase();
                obj.setPartId(rs.getInt("part_id"));
                obj.setSupportedMotherboards(rs.getString("supported_motherboards"));
                obj.setMaxVgaLengthMm(rs.getInt("max_vga_length_mm"));
                obj.setMaxCpuCoolerHeightMm(rs.getInt("max_cpu_cooler_height_mm"));
                obj.setFanSupportInfo(rs.getString("fan_support_info"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public boolean insert(int partId, String caseFormFactor) {
    String sql = "INSERT INTO spec_case (part_id, case_form_factor) VALUES (?, ?)";
    try (Connection conn = new DBConnect().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, partId);
        ps.setString(2, caseFormFactor);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}
