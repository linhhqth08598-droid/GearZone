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
        request.getRequestDispatcher("/views/dungntth08581/checkout/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = requireLogin(request, response);
        if (user == null) return;
        HttpSession session = request.getSession();
        try {
            int cartId = Integer.parseInt(request.getParameter("cartId"));
            if ("remove".equals(request.getParameter("action"))) {
                service.remove(user.getUserId(), cartId);
                session.setAttribute("message", "Đã xóa khỏi giỏ hàng.");
            } else if ("update".equals(request.getParameter("action"))) {
                if (!service.updateQuantity(user.getUserId(), cartId, Integer.parseInt(request.getParameter("quantity")))) {
                    session.setAttribute("error", "Số lượng phải từ 1 đến 99!");
                }
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
