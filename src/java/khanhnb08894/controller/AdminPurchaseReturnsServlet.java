package khanhnb08894.controller;

import fourSome.model.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import khanhnb08894.service.SupplierService;

/**
 * Quản lý phiếu trả hàng cho nhà cung cấp (Module E - UC 323).
 */
@WebServlet(name = "AdminPurchaseReturnsServlet", urlPatterns = {"/admin/purchase-returns", "/admin/supplier-returns", "/admin/supplier-return"})
public class AdminPurchaseReturnsServlet extends HttpServlet {

    private final SupplierService supplierService = new SupplierService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        request.setAttribute("returnList", supplierService.getAllSupplierReturns());
        request.getRequestDispatcher("/views/khanhnb08894/admin/supplier_returns.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        try {
            if ("createReturn".equals(action)) {
                int poId = Integer.parseInt(request.getParameter("poId"));
                int partId = Integer.parseInt(request.getParameter("partId"));
                int qty = Integer.parseInt(request.getParameter("returnQty"));
                String defect = request.getParameter("defectType");
                String reason = request.getParameter("reason");

                Users loggedUser = (Users) session.getAttribute("loggedUser");
                Integer userId = loggedUser != null ? loggedUser.getUserId() : null;

                String code = supplierService.createSupplierReturn(poId, partId, qty, defect, reason, userId);
                session.setAttribute("message", "Lập phiếu trả hàng thành công! Mã phiếu: " + code);
                response.sendRedirect(request.getContextPath() + "/admin/purchase-detail?poId=" + poId);
                return;
            } else if ("updateStatus".equals(action)) {
                int returnId = Integer.parseInt(request.getParameter("returnId"));
                String nextStatus = request.getParameter("status");
                if (supplierService.updateSupplierReturnStatus(returnId, nextStatus)) {
                    session.setAttribute("message", "Đã cập nhật trạng thái phiếu trả hàng.");
                } else {
                    session.setAttribute("error", "Không cập nhật được trạng thái phiếu trả hàng.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Lỗi xử lý phiếu trả hàng: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/supplier-returns");
    }
}
