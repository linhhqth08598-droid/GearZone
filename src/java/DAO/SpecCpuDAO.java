package DAO;

import Models.SpecCpu;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecCpuDAO {

    public List<SpecCpu> getAll() {
        List<SpecCpu> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_cpu]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecCpu sc = new SpecCpu();
                    sc.setPartId(rs.getInt("part_id"));
                    sc.setSocketId(rs.getInt("socket_id"));
                    sc.setCores(rs.getInt("cores"));
                    sc.setThreads(rs.getInt("threads"));
                    sc.setBaseClockGhz(rs.getDouble("base_clock_ghz"));
                    sc.setBoostClockGhz(rs.getDouble("boost_clock_ghz"));
                    sc.setTdpWatts(rs.getInt("tdp_watts"));
                    sc.setHasIntegratedGpu(rs.getBoolean("has_integrated_gpu"));
                    sc.setSupportedRamType(rs.getString("supported_ram_type"));
                    sc.setGeneration(rs.getString("generation"));
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
