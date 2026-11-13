package khanhnb08894.dao;

import model.SpecCpu;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecCpuDAO {

    public List<SpecCpu> getAll() {
        List<SpecCpu> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_cpu]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecCpu obj = new SpecCpu();
                obj.setPartId(rs.getInt("part_id"));
                obj.setSocketId(rs.getInt("socket_id"));
                obj.setCores(rs.getInt("cores"));
                obj.setThreads(rs.getInt("threads"));
                obj.setBaseClockGhz(rs.getDouble("base_clock_ghz"));
                obj.setBoostClockGhz(rs.getDouble("boost_clock_ghz"));
                obj.setTdpWatts(rs.getInt("tdp_watts"));
                obj.setHasIntegratedGpu(rs.getBoolean("has_integrated_gpu"));
                obj.setSupportedRamType(rs.getString("supported_ram_type"));
                obj.setGeneration(rs.getString("generation"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
