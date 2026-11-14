package linhhqth08598.controller;

import fourSome.model.Users;
import fourSome.model.UserWishlists;
import linhhqth08598.service.WishlistService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "WishlistServlet", urlPatterns = {"/user/wishlist"})
public class WishlistServlet extends HttpServlet {

    private WishlistService wishlistService = new WishlistService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        List<UserWishlists> list = wishlistService.getWishlist(user.getUserId());
        request.setAttribute("wishlist", list);
        request.getRequestDispatcher("/views/linhhqth08598/user/wishlist.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8"); // Trả về JSON
        
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            response.getWriter().write("{\"success\": false, \"message\": \"Chưa đăng nhập\"}");
            return;
        }

        String action = request.getParameter("action");
        String partIdStr = request.getParameter("partId");
        
        if (action != null && partIdStr != null) {
            try {
                int partId = Integer.parseInt(partIdStr);
                String error = null;
                boolean isWishlisted = false;
                
                if ("add".equals(action)) {
                    error = wishlistService.addWishlist(user.getUserId(), partId);
                    isWishlisted = true;
                } else if ("remove".equals(action)) {
                    error = wishlistService.removeWishlist(user.getUserId(), partId);
                    isWishlisted = false;
                } else if ("toggle".equals(action)) {
                    // Xử lý toggle (dành cho AJAX)
                    boolean exists = wishlistService.checkExists(user.getUserId(), partId);
                    if (exists) {
                        wishlistService.removeWishlist(user.getUserId(), partId);
                        isWishlisted = false;
                    } else {
                        wishlistService.addWishlist(user.getUserId(), partId);
                        isWishlisted = true;
                    }
                }
                
                if (error != null) {
                    response.getWriter().write("{\"success\": false, \"message\": \"" + error + "\"}");
                } else {
                    response.getWriter().write("{\"success\": true, \"isWishlisted\": " + isWishlisted + "}");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("{\"success\": false, \"message\": \"Mã sản phẩm không hợp lệ\"}");
            }
        } else {
            response.getWriter().write("{\"success\": false, \"message\": \"Thiếu tham số\"}");
        }
    }
}