package linhhhqth08598.controller;

import fourSome.model.Users;
import fourSome.model.Notifications;
import linhhqth08598.service.NotificationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "NotificationServlet", urlPatterns = {"/user/notifications"})
public class NotificationServlet extends HttpServlet {

    private NotificationService service = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if ("markRead".equals(action)) {
            service.markAllRead(user.getUserId());
            response.sendRedirect(request.getContextPath() + "/user/notifications");
            return;
        }

        List<Notifications> list = service.getNotifications(user.getUserId());
        request.setAttribute("notifications", list);
        
        // Đánh dấu đã đọc sau khi load xong
        service.markAllRead(user.getUserId());

        request.getRequestDispatcher("/views/linhhqth08598/user/notifications.jsp").forward(request, response);
    }
}