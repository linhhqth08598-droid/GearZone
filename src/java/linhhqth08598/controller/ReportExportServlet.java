package linhhqth08598.controller;

import fourSome.url.DBConnect;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "ReportExportServlet", urlPatterns = {"/admin/export-report"})
public class ReportExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String type = request.getParameter("type");
        if (type == null) type = "revenue"; // Mặc định xuất doanh thu

        // Thiết lập để tải file Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bao_cao_" + type + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo");
            Row headerRow = sheet.createRow(0);
            
            // Style cho Header (In đậm, nền xám)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            if (type.equals("revenue")) {
                // --- XUẤT BÁO CÁO DOANH THU ---
                String[] headers = {"Mã Đơn", "Ngày Đặt", "Tổng Tiền", "Trạng Thái"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                String sql = "SELECT order_id, created_at, final_amount, order_status FROM orders ORDER BY created_at DESC";
                try (Connection conn = DBConnect.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    int rowNum = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(rs.getInt("order_id"));
                        row.createCell(1).setCellValue(rs.getString("created_at"));
                        
                        // Định dạng tiền tệ
                        Cell moneyCell = row.createCell(2);
                        moneyCell.setCellValue(rs.getDouble("final_amount"));
                        CellStyle moneyStyle = workbook.createCellStyle();
                        moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
                        moneyCell.setCellStyle(moneyStyle);
                        
                        row.createCell(3).setCellValue(rs.getString("order_status"));
                    }
                }
            } else if (type.equals("inventory")) {
                // --- XUẤT BÁO CÁO TỒN KHO ---
                String[] headers = {"Tên Linh Kiện", "Số Lượng Tồn", "Số Lượng Đã Giữ", "Số Lượng Khả Dụng"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                String sql = "SELECT p.part_name, i.stock_quantity, i.reserved_quantity FROM inventory i JOIN parts p ON i.part_id = p.part_id";
                try (Connection conn = DBConnect.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    int rowNum = 1;
                    while (rs.next()) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(rs.getString("part_name"));
                        int stock = rs.getInt("stock_quantity");
                        int reserved = rs.getInt("reserved_quantity");
                        
                        row.createCell(1).setCellValue(stock);
                        row.createCell(2).setCellValue(reserved);
                        row.createCell(3).setCellValue(stock - reserved); // Tính số lượng khả dụng
                    }
                }
            }

            // Tự động co giãn cột cho đẹp
            for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);

            // Xuất file ra trình duyệt
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h3>Lỗi xuất file: " + e.getMessage() + "</h3>");
        }
    }
}