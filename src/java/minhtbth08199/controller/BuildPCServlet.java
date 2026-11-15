package minhtbth08199.controller;

import fourSome.model.Users;
import dungntth08581.service.CartService;
import fourSome.model.CompatibilityResult;
import linhhqth08598.service.BuildShareService;
import minhtbth08199.service.BuildPCService;
import minhtbth08199.service.BuildPCService.Line;
import minhtbth08199.service.BuildPCService.Slot;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Trang dựng cấu hình PC: /build-pc?buildId=..&cat=..&q=..
 * POST action: setPart | removePart | newBuild | save (save gọi BuildShareService.saveBuild của Linh - UC 16).
 */
@WebServlet(name = "BuildPCServlet", urlPatterns = {"/build-pc"})
public class BuildPCServlet extends HttpServlet {

    private final BuildPCService service = new BuildPCService();
    private final BuildShareService shareService = new BuildShareService();
    private final CartService cartService = new CartService();

    private Users requireLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            String url = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
            session.setAttribute("redirectAfterLogin", url);
            res.sendRedirect(req.getContextPath() + "/auth?action=login");
        }
        return user;
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = requireLogin(request, response);
        if (user == null) return;

        int buildId = parseInt(request.getParameter("buildId"), parseInt(request.getParameter("id"), 0)); // nhận cả ?buildId= và ?id= (nút "Xem / sửa" ở lịch sử dùng id)
        if (buildId <= 0 || !service.canEdit(buildId, user.getUserId())) buildId = service.getOrCreateDraft(user.getUserId());
        if (buildId <= 0) {
            request.getSession().setAttribute("error", "Không tạo được cấu hình mới, vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        List<Slot> slots = service.getSlots();
        List<Line> lines = service.getLines(buildId);
        int cat = parseInt(request.getParameter("cat"), slots.isEmpty() ? 0 : slots.get(0).getCategoryId());
        String q = request.getParameter("q");

        request.setAttribute("buildId", buildId);
        request.setAttribute("buildName", service.getBuildName(buildId));
        request.setAttribute("slots", slots);
        request.setAttribute("lines", lines);
        request.setAttribute("total", service.getTotal(buildId));
        request.setAttribute("missing", service.getMissingRequired(slots, lines));
        CompatibilityResult compat = service.checkCompatibility(lines);
        request.setAttribute("compatErrors", compat.getErrors());
        request.setAttribute("compatWarnings", compat.getWarnings());
        request.setAttribute("currentCat", cat);
        request.setAttribute("q", q == null ? "" : q);
        request.setAttribute("options", cat > 0 ? service.searchParts(cat, q) : null);
        request.getRequestDispatcher("/views/minhtbth08199/buildpc/custom.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users user = requireLogin(request, response);
        if (user == null) return;
        HttpSession session = request.getSession();

        String action = request.getParameter("action");
        int buildId = parseInt(request.getParameter("buildId"), 0);
        int cat = parseInt(request.getParameter("cat"), 0);
        String ctx = request.getContextPath();

        if ("newBuild".equals(action)) {
            int id = service.newDraft(user.getUserId());
            response.sendRedirect(ctx + "/build-pc" + (id > 0 ? "?buildId=" + id : ""));
            return;
        }

        String back = ctx + "/build-pc?buildId=" + buildId + (cat > 0 ? "&cat=" + cat : "");
        String err = null;
        String ok = null;
        if ("setPart".equals(action)) {
            err = service.setPart(buildId, user.getUserId(), parseInt(request.getParameter("partId"), 0), parseInt(request.getParameter("quantity"), 1));
            ok = "Đã cập nhật cấu hình.";
        } else if ("removePart".equals(action)) {
            err = service.removeLine(buildId, user.getUserId(), parseInt(request.getParameter("detailId"), 0));
            ok = "Đã xóa linh kiện.";
        } else if ("save".equals(action)) {
            err = shareService.saveBuild(buildId, user.getUserId(), request.getParameter("buildName"));
            if (err == null) {
                session.setAttribute("message", "Đã lưu cấu hình thành công! Bạn có thể chia sẻ link ở trang này.");
                response.sendRedirect(ctx + "/user/pc-build-history?share=" + buildId);
                return;
            }
        } else if ("addToCart".equals(action)) {
            // UC 98/221 (giỏ hàng của Dũng) + UC 99 (kiểm tra tồn kho cả cấu hình)
            CartService.AddResult r = cartService.addBuildToCart(user.getUserId(), buildId);
            session.setAttribute("cartCount", cartService.countLines(user.getUserId()));
            if (!r.ok) {
                err = r.message;
            } else {
                String msg = r.message;
                if (!r.shortages.isEmpty()) msg += " Lưu ý thiếu hàng/ngừng bán: " + String.join("; ", r.shortages) + ".";
                session.setAttribute("message", msg);
                response.sendRedirect(ctx + "/cart");
                return;
            }
        } else {
            err = "Thao tác không hợp lệ!";
        }
        if (err != null) session.setAttribute("error", err); else if (ok != null) session.setAttribute("message", ok);
        response.sendRedirect(back);
    }
}
