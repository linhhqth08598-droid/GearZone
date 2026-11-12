package DAO;

import Models.SpecRam;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecRamDAO {

    public List<SpecRam> getAll() {
        List<SpecRam> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_ram]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecRam sr = new SpecRam();
                    sr.setPartId(rs.getInt("part_id"));
                    sr.setDdrType(rs.getString("ddr_type"));
                    sr.setCapacityGb(rs.getInt("capacity_gb"));
                    sr.setBusSpeedMhz(rs.getInt("bus_speed_mhz"));
                    sr.setModuleCount(rs.getInt("module_count"));
                    sr.setCasLatency(rs.getString("cas_latency"));
                    sr.setStatus(rs.getString("status"));
                    list.add(sr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
