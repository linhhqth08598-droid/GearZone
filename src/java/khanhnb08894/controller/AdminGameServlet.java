package khanhnb08894.controller;

import fourSome.model.GameFpsBenchmarks;
import fourSome.model.Games;
import fourSome.model.Parts;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import khanhnb08894.service.GameService;

/**
 * Quản trị danh mục Game & dữ liệu benchmark FPS cho VGA.
 */
@WebServlet(name = "AdminGameServlet", urlPatterns = {"/admin/game", "/admin/games"})
public class AdminGameServlet extends HttpServlet {

    private final GameService gameService = new GameService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        List<Parts> vgaParts = new ArrayList<>();
        for (Parts p : gameService.getCandidatePartsForComparison()) {
            if (p.getCategoryId() != null && p.getCategoryId() == 4) {
                vgaParts.add(p);
            }
        }

        request.setAttribute("games", gameService.getAllGames());
        request.setAttribute("vgaParts", vgaParts);
        request.setAttribute("benchmarks", gameService.getBenchmarkRowsWithNames());

        request.getRequestDispatcher("/views/khanhnb08894/admin/games.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        try {
            if ("saveGame".equals(action)) {
                Games g = new Games();
                String idStr = request.getParameter("gameId");
                if (idStr != null && idStr.trim().matches("\\d+")) {
                    g.setGameId(Integer.parseInt(idStr.trim()));
                }
                g.setGameName(request.getParameter("gameName"));
                g.setGenre(request.getParameter("genre"));
                g.setCoverUrl(request.getParameter("coverUrl"));
                g.setMinRamGb(parseIntOrDefault(request.getParameter("minRamGb"), 8));
                g.setRecommendedRamGb(parseIntOrDefault(request.getParameter("recommendedRamGb"), 16));
                g.setMinCpuScore(parseIntOrDefault(request.getParameter("minCpuScore"), 0));
                g.setRecommendedCpuScore(parseIntOrDefault(request.getParameter("recommendedCpuScore"), 0));
                g.setMinGpuScore(parseIntOrDefault(request.getParameter("minGpuScore"), 0));
                g.setRecommendedGpuScore(parseIntOrDefault(request.getParameter("recommendedGpuScore"), 0));
                g.setStatus(request.getParameter("status"));

                if (gameService.saveGame(g)) {
                    session.setAttribute("message", "Lưu thông tin game thành công!");
                } else {
                    session.setAttribute("error", "Không thể lưu game. Vui lòng kiểm tra tên game.");
                }
            } else if ("toggleStatus".equals(action)) {
                int gameId = Integer.parseInt(request.getParameter("gameId"));
                if (gameService.toggleGameStatus(gameId)) {
                    session.setAttribute("message", "Đã cập nhật trạng thái game.");
                } else {
                    session.setAttribute("error", "Không tìm thấy game để đổi trạng thái.");
                }
            } else if ("addBenchmark".equals(action)) {
                GameFpsBenchmarks bm = new GameFpsBenchmarks();
                bm.setGameId(Integer.parseInt(request.getParameter("gameId")));
                bm.setVgaPartId(Integer.parseInt(request.getParameter("vgaPartId")));
                bm.setResolution(request.getParameter("resolution"));
                bm.setGraphicsPreset(request.getParameter("graphicsPreset"));
                bm.setAvgFps(Integer.parseInt(request.getParameter("avgFps")));
                bm.setStatus("ACTIVE");

                if (gameService.addBenchmark(bm)) {
                    session.setAttribute("message", "Thêm dữ liệu FPS benchmark thành công!");
                } else {
                    session.setAttribute("error", "Không thể thêm dữ liệu FPS benchmark.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Lỗi xử lý dữ liệu game: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/game");
    }

    private int parseIntOrDefault(String raw, int fallback) {
        if (raw == null || raw.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
