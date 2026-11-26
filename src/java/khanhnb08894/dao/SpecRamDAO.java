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
        return insert(partId, ddrType, capacityGb, busSpeedMhz, 1, null);
    }

    public boolean insert(int partId, String ddrType, int capacityGb, int busSpeedMhz, int moduleCount, String casLatency) {
        String std = "DDR5".equalsIgnoreCase(ddrType) ? "DDR5" : "DDR4";
        String sql = "INSERT INTO [spec_ram] (part_id, ddr_type, capacity_gb, bus_speed_mhz, module_count, cas_latency, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setString(2, std);
            ps.setInt(3, capacityGb);
            ps.setInt(4, busSpeedMhz);
            ps.setInt(5, moduleCount > 0 ? moduleCount : 1);
            if (casLatency == null || casLatency.trim().isEmpty()) {
                ps.setNull(6, Types.VARCHAR);
            } else {
                ps.setString(6, casLatency.trim());
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, String ddrType, int capacityGb, int busSpeedMhz, int moduleCount, String casLatency) {
        String std = "DDR5".equalsIgnoreCase(ddrType) ? "DDR5" : "DDR4";
        String sql = "UPDATE [spec_ram] SET ddr_type = ?, capacity_gb = ?, bus_speed_mhz = ?, module_count = ?, cas_latency = ? "
                   + "WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, std);
            ps.setInt(2, capacityGb);
            ps.setInt(3, busSpeedMhz);
            ps.setInt(4, moduleCount > 0 ? moduleCount : 1);
            if (casLatency == null || casLatency.trim().isEmpty()) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, casLatency.trim());
            }
            ps.setInt(6, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
