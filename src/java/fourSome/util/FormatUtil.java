package fourSome.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Định dạng hiển thị: tiền VND, số, slug URL.
 */
public class FormatUtil {

    private static DecimalFormat vndFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        return new DecimalFormat("#,###", symbols);
    }

    /** 15990000 -> "15.990.000 ₫" */
    public static String formatVND(long amount) {
        return vndFormat().format(amount) + " ₫";
    }

    public static String formatVND(BigDecimal amount) {
        return amount == null ? "0 ₫" : formatVND(amount.longValue());
    }

    public static String formatVND(double amount) {
        return formatVND(Math.round(amount));
    }

    /** "Card đồ họa RTX 4060" -> "card-do-hoa-rtx-4060" */
    public static String toSlug(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace('đ', 'd').replace('Đ', 'D');
        return n.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    /** Rút gọn chuỗi dài: "abcdef..." */
    public static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    /** Che email: abcdef@gmail.com -> ab****@gmail.com */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "";
        int at = email.indexOf('@');
        String name = email.substring(0, at);
        return name.substring(0, Math.min(2, name.length())) + "****" + email.substring(at);
    }
}