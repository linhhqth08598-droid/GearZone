package linhhqth08598.controller;

import fourSome.model.Users;
import linhhqth08598.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "UserProfileServlet", urlPatterns = {"/user/profile"})
public class UserProfileServlet extends HttpServlet {

    private UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("loggedUser") == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        request.getRequestDispatcher("/views/linhhqth08598/user/profile.jsp").forward(request, response);
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

        String action = request.getParameter("action");
        if ("updateProfile".equals(action)) {
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");

            String error = userService.updateProfile(user.getUserId(), fullName, phone);
            
            if (error == null) {
                // Cập nhật lại Session để hiển thị tên mới ngay lập tức
                user.setFullName(fullName);
                user.setPhoneNumber(phone);
                session.setAttribute("loggedUser", user);
                session.setAttribute("message", "Cập nhật thông tin thành công!");
            } else {
                session.setAttribute("error", error);
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/user/profile");
    }
}