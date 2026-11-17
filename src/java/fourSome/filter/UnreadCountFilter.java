package fourSome.filter;

import fourSome.model.Users;
import dungntth08581.service.CartService;
import linhhqth08598.service.NotificationService;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Cập nhật số thông báo chưa đọc (session "unreadCount") cho chuông và số món trong giỏ (session "cartCount")
 * cho biểu tượng giỏ hàng ở header.
 * Chỉ chạy với request GET tới trang (bỏ qua css/js/ảnh) của người đã đăng nhập.
 */
@WebFilter(urlPatterns = {"/*"})
public class UnreadCountFilter implements Filter {

    private final NotificationService service = new NotificationService();
    private final CartService cartService = new CartService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if ("GET".equalsIgnoreCase(req.getMethod()) && !isStatic(req.getRequestURI())) {
            HttpSession session = req.getSession(false);
            Users user = session == null ? null : (Users) session.getAttribute("loggedUser");
            if (user != null) {
                try {
                    session.setAttribute("unreadCount", service.getUnreadCount(user.getUserId()));
                } catch (Exception e) { /* không để lỗi đếm thông báo làm hỏng trang */ }
                try {
                    session.setAttribute("cartCount", cartService.countLines(user.getUserId()));
                } catch (Exception e) { /* không để lỗi đếm giỏ hàng làm hỏng trang */ }
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isStatic(String uri) {
        String u = uri.toLowerCase();
        return u.endsWith(".css") || u.endsWith(".js") || u.endsWith(".png") || u.endsWith(".jpg") || u.endsWith(".jpeg")
            || u.endsWith(".gif") || u.endsWith(".svg") || u.endsWith(".ico") || u.endsWith(".woff") || u.endsWith(".woff2")
            || u.contains("/api/");
    }
}
