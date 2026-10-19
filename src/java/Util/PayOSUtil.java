package Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class PayOSUtil {
    // Thông tin API của Linh
    private static final String CLIENT_ID = "c62043fa-2c04-4ad4-80ca-172b00768eeb";
    private static final String API_KEY = "c05cd44c-ec30-470b-b762-36dd84f05b59";
    private static final String CHECKSUM_KEY = "e838f1fb5621be4a17c4893676ec0225bbfc6defa1d984a98e1d346991e4c0bf";

    public static String createPaymentLink(int orderCode, int amount, String description, String returnUrl, String cancelUrl) {
        try {
            // 1. Tạo chuỗi ký tự để mã hóa bảo mật (bắt buộc sắp xếp theo alphabet)
            String signatureData = "amount=" + amount + "&cancelUrl=" + cancelUrl + "&description=" + description + "&orderCode=" + orderCode + "&returnUrl=" + returnUrl;
            
            // 2. Mã hóa HMAC-SHA256
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(CHECKSUM_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(signatureData.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String signature = hexString.toString();

            // 3. Chuẩn bị dữ liệu JSON gửi lên PayOS
            String jsonInputString = "{"
                    + "\"orderCode\":" + orderCode + ","
                    + "\"amount\":" + amount + ","
                    + "\"description\":\"" + description + "\","
                    + "\"returnUrl\":\"" + returnUrl + "\","
                    + "\"cancelUrl\":\"" + cancelUrl + "\","
                    + "\"signature\":\"" + signature + "\""
                    + "}";

            // 4. Gọi API
            URL url = new URL("https://api-merchant.payos.vn/v2/payment-requests");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-client-id", CLIENT_ID);
            conn.setRequestProperty("x-api-key", API_KEY);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 5. Đọc kết quả trả về để lấy đường link cổng thanh toán
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            
            String res = response.toString();
            if (res.contains("\"checkoutUrl\":\"")) {
                int start = res.indexOf("\"checkoutUrl\":\"") + 15;
                int end = res.indexOf("\"", start);
                return res.substring(start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu có lỗi
    }
}