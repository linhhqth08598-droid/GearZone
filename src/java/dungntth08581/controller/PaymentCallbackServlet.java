package dungntth08581.controller;

import dungntth08581.service.PaymentService;
import fourSome.util.PayOSUtil;

import java.io.BufferedReader;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * /payment-callback (công khai, không qua UserAuthFilter):
 *  - POST  = webhook PayOS gọi về (kiểm tra chữ ký, ghi nhận thanh toán) - UC 230.
 *  - GET ?orderCode=..  = trình duyệt khách quay về từ PayOS (returnUrl/cancelUrl): hỏi lại PayOS rồi chuyển về trang đơn.
 *  - GET không tham số  = trả "OK" để kiểm tra đường dẫn (ngrok) từ trình duyệt.
 */
@WebServlet(name = "PaymentCallbackServlet", urlPatterns = {"/payment-callback"})
public class PaymentCallbackServlet extends HttpServlet {

    private final PaymentService service = new PaymentService();

    /** Địa chỉ gốc web để PayOS đưa khách quay về: ưu tiên cấu hình app.base-url, nếu không thì lấy từ request (hiểu cả ngrok/proxy). */
    public static String baseUrl(HttpServletRequest req) {
        String cfg = PayOSUtil.appBaseUrl();
        if (cfg != null) return cfg;
        String proto = req.getHeader("X-Forwarded-Proto"); if (proto == null || proto.isEmpty()) proto = req.getScheme();
        String host = req.getHeader("X-Forwarded-Host");
        if (host == null || host.isEmpty()) {
            int port = req.getServerPort();
            host = req.getServerName() + (port == 80 || port == 443 ? "" : ":" + port);
        }
        return proto + "://" + host + req.getContextPath();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String oc = request.getParameter("orderCode");
        if (oc == null || oc.isEmpty()) {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("GearZone PayOS callback OK");
            return;
        }
        HttpSession session = request.getSession();
        PaymentService.SyncResult s;
        try { s = service.syncByOrderCode(Long.parseLong(oc.trim())); }
        catch (NumberFormatException e) { s = new PaymentService.SyncResult(); s.error = true; s.message = "Mã giao dịch không hợp lệ!"; }
        session.setAttribute(s.error ? "error" : "message", s.message);
        response.sendRedirect(request.getContextPath() + (s.orderId != null ? "/order?id=" + s.orderId : "/user/order-history"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = request.getReader()) { String line; while ((line = br.readLine()) != null) sb.append(line); }
        int status = service.handleWebhook(sb.toString());
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(status == 200 ? "{\"success\":true}" : "{\"success\":false}");
    }
}