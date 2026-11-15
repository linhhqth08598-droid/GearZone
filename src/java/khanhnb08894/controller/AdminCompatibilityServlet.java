package khanhnb08894.controller;

import fourSome.model.Users;
import fourSome.model.CompatibilityRules;
import khanhnb08894.service.CatalogService; // Import để lấy danh sách Categories
import linhhqth08598.dao.CompatibilityRulesDAO;
import linhhqth08598.service.AuditLogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "AdminCompatibilityServlet", urlPatterns = {"/admin/compatibility"})
public class AdminCompatibilityServlet extends HttpServlet {
    private CompatibilityRulesDAO dao = new CompatibilityRulesDAO();
    private CatalogService catalogService = new CatalogService(); // Tái sử dụng

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("rules", dao.getAll());
        // Lấy danh sách Categories đổ vào Dropdown
        request.setAttribute("categories", catalogService.getAllCategories()); 
        request.getRequestDispatcher("/views/khanhnb08894/admin/compatibility.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        try {
            if ("save".equals(action) || "update".equals(action)) {
                boolean isUpdate = "update".equals(action);
                CompatibilityRules rule = new CompatibilityRules();
                String name = request.getParameter("ruleName");
                String type = request.getParameter("checkType");
                String msg = request.getParameter("errorMessage");
                if (name == null || name.trim().isEmpty() || type == null || type.trim().isEmpty()
                        || msg == null || msg.trim().isEmpty()) {
                    session.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
                    response.sendRedirect(request.getContextPath() + "/admin/compatibility");
                    return;
                }
                rule.setRuleName(name.trim());
                rule.setPrimaryCategoryId(Integer.parseInt(request.getParameter("primaryCategoryId")));
                rule.setTargetCategoryId(Integer.parseInt(request.getParameter("targetCategoryId")));
                rule.setCheckType(type.trim().toUpperCase());
                rule.setErrorMessage(msg.trim());
                String sev = request.getParameter("severity");
                rule.setSeverity("WARNING".equals(sev) ? "WARNING" : "ERROR");

                AuditLogService log = new AuditLogService();
                if (isUpdate) {
                    rule.setRuleId(Integer.parseInt(request.getParameter("ruleId")));
                    if (dao.update(rule)) {
                        session.setAttribute("message", "Đã cập nhật quy tắc!");
                        log.logAction(adminUser.getUserId(), "UPDATE_RULE", "compatibility_rules", rule.getRuleId(), "Sửa quy tắc: " + rule.getRuleName());
                    } else {
                        session.setAttribute("error", "Lỗi khi cập nhật quy tắc!");
                    }
                } else if (dao.insert(rule)) {
                    session.setAttribute("message", "Thêm quy tắc thành công!");
                    log.logAction(adminUser.getUserId(), "INSERT_RULE", "compatibility_rules", 0, "Thêm quy tắc: " + rule.getRuleName());
                } else {
                    session.setAttribute("error", "Lỗi khi thêm quy tắc!");
                }
            } else if ("delete".equals(action) || "restore".equals(action)) {
                int id = Integer.parseInt(request.getParameter("ruleId"));
                boolean hide = "delete".equals(action);
                if (dao.setStatus(id, hide ? "INACTIVE" : "ACTIVE")) {
                    session.setAttribute("message", hide ? "Đã ẩn quy tắc!" : "Đã hiện lại quy tắc!");
                    new AuditLogService().logAction(adminUser.getUserId(), hide ? "HIDE_RULE" : "RESTORE_RULE", "compatibility_rules", id, (hide ? "Ẩn" : "Hiện") + " quy tắc #" + id);
                }
            }
        } catch (Exception e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/compatibility");
    }
}