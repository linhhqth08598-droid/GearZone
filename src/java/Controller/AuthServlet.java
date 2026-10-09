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

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout", "/fgpassword"})
public class AuthServlet extends HttpServlet {

    // Gọi tầng Service để xử lý logic
    private AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();

        // Mở trang đăng nhập
        if (action.equals("/login")) {
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        } // Mở trang đăng ký
        else if (action.equals("/register")) {
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
        } // Mở trang quên mật khẩu
        else if (action.equals("/fgpassword")) {
            // 2. THÊM .forward(request, response); VÀO ĐÂY
            request.getRequestDispatcher("/views/fgpassword.jsp").forward(request, response);
        } // Xử lý đăng xuất
        else if (action.equals("/logout")) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set encoding để không bị lỗi font tiếng Việt
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();

        // Xử lý Đăng nhập
        if (action.equals("/login")) {
            String user = request.getParameter("username");
            String pass = request.getParameter("password");

            User loggedInUser = authService.login(user, pass);

            // Trong AuthServlet.java
            if (loggedInUser != null) {
                // Đăng nhập thành công, lưu thông tin vào Session
                HttpSession session = request.getSession();
                session.setAttribute("userAcc", loggedInUser);

                // MỌI TÀI KHOẢN (bao gồm cả Admin RoleID=1) ĐỀU CHUYỂN VỀ TRANG CHỦ
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                // Đăng nhập thất bại, trả về lại trang login kèm thông báo lỗi
                request.setAttribute("error", "Sai tài khoản hoặc mật khẩu!");
                request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            }
        } // Xử lý Đăng ký
        else if (action.equals("/register")) {
            String user = request.getParameter("username");
            String pass = request.getParameter("password");
            String confirm = request.getParameter("confirmPassword");
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");

            String result = authService.register(user, pass, confirm, email, fullName, phone);

            if (result.equals("SUCCESS")) {
                // Đăng ký thành công -> Chuyển hướng sang trang đăng nhập
                response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            } else {
                // Đăng ký thất bại -> Giữ nguyên trang đăng ký và báo lỗi
                request.setAttribute("error", result);
                request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            }
        }
    }
}
