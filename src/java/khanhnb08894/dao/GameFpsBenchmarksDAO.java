package khanhnb08894.dao;

import model.GameFpsBenchmarks;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameFpsBenchmarksDAO {

    public List<GameFpsBenchmarks> getAll() {
        List<GameFpsBenchmarks> list = new ArrayList<>();
        String sql = "SELECT * FROM [game_fps_benchmarks]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GameFpsBenchmarks obj = new GameFpsBenchmarks();
                obj.setBenchmarkId(rs.getInt("benchmark_id"));
                obj.setGameId(rs.getInt("game_id"));
                obj.setVgaPartId(rs.getInt("vga_part_id"));
                obj.setResolution(rs.getString("resolution"));
                obj.setGraphicsPreset(rs.getString("graphics_preset"));
                obj.setAvgFps(rs.getInt("avg_fps"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
