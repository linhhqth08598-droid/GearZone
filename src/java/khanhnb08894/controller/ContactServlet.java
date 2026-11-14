package khanhnb08894.controller;

import fourSome.url.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet(name = "ContactServlet", urlPatterns = {"/contact"})
public class ContactServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/khanhnb08894/contact.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String content = request.getParameter("content");

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "INSERT INTO contacts (full_name, phone, email, content, status, created_at) VALUES (?, ?, ?, ?, 'PENDING', GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, fullName);
                ps.setString(2, phone);
                ps.setString(3, email);
                ps.setString(4, content);
                
                if (ps.executeUpdate() > 0) {
                    session.setAttribute("message", "Cảm ơn bạn đã gửi yêu cầu! Chúng tôi sẽ liên hệ lại sớm nhất.");
                } else {
                    session.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/contact");
    }
}