package Controller;

import DAO.PromotionDAO;
import Models.Promotion;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "PromotionsServlet", urlPatterns = {"/promotions", "/admin/savePromotion", "/admin/deletePromotion"})
public class PromotionsServlet extends HttpServlet {

    private PromotionDAO promoDAO = new PromotionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/promotions".equals(path)) {
            // Hiển thị danh sách khuyến mãi
            List<Promotion> listPromos = promoDAO.getAllPromotions();
            request.setAttribute("promotionList", listPromos);
            request.setAttribute("activeMenu", "promotions"); // Highlight menu
            
            request.getRequestDispatcher("/views/promotions.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        HttpSession session = request.getSession();

        try {
            if ("/admin/savePromotion".equals(path)) {
                String actionType = request.getParameter("actionType");
                String idStr = request.getParameter("promotionId");
                int id = (idStr != null && !idStr.isEmpty()) ? Integer.parseInt(idStr) : 0;
                
                String code = request.getParameter("promoCode");
                String name = request.getParameter("promotionName");
                
                String percentStr = request.getParameter("discountPercent");
                double percent = (percentStr != null && !percentStr.isEmpty()) ? Double.parseDouble(percentStr) : 0;
                
                String amountStr = request.getParameter("discountAmount");
                double amount = (amountStr != null && !amountStr.isEmpty()) ? Double.parseDouble(amountStr) : 0;
                
                String minOrderStr = request.getParameter("minOrderValue");
                double minOrder = (minOrderStr != null && !minOrderStr.isEmpty()) ? Double.parseDouble(minOrderStr) : 0;
                
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                
                String limitStr = request.getParameter("usageLimit");
                int limit = (limitStr != null && !limitStr.isEmpty()) ? Integer.parseInt(limitStr) : 0;
                
                boolean isActive = request.getParameter("isActive") != null;

                if ("add".equals(actionType)) {
                    promoDAO.insertPromotion(code, name, percent, amount, minOrder, startDate, endDate, limit, isActive);
                    session.setAttribute("successMsg", "Thêm chương trình khuyến mãi thành công!");
                } else if ("edit".equals(actionType)) {
                    promoDAO.updatePromotion(id, code, name, percent, amount, minOrder, startDate, endDate, limit, isActive);
                    session.setAttribute("successMsg", "Cập nhật khuyến mãi thành công!");
                }
                
            } else if ("/admin/deletePromotion".equals(path)) {
                int id = Integer.parseInt(request.getParameter("promotionId"));
                promoDAO.deletePromotion(id);
                session.setAttribute("successMsg", "Đã xóa chương trình khuyến mãi!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/promotions");
    }
}