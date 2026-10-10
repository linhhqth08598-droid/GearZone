package Controller;

import Models.User;
import Service.AuthService;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Đã thêm /verify-otp và /reset-password vào urlPatterns
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout", "/fgpassword", "/verify-otp", "/reset-password"})
public class AuthServlet extends HttpServlet {

    private AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();

        if (action.equals("/login")) {
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        } else if (action.equals("/register")) {
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        } else if (action.equals("/logout")) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login");
        } else if (action.equals("/fgpassword")) {
            request.getRequestDispatcher("/views/fgpassword.jsp").forward(request, response);
        } else if (action.equals("/verify-otp")) {
            request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
        } else if (action.equals("/reset-password")) {
            request.getRequestDispatcher("/views/reset-password.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();

        // Xử lý Đăng nhập
        if (action.equals("/login")) {
            String user = request.getParameter("username");
            String pass = request.getParameter("password");
            User loggedInUser = authService.login(user, pass);

            if (loggedInUser != null) {
                HttpSession session = request.getSession();
                session.setAttribute("userAcc", loggedInUser);
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                request.setAttribute("error", "Sai tài khoản hoặc mật khẩu!");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            }
        } 
        // Xử lý Đăng ký
        else if (action.equals("/register")) {
            String user = request.getParameter("username");
            String pass = request.getParameter("password");
            String confirm = request.getParameter("confirmPassword");
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");

            String result = authService.register(user, pass, confirm, email, fullName, phone);

            if (result.equals("SUCCESS")) {
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                request.setAttribute("error", result);
                request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            }
        }
        // ================= CÁC LUỒNG QUÊN MẬT KHẨU ================= //
        // 1. Nhập email yêu cầu lấy mã OTP
        else if (action.equals("/fgpassword")) {
            String email = request.getParameter("email");
            String result = authService.processForgotPassword(email);

            if (result.equals("SUCCESS")) {
                request.getSession().setAttribute("resetEmail", email);
                response.sendRedirect(request.getContextPath() + "/verify-otp");
            } else {
                request.setAttribute("error", result);
                request.getRequestDispatcher("/views/fgpassword.jsp").forward(request, response);
            }
        } 
        // 2. Xác nhận mã OTP
        else if (action.equals("/verify-otp")) {
            String email = (String) request.getSession().getAttribute("resetEmail");
            String otpCode = request.getParameter("otpCode");
            
            String result = authService.verifyOTP(email, otpCode);
            if (result.equals("SUCCESS")) {
                request.getSession().setAttribute("canReset", true);
                response.sendRedirect(request.getContextPath() + "/reset-password");
            } else {
                request.setAttribute("error", result);
                request.getRequestDispatcher("/views/verify-otp.jsp").forward(request, response);
            }
        } 
        // 3. Đặt lại mật khẩu mới
        else if (action.equals("/reset-password")) {
            Boolean canReset = (Boolean) request.getSession().getAttribute("canReset");
            if (canReset == null || !canReset) {
                response.sendRedirect(request.getContextPath() + "/fgpassword");
                return;
            }

            // Ở file JSP bạn truyền biến "email", nhưng bảo mật nhất là lấy email từ Session đã lưu lúc trước
            String email = (String) request.getSession().getAttribute("resetEmail"); 
            String newPass = request.getParameter("newPassword");
            String confirm = request.getParameter("confirmNewPassword");

            String result = authService.resetPassword(email, newPass, confirm);
            if (result.equals("SUCCESS")) {
                // Đổi pass xong thì xóa các session tạm đi
                request.getSession().removeAttribute("resetEmail");
                request.getSession().removeAttribute("canReset");
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                request.setAttribute("error", result);
                request.getRequestDispatcher("/views/reset-password.jsp").forward(request, response);
            }
        }
    }
}