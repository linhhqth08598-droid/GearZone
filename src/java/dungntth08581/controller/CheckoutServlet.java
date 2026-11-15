package dungntth08581.controller;

import dungntth08581.service.CartService;
import dungntth08581.service.OrderService;
import fourSome.model.ShippingAddresses;
import fourSome.model.Users;
import linhhqth08598.service.UserService;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Đặt hàng (bản gọn, thanh toán khi nhận hàng): /checkout. Địa chỉ lấy từ ShippingAddresses của Linh (UC 223). */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    private final CartService cartService = new CartService();
    private final OrderService orderService = new OrderService();
    private final UserService userService = new UserService();

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
        List<CartService.CartLine> lines = cartService.getCart(user.getUserId());
        if (lines.isEmpty()) {
            request.getSession().setAttribute("error", "Giỏ hàng đang trống!");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }
        double total = 0;
        for (CartService.CartLine l : lines) total += l.getLineTotal();
        request.setAttribute("cartLines", lines);
        request.setAttribute("cartTotal", total);
        request.setAttribute("addresses", userService.getUserAddresses(user.getUserId()));
        request.setAttribute("shippingMethods", orderService.getShippingMethods());
        request.getRequestDispatcher("/views/dungntth08581/checkout/payment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users user = requireLogin(request, response);
        if (user == null) return;
        HttpSession session = request.getSession();

        ShippingAddresses chosen = null;
        try {
            int addrId = Integer.parseInt(request.getParameter("addressId"));
            for (ShippingAddresses a : userService.getUserAddresses(user.getUserId())) {
                if (a.getAddressId() != null && a.getAddressId() == addrId) { chosen = a; break; }   // chỉ nhận địa chỉ của chính user
            }
        } catch (Exception ignored) { }
        int methodId = 0;
        try { methodId = Integer.parseInt(request.getParameter("shippingMethodId")); } catch (Exception ignored) { }

        OrderService.PlaceResult r = orderService.placeOrder(user.getUserId(), chosen, methodId, request.getParameter("note"));
        if (r.error != null) {
            session.setAttribute("error", r.error);
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }
        session.setAttribute("cartCount", 0);
        session.setAttribute("message", "Đặt hàng thành công! Mã đơn: " + r.orderCode);
        response.sendRedirect(request.getContextPath() + "/order?id=" + r.orderId);
    }
}
