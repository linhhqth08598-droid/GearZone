package linhhqth08598.controller;

import fourSome.model.Users;
import linhhqth08598.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@WebServlet(name = "UserProfileServlet", urlPatterns = {"/user/profile"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5) // Giới hạn ảnh 5MB
public class UserProfileServlet extends HttpServlet {

    private UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("loggedUser") == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        request.getRequestDispatcher("/views/linhhqth08598/user/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        
        if ("updateProfile".equals(action)) {
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");

            String error = userService.updateProfile(user.getUserId(), fullName, phone);
            
            if (error == null) {
                user.setFullName(fullName);
                user.setPhoneNumber(phone);
                session.setAttribute("loggedUser", user);
                session.setAttribute("message", "Cập nhật thông tin thành công!");
            } else {
                session.setAttribute("error", error);
            }
            
        } else if ("updateAvatar".equals(action)) {
            try {
                Part filePart = request.getPart("avatarFile");
                if (filePart != null && filePart.getSize() > 0) {
                    InputStream inputStream = filePart.getInputStream();
                    byte[] bytes = inputStream.readAllBytes();
                    String base64Image = "data:" + filePart.getContentType() + ";base64," + Base64.getEncoder().encodeToString(bytes);

                    // GỌI QUA SERVICE (đã có sẵn hàm updateAvatar)
                    String error = userService.updateAvatar(user.getUserId(), base64Image);
                    
                    if (error == null) {
                        user.setAvatarUrl(base64Image);
                        session.setAttribute("loggedUser", user);
                        session.setAttribute("message", "Cập nhật ảnh đại diện thành công!");
                    } else {
                        session.setAttribute("error", error);
                    }
                } else {
                    session.setAttribute("error", "Vui lòng chọn ảnh!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("error", "Lỗi khi upload ảnh!");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/user/profile");
    }
}