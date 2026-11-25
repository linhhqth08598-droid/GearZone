package khanhnb08894.controller;

import fourSome.model.PurchaseOrders;
import fourSome.model.Suppliers;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import khanhnb08894.service.SupplierService;

/**
 * Quản lý đơn nhập hàng (PO) và lịch sử giao dịch nhà cung cấp (Module E).
 */
@WebServlet(name = "AdminPurchaseServlet", urlPatterns = {"/admin/purchase", "/admin/purchases"})
public class AdminPurchaseServlet extends HttpServlet {

    private SupplierService supplierService;

    @Override
    public void init() throws ServletException {
        supplierService = new SupplierService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        Integer filterSupplierId = null;
        String supParam = request.getParameter("supplierId");
        if (supParam != null && supParam.trim().matches("\\d+")) {
            int sid = Integer.parseInt(supParam.trim());
            if (sid > 0) {
                filterSupplierId = sid;
            }
        }

        List<PurchaseOrders> purchases = supplierService.getAllPurchaseOrders();
        List<Suppliers> suppliers = supplierService.getAllSuppliers();

        request.setAttribute("purchases", purchases);
        request.setAttribute("suppliers", suppliers);
        request.setAttribute("selectedSupplierId", filterSupplierId);
        request.setAttribute("supplierHistory", supplierService.getSupplierTransactionHistory(filterSupplierId));

        request.getRequestDispatcher("/views/khanhnb08894/admin/purchases.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        try {
            if ("save".equals(action)) {
                String poCode = request.getParameter("poCode");
                int supplierId = Integer.parseInt(request.getParameter("supplierId"));
                Date parsedEta = null;
                String eta = request.getParameter("expectedDeliveryDate");
                if (eta != null && !eta.trim().isEmpty()) {
                    parsedEta = new SimpleDateFormat("yyyy-MM-dd").parse(eta.trim());
                }

                if (supplierService.createPurchaseOrder(poCode, supplierId, parsedEta)) {
                    session.setAttribute("message", "Tạo Đơn nhập hàng thành công!");
                } else {
                    session.setAttribute("error", "Lỗi khi tạo đơn nhập hàng.");
                }
            } else if ("updateStatus".equals(action)) {
                int poId = Integer.parseInt(request.getParameter("poId"));
                String status = request.getParameter("status");

                if (supplierService.updatePurchaseOrderStatus(poId, status)) {
                    session.setAttribute("message", "Cập nhật trạng thái thành công!");
                } else {
                    session.setAttribute("error", "Lỗi khi cập nhật trạng thái.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", e instanceof IllegalStateException ? e.getMessage() : "Đã xảy ra lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/purchases");
    }
}
