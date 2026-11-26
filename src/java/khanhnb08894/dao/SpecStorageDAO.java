package khanhnb08894.dao;

import fourSome.model.SpecStorage;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecStorageDAO {

    public List<SpecStorage> getAll() {
        List<SpecStorage> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_storage]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecStorage obj = new SpecStorage();
                obj.setPartId(rs.getInt("part_id"));
                obj.setStorageType(rs.getString("storage_type"));
                obj.setFormFactor(rs.getString("form_factor"));
                obj.set_interface(rs.getString("interface"));
                obj.setCapacityGb(rs.getInt("capacity_gb"));
                obj.setReadSpeedMbps(rs.getInt("read_speed_mbps"));
                obj.setWriteSpeedMbps(rs.getInt("write_speed_mbps"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public boolean insert(int partId, String storageType, int capacityGb) {
        String sType = (storageType != null && storageType.toUpperCase().contains("HDD")) ? "HDD" : "SSD";
        String iface = (storageType != null && !storageType.trim().isEmpty()) ? storageType.trim() : ("HDD".equals(sType) ? "SATA III" : "NVMe PCIe");
        String form = "HDD".equals(sType) ? "3.5 inch" : "M.2 2280";
        return insert(partId, sType, form, iface, capacityGb, null, null);
    }

    public boolean insert(int partId, String storageType, String formFactor, String iface, int capacityGb, Integer readSpeed, Integer writeSpeed) {
        String sType = (storageType != null && storageType.toUpperCase().contains("HDD")) ? "HDD" : "SSD";
        String sql = "INSERT INTO [spec_storage] (part_id, storage_type, form_factor, interface, capacity_gb, read_speed_mbps, write_speed_mbps, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setString(2, sType);
            ps.setString(3, (formFactor == null || formFactor.trim().isEmpty()) ? ("HDD".equals(sType) ? "3.5 inch" : "M.2 2280") : formFactor.trim());
            ps.setString(4, (iface == null || iface.trim().isEmpty()) ? ("HDD".equals(sType) ? "SATA III" : "NVMe PCIe") : iface.trim());
            ps.setInt(5, capacityGb);
            if (readSpeed == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, readSpeed);
            if (writeSpeed == null) ps.setNull(7, Types.INTEGER); else ps.setInt(7, writeSpeed);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, String storageType, String formFactor, String iface, int capacityGb, Integer readSpeed, Integer writeSpeed) {
        String sType = (storageType != null && storageType.toUpperCase().contains("HDD")) ? "HDD" : "SSD";
        String sql = "UPDATE [spec_storage] SET storage_type = ?, form_factor = ?, interface = ?, capacity_gb = ?, "
                   + "read_speed_mbps = ?, write_speed_mbps = ? WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sType);
            ps.setString(2, (formFactor == null || formFactor.trim().isEmpty()) ? ("HDD".equals(sType) ? "3.5 inch" : "M.2 2280") : formFactor.trim());
            ps.setString(3, (iface == null || iface.trim().isEmpty()) ? ("HDD".equals(sType) ? "SATA III" : "NVMe PCIe") : iface.trim());
            ps.setInt(4, capacityGb);
            if (readSpeed == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, readSpeed);
            if (writeSpeed == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, writeSpeed);
            ps.setInt(7, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
