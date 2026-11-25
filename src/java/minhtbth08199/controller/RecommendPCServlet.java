package minhtbth08199.controller;

import fourSome.model.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import minhtbth08199.dao.UserRequirementsDAO;
import minhtbth08199.service.RecommendPCService;
import minhtbth08199.service.BuildPCService;
import fourSome.model.PcBuilds;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/recommend-pc")
public class RecommendPCServlet extends HttpServlet {
    private final UserRequirementsDAO dao = new UserRequirementsDAO();
    private final RecommendPCService recommend = new RecommendPCService();
    private final BuildPCService buildService = new BuildPCService();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setOptions(request);
        request.getRequestDispatcher("/views/minhtbth08199/buildpc/recommend.jsp").forward(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setOptions(request);
        boolean preset = "true".equals(request.getParameter("preset"));
        try {
            double budget = parseBudget(request.getParameter("budget"));
            if (!Double.isFinite(budget) || budget < 3_000_000 || budget > 500_000_000) throw new IllegalArgumentException("Ngân sách phải từ 3 triệu đến 500 triệu đồng.");
            String gameValue = request.getParameter("gameProfile");
            String game = gameValue == null && !preset ? "general" : normalizeGameChoice(gameValue);
            String resolution = allowed(request.getParameter("resolution") == null && preset ? "1080p" : request.getParameter("resolution"), "1080p", "1440p", "2K", "4K");
            List<Integer> refreshRates = recommend.availableMonitorRefreshRates();
            List<Integer> ramSizes = recommend.availableRamSizes();
            String fpsValue = request.getParameter("fps");
            if (preset && fpsValue == null) fpsValue = preferredOption(refreshRates, 144);
            String fps = allowedNumber(fpsValue, refreshRates);
            String purpose = allowed(request.getParameter("purpose"), "gaming", "design", "office", "streaming", "video");
            String ramValue = request.getParameter("ram");
            if (preset && ramValue == null) ramValue = preferredOption(ramSizes, 16);
            int ram = Integer.parseInt(allowedNumber(ramValue, ramSizes));
            String priority = allowed(request.getParameter("priority"), "balanced", "gpu", "cpu", "cost");

            Users user = (Users) request.getSession().getAttribute("loggedUser");
            // Do not persist a requirement or an empty build until a complete
            // recommendation has been assembled successfully.
            var suggestedParts = recommend.recommend(budget, purpose, priority, game, ram, Integer.parseInt(fps));
            int id = dao.create(user == null ? null : user.getUserId());
            dao.updateField(id, "budget", budget);
            dao.updateField(id, "target_resolution", resolution);
            dao.updateField(id, "target_fps", fps);
            dao.updateField(id, "desired_ram_gb", ram);
            dao.updateField(id, "wants_rgb", "true".equals(request.getParameter("rgb")));
            dao.updateField(id, "optimize_cost", "cost".equals(priority));
            dao.updateField(id, "cpu_priority", "cpu".equals(priority) ? 5 : "gpu".equals(priority) ? 2 : 3);
            dao.updateField(id, "gpu_priority", "gpu".equals(priority) ? 5 : "cpu".equals(priority) ? 2 : 3);
            dao.complete(id, ("general".equals(game) ? "Nhu cầu: " + purpose : "Game: " + game) + "; " + resolution + " ở " + fps + " Hz; ưu tiên " + priority + ".");

            String buildLabel = "general".equals(game) ? purposeLabel(purpose) : gameDisplayLabel(game);
            PcBuilds build = buildService.create(user == null ? null : user.getUserId(), "Build " + buildLabel + " - " + (int)(budget / 1_000_000) + " triệu");
            for (var part : suggestedParts) buildService.setPart(build.getBuildId(), part.getPartId(), 1);
            if (user == null) {
                HttpSession session = request.getSession();
                @SuppressWarnings("unchecked") Set<Integer> guestIds = (Set<Integer>) session.getAttribute("guestBuildIds");
                if (guestIds == null) { guestIds = new LinkedHashSet<>(); session.setAttribute("guestBuildIds", guestIds); }
                guestIds.add(build.getBuildId());
            }
            response.sendRedirect(request.getContextPath() + "/build-pc?buildId=" + build.getBuildId());
            return;
        } catch (IllegalArgumentException ex) {
            showError(request, response, preset, ex.getMessage());
        } catch (RuntimeException ex) {
            showError(request, response, preset, "Chưa tạo được cấu hình. Vui lòng kiểm tra kho linh kiện và thử lại.");
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, boolean preset, String message) throws ServletException, IOException {
        if (preset) {
            request.getSession().setAttribute("gameBuildError", message);
            response.sendRedirect(request.getContextPath() + "/build-pc#game-builder");
        } else {
            request.setAttribute("error", message);
            request.getRequestDispatcher("/views/minhtbth08199/buildpc/recommend.jsp").forward(request, response);
        }
    }

    private double parseBudget(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập ngân sách hợp lệ.");
        }
        String cleaned = raw.trim().replaceAll("[\\s.,_₫đĐ]", "");
        return Double.parseDouble(cleaned);
    }

    private String normalizeGameChoice(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Một lựa chọn không hợp lệ. Vui lòng kiểm tra lại biểu mẫu.");
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Một lựa chọn không hợp lệ. Vui lòng kiểm tra lại biểu mẫu.");
        }
        String lower = trimmed.toLowerCase(java.util.Locale.ROOT);
        if ("valorant".equals(lower)) {
            return "valorant";
        }
        if ("lol".equals(lower) || "liên minh huyền thoại".equals(lower) || "lien minh huyen thoai".equals(lower)) {
            return "lol";
        }
        if ("cs2".equals(lower) || "counter-strike 2".equals(lower)) {
            return "cs2";
        }
        return trimmed;
    }

    private String gameDisplayLabel(String game) {
        if (game == null || game.isBlank()) {
            return "Gaming";
        }
        return switch (game.toLowerCase(java.util.Locale.ROOT)) {
            case "valorant" -> "Valorant";
            case "lol" -> "Liên Minh Huyền Thoại";
            case "cs2" -> "Counter-Strike 2";
            default -> game;
        };
    }

    private String purposeLabel(String purpose) {
        return switch (purpose) { case "gaming" -> "Gaming"; case "design" -> "Đồ họa"; case "office" -> "Văn phòng"; case "streaming" -> "Livestream"; default -> "Dựng video"; };
    }

    private String allowed(String value, String... choices) {
        if (value != null) for (String choice : choices) if (choice.equals(value)) return value;
        throw new IllegalArgumentException("Một lựa chọn không hợp lệ. Vui lòng kiểm tra lại biểu mẫu.");
    }

    private String allowedNumber(String value, List<Integer> choices) {
        if (value != null) for (Integer choice : choices) if (choice.toString().equals(value)) return value;
        throw new IllegalArgumentException("Mức Hz hoặc dung lượng RAM không còn trong kho. Vui lòng chọn lại.");
    }

    private String preferredOption(List<Integer> options, int preferred) {
        if (options.contains(preferred)) return Integer.toString(preferred);
        if (options.isEmpty()) throw new IllegalArgumentException("Kho chưa có RAM hoặc màn hình phù hợp để gợi ý.");
        return Integer.toString(options.get(0));
    }

    private void setOptions(HttpServletRequest request) {
        request.setAttribute("fpsOptions", recommend.availableMonitorRefreshRates());
        request.setAttribute("ramOptions", recommend.availableRamSizes());
    }
}
