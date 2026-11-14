package linhhqth08598.controller;

import fourSome.model.Users;
import fourSome.model.SupportTickets;
import linhhqth08598.service.SupportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminSupportServlet", urlPatterns = {"/admin/support"})
public class AdminSupportServlet extends HttpServlet {

    private SupportService supportService = new SupportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<SupportTickets> tickets = supportService.getAllTicketsForAdmin();
        request.setAttribute("ticketsList", tickets);
        request.getRequestDispatcher("/views/linhhqth08598/admin/support.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        try {
            int ticketId = Integer.parseInt(request.getParameter("ticketId"));
            String status = request.getParameter("status");

            String error = supportService.processTicket(ticketId, status, adminUser);

            if (error == null) {
                session.setAttribute("message", "Cập nhật trạng thái ticket thành công!");
            } else {
                session.setAttribute("error", error);
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/support");
    }
}