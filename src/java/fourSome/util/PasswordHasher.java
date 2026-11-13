package fourSome.util;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Mã hóa và kiểm tra mật khẩu bằng PBKDF2-HMAC-SHA256 (có sẵn trong JDK, không cần thư viện ngoài).
 * Chuỗi lưu DB có dạng: iterations$salt$hash (Base64), dài khoảng 90 ký tự.
 */
public class PasswordHasher {

    private static final int ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Mã hóa mật khẩu thô, mỗi lần gọi sinh salt khác nhau. */
    public static String hash(String plainPassword) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(plainPassword.toCharArray(), salt, ITERATIONS);
        return ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(hash);
    }

    /** So khớp mật khẩu người dùng nhập với chuỗi hash lưu trong DB. */
    public static boolean verify(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        try {
            String[] parts = storedHash.split("\\$");
            if (parts.length != 3) {
                return false;
            }
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = pbkdf2(plainPassword.toCharArray(), salt, iterations);

            // So sánh thời gian cố định để tránh timing attack
            int diff = expected.length ^ actual.length;
            for (int i = 0; i < expected.length && i < actual.length; i++) {
                diff |= expected[i] ^ actual[i];
            }
            return diff == 0;
        } catch (Exception e) {
            return false; // chuỗi hash sai định dạng
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Không thể mã hóa mật khẩu", e);
        }
    }
}