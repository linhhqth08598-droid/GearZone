package dungntth08581.controller;

import dungntth08581.service.PromotionService;
import fourSome.model.Users;
import khanhnb08894.dao.BrandsDAO;
import khanhnb08894.dao.CategoriesDAO;
import linhhqth08598.service.AuditLogService;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Quản lý mã giảm giá / khuyến mãi / combo + gửi thông báo khuyến mãi (UC 236-250), admin: /admin/promotion?tab=coupons|promotions|combos|usage|broadcast.
 * Quyền do AdminAuthFilter (khu PROMOTION). Mọi thao tác ghi staff_audit_logs.
 */
@WebServlet(name = "AdminPromotionServlet", urlPatterns = {"/admin/promotion"})
public class AdminPromotionServlet extends HttpServlet {

    private final PromotionService service = new PromotionService();
    private final AuditLogService audit = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tab = request.getParameter("tab");
        if (tab == null) tab = "coupons";
        switch (tab) {
            case "promotions": request.setAttribute("promotions", service.listPromotions(false)); break;
            case "combos": request.setAttribute("combos", service.listCombos()); break;
            case "usage":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    request.setAttribute("coupon", service.findCoupon(id));
                    request.setAttribute("usage", service.couponUsage(id));
                } catch (NumberFormatException e) { tab = "coupons"; }
                if (!"usage".equals(tab)) request.setAttribute("coupons", service.listCoupons());
                break;
            case "broadcast": break;
            default: tab = "coupons"; request.setAttribute("coupons", service.listCoupons());
        }
        if ("coupons".equals(tab) || "promotions".equals(tab)) {
            request.setAttribute("categories", new CategoriesDAO().getAll());
            request.setAttribute("brands", new BrandsDAO().getAll());
        }
        request.setAttribute("tab", tab);
        request.getRequestDispatcher("/views/dungntth08581/admin/promotions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users admin = (Users) session.getAttribute("loggedUser");
        int adminId = admin == null ? 0 : admin.getUserId();
        String action = request.getParameter("action") == null ? "" : request.getParameter("action");
        String tab = "coupons";
        String err = null, ok = null;
        try {
            switch (action) {
                case "saveCoupon": {
                    int id = request.getParameter("id") == null || request.getParameter("id").isEmpty() ? 0 : Integer.parseInt(request.getParameter("id"));
                    err = service.saveCoupon(id, request.getParameter("code"), request.getParameter("name"), request.getParameter("type"), request.getParameter("value"),
                            request.getParameter("minOrder"), request.getParameter("startDate"), request.getParameter("endDate"), request.getParameter("usageLimit"),
                            request.getParameter("perUserLimit"), request.getParameter("maxDiscount"), request.getParameter("categoryId"), request.getParameter("brandId"));
                    if (err == null) { ok = id == 0 ? "Đã tạo mã giảm giá." : "Đã cập nhật mã giảm giá."; log(adminId, "COUPON_SAVE", id, ok); }
                    break;
                }
                case "toggleCoupon": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (service.toggleCoupon(id)) { ok = "Đã đổi trạng thái mã giảm giá."; log(adminId, "COUPON_TOGGLE", id, ok); } else err = "Không đổi được trạng thái!";
                    break;
                }
                case "savePromotion":
                    tab = "promotions";
                    err = service.savePromotion(request.getParameter("name"), request.getParameter("targetKind"), request.getParameter("targetId"), request.getParameter("type"),
                            request.getParameter("value"), request.getParameter("minOrder"), request.getParameter("startDate"), request.getParameter("endDate"), adminId);
                    if (err == null) { ok = "Đã tạo khuyến mãi."; log(adminId, "PROMOTION_CREATE", null, ok); }
                    break;
                case "togglePromotion": {
                    tab = "promotions";
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (service.togglePromotion(id)) { ok = "Đã đổi trạng thái khuyến mãi."; log(adminId, "PROMOTION_TOGGLE", id, ok); } else err = "Không đổi được trạng thái!";
                    break;
                }
                case "saveCombo":
                    tab = "combos";
                    err = service.saveCombo(request.getParameter("name"), request.getParameter("type"), request.getParameter("percent"), request.getParameter("minParts"),
                            request.getParameter("startDate"), request.getParameter("endDate"));
                    if (err == null) { ok = "Đã tạo combo."; log(adminId, "COMBO_CREATE", null, ok); }
                    break;
                case "toggleCombo": {
                    tab = "combos";
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (service.toggleCombo(id)) { ok = "Đã đổi trạng thái combo."; log(adminId, "COMBO_TOGGLE", id, ok); } else err = "Không đổi được trạng thái!";
                    break;
                }
                case "broadcast": {
                    tab = "broadcast";
                    int n = service.broadcast(request.getParameter("title"), request.getParameter("content"));
                    if (n < 0) err = "Vui lòng nhập tiêu đề và nội dung!"; else { ok = "Đã gửi thông báo khuyến mãi đến " + n + " khách hàng."; log(adminId, "PROMOTION_BROADCAST", null, ok); }
                    break;
                }
                default: err = "Thao tác không hợp lệ!";
            }
        } catch (NumberFormatException e) { err = "Dữ liệu không hợp lệ!"; }
        if (ok != null) session.setAttribute("message", ok);
        if (err != null) session.setAttribute("error", err);
        response.sendRedirect(request.getContextPath() + "/admin/promotion?tab=" + tab);
    }

    private void log(int adminId, String action, Integer targetId, String desc) {
        if (adminId > 0) audit.logAction(adminId, action, "promotions", targetId, desc);
    }
}
