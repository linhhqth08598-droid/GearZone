package dungntth08581.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Phục vụ ảnh PC hoàn thiện (UC 267). File lưu ngoài thư mục deploy: ~/gearzone-uploads/assembly/.
 * URL: /media/assembly/{tên-file}. Cần đăng nhập; tên file là UUID nên không đoán được.
 */
@WebServlet(name = "AssemblyMediaServlet", urlPatterns = {"/media/assembly/*"})
public class AssemblyMediaServlet extends HttpServlet {

    public static Path root() {
        return Paths.get(System.getProperty("user.home"), "gearzone-uploads");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getSession().getAttribute("loggedUser") == null) { res.sendError(403); return; }
        String name = req.getPathInfo();
        if (name == null || name.length() < 2 || !name.substring(1).matches("[A-Za-z0-9._-]+")) { res.sendError(404); return; }
        Path dir = root().resolve("assembly").normalize();
        Path f = dir.resolve(name.substring(1)).normalize();
        if (!f.startsWith(dir) || !Files.isRegularFile(f)) { res.sendError(404); return; }
        String n = f.getFileName().toString().toLowerCase();
        res.setContentType(n.endsWith(".png") ? "image/png" : n.endsWith(".webp") ? "image/webp" : n.endsWith(".gif") ? "image/gif" : "image/jpeg");
        res.setHeader("Cache-Control", "private, max-age=86400");
        res.setContentLengthLong(Files.size(f));
        Files.copy(f, res.getOutputStream());
    }
}
