package dungntth08581.controller;

import dungntth08581.service.PromotionService;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Trang khuyến mãi công khai (UC 249): khuyến mãi đang chạy + linh kiện đang giảm giá. Không cần đăng nhập. */
@WebServlet(name = "PromotionServlet", urlPatterns = {"/promotion"})
public class PromotionServlet extends HttpServlet {

    private final PromotionService service = new PromotionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("promotions", service.listPromotions(true));
        request.setAttribute("parts", service.discountedParts(60));
        request.getRequestDispatcher("/views/dungntth08581/promotion/list.jsp").forward(request, response);
    }
}
