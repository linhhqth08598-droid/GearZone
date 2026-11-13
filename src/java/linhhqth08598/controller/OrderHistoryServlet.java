package linhhqth08598.controller;

import fourSome.model.Users;
import fourSome.model.OrderHistoryItem;
import linhhqth08598.dao.UserWishlistsDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderHistoryServlet", urlPatterns = {"/user/order-history"})
public class OrderHistoryServlet extends HttpServlet {

    private UserWishlistsDAO dao = new UserWishlistsDAO(); // Tạm dùng DAO này, hoặc bạn tách ra

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        List<OrderHistoryItem> history = dao.getOrderHistory(user.getUserId());
        request.setAttribute("historyList", history);

        request.getRequestDispatcher("/views/linhhqth08598/user/order_history.jsp").forward(request, response);
    }
}