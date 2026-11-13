package fourSome.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Sinh mã OTP ngẫu nhiên an toàn (SecureRandom).
 */
public class OTPGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    /** Sinh OTP số với độ dài mặc định (6 chữ số). */
    public static String generateOTP() {
        return generateOTP(Constants.OTP_LENGTH);
    }

    public static String generateOTP(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /** Thời điểm OTP hết hạn tính từ bây giờ. */
    public static LocalDateTime expireTime() {
        return LocalDateTime.now().plusMinutes(Constants.OTP_EXPIRE_MINUTES);
    }
}