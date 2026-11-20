package dungntth08581.controller;

import dungntth08581.service.WarrantyService;
import fourSome.model.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "WarrantyServlet", urlPatterns = {"/warranty"})
public class WarrantyServlet extends HttpServlet {

    private final WarrantyService warrantyService = new WarrantyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Users user = (session != null) ? (Users) session.getAttribute("loggedUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        List<HashMap<String, Object>> warranties = warrantyService.getUserWarranties(user.getUserId());
        List<HashMap<String, Object>> claims = warrantyService.getUserClaimHistory(user.getUserId());

        // Gắn log tiến độ cho từng yêu cầu để khách xem chi tiết lịch sử
        for (HashMap<String, Object> c : claims) {
            int claimId = (Integer) c.get("claimId");
            c.put("logs", warrantyService.getClaimLogs(claimId));
        }

        req.setAttribute("warranties", warranties);
        req.setAttribute("claims", claims);
        req.getRequestDispatcher("/views/dungntth08581/user/warranty_claims.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        Users user = (session != null) ? (Users) session.getAttribute("loggedUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        try {
            int warrantyId = Integer.parseInt(req.getParameter("warrantyId"));
            String faultDescription = req.getParameter("faultDescription");
            String err = warrantyService.createClaim(user.getUserId(), warrantyId, faultDescription);
            if (err != null) {
                session.setAttribute("error", err);
            } else {
                session.setAttribute("message", "Gửi yêu cầu bảo hành thành công! Cửa hàng sẽ sớm kiểm tra và phản hồi.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Vui lòng chọn phiếu bảo hành hợp lệ.");
        }
        resp.sendRedirect(req.getContextPath() + "/warranty");
    }
}