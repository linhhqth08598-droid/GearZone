package fourSome.util;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Xuất file .xlsx KHÔNG cần thư viện ngoài (không Apache POI): tự ghi các file XML rồi nén ZIP bằng java.util.zip.
 * Tạo toàn bộ file trong bộ nhớ trước, thành công mới gửi header + nội dung.
 */
public class ExcelExportUtil {

    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static byte[] build(String sheetName, String[] headers, List<Object[]> rows) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bos, StandardCharsets.UTF_8)) {
            put(zip, "[Content_Types].xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
              + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
              + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
              + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
              + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
              + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
              + "</Types>");
            put(zip, "_rels/.rels",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
              + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
              + "</Relationships>");
            put(zip, "xl/workbook.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
              + "<sheets><sheet name=\"" + esc(safeSheetName(sheetName)) + "\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            put(zip, "xl/_rels/workbook.xml.rels",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
              + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
              + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
              + "</Relationships>");
            // style 0: mặc định, 1: tiêu đề (đậm + nền xám), 2: số có dấu phân cách nghìn
            put(zip, "xl/styles.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
              + "<numFmts count=\"1\"><numFmt numFmtId=\"164\" formatCode=\"#,##0.##\"/></numFmts>"
              + "<fonts count=\"2\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font><font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts>"
              + "<fills count=\"3\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill>"
              + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFD9D9D9\"/><bgColor indexed=\"64\"/></patternFill></fill></fills>"
              + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
              + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
              + "<cellXfs count=\"3\">"
              + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
              + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\"/>"
              + "<xf numFmtId=\"164\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyNumberFormat=\"1\"/>"
              + "</cellXfs><cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles></styleSheet>");

            StringBuilder sb = new StringBuilder(8192);
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
              .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
              .append("<cols>");
            for (int i = 0; i < headers.length; i++) {
                sb.append("<col min=\"").append(i + 1).append("\" max=\"").append(i + 1).append("\" width=\"22\" customWidth=\"1\"/>");
            }
            sb.append("</cols><sheetData><row r=\"1\">");
            for (int i = 0; i < headers.length; i++) {
                sb.append(strCell(col(i) + "1", headers[i], 1));
            }
            sb.append("</row>");
            int r = 2;
            for (Object[] data : rows) {
                sb.append("<row r=\"").append(r).append("\">");
                for (int i = 0; i < data.length; i++) {
                    String ref = col(i) + r;
                    Object v = data[i];
                    if (v instanceof Number) {
                        double d = ((Number) v).doubleValue();
                        if (Double.isNaN(d) || Double.isInfinite(d)) d = 0;
                        sb.append("<c r=\"").append(ref).append("\" s=\"2\"><v>").append(java.math.BigDecimal.valueOf(d).toPlainString()).append("</v></c>");
                    } else {
                        sb.append(strCell(ref, v == null ? "" : String.valueOf(v), 0));
                    }
                }
                sb.append("</row>");
                r++;
            }
            sb.append("</sheetData></worksheet>");
            put(zip, "xl/worksheets/sheet1.xml", sb.toString());
        }
        return bos.toByteArray();
    }

    private static void put(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String strCell(String ref, String text, int style) {
        return "<c r=\"" + ref + "\" t=\"inlineStr\" s=\"" + style + "\"><is><t xml:space=\"preserve\">" + esc(text) + "</t></is></c>";
    }

    /** Tên cột Excel: 0->A, 25->Z, 26->AA ... */
    private static String col(int i) {
        StringBuilder s = new StringBuilder();
        for (int n = i; n >= 0; n = n / 26 - 1) s.insert(0, (char) ('A' + n % 26));
        return s.toString();
    }

    private static String safeSheetName(String n) {
        String s = (n == null || n.trim().isEmpty() ? "Sheet1" : n).replaceAll("[\\[\\]:*?/\\\\]", " ").trim();
        return s.length() > 31 ? s.substring(0, 31) : s;
    }

    /** Escape XML và bỏ ký tự điều khiển không hợp lệ. */
    private static String esc(String s) {
        StringBuilder b = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '&') b.append("&amp;");
            else if (c == '<') b.append("&lt;");
            else if (c == '>') b.append("&gt;");
            else if (c == '"') b.append("&quot;");
            else if (c < 0x20 && c != '\t' && c != '\n' && c != '\r') continue;
            else b.append(c);
        }
        return b.toString();
    }

    /** Gửi file xlsx; mọi lỗi đều được báo bằng trang lỗi 500 dễ đọc. */
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
                + "<p>Xem chi tiết trong tab Output của Tomcat.</p>");
    }
}