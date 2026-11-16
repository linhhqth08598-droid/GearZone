package khanhnb08894.controller;

import java.io.IOException;
import java.nio.file.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/** Phục vụ ảnh sản phẩm đã tải lên (lưu ngoài thư mục deploy để không mất khi build lại). URL: /media/part/{tên-file} */
@WebServlet(name = "MediaServlet", urlPatterns = {"/media/part/*"})
public class MediaServlet extends HttpServlet {

    public static Path uploadDir() {
        return Paths.get(System.getProperty("user.home"), "gearzone-uploads", "parts");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String name = req.getPathInfo();
        if (name == null || name.length() < 2 || !name.substring(1).matches("[A-Za-z0-9._-]+")) { res.sendError(404); return; }
        Path f = uploadDir().resolve(name.substring(1)).normalize();
        if (!f.startsWith(uploadDir()) || !Files.isRegularFile(f)) { res.sendError(404); return; }
        String n = f.getFileName().toString().toLowerCase();
        res.setContentType(n.endsWith(".png") ? "image/png" : n.endsWith(".webp") ? "image/webp" : n.endsWith(".gif") ? "image/gif" : "image/jpeg");
        res.setHeader("Cache-Control", "public, max-age=86400");
        res.setContentLengthLong(Files.size(f));
        Files.copy(f, res.getOutputStream());
    }
}
