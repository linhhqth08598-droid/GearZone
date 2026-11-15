package minhtbth08199.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * /shared-build?token=... -> chuyển sang trang xem chia sẻ công khai /build-share (UC 17, linhhqth08598.SharedBuildServlet)
 * để chỉ có MỘT nơi xử lý link chia sẻ.
 */
@WebServlet(name = "PublicBuildServlet", urlPatterns = {"/shared-build"})
public class PublicBuildServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        response.sendRedirect(request.getContextPath() + "/build-share"
                + (token == null ? "" : "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)));
    }
}
