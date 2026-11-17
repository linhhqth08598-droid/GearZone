package khanhnb08894.dao;

import fourSome.model.SpecMainboard;
import fourSome.url.DBConnect;
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
    public boolean insert(int partId, int socketId, String chipset, String ramStandard, int ramSlots) {
        String sql = "INSERT INTO spec_mainboard (part_id, socket_id, chipset, form_factor, ram_standard, ram_slots, max_ram_speed_mhz, max_ram_capacity_gb, m2_slots_count, supports_nvme, pcie_generation, sata_ports_count, status) "
                   + "VALUES (?, ?, ?, 'ATX', ?, ?, 3200, 64, 2, 1, 'PCIe 4.0', 4, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setInt(2, socketId);
            ps.setString(3, chipset);
            ps.setString(4, ramStandard);
            ps.setInt(5, ramSlots);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
