package khanhnb08894.controller;

import fourSome.model.InventoryTransactions;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import khanhnb08894.dao.InventoryTransactionsDAO;
import khanhnb08894.dao.PartsDAO;

@WebServlet(name = "AdminInventoryTransactionsServlet", urlPatterns = {"/admin/inventory-transactions"})
public class AdminInventoryTransactionsServlet extends HttpServlet {

    private InventoryTransactionsDAO transactionDAO;

    @Override
    public void init() throws ServletException {
        transactionDAO = new InventoryTransactionsDAO();
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        List<InventoryTransactions> list = transactionDAO.getAll();
        Map<Integer, String> partLabels = new HashMap<>();
        try {
            for (fourSome.model.Parts p : new PartsDAO().getAll()) {
                partLabels.put(p.getPartId(), p.getProductCode() + " - " + p.getPartName());
            }
        } catch (Exception ignored) {
        }

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try (PrintWriter out = response.getWriter()) {
            request.getRequestDispatcher("/views/components/admin_header.jsp").include(request, response);
            request.getRequestDispatcher("/views/components/admin_sidebar.jsp").include(request, response);
            out.flush();

            out.println("<div class='d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4'>");
            out.println("<h1 class='h2 mb-0'><i class='fas fa-history me-2 text-primary'></i>Lịch Sử Biến Động Kho</h1>");
            out.println("<div class='d-flex gap-2'>");
            out.println("<a href='" + request.getContextPath() + "/admin/inventory' class='btn btn-outline-secondary'><i class='fas fa-warehouse me-1'></i> Xem Tồn Kho</a>");
            out.println("<a href='" + request.getContextPath() + "/admin/purchases' class='btn btn-outline-primary'><i class='fas fa-dolly me-1'></i> Quản lý Đơn Nhập</a>");
            out.println("</div>");
            out.println("</div>");

            out.println("<div class='card shadow-sm border-0 rounded-4'>");
            out.println("<div class='card-body p-4'>");
            out.println("<div class='table-responsive'>");
            out.println("<table class='table table-hover align-middle mb-0'>");
            out.println("<thead class='table-light'><tr><th>ID</th><th>Linh kiện</th><th>Loại GD</th><th>Thay đổi</th><th>Trước &rarr; Sau</th><th>Tham chiếu</th><th>Lý do</th><th>Thời gian</th></tr></thead>");
            out.println("<tbody>");

            if (list.isEmpty()) {
                out.println("<tr><td colspan='8' class='text-center text-muted py-4'>Chưa có lịch sử giao dịch nào.</td></tr>");
            } else {
                for (InventoryTransactions item : list) {
                    String type = item.getTransactionType() == null ? "" : item.getTransactionType().trim().toUpperCase();
                    String typeBadge;
                    if ("IMPORT".equals(type)) {
                        typeBadge = "<span class='badge bg-success'>NHẬP (IMPORT)</span>";
                    } else if ("EXPORT".equals(type)) {
                        typeBadge = "<span class='badge bg-danger'>XUẤT (EXPORT)</span>";
                    } else if ("RELEASE".equals(type)) {
                        typeBadge = "<span class='badge bg-info text-dark'>HOÀN GIỮ (RELEASE)</span>";
                    } else if ("RESERVE".equals(type)) {
                        typeBadge = "<span class='badge bg-primary'>GIỮ HÀNG (RESERVE)</span>";
                    } else {
                        typeBadge = "<span class='badge bg-secondary'>" + esc(type) + "</span>";
                    }

                    int chg = item.getQuantityChange();
                    String chgText = chg > 0 ? ("+" + chg) : String.valueOf(chg);
                    String chgClass = chg > 0 ? "text-success" : (chg < 0 ? "text-danger" : "text-muted");

                    String label = partLabels.get(item.getPartId());
                    String partDisplay = label != null
                            ? (esc(label) + " <small class='text-muted'>(#" + item.getPartId() + ")</small>")
                            : ("Part #" + item.getPartId());

                    String refText = (item.getReferenceType() != null ? esc(item.getReferenceType()) : "-")
                            + (item.getReferenceId() != null && item.getReferenceId() > 0 ? (" #" + item.getReferenceId()) : "");

                    out.println("<tr>");
                    out.println("<td>#" + item.getTransactionId() + "</td>");
                    out.println("<td class='fw-semibold'>" + partDisplay + "</td>");
                    out.println("<td>" + typeBadge + "</td>");
                    out.println("<td class='fw-bold " + chgClass + "'>" + chgText + "</td>");
                    out.println("<td><span class='text-muted'>" + item.getQuantityBefore() + "</span> &rarr; <strong class='text-dark'>" + item.getQuantityAfter() + "</strong></td>");
                    out.println("<td><small class='text-muted'>" + refText + "</small></td>");
                    out.println("<td>" + (item.getReason() != null ? esc(item.getReason()) : "-") + "</td>");
                    out.println("<td><small>" + (item.getCreatedAt() != null ? fmt.format(item.getCreatedAt()) : "-") + "</small></td>");
                    out.println("</tr>");
                }
            }

            out.println("</tbody></table></div></div></div>");
            out.flush();
            request.getRequestDispatcher("/views/components/footer.jsp").include(request, response);
        }
    }
}
