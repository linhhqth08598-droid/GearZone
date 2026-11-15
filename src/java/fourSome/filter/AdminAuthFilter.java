package fourSome.filter;

import fourSome.model.Users;
import linhhqth08598.dao.RolePermissionsDAO;
import linhhqth08598.dao.UserRolesDAO;
import linhhqth08598.dao.UsersDAO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bảo vệ toàn bộ /admin/*:
 *  - Chưa đăng nhập            -> về trang đăng nhập
 *  - Tài khoản bị khóa         -> đăng xuất khỏi phiên
 *  - ADMIN / OWNER             -> toàn quyền
 *  - STAFF                     -> chỉ vào được khu vực mà ma trận phân quyền cho phép
 *                                 (GET cần *_VIEW hoặc *_MANAGE, POST cần *_MANAGE)
 *  - CUSTOMER / không có role  -> 403
 */
@WebFilter(urlPatterns = {"/admin/*"})
public class AdminAuthFilter implements Filter {

    private final UserRolesDAO userRolesDAO = new UserRolesDAO();
    private final RolePermissionsDAO rolePermissionsDAO = new RolePermissionsDAO();
    private final UsersDAO usersDAO = new UsersDAO();

    /** Đường dẫn -> khu vực quyền. Khu vực X có 2 mã quyền: X_VIEW và X_MANAGE. */
    private static final Map<String, String> AREA = new LinkedHashMap<>();
    static {
        AREA.put("/admin/order", "ORDER");
        AREA.put("/admin/orders", "ORDER");
        AREA.put("/admin/returns", "RETURN");
        AREA.put("/admin/reviews", "REVIEW");
        AREA.put("/admin/products", "PRODUCT");
        AREA.put("/admin/game", "PRODUCT");
        AREA.put("/admin/promotion", "PROMOTION");
        AREA.put("/admin/shipping", "PROMOTION");
        AREA.put("/admin/inventory", "INVENTORY");
        AREA.put("/admin/purchase", "INVENTORY");
        AREA.put("/admin/supplier", "INVENTORY");
        AREA.put("/admin/supplier-return", "INVENTORY");
        AREA.put("/admin/assembly", "TECH");
        AREA.put("/admin/warranty", "TECH");
        AREA.put("/admin/compatibility", "COMPAT");
        AREA.put("/admin/support", "SUPPORT");
        AREA.put("/admin/catalog", "CATALOG");
        AREA.put("/admin/socket", "CATALOG");
        AREA.put("/admin/system-catalog", "CATALOG");
        AREA.put("/admin/system", "CATALOG");
        AREA.put("/admin/report", "REPORT");
        AREA.put("/admin/export", "REPORT");
        AREA.put("/admin/export-report", "REPORT");
    }

    /** Chỉ ADMIN / OWNER được vào. */
    private static final String[] FULL_ACCESS_ONLY = {"/admin/users", "/admin/roles", "/admin/audit-logs"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        // 1. Lấy user đang đăng nhập
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            res.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        // 2. Tài khoản bị khóa sau khi đã đăng nhập -> đá ra ngay
        Users fresh = usersDAO.findById(loggedUser.getUserId());
        if (fresh == null || !"ACTIVE".equals(fresh.getStatus())) {
            session.invalidate();
            res.sendRedirect(req.getContextPath() + "/auth?action=login");
            return;
        }

        // 3. Vai trò + quyền mới nhất từ DB
        int userId = loggedUser.getUserId();
        List<String> roles = userRolesDAO.getRoleCodesByUserId(userId);
        Set<String> permissions = rolePermissionsDAO.getPermissionCodesByUserId(userId);
        session.setAttribute("userRoles", roles);
        session.setAttribute("userPermissions", permissions);

        boolean fullAccess = roles.contains("ADMIN") || roles.contains("OWNER");
        boolean isStaff = roles.contains("STAFF");

        if (!fullAccess && !isStaff) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập khu vực quản trị.");
            return;
        }

        // 4. ADMIN / OWNER đi thẳng
        if (fullAccess) {
            chain.doFilter(request, response);
            return;
        }

        // 5. STAFF: kiểm tra theo ma trận quyền
        String path = req.getRequestURI().substring(req.getContextPath().length());
        int semi = path.indexOf(';');
        if (semi >= 0) path = path.substring(0, semi);
        if (path.length() > 1 && path.endsWith("/")) path = path.substring(0, path.length() - 1);

        if (matches(path, "/admin/dashboard") || path.equals("/admin")) {
            chain.doFilter(request, response);
            return;
        }
        for (String p : FULL_ACCESS_ONLY) {
            if (matches(path, p)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Chức năng này chỉ dành cho Quản trị viên / Chủ cửa hàng.");
                return;
            }
        }

        String area = null;
        for (Map.Entry<String, String> e : AREA.entrySet()) {
            if (matches(path, e.getKey())) { area = e.getValue(); break; }
        }
        if (area == null) { // đường dẫn lạ -> mặc định chặn STAFF
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập chức năng này.");
            return;
        }

        boolean readOnly = "GET".equalsIgnoreCase(req.getMethod()) || "HEAD".equalsIgnoreCase(req.getMethod());
        boolean allowed;
        if ("REPORT".equals(area)) {
            allowed = permissions.contains("REPORT_VIEW");
        } else if (readOnly) {
            allowed = permissions.contains(area + "_VIEW") || permissions.contains(area + "_MANAGE");
        } else {
            allowed = permissions.contains(area + "_MANAGE");
        }

        if (!allowed) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Vai trò của bạn chưa được cấp quyền cho chức năng này.");
            return;
        }
        chain.doFilter(request, response);
    }

    /** path trùng khít prefix hoặc nằm dưới prefix (tránh /admin/order khớp nhầm /admin/orders-x). */
    private static boolean matches(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}
