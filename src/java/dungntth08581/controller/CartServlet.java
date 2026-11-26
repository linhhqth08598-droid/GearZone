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
                int qty = (q == null || q.isEmpty()) ? 1 : Integer.parseInt(q);
                boolean isBuyNow = "1".equals(request.getParameter("buyNow"));
                if (isBuyNow) {
                    String err = service.prepareBuyNow(user.getUserId(), partId, qty);
                    if (err == null) {
                        session.setAttribute("buyNowPartId", partId);
                        session.setAttribute("buyNowQty", qty);
                        response.sendRedirect(request.getContextPath() + "/checkout?buyNow=1");
                    } else {
                        session.setAttribute("error", err);
                        String back = request.getHeader("Referer");
                        String target = request.getContextPath() + "/product";
                        if (back != null) {
                            int i = back.indexOf(request.getContextPath() + "/");
                            if (i >= 0 && back.startsWith("http") && !back.contains("/cart")) target = back.substring(i);
                        }
                        response.sendRedirect(target);
                    }
                    return;
                }
                String err = service.addPart(user.getUserId(), partId, qty);
                if (err == null) session.setAttribute("message", "Đã thêm vào giỏ hàng.");
                else session.setAttribute("error", err);
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Dữ liệu không hợp lệ!");
            }
            // Ở lại trang đang xem (danh sách / chi tiết / trang chủ) khi thêm vào giỏ
            String ctx = request.getContextPath();
            String back = request.getHeader("Referer");
            String target = ctx + "/product";
            if (back != null) {
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
                int reqQty = Integer.parseInt(request.getParameter("quantity"));
                boolean ok = service.updateQuantity(user.getUserId(), cartId, reqQty);
                if ("1".equals(request.getParameter("ajax"))) {
                    // Cập nhật không tải lại trang: trả JSON để trang cập nhật thành tiền/tổng ngay
                    double total = 0, lineTotal = 0; boolean shortage = false; int available = 0; boolean removed = false;
                    java.util.List<CartService.CartLine> currentLines = service.getCart(user.getUserId());
                    for (CartService.CartLine l : currentLines) {
                        total += l.getLineTotal();
                        if (l.getCartId() == cartId) { lineTotal = l.getLineTotal(); shortage = l.isShortage(); available = l.getAvailable(); }
                    }
                    if (ok && reqQty == 0) {
                        removed = true;
                    }
                    session.setAttribute("cartCount", currentLines.size());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":" + ok + ",\"removed\":" + removed
                            + ",\"cartCount\":" + currentLines.size() + ",\"lineTotal\":" + (long) lineTotal
                            + ",\"total\":" + (long) total + ",\"shortage\":" + shortage + ",\"available\":" + available + "}");
                    return;
                }
                if (!ok && reqQty < 0) {
                    session.setAttribute("error", "Số lượng không được nhỏ hơn 0!");
                }
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
