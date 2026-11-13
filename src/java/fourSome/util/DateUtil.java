package util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Tiện ích xử lý ngày giờ.
 */
public class DateUtil {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatDate(LocalDateTime dt) {
        return dt == null ? "" : dt.format(DATE);
    }

    public static String formatDateTime(LocalDateTime dt) {
        return dt == null ? "" : dt.format(DATE_TIME);
    }

    public static String formatDateTime(Timestamp ts) {
        return ts == null ? "" : formatDateTime(ts.toLocalDateTime());
    }

    /** Chuyển chuỗi dd/MM/yyyy thành LocalDate, sai định dạng trả về null. */
    public static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s.trim(), DATE);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    /** Chuyển chuỗi yyyy-MM-dd (input type="date") thành LocalDate. */
    public static LocalDate parseIsoDate(String s) {
        try {
            return LocalDate.parse(s.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    public static Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    public static boolean isExpired(LocalDateTime expireAt) {
        return expireAt == null || LocalDateTime.now().isAfter(expireAt);
    }

    /** Ngày hết hạn bảo hành = ngày bắt đầu + số tháng. */
    public static LocalDate addMonths(LocalDate start, int months) {
        return start.plusMonths(months);
    }
}