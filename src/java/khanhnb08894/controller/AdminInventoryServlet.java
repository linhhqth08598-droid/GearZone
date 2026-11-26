package khanhnb08894.controller;

import fourSome.model.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import khanhnb08894.dao.PartsDAO;
import khanhnb08894.service.InventoryService;

/**
 * Quản lý tồn kho, điều chỉnh kiểm kê/lỗi/thất thoát và thống kê danh mục (Module D).
 */
@WebServlet(name = "AdminInventoryServlet", urlPatterns = {"/admin/inventory"})
public class AdminInventoryServlet extends HttpServlet {

    private final InventoryService inventoryService = new InventoryService();
    private final PartsDAO partsDAO = new PartsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        int minIdleDays = parseIdleDays(request.getParameter("idleDays"));

        request.setAttribute("inventoryList", inventoryService.getAllInventory());
        request.setAttribute("partsList", partsDAO.getAllWithImages());
        request.setAttribute("categorySummary", inventoryService.getCategoryStockSummary());
        request.setAttribute("lowStockItems", inventoryService.getLowOrOutOfStockItems());
        request.setAttribute("agingStockItems", inventoryService.getAgingStockItems(minIdleDays));
        request.setAttribute("paramIdleDays", minIdleDays);

        request.getRequestDispatcher("/views/khanhnb08894/admin/inventory.jsp").forward(request, response);
    }

    private int parseIdleDays(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return 30;
        }
        try {
            return Math.max(1, Integer.parseInt(raw.trim()));
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        try {
            if ("adjustStock".equals(action)) {
                int partId = Integer.parseInt(request.getParameter("partId"));
                String txType = request.getParameter("txType");
                int deltaQty = Integer.parseInt(request.getParameter("deltaQty"));
                String reason = request.getParameter("reason");
                Users loggedUser = (Users) session.getAttribute("loggedUser");
                Integer staffId = loggedUser != null ? loggedUser.getUserId() : null;

                if (inventoryService.adjustStock(partId, txType, deltaQty, reason, staffId)) {
                    session.setAttribute("message", "Đã cập nhật điều chỉnh tồn kho và ghi lịch sử giao dịch.");
                } else {
                    session.setAttribute("error", "Không thể điều chỉnh tồn kho.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Lỗi điều chỉnh tồn kho: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/inventory");
    }
}
