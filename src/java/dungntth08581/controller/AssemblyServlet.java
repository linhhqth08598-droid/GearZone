package dungntth08581.controller;

import dungntth08581.service.AssemblyService;
import fourSome.model.Users;
import linhhqth08598.service.ReviewService;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Khách: /assembly = danh sách đơn lắp ráp (+ đánh giá UC 301 của Linh), /assembly/order = form đăng ký + đặt lịch (UC 251–254).
 * POST action=register | cancel.
 */
@WebServlet(name = "AssemblyServlet", urlPatterns = {"/assembly", "/assembly/order"})
public class AssemblyServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();
    private final AssemblyService service = new AssemblyService();

    private Users requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Users user = (Users) req.getSession().getAttribute("loggedUser");
        if (user == null) resp.sendRedirect(req.getContextPath() + "/auth?action=login");
        return user;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = requireLogin(request, response);
        if (user == null) return;
        if (request.getRequestURI().endsWith("/assembly/order")) {
            request.setAttribute("orders", service.ordersOf(user.getUserId()));
            request.setAttribute("builds", service.buildsOf(user.getUserId()));
            request.setAttribute("type", request.getParameter("type"));
            request.getRequestDispatcher("/views/dungntth08581/user/assembly_register.jsp").forward(request, response);
            return;
        }
        String idParam = request.getParameter("id");
        if (idParam != null) {   // chi tiết đơn của chính khách: linh kiện, kết quả kiểm tra, ảnh (D3)
            AssemblyService.Row row = null;
            try { row = service.getRowOfUser(user.getUserId(), Integer.parseInt(idParam)); } catch (NumberFormatException ignored) { }
            if (row == null) { request.getSession().setAttribute("error", "Không tìm thấy đơn lắp ráp!"); response.sendRedirect(request.getContextPath() + "/assembly"); return; }
            request.setAttribute("a", row);
            request.setAttribute("isService", AssemblyService.isServiceType(row.type));
            request.setAttribute("used", service.usedParts(row.assemblyId));
            request.setAttribute("report", service.getReport(row.assemblyId));
            request.setAttribute("photos", service.photos(row.assemblyId));
            request.getRequestDispatcher("/views/dungntth08581/user/assembly_detail.jsp").forward(request, response);
            return;
        }
        request.setAttribute("assemblies", reviewService.getAssemblies(user.getUserId()));
        request.getRequestDispatcher("/views/dungntth08581/user/assembly.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users user = requireLogin(request, response);
        if (user == null) return;
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String ctx = request.getContextPath();

        if ("register".equals(action)) {
            String err = service.register(user.getUserId(), request.getParameter("type"),
                    toInt(request.getParameter("orderId")), toInt(request.getParameter("buildId")),
                    request.getParameter("when"), request.getParameter("note"));
            if (err == null) { session.setAttribute("message", "Đã đăng ký lắp ráp! Cửa hàng sẽ xác nhận lịch cho bạn."); response.sendRedirect(ctx + "/assembly"); }
            else { session.setAttribute("error", err); response.sendRedirect(ctx + "/assembly/order"); }
            return;
        }
        if ("cancel".equals(action)) {
            Integer id = toInt(request.getParameter("id"));
            String err = id == null ? "Dữ liệu không hợp lệ!" : service.cancelByUser(user.getUserId(), id);
            if (err == null) session.setAttribute("message", "Đã hủy lịch lắp ráp."); else session.setAttribute("error", err);
        }
        response.sendRedirect(ctx + "/assembly");
    }

    private static Integer toInt(String s) {
        try { return s == null || s.trim().isEmpty() ? null : Integer.valueOf(s.trim()); } catch (NumberFormatException e) { return null; }
    }
}