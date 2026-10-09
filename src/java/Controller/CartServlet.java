package Controller;

import DAO.CartDAO;
import DAO.PromotionDAO;
import Models.CartItem;
import Models.Promotion;
import Models.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/add-to-cart", "/remove-cart", "/update-cart", "/apply-promo"})
public class CartServlet extends HttpServlet {
    
    private PromotionDAO promoDAO = new PromotionDAO();
    private CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        HttpSession session = request.getSession();
        
        // Lấy thông tin user đang đăng nhập
        User user = (User) session.getAttribute("userAcc");

        // Bắt buộc đăng nhập mới được dùng giỏ hàng
        if (user == null) {
            session.setAttribute("errorMsg", "Vui lòng đăng nhập để sử dụng giỏ hàng!");
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        try {
            if ("/cart".equals(path)) {
                List<CartItem> cartItems = cartDAO.getCartItems(user.getUserId());
                double totalAmount = 0;
                for (CartItem item : cartItems) {
                    totalAmount += item.getTotalPrice();
                }
                
                // --- XỬ LÝ HIỂN THỊ KHUYẾN MÃI ĐÃ LƯU TRONG SESSION ---
                Promotion appliedPromo = (Promotion) session.getAttribute("appliedPromo");
                double discountValue = 0;
                
                if (appliedPromo != null) {
                    // Nếu tổng tiền không đạt mức tối thiểu, hủy bỏ mã giảm giá
                    if (totalAmount < appliedPromo.getMinOrderValue()) {
                        session.removeAttribute("appliedPromo");
                        session.setAttribute("errorMsg", "Đơn hàng chưa đạt mức tối thiểu (" + appliedPromo.getMinOrderValue() + "đ) để áp dụng mã này.");
                    } else {
                        // Tính toán số tiền được giảm
                        if (appliedPromo.getDiscountPercent() > 0) {
                            discountValue = totalAmount * (appliedPromo.getDiscountPercent() / 100);
                        } else if (appliedPromo.getDiscountAmount() > 0) {
                            discountValue = appliedPromo.getDiscountAmount();
                        }
                        
                        // Đảm bảo số tiền giảm không vượt quá tổng tiền
                        if (discountValue > totalAmount) {
                            discountValue = totalAmount;
                        }
                    }
                }
                
                double finalAmount = totalAmount - discountValue;
                
                request.setAttribute("cartItems", cartItems);
                request.setAttribute("totalAmount", totalAmount);
                request.setAttribute("discountValue", discountValue);
                request.setAttribute("finalAmount", finalAmount);
                request.setAttribute("activeMenu", "cart");
                
                request.getRequestDispatcher("/views/cart.jsp").forward(request, response);
                
            } else if ("/add-to-cart".equals(path)) {
                // HÀNH ĐỘNG 2: THÊM SẢN PHẨM VÀO GIỎ
                int productId = Integer.parseInt(request.getParameter("id"));
                int quantity = 1; // Mặc định mỗi lần bấm 'Mua' ngoài trang chủ là thêm 1 sản phẩm
                
                // Lấy số lượng nếu người dùng tự nhập (khi ở trang chi tiết sản phẩm)
                String qtyStr = request.getParameter("quantity");
                if (qtyStr != null && !qtyStr.isEmpty()) {
                    quantity = Integer.parseInt(qtyStr);
                }

                cartDAO.addToCart(user.getUserId(), productId, quantity);
                session.setAttribute("successMsg", "Đã thêm sản phẩm vào giỏ hàng!");
                
                // Quay lại trang hiện tại (nơi người dùng vừa bấm nút)
                String referer = request.getHeader("Referer");
                if (referer != null) {
                    response.sendRedirect(referer);
                } else {
                    response.sendRedirect(request.getContextPath() + "/home");
                }
                
            } else if ("/remove-cart".equals(path)) {
                // HÀNH ĐỘNG 3: XÓA KHỎI GIỎ HÀNG
                int cartItemId = Integer.parseInt(request.getParameter("id"));
                cartDAO.removeCartItem(cartItemId);
                
                session.setAttribute("successMsg", "Đã xóa sản phẩm khỏi giỏ!");
                response.sendRedirect(request.getContextPath() + "/cart");
            } else if ("/update-cart".equals(path)) {
                // HÀNH ĐỘNG 4: CẬP NHẬT SỐ LƯỢNG
                int cartItemId = Integer.parseInt(request.getParameter("id"));
                int newQuantity = Integer.parseInt(request.getParameter("quantity"));
                
                cartDAO.updateCartItemQuantity(cartItemId, newQuantity);
                response.sendRedirect(request.getContextPath() + "/cart");
            } else if ("/apply-promo".equals(path)) {
                // HÀNH ĐỘNG 5: ÁP DỤNG MÃ KHUYẾN MÃI
                String promoCode = request.getParameter("promoCode");
                
                if (promoCode == null || promoCode.trim().isEmpty()) {
                    session.removeAttribute("appliedPromo"); // Nếu gửi trống, coi như gỡ mã
                } else {
                    Promotion promo = promoDAO.getValidPromotion(promoCode.trim().toUpperCase());
                    if (promo != null) {
                        session.setAttribute("appliedPromo", promo);
                        session.setAttribute("successMsg", "Áp dụng mã giảm giá thành công!");
                    } else {
                        session.setAttribute("errorMsg", "Mã giảm giá không hợp lệ, đã hết hạn hoặc hết lượt dùng.");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/cart");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Lỗi Giỏ Hàng: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Mọi form POST gửi về (ví dụ cập nhật số lượng) tạm thời đẩy sang doGet xử lý
        doGet(request, response);
    }
}