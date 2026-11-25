package khanhnb08894.service;

import fourSome.model.GameFpsBenchmarks;
import fourSome.model.Games;
import fourSome.model.Parts;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import khanhnb08894.dao.GameFpsBenchmarksDAO;
import khanhnb08894.dao.GamesDAO;
import khanhnb08894.dao.PartsDAO;

/**
 * Nghiệp vụ quản lý game, benchmark FPS và đối chiếu hiệu năng phần cứng.
 */
public class GameService {

    private final GamesDAO gamesDAO = new GamesDAO();
    private final GameFpsBenchmarksDAO benchmarksDAO = new GameFpsBenchmarksDAO();
    private final PartsDAO partsDAO = new PartsDAO();

    public List<Games> getAllGames() {
        return gamesDAO.getAll();
    }

    public List<Games> getActiveGames() {
        List<Games> active = new ArrayList<>();
        for (Games g : gamesDAO.getAll()) {
            if ("ACTIVE".equalsIgnoreCase(g.getStatus())) {
                active.add(g);
            }
        }
        return active;
    }

    public Games findGameById(int gameId) {
        if (gameId <= 0) {
            return null;
        }
        String sql = "SELECT * FROM [games] WHERE game_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, gameId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
                    return g;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveGame(Games game) {
        if (game == null || game.getGameName() == null || game.getGameName().trim().isEmpty()) {
            return false;
        }
        String status = (game.getStatus() == null || game.getStatus().trim().isEmpty()) ? "ACTIVE" : game.getStatus().trim();
        if (game.getGameId() != null && game.getGameId() > 0) {
            String sql = "UPDATE [games] SET game_name = ?, genre = ?, cover_url = ?, min_ram_gb = ?, recommended_ram_gb = ?, "
                    + "min_cpu_score = ?, recommended_cpu_score = ?, min_gpu_score = ?, recommended_gpu_score = ?, status = ? WHERE game_id = ?";
            try (Connection con = DBConnect.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, game.getGameName().trim());
                ps.setString(2, trimOrNull(game.getGenre()));
                ps.setString(3, trimOrNull(game.getCoverUrl()));
                ps.setInt(4, safeInt(game.getMinRamGb(), 8));
                ps.setInt(5, safeInt(game.getRecommendedRamGb(), 16));
                ps.setInt(6, safeInt(game.getMinCpuScore(), 0));
                ps.setInt(7, safeInt(game.getRecommendedCpuScore(), 0));
                ps.setInt(8, safeInt(game.getMinGpuScore(), 0));
                ps.setInt(9, safeInt(game.getRecommendedGpuScore(), 0));
                ps.setString(10, status);
                ps.setInt(11, game.getGameId());
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String sql = "INSERT INTO [games] (game_name, genre, cover_url, min_ram_gb, recommended_ram_gb, "
                    + "min_cpu_score, recommended_cpu_score, min_gpu_score, recommended_gpu_score, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection con = DBConnect.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, game.getGameName().trim());
                ps.setString(2, trimOrNull(game.getGenre()));
                ps.setString(3, trimOrNull(game.getCoverUrl()));
                ps.setInt(4, safeInt(game.getMinRamGb(), 8));
                ps.setInt(5, safeInt(game.getRecommendedRamGb(), 16));
                ps.setInt(6, safeInt(game.getMinCpuScore(), 0));
                ps.setInt(7, safeInt(game.getRecommendedCpuScore(), 0));
                ps.setInt(8, safeInt(game.getMinGpuScore(), 0));
                ps.setInt(9, safeInt(game.getRecommendedGpuScore(), 0));
                ps.setString(10, status);
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean toggleGameStatus(int gameId) {
        Games current = findGameById(gameId);
        if (current == null) {
            return false;
        }
        String nextStatus = "ACTIVE".equalsIgnoreCase(current.getStatus()) ? "INACTIVE" : "ACTIVE";
        String sql = "UPDATE [games] SET status = ? WHERE game_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nextStatus);
            ps.setInt(2, gameId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<GameFpsBenchmarks> getAllBenchmarks() {
        return benchmarksDAO.getAll();
    }

    public boolean addBenchmark(GameFpsBenchmarks bm) {
        if (bm == null || bm.getGameId() == null || bm.getVgaPartId() == null || bm.getAvgFps() == null) {
            return false;
        }
        String res = (bm.getResolution() == null || bm.getResolution().trim().isEmpty()) ? "1080p" : bm.getResolution().trim();
        String preset = (bm.getGraphicsPreset() == null || bm.getGraphicsPreset().trim().isEmpty()) ? "HIGH" : bm.getGraphicsPreset().trim().toUpperCase();
        String status = (bm.getStatus() == null || bm.getStatus().trim().isEmpty()) ? "ACTIVE" : bm.getStatus().trim();
        String sql = "INSERT INTO [game_fps_benchmarks] (game_id, vga_part_id, resolution, graphics_preset, avg_fps, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bm.getGameId());
            ps.setInt(2, bm.getVgaPartId());
            ps.setString(3, res);
            ps.setString(4, preset);
            ps.setInt(5, Math.max(0, bm.getAvgFps()));
            ps.setString(6, status);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> getBenchmarkRowsWithNames() {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "SELECT b.benchmark_id, b.game_id, b.vga_part_id, b.resolution, b.graphics_preset, b.avg_fps, b.status, "
                + "g.game_name, p.part_name, p.product_code "
                + "FROM [game_fps_benchmarks] b "
                + "JOIN [games] g ON b.game_id = g.game_id "
                + "JOIN [parts] p ON b.vga_part_id = p.part_id "
                + "ORDER BY b.benchmark_id DESC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("benchmarkId", rs.getInt("benchmark_id"));
                row.put("gameId", rs.getInt("game_id"));
                row.put("vgaPartId", rs.getInt("vga_part_id"));
                row.put("gameName", rs.getString("game_name"));
                row.put("partName", rs.getString("part_name"));
                row.put("productCode", rs.getString("product_code"));
                row.put("resolution", rs.getString("resolution"));
                row.put("graphicsPreset", rs.getString("graphics_preset"));
                row.put("avgFps", rs.getInt("avg_fps"));
                row.put("status", rs.getString("status"));
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<Parts> getCandidatePartsForComparison() {
        List<Parts> result = new ArrayList<>();
        for (Parts p : partsDAO.getAllWithImages()) {
            if ("ACTIVE".equalsIgnoreCase(p.getStatus())) {
                result.add(p);
            }
        }
        return result;
    }

    private String trimOrNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private int safeInt(Integer v, int fallback) {
        return v == null ? fallback : v;
    }
}
