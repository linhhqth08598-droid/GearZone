package fourSome.util;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

            // LỖI CŨ Ở ĐÂY: Target="xl/styles.xml" (sai đường dẫn tương đối từ thư mục xl/_rels/)
            // SỬA LẠI: Target="styles.xml" để WPS Office và Excel nhận diện được màu sắc & viền!
            put(zip, "xl/_rels/workbook.xml.rels",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
              + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
              + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
              + "</Relationships>");

            // Bảng Style chuẩn đẹp: có viền bảng (border), màu Header xanh đậm, màu phân cấp Tổng Ngày / Tháng / Năm rõ ràng
            put(zip, "xl/styles.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
              + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
              + "<numFmts count=\"2\">"
              + "<numFmt numFmtId=\"164\" formatCode=\"#,##0\"/>"
              + "<numFmt numFmtId=\"165\" formatCode=\"#,##0.00\"/>"
              + "</numFmts>"
              + "<fonts count=\"6\">"
              + "<font><sz val=\"11\"/><name val=\"Calibri\"/><color rgb=\"FF1F2937\"/></font>" // 0: Text thường
              + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/><color rgb=\"FFFFFFFF\"/></font>" // 1: Header trắng đậm
              + "<font><sz val=\"11\"/><name val=\"Calibri\"/><color rgb=\"FF4B5563\"/></font>" // 2: Ngày tháng xám đậm dễ đọc
              + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/><color rgb=\"FF1E3A8A\"/></font>" // 3: Tổng Ngày (xanh đậm)
              + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/><color rgb=\"FF92400E\"/></font>" // 4: Tổng Tháng (nâu cam đậm)
              + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/><color rgb=\"FFFFFFFF\"/></font>" // 5: Tổng Năm / Tổng Cộng (trắng đậm)
              + "</fonts>"
              + "<fills count=\"7\">"
              + "<fill><patternFill patternType=\"none\"/></fill>"
              + "<fill><patternFill patternType=\"gray125\"/></fill>"
              + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF1D4ED8\"/><bgColor indexed=\"64\"/></patternFill></fill>" // 2: Header xanh dương đậm
              + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFF8FAFC\"/><bgColor indexed=\"64\"/></patternFill></fill>" // 3: Dòng xen kẽ (Zebra)
              + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFDBEAFE\"/><bgColor indexed=\"64\"/></patternFill></fill>" // 4: Tổng Ngày (xanh nhạt)
              + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFFEF3C7\"/><bgColor indexed=\"64\"/></patternFill></fill>" // 5: Tổng Tháng (vàng kem)
              + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF1E3A8A\"/><bgColor indexed=\"64\"/></patternFill></fill>" // 6: Tổng Năm / Tổng Cộng (xanh navy)
              + "</fills>"
              + "<borders count=\"2\">"
              + "<border><left/><right/><top/><bottom/><diagonal/></border>"
              + "<border>"
              + "<left style=\"thin\"><color rgb=\"FFD1D5DB\"/></left>"
              + "<right style=\"thin\"><color rgb=\"FFD1D5DB\"/></right>"
              + "<top style=\"thin\"><color rgb=\"FFD1D5DB\"/></top>"
              + "<bottom style=\"thin\"><color rgb=\"FFD1D5DB\"/></bottom>"
              + "<diagonal/>"
              + "</border>"
              + "</borders>"
              + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
              + "<cellXfs count=\"10\">"
              + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyBorder=\"1\"><alignment vertical=\"center\"/></xf>" // 0: Text trái
              + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>" // 1: Header
              + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"right\" vertical=\"center\"/></xf>" // 2: Số phải (chuẩn VN xxx.xxx)
              + "<xf numFmtId=\"0\" fontId=\"2\" fillId=\"0\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>" // 3: Ngày giữa
              + "<xf numFmtId=\"0\" fontId=\"3\" fillId=\"4\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"left\" vertical=\"center\"/></xf>" // 4: Label Tổng Ngày
              + "<xf numFmtId=\"0\" fontId=\"3\" fillId=\"4\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"right\" vertical=\"center\"/></xf>" // 5: Số Tổng Ngày
              + "<xf numFmtId=\"0\" fontId=\"4\" fillId=\"5\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"left\" vertical=\"center\"/></xf>" // 6: Label Tổng Tháng
              + "<xf numFmtId=\"0\" fontId=\"4\" fillId=\"5\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"right\" vertical=\"center\"/></xf>" // 7: Số Tổng Tháng
              + "<xf numFmtId=\"0\" fontId=\"5\" fillId=\"6\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"left\" vertical=\"center\"/></xf>" // 8: Label Tổng Năm
              + "<xf numFmtId=\"0\" fontId=\"5\" fillId=\"6\" borderId=\"1\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyBorder=\"1\" applyAlignment=\"1\"><alignment horizontal=\"right\" vertical=\"center\"/></xf>" // 9: Số Tổng Năm
              + "</cellXfs>"
              + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles></styleSheet>");

            List<Object[]> dataRows = new ArrayList<>(rows);
            int dateColIdx = detectDateColumn(headers, dataRows);

            StringBuilder sb = new StringBuilder(8192);
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
              .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
              .append("<cols>");
            for (int i = 0; i < headers.length; i++) {
                sb.append("<col min=\"").append(i + 1).append("\" max=\"").append(i + 1).append("\" width=\"24\" customWidth=\"1\"/>");
            }
            sb.append("</cols><sheetData>");

            // Header row (r=1)
            sb.append("<row r=\"1\" ht=\"28\" customHeight=\"1\">");
            for (int i = 0; i < headers.length; i++) {
                sb.append(strCell(col(i) + "1", headers[i], 1));
            }
            sb.append("</row>");

            boolean[] isNumericCol = detectNumericColumns(dataRows);
            int r = 2;

            if (dateColIdx != -1) {
                Map<String, List<Object[]>> byYear = groupByDate(dataRows, dateColIdx, "year");
                for (Map.Entry<String, List<Object[]>> y : byYear.entrySet()) {
                    String yearLabel = y.getKey();

                    Map<String, List<Object[]>> byMonth = groupByDate(y.getValue(), dateColIdx, "month");
                    for (Map.Entry<String, List<Object[]>> m : byMonth.entrySet()) {
                        String monthLabel = m.getKey();

                        Map<String, List<Object[]>> byDay = groupByDate(m.getValue(), dateColIdx, "day");
                        for (Map.Entry<String, List<Object[]>> d : byDay.entrySet()) {
                            for (Object[] row : d.getValue()) {
                                sb.append("<row r=\"").append(r).append("\" ht=\"22\" customHeight=\"1\">");
                                for (int i = 0; i < row.length; i++) {
                                    sb.append(buildCell(col(i) + r, row[i]));
                                }
                                sb.append("</row>");
                                r++;
                            }
                            // Tổng Ngày (Style 4 & 5 - Xanh nhạt)
                            r = addSummaryRow(sb, r, "TỔNG NGÀY " + d.getKey(), d.getValue(), headers, isNumericCol, dateColIdx, 4, 5);
                        }
                        // Tổng Tháng (Style 6 & 7 - Vàng kem)
                        r = addSummaryRow(sb, r, "TỔNG THÁNG " + monthLabel, m.getValue(), headers, isNumericCol, dateColIdx, 6, 7);
                    }
                    // Tổng Năm (Style 8 & 9 - Xanh Navy chữ trắng)
                    r = addSummaryRow(sb, r, "TỔNG NĂM " + yearLabel, y.getValue(), headers, isNumericCol, dateColIdx, 8, 9);
                }
            } else {
                for (Object[] data : dataRows) {
                    sb.append("<row r=\"").append(r).append("\" ht=\"22\" customHeight=\"1\">");
                    for (int i = 0; i < data.length; i++) {
                        sb.append(buildCell(col(i) + r, data[i]));
                    }
                    sb.append("</row>");
                    r++;
                }
                boolean hasTotal = false;
                for (int i = 0; i < headers.length; i++) {
                    if (isNumericCol[i] && isSummableHeader(headers[i])) {
                        hasTotal = true;
                        break;
                    }
                }
                if (hasTotal && !dataRows.isEmpty()) {
                    r = addSummaryRow(sb, r, "TỔNG CỘNG", dataRows, headers, isNumericCol, 0, 8, 9);
                }
            }

            sb.append("</sheetData></worksheet>");
            put(zip, "xl/worksheets/sheet1.xml", sb.toString());
        }
        return bos.toByteArray();
    }

    // Định dạng số chuẩn Việt Nam: dấu chấm ngăn cách hàng nghìn (xxx.xxx), dấu phẩy thập phân
    private static String formatVietnameseNumber(BigDecimal bd) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.##", symbols);
        return df.format(bd);
    }

    private static String buildCell(String ref, Object v) {
        if (v instanceof Number) {
            double d = ((Number) v).doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) d = 0;
            BigDecimal bd = BigDecimal.valueOf(d).stripTrailingZeros();
            // Xuất dạng chuỗi đã format chuẩn xxx.xxx căn phải để WPS / Excel hiển thị chính xác tuyệt đối theo kiểu Việt Nam
            return strCell(ref, formatVietnameseNumber(bd), 2);
        } else if (v instanceof LocalDateTime) {
            return strCell(ref, ((LocalDateTime) v).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), 3);
        } else if (v instanceof LocalDate) {
            return strCell(ref, ((LocalDate) v).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 3);
        } else {
            return strCell(ref, v == null ? "" : String.valueOf(v), 0);
        }
    }

    private static int addSummaryRow(StringBuilder sb, int r, String label, List<Object[]> rows,
                                     String[] headers, boolean[] isNumericCol, int labelColIdx,
                                     int labelStyle, int numStyle) {
        sb.append("<row r=\"").append(r).append("\" ht=\"24\" customHeight=\"1\">");
        for (int i = 0; i < headers.length; i++) {
            String ref = col(i) + r;
            if (i == labelColIdx) {
                sb.append(strCell(ref, label, labelStyle));
            } else if (isNumericCol[i] && isSummableHeader(headers[i])) {
                BigDecimal sum = BigDecimal.ZERO;
                for (Object[] row : rows) {
                    if (row[i] instanceof Number) {
                        sum = sum.add(BigDecimal.valueOf(((Number) row[i]).doubleValue()));
                    }
                }
                sb.append(strCell(ref, formatVietnameseNumber(sum.stripTrailingZeros()), numStyle));
            } else {
                sb.append(strCell(ref, "", labelStyle));
            }
        }
        sb.append("</row>");
        return r + 1;
    }

    private static int detectDateColumn(String[] headers, List<Object[]> rows) {
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].toLowerCase();
            if (h.contains("ngày") || h.contains("date") || h.contains("thời gian") || h.contains("time")) {
                for (Object[] row : rows) {
                    if (row[i] instanceof LocalDate || row[i] instanceof LocalDateTime || row[i] instanceof java.util.Date) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private static Map<String, List<Object[]>> groupByDate(List<Object[]> rows, int dateColIdx, String type) {
        Map<String, List<Object[]>> map = new LinkedHashMap<>();
        DateTimeFormatter fmt = type.equals("year") ? DateTimeFormatter.ofPattern("yyyy")
                             : type.equals("month") ? DateTimeFormatter.ofPattern("MM/yyyy")
                             : DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Object[] row : rows) {
            LocalDate d = null;
            Object raw = row[dateColIdx];
            if (raw instanceof LocalDate) d = (LocalDate) raw;
            else if (raw instanceof LocalDateTime) d = ((LocalDateTime) raw).toLocalDate();
            else if (raw instanceof java.util.Date) d = new java.sql.Date(((java.util.Date) raw).getTime()).toLocalDate();

            if (d != null) {
                String key = d.format(fmt);
                map.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
            }
        }
        return map;
    }

    private static boolean isSummableHeader(String header) {
        if (header == null) return false;
        String h = header.toLowerCase();
        return !(h.contains("%") || h.contains("tỷ lệ") || h.contains("điểm tb")
              || h.contains("trung bình") || h.contains("ngưỡng") || h.contains("năm")
              || h.contains("mã đơn") || h.contains("ngày") || h.contains("thời gian"));
    }

    private static boolean[] detectNumericColumns(List<Object[]> rows) {
        if (rows.isEmpty()) return new boolean[0];
        int cols = rows.get(0).length;
        boolean[] res = new boolean[cols];
        for (Object[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] instanceof Number) res[i] = true;
            }
        }
        return res;
    }

    private static void put(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String strCell(String ref, String text, int style) {
        return "<c r=\"" + ref + "\" t=\"inlineStr\" s=\"" + style + "\"><is><t xml:space=\"preserve\">" + esc(text) + "</t></is></c>";
    }

    private static String col(int i) {
        StringBuilder s = new StringBuilder();
        for (int n = i; n >= 0; n = n / 26 - 1) s.insert(0, (char) ('A' + n % 26));
        return s.toString();
    }

    private static String safeSheetName(String n) {
        String s = (n == null || n.trim().isEmpty() ? "Sheet1" : n).replaceAll("[\\[\\]:*?/\\\\]", " ").trim();
        return s.length() > 31 ? s.substring(0, 31) : s;
    }

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
