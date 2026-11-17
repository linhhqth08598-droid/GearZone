package khanhnb08894.controller;

import khanhnb08894.dao.PurchaseOrderDetailsDAO;
import khanhnb08894.PurchaseOrderDetailDTO;
import fourSome.model.Parts;
import fourSome.model.PurchaseOrderDetails;
import fourSome.url.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import khanhnb08894.dao.PartsDAO;

@WebServlet(name = "AdminPurchaseDetailServlet", urlPatterns = {"/admin/purchase-detail", "/admin/purchase-details"})
public class AdminPurchaseDetailServlet extends HttpServlet {

    private PurchaseOrderDetailsDAO detailDAO;
    private PartsDAO partsDAO;

    @Override
    public void init() throws ServletException {
        detailDAO = new PurchaseOrderDetailsDAO();
        partsDAO = new PartsDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        String poIdParam = request.getParameter("poId");
        if (poIdParam == null || poIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/purchases");
            return;
        }

        int poId = Integer.parseInt(poIdParam);
        List<PurchaseOrderDetailDTO> details = detailDAO.getDetailsByPoId(poId);
        List<Parts> partsList = partsDAO.getAll(); 
        
        HttpSession session = request.getSession();
        String message = (String) session.getAttribute("message");
        String error = (String) session.getAttribute("error");
        session.removeAttribute("message");
        session.removeAttribute("error");

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='vi'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Chi tiết Đơn Nhập Hàng #" + poId + "</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css' rel='stylesheet'>");
            out.println("</head>");
            out.println("<body class='bg-light'>");
            out.println("<div class='container py-5'>");
            
            // Header & Nút quay lại
            out.println("<div class='d-flex justify-content-between align-items-center mb-4'>");
            out.println("<h2><i class='fas fa-boxes me-2 text-primary'></i>Chi tiết Đơn Nhập Hàng (PO ID: #" + poId + ")</h2>");
            out.println("<div>");
            out.println("<a href='" + request.getContextPath() + "/admin/supplier-returns' class='btn btn-outline-danger me-2'><i class='fas fa-undo me-1'></i> Xem DS Trả Hàng</a>");
            out.println("<a href='" + request.getContextPath() + "/admin/purchases' class='btn btn-outline-secondary'><i class='fas fa-arrow-left me-1'></i> Quay lại PO</a>");
            out.println("</div>");
            out.println("</div>");

            // Thông báo
            if (message != null) {
                out.println("<div class='alert alert-success alert-dismissible fade show'><i class='fas fa-check-circle me-1'></i> " + message + "<button type='button' class='btn-close' data-bs-dismiss='alert'></button></div>");
            }
            if (error != null) {
                out.println("<div class='alert alert-danger alert-dismissible fade show'><i class='fas fa-exclamation-circle me-1'></i> " + error + "<button type='button' class='btn-close' data-bs-dismiss='alert'></button></div>");
            }

            // Form thêm linh kiện vào đơn
            out.println("<div class='card shadow-sm border-0 rounded-4 mb-4'>");
            out.println("<div class='card-body p-4'>");
            out.println("<h5 class='card-title fw-bold text-primary mb-3'><i class='fas fa-plus-circle me-2'></i>Thêm linh kiện vào đơn nhập</h5>");
            out.println("<form action='" + request.getContextPath() + "/admin/purchase-detail' method='POST' class='row g-3'>");
            out.println("<input type='hidden' name='action' value='addDetail'>");
            out.println("<input type='hidden' name='poId' value='" + poId + "'>");
            
            out.println("<div class='col-md-5'>");
            out.println("<select name='partId' class='form-select' required>");
            out.println("<option value=''>-- Chọn linh kiện --</option>");
            for (Parts p : partsList) {
                out.println("<option value='" + p.getPartId() + "'>" + p.getProductCode() + " - " + p.getPartName() + "</option>");
            }
            out.println("</select>");
            out.println("</div>");
            
            out.println("<div class='col-md-3'>");
            out.println("<input type='number' name='importQuantity' class='form-control' required min='1' value='10' placeholder='Số lượng'>");
            out.println("</div>");
            
            out.println("<div class='col-md-3'>");
            out.println("<input type='number' step='0.01' name='importUnitPrice' class='form-control' required min='0' placeholder='Đơn giá (VNĐ)'>");
            out.println("</div>");
            
            out.println("<div class='col-md-1'>");
            out.println("<button type='submit' class='btn btn-primary w-100'><i class='fas fa-plus'></i></button>");
            out.println("</div>");
            out.println("</form>");
            out.println("</div></div>");

            // Bảng danh sách chi tiết đơn nhập & Nút tạo phiếu trả hàng nhanh
            out.println("<div class='card shadow-sm border-0 rounded-4'>");
            out.println("<div class='card-body p-4'>");
            out.println("<h5 class='card-title fw-bold mb-3'>Danh sách linh kiện trong đơn</h5>");
            out.println("<div class='table-responsive'>");
            out.println("<table class='table table-hover align-middle'>");
            out.println("<thead class='table-light'><tr><th>ID</th><th>Mã SKU</th><th>Tên Linh Kiện</th><th>SL Nhập</th><th>Đơn Giá</th><th>Thành Tiền</th><th class='text-center'>Thao tác</th></tr></thead>");
            out.println("<tbody>");
            
            if (details.isEmpty()) {
                out.println("<tr><td colspan='7' class='text-center text-muted py-4'>Chưa có linh kiện nào trong đơn nhập này.</td></tr>");
            } else {
                for (PurchaseOrderDetailDTO item : details) {
                    double thanhTien = item.getImportQuantity() * item.getImportUnitPrice();
                    out.println("<tr>");
                    out.println("<td>#" + item.getPoDetailId() + "</td>");
                    out.println("<td><code>" + item.getProductCode() + "</code></td>");
                    out.println("<td class='fw-bold'>" + item.getPartName() + "</td>");
                    out.println("<td>" + item.getImportQuantity() + "</td>");
                    out.println("<td class='text-danger'>" + String.format("%,.0f đ", item.getImportUnitPrice()) + "</td>");
                    out.println("<td class='text-danger fw-semibold'>" + String.format("%,.0f đ", thanhTien) + "</td>");
                    out.println("<td class='text-center'>");
                    
                    // Nút mở modal / form trả hàng cho từng linh kiện lỗi
                    out.println("<button type='button' class='btn btn-sm btn-outline-danger me-1' data-bs-toggle='modal' data-bs-target='#returnModal" + item.getPoDetailId() + "'><i class='fas fa-undo'></i> Trả hàng</button>");
                    
                    // Form xóa chi tiết
                    out.println("<form action='" + request.getContextPath() + "/admin/purchase-detail' method='POST' style='display:inline;'>");
                    out.println("<input type='hidden' name='action' value='deleteDetail'>");
                    out.println("<input type='hidden' name='poDetailId' value='" + item.getPoDetailId() + "'>");
                    out.println("<input type='hidden' name='poId' value='" + poId + "'>");
                    out.println("<button type='submit' class='btn btn-sm btn-outline-secondary' onclick=\"return confirm('Xóa linh kiện này?');\"><i class='fas fa-trash'></i></button>");
                    out.println("</form>");
                    
                    out.println("</td>");
                    out.println("</tr>");

                    // Modal tạo phiếu trả hàng cho từng dòng sản phẩm
                    out.println("<div class='modal fade' id='returnModal" + item.getPoDetailId() + "' tabindex='-1'>");
                    out.println("<div class='modal-dialog modal-dialog-centered'><div class='modal-content border-0 shadow-lg rounded-4'>");
                    out.println("<form action='" + request.getContextPath() + "/admin/purchase-detail' method='POST'>");
                    out.println("<input type='hidden' name='action' value='createReturn'>");
                    out.println("<input type='hidden' name='poId' value='" + poId + "'>");
                    out.println("<input type='hidden' name='partId' value='" + item.getPartId() + "'>");
                    out.println("<div class='modal-header bg-white border-bottom border-danger border-3 py-3'>");
                    out.println("<h5 class='modal-title fw-bold text-danger'><i class='fas fa-undo me-2'></i>Lập Phiếu Trả Hàng (SKU: " + item.getProductCode() + ")</h5>");
                    out.println("<button type='button' class='btn-close' data-bs-dismiss='modal'></button></div>");
                    out.println("<div class='modal-body p-4'>");
                    out.println("<div class='mb-3'><label class='form-label fw-semibold'>Số lượng trả *</label><input type='number' name='returnQty' class='form-control' required min='1' max='" + item.getImportQuantity() + "' value='1'></div>");
                    
                    // FIX LỖI Ở ĐÂY: Đổi value thành tiếng Anh chuẩn (DEFECTIVE, DAMAGED, WRONG_ITEM, OTHER)
                    out.println("<div class='mb-3'><label class='form-label fw-semibold'>Loại lỗi (Defect Type)</label><select name='defectType' class='form-select'><option value='DEFECTIVE'>Lỗi kỹ thuật</option><option value='DAMAGED'>Móp méo vỏ</option><option value='WRONG_ITEM'>Giao sai loại</option><option value='OTHER'>Lý do khác</option></select></div>");
                    
                    out.println("<div class='mb-3'><label class='form-label fw-semibold'>Lý do chi tiết</label><textarea name='reason' class='form-control' rows='2' placeholder='Nhập lý do hoàn trả...'></textarea></div>");
                    out.println("</div><div class='modal-footer border-0 bg-light py-3'>");
                    out.println("<button type='button' class='btn btn-outline-secondary' data-bs-dismiss='modal'>Hủy</button>");
                    out.println("<button type='submit' class='btn btn-danger'>Xác nhận Trả Hàng</button>");
                    out.println("</div></form></div></div></div>");
                }
            }
            
            out.println("</tbody></table></div></div></div>");
            out.println("</div>");
            out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String poId = request.getParameter("poId");

        try {
            if ("addDetail".equals(action)) {
                PurchaseOrderDetails detail = new PurchaseOrderDetails();
                detail.setPoId(Integer.parseInt(poId));
                detail.setPartId(Integer.parseInt(request.getParameter("partId")));
                detail.setImportQuantity(Integer.parseInt(request.getParameter("importQuantity")));
                detail.setImportUnitPrice(Double.parseDouble(request.getParameter("importUnitPrice")));

                if (detailDAO.insertDetail(detail)) {
                    session.setAttribute("message", "Thêm linh kiện thành công!");
                } else {
                    session.setAttribute("error", "Lỗi khi thêm linh kiện.");
                }
            } else if ("deleteDetail".equals(action)) {
                int poDetailId = Integer.parseInt(request.getParameter("poDetailId"));
                if (detailDAO.deleteDetail(poDetailId)) {
                    session.setAttribute("message", "Đã xóa linh kiện!");
                } else {
                    session.setAttribute("error", "Lỗi khi xóa linh kiện.");
                }
            } else if ("createReturn".equals(action)) {
                // Logic xử lý tạo phiếu trả hàng đã được fix lỗi thiếu cột created_by
                int pId = Integer.parseInt(request.getParameter("partId"));
                int qty = Integer.parseInt(request.getParameter("returnQty"));
                String defect = request.getParameter("defectType"); // Nhận giá trị tiếng Anh
                String reason = request.getParameter("reason");
                String returnCode = "RET-" + System.currentTimeMillis();

                String sql = "INSERT INTO [supplier_returns] (return_code, supplier_id, po_id, part_id, quantity, defect_type, reason, status, created_by, created_at) " +
                             "VALUES (?, 1, ?, ?, ?, ?, ?, 'PENDING', 1, GETDATE())";

                try (Connection con = DBConnect.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, returnCode);
                    ps.setInt(2, Integer.parseInt(poId));
                    ps.setInt(3, pId);
                    ps.setInt(4, qty);
                    ps.setString(5, defect);
                    ps.setString(6, reason);
                    ps.executeUpdate();
                    session.setAttribute("message", "Lập phiếu trả hàng thành công! Mã phiếu: " + returnCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/purchase-detail?poId=" + poId);
    }
}