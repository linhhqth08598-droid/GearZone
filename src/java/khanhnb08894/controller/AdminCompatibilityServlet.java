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
            if ("save".equals(action)) {
                CompatibilityRules rule = new CompatibilityRules();
                rule.setRuleName(request.getParameter("ruleName"));
                rule.setPrimaryCategoryId(Integer.parseInt(request.getParameter("primaryCategoryId")));
                rule.setTargetCategoryId(Integer.parseInt(request.getParameter("targetCategoryId")));
                rule.setCheckType(request.getParameter("checkType"));
                rule.setErrorMessage(request.getParameter("errorMessage"));
                rule.setSeverity(request.getParameter("severity"));

                if (dao.insert(rule)) {
                    session.setAttribute("message", "Thêm quy tắc thành công!");
                    AuditLogService log = new AuditLogService();
                    log.logAction(adminUser.getUserId(), "INSERT_RULE", "compatibility_rules", 0, "Thêm quy tắc: " + rule.getRuleName());
                } else {
                    session.setAttribute("error", "Lỗi khi thêm quy tắc!");
                }
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("ruleId"));
                if (dao.softDelete(id)) {
                    session.setAttribute("message", "Đã ẩn quy tắc!");
                }
            }
        } catch (Exception e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/compatibility");
    }
}