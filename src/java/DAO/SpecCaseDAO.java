package DAO;

import Models.SpecCase;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecCaseDAO {

    public List<SpecCase> getAll() {
        List<SpecCase> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_case]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecCase sc = new SpecCase();
                    sc.setPartId(rs.getInt("part_id"));
                    sc.setSupportedMotherboards(rs.getString("supported_motherboards"));
                    sc.setMaxVgaLengthMm(rs.getInt("max_vga_length_mm"));
                    sc.setMaxCpuCoolerHeightMm(rs.getInt("max_cpu_cooler_height_mm"));
                    sc.setFanSupportInfo(rs.getString("fan_support_info"));
                    sc.setStatus(rs.getString("status"));
                    list.add(sc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
