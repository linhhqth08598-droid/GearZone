package khanhnb08894.controller;

import fourSome.model.Parts;
import fourSome.model.Users; // Thêm import này
import fourSome.url.DBConnect; // Thêm import này
import khanhnb08894.dao.PartsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "ProductServlet", urlPatterns = {"/product"})
public class ProductServlet extends HttpServlet {
    private PartsDAO partsDAO = new PartsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("detail".equals(action)) {
            try {
                int partId = Integer.parseInt(request.getParameter("id"));
                Parts part = partsDAO.findById(partId); 
                
                if (part != null) {
                    request.setAttribute("part", part);
                    
                    // 1. Lấy danh sách đánh giá
                    linhhqth08598.service.ReviewService reviewService = new linhhqth08598.service.ReviewService();
                    request.setAttribute("reviews", reviewService.getReviewsByPart(partId));
                    
                    // 2. KIỂM TRA TRẠNG THÁI YÊU THÍCH & THEO DÕI GIÁ
                    Users loggedUser = (Users) request.getSession().getAttribute("loggedUser");
                    boolean isWishlisted = false;
                    boolean isPriceAlerted = false;
                    
                    if (loggedUser != null) {
                        try (Connection conn = DBConnect.getConnection()) {
                            // Check Wishlist
                            String sqlW = "SELECT 1 FROM user_wishlists WHERE user_id = ? AND part_id = ?";
                            try (PreparedStatement ps = conn.prepareStatement(sqlW)) {
                                ps.setInt(1, loggedUser.getUserId());
                                ps.setInt(2, partId);
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (rs.next()) isWishlisted = true;
                                }
                            }
                            // Check Price Alert
                            String sqlP = "SELECT 1 FROM price_drop_notifications WHERE user_id = ? AND part_id = ?";
                            try (PreparedStatement ps = conn.prepareStatement(sqlP)) {
                                ps.setInt(1, loggedUser.getUserId());
                                ps.setInt(2, partId);
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (rs.next()) isPriceAlerted = true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    request.setAttribute("isWishlisted", isWishlisted);
                    request.setAttribute("isPriceAlerted", isPriceAlerted);
                    
                    // 3. Chuyển tiếp sang trang detail.jsp
                    request.getRequestDispatcher("/views/khanhnb08894/product/detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/home");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } else {
            // Hiển thị danh sách sản phẩm
            request.setAttribute("parts", partsDAO.getAll());
            request.getRequestDispatcher("/views/khanhnb08894/product/list.jsp").forward(request, response);
        }
    }
}