package dungntth08581.controller;

import dungntth08581.service.WarrantyService;
import fourSome.model.EWarranties;
import fourSome.model.Users;
import fourSome.url.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "AdminWarrantyServlet", urlPatterns = {"/admin/warranty"})
public class AdminWarrantyServlet extends HttpServlet {

    private final WarrantyService warrantyService = new WarrantyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String status = req.getParameter("status");
        List<HashMap<String, Object>> claims = warrantyService.getAdminClaims(status);
        for (HashMap<String, Object> c : claims) {
            int claimId = (Integer) c.get("claimId");
            c.put("logs", warrantyService.getClaimLogs(claimId));
        }

        req.setAttribute("status", status == null ? "" : status);
        req.setAttribute("claims", claims);
        req.setAttribute("categories", loadCategories());
        req.getRequestDispatcher("/views/dungntth08581/admin/warranties.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        Users staff = (session != null) ? (Users) session.getAttribute("loggedUser") : null;
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        String action = req.getParameter("action");
        String statusFilter = req.getParameter("statusFilter");
        String err = null;
        String msg = null;

        try {
            if ("setCategoryPolicy".equals(action)) {
                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
                int months = Integer.parseInt(req.getParameter("warrantyMonths"));
                err = warrantyService.setWarrantyMonthsForCategory(staff.getUserId(), categoryId, months);
                if (err == null) msg = "Đã cập nhật thời hạn bảo hành cho danh mục thành " + months + " tháng.";

            } else if ("issueOrder".equals(action)) {
                int orderId = Integer.parseInt(req.getParameter("orderId"));
                List<EWarranties> issued = warrantyService.issueWarrantiesForOrder(orderId, staff.getUserId());
                msg = "Đã phát hành / kích hoạt " + issued.size() + " phiếu bảo hành điện tử cho đơn #" + orderId + ".";

            } else if ("inspect".equals(action)) {
                int claimId = Integer.parseInt(req.getParameter("claimId"));
                String receivedCondition = req.getParameter("receivedCondition");
                boolean eligible = "true".equals(req.getParameter("eligible"));
                String rejectionReason = req.getParameter("rejectionReason");
                err = warrantyService.receiveAndInspectClaim(claimId, staff.getUserId(), receivedCondition, eligible, rejectionReason);
                if (err == null) msg = eligible ? "Đã duyệt yêu cầu bảo hành #" + claimId : "Đã từ chối yêu cầu bảo hành #" + claimId;

            } else if ("sendCenter".equals(action)) {
                int claimId = Integer.parseInt(req.getParameter("claimId"));
                String centerName = req.getParameter("serviceCenterName");
                String note = req.getParameter("note");
                err = warrantyService.sendToServiceCenter(claimId, staff.getUserId(), centerName, note);
                if (err == null) msg = "Đã chuyển yêu cầu #" + claimId + " đến trung tâm bảo hành.";

            } else if ("progress".equals(action)) {
                int claimId = Integer.parseInt(req.getParameter("claimId"));
                String newStatus = req.getParameter("newStatus");
                String note = req.getParameter("note");
                err = warrantyService.updateProgress(claimId, staff.getUserId(), newStatus, note);
                if (err == null) msg = "Đã cập nhật tiến độ yêu cầu #" + claimId + ".";

            } else if ("resolve".equals(action)) {
                int claimId = Integer.parseInt(req.getParameter("claimId"));
                boolean isReplacement = "REPLACED".equals(req.getParameter("resolutionType"));
                String replacementSerial = req.getParameter("replacementSerial");
                String resolutionNote = req.getParameter("resolutionNote");
                err = warrantyService.recordResolution(claimId, staff.getUserId(), isReplacement, replacementSerial, resolutionNote);
                if (err == null) msg = "Đã ghi nhận kết quả bảo hành cho yêu cầu #" + claimId + ".";

            } else if ("complete".equals(action)) {
                int claimId = Integer.parseInt(req.getParameter("claimId"));
                String finalNote = req.getParameter("finalNote");
                err = warrantyService.completeClaim(claimId, staff.getUserId(), finalNote);
                if (err == null) msg = "Đã hoàn tất & bàn giao bảo hành #" + claimId + ".";
            }
        } catch (Exception e) {
            err = "Lỗi thao tác: " + e.getMessage();
        }

        if (err != null) session.setAttribute("error", err);
        if (msg != null) session.setAttribute("message", msg);

        String redirectUrl = req.getContextPath() + "/admin/warranty";
        if (statusFilter != null && !statusFilter.trim().isEmpty()) {
            redirectUrl += "?status=" + statusFilter.trim();
        }
        resp.sendRedirect(redirectUrl);
    }

    private List<HashMap<String, Object>> loadCategories() {
        List<HashMap<String, Object>> list = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM categories WHERE status = 'ACTIVE' ORDER BY category_name";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                row.put("categoryId", rs.getInt("category_id"));
                row.put("categoryName", rs.getString("category_name"));
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}