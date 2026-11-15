package linhhqth08598.controller;

import linhhqth08598.service.ReportService;
import linhhqth08598.service.ReportService.Report;
import linhhqth08598.service.ReportService.ReportResult;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

/** UC 346–367: Thống kê chi tiết. /admin/report?type=...&from=yyyy-MM-dd&to=yyyy-MM-dd */
@WebServlet(name = "AdminReportServlet", urlPatterns = {"/admin/report"})
public class AdminReportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Report> reports = ReportService.getReports();

        String type = request.getParameter("type");
        Report current = ReportService.getReport(type);
        if (current == null) current = reports.values().iterator().next();

        LocalDate from = parseDate(request.getParameter("from"));
        LocalDate to = parseDate(request.getParameter("to"));
        if (from != null && to != null && from.isAfter(to)) {
            request.setAttribute("error", "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.");
            from = null;
            to = null;
        }

        try {
            ReportResult result = reportService.run(current.getKey(), from, to);
            request.setAttribute("result", result);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không lấy được dữ liệu báo cáo: " + e.getMessage());
        }

        request.setAttribute("reports", reports.values());
        request.setAttribute("current", current);
        request.setAttribute("from", from == null ? "" : from.toString());
        request.setAttribute("to", to == null ? "" : to.toString());
        request.getRequestDispatcher("/views/linhhqth08598/admin/reports.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
