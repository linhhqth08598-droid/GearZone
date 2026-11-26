package khanhnb08894.dao;

import fourSome.model.SpecCpu;
import fourSome.url.DBConnect;
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
    public boolean insert(int partId, int socketId, int cores, int threads, double baseClock, double boostClock, int tdp) {
        String sql = "INSERT INTO [spec_cpu] (part_id, socket_id, cores, threads, base_clock_ghz, boost_clock_ghz, tdp_watts, has_integrated_gpu, supported_ram_type, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, 0, 'DDR4', 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setInt(2, socketId);
            ps.setInt(3, cores);
            ps.setInt(4, threads);
            ps.setDouble(5, baseClock);
            ps.setDouble(6, boostClock);
            ps.setInt(7, tdp);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, int socketId, int cores, int threads, double baseClock, double boostClock, int tdp) {
        String sql = "UPDATE [spec_cpu] SET socket_id = ?, cores = ?, threads = ?, base_clock_ghz = ?, boost_clock_ghz = ?, tdp_watts = ? "
                   + "WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, socketId);
            ps.setInt(2, cores);
            ps.setInt(3, threads);
            ps.setDouble(4, baseClock);
            ps.setDouble(5, boostClock);
            ps.setInt(6, tdp);
            ps.setInt(7, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
