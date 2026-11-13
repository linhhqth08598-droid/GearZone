package fourSome.filter;

import fourSome.model.Users;
import linhhqth08598.dao.UserRolesDAO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebFilter(urlPatterns = {"/admin/*"})
public class AdminAuthFilter implements Filter {

    private UserRolesDAO userRolesDAO = new UserRolesDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        
        // Thống nhất dùng "loggedUser" theo checklist của nhóm
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        
        // 1. Chưa đăng nhập -> Đá về trang đăng nhập
        if (loggedUser == null) {
            res.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }
        
        // 2. Lấy danh sách roles của user này và lưu lên Session để dùng lại
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) session.getAttribute("userRoles");
        if (roles == null) {
            roles = userRolesDAO.getRoleCodesByUserId(loggedUser.getUserId());
            session.setAttribute("userRoles", roles);
        }
        
        // 3. Nếu là CUSTOMER (khách hàng bình thường) và không có bất kỳ role nhân viên nào -> Báo lỗi 403
        if (roles.isEmpty() || (roles.size() == 1 && roles.contains("CUSTOMER"))) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập khu vực quản trị.");
            return;
        }
        
        // Hợp lệ -> Cho phép đi tiếp vào trang Admin
        chain.doFilter(request, response);
    }
}