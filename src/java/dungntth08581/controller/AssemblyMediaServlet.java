package dungntth08581.controller;

import fourSome.url.DBConnect;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Phục vụ ảnh PC hoàn thiện (UC 267). Ảnh lưu trong DB dưới dạng base64 (data URI) ở assembly_photos.photo_url.
 * URL: /media/assembly/{photo_id}. Cần đăng nhập.
 */
@WebServlet(name = "AssemblyMediaServlet", urlPatterns = {"/media/assembly/*"})
public class AssemblyMediaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getSession().getAttribute("loggedUser") == null) { res.sendError(403); return; }
        String p = req.getPathInfo();
        if (p == null || !p.substring(1).matches("\\d{1,9}")) { res.sendError(404); return; }
        String data = null;
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT photo_url FROM assembly_photos WHERE photo_id = ? AND status = 'ACTIVE'")) {
            ps.setInt(1, Integer.parseInt(p.substring(1)));
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) data = rs.getString(1); }
        } catch (Exception e) { e.printStackTrace(); res.sendError(500); return; }
        int comma = data == null ? -1 : data.indexOf(',');
        if (comma < 0 || !data.startsWith("data:") || !data.substring(0, comma).endsWith(";base64")) { res.sendError(404); return; }
        String mime = data.substring(5, comma - 7);
        if (!mime.matches("image/(jpeg|png|webp|gif)")) { res.sendError(404); return; }
        byte[] bytes = Base64.getDecoder().decode(data.substring(comma + 1));
        res.setContentType(mime);
        res.setHeader("Cache-Control", "private, max-age=86400");
        res.setContentLength(bytes.length);
        res.getOutputStream().write(bytes);
    }
}
