package Util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

/**
 * Lưu và xóa file upload (ảnh, video) cho Servlet dùng @MultipartConfig.
 * File được đổi tên ngẫu nhiên (UUID) để tránh trùng tên và path traversal.
 */
public class FileUploadUtil {

    /**
     * Lưu ảnh vào {uploadRoot}/{subFolder}/ và trả về đường dẫn tương đối
     * (ví dụ "parts/3f2a....jpg") để lưu vào DB. Trả về null nếu không có file.
     * Ném IOException nếu sai định dạng hoặc quá dung lượng.
     */
    public static String saveImage(Part part, String uploadRoot, String subFolder) throws IOException {
        return save(part, uploadRoot, subFolder, Constants.IMAGE_EXTENSIONS, Constants.MAX_IMAGE_SIZE);
    }

    public static String saveVideo(Part part, String uploadRoot, String subFolder) throws IOException {
        return save(part, uploadRoot, subFolder, Constants.VIDEO_EXTENSIONS, Constants.MAX_VIDEO_SIZE);
    }

    private static String save(Part part, String uploadRoot, String subFolder,
                               String[] allowedExt, long maxSize) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        if (part.getSize() > maxSize) {
            throw new IOException("File vượt quá dung lượng cho phép (" + (maxSize / 1024 / 1024) + "MB)");
        }

        String ext = getExtension(part.getSubmittedFileName());
        if (!Arrays.asList(allowedExt).contains(ext)) {
            throw new IOException("Định dạng file không được hỗ trợ: ." + ext);
        }

        File dir = new File(uploadRoot, subFolder);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Không tạo được thư mục upload: " + dir.getPath());
        }

        String fileName = UUID.randomUUID() + "." + ext;
        try (InputStream in = part.getInputStream()) {
            Files.copy(in, new File(dir, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return subFolder + "/" + fileName;
    }

    /** Xóa file theo đường dẫn tương đối đã lưu trong DB. */
    public static boolean delete(String uploadRoot, String relativePath) {
        if (relativePath == null || relativePath.isEmpty() || relativePath.contains("..")) {
            return false;
        }
        File f = new File(uploadRoot, relativePath);
        return f.exists() && f.delete();
    }

    private static String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}