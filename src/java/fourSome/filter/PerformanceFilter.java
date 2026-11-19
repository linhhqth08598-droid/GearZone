package fourSome.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*") // Tự động đo mọi request gửi lên web
public class PerformanceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();

        // Bỏ qua các file tĩnh (CSS, JS, ảnh) và API đồng bộ ngầm để log gọn gàng
        if (uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") 
                || uri.endsWith(".jpg") || uri.contains("/api/sync-state")) {
            chain.doFilter(request, response);
            return;
        }

        // Bắt đầu bấm giờ (tính bằng nano giây để đo chính xác tới 0.01ms)
        long startTime = System.nanoTime();

        // Cho phép Servlet gọi DAO -> SQL Server -> render JSP lên web
        chain.doFilter(request, response);

        // Kết thúc bấm giờ
        double elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0;
        String query = req.getQueryString() != null ? "?" + req.getQueryString() : "";

        System.out.printf("[PERF] %s %s%s -> Xử lý DB & Render Web mất: %.2f ms%n",
                req.getMethod(), uri, query, elapsedMs);
    }
}
