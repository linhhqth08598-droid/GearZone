package linhhqth08598.controller;

import fourSome.model.Users;
import linhhqth08598.service.AuditLogService;
import linhhqth08598.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private UserService userService = new UserService();
    private AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        if (action.equals("list")) {
            List<Users> userList = userService.getAllUsers();
            request.setAttribute("users", userList);
            request.getRequestDispatcher("/views/linhhqth08598/admin/users.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");
        String action = request.getParameter("action");

        if ("toggleStatus".equals(action)) {
            int targetUserId = Integer.parseInt(request.getParameter("userId"));
            
            // Không cho phép Admin tự khóa chính mình
            if (targetUserId == adminUser.getUserId()) {
                session.setAttribute("error", "Bạn không thể tự khóa tài khoản của chính mình!");
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }

            String error = userService.toggleUserStatus(targetUserId);
            
            if (error == null) {
                session.setAttribute("message", "Cập nhật trạng thái tài khoản thành công!");
                
                // GHI AUDIT LOG THAO TÁC CỦA ADMIN
                auditLogService.logAction(
                    adminUser.getUserId(), 
                    "TOGGLE_USER_STATUS", 
                    "users", 
                    targetUserId, 
                    "Admin " + adminUser.getEmail() + " đã thay đổi trạng thái của User ID: " + targetUserId
                );
            } else {
                session.setAttribute("error", error);
            }
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }
}