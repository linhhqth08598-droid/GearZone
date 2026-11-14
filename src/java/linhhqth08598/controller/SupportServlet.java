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

@WebServlet(name = "SupportServlet", urlPatterns = {"/user/support"})
public class SupportServlet extends HttpServlet {

    private SupportService supportService = new SupportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        List<SupportTickets> tickets = supportService.getUserTickets(user.getUserId());
        request.setAttribute("tickets", tickets);
        request.getRequestDispatcher("/views/linhhqth08598/support/ticket.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String type = request.getParameter("type");
        String subject = request.getParameter("subject");
        String content = request.getParameter("content");

        String error = supportService.createTicket(user.getUserId(), type, subject, content);
        
        if (error == null) {
            session.setAttribute("message", "Gửi yêu cầu hỗ trợ thành công! Chúng tôi sẽ phản hồi sớm nhất.");
        } else {
            session.setAttribute("error", error);
        }
        
        response.sendRedirect(request.getContextPath() + "/user/support");
    }
}