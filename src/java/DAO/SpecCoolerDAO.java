package DAO;

import Models.SpecCooler;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecCoolerDAO {

    public List<SpecCooler> getAll() {
        List<SpecCooler> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_cooler]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecCooler sc = new SpecCooler();
                    sc.setPartId(rs.getInt("part_id"));
                    sc.setCoolerType(rs.getString("cooler_type"));
                    sc.setHeightMm(rs.getInt("height_mm"));
                    sc.setRadiatorSizeMm(rs.getInt("radiator_size_mm"));
                    sc.setSupportedSockets(rs.getString("supported_sockets"));
                    sc.setMaxTdpCoolingWatts(rs.getInt("max_tdp_cooling_watts"));
                    sc.setStatus(rs.getString("status"));
                    list.add(sc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
