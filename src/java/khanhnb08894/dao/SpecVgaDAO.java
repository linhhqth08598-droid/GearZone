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
    public boolean insert(int partId, String gpuChipset, int vramCapacityGb, int recommendedPsuWatts) {
        String sql = "INSERT INTO [spec_vga] (part_id, gpu_chipset, vram_capacity_gb, vram_type, recommended_psu_watts, tgp_watts, power_connectors, length_mm, pcie_interface, hdmi_ports, displayport_ports, status) "
                   + "VALUES (?, ?, ?, 'GDDR6', ?, ?, '8-pin', 280, 'PCIe 4.0 x16', 1, 3, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setString(2, gpuChipset);
            ps.setInt(3, vramCapacityGb);
            ps.setInt(4, recommendedPsuWatts);
            ps.setInt(5, Math.max(75, recommendedPsuWatts / 2));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, String gpuChipset, int vramCapacityGb, int recommendedPsuWatts) {
        String sql = "UPDATE [spec_vga] SET gpu_chipset = ?, vram_capacity_gb = ?, recommended_psu_watts = ?, tgp_watts = ? "
                   + "WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, gpuChipset);
            ps.setInt(2, vramCapacityGb);
            ps.setInt(3, recommendedPsuWatts);
            ps.setInt(4, Math.max(75, recommendedPsuWatts / 2));
            ps.setInt(5, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertFull(int partId, String gpuChipset, int vramCapacityGb, String vramType,
                             int recommendedPsuWatts, int tgpWatts, String powerConnectors,
                             int lengthMm, String pcieInterface, int hdmiPorts, int displayportPorts) {
        String sql = "INSERT INTO [spec_vga] (part_id, gpu_chipset, vram_capacity_gb, vram_type, recommended_psu_watts, "
                   + "tgp_watts, power_connectors, length_mm, pcie_interface, hdmi_ports, displayport_ports, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setString(2, gpuChipset);
            ps.setInt(3, vramCapacityGb);
            ps.setString(4, (vramType == null || vramType.trim().isEmpty()) ? "GDDR6" : vramType.trim());
            ps.setInt(5, recommendedPsuWatts);
            ps.setInt(6, tgpWatts > 0 ? tgpWatts : Math.max(75, recommendedPsuWatts / 2));
            ps.setString(7, (powerConnectors == null || powerConnectors.trim().isEmpty()) ? "8-pin" : powerConnectors.trim());
            ps.setInt(8, lengthMm > 0 ? lengthMm : 280);
            ps.setString(9, (pcieInterface == null || pcieInterface.trim().isEmpty()) ? "PCIe 4.0 x16" : pcieInterface.trim());
            ps.setInt(10, Math.max(0, hdmiPorts));
            ps.setInt(11, Math.max(0, displayportPorts));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFull(int partId, String gpuChipset, int vramCapacityGb, String vramType,
                             int recommendedPsuWatts, int tgpWatts, String powerConnectors,
                             int lengthMm, String pcieInterface, int hdmiPorts, int displayportPorts) {
        String sql = "UPDATE [spec_vga] SET gpu_chipset = ?, vram_capacity_gb = ?, vram_type = ?, recommended_psu_watts = ?, "
                   + "tgp_watts = ?, power_connectors = ?, length_mm = ?, pcie_interface = ?, hdmi_ports = ?, "
                   + "displayport_ports = ? WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, gpuChipset);
            ps.setInt(2, vramCapacityGb);
            ps.setString(3, (vramType == null || vramType.trim().isEmpty()) ? "GDDR6" : vramType.trim());
            ps.setInt(4, recommendedPsuWatts);
            ps.setInt(5, tgpWatts > 0 ? tgpWatts : Math.max(75, recommendedPsuWatts / 2));
            ps.setString(6, (powerConnectors == null || powerConnectors.trim().isEmpty()) ? "8-pin" : powerConnectors.trim());
            ps.setInt(7, lengthMm > 0 ? lengthMm : 280);
            ps.setString(8, (pcieInterface == null || pcieInterface.trim().isEmpty()) ? "PCIe 4.0 x16" : pcieInterface.trim());
            ps.setInt(9, Math.max(0, hdmiPorts));
            ps.setInt(10, Math.max(0, displayportPorts));
            ps.setInt(11, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
