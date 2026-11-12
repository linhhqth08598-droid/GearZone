package DAO;

import Models.SpecMainboard;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecMainboardDAO {

    public List<SpecMainboard> getAll() {
        List<SpecMainboard> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_mainboard]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecMainboard sm = new SpecMainboard();
                    sm.setPartId(rs.getInt("part_id"));
                    sm.setSocketId(rs.getInt("socket_id"));
                    sm.setChipset(rs.getString("chipset"));
                    sm.setFormFactor(rs.getString("form_factor"));
                    sm.setRamStandard(rs.getString("ram_standard"));
                    sm.setRamSlots(rs.getInt("ram_slots"));
                    sm.setMaxRamSpeedMhz(rs.getInt("max_ram_speed_mhz"));
                    sm.setMaxRamCapacityGb(rs.getInt("max_ram_capacity_gb"));
                    sm.setM2SlotsCount(rs.getInt("m2_slots_count"));
                    sm.setSupportsNvme(rs.getBoolean("supports_nvme"));
                    sm.setPcieGeneration(rs.getString("pcie_generation"));
                    sm.setSataPortsCount(rs.getInt("sata_ports_count"));
                    sm.setCpuPowerConnectors(rs.getString("cpu_power_connectors"));
                    sm.setSupportedCpuGenerations(rs.getString("supported_cpu_generations"));
                    sm.setStatus(rs.getString("status"));
                    list.add(sm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
