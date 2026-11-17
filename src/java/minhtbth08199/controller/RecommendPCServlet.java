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
        try {
            double budget = Double.parseDouble(request.getParameter("budget"));
            if (!Double.isFinite(budget) || budget < 3_000_000 || budget > 500_000_000) throw new IllegalArgumentException("Ngân sách phải từ 3 triệu đến 500 triệu đồng.");
            String resolution = allowed(request.getParameter("resolution"), "1080p", "1440p", "4K");
            String fps = allowedNumber(request.getParameter("fps"), recommend.availableMonitorRefreshRates());
            String purpose = allowed(request.getParameter("purpose"), "gaming", "design", "office", "streaming", "video");
            int ram = Integer.parseInt(allowedNumber(request.getParameter("ram"), recommend.availableRamSizes()));
            String priority = allowed(request.getParameter("priority"), "balanced", "gpu", "cpu", "cost");

            Users user = (Users) request.getSession().getAttribute("loggedUser");
            int id = dao.create(user == null ? null : user.getUserId());
            dao.updateField(id, "budget", budget);
            dao.updateField(id, "target_resolution", resolution);
            dao.updateField(id, "target_fps", fps);
            dao.updateField(id, "desired_ram_gb", ram);
            dao.updateField(id, "wants_rgb", "true".equals(request.getParameter("rgb")));
            dao.updateField(id, "optimize_cost", "cost".equals(priority));
            dao.updateField(id, "cpu_priority", "cpu".equals(priority) ? 5 : "gpu".equals(priority) ? 2 : 3);
            dao.updateField(id, "gpu_priority", "gpu".equals(priority) ? 5 : "cpu".equals(priority) ? 2 : 3);
            dao.complete(id, "Nhu cầu: " + purpose + "; " + resolution + " ở " + fps + " FPS; ưu tiên " + priority + ".");

            var suggestedParts = recommend.recommend(budget, purpose, priority, ram, Integer.parseInt(fps));
            PcBuilds build = buildService.create(user == null ? null : user.getUserId(), "Cấu hình gợi ý trong ngân sách");
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
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/views/minhtbth08199/buildpc/recommend.jsp").forward(request, response);
        } catch (RuntimeException ex) {
            request.setAttribute("error", "Chưa lưu được nhu cầu. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/views/minhtbth08199/buildpc/recommend.jsp").forward(request, response);
        }
    }

    private String allowed(String value, String... choices) {
        if (value != null) for (String choice : choices) if (choice.equals(value)) return value;
        throw new IllegalArgumentException("Một lựa chọn không hợp lệ. Vui lòng kiểm tra lại biểu mẫu.");
    }

    private String allowedNumber(String value, List<Integer> choices) {
        if (value != null) for (Integer choice : choices) if (choice.toString().equals(value)) return value;
        throw new IllegalArgumentException("Mức Hz hoặc dung lượng RAM không còn trong kho. Vui lòng chọn lại.");
    }

    private void setOptions(HttpServletRequest request) {
        request.setAttribute("fpsOptions", recommend.availableMonitorRefreshRates());
        request.setAttribute("ramOptions", recommend.availableRamSizes());
    }
}
