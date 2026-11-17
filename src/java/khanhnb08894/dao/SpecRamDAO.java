package khanhnb08894.dao;

import fourSome.model.SpecRam;
import fourSome.url.DBConnect;
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
    public boolean insert(int partId, String ddrType, int capacityGb, int busSpeedMhz) {
    String sql = "INSERT INTO spec_ram (part_id, ddr_type, capacity_gb, bus_speed_mhz) VALUES (?, ?, ?, ?)";
    try (Connection conn = new DBConnect().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, partId);
        ps.setString(2, ddrType);
        ps.setInt(3, capacityGb);
        ps.setInt(4, busSpeedMhz);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}
