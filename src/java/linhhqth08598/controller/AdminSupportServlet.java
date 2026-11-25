package linhhqth08598.controller;

import fourSome.util.Constants;
import fourSome.util.PaginationUtil;
import fourSome.model.SiteContactInfo;
import fourSome.model.Users;
import fourSome.model.SupportTickets;
import linhhqth08598.dao.SiteContactInfoDAO;
import linhhqth08598.service.AuditLogService;
import linhhqth08598.service.SupportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminSupportServlet", urlPatterns = {"/admin/support"})
public class AdminSupportServlet extends HttpServlet {

    private SupportService supportService = new SupportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<SupportTickets> tickets = supportService.getAllTicketsForAdmin();
        PaginationUtil page = new PaginationUtil(request.getParameter("page"), Constants.ADMIN_PAGE_SIZE, tickets.size());
        tickets = page.slice(tickets);
        request.setAttribute("pagination", page);
        request.setAttribute("ticketsList", tickets);
        request.setAttribute("siteContactInfo", new SiteContactInfoDAO().getActiveInfo());
        request.getRequestDispatcher("/views/linhhqth08598/admin/support.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        String action = request.getParameter("action");
        if ("updateContactInfo".equals(action)) {
            if (adminUser == null) {
                session.setAttribute("error", "Bạn chưa đăng nhập!");
                response.sendRedirect(request.getContextPath() + "/auth?action=login");
                return;
            }
            SiteContactInfo info = new SiteContactInfo();
            info.setCompanyName(request.getParameter("companyName"));
            info.setIntroText(request.getParameter("introText"));
            String[] addrParams = request.getParameterValues("addresses");
            if (addrParams != null) {
                java.util.List<String> addrList = new java.util.ArrayList<>();
                for (String a : addrParams) {
                    if (a != null && !a.trim().isEmpty()) {
                        addrList.add(a.trim());
                    }
                }
                info.setAddresses(addrList);
                info.setAddress1(!addrList.isEmpty() ? addrList.get(0) : null);
                info.setAddress2(addrList.size() > 1 ? addrList.get(1) : null);
            } else {
                info.setAddress1(request.getParameter("address1"));
                info.setAddress2(request.getParameter("address2"));
            }
            info.setSalesHours(request.getParameter("salesHours"));
            info.setTechHours(request.getParameter("techHours"));
            info.setHotline(request.getParameter("hotline"));
            info.setFacebookUrl(request.getParameter("facebookUrl"));
            info.setYoutubeUrl(request.getParameter("youtubeUrl"));
            info.setChatUrl(request.getParameter("chatUrl"));
            info.setUpdatedBy(adminUser.getUserId());

            if (new SiteContactInfoDAO().saveInfo(info)) {
                new AuditLogService().logAction(adminUser.getUserId(), "UPDATE_CONTACT_INFO", "site_contact_info", 1, "Cập nhật thông tin liên hệ cửa hàng");
                session.setAttribute("message", "Cập nhật thông tin liên hệ thành công!");
            } else {
                session.setAttribute("error", "Không thể cập nhật thông tin liên hệ!");
            }
            response.sendRedirect(request.getContextPath() + "/admin/support");
            return;
        }

        try {
            int ticketId = Integer.parseInt(request.getParameter("ticketId"));
            String status = request.getParameter("status");

            String error = supportService.processTicket(ticketId, status, adminUser);

            if (error == null) {
                session.setAttribute("message", "Cập nhật trạng thái ticket thành công!");
            } else {
                session.setAttribute("error", error);
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/support");
    }
}