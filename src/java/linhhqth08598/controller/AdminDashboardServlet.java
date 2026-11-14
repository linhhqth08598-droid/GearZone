package linhhqth098.controller;

import linhhqth098.service.ReportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy số liệu KPI
        request.setAttribute("totalRevenue", reportService.getTotalRevenue());
        request.setAttribute("totalOrders", reportService.getTotalOrders());
        request.setAttribute("totalPartsSold", reportService.getTotalPartsSold());
        
        // Lấy dữ liệu cho biểu đồ
        Map<String, Double> revenueByCategory = reportService.getRevenueByCategory();
        Map<String, Integer> topParts = reportService.getTopSellingParts();
        
        // Chuyển Map thành dạng String JSON thủ công để đẩy sang JSP cho dễ dùng
        StringBuilder categories = new StringBuilder("[");
        StringBuilder revenues = new StringBuilder("[");
        for (Map.Entry<String, Double> entry : revenueByCategory.entrySet()) {
            categories.append("'").append(entry.getKey()).append("',");
            revenues.append(entry.getValue()).append(",");
        }
        if (categories.length() > 1) { categories.setLength(categories.length() - 1); revenues.setLength(revenues.length() - 1); }
        categories.append("]"); revenues.append("]");
        
        StringBuilder partNames = new StringBuilder("[");
        StringBuilder partQtys = new StringBuilder("[");
        for (Map.Entry<String, Integer> entry : topParts.entrySet()) {
            partNames.append("'").append(entry.getKey()).append("',");
            partQtys.append(entry.getValue()).append(",");
        }
        if (partNames.length() > 1) { partNames.setLength(partNames.length() - 1); partQtys.setLength(partQtys.length() - 1); }
        partNames.append("]"); partQtys.append("]");

        request.setAttribute("categoryLabels", categories.toString());
        request.setAttribute("categoryData", revenues.toString());
        request.setAttribute("partLabels", partNames.toString());
        request.setAttribute("partData", partQtys.toString());

        request.getRequestDispatcher("/views/linhhqth08598/admin/dashboard.jsp").forward(request, response);
    }
}