package linhhqth08598.controller;

import fourSome.util.ExcelExportUtil;
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

/**
 * UC 368–370: Xuất Excel bất kỳ báo cáo nào trong ReportService.
 * /admin/export?type=revenue_orders|inventory_all|rev_time|...&from=...&to=...
 * (Hai nút cũ ở dashboard vẫn dùng /admin/export-report của ReportExportServlet.)
 */
@WebServlet(name = "AdminExportServlet", urlPatterns = {"/admin/export"})
public class AdminExportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Report report = ReportService.getReport(request.getParameter("type"));
        if (report == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Loại báo cáo không hợp lệ");
            return;
        }
        try {
            ReportResult result = reportService.run(report.getKey(), parseDate(request.getParameter("from")), parseDate(request.getParameter("to")));
            ExcelExportUtil.send(response, "bao_cao_" + report.getKey(), "Báo cáo", result.getHeaders(), result.getRows());
        } catch (Throwable t) {
            t.printStackTrace();
            ExcelExportUtil.sendError(response, t);
        }
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
