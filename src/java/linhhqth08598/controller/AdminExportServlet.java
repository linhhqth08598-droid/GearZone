package linhhqth08598.controller;

import linhhqth08598.service.ReportService;
import linhhqth08598.service.ReportService.Report;
import linhhqth08598.service.ReportService.ReportResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

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

        ReportResult result;
        try {
            result = reportService.run(report.getKey(), parseDate(request.getParameter("from")), parseDate(request.getParameter("to")));
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h3>Lỗi xuất file: " + e.getMessage().replace("<", "&lt;") + "</h3>");
            return;
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bao_cao_" + report.getKey() + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.##"));

            String[] headers = result.getHeaders();
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Object[]> rows = result.getRows();
            int rowNum = 1;
            for (Object[] data : rows) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < data.length; i++) {
                    Cell cell = row.createCell(i);
                    if (data[i] instanceof Number) {
                        cell.setCellValue(((Number) data[i]).doubleValue());
                        cell.setCellStyle(numberStyle);
                    } else {
                        cell.setCellValue(String.valueOf(data[i]));
                    }
                }
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
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
