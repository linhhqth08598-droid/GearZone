package khanhnb08894.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        java.util.List<fourSome.model.Parts> featured = new java.util.ArrayList<>();
        for (fourSome.model.Parts p : new khanhnb08894.dao.PartsDAO().getAll()) {
            if ("ACTIVE".equals(p.getStatus())) featured.add(p);
        }
        featured.sort((a, b) -> Integer.compare(b.getSoldCount() == null ? 0 : b.getSoldCount(), a.getSoldCount() == null ? 0 : a.getSoldCount()));
        request.setAttribute("featured", featured.size() > 8 ? featured.subList(0, 8) : featured);
        // Chuyển tiếp tới trang home.jsp
        request.getRequestDispatcher("/views/khanhnb08894/home.jsp").forward(request, response);
    }
}