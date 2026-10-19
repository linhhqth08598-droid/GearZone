package Controller;

import DAO.OrderDAO;
import Models.Order;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CheckPaymentServlet", urlPatterns = {"/api/check-payment"})
public class CheckPaymentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String orderCode = request.getParameter("orderCode");
        OrderDAO orderDAO = new OrderDAO();
        
        Order order = orderDAO.getOrderByCode(orderCode); 
        
        if (order != null && "Paid".equalsIgnoreCase(order.getPaymentStatus())) {
            out.print("{\"status\": \"Paid\"}");
        } else {
            out.print("{\"status\": \"Pending\"}");
        }
        out.flush();
    }
}