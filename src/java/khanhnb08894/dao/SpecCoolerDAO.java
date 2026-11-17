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
    String sql = "INSERT INTO spec_cooler (part_id, cooler_type, max_tdp) VALUES (?, ?, ?)";
    try (Connection conn = new DBConnect().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, partId);
        ps.setString(2, coolerType);
        ps.setInt(3, maxTdp);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}
