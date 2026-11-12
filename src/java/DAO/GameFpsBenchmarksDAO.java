package DAO;

import Models.GameFpsBenchmarks;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameFpsBenchmarksDAO {

    public List<GameFpsBenchmarks> getAll() {
        List<GameFpsBenchmarks> list = new ArrayList<>();
        String sql = "SELECT * FROM [game_fps_benchmarks]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GameFpsBenchmarks gfb = new GameFpsBenchmarks();
                    gfb.setBenchmarkId(rs.getInt("benchmark_id"));
                    gfb.setGameId(rs.getInt("game_id"));
                    gfb.setVgaPartId(rs.getInt("vga_part_id"));
                    gfb.setResolution(rs.getString("resolution"));
                    gfb.setGraphicsPreset(rs.getString("graphics_preset"));
                    gfb.setAvgFps(rs.getInt("avg_fps"));
                    gfb.setStatus(rs.getString("status"));
                    list.add(gfb);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
