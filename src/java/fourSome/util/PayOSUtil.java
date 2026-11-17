package fourSome.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Gọi API PayOS (v2) + kiểm tra chữ ký webhook.
 *
 * KHÓA KHÔNG ĐƯỢC GHI TRONG CODE. Lấy theo thứ tự ưu tiên:
 *   1. System property (-Dpayos.client-id=...)
 *   2. Biến môi trường (PAYOS_CLIENT_ID, PAYOS_API_KEY, PAYOS_CHECKSUM_KEY, APP_BASE_URL)
 *   3. File payos.properties trên classpath (đặt ở src/java/payos.properties, đã .gitignore)
 */
public class PayOSUtil {

    private static final String API = "https://api-merchant.payos.vn/v2/payment-requests";
    private static final Properties FILE = loadFile();

    private static Properties loadFile() {
        Properties p = new Properties();
        try (InputStream in = PayOSUtil.class.getClassLoader().getResourceAsStream("payos.properties")) {
            if (in != null) p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException ignored) { }
        return p;
    }

    private static String cfg(String key, String env) {
        String v = System.getProperty(key);
        if (v == null || v.trim().isEmpty()) v = System.getenv(env);
        if (v == null || v.trim().isEmpty()) v = FILE.getProperty(key);
        return v == null ? "" : v.trim();
    }

    public static String clientId() { return cfg("payos.client-id", "PAYOS_CLIENT_ID"); }
    public static String apiKey() { return cfg("payos.api-key", "PAYOS_API_KEY"); }
    public static String checksumKey() { return cfg("payos.checksum-key", "PAYOS_CHECKSUM_KEY"); }

    /** Địa chỉ gốc của web (vd https://xxx.ngrok-free.dev/gearzone), không có "/" cuối; null nếu không cấu hình. */
    public static String appBaseUrl() {
        String v = cfg("app.base-url", "APP_BASE_URL");
        while (v.endsWith("/")) v = v.substring(0, v.length() - 1);
        return v.isEmpty() ? null : v;
    }

    public static boolean isConfigured() {
        return !clientId().isEmpty() && !apiKey().isEmpty() && !checksumKey().isEmpty();
    }

    // =====================================================================
    // KẾT QUẢ
    // =====================================================================
    public static class LinkResult {
        public boolean ok; public String checkoutUrl, paymentLinkId, error;
    }

    public static class PaymentInfo {
        public boolean ok; public String status, reference, error; public long amount, amountPaid;
    }

    public static class Webhook {
        public boolean valid, success; public Map<String, String> data; public String error;
    }

    // =====================================================================
    // GỌI API
    // =====================================================================

    /** Tạo link thanh toán. orderCode phải là số nguyên dương, KHÔNG được trùng giữa các lần tạo link. */
    public static LinkResult createPaymentLink(long orderCode, int amount, String description, String returnUrl, String cancelUrl, int expireSeconds) {
        LinkResult r = new LinkResult();
        try {
            // chữ ký: các trường sắp theo alphabet, giá trị để nguyên (không mã hóa URL)
            String signData = "amount=" + amount + "&cancelUrl=" + cancelUrl + "&description=" + description + "&orderCode=" + orderCode + "&returnUrl=" + returnUrl;
            String signature = hmacSha256(checksumKey(), signData);
            long expiredAt = System.currentTimeMillis() / 1000L + Math.max(expireSeconds, 60);
            String body = "{\"orderCode\":" + orderCode + ",\"amount\":" + amount
                    + ",\"description\":\"" + esc(description) + "\",\"returnUrl\":\"" + esc(returnUrl) + "\",\"cancelUrl\":\"" + esc(cancelUrl)
                    + "\",\"expiredAt\":" + expiredAt + ",\"signature\":\"" + signature + "\"}";
            String res = call("POST", API, body);
            if (res.isEmpty()) { r.error = "Không kết nối được tới PayOS"; return r; }
            if ("00".equals(field(res, "code"))) {
                r.checkoutUrl = field(res, "checkoutUrl"); r.paymentLinkId = field(res, "paymentLinkId");
                r.ok = r.checkoutUrl != null;
                if (!r.ok) r.error = "PayOS không trả về đường dẫn thanh toán";
            } else {
                String d = field(res, "desc");
                r.error = (d == null ? "PayOS từ chối yêu cầu" : d) + " (mã " + field(res, "code") + ")";
            }
        } catch (Exception e) {
            e.printStackTrace();
            r.error = "Không kết nối được tới PayOS";
        }
        return r;
    }

    /** Hỏi PayOS trạng thái thật của 1 link (server-to-server bằng khóa của mình, không tin tham số trên URL). */
    public static PaymentInfo getPaymentInfo(long orderCode) {
        PaymentInfo p = new PaymentInfo();
        try {
            String res = call("GET", API + "/" + orderCode, null);
            if ("00".equals(field(res, "code"))) {
                p.ok = true; p.status = field(res, "status"); p.reference = field(res, "reference");
                p.amount = num(field(res, "amount")); p.amountPaid = num(field(res, "amountPaid"));
            } else {
                String d = field(res, "desc"); p.error = d == null ? "Không lấy được trạng thái từ PayOS" : d;
            }
        } catch (Exception e) { e.printStackTrace(); p.error = "Không kết nối được tới PayOS"; }
        return p;
    }

    /** Hủy link chưa thanh toán (best effort). */
    public static boolean cancelPaymentLink(long orderCode, String reason) {
        try {
            String res = call("POST", API + "/" + orderCode + "/cancel", "{\"cancellationReason\":\"" + esc(reason == null ? "Huy don" : reason) + "\"}");
            return "00".equals(field(res, "code"));
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private static String call(String method, String url, String body) throws IOException {
        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        c.setRequestMethod(method);
        c.setConnectTimeout(10000);
        c.setReadTimeout(15000);
        c.setRequestProperty("Content-Type", "application/json");
        c.setRequestProperty("x-client-id", clientId());
        c.setRequestProperty("x-api-key", apiKey());
        if (body != null) {
            c.setDoOutput(true);
            try (OutputStream os = c.getOutputStream()) { os.write(body.getBytes(StandardCharsets.UTF_8)); }
        }
        int code = c.getResponseCode();
        InputStream in = code >= 400 ? c.getErrorStream() : c.getInputStream();
        return in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }

    // =====================================================================
    // WEBHOOK
    // =====================================================================

    /**
     * Kiểm tra webhook PayOS gửi về: signature = HMAC-SHA256(checksumKey, "k1=v1&k2=v2..." của object "data", key sắp alphabet).
     * valid = false nếu body hỏng hoặc chữ ký sai.
     */
    public static Webhook parseWebhook(String body) {
        Webhook w = new Webhook();
        try {
            Object root = new Json(body).value();
            if (!(root instanceof Map)) { w.error = "Body không phải JSON object"; return w; }
            @SuppressWarnings("unchecked") Map<String, Object> m = (Map<String, Object>) root;
            Object data = m.get("data"); Object sig = m.get("signature");
            if (!(data instanceof Map) || !(sig instanceof String)) { w.error = "Thiếu data/signature"; return w; }
            @SuppressWarnings("unchecked") Map<String, Object> d = (Map<String, Object>) data;
            TreeMap<String, Object> sorted = new TreeMap<>(d);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> e : sorted.entrySet()) {
                if (sb.length() > 0) sb.append('&');
                sb.append(e.getKey()).append('=').append(signValue(e.getValue()));
            }
            String expected = hmacSha256(checksumKey(), sb.toString());
            if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), ((String) sig).trim().toLowerCase().getBytes(StandardCharsets.UTF_8))) {
                w.error = "Chữ ký không hợp lệ"; return w;
            }
            w.valid = true;
            w.data = new LinkedHashMap<>();
            for (Map.Entry<String, Object> e : d.entrySet()) w.data.put(e.getKey(), e.getValue() == null ? null : signValue(e.getValue()));
            w.success = "00".equals(m.get("code") == null ? null : signValue(m.get("code"))) && "00".equals(w.data.get("code"));
        } catch (Exception e) {
            w.error = "Body không đọc được: " + e.getMessage();
        }
        return w;
    }

    // =====================================================================
    // TIỆN ÍCH
    // =====================================================================

    static String hmacSha256(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) { String h = Integer.toHexString(0xff & b); if (h.length() == 1) hex.append('0'); hex.append(h); }
            return hex.toString();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    private static long num(String s) {
        try { return s == null ? 0 : Math.round(Double.parseDouble(s)); } catch (NumberFormatException e) { return 0; }
    }

    /** Giá trị đưa vào chuỗi ký: null -> rỗng, số/chuỗi -> nguyên văn, object/mảng -> JSON. */
    private static String signValue(Object o) {
        if (o == null) return "";
        if (o instanceof String) return ((String) o).equals("null") || ((String) o).equals("undefined") ? "" : (String) o;
        if (o instanceof Raw) return ((Raw) o).text;
        return toJson(o);
    }

    @SuppressWarnings("unchecked")
    private static String toJson(Object o) {
        if (o == null) return "null";
        if (o instanceof String) return "\"" + esc((String) o) + "\"";
        if (o instanceof Raw) return ((Raw) o).text;
        StringBuilder sb = new StringBuilder();
        if (o instanceof Map) {
            sb.append('{'); boolean first = true;
            for (Map.Entry<String, Object> e : new TreeMap<>((Map<String, Object>) o).entrySet()) {
                if (!first) sb.append(',');
                first = false;
                sb.append('"').append(esc(e.getKey())).append("\":").append(toJson(e.getValue()));
            }
            return sb.append('}').toString();
        }
        sb.append('['); boolean first = true;
        for (Object x : (List<Object>) o) {
            if (!first) sb.append(',');
            first = false;
            sb.append(toJson(x));
        }
        return sb.append(']').toString();
    }

    /** Lấy giá trị đầu tiên của "key" trong chuỗi JSON (chuỗi đã bỏ escape, số/true/false giữ nguyên; null -> null). */
    static String field(String json, String key) {
        if (json == null) return null;
        Matcher m = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\"(?:\\\\.|[^\"\\\\])*\"|-?\\d+(?:\\.\\d+)?|true|false|null)").matcher(json);
        if (!m.find()) return null;
        String v = m.group(1);
        if (v.equals("null")) return null;
        if (v.startsWith("\"")) return unescape(v.substring(1, v.length() - 1));
        return v;
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\' || i + 1 >= s.length()) { sb.append(c); continue; }
            char e = s.charAt(++i);
            switch (e) {
                case 'n': sb.append('\n'); break;
                case 't': sb.append('\t'); break;
                case 'r': sb.append('\r'); break;
                case 'u':
                    if (i + 4 < s.length()) { sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16)); i += 4; }
                    break;
                default: sb.append(e);
            }
        }
        return sb.toString();
    }

    // ---------- JSON reader tối giản (đủ cho webhook của PayOS) ----------
    private static final class Raw { final String text; Raw(String t) { text = t; } }

    private static final class Json {
        final String s; int i = 0;
        Json(String s) { this.s = s; }

        Object value() {
            ws();
            char c = s.charAt(i);
            if (c == '{') return obj();
            if (c == '[') return arr();
            if (c == '"') return str();
            int st = i;
            while (i < s.length() && ",}] \t\r\n".indexOf(s.charAt(i)) < 0) i++;
            String t = s.substring(st, i);
            return t.equals("null") ? null : new Raw(t);
        }

        Map<String, Object> obj() {
            Map<String, Object> m = new LinkedHashMap<>();
            i++; ws();
            if (s.charAt(i) == '}') { i++; return m; }
            while (true) {
                ws(); String k = str(); ws(); expect(':'); m.put(k, value()); ws();
                char c = s.charAt(i++);
                if (c == '}') return m;
                if (c != ',') throw new IllegalArgumentException("JSON sai tại " + i);
            }
        }

        List<Object> arr() {
            List<Object> l = new ArrayList<>();
            i++; ws();
            if (s.charAt(i) == ']') { i++; return l; }
            while (true) {
                l.add(value()); ws();
                char c = s.charAt(i++);
                if (c == ']') return l;
                if (c != ',') throw new IllegalArgumentException("JSON sai tại " + i);
            }
        }

        String str() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = s.charAt(i++);
                if (c == '"') break;
                if (c != '\\') { sb.append(c); continue; }
                char e = s.charAt(i++);
                switch (e) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'u': sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16)); i += 4; break;
                    default: sb.append(e);
                }
            }
            return sb.toString();
        }

        void ws() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }
        void expect(char c) { if (s.charAt(i) != c) throw new IllegalArgumentException("Thiếu '" + c + "' tại " + i); i++; }
    }
}