package Controller;

import DAO.OrderDAO;
import java.io.BufferedReader;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "PayosWebhookServlet", urlPatterns = {"/api/payos-webhook"})
public class PayosWebhookServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String payload = sb.toString();

        // Lấy trực tiếp trường "orderCode" bằng số từ JSON PayOS gửi về
        if (payload.contains("\"orderCode\":")) {
            int start = payload.indexOf("\"orderCode\":") + 12;
            int end = payload.indexOf(",", start);
            if (end == -1) end = payload.indexOf("}", start);
            
            // Xóa hết ký tự thừa, chỉ giữ lại số
            String orderCodeStr = payload.substring(start, end).trim().replaceAll("[^0-9]", "");
            
            if (!orderCodeStr.isEmpty()) {
                OrderDAO orderDAO = new OrderDAO();
                orderDAO.updatePaymentStatus(orderCodeStr, "Paid");
            }
        }
        
        response.setContentType("application/json");
        response.getWriter().print("{\"success\": true}");
    }
}