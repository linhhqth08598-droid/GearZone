package util;

/**
 * Các hằng số dùng chung toàn hệ thống GearZone.
 */
public final class Constants {

    private Constants() {}

    // ==== Tên thuộc tính lưu trong session ====
    public static final String SESSION_USER = "loggedUser";
    public static final String SESSION_ROLES = "userRoles";
    public static final String SESSION_CART_COUNT = "cartCount";

    // ==== OTP ====
    public static final int OTP_LENGTH = 6;
    public static final int OTP_EXPIRE_MINUTES = 5;

    // ==== Phân trang ====
    public static final int DEFAULT_PAGE_SIZE = 12;
    public static final int ADMIN_PAGE_SIZE = 10;

    // ==== Upload file ====
    public static final String UPLOAD_DIR = "uploads";
    public static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;   // 10MB
    public static final long MAX_VIDEO_SIZE = 50L * 1024 * 1024;   // 50MB
    public static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "webp", "gif"};
    public static final String[] VIDEO_EXTENSIONS = {"mp4", "webm", "mov"};

    // ==== Mật khẩu ====
    public static final int MIN_PASSWORD_LENGTH = 8;
}