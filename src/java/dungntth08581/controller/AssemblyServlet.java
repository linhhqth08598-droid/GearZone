package dungntth08581.controller;

import fourSome.model.Users;
import linhhqth08598.service.ReviewService;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * BẢN GỌN (làm hộ Dũng): trang "Dịch vụ lắp ráp của tôi" - xem các đơn lắp ráp và đánh giá (UC 301 của Linh).
 * Đăng ký/đặt lịch lắp ráp (UC 251–255...) vẫn là phần của Dũng.
 */
@WebServlet(name = "AssemblyServlet", urlPatterns = {"/assembly"})
public class AssemblyServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        request.setAttribute("assemblies", reviewService.getAssemblies(user.getUserId()));
        request.getRequestDispatcher("/views/dungntth08581/user/assembly.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/assembly");
    }
}
