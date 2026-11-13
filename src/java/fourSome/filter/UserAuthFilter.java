package fourSome.filter;

import fourSome.model.Users;
import fourSome.util.Constants;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// CHỈ LIỆT KÊ CÁC ĐƯỜNG DẪN BẮT BUỘC KHÁCH HÀNG PHẢI ĐĂNG NHẬP
@WebFilter(urlPatterns = {
    "/user/*",       // Hồ sơ, địa chỉ cá nhân
    "/cart/*",       // Giỏ hàng
    "/checkout/*",   // Thanh toán
    "/wishlist/*",   // Danh sách yêu thích
    "/user/wishlist",
    "/user/price-alert",
    "/order/*",      // Lịch sử đơn hàng
    "/return/*",     // Đổi trả
    "/review/*",     // Đánh giá sản phẩm
    "/support/*",    // Gửi yêu cầu hỗ trợ (ticket)
    "/assembly/order" // Đặt lịch lắp ráp
})
public class UserAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        
        Users loggedUser = (Users) session.getAttribute("loggedUser"); // Hoặc Constants.SESSION_USER
        
        // Nếu chưa đăng nhập mà cố tình vào các trang trên
        if (loggedUser == null) {
            // Kiểm tra xem đây có phải là request AJAX không (ví dụ: bấm nút "Thêm vào giỏ" không load trang)
            String requestedWith = req.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWith)) {
                // Trả về mã lỗi 401 Unauthorized để Javascript frontend tự xử lý (hiển thị popup yêu cầu đăng nhập)
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write("Vui lòng đăng nhập để thực hiện chức năng này.");
                return;
            } else {
                // Nếu là chuyển trang bình thường, lưu lại trang người dùng đang muốn vào
                // Để sau khi login xong, hệ thống tự trả về đúng trang đó (UX tốt hơn)
                String currentUrl = req.getRequestURI();
                if (req.getQueryString() != null) {
                    currentUrl += "?" + req.getQueryString();
                }
                session.setAttribute("redirectAfterLogin", currentUrl);
                
                // Đá về trang đăng nhập
                res.sendRedirect(req.getContextPath() + "/auth?action=login");
                return;
            }
        }
        
        // Đã đăng nhập -> Cho đi tiếp
        chain.doFilter(request, response);
    }
}