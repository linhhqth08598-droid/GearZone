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
        
        // 1. Lấy user đang đăng nhập (nếu có)
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        
        // 2. Nếu là khách vãng lai (GUEST) -> Đá về trang đăng nhập
        if (loggedUser == null) {
            res.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }
        
        // 3. Lấy danh sách quyền mới nhất từ DB
        List<String> roles = userRolesDAO.getRoleCodesByUserId(loggedUser.getUserId());
        session.setAttribute("userRoles", roles);
        
        // 4. Kiểm tra quyền: Chỉ ADMIN, OWNER hoặc STAFF mới được vào
        boolean isAdmin = roles.contains("ADMIN");
        boolean isOwner = roles.contains("OWNER");
        boolean isStaff = roles.contains("STAFF");
        
        // Nếu chỉ là CUSTOMER (User) hoặc không có quyền gì -> Chặn
        if (!isAdmin && !isOwner && !isStaff) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập khu vực quản trị.");
            return;
        }
        
        // Hợp lệ -> Cho phép đi tiếp
        chain.doFilter(request, response);
    }
}