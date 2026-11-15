package dungntth08581.controller;

import dungntth08581.service.OrderService;
import fourSome.model.Users;
import fourSome.util.Constants;
import fourSome.util.PaginationUtil;
import linhhqth08598.service.AuditLogService;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Quản lý đơn hàng (admin): danh sách, lọc trạng thái, xác nhận -> giao -> hoàn tất, hủy. Quyền do AdminAuthFilter (khu ORDER). */
@WebServlet(name = "AdminOrderServlet", urlPatterns = {"/admin/orders", "/admin/order"})
public class AdminOrderServlet extends HttpServlet {

    private final OrderService service = new OrderService();
    private final AuditLogService audit = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String status = request.getParameter("status");
        List<OrderService.OrderRow> all = service.listForAdmin(status);
        PaginationUtil page = new PaginationUtil(request.getParameter("page"), Constants.ADMIN_PAGE_SIZE, all.size());
        request.setAttribute("orders", page.slice(all));
        request.setAttribute("pagination", page);
        request.setAttribute("status", status == null ? "" : status);
        request.getRequestDispatcher("/views/dungntth08581/admin/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users admin = (Users) session.getAttribute("loggedUser");
        String err;
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
            String next = request.getParameter("next");
            err = service.adminUpdateStatus(id, next, request.getParameter("extra"));
            if (err == null && admin != null) audit.logAction(admin.getUserId(), "ORDER_" + next, "orders", id, "Đổi trạng thái đơn sang " + next);
        } catch (NumberFormatException e) {
            err = "Dữ liệu không hợp lệ!";
        }
        if (err == null) session.setAttribute("message", "Đã cập nhật đơn hàng #" + id + "."); else session.setAttribute("error", err);
        String back = request.getContextPath() + "/admin/orders";
        String st = request.getParameter("status");
        if (st != null && !st.isEmpty()) back += "?status=" + java.net.URLEncoder.encode(st, java.nio.charset.StandardCharsets.UTF_8);
        response.sendRedirect(back);
    }
}
