package linhhqth08598.controller;

import fourSome.util.Constants;
import fourSome.util.PaginationUtil;
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
        // Gộp các dòng sản phẩm cùng một đơn vào một nhóm (giữ thứ tự mới nhất trước)
        java.util.Map<Integer, List<OrderHistoryItem>> grouped = new java.util.LinkedHashMap<>();
        for (OrderHistoryItem it : history) {
            grouped.computeIfAbsent(it.getOrderId(), k -> new java.util.ArrayList<>()).add(it);
        }
        List<List<OrderHistoryItem>> orders = new java.util.ArrayList<>(grouped.values());
        PaginationUtil page = new PaginationUtil(request.getParameter("page"), Constants.DEFAULT_PAGE_SIZE, orders.size());
        orders = page.slice(orders);
        request.setAttribute("pagination", page);
        request.setAttribute("historyList", orders);

        request.getRequestDispatcher("/views/linhhqth08598/user/order_history.jsp").forward(request, response);
    }
}