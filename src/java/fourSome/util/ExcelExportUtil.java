package fourSome.util;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Xuất Excel an toàn: tạo toàn bộ file TRONG BỘ NHỚ trước, thành công mới gửi header + nội dung.
 * Nếu lỗi (SQL, thiếu thư viện, thiếu font...) thì trả trang lỗi rõ ràng thay vì một phản hồi hỏng
 * (trình duyệt báo ERR_INVALID_RESPONSE).
 */
public class ExcelExportUtil {

    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static byte[] build(String sheetName, String[] headers, List<Object[]> rows) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet(sheetName);

            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle numberStyle = wb.createCellStyle();
            numberStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0.##"));

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }
            int r = 1;
            for (Object[] data : rows) {
                Row row = sheet.createRow(r++);
                for (int i = 0; i < data.length; i++) {
                    Cell c = row.createCell(i);
                    if (data[i] instanceof Number) {
                        c.setCellValue(((Number) data[i]).doubleValue());
                        c.setCellStyle(numberStyle);
                    } else {
                        c.setCellValue(data[i] == null ? "" : String.valueOf(data[i]));
                    }
                }
            }
            // autoSizeColumn cần font hệ thống; máy chủ thiếu font sẽ ném Error -> dùng độ rộng cố định.
            for (int i = 0; i < headers.length; i++) {
                try {
                    sheet.autoSizeColumn(i);
                } catch (Throwable t) {
                    sheet.setColumnWidth(i, 22 * 256);
                }
            }
            wb.write(bos);
            return bos.toByteArray();
        }
    }

    /** Gửi file xlsx; mọi lỗi (kể cả Error) đều được báo bằng trang lỗi 500 dễ đọc. */
    public static void send(HttpServletResponse response, String fileBaseName, String sheetName,
                            String[] headers, List<Object[]> rows) throws IOException {
        byte[] data;
        try {
            data = build(sheetName, headers, rows);
        } catch (Throwable t) {
            t.printStackTrace();
            sendError(response, t);
            return;
        }
        String encoded = URLEncoder.encode(fileBaseName + ".xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        response.reset();
        response.setContentType(XLSX);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileBaseName.replaceAll("[^A-Za-z0-9_.-]", "_")
                + ".xlsx\"; filename*=UTF-8''" + encoded);
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    public static void sendError(HttpServletResponse response, Throwable t) throws IOException {
        if (response.isCommitted()) return;
        response.reset();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/html;charset=UTF-8");
        String msg = t.getClass().getSimpleName() + ": " + (t.getMessage() == null ? "" : t.getMessage());
        response.getWriter().println("<h3>Lỗi xuất file Excel</h3><pre>" + msg.replace("&", "&amp;").replace("<", "&lt;") + "</pre>"
                + "<p>Kiểm tra: đã thêm đủ thư viện Apache POI (poi, poi-ooxml, xmlbeans, commons-*, log4j-api) vào Libraries chưa; "
                + "xem chi tiết trong tab Output của Tomcat.</p>");
    }
}
