package linhhqth08598.controller;

import fourSome.model.Roles;
import fourSome.model.Users;
import fourSome.util.Constants;
import fourSome.util.PaginationUtil;
import linhhqth08598.service.AuditLogService;
import linhhqth08598.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final AuditLogService auditLogService = new AuditLogService();

    // =====================================================================
    //  GET: danh sách + tìm kiếm + lọc + phân trang
    // =====================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = trim(request.getParameter("q"));
        String status = trim(request.getParameter("status"));
        String roleCode = trim(request.getParameter("role"));
        if (!"ACTIVE".equals(status) && !"LOCKED".equals(status)) status = "";

        int total = userService.countUsers(keyword, status, roleCode);
        PaginationUtil page = new PaginationUtil(request.getParameter("page"), Constants.ADMIN_PAGE_SIZE, total);
        List<Users> users = userService.searchUsers(keyword, status, roleCode, page.getOffset(), page.getPageSize());

        List<Integer> ids = new ArrayList<>();
        for (Users u : users) ids.add(u.getUserId());

        request.setAttribute("users", users);
        request.setAttribute("userRolesMap", userService.getRolesOfUsers(ids));
        request.setAttribute("allRoles", userService.getAllRoles());
        request.setAttribute("pagination", page);
        request.setAttribute("totalUsers", total);
        request.setAttribute("fKeyword", keyword);
        request.setAttribute("fStatus", status);
        request.setAttribute("fRole", roleCode);
        request.setAttribute("isAdminActor", actorRoles(request).contains("ADMIN"));

        request.getRequestDispatcher("/views/linhhqth08598/admin/users.jsp").forward(request, response);
    }

    // =====================================================================
    //  POST: create | update | toggleStatus | resetPassword
    // =====================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Users admin = (Users) session.getAttribute("loggedUser");
        String action = request.getParameter("action");
        String back = safeBackQuery(request.getParameter("back"));
        String redirect = request.getContextPath() + "/admin/users" + back;

        if (action == null) {
            response.sendRedirect(redirect);
            return;
        }

        switch (action) {
            case "create":
                handleCreate(request, session, admin);
                break;
            case "update":
                handleUpdate(request, session, admin);
                break;
            case "toggleStatus":
                handleToggle(request, session, admin);
                break;
            case "resetPassword":
                handleResetPassword(request, session, admin);
                break;
            default:
                break;
        }
        response.sendRedirect(redirect);
    }

    // ---------------------------------------------------------------------
    private void handleCreate(HttpServletRequest request, HttpSession session, Users admin) {
        List<Integer> roleIds = parseRoleIds(request);
        if (!canAssign(request, roleIds)) {
            session.setAttribute("error", "Chỉ Quản trị viên mới được cấp vai trò Quản trị viên / Chủ cửa hàng!");
            return;
        }
        String email = request.getParameter("email");
        String error = userService.createUser(email, request.getParameter("fullName"),
                request.getParameter("phone"), request.getParameter("password"), roleIds);
        if (error == null) {
            session.setAttribute("message", "Đã tạo tài khoản " + email.trim().toLowerCase() + " thành công!");
            auditLogService.logAction(admin.getUserId(), "CREATE_USER", "users", null,
                    "Admin " + admin.getEmail() + " đã tạo tài khoản " + email.trim().toLowerCase());
        } else {
            session.setAttribute("error", error);
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpSession session, Users admin) {
        Integer targetId = parseInt(request.getParameter("userId"));
        if (targetId == null) { session.setAttribute("error", "Tài khoản không hợp lệ!"); return; }
        boolean self = targetId.equals(admin.getUserId());

        if (!canManageTarget(request, targetId, self)) {
            session.setAttribute("error", "Bạn không có quyền chỉnh sửa tài khoản Quản trị viên / Chủ cửa hàng khác!");
            return;
        }

        // Không cho tự đổi vai trò của chính mình (tránh tự tước quyền, khóa mình ngoài hệ thống)
        List<Integer> roleIds = null;
        if (!self && request.getParameterValues("roleIds") != null) {
            roleIds = parseRoleIds(request);
            if (!canAssign(request, roleIds)) {
                session.setAttribute("error", "Chỉ Quản trị viên mới được cấp vai trò Quản trị viên / Chủ cửa hàng!");
                return;
            }
        } else if (!self) {
            roleIds = new ArrayList<>(); // form có gửi nhưng bỏ chọn hết -> báo lỗi "ít nhất 1 vai trò"
        }

        String error = userService.adminUpdateUser(targetId, request.getParameter("fullName"),
                request.getParameter("phone"), roleIds);
        if (error == null) {
            session.setAttribute("message", "Cập nhật tài khoản thành công!"
                    + (self ? " (Vai trò của chính bạn không thể tự thay đổi.)" : ""));
            auditLogService.logAction(admin.getUserId(), "UPDATE_USER", "users", targetId,
                    "Admin " + admin.getEmail() + " đã cập nhật thông tin / vai trò của User ID: " + targetId);
            // Cập nhật lại session nếu tự sửa mình
            if (self) {
                admin.setFullName(request.getParameter("fullName").trim());
                String ph = request.getParameter("phone");
                admin.setPhoneNumber(ph == null || ph.trim().isEmpty() ? null : ph.trim());
                session.setAttribute("loggedUser", admin);
            }
        } else {
            session.setAttribute("error", error);
        }
    }

    private void handleToggle(HttpServletRequest request, HttpSession session, Users admin) {
        Integer targetId = parseInt(request.getParameter("userId"));
        if (targetId == null) { session.setAttribute("error", "Tài khoản không hợp lệ!"); return; }

        if (targetId.equals(admin.getUserId())) {
            session.setAttribute("error", "Bạn không thể tự khóa tài khoản của chính mình!");
            return;
        }
        if (!canManageTarget(request, targetId, false)) {
            session.setAttribute("error", "Bạn không có quyền khóa tài khoản Quản trị viên / Chủ cửa hàng!");
            return;
        }

        String error = userService.toggleUserStatus(targetId);
        if (error == null) {
            session.setAttribute("message", "Cập nhật trạng thái tài khoản thành công!");
            auditLogService.logAction(admin.getUserId(), "TOGGLE_USER_STATUS", "users", targetId,
                    "Admin " + admin.getEmail() + " đã thay đổi trạng thái của User ID: " + targetId);
        } else {
            session.setAttribute("error", error);
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpSession session, Users admin) {
        Integer targetId = parseInt(request.getParameter("userId"));
        if (targetId == null) { session.setAttribute("error", "Tài khoản không hợp lệ!"); return; }
        if (targetId.equals(admin.getUserId())) {
            session.setAttribute("error", "Hãy dùng chức năng Đổi mật khẩu để đổi mật khẩu của chính bạn!");
            return;
        }
        if (!canManageTarget(request, targetId, false)) {
            session.setAttribute("error", "Bạn không có quyền đặt lại mật khẩu của Quản trị viên / Chủ cửa hàng!");
            return;
        }
        Users target = userService.getUserById(targetId);
        String[] result = userService.resetPassword(targetId);
        if (result[0] == null && target != null) {
            // Mật khẩu tạm chỉ hiển thị 1 lần (JSP dùng c:out và xóa khỏi session ngay)
            session.setAttribute("tempPasswordEmail", target.getEmail());
            session.setAttribute("tempPassword", result[1]);
            auditLogService.logAction(admin.getUserId(), "RESET_USER_PASSWORD", "users", targetId,
                    "Admin " + admin.getEmail() + " đã đặt lại mật khẩu của User ID: " + targetId);
        } else {
            session.setAttribute("error", result[0]);
        }
    }

    // =====================================================================
    //  Quy tắc phân quyền
    // =====================================================================
    @SuppressWarnings("unchecked")
    private List<String> actorRoles(HttpServletRequest request) {
        Object r = request.getSession().getAttribute("userRoles");
        return r instanceof List ? (List<String>) r : new ArrayList<String>();
    }

    /** Chỉ ADMIN được cấp / thu hồi vai trò ADMIN, OWNER. */
    private boolean canAssign(HttpServletRequest request, List<Integer> roleIds) {
        if (actorRoles(request).contains("ADMIN")) return true;
        Map<Integer, String> codeById = new HashMap<>();
        for (Roles r : userService.getAllRoles()) codeById.put(r.getRoleId(), r.getRoleCode());
        for (Integer id : roleIds) {
            String code = codeById.get(id);
            if ("ADMIN".equals(code) || "OWNER".equals(code)) return false;
        }
        return true;
    }

    /** Người không phải ADMIN thì không được động tới tài khoản ADMIN / OWNER của người khác. */
    private boolean canManageTarget(HttpServletRequest request, int targetId, boolean self) {
        if (self || actorRoles(request).contains("ADMIN")) return true;
        List<String> targetRoles = userService.getRoleCodes(targetId);
        return !(targetRoles.contains("ADMIN") || targetRoles.contains("OWNER"));
    }

    // =====================================================================
    //  Tiện ích
    // =====================================================================
    private List<Integer> parseRoleIds(HttpServletRequest request) {
        List<Integer> ids = new ArrayList<>();
        String[] raw = request.getParameterValues("roleIds");
        if (raw != null) {
            for (String s : raw) {
                Integer v = parseInt(s);
                if (v != null) ids.add(v);
            }
        }
        return ids;
    }

    private Integer parseInt(String s) {
        try { return Integer.valueOf(s.trim()); } catch (Exception e) { return null; }
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /** Chỉ cho phép quay lại với query string an toàn (tránh open-redirect / header injection). */
    private String safeBackQuery(String back) {
        if (back == null || back.isEmpty()) return "";
        if (back.matches("^\\?[A-Za-z0-9=&%+_.\\-]*$") && back.length() < 300) return back;
        return "";
    }
}
