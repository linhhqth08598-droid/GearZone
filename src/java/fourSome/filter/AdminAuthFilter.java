package fourSome.filter;

import fourSome.model.Users;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter bảo vệ các route dành cho Admin/Staff.
 * Chặn mọi truy cập vào /admin/* nếu chưa đăng nhập.
 */
@WebFilter(filterName = "AdminAuthFilter", urlPatterns = {"/admin/*", "/Admin*"})
public class AdminAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo filter (không cần logic đặc biệt lúc này)
        System.out.println("[Filter] AdminAuthFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // Kiểm tra xem session có tồn tại và chứa loggedUser không
        boolean isLoggedIn = (session != null && session.getAttribute("loggedUser") != null);

        if (isLoggedIn) {
            // Lấy thông tin user hiện tại
            Users user = (Users) session.getAttribute("loggedUser");
            
            // TODO: (Tuần 2) Thêm logic kiểm tra quyền (Role).
            // Lấy userRoles từ session, nếu có role ADMIN, STAFF, MANAGER... thì mới cho qua.
            // Tạm thời ở Tuần 1, chỉ cần đăng nhập là cho qua để bạn test giao diện Admin trước.
            
            // Cho phép đi tiếp tới Servlet/JSP được yêu cầu
            chain.doFilter(request, response);
        } else {
            // Lưu lại đường dẫn user định vào để có thể chuyển hướng lại sau khi login (tùy chọn)
            // String reqURI = req.getRequestURI();
            
            // Chưa đăng nhập -> Đá về trang đăng nhập
            res.sendRedirect(req.getContextPath() + "/auth?action=login");
        }
    }

    @Override
    public void destroy() {
        // Hủy filter khi server tắt
    }
}