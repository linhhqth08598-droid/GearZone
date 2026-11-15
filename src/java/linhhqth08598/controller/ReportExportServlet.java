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

/**
 * UC 368 (doanh thu) và UC 369 (tồn kho): 2 nút ở dashboard.
 * /admin/export-report?type=revenue|inventory  -> dùng chung ReportService + ExcelExportUtil
 * (xuất toàn bộ, không lọc ngày). Báo cáo khác / có lọc ngày: /admin/export (AdminExportServlet).
 */
@WebServlet(name = "ReportExportServlet", urlPatterns = {"/admin/export-report"})
public class ReportExportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        String key = "inventory".equals(type) ? "inventory_all" : "revenue_orders";
        try {
            Report report = ReportService.getReport(key);
            // Khoảng ngày rất rộng để lấy toàn bộ dữ liệu cho báo cáo "dated"
            ReportResult result = reportService.run(key, null, null);
            ExcelExportUtil.send(response, "bao_cao_" + ("inventory".equals(type) ? "ton_kho" : "doanh_thu"),
                    "Báo cáo", result.getHeaders(), result.getRows());
        } catch (Throwable t) {
            t.printStackTrace();
            ExcelExportUtil.sendError(response, t);
        }
    }
}
