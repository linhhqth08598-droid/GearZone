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
        return insert(partId, socketId, chipset, "ATX", ramStandard, ramSlots);
    }

    public boolean insert(int partId, int socketId, String chipset, String formFactor, String ramStandard, int ramSlots) {
        String std = "DDR5".equalsIgnoreCase(ramStandard) ? "DDR5" : "DDR4";
        String sql = "INSERT INTO [spec_mainboard] (part_id, socket_id, chipset, form_factor, ram_standard, ram_slots, max_ram_speed_mhz, max_ram_capacity_gb, m2_slots_count, supports_nvme, pcie_generation, sata_ports_count, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 3200, 64, 2, 1, 'PCIe 4.0', 4, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setInt(2, socketId);
            ps.setString(3, chipset);
            ps.setString(4, (formFactor == null || formFactor.trim().isEmpty()) ? "ATX" : formFactor.trim());
            ps.setString(5, std);
            ps.setInt(6, ramSlots > 0 ? ramSlots : 4);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, int socketId, String chipset, String formFactor, String ramStandard, int ramSlots) {
        String std = "DDR5".equalsIgnoreCase(ramStandard) ? "DDR5" : "DDR4";
        String sql = "UPDATE [spec_mainboard] SET socket_id = ?, chipset = ?, form_factor = ?, ram_standard = ?, ram_slots = ? "
                   + "WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, socketId);
            ps.setString(2, chipset);
            ps.setString(3, (formFactor == null || formFactor.trim().isEmpty()) ? "ATX" : formFactor.trim());
            ps.setString(4, std);
            ps.setInt(5, ramSlots > 0 ? ramSlots : 4);
            ps.setInt(6, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertFull(int partId, int socketId, String chipset, String formFactor, String ramStandard,
                             int ramSlots, int maxRamSpeedMhz, int maxRamCapacityGb, int m2SlotsCount,
                             boolean supportsNvme, String pcieGeneration, int sataPortsCount,
                             String cpuPowerConnectors, String supportedCpuGenerations) {
        String std = "DDR5".equalsIgnoreCase(ramStandard) ? "DDR5" : "DDR4";
        String sql = "INSERT INTO [spec_mainboard] (part_id, socket_id, chipset, form_factor, ram_standard, ram_slots, "
                   + "max_ram_speed_mhz, max_ram_capacity_gb, m2_slots_count, supports_nvme, pcie_generation, "
                   + "sata_ports_count, cpu_power_connectors, supported_cpu_generations, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setInt(2, socketId);
            ps.setString(3, chipset);
            ps.setString(4, (formFactor == null || formFactor.trim().isEmpty()) ? "ATX" : formFactor.trim());
            ps.setString(5, std);
            ps.setInt(6, ramSlots > 0 ? ramSlots : 4);
            ps.setInt(7, maxRamSpeedMhz > 0 ? maxRamSpeedMhz : 3200);
            ps.setInt(8, maxRamCapacityGb > 0 ? maxRamCapacityGb : 64);
            ps.setInt(9, Math.max(0, m2SlotsCount));
            ps.setBoolean(10, supportsNvme);
            ps.setString(11, (pcieGeneration == null || pcieGeneration.trim().isEmpty()) ? "PCIe 4.0" : pcieGeneration.trim());
            ps.setInt(12, Math.max(0, sataPortsCount));
            if (cpuPowerConnectors == null || cpuPowerConnectors.trim().isEmpty()) ps.setNull(13, Types.VARCHAR);
            else ps.setString(13, cpuPowerConnectors.trim());
            if (supportedCpuGenerations == null || supportedCpuGenerations.trim().isEmpty()) ps.setNull(14, Types.VARCHAR);
            else ps.setString(14, supportedCpuGenerations.trim());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFull(int partId, int socketId, String chipset, String formFactor, String ramStandard,
                             int ramSlots, int maxRamSpeedMhz, int maxRamCapacityGb, int m2SlotsCount,
                             boolean supportsNvme, String pcieGeneration, int sataPortsCount,
                             String cpuPowerConnectors, String supportedCpuGenerations) {
        String std = "DDR5".equalsIgnoreCase(ramStandard) ? "DDR5" : "DDR4";
        String sql = "UPDATE [spec_mainboard] SET socket_id = ?, chipset = ?, form_factor = ?, ram_standard = ?, "
                   + "ram_slots = ?, max_ram_speed_mhz = ?, max_ram_capacity_gb = ?, m2_slots_count = ?, "
                   + "supports_nvme = ?, pcie_generation = ?, sata_ports_count = ?, cpu_power_connectors = ?, "
                   + "supported_cpu_generations = ? WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, socketId);
            ps.setString(2, chipset);
            ps.setString(3, (formFactor == null || formFactor.trim().isEmpty()) ? "ATX" : formFactor.trim());
            ps.setString(4, std);
            ps.setInt(5, ramSlots > 0 ? ramSlots : 4);
            ps.setInt(6, maxRamSpeedMhz > 0 ? maxRamSpeedMhz : 3200);
            ps.setInt(7, maxRamCapacityGb > 0 ? maxRamCapacityGb : 64);
            ps.setInt(8, Math.max(0, m2SlotsCount));
            ps.setBoolean(9, supportsNvme);
            ps.setString(10, (pcieGeneration == null || pcieGeneration.trim().isEmpty()) ? "PCIe 4.0" : pcieGeneration.trim());
            ps.setInt(11, Math.max(0, sataPortsCount));
            if (cpuPowerConnectors == null || cpuPowerConnectors.trim().isEmpty()) ps.setNull(12, Types.VARCHAR);
            else ps.setString(12, cpuPowerConnectors.trim());
            if (supportedCpuGenerations == null || supportedCpuGenerations.trim().isEmpty()) ps.setNull(13, Types.VARCHAR);
            else ps.setString(13, supportedCpuGenerations.trim());
            ps.setInt(14, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
