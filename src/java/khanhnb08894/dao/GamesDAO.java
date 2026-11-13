package khanhnb08894.dao;

import model.Games;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GamesDAO {

    public List<Games> getAll() {
        List<Games> list = new ArrayList<>();
        String sql = "SELECT * FROM [games]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Games obj = new Games();
                obj.setGameId(rs.getInt("game_id"));
                obj.setGameName(rs.getString("game_name"));
                obj.setGenre(rs.getString("genre"));
                obj.setCoverUrl(rs.getString("cover_url"));
                obj.setMinRamGb(rs.getInt("min_ram_gb"));
                obj.setRecommendedRamGb(rs.getInt("recommended_ram_gb"));
                obj.setMinCpuScore(rs.getInt("min_cpu_score"));
                obj.setRecommendedCpuScore(rs.getInt("recommended_cpu_score"));
                obj.setMinGpuScore(rs.getInt("min_gpu_score"));
                obj.setRecommendedGpuScore(rs.getInt("recommended_gpu_score"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
