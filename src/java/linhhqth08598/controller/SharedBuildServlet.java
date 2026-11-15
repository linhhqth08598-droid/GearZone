package linhhqth08598.controller;

import fourSome.model.PcBuildHistoryItem;
import fourSome.model.Users;
import linhhqth08598.service.BuildShareService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * UC 17 - Xem cấu hình PC qua link chia sẻ (công khai, chỉ đọc): /build-share?token=...
 * Người đã đăng nhập có thể "Lưu về tài khoản của tôi".
 * (Dùng URL /build-share để không trùng với /shared-build của PublicBuildServlet bên minhtbth08199.)
 */
@WebServlet(name = "SharedBuildServlet", urlPatterns = {"/build-share"})
public class SharedBuildServlet extends HttpServlet {

    private final BuildShareService shareService = new BuildShareService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        PcBuildHistoryItem build = shareService.getByToken(token);

        if (build == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("notFound", true);
        } else {
            request.setAttribute("build", build);
            request.setAttribute("ownerName", shareService.getOwnerName(build.getBuildId()));
            request.setAttribute("token", token);
        }
        request.getRequestDispatcher("/views/linhhqth08598/user/shared_build.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        String token = request.getParameter("token");

        if (!BuildShareService.isValidToken(token)) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        String back = request.getContextPath() + "/build-share?token=" + token;

        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", back);
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        int newId = shareService.copyFromShared(token, user.getUserId());
        if (newId > 0) {
            session.setAttribute("message", "Đã lưu cấu hình vào tài khoản của bạn!");
            response.sendRedirect(request.getContextPath() + "/user/pc-build-history");
        } else {
            session.setAttribute("error", "Không lưu được cấu hình này!");
            response.sendRedirect(back);
        }
    }
}
