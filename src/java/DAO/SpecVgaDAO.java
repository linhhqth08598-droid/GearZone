package DAO;

import Models.SpecVga;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecVgaDAO {

    public List<SpecVga> getAll() {
        List<SpecVga> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_vga]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecVga sv = new SpecVga();
                    sv.setPartId(rs.getInt("part_id"));
                    sv.setGpuChipset(rs.getString("gpu_chipset"));
                    sv.setVramCapacityGb(rs.getInt("vram_capacity_gb"));
                    sv.setVramType(rs.getString("vram_type"));
                    sv.setRecommendedPsuWatts(rs.getInt("recommended_psu_watts"));
                    sv.setTgpWatts(rs.getInt("tgp_watts"));
                    sv.setPowerConnectors(rs.getString("power_connectors"));
                    sv.setLengthMm(rs.getInt("length_mm"));
                    sv.setPcieInterface(rs.getString("pcie_interface"));
                    sv.setHdmiPorts(rs.getInt("hdmi_ports"));
                    sv.setDisplayportPorts(rs.getInt("displayport_ports"));
                    sv.setStatus(rs.getString("status"));
                    list.add(sv);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
