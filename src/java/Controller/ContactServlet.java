package Controller;

import DAO.ContactDAO;
import DAO.StoreSettingDAO;
import Models.StoreSetting;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ContactServlet", urlPatterns = {"/contact", "/submit-contact", "/admin/update-contact-info", "/admin/messages", "/admin/mark-read", "/admin/delete-message"})
public class ContactServlet extends HttpServlet {

    private ContactDAO contactDAO = new ContactDAO();
    private StoreSettingDAO settingDAO = new StoreSettingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/contact".equals(path)) {
            Models.StoreSetting setting = settingDAO.getStoreSetting();
            request.setAttribute("storeSetting", setting);
            request.setAttribute("activeMenu", "contact");
            request.getRequestDispatcher("/views/contact.jsp").forward(request, response);
            
        } else if ("/admin/messages".equals(path)) {
            // Lấy danh sách tin nhắn đẩy ra giao diện Admin
            request.setAttribute("messageList", contactDAO.getAllMessages());
            request.getRequestDispatcher("/views/admin-messages.jsp").forward(request, response);
            
        } else if ("/admin/mark-read".equals(path)) {
            // Đánh dấu đã đọc
            int id = Integer.parseInt(request.getParameter("id"));
            contactDAO.markAsRead(id);
            response.sendRedirect(request.getContextPath() + "/admin/messages");
            
        } else if ("/admin/delete-message".equals(path)) {
            // Xóa tin nhắn
            int id = Integer.parseInt(request.getParameter("id"));
            contactDAO.deleteMessage(id);
            request.getSession().setAttribute("successMsg", "Đã xóa tin nhắn!");
            response.sendRedirect(request.getContextPath() + "/admin/messages");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if ("/submit-contact".equals(path)) {
            String name = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String content = request.getParameter("content");

            boolean isSuccess = contactDAO.insertMessage(name, email, phone, content);

            if (isSuccess) {
                request.getSession().setAttribute("successMsg", "Cảm ơn bạn! Thông tin liên hệ đã được gửi thành công.");
            } else {
                request.getSession().setAttribute("errorMsg", "Có lỗi xảy ra, vui lòng thử lại sau.");
            }
            response.sendRedirect(request.getContextPath() + "/contact");
            
        } else if ("/admin/update-contact-info".equals(path)) {
            // Admin cập nhật thông tin cửa hàng
            StoreSetting s = new StoreSetting();
            s.setSettingId(Integer.parseInt(request.getParameter("settingId")));
            s.setCompanyName(request.getParameter("companyName"));
            s.setBusinessLicense(request.getParameter("businessLicense"));
            s.setAddress1(request.getParameter("address1"));
            s.setAddress2(request.getParameter("address2"));
            s.setHotline(request.getParameter("hotline"));
            s.setWebsiteUrl(request.getParameter("websiteUrl"));
            s.setFanpageUrl(request.getParameter("fanpageUrl"));
            s.setEmail(request.getParameter("email"));
            s.setGoogleMapIframe(request.getParameter("googleMapIframe"));

            settingDAO.updateStoreSetting(s);
            request.getSession().setAttribute("successMsg", "Đã cập nhật thông tin cửa hàng thành công!");
            response.sendRedirect(request.getContextPath() + "/contact");
        }
    }
}