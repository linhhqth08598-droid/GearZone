package khanhnb08894.controller;

import khanhnb08894.dao.InventoryTransactionsDAO;
import fourSome.model.InventoryTransactions;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminInventoryTransactionsServlet", urlPatterns = {"/admin/inventory-transactions"})
public class AdminInventoryTransactionsServlet extends HttpServlet {

    private InventoryTransactionsDAO transactionDAO;

    @Override
    public void init() throws ServletException {
        transactionDAO = new InventoryTransactionsDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        List<InventoryTransactions> list = transactionDAO.getAll();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='vi'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Lịch sử Giao dịch Kho - GearZone</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
            out.println("</head>");
            out.println("<body class='bg-light'>");
            out.println("<div class='container py-5'>");
            
            // Header
            out.println("<div class='d-flex justify-content-between align-items-center mb-4'>");
            out.println("<h2><i class='fas fa-history me-2 text-primary'></i>Lịch Sử Biến Động Kho</h2>");
            out.println("<div>");
            out.println("<a href='" + request.getContextPath() + "/admin/inventory' class='btn btn-outline-secondary me-2'><i class='fas fa-warehouse me-1'></i> Xem Tồn Kho</a>");
            out.println("<a href='" + request.getContextPath() + "/admin/purchases' class='btn btn-outline-primary'><i class='fas fa-dolly me-1'></i> Quản lý Đơn Nhập</a>");
            out.println("</div>");
            out.println("</div>");

            // Bảng danh sách giao dịch
            out.println("<div class='card shadow-sm border-0 rounded-4'>");
            out.println("<div class='card-body p-4'>");
            out.println("<div class='table-responsive'>");
            out.println("<table class='table table-hover align-middle'>");
            out.println("<thead class='table-light'><tr><th>ID</th><th>Part ID</th><th>Loại GD</th><th>Thay đổi</th><th>Trước $\rightarrow$ Sau</th><th>Tham chiếu</th><th>Lý do</th><th>Thời gian</th></tr></thead>");
            out.println("<tbody>");

            if (list.isEmpty()) {
                out.println("<tr><td colspan='8' class='text-center text-muted py-4'>Chưa có lịch sử giao dịch nào.</td></tr>");
            } else {
                for (InventoryTransactions item : list) {
                    String typeBadge = "IMPORT".equalsIgnoreCase(item.getTransactionType()) 
                        ? "<span class='badge bg-success'>NHẬP (IMPORT)</span>" 
                        : "<span class='badge bg-primary'>" + item.getTransactionType() + "</span>";
                    
                    out.println("<tr>");
                    out.println("<td>#" + item.getTransactionId() + "</td>");
                    out.println("<td><code>Part #" + item.getPartId() + "</code></td>");
                    out.println("<td>" + typeBadge + "</td>");
                    out.println("<td class='fw-bold text-success'>+" + item.getQuantityChange() + "</td>");
                    out.println("<td><span class='text-muted'>" + item.getQuantityBefore() + "</span> $\rightarrow$ <strong class='text-dark'>" + item.getQuantityAfter() + "</strong></td>");
                    out.println("<td><small class='text-muted'>" + item.getReferenceType() + " (ID: " + item.getReferenceId() + ")</small></td>");
                    out.println("<td>" + (item.getReason() != null ? item.getReason() : "-") + "</td>");
                    out.println("<td><small>" + item.getCreatedAt() + "</small></td>");
                    out.println("</tr>");
                }
            }

            out.println("</tbody></table></div></div></div>");
            out.println("</div>");
            out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
            out.println("</body></html>");
        }
    }
}