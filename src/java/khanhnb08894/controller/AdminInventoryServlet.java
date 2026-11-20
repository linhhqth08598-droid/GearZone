package khanhnb08894.controller;

import khanhnb08894.dao.InventoryDAO;
import fourSome.model.Inventory;
import fourSome.url.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminInventoryServlet", urlPatterns = {"/admin/inventory"})
public class AdminInventoryServlet extends HttpServlet {

    private InventoryDAO inventoryDAO;

    @Override
    public void init() throws ServletException {
        inventoryDAO = new InventoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        List<Inventory> inventoryList = inventoryDAO.getAll();
        java.util.Map<Integer, String> partNames = new java.util.HashMap<>();
        for (fourSome.model.Parts p : new khanhnb08894.dao.PartsDAO().getAll()) partNames.put(p.getPartId(), p.getProductCode() + " - " + p.getPartName());

        try (PrintWriter out = response.getWriter()) {
            request.getRequestDispatcher("/views/components/admin_header.jsp").include(request, response);
            request.getRequestDispatcher("/views/components/admin_sidebar.jsp").include(request, response);
            out.flush();
            
            // Header
            out.println("<div class='d-flex justify-content-between align-items-center mb-4'>");
            out.println("<h2><i class='fas fa-warehouse me-2 text-primary'></i>Quản Lý Tồn Kho Linh Kiện</h2>");
            out.println("<a href='" + request.getContextPath() + "/admin/purchases' class='btn btn-outline-primary'><i class='fas fa-dolly me-1'></i> Quản lý Đơn Nhập Hàng</a>");
            out.println("</div>");

            // Bảng danh sách tồn kho
            out.println("<div class='card shadow-sm border-0 rounded-4'>");
            out.println("<div class='card-body p-4'>");
            out.println("<div class='table-responsive'>");
            out.println("<table class='table table-hover align-middle'>");
            out.println("<thead class='table-light'><tr><th>ID</th><th>Linh kiện</th><th>Tồn kho</th><th>Đang giữ (Reserved)</th><th>Hỏng / Lỗi</th><th>Ngưỡng báo động</th><th class='text-center'>Trạng thái</th><th class='text-center'>Serial</th></tr></thead>");
            out.println("<tbody>");

            if (inventoryList.isEmpty()) {
                out.println("<tr><td colspan='8' class='text-center text-muted py-4'>Chưa có dữ liệu tồn kho.</td></tr>");
            } else {
                for (Inventory item : inventoryList) {
                    int stock = item.getStockQuantity();
                    int threshold = item.getLowStockThreshold();
                    String badge = stock > threshold ? "<span class='badge bg-success'>Ổn định</span>" : (stock > 0 ? "<span class='badge bg-warning text-dark'>Sắp hết</span>" : "<span class='badge bg-danger'>Hết hàng</span>");
                    
                    out.println("<tr>");
                    out.println("<td>#" + item.getInventoryId() + "</td>");
                    String pn = partNames.get(item.getPartId());
                    out.println("<td><span class='fw-semibold'>" + (pn == null ? "Part #" + item.getPartId() : pn.replace("&", "&amp;").replace("<", "&lt;")) + "</span> <small class='text-muted'>(#" + item.getPartId() + ")</small></td>");
                    out.println("<td class='fw-bold text-primary'>" + stock + "</td>");
                    out.println("<td>" + item.getReservedQuantity() + "</td>");
                    out.println("<td class='text-danger'>" + item.getDamagedQuantity() + "</td>");
                    out.println("<td>" + threshold + "</td>");
                    out.println("<td class='text-center'>" + badge + "</td>");
                    out.println("<td class='text-center text-nowrap'><a class='btn btn-sm btn-outline-dark text-nowrap py-0 px-2' style='font-size:13px' href='" + request.getContextPath() + "/admin/part-serials?partId=" + item.getPartId() + "'><i class='fas fa-barcode'></i> Xem Serial</a></td>");
                    out.println("</tr>");
                }
            }

            out.println("</tbody></table></div></div></div>");
            out.flush();
            request.getRequestDispatcher("/views/components/footer.jsp").include(request, response);
        }
    }
}