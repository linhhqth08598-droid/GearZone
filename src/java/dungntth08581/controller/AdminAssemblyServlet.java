package dungntth08581.controller;

import dungntth08581.service.AssemblyService;
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

/** Quản lý lắp ráp (admin/kỹ thuật): danh sách, lọc trạng thái, xác nhận/đổi lịch/từ chối (UC 255). Quyền do AdminAuthFilter (khu TECH). */
@WebServlet(name = "AdminAssemblyServlet", urlPatterns = {"/admin/assembly"})
public class AdminAssemblyServlet extends HttpServlet {

    private final AssemblyService service = new AssemblyService();
    private final AuditLogService audit = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null) {   // trang chi tiết: phân công, ghi linh kiện, cập nhật tiến độ (D2)
            AssemblyService.Row row = null;
            try { row = service.getRow(Integer.parseInt(idParam)); } catch (NumberFormatException ignored) { }
            if (row == null) { request.getSession().setAttribute("error", "Không tìm thấy đơn lắp ráp!"); response.sendRedirect(request.getContextPath() + "/admin/assembly"); return; }
            request.setAttribute("a", row);
            request.setAttribute("technicians", service.technicians());
            request.setAttribute("source", service.sourceParts(row.assemblyId));
            request.setAttribute("used", service.usedParts(row.assemblyId));
            request.getRequestDispatcher("/views/dungntth08581/admin/assembly_detail.jsp").forward(request, response);
            return;
        }
        String status = request.getParameter("status");
        List<AssemblyService.Row> all = service.listForAdmin(status);
        PaginationUtil page = new PaginationUtil(request.getParameter("page"), Constants.ADMIN_PAGE_SIZE, all.size());
        request.setAttribute("rows", page.slice(all));
        request.setAttribute("pagination", page);
        request.setAttribute("status", status == null ? "" : status);
        request.getRequestDispatcher("/views/dungntth08581/admin/assembly.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users admin = (Users) session.getAttribute("loggedUser");
        String err; int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
            String action = request.getParameter("action");
            if ("assign".equals(action)) err = service.assign(id, Integer.parseInt(request.getParameter("technicianId")));
            else if ("addPart".equals(action)) err = service.addUsedPart(id, Integer.parseInt(request.getParameter("partId")), Integer.parseInt(request.getParameter("qty")), request.getParameter("serial"));
            else if ("removePart".equals(action)) err = service.removeUsedPart(id, Integer.parseInt(request.getParameter("usedId")));
            else if ("advance".equals(action)) err = service.advance(id, request.getParameter("next"));
            else err = service.adminAction(id, action, request.getParameter("when"), request.getParameter("reason"));
            if (err == null && admin != null) audit.logAction(admin.getUserId(), "ASSEMBLY_" + action.toUpperCase(), "assembly_orders", id, "Lắp ráp #" + id + ": " + action);
        } catch (NumberFormatException e) { err = "Dữ liệu không hợp lệ!"; }
        if (err == null) session.setAttribute("message", "Đã cập nhật đơn lắp ráp #" + id + "."); else session.setAttribute("error", err);
        if (request.getParameter("detail") != null) { response.sendRedirect(request.getContextPath() + "/admin/assembly?id=" + id); return; }
        String back = request.getContextPath() + "/admin/assembly";
        String st = request.getParameter("status");
        if (st != null && !st.isEmpty()) back += "?status=" + java.net.URLEncoder.encode(st, java.nio.charset.StandardCharsets.UTF_8);
        response.sendRedirect(back);
    }
}