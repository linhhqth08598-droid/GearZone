package dungntth08581.controller;

import dungntth08581.service.CartService;
import fourSome.model.Users;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Giỏ hàng (bản gọn): xem, đổi số lượng, xóa dòng. Thanh toán (checkout) do Dũng làm tiếp. */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private final CartService service = new CartService();

    private Users requireLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", req.getRequestURI());
            res.sendRedirect(req.getContextPath() + "/auth?action=login");
        }
        return user;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = requireLogin(request, response);
        if (user == null) return;
        java.util.List<CartService.CartLine> lines = service.getCart(user.getUserId());
        double total = 0;
        for (CartService.CartLine l : lines) total += l.getLineTotal();
        request.getSession().setAttribute("cartCount", lines.size());
        request.setAttribute("cartLines", lines);
        request.setAttribute("cartTotal", total);
        request.setAttribute("compat", service.checkCompatibility(user.getUserId()));   // UC 219
        request.getRequestDispatcher("/views/dungntth08581/checkout/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = requireLogin(request, response);
        if (user == null) return;
        HttpSession session = request.getSession();
        if ("add".equals(request.getParameter("action"))) {
            try {
                int partId = Integer.parseInt(request.getParameter("partId"));
                String q = request.getParameter("quantity");
                String err = service.addPart(user.getUserId(), partId, q == null || q.isEmpty() ? 1 : Integer.parseInt(q));
                if (err == null) session.setAttribute("message", "Đã thêm vào giỏ hàng.");
                else session.setAttribute("error", err);
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Dữ liệu không hợp lệ!");
            }
            // Ở lại trang đang xem (danh sách / chi tiết / trang chủ); chỉ chuyển sang /cart khi bấm "Mua ngay"
            String ctx = request.getContextPath();
            String back = request.getHeader("Referer");
            String target = ctx + "/product";
            if ("1".equals(request.getParameter("buyNow"))) target = ctx + "/cart";
            else if (back != null) {
                int i = back.indexOf(ctx + "/");
                if (i >= 0 && back.startsWith("http") && !back.contains("/cart")) target = back.substring(i);
            }
            response.sendRedirect(target);
            return;
        }
        try {
            int cartId = Integer.parseInt(request.getParameter("cartId"));
            if ("remove".equals(request.getParameter("action"))) {
                service.remove(user.getUserId(), cartId);
                session.setAttribute("message", "Đã xóa khỏi giỏ hàng.");
            } else if ("update".equals(request.getParameter("action"))) {
                boolean ok = service.updateQuantity(user.getUserId(), cartId, Integer.parseInt(request.getParameter("quantity")));
                if ("1".equals(request.getParameter("ajax"))) {
                    // Cập nhật không tải lại trang: trả JSON để trang cập nhật thành tiền/tổng ngay
                    double total = 0, lineTotal = 0; boolean shortage = false; int available = 0;
                    for (CartService.CartLine l : service.getCart(user.getUserId())) {
                        total += l.getLineTotal();
                        if (l.getCartId() == cartId) { lineTotal = l.getLineTotal(); shortage = l.isShortage(); available = l.getAvailable(); }
                    }
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":" + ok + ",\"lineTotal\":" + (long) lineTotal
                            + ",\"total\":" + (long) total + ",\"shortage\":" + shortage + ",\"available\":" + available + "}");
                    return;
                }
                if (!ok) session.setAttribute("error", "Số lượng phải từ 1 đến 99!");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
