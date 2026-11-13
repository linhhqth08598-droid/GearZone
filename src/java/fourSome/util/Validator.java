package util;

import java.util.regex.Pattern;

/**
 * Các hàm kiểm tra dữ liệu đầu vào cơ bản.
 */
public class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    // SĐT Việt Nam: 0xxxxxxxxx hoặc +84xxxxxxxxx
    private static final Pattern PHONE_VN = Pattern.compile("^(0|\\+84)(3|5|7|8|9)\\d{8}$");
    // Mật khẩu: tối thiểu 8 ký tự, có chữ hoa, chữ thường và số
    private static final Pattern STRONG_PASSWORD =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{" + Constants.MIN_PASSWORD_LENGTH + ",}$");

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static boolean isEmail(String s) {
        return s != null && EMAIL.matcher(s.trim()).matches();
    }

    public static boolean isPhoneVN(String s) {
        return s != null && PHONE_VN.matcher(s.trim()).matches();
    }

    public static boolean isStrongPassword(String s) {
        return s != null && STRONG_PASSWORD.matcher(s).matches();
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositiveNumber(String s) {
        try {
            return Double.parseDouble(s.trim()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean lengthBetween(String s, int min, int max) {
        return s != null && s.trim().length() >= min && s.trim().length() <= max;
    }

    /** Chống XSS cơ bản khi in dữ liệu người dùng nhập ra HTML. */
    public static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}