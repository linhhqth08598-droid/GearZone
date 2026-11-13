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
        
        // Chuyển tiếp tới trang JSP
        request.getRequestDispatcher("/views/linhhqth08598/user/wishlist.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        String partIdStr = request.getParameter("partId");
        
        if (action != null && partIdStr != null) {
            try {
                int partId = Integer.parseInt(partIdStr);
                String error = null;
                
                if ("add".equals(action)) {
                    error = wishlistService.addWishlist(user.getUserId(), partId);
                    if (error == null) session.setAttribute("message", "Đã thêm vào danh sách yêu thích!");
                } else if ("remove".equals(action)) {
                    error = wishlistService.removeWishlist(user.getUserId(), partId);
                    if (error == null) session.setAttribute("message", "Đã xóa khỏi danh sách yêu thích!");
                }
                
                if (error != null) {
                    session.setAttribute("error", error);
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Mã sản phẩm không hợp lệ!");
            }
        }
        
        // Load lại trang Wishlist
        response.sendRedirect(request.getContextPath() + "/user/wishlist");
    }
}