package linhhqth08598.controller;

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

@WebServlet(name = "AdminRoleServlet", urlPatterns = {"/admin/roles"})
public class AdminRoleServlet extends HttpServlet {

    private RoleService roleService = new RoleService();
    private AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy danh sách toàn bộ Vai trò (Roles) và Quyền (Permissions)
        request.setAttribute("roles", roleService.getAllRoles());
        request.setAttribute("permissions", roleService.getAllPermissions());
        
        // Mặc định chọn role đầu tiên để cấu hình nếu không truyền lên roleId
        String roleIdParam = request.getParameter("roleId");
        int selectedRoleId = (roleIdParam != null) ? Integer.parseInt(roleIdParam) : 1;
        
        request.setAttribute("selectedRoleId", selectedRoleId);
        request.setAttribute("assignedPermissions", roleService.getPermissionIdsByRole(selectedRoleId));

        request.getRequestDispatcher("/views/linhhqth08598/admin/roles.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");
        
        int roleId = Integer.parseInt(request.getParameter("roleId"));
        String[] permissionIds = request.getParameterValues("permissions"); // Lấy mảng các checkbox quyền được chọn

        boolean success = roleService.updatePermissions(roleId, permissionIds);

        if (success) {
            session.setAttribute("message", "Cập nhật ma trận phân quyền thành công!");
            
            // Ghi Audit Log hành động thay đổi quyền
            auditLogService.logAction(
                adminUser.getUserId(),
                "UPDATE_ROLE_PERMISSIONS",
                "role_permissions",
                roleId,
                "Admin " + adminUser.getEmail() + " đã cập nhật quyền cho Role ID: " + roleId
            );
        } else {
            session.setAttribute("error", "Lỗi khi cập nhật phân quyền!");
        }

        response.sendRedirect(request.getContextPath() + "/admin/roles?roleId=" + roleId);
    }
}