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
import khanhnb08894.dao.PartSerialsDAO;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@WebServlet(name = "AdminPurchaseDetailServlet", urlPatterns = {"/admin/purchase-detail", "/admin/purchase-details"})
public class AdminPurchaseDetailServlet extends HttpServlet {

    private PurchaseOrderDetailsDAO detailDAO;
    private PartsDAO partsDAO;
    private PartSerialsDAO serialsDAO;

    @Override
    public void init() throws ServletException {
        detailDAO = new PurchaseOrderDetailsDAO();
        partsDAO = new PartsDAO();
        serialsDAO = new PartSerialsDAO();
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
        final boolean locked = isLocked(getPoStatus(poId));
        
        HttpSession session = request.getSession();
        String message = (String) session.getAttribute("message");
        String error = (String) session.getAttribute("error");
        session.removeAttribute("message");
        session.removeAttribute("error");

        try (PrintWriter out = response.getWriter()) {
            request.getRequestDispatcher("/views/components/admin_header.jsp").include(request, response);
            request.getRequestDispatcher("/views/components/admin_sidebar.jsp").include(request, response);
            out.flush();

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
                out.println("<div class='alert alert-success alert-dismissible fade show'><i class='fas fa-check-circle me-1'></i> " + esc(message) + "<button type='button' class='btn-close' data-bs-dismiss='alert'></button></div>");
            }
            if (error != null) {
                out.println("<div class='alert alert-danger alert-dismissible fade show'><i class='fas fa-exclamation-circle me-1'></i> " + esc(error) + "<button type='button' class='btn-close' data-bs-dismiss='alert'></button></div>");
            }

            if (locked) {
                out.println("<div class='alert alert-secondary'><i class='fas fa-lock me-1'></i> Đơn nhập này đã ở trạng thái <b>Đã nhập kho / Đã hủy</b> nên bị khóa: không thể thêm, xóa linh kiện hoặc nhập thêm Serial. Chỉ có thể lập phiếu trả hàng.</div>");
            } else {
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

            }

            // Bảng danh sách chi tiết đơn nhập & Nút tạo phiếu trả hàng nhanh
            out.println("<div class='card shadow-sm border-0 rounded-4'>");
            out.println("<div class='card-body p-4'>");
            out.println("<h5 class='card-title fw-bold mb-3'>Danh sách linh kiện trong đơn</h5>");
            out.println("<div class='table-responsive'>");
            out.println("<table class='table table-hover align-middle'>");
            out.println("<thead class='table-light'><tr><th>ID</th><th>Mã SKU</th><th>Tên Linh Kiện</th><th>SL Nhập</th><th>Đã nhận (S/N)</th><th>Đơn Giá</th><th>Thành Tiền</th><th class='text-center'>Thao tác</th></tr></thead>");
            out.println("<tbody>");
            StringBuilder modals = new StringBuilder();
            
            if (details.isEmpty()) {
                out.println("<tr><td colspan='8' class='text-center text-muted py-4'>Chưa có linh kiện nào trong đơn nhập này.</td></tr>");
            } else {
                for (PurchaseOrderDetailDTO item : details) {
                    double thanhTien = item.getImportQuantity() * item.getImportUnitPrice();
                    out.println("<tr>");
                    out.println("<td>#" + item.getPoDetailId() + "</td>");
                    out.println("<td><code>" + item.getProductCode() + "</code></td>");
                    out.println("<td class='fw-bold'>" + item.getPartName() + "</td>");
                    out.println("<td>" + item.getImportQuantity() + "</td>");
                    out.println("<td>" + item.getReceivedQuantity() + "</td>");
                    out.println("<td class='text-danger'>" + String.format("%,.0f đ", item.getImportUnitPrice()) + "</td>");
                    out.println("<td class='text-danger fw-semibold'>" + String.format("%,.0f đ", thanhTien) + "</td>");
                    out.println("<td class='text-center'>");
                    
                    if (!locked) {
                        out.println("<button type='button' class='btn btn-sm btn-outline-primary me-1' data-bs-toggle='modal' data-bs-target='#serialModal" + item.getPoDetailId() + "'><i class='fas fa-barcode'></i> Nhập Serial</button>");
                    }
                    out.println("<a class='btn btn-sm btn-outline-dark me-1' href='" + request.getContextPath() + "/admin/part-serials?partId=" + item.getPartId() + "'><i class='fas fa-list'></i> Xem Serial</a>");
                    // Nút mở modal / form trả hàng cho từng linh kiện lỗi
                    out.println("<button type='button' class='btn btn-sm btn-outline-danger me-1' data-bs-toggle='modal' data-bs-target='#returnModal" + item.getPoDetailId() + "'><i class='fas fa-undo'></i> Trả hàng</button>");
                    
                    if (!locked) {
                    // Form xóa chi tiết
                    out.println("<form action='" + request.getContextPath() + "/admin/purchase-detail' method='POST' style='display:inline;'>");
                    out.println("<input type='hidden' name='action' value='deleteDetail'>");
                    out.println("<input type='hidden' name='poDetailId' value='" + item.getPoDetailId() + "'>");
                    out.println("<input type='hidden' name='poId' value='" + poId + "'>");
                    out.println("<button type='submit' class='btn btn-sm btn-outline-secondary' onclick=\"return confirm('Xóa linh kiện này?');\"><i class='fas fa-trash'></i></button>");
                    out.println("</form>");
                    
                    }

                    out.println("</td>");
                    out.println("</tr>");

                    // Modal nhập Serial khi nhận hàng
                    if (!locked) {
                    modals.append("<div class='modal fade' id='serialModal" + item.getPoDetailId() + "' tabindex='-1'>").append('\n');
                    modals.append("<div class='modal-dialog modal-dialog-centered'><div class='modal-content border-0 shadow-lg rounded-4'>").append('\n');
                    modals.append("<form action='" + request.getContextPath() + "/admin/purchase-detail' method='POST'>").append('\n');
                    modals.append("<input type='hidden' name='action' value='receiveSerials'>").append('\n');
                    modals.append("<input type='hidden' name='poId' value='" + poId + "'>").append('\n');
                    modals.append("<input type='hidden' name='poDetailId' value='" + item.getPoDetailId() + "'>").append('\n');
                    modals.append("<input type='hidden' name='partId' value='" + item.getPartId() + "'>").append('\n');
                    modals.append("<div class='modal-header'><h5 class='modal-title fw-bold text-primary'><i class='fas fa-barcode me-2'></i>Nhận hàng & nhập Serial (SKU: " + item.getProductCode() + ")</h5>").append('\n');
                    modals.append("<button type='button' class='btn-close' data-bs-dismiss='modal'></button></div>").append('\n');
                    modals.append("<div class='modal-body p-4'>").append('\n');
                    modals.append("<p class='text-muted small mb-2'>Mỗi dòng 1 mã Serial. Số dòng = số lượng thực nhận (tối đa " + item.getImportQuantity() + ").</p>").append('\n');
                    modals.append("<textarea name='serialList' autocomplete='off' class='form-control mb-3' rows='6' placeholder='SN20260001&#10;SN20260002'></textarea>").append('\n');
                    modals.append("<div class='form-check mb-2'><input class='form-check-input' type='checkbox' name='autoGen' value='1' id='auto" + item.getPoDetailId() + "'><label class='form-check-label' for='auto" + item.getPoDetailId() + "'>Tạo nhanh dải Serial theo khoảng số (bỏ qua ô trên)</label></div>").append('\n');
                    modals.append("<div class='row g-2'><div class='col-12'><label class='form-label small mb-1'>Tiền tố</label><input type='text' name='autoPrefix' autocomplete='off' class='form-control' value='SN-2026-' placeholder='Tiền tố'></div>").append('\n');
                    modals.append("<div class='col-6'><label class='form-label small mb-1'>Từ số</label><input type='number' name='autoFrom' autocomplete='off' class='form-control' min='0' placeholder='VD: 3262 (trống = nối tiếp)'></div>").append('\n');
                    modals.append("<div class='col-6'><label class='form-label small mb-1'>Đến số</label><input type='number' name='autoTo' autocomplete='off' class='form-control' min='0' placeholder='VD: 4261'></div></div>").append('\n');
                    modals.append("<div class='form-text'>Số lượng = Đến − Từ + 1, tối đa " + Math.max(item.getImportQuantity() - item.getReceivedQuantity(), 0) + " (còn phải nhận). Bỏ trống \"Đến số\" = tạo đủ số còn phải nhận.</div>").append('\n');
                    modals.append("</div><div class='modal-footer border-0 bg-light'>").append('\n');
                    modals.append("<button type='button' class='btn btn-outline-secondary' data-bs-dismiss='modal'>Hủy</button>").append('\n');
                    modals.append("<button type='submit' class='btn btn-primary'>Lưu Serial</button>").append('\n');
                    modals.append("</div></form></div></div></div>").append('\n');

                    }

                    // Modal tạo phiếu trả hàng cho từng dòng sản phẩm
                    modals.append("<div class='modal fade' id='returnModal" + item.getPoDetailId() + "' tabindex='-1'>").append('\n');
                    modals.append("<div class='modal-dialog modal-dialog-centered'><div class='modal-content border-0 shadow-lg rounded-4'>").append('\n');
                    modals.append("<form action='" + request.getContextPath() + "/admin/purchase-detail' method='POST'>").append('\n');
                    modals.append("<input type='hidden' name='action' value='createReturn'>").append('\n');
                    modals.append("<input type='hidden' name='poId' value='" + poId + "'>").append('\n');
                    modals.append("<input type='hidden' name='partId' value='" + item.getPartId() + "'>").append('\n');
                    modals.append("<div class='modal-header bg-white border-bottom border-danger border-3 py-3'>").append('\n');
                    modals.append("<h5 class='modal-title fw-bold text-danger'><i class='fas fa-undo me-2'></i>Lập Phiếu Trả Hàng (SKU: " + item.getProductCode() + ")</h5>").append('\n');
                    modals.append("<button type='button' class='btn-close' data-bs-dismiss='modal'></button></div>").append('\n');
                    modals.append("<div class='modal-body p-4'>").append('\n');
                    modals.append("<div class='mb-3'><label class='form-label fw-semibold'>Số lượng trả *</label><input type='number' name='returnQty' class='form-control' required min='1' max='" + item.getImportQuantity() + "' value='1'></div>").append('\n');
                    
                    // FIX LỖI Ở ĐÂY: Đổi value thành tiếng Anh chuẩn (DEFECTIVE, DAMAGED, WRONG_ITEM, OTHER)
                    modals.append("<div class='mb-3'><label class='form-label fw-semibold'>Loại lỗi (Defect Type)</label><select name='defectType' class='form-select'><option value='DEFECTIVE'>Lỗi kỹ thuật</option><option value='DAMAGED'>Móp méo vỏ</option><option value='WRONG_ITEM'>Giao sai loại</option><option value='OTHER'>Lý do khác</option></select></div>").append('\n');
                    
                    modals.append("<div class='mb-3'><label class='form-label fw-semibold'>Lý do chi tiết</label><textarea name='reason' class='form-control' rows='2' placeholder='Nhập lý do hoàn trả...'></textarea></div>").append('\n');
                    modals.append("</div><div class='modal-footer border-0 bg-light py-3'>").append('\n');
                    modals.append("<button type='button' class='btn btn-outline-secondary' data-bs-dismiss='modal'>Hủy</button>").append('\n');
                    modals.append("<button type='submit' class='btn btn-danger'>Xác nhận Trả Hàng</button>").append('\n');
                    modals.append("</div></form></div></div></div>").append('\n');
                }
            }
            
            out.println("</tbody></table></div></div></div>");
            out.println(modals.toString());
            out.flush();
            request.getRequestDispatcher("/views/components/footer.jsp").include(request, response);
        }
    }

    /** Trạng thái hiện tại của PO (null nếu không có). */
    private String getPoStatus(int poId) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT status FROM purchase_orders WHERE po_id = ?")) {
            ps.setInt(1, poId);
            try (java.sql.ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getString(1) : null; }
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    /** PO đã nhập kho / đã hủy thì khóa cứng: không thêm, xóa linh kiện hay nhập thêm Serial. */
    private static boolean isLocked(String status) {
        return "RECEIVED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status);
    }

    private static String esc(String t) {
        return t == null ? "" : t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String poId = request.getParameter("poId");

        try {
            if (("addDetail".equals(action) || "deleteDetail".equals(action) || "receiveSerials".equals(action))
                    && poId != null && isLocked(getPoStatus(Integer.parseInt(poId)))) {
                throw new IllegalStateException("Đơn nhập đã ở trạng thái Đã nhập kho / Đã hủy nên bị khóa, không thể thêm, xóa linh kiện hay nhập Serial.");
            }
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
            } else if ("receiveSerials".equals(action)) {
                int poDetailId = Integer.parseInt(request.getParameter("poDetailId"));
                int pId = Integer.parseInt(request.getParameter("partId"));
                List<String> serials = new ArrayList<>();
                boolean autoGen = "1".equals(request.getParameter("autoGen"));
                String autoPrefix = "SN-2026-";
                Long autoFrom = null, autoTo = null;
                if (autoGen) {
                    String pf = request.getParameter("autoPrefix");
                    if (pf != null && !pf.trim().isEmpty()) autoPrefix = pf.trim();
                    String f = request.getParameter("autoFrom"), t = request.getParameter("autoTo");
                    if (f != null && !f.trim().isEmpty()) autoFrom = Long.parseLong(f.trim());
                    if (t != null && !t.trim().isEmpty()) autoTo = Long.parseLong(t.trim());
                    if (autoFrom != null && autoFrom < 0 || autoTo != null && autoTo < 0) throw new IllegalArgumentException("Số bắt đầu/kết thúc phải >= 0.");
                    if (autoFrom != null && autoTo != null && autoTo < autoFrom) throw new IllegalArgumentException("\"Đến số\" phải lớn hơn hoặc bằng \"Từ số\".");
                } else {
                    Set<String> uniq = new LinkedHashSet<>();
                    for (String line : String.valueOf(request.getParameter("serialList")).split("\\r?\\n")) {
                        String sn = line.trim();
                        if (sn.isEmpty() || sn.equals("null")) continue;
                        if (!uniq.add(sn)) throw new IllegalArgumentException("Mã Serial bị lặp trong danh sách: " + sn);
                    }
                    serials.addAll(uniq);
                }
                if (!autoGen && serials.isEmpty()) throw new IllegalArgumentException("Chưa nhập Serial nào.");
                try (Connection con = DBConnect.getConnection()) {
                    con.setAutoCommit(false);
                    try {
                        int importQty = 0;
                        try (PreparedStatement ps = con.prepareStatement("SELECT import_quantity - received_quantity FROM purchase_order_details WHERE po_detail_id = ?")) {
                            ps.setInt(1, poDetailId);
                            try (java.sql.ResultSet rs = ps.executeQuery()) { if (rs.next()) importQty = Math.max(rs.getInt(1), 0); }
                        }
                        if (autoGen) {
                            long start;
                            if (autoFrom != null) start = autoFrom;
                            else {   // không nhập "Từ số": nối tiếp sau số lớn nhất đang có với tiền tố này
                                start = 1;
                                try (PreparedStatement ps = con.prepareStatement(
                                        "SELECT MAX(TRY_CAST(SUBSTRING(serial_number, ?, 20) AS BIGINT)) FROM part_serials WHERE LEFT(serial_number, ?) = ?")) {
                                    ps.setInt(1, autoPrefix.length() + 1);
                                    ps.setInt(2, autoPrefix.length());
                                    ps.setString(3, autoPrefix);
                                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                                        if (rs.next() && rs.getObject(1) != null) start = rs.getLong(1) + 1;
                                    }
                                }
                            }
                            long end = autoTo != null ? autoTo : start + Math.max(importQty, 1) - 1;
                            long count = end - start + 1;
                            if (count < 1) throw new IllegalArgumentException("Khoảng số không hợp lệ.");
                            if (count > importQty) throw new IllegalArgumentException("Khoảng " + start + " → " + end + " có " + count
                                    + " Serial, vượt số lượng còn phải nhận (" + importQty + "). Hãy chỉnh \"Đến số\" thành tối đa " + (start + importQty - 1) + ".");
                            int width = Math.max(4, String.valueOf(end).length());
                            for (long n = start; n <= end; n++) serials.add(autoPrefix + String.format("%0" + width + "d", n));
                        }
                        for (String sn : serials) {
                            if (sn.length() > 100) throw new IllegalArgumentException("Serial quá 100 ký tự: " + sn);
                        }
                        // Kiểm tra TRÙNG trước: báo rõ mã nào đã có trong hệ thống
                        List<String> dups = new ArrayList<>();
                        for (String sn : serials) {
                            if (serialsDAO.exists(con, sn)) dups.add(sn);
                        }
                        if (!dups.isEmpty()) {
                            String shown = String.join(", ", dups.subList(0, Math.min(dups.size(), 5)));
                            throw new IllegalArgumentException("Serial đã tồn tại trong hệ thống, không thể nhập trùng: " + shown
                                    + (dups.size() > 5 ? " ... (và " + (dups.size() - 5) + " mã khác)" : ""));
                        }
                        if (importQty <= 0) throw new IllegalArgumentException("Dòng này đã nhận đủ số lượng, không thể nhập thêm Serial.");
                        if (serials.size() > importQty) throw new IllegalArgumentException("Số Serial (" + serials.size() + ") vượt số lượng còn phải nhận (" + importQty + ").");
                        serialsDAO.insertBatch(con, pId, serials);
                        new khanhnb08894.service.InventoryService().importStock(con, pId, serials.size(), Integer.parseInt(poId));
                        try (PreparedStatement ps = con.prepareStatement(
                                "UPDATE purchase_order_details SET received_quantity = received_quantity + ? WHERE po_detail_id = ?")) {
                            ps.setInt(1, serials.size());
                            ps.setInt(2, poDetailId);
                            ps.executeUpdate();
                        }
                        con.commit();
                        session.setAttribute("message", "Đã nhập " + serials.size() + " Serial vào kho (IN_STOCK).");
                    } catch (Exception ex) {
                        con.rollback();
                        throw ex;
                    }
                }
            } else if ("createReturn".equals(action)) {
                int pId = Integer.parseInt(request.getParameter("partId"));
                int qty = Integer.parseInt(request.getParameter("returnQty"));
                String defect = request.getParameter("defectType");
                String reason = request.getParameter("reason");
                fourSome.model.Users loggedUser = (fourSome.model.Users) session.getAttribute("loggedUser");
                Integer userId = loggedUser != null ? loggedUser.getUserId() : null;

                String returnCode = new khanhnb08894.service.SupplierService()
                        .createSupplierReturn(Integer.parseInt(poId), pId, qty, defect, reason, userId);
                session.setAttribute("message", "Lập phiếu trả hàng thành công! Mã phiếu: " + returnCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/purchase-detail?poId=" + poId);
    }
}