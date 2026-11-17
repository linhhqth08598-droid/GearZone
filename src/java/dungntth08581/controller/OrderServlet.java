package dungntth08581.controller;

import dungntth08581.service.OrderService;
import dungntth08581.service.PaymentService;
import fourSome.model.Users;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Chi tiết đơn hàng của khách (/order?id=) + hủy đơn (UC 231, 232). */
@WebServlet(name = "OrderServlet", urlPatterns = {"/order"})
public class OrderServlet extends HttpServlet {

    private final OrderService service = new OrderService();
    private final PaymentService paymentService = new PaymentService();

    private Users requireLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
            res.sendRedirect(req.getContextPath() + "/auth?action=login");
        }
        return user;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Users user = requireLogin(request, response);
        if (user == null) return;
        OrderService.OrderRow order = null;
        try { order = service.getUserOrder(user.getUserId(), Integer.parseInt(request.getParameter("id"))); } catch (Exception ignored) { }
        if (order == null) {
            request.getSession().setAttribute("error", "Không tìm thấy đơn hàng!");
            response.sendRedirect(request.getContextPath() + "/user/order-history");
            return;
        }
        request.setAttribute("order", order);
        request.setAttribute("lines", service.getLines(order.getOrderId()));
        String[] pay = paymentService.paymentInfo(order.getOrderId());
        request.setAttribute("payMethod", pay == null ? "COD" : pay[0]);
        request.setAttribute("payStatus", pay == null ? "" : pay[1]);
        if ("invoice".equals(request.getParameter("action"))) {   // UC 235: hóa đơn in được (chỉ chủ đơn, không cho hóa đơn của đơn đã hủy)
            if ("CANCELLED".equals(order.getStatus())) {
                request.getSession().setAttribute("error", "Đơn đã hủy không có hóa đơn!");
                response.sendRedirect(request.getContextPath() + "/order?id=" + order.getOrderId());
                return;
            }
            if ("PAYOS".equals(request.getAttribute("payMethod")) && !"PAID".equals(request.getAttribute("payStatus"))) {   // đơn PayOS chưa trả thì chưa có hóa đơn
                request.getSession().setAttribute("error", "Đơn chưa thanh toán nên chưa có hóa đơn!");
                response.sendRedirect(request.getContextPath() + "/order?id=" + order.getOrderId());
                return;
            }
            request.getRequestDispatcher("/views/dungntth08581/user/invoice.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("/views/dungntth08581/user/order_detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Users user = requireLogin(request, response);
        if (user == null) return;
        HttpSession session = request.getSession();
        int id = 0;
        try { id = Integer.parseInt(request.getParameter("id")); } catch (Exception ignored) { }
        if ("cancel".equals(request.getParameter("action"))) {
            String err = service.cancelByUser(user.getUserId(), id, request.getParameter("reason"));
            if (err == null) session.setAttribute("message", "Đã hủy đơn hàng."); else session.setAttribute("error", err);
        }
        if ("pay".equals(request.getParameter("action"))) {   // thanh toán lại đơn PayOS (UC 228)
            PaymentService.LinkResult lr = paymentService.createLink(user.getUserId(), id, PaymentCallbackServlet.baseUrl(request));
            if (lr.url != null) { response.sendRedirect(lr.url); return; }
            session.setAttribute("error", lr.error);
        }
        if ("received".equals(request.getParameter("action"))) {
            String err = service.confirmReceived(user.getUserId(), id);
            if (err == null) session.setAttribute("message", "Cảm ơn bạn! Đơn hàng đã được xác nhận là đã nhận.");
            else session.setAttribute("error", err);
            if ("history".equals(request.getParameter("back"))) {
                response.sendRedirect(request.getContextPath() + "/user/order-history");
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/order?id=" + id);
    }
}