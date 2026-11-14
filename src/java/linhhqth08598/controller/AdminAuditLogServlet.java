package linhhqth08598.controller;

import fourSome.model.StaffAuditLogs;
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

        List<StaffAuditLogs> logs = auditLogService.searchLogs(keyword, fromDate, toDate);
        
        request.setAttribute("auditLogs", logs);
        request.getRequestDispatcher("/views/linhhqth08598/admin/audit_logs.jsp").forward(request, response);
    }
}