package khanhnb08894.dao;

import model.SpecMainboard;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecMainboardDAO {

    public List<SpecMainboard> getAll() {
        List<SpecMainboard> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_mainboard]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecMainboard obj = new SpecMainboard();
                obj.setPartId(rs.getInt("part_id"));
                obj.setSocketId(rs.getInt("socket_id"));
                obj.setChipset(rs.getString("chipset"));
                obj.setFormFactor(rs.getString("form_factor"));
                obj.setRamStandard(rs.getString("ram_standard"));
                obj.setRamSlots(rs.getInt("ram_slots"));
                obj.setMaxRamSpeedMhz(rs.getInt("max_ram_speed_mhz"));
                obj.setMaxRamCapacityGb(rs.getInt("max_ram_capacity_gb"));
                obj.setM2SlotsCount(rs.getInt("m2_slots_count"));
                obj.setSupportsNvme(rs.getBoolean("supports_nvme"));
                obj.setPcieGeneration(rs.getString("pcie_generation"));
                obj.setSataPortsCount(rs.getInt("sata_ports_count"));
                obj.setCpuPowerConnectors(rs.getString("cpu_power_connectors"));
                obj.setSupportedCpuGenerations(rs.getString("supported_cpu_generations"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
