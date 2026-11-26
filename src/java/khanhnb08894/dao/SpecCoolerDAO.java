package khanhnb08894.dao;

import fourSome.model.SpecCooler;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecCoolerDAO {

    public List<SpecCooler> getAll() {
        List<SpecCooler> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_cooler]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecCooler obj = new SpecCooler();
                obj.setPartId(rs.getInt("part_id"));
                obj.setCoolerType(rs.getString("cooler_type"));
                obj.setHeightMm(rs.getInt("height_mm"));
                obj.setRadiatorSizeMm(rs.getInt("radiator_size_mm"));
                obj.setSupportedSockets(rs.getString("supported_sockets"));
                obj.setMaxTdpCoolingWatts(rs.getInt("max_tdp_cooling_watts"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public boolean insert(int partId, String coolerType, int maxTdp) {
        String cType = (coolerType != null && (coolerType.toUpperCase().contains("AIO") || coolerType.toUpperCase().contains("LIQUID") || coolerType.toLowerCase().contains("nước"))) ? "AIO_LIQUID" : "AIR";
        Integer rad = "AIO_LIQUID".equals(cType) ? 240 : null;
        return insert(partId, cType, 155, rad, "LGA1700, AM4, AM5", maxTdp);
    }

    public boolean insert(int partId, String coolerType, int heightMm, Integer radiatorSizeMm, String supportedSockets, int maxTdpCoolingWatts) {
        String cType = (coolerType != null && (coolerType.toUpperCase().contains("AIO") || coolerType.toUpperCase().contains("LIQUID") || coolerType.toLowerCase().contains("nước"))) ? "AIO_LIQUID" : "AIR";
        String sql = "INSERT INTO [spec_cooler] (part_id, cooler_type, height_mm, radiator_size_mm, supported_sockets, max_tdp_cooling_watts, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setString(2, cType);
            ps.setInt(3, heightMm > 0 ? heightMm : 155);
            if (radiatorSizeMm == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, radiatorSizeMm);
            ps.setString(5, (supportedSockets == null || supportedSockets.trim().isEmpty()) ? "LGA1700, AM4, AM5" : supportedSockets.trim());
            ps.setInt(6, maxTdpCoolingWatts > 0 ? maxTdpCoolingWatts : 180);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, String coolerType, int heightMm, Integer radiatorSizeMm, String supportedSockets, int maxTdpCoolingWatts) {
        String cType = (coolerType != null && (coolerType.toUpperCase().contains("AIO") || coolerType.toUpperCase().contains("LIQUID") || coolerType.toLowerCase().contains("nước"))) ? "AIO_LIQUID" : "AIR";
        String sql = "UPDATE [spec_cooler] SET cooler_type = ?, height_mm = ?, radiator_size_mm = ?, supported_sockets = ?, max_tdp_cooling_watts = ? "
                   + "WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cType);
            ps.setInt(2, heightMm > 0 ? heightMm : 155);
            if (radiatorSizeMm == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, radiatorSizeMm);
            ps.setString(4, (supportedSockets == null || supportedSockets.trim().isEmpty()) ? "LGA1700, AM4, AM5" : supportedSockets.trim());
            ps.setInt(5, maxTdpCoolingWatts > 0 ? maxTdpCoolingWatts : 180);
            ps.setInt(6, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
