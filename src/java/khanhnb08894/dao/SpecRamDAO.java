package khanhnb08894.dao;

import model.SpecRam;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecRamDAO {

    public List<SpecRam> getAll() {
        List<SpecRam> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_ram]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecRam obj = new SpecRam();
                obj.setPartId(rs.getInt("part_id"));
                obj.setDdrType(rs.getString("ddr_type"));
                obj.setCapacityGb(rs.getInt("capacity_gb"));
                obj.setBusSpeedMhz(rs.getInt("bus_speed_mhz"));
                obj.setModuleCount(rs.getInt("module_count"));
                obj.setCasLatency(rs.getString("cas_latency"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
