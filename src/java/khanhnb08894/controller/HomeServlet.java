package khanhnb08894.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        java.util.List<fourSome.model.Parts> featured = new java.util.ArrayList<>();
        for (fourSome.model.Parts p : new khanhnb08894.dao.PartsDAO().getAllWithImages()) {
            if ("ACTIVE".equals(p.getStatus())) featured.add(p);
        }
        featured.sort((a, b) -> Integer.compare(b.getSoldCount() == null ? 0 : b.getSoldCount(), a.getSoldCount() == null ? 0 : a.getSoldCount()));
        // 10 sản phẩm = 2 hàng x 5 thẻ
        request.setAttribute("featured", featured.size() > 10 ? featured.subList(0, 10) : featured);
        request.setAttribute("specSummaries", new khanhnb08894.service.SpecService().getSummaries());
        // Khuyến mãi đang chạy + mã giảm giá còn dùng được (dữ liệu của Dũng)
        dungntth08581.service.PromotionService promoSv = new dungntth08581.service.PromotionService();
        java.util.List<dungntth08581.service.PromotionService.PromoRow> promos = promoSv.listPromotions(true);
        request.setAttribute("homePromos", promos.size() > 6 ? promos.subList(0, 6) : promos);
        java.util.List<dungntth08581.service.PromotionService.CouponRow> coupons = new java.util.ArrayList<>();
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        for (dungntth08581.service.PromotionService.CouponRow c : promoSv.listCoupons()) {
            if (!"ACTIVE".equals(c.getStatus())) continue;
            if (c.getStartDate() != null && c.getStartDate().after(today)) continue;
            if (c.getEndDate() != null && c.getEndDate().before(today)) continue;
            if (c.getUsageLimit() > 0 && c.getUsedCount() >= c.getUsageLimit()) continue;
            coupons.add(c);
        }
        request.setAttribute("homeCoupons", coupons.size() > 6 ? coupons.subList(0, 6) : coupons);
        // Chuyển tiếp tới trang home.jsp
        request.getRequestDispatcher("/views/khanhnb08894/home.jsp").forward(request, response);
    }
}