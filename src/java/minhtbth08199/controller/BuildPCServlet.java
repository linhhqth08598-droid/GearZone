package minhtbth08199.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import fourSome.model.PcBuilds;
import fourSome.model.Users;
import java.util.LinkedHashSet;
import java.util.Set;
import minhtbth08199.service.BuildPCService;
import dungntth08581.service.CartService;
import minhtbth08199.service.RecommendPCService;
import khanhnb08894.service.GameService;

@WebServlet(name = "BuildPCServlet", urlPatterns = {"/build-pc"})
public class BuildPCServlet extends HttpServlet {
    private final BuildPCService service = new BuildPCService();
    private final CartService cartService = new CartService();
    private final RecommendPCService recommendService = new RecommendPCService();
    private final GameService gameService = new GameService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("fpsOptions", recommendService.availableMonitorRefreshRates());
        request.setAttribute("ramOptions", recommendService.availableRamSizes());
        request.setAttribute("games", gameService.getActiveGames());
        Object gameBuildError = request.getSession().getAttribute("gameBuildError");
        if (gameBuildError != null) {
            request.setAttribute("gameBuildError", gameBuildError);
            request.getSession().removeAttribute("gameBuildError");
        }
        String raw = request.getParameter("buildId");
        if (raw != null) {
            try {
                PcBuilds build = service.get(Integer.parseInt(raw));
                if (build == null || !canAccess(request, build)) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                request.setAttribute("build", build);
            }
            catch (NumberFormatException e) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã cấu hình không hợp lệ"); return; }
        }
        request.getRequestDispatcher("/views/minhtbth08199/buildpc/custom.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("loggedUser");
        if ("addToCart".equals(request.getParameter("action"))) {
            if (user == null) {
                request.getSession().setAttribute("redirectAfterLogin", request.getContextPath() + "/build-pc?buildId=" + request.getParameter("buildId"));
                response.sendRedirect(request.getContextPath() + "/auth?action=login");
                return;
            }
            try {
                int buildId = Integer.parseInt(request.getParameter("buildId"));
                PcBuilds build = service.get(buildId);
                if (build == null || !canAccess(request, build)) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                CartService.AddResult result = cartService.addBuildToCart(user.getUserId(), buildId);
                request.getSession().setAttribute(result.ok ? "message" : "error", result.message);
                if (!result.shortages.isEmpty()) request.getSession().setAttribute("error", String.join("; ", result.shortages));
                response.sendRedirect(request.getContextPath() + "/cart");
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã cấu hình không hợp lệ");
            }
            return;
        }
        PcBuilds build = service.create(user == null ? null : user.getUserId(), request.getParameter("buildName"));
        if (user == null) guestBuildIds(request.getSession()).add(build.getBuildId());
        response.sendRedirect(request.getContextPath() + "/build-pc?buildId=" + build.getBuildId());
    }

    static boolean canAccess(HttpServletRequest request, PcBuilds build) {
        Users user = (Users) request.getSession().getAttribute("loggedUser");
        if (build.getUserId() != null) return user != null && build.getUserId().equals(user.getUserId());
        return guestBuildIds(request.getSession()).contains(build.getBuildId());
    }

    @SuppressWarnings("unchecked")
    private static Set<Integer> guestBuildIds(HttpSession session) {
        Object value = session.getAttribute("guestBuildIds");
        if (value instanceof Set) return (Set<Integer>) value;
        Set<Integer> ids = new LinkedHashSet<>(); session.setAttribute("guestBuildIds", ids); return ids;
    }

}
