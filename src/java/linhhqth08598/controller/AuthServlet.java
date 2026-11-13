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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "login";

        switch (action) {
            case "login":
                request.getRequestDispatcher("/views/linhhqth08598/auth/login.jsp").forward(request, response);
                break;
            case "register":
                request.getRequestDispatcher("/views/linhhqth08598/auth/register.jsp").forward(request, response);
                break;
            case "forget":
                request.getRequestDispatcher("/views/linhhqth08598/auth/forgetPW.jsp").forward(request, response);
                break;
            case "verify":
                String vEmail = request.getParameter("email");
                if (vEmail == null || vEmail.trim().isEmpty()) {
                    vEmail = (String) request.getSession().getAttribute("verify_email");
                }
                request.setAttribute("email", vEmail);

                Object vExpiry = request.getSession().getAttribute("otp_expiry");
                if (vExpiry != null) {
                    request.setAttribute("expiresAt", vExpiry);
                } else {
                    java.sql.Timestamp dbExpiry = authService.getOTPExpiry(vEmail, "EMAIL");
                    if (dbExpiry != null) {
                        request.setAttribute("expiresAt", dbExpiry.getTime());
                    } else {
                        request.setAttribute("expiresAt", 0); 
                    }
                }
                request.getRequestDispatcher("/views/linhhqth08598/auth/verifyOTP.jsp").forward(request, response);
                break;
            case "reset":
                String email = request.getParameter("email");
                if (email == null || email.trim().isEmpty()) {
                    email = (String) request.getSession().getAttribute("reset_email");
                }
                request.setAttribute("email", email);

                Object expiry = request.getSession().getAttribute("otp_expiry");
                if (expiry != null) {
                    request.setAttribute("expiresAt", expiry);
                } else {
                    java.sql.Timestamp dbExpiry = authService.getOTPExpiry(email, "RECOVERY");
                    if (dbExpiry != null) {
                        request.setAttribute("expiresAt", dbExpiry.getTime());
                    } else {
                        request.setAttribute("expiresAt", 0);
                    }
                }
                request.getRequestDispatcher("/views/linhhqth08598/auth/resetPW.jsp").forward(request, response);
                break;
            case "logout":
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath() + "/auth?action=login");
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/auth?action=login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "login";

        switch (action) {
            case "login": handleLogin(request, response); break;
            case "register": handleRegister(request, response); break;
            case "verify": handleVerify(request, response); break;
            case "forget": handleForgetPassword(request, response); break;
            case "reset": handleResetPassword(request, response); break;
            default: response.sendRedirect(request.getContextPath() + "/auth?action=login");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Users user = authService.login(email, password);

        if (user != null) {
            if (!user.getIsEmailVerified()) {
                authService.sendVerificationOTP(email);
                request.getSession().setAttribute("verify_email", email);
                request.getSession().setAttribute("message", "Tài khoản chưa xác thực. Vui lòng nhập mã OTP vừa được gửi!");
                
                java.sql.Timestamp expiry = authService.getOTPExpiry(email, "EMAIL");
                if (expiry != null) {
                    request.getSession().setAttribute("otp_expiry", expiry.getTime());
                }
                response.sendRedirect(request.getContextPath() + "/auth?action=verify");
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", user);
            
            // Lấy URL cũ từ UserAuthFilter (nếu có)
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null) {
                session.removeAttribute("redirectAfterLogin"); // Xóa đi sau khi dùng
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không chính xác, hoặc tài khoản bị khóa!");
            request.setAttribute("email", email);
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
            authService.sendVerificationOTP(email);
            request.getSession().setAttribute("verify_email", email);
            request.getSession().setAttribute("message", "Đăng ký thành công! Vui lòng kiểm tra email để nhận mã xác thực.");
            
            java.sql.Timestamp expiry = authService.getOTPExpiry(email, "EMAIL");
            if (expiry != null) {
                request.getSession().setAttribute("otp_expiry", expiry.getTime());
            }
            
            response.sendRedirect(request.getContextPath() + "/auth?action=verify");
        } else {
            request.setAttribute("error", error);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("fullName", fullName);
            request.getRequestDispatcher("/views/linhhqth08598/auth/register.jsp").forward(request, response);
        }
    }

    private void handleVerify(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            email = (String) request.getSession().getAttribute("verify_email");
        }
        String otp = request.getParameter("otp");

        if (otp == null || otp.trim().isEmpty() || otp.length() < 6) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ 6 số mã OTP!");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/linhhqth08598/auth/verifyOTP.jsp").forward(request, response);
            return;
        }

        String error = authService.verifyAccount(email, otp);
        if (error == null) {
            request.getSession().removeAttribute("verify_email");
            request.getSession().removeAttribute("otp_expiry");
            request.getSession().setAttribute("message", "Xác thực tài khoản thành công! Vui lòng đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {
            request.setAttribute("error", error);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/views/linhhqth08598/auth/verifyOTP.jsp").forward(request, response);
        }
    }

    private void handleForgetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        java.util.Map<String, Object> result = authService.forgotPassword(email);

        if (result.get("error") == null) {
            request.getSession().setAttribute("reset_email", email);
            request.getSession().setAttribute("otp_expiry", result.get("expiresAt")); 

            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("sessionStorage.removeItem('resendStartTime');");
            response.getWriter().println("window.location.href='" + request.getContextPath() + "/auth?action=reset';");
            response.getWriter().println("</script>");
        } else {
            request.setAttribute("error", result.get("error"));
            request.getRequestDispatcher("/views/linhhqth08598/auth/forgetPW.jsp").forward(request, response);
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            email = (String) request.getSession().getAttribute("reset_email");
        }

        String otp = request.getParameter("otp");
        String newPassword = request.getParameter("newPassword");

        if (otp == null || otp.trim().isEmpty() || otp.length() < 6) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ 6 số mã OTP!");
            request.setAttribute("email", email);

            Object expiry = request.getSession().getAttribute("otp_expiry");
            if (expiry != null) {
                request.setAttribute("expiresAt", expiry);
            } else {
                request.setAttribute("expiresAt", System.currentTimeMillis() + (5 * 60 * 1000));
            }
            request.getRequestDispatcher("/views/linhhqth08598/auth/resetPW.jsp").forward(request, response);
            return; 
        }

        String error = authService.resetPassword(email, otp, newPassword);

        if (error == null) {
            request.getSession().removeAttribute("reset_email");
            request.getSession().removeAttribute("otp_expiry");

            request.getSession().setAttribute("message", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
        } else {
            request.setAttribute("error", error);
            request.setAttribute("email", email);

            Object expiry = request.getSession().getAttribute("otp_expiry");
            if (expiry != null) {
                request.setAttribute("expiresAt", expiry);
            } else {
                java.sql.Timestamp dbExpiry = authService.getOTPExpiry(email, "RECOVERY");
                if (dbExpiry != null) {
                    request.setAttribute("expiresAt", dbExpiry.getTime());
                } else {
                    request.setAttribute("expiresAt", 0); 
                }
            }
            request.getRequestDispatcher("/views/linhhqth08598/auth/resetPW.jsp").forward(request, response);
        }
    }
}