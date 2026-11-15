package linhhqth08598.controller;

import fourSome.model.Permissions;
import fourSome.model.Roles;
import fourSome.model.Users;
import linhhqth08598.service.AuditLogService;
import linhhqth08598.service.RoleService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminRoleServlet", urlPatterns = {"/admin/roles"})
public class AdminRoleServlet extends HttpServlet {

    private RoleService roleService = new RoleService();
    private AuditLogService auditLogService = new AuditLogService();

    /** Tên hiển thị của từng nhóm quyền (cột module trong DB là mã không dấu). */
    private static final Map<String, String> MODULE_LABELS = new LinkedHashMap<>();
    static {
        MODULE_LABELS.put("SALES", "Bán hàng");
        MODULE_LABELS.put("WAREHOUSE", "Kho");
        MODULE_LABELS.put("TECHNICAL", "Kỹ thuật");
        MODULE_LABELS.put("SUPPORT", "Chăm sóc khách hàng & Tư vấn");
        MODULE_LABELS.put("CATALOG", "Danh mục hệ thống");
        MODULE_LABELS.put("REPORT", "Thống kê & Báo cáo");
    }

    /** ADMIN, OWNER luôn có toàn quyền (AdminAuthFilter cho qua), nên không cho chỉnh ma trận của 2 vai trò này. */
    private boolean isFullAccessRole(Roles r) {
        return r != null && ("ADMIN".equals(r.getRoleCode()) || "OWNER".equals(r.getRoleCode()));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Roles> roles = roleService.getAllRoles();
        List<Permissions> permissions = roleService.getAllPermissions();

        // Mặc định chọn role đầu tiên có trong DB (không giả định role_id = 1)
        int selectedRoleId = roles.isEmpty() ? 0 : roles.get(0).getRoleId();
        String roleIdParam = request.getParameter("roleId");
        if (roleIdParam != null && roleIdParam.matches("\\d{1,9}")) {
            selectedRoleId = Integer.parseInt(roleIdParam);
        }
        Roles selectedRole = null;
        for (Roles r : roles) {
            if (r.getRoleId() == selectedRoleId) selectedRole = r;
        }
        if (selectedRole == null && !roles.isEmpty()) {
            selectedRole = roles.get(0);
            selectedRoleId = selectedRole.getRoleId();
        }

        // Nhóm quyền theo module để hiển thị thành từng khối
        Map<String, List<Permissions>> grouped = new LinkedHashMap<>();
        for (String m : MODULE_LABELS.keySet()) grouped.put(m, new java.util.ArrayList<>());
        for (Permissions p : permissions) {
            grouped.computeIfAbsent(p.getModule(), k -> new java.util.ArrayList<>()).add(p);
        }
        grouped.values().removeIf(List::isEmpty);

        request.setAttribute("roles", roles);
        request.setAttribute("permissions", permissions);
        request.setAttribute("groupedPermissions", grouped);
        request.setAttribute("moduleLabels", MODULE_LABELS);
        request.setAttribute("selectedRoleId", selectedRoleId);
        request.setAttribute("selectedRole", selectedRole);
        request.setAttribute("lockedRole", isFullAccessRole(selectedRole));
        request.setAttribute("assignedPermissions", selectedRole == null
                ? new java.util.ArrayList<Integer>() : roleService.getPermissionIdsByRole(selectedRoleId));

        request.getRequestDispatcher("/views/linhhqth08598/admin/roles.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        String roleIdParam = request.getParameter("roleId");
        if (roleIdParam == null || !roleIdParam.matches("\\d{1,9}")) {
            session.setAttribute("error", "Vai trò không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/admin/roles");
            return;
        }
        int roleId = Integer.parseInt(roleIdParam);
        Roles role = roleService.getRoleById(roleId);

        if (role == null) {
            session.setAttribute("error", "Vai trò không tồn tại!");
            response.sendRedirect(request.getContextPath() + "/admin/roles");
            return;
        }
        if (isFullAccessRole(role)) {
            session.setAttribute("error", "Vai trò " + role.getRoleName() + " luôn có toàn quyền, không thể chỉnh sửa.");
            response.sendRedirect(request.getContextPath() + "/admin/roles?roleId=" + roleId);
            return;
        }

        String[] permissionIds = request.getParameterValues("permissions");
        boolean success = roleService.updatePermissions(roleId, permissionIds);

        if (success) {
            int count = permissionIds == null ? 0 : permissionIds.length;
            session.setAttribute("message", "Đã lưu " + count + " quyền cho vai trò " + role.getRoleName() + "!");
            auditLogService.logAction(
                adminUser.getUserId(),
                "UPDATE_ROLE_PERMISSIONS",
                "role_permissions",
                roleId,
                "Admin " + adminUser.getEmail() + " đã cập nhật quyền cho vai trò " + role.getRoleCode() + " (" + count + " quyền)"
            );
        } else {
            session.setAttribute("error", "Lỗi khi cập nhật phân quyền!");
        }

        response.sendRedirect(request.getContextPath() + "/admin/roles?roleId=" + roleId);
    }
}
