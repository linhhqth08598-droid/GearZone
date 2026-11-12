package DAO;

import Models.Games;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GamesDAO {

    public List<Games> getAll() {
        List<Games> list = new ArrayList<>();
        String sql = "SELECT * FROM [games]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Games g = new Games();
                    g.setGameId(rs.getInt("game_id"));
                    g.setGameName(rs.getString("game_name"));
                    g.setGenre(rs.getString("genre"));
                    g.setCoverUrl(rs.getString("cover_url"));
                    g.setMinRamGb(rs.getInt("min_ram_gb"));
                    g.setRecommendedRamGb(rs.getInt("recommended_ram_gb"));
                    g.setMinCpuScore(rs.getInt("min_cpu_score"));
                    g.setRecommendedCpuScore(rs.getInt("recommended_cpu_score"));
                    g.setMinGpuScore(rs.getInt("min_gpu_score"));
                    g.setRecommendedGpuScore(rs.getInt("recommended_gpu_score"));
                    g.setStatus(rs.getString("status"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
