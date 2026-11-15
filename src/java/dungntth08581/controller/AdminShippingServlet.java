package dungntth08581.controller;

import dungntth08581.service.ShippingService;
import fourSome.model.Users;
import linhhqth08598.service.AuditLogService;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Quản lý phương thức & phí vận chuyển (admin): /admin/shipping. Quyền theo AdminAuthFilter (khu PROMOTION). */
@WebServlet(name = "AdminShippingServlet", urlPatterns = {"/admin/shipping"})
public class AdminShippingServlet extends HttpServlet {

    private final ShippingService service = new ShippingService();
    private final AuditLogService audit = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("methods", service.listAll());
        request.getRequestDispatcher("/views/dungntth08581/admin/shipping.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users admin = (Users) session.getAttribute("loggedUser");
        String err = null, ok = null; int id = 0;
        try {
            if ("toggle".equals(request.getParameter("action"))) {
                id = Integer.parseInt(request.getParameter("id"));
                if (service.toggle(id)) ok = "Đã đổi trạng thái phương thức vận chuyển."; else err = "Không đổi được trạng thái!";
            } else {
                String idS = request.getParameter("id");
                id = idS == null || idS.isEmpty() ? 0 : Integer.parseInt(idS);
                err = service.save(id, request.getParameter("code"), request.getParameter("name"), request.getParameter("carrier"), request.getParameter("fee"), request.getParameter("days"));
                if (err == null) ok = id == 0 ? "Đã thêm phương thức vận chuyển." : "Đã cập nhật phương thức vận chuyển.";
            }
        } catch (NumberFormatException e) { err = "Dữ liệu không hợp lệ!"; }
        if (ok != null) {
            session.setAttribute("message", ok);
            if (admin != null) audit.logAction(admin.getUserId(), "SHIPPING_SAVE", "shipping_methods", id == 0 ? null : id, ok);
        }
        if (err != null) session.setAttribute("error", err);
        response.sendRedirect(request.getContextPath() + "/admin/shipping");
    }
}
