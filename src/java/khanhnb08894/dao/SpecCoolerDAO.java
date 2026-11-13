package khanhnb08894.dao;

import model.SpecCooler;
import url.DBConnect;
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
}
