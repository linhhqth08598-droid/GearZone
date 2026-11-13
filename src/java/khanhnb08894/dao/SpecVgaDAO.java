package khanhnb08894.dao;

import fourSome.model.SpecVga;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecVgaDAO {

    public List<SpecVga> getAll() {
        List<SpecVga> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_vga]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecVga obj = new SpecVga();
                obj.setPartId(rs.getInt("part_id"));
                obj.setGpuChipset(rs.getString("gpu_chipset"));
                obj.setVramCapacityGb(rs.getInt("vram_capacity_gb"));
                obj.setVramType(rs.getString("vram_type"));
                obj.setRecommendedPsuWatts(rs.getInt("recommended_psu_watts"));
                obj.setTgpWatts(rs.getInt("tgp_watts"));
                obj.setPowerConnectors(rs.getString("power_connectors"));
                obj.setLengthMm(rs.getInt("length_mm"));
                obj.setPcieInterface(rs.getString("pcie_interface"));
                obj.setHdmiPorts(rs.getInt("hdmi_ports"));
                obj.setDisplayportPorts(rs.getInt("displayport_ports"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
