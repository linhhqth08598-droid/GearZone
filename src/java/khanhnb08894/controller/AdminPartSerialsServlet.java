package khanhnb08894.controller;

import fourSome.url.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

/**
 * Admin xem danh sách Serial (S/N) của 1 linh kiện: đếm theo trạng thái, lọc, và với serial SOLD thì
 * hiện kèm mã đơn hàng đã mua. URL: /admin/part-serials?partId=1[&status=IN_STOCK][&q=SN-2026][&page=1]
 */
@WebServlet(name = "AdminPartSerialsServlet", urlPatterns = {"/admin/part-serials"})
public class AdminPartSerialsServlet extends HttpServlet {

    private static final int PAGE_SIZE = 100;

    private static String esc(String t) {
        return t == null ? "" : t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;").replace("\"", "&quot;");
    }

    private static String badge(String st) {
        switch (st == null ? "" : st) {
            case "IN_STOCK": return "<span class='badge bg-success'>Trong kho</span>";
            case "SOLD": return "<span class='badge bg-primary'>Đã bán</span>";
            case "RETURNED": return "<span class='badge bg-warning text-dark'>Khách trả</span>";
            case "DEFECTIVE": return "<span class='badge bg-danger'>Lỗi / hỏng</span>";
            default: return "<span class='badge bg-secondary'>" + esc(st) + "</span>";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        int partId;
        try { partId = Integer.parseInt(request.getParameter("partId")); }
        catch (Exception e) { response.sendRedirect(request.getContextPath() + "/admin/inventory"); return; }

        String status = request.getParameter("status");
        if (status != null && !status.matches("IN_STOCK|SOLD|RETURNED|DEFECTIVE")) status = null;
        String q = request.getParameter("q");
        if (q != null) q = q.trim();
        if (q != null && q.isEmpty()) q = null;
        int page = 1;
        try { page = Math.max(1, Integer.parseInt(request.getParameter("page"))); } catch (Exception ignored) { }

        String partName = "Part #" + partId, sku = "";
        int[] counts = new int[4]; // IN_STOCK, SOLD, RETURNED, DEFECTIVE
        int total = 0;
        StringBuilder rows = new StringBuilder();
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try (Connection con = DBConnect.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT part_name, product_code FROM parts WHERE part_id = ?")) {
                ps.setInt(1, partId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { partName = rs.getString(1); sku = rs.getString(2); } }
            }
            try (PreparedStatement ps = con.prepareStatement("SELECT status, COUNT(*) FROM part_serials WHERE part_id = ? GROUP BY status")) {
                ps.setInt(1, partId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String st = rs.getString(1); int c = rs.getInt(2);
                        if ("IN_STOCK".equals(st)) counts[0] = c; else if ("SOLD".equals(st)) counts[1] = c;
                        else if ("RETURNED".equals(st)) counts[2] = c; else if ("DEFECTIVE".equals(st)) counts[3] = c;
                    }
                }
            }
            String where = " WHERE s.part_id = ?" + (status != null ? " AND s.status = ?" : "") + (q != null ? " AND s.serial_number LIKE ?" : "");
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM part_serials s" + where)) {
                int i = 1; ps.setInt(i++, partId);
                if (status != null) ps.setString(i++, status);
                if (q != null) ps.setString(i++, "%" + q + "%");
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) total = rs.getInt(1); }
            }
            // Serial SOLD -> tìm đơn hàng chứa mã đó (serial_number trên order_items là chuỗi "SN1, SN2")
            String sql = "SELECT s.serial_number, s.status, s.imported_at, "
                    + "(SELECT TOP 1 o.order_code FROM order_items oi JOIN orders o ON o.order_id = oi.order_id "
                    + " WHERE oi.part_id = s.part_id AND oi.serial_number IS NOT NULL "
                    + " AND ',' + REPLACE(oi.serial_number, ' ', '') + ',' LIKE '%,' + s.serial_number + ',%' "
                    + " ORDER BY o.order_id DESC) AS order_code "
                    + "FROM part_serials s" + where + " ORDER BY s.imported_at ASC, s.serial_id ASC "
                    + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int i = 1; ps.setInt(i++, partId);
                if (status != null) ps.setString(i++, status);
                if (q != null) ps.setString(i++, "%" + q + "%");
                ps.setInt(i++, (page - 1) * PAGE_SIZE);
                ps.setInt(i, PAGE_SIZE);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String oc = rs.getString("order_code");
                        rows.append("<tr><td><code>").append(esc(rs.getString(1))).append("</code></td><td>")
                            .append(badge(rs.getString(2))).append("</td><td>")
                            .append(rs.getTimestamp(3) == null ? "" : fmt.format(rs.getTimestamp(3))).append("</td><td>")
                            .append(oc == null ? "<span class='text-muted'>—</span>" : "<b>#" + esc(oc) + "</b>")
                            .append("</td></tr>\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            rows.append("<tr><td colspan='4' class='text-danger'>Lỗi tải dữ liệu: ").append(esc(e.getMessage())).append("</td></tr>");
        }

        int pages = Math.max(1, (total + PAGE_SIZE - 1) / PAGE_SIZE);
        String base = request.getContextPath() + "/admin/part-serials?partId=" + partId
                + (status != null ? "&status=" + status : "") + (q != null ? "&q=" + java.net.URLEncoder.encode(q, "UTF-8") : "");

        try (PrintWriter out = response.getWriter()) {
            request.getRequestDispatcher("/views/components/admin_header.jsp").include(request, response);
            request.getRequestDispatcher("/views/components/admin_sidebar.jsp").include(request, response);
            out.flush();
            out.println("<div class='d-flex justify-content-between align-items-center mb-4'>");
            out.println("<h3><i class='fas fa-barcode me-2 text-primary'></i>Serial của: " + esc(partName)
                    + (sku.isEmpty() ? "" : " <small class='text-muted'>(" + esc(sku) + ")</small>") + "</h3>");
            out.println("<a href='" + request.getContextPath() + "/admin/inventory' class='btn btn-outline-secondary'><i class='fas fa-arrow-left me-1'></i> Tồn kho</a></div>");

            String[][] tabs = {{"", "Tất cả", String.valueOf(counts[0] + counts[1] + counts[2] + counts[3]), "dark"},
                {"IN_STOCK", "Trong kho", String.valueOf(counts[0]), "success"}, {"SOLD", "Đã bán", String.valueOf(counts[1]), "primary"},
                {"RETURNED", "Khách trả", String.valueOf(counts[2]), "warning"}, {"DEFECTIVE", "Lỗi / hỏng", String.valueOf(counts[3]), "danger"}};
            out.println("<div class='d-flex flex-wrap gap-2 mb-3'>");
            for (String[] t : tabs) {
                boolean active = t[0].equals(status == null ? "" : status);
                out.println("<a class='btn btn-" + (active ? "" : "outline-") + t[3] + "' href='" + request.getContextPath() + "/admin/part-serials?partId=" + partId
                        + (t[0].isEmpty() ? "" : "&status=" + t[0]) + "'>" + t[1] + " <span class='badge bg-light text-dark'>" + t[2] + "</span></a>");
            }
            out.println("</div>");
            out.println("<form class='row g-2 mb-3' method='GET' action='" + request.getContextPath() + "/admin/part-serials'>");
            out.println("<input type='hidden' name='partId' value='" + partId + "'>");
            if (status != null) out.println("<input type='hidden' name='status' value='" + status + "'>");
            out.println("<div class='col-md-4'><input name='q' value='" + esc(q) + "' class='form-control' placeholder='Tìm mã Serial...'></div>");
            out.println("<div class='col-auto'><button class='btn btn-primary'><i class='fas fa-search'></i> Tìm</button></div></form>");

            out.println("<div class='card shadow-sm border-0 rounded-4'><div class='card-body p-4'><div class='table-responsive'>");
            out.println("<table class='table table-hover align-middle'><thead class='table-light'><tr><th>Số Serial</th><th>Trạng thái</th><th>Ngày nhập kho</th><th>Đơn hàng đã bán</th></tr></thead><tbody>");
            out.println(rows.length() == 0 ? "<tr><td colspan='4' class='text-center text-muted py-4'>Không có Serial nào.</td></tr>" : rows.toString());
            out.println("</tbody></table></div>");
            out.println("<div class='d-flex justify-content-between align-items-center'><span class='text-muted small'>Tổng " + total + " Serial · Trang " + page + "/" + pages + "</span><div>");
            if (page > 1) out.println("<a class='btn btn-sm btn-outline-secondary me-1' href='" + base + "&page=" + (page - 1) + "'>&laquo; Trước</a>");
            if (page < pages) out.println("<a class='btn btn-sm btn-outline-secondary' href='" + base + "&page=" + (page + 1) + "'>Sau &raquo;</a>");
            out.println("</div></div></div></div>");
            out.flush();
            request.getRequestDispatcher("/views/components/footer.jsp").include(request, response);
        }
    }
}
