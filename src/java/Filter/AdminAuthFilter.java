package Filter;

import Models.User;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Giả sử các trang quản trị nằm trong thư mục /admin/
@WebFilter(filterName = "AdminAuthFilter", urlPatterns = {"/admin/*"})
public class AdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        
        boolean isLoggedIn = (session != null && session.getAttribute("userAcc") != null);
        
        if (isLoggedIn) {
            User u = (User) session.getAttribute("userAcc");
            // Check Role: 1 (Admin), 2 (Cửa hàng trưởng), 3 (Nhân viên)
            if (u.getRoleId() == 1 || u.getRoleId() == 2 || u.getRoleId() == 3) {
                chain.doFilter(request, response); // Cho phép qua
            } else {
                res.sendRedirect(req.getContextPath() + "/views/403.jsp"); // Không có quyền
            }
        } else {
            res.sendRedirect(req.getContextPath() + "/views/login.jsp");
        }
    }
}