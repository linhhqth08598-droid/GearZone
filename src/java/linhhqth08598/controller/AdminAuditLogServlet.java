package linhhqth08598.controller;

import fourSome.model.StaffAuditLogs;
import fourSome.util.Constants;
import fourSome.util.PaginationUtil;
import linhhqth08598.service.AuditLogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminAuditLogServlet", urlPatterns = {"/admin/audit-logs"})
public class AdminAuditLogServlet extends HttpServlet {

    private AuditLogService auditLogService = new AuditLogService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        int total = auditLogService.countLogs(keyword, fromDate, toDate);
        PaginationUtil page = new PaginationUtil(request.getParameter("page"), Constants.ADMIN_PAGE_SIZE, total);
        List<StaffAuditLogs> logs = auditLogService.searchLogsPaged(keyword, fromDate, toDate, page.getOffset(), page.getPageSize());

        request.setAttribute("auditLogs", logs);
        request.setAttribute("pagination", page);
        request.getRequestDispatcher("/views/linhhqth08598/admin/audit_logs.jsp").forward(request, response);
    }
}