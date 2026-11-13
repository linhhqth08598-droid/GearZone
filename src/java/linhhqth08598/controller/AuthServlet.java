package linhhqth08598.controller;

import fourSome.model.Users;
import linhhqth08598.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

    private AuthService authService = new AuthService();

    // Xử lý việc ĐIỀU HƯỚNG sang các trang giao diện (JSP)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "login";
        }

        switch (action) {
            case "login":
                request.getRequestDispatcher("/views/linhhqth08598/auth/login.jsp").forward(request, response);
                break;
            case "register":
                request.getRequestDispatcher("/views/linhhqth08598/auth/register.jsp").forward(request, response);
                break;
            case "forget":
                request.getRequestDispatcher("/views/linhhqth08598/auth/forgetPS.jsp").forward(request, response);
                break;
            case "reset":
                String email = request.getParameter("email");
                if (email == null) {
                    email = (String) request.getSession().getAttribute("reset_email");
                }
                request.setAttribute("email", email);

                // Lấy thời gian hết hạn từ session hoặc tự tính lại nếu cần
                Object expiry = request.getSession().getAttribute("otp_expiry");
                if (expiry != null) {
                    request.setAttribute("expiresAt", ((java.sql.Timestamp) expiry).getTime()); // Ép kiểu về mili-giây
                } else {
                    // Nếu mất session, tạm tính 5 phút từ hiện tại
                    request.setAttribute("expiresAt", System.currentTimeMillis() + (5 * 60 * 1000));
                }

                request.getRequestDispatcher("/views/linhhqth08598/auth/resetPS.jsp").forward(request, response);
                break;
            case "logout":
                request.getSession().invalidate(); // Xóa phiên đăng nhập
                response.sendRedirect(request.getContextPath() + "/auth?action=login");
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/auth?action=login");
        }
    }

    // Xử lý DỮ LIỆU FORM do người dùng submit lên (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đảm bảo không bị lỗi font tiếng Việt khi submit form
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "login";
        }

        switch (action) {
            case "login":
                handleLogin(request, response);
                break;
            case "register":
                handleRegister(request, response);
                break;
            case "forget":
                handleForgetPassword(request, response);
                break;
            case "reset":
                handleResetPassword(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/auth?action=login");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Users user = authService.login(email, password);

        if (user != null) {
            // Lưu thông tin user vào Session (dùng hằng SESSION_USER chung cho cả nhóm)
            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", user);

            // Tạm thời redirect về trang chủ của nhóm, bạn đổi lại URL trang chủ thực tế sau nhé
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không chính xác, hoặc tài khoản bị khóa!");
            request.setAttribute("email", email); // Giữ lại email trên form
            request.getRequestDispatcher("/views/linhhqth08598/auth/login.jsp").forward(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");

        String error = authService.register(email, phone, password, fullName);

        if (error == null) {
            // Đăng ký thành công, chuyển hướng sang trang đăng nhập kèm thông báo
            request.getSession().setAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {
            // Lỗi (trùng email, v.v.), đẩy về lại form đăng ký
            request.setAttribute("error", error);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("fullName", fullName);
            request.getRequestDispatcher("/views/linhhqth08598/auth/register.jsp").forward(request, response);
        }
    }

    private void handleForgetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");

        // Gọi service mới
        java.util.Map<String, Object> result = authService.forgotPassword(email);

        if (result.get("error") == null) {
            // Gửi OTP thành công, lưu email và thời gian hết hạn vào Session tạm để hiển thị
            request.getSession().setAttribute("reset_email", email);
            request.getSession().setAttribute("otp_expiry", result.get("expiresAt"));

            // Chuyển sang trang nhập OTP
            response.sendRedirect(request.getContextPath() + "/auth?action=reset");
        } else {
            request.setAttribute("error", result.get("error"));
            request.getRequestDispatcher("/views/linhhqth08598/auth/forgetPS.jsp").forward(request, response);
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String otp = request.getParameter("otp");
        String newPassword = request.getParameter("newPassword");

        String error = authService.resetPassword(email, otp, newPassword);

        if (error == null) {
            request.getSession().setAttribute("message", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {
            request.setAttribute("error", error);
            request.setAttribute("email", email); // Giữ lại email
            request.getRequestDispatcher("/views/linhhqth08598/auth/resetPS.jsp").forward(request, response);
        }
    }
}
