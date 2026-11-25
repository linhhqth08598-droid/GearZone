package khanhnb08894.controller;

import fourSome.url.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Endpoint upload ảnh/video sản phẩm (part_media) cho Module A (UC 34-35).
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024 * 60)
@WebServlet(name = "UploadApiServlet", urlPatterns = {"/api/upload"})
public class UploadApiServlet extends HttpServlet {

    private static final List<String> IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final List<String> VIDEO_TYPES = Arrays.asList("video/mp4", "video/webm", "video/quicktime");
    private static final long MAX_IMAGE_BYTES = 10L * 1024 * 1024;
    private static final long MAX_VIDEO_BYTES = 50L * 1024 * 1024;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"status\":\"ok\",\"endpoint\":\"/api/upload\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try {
            String partIdRaw = request.getParameter("partId");
            if (partIdRaw == null || !partIdRaw.trim().matches("\\d+")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJson(response, false, "Thiếu hoặc sai partId.", null, null);
                return;
            }
            int partId = Integer.parseInt(partIdRaw.trim());
            boolean primary = "1".equals(request.getParameter("primary")) || "true".equalsIgnoreCase(request.getParameter("primary"));

            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                filePart = request.getPart("imageFile");
            }
            if (filePart == null || filePart.getSize() == 0) {
                filePart = request.getPart("productImage");
            }

            String mediaType = "IMAGE";
            String mediaUrl = null;

            if (filePart != null && filePart.getSize() > 0) {
                String ct = filePart.getContentType() == null ? "" : filePart.getContentType().toLowerCase();
                if (IMAGE_TYPES.contains(ct)) {
                    if (filePart.getSize() > MAX_IMAGE_BYTES) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(response, false, "Ảnh vượt quá giới hạn 10MB.", null, null);
                        return;
                    }
                    mediaType = "IMAGE";
                    byte[] bytes = filePart.getInputStream().readAllBytes();
                    mediaUrl = "data:" + ct + ";base64," + Base64.getEncoder().encodeToString(bytes);
                } else if (VIDEO_TYPES.contains(ct)) {
                    if (filePart.getSize() > MAX_VIDEO_BYTES) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(response, false, "Video vượt quá giới hạn 50MB.", null, null);
                        return;
                    }
                    mediaType = "VIDEO";
                    String videoUrlParam = request.getParameter("mediaUrl");
                    if (videoUrlParam != null && !videoUrlParam.trim().isEmpty()) {
                        mediaUrl = videoUrlParam.trim();
                    } else {
                        byte[] bytes = filePart.getInputStream().readAllBytes();
                        mediaUrl = "data:" + ct + ";base64," + Base64.getEncoder().encodeToString(bytes);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(response, false, "Định dạng file không hỗ trợ (chỉ nhận ảnh jpg/png/webp/gif hoặc video mp4/webm/mov).", null, null);
                    return;
                }
            } else {
                String urlParam = request.getParameter("mediaUrl");
                if (urlParam == null || urlParam.trim().isEmpty()) {
                    urlParam = request.getParameter("videoUrl");
                }
                if (urlParam == null || urlParam.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(response, false, "Vui lòng chọn file hoặc nhập đường dẫn media.", null, null);
                    return;
                }
                String requestedType = request.getParameter("mediaType");
                if ("VIDEO".equalsIgnoreCase(requestedType)) {
                    mediaType = "VIDEO";
                }
                mediaUrl = urlParam.trim();
            }

            int mediaId = insertMedia(partId, mediaType, mediaUrl, primary);
            if (mediaId > 0) {
                writeJson(response, true, "Lưu media thành công.", mediaId, mediaType);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writeJson(response, false, "Không thể lưu media vào cơ sở dữ liệu.", null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, false, "Lỗi upload media: " + e.getMessage(), null, null);
        }
    }

    private int insertMedia(int partId, String mediaType, String mediaUrl, boolean primary) throws Exception {
        try (Connection con = DBConnect.getConnection()) {
            if (con == null) {
                throw new IllegalStateException("Không thể kết nối CSDL.");
            }
            con.setAutoCommit(false);
            try {
                if (primary && "IMAGE".equals(mediaType)) {
                    try (PreparedStatement psReset = con.prepareStatement("UPDATE [part_media] SET is_primary = 0 WHERE part_id = ?")) {
                        psReset.setInt(1, partId);
                        psReset.executeUpdate();
                    }
                }
                String sql = "INSERT INTO [part_media] (part_id, media_type, media_url, is_primary, status) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVE')";
                try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, partId);
                    ps.setString(2, mediaType);
                    ps.setString(3, mediaUrl);
                    ps.setInt(4, (primary && "IMAGE".equals(mediaType)) ? 1 : 0);
                    ps.executeUpdate();
                    int createdId = 0;
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            createdId = rs.getInt(1);
                        }
                    }
                    con.commit();
                    return createdId;
                }
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    private void writeJson(HttpServletResponse response, boolean ok, String message, Integer mediaId, String mediaType) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"success\":" + ok
                    + ",\"message\":\"" + escape(message) + "\""
                    + (mediaId != null ? ",\"mediaId\":" + mediaId : "")
                    + (mediaType != null ? ",\"mediaType\":\"" + escape(mediaType) + "\"" : "")
                    + "}");
            out.flush();
        }
    }

    private String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}
