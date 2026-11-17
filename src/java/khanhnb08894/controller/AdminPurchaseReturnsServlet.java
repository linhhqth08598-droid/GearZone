package khanhnb08894.controller;

import khanhnb08894.dao.SupplierReturnsDAO;
import fourSome.model.SupplierReturns;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminPurchaseReturnsServlet", urlPatterns = {"/admin/purchase-returns", "/admin/supplier-returns", "/admin/supplier-return"})
public class AdminPurchaseReturnsServlet extends HttpServlet {

    private SupplierReturnsDAO supplierReturnsDAO;

    @Override
    public void init() throws ServletException {
        supplierReturnsDAO = new SupplierReturnsDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        List<SupplierReturns> returnList = supplierReturnsDAO.getAll();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='vi'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Trả hàng Nhà Cung Cấp - GearZone</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
            out.println("</head>");
            out.println("<body class='bg-light'>");
            out.println("<div class='container py-5'>");
            
            // Header & Điều hướng nhanh
            out.println("<div class='d-flex justify-content-between align-items-center mb-4'>");
            out.println("<h2><i class='fas fa-undo me-2 text-danger'></i>Quản Lý Trả Hàng Nhà Cung Cấp</h2>");
            out.println("<div>");
            out.println("<a href='" + request.getContextPath() + "/admin/purchases' class='btn btn-outline-primary me-2'><i class='fas fa-dolly me-1'></i> Đơn Nhập Hàng</a>");
            out.println("<a href='" + request.getContextPath() + "/admin/inventory' class='btn btn-outline-secondary'><i class='fas fa-warehouse me-1'></i> Tồn Kho</a>");
            out.println("</div>");
            out.println("</div>");

            // Card thông báo
            out.println("<div class='alert alert-info shadow-sm mb-4'>");
            out.println("<i class='fas fa-info-circle me-2'></i> Phân hệ này quản lý danh sách phiếu hoàn trả linh kiện lỗi, hỏng cho Nhà cung cấp dựa trên dữ liệu bảng <code>supplier_returns</code>.");
            out.println("</div>");

            // Bảng danh sách phiếu trả hàng
            out.println("<div class='card shadow-sm border-0 rounded-4'>");
            out.println("<div class='card-body p-4'>");
            out.println("<div class='table-responsive'>");
            out.println("<table class='table table-hover align-middle'>");
            out.println("<thead class='table-light'><tr><th>ID</th><th>Mã Phiếu</th><th>Nhà Cung Cấp</th><th>Đơn PO</th><th>Linh Kiện</th><th>Số Lượng</th><th>Lỗi (Defect)</th><th>Trạng thái</th><th class='text-center'>Ngày tạo</th></tr></thead>");
            out.println("<tbody>");

            if (returnList.isEmpty()) {
                out.println("<tr><td colspan='9' class='text-center text-muted py-4'>Chưa có phiếu trả hàng nào trong hệ thống.</td></tr>");
            } else {
                for (SupplierReturns item : returnList) {
                    String statusBadge = "COMPLETED".equalsIgnoreCase(item.getStatus()) 
                        ? "<span class='badge bg-success'>Hoàn thành</span>" 
                        : "<span class='badge bg-warning text-dark'>" + (item.getStatus() != null ? item.getStatus() : "PENDING") + "</span>";
                    
                    out.println("<tr>");
                    out.println("<td>#" + item.getSupplierReturnId() + "</td>");
                    out.println("<td><code>" + item.getReturnCode() + "</code></td>");
                    out.println("<td class='fw-bold'>NCC #" + item.getSupplierId() + "</td>");
                    out.println("<td>PO #" + item.getPoId() + "</td>");
                    out.println("<td>Part #" + item.getPartId() + "</td>");
                    out.println("<td class='fw-bold text-danger'>-" + item.getQuantity() + "</td>");
                    out.println("<td><span class='badge bg-secondary'>" + (item.getDefectType() != null ? item.getDefectType() : "Lỗi kỹ thuật") + "</span></td>");
                    out.println("<td>" + statusBadge + "</td>");
                    out.println("<td class='text-center'><small>" + (item.getCreatedAt() != null ? item.getCreatedAt() : "-") + "</small></td>");
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