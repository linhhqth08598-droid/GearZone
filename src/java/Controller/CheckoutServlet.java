package Controller;

import DAO.CartDAO;
import DAO.OrderDAO;
import Models.CartItem;
import Models.Promotion;
import Models.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout", "/place-order", "/payment-qr", "/my-orders", "/admin/orders", "/admin/update-order-status", "/order-detail"})
public class CheckoutServlet extends HttpServlet {

    private CartDAO cartDAO = new CartDAO();
    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("userAcc");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();
        
        if ("/checkout".equals(path)) {
            // Lấy giỏ hàng hiện tại
            List<CartItem> cartItems = cartDAO.getCartItems(user.getUserId());
            
            if (cartItems.isEmpty()) {
                session.setAttribute("errorMsg", "Giỏ hàng của bạn đang trống!");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            double totalAmount = 0;
            for (CartItem item : cartItems) {
                totalAmount += item.getTotalPrice();
            }

            // Xử lý khuyến mãi
            Promotion appliedPromo = (Promotion) session.getAttribute("appliedPromo");
            double discountValue = 0;
            if (appliedPromo != null && totalAmount >= appliedPromo.getMinOrderValue()) {
                if (appliedPromo.getDiscountPercent() > 0) {
                    discountValue = totalAmount * (appliedPromo.getDiscountPercent() / 100);
                } else {
                    discountValue = appliedPromo.getDiscountAmount();
                }
                if (discountValue > totalAmount) discountValue = totalAmount;
            }

            double finalAmount = totalAmount - discountValue;

            request.setAttribute("cartItems", cartItems);
            request.setAttribute("totalAmount", totalAmount);
            request.setAttribute("discountValue", discountValue);
            request.setAttribute("finalAmount", finalAmount);
            
            request.getRequestDispatcher("/views/checkout.jsp").forward(request, response);
            
        } else if ("/my-orders".equals(path)) {
            // Lấy toàn bộ đơn hàng của khách đó đẩy ra view
            request.setAttribute("orderList", orderDAO.getOrdersByUser(user.getUserId()));
            request.setAttribute("activeMenu", "my-orders"); // Thêm dòng này để bôi đỏ menu
            request.getRequestDispatcher("/views/orders.jsp").forward(request, response);
            
        } else if ("/payment-qr".equals(path)) {
            // Hiển thị trang quét mã QR thanh toán
            request.getRequestDispatcher("/views/payment-qr.jsp").forward(request, response);
            
        } else if ("/admin/orders".equals(path)) {
            // Chặn không cho người dùng thường vào trang admin
            if (user.getRoleId() > 3) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            request.setAttribute("adminOrderList", orderDAO.getAllOrders());
            request.getRequestDispatcher("/views/admin-orders.jsp").forward(request, response);
        } else if ("/order-detail".equals(path)) {
            int orderId = Integer.parseInt(request.getParameter("id"));
            
            Models.Order orderInfo = null;
            List<Models.Order> orderList;
            
            // Nếu là Admin/Nhân sự thì lấy tất cả đơn để kiểm tra, ngược lại chỉ lấy đơn của cá nhân
            if (user.getRoleId() <= 3) {
                orderList = orderDAO.getAllOrders();
            } else {
                orderList = orderDAO.getOrdersByUser(user.getUserId());
            }
            
            for(Models.Order o : orderList) {
                if(o.getOrderId() == orderId) {
                    orderInfo = o; break;
                }
            }
            
            List<Models.OrderDetail> details = orderDAO.getOrderDetails(orderId);
            
            request.setAttribute("orderInfo", orderInfo);
            request.setAttribute("orderDetails", details);
            request.setAttribute("activeMenu", user.getRoleId() <= 3 ? "admin-orders" : "my-orders");
            
            request.getRequestDispatcher("/views/order-detail.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("userAcc");
        
        // BỔ SUNG DÒNG NÀY ĐỂ KHAI BÁO BIẾN path CHO doPost
        String path = request.getServletPath();

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("/place-order".equals(path)) {
            // Lấy thông tin giao hàng từ form
            String shippingName = request.getParameter("shippingName");
            String shippingPhone = request.getParameter("shippingPhone");
            String shippingAddress = request.getParameter("shippingAddress");
            String paymentMethod = request.getParameter("paymentMethod");

            // Lấy lại giỏ hàng và tính toán tiền (chống hack sửa HTML)
            List<CartItem> cartItems = cartDAO.getCartItems(user.getUserId());
            if (cartItems.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            double totalAmount = 0;
            for (CartItem item : cartItems) {
                totalAmount += item.getTotalPrice();
            }

            Promotion appliedPromo = (Promotion) session.getAttribute("appliedPromo");
            Integer promoId = null;
            double discountValue = 0;
            
            if (appliedPromo != null && totalAmount >= appliedPromo.getMinOrderValue()) {
                promoId = appliedPromo.getPromotionId();
                if (appliedPromo.getDiscountPercent() > 0) {
                    discountValue = totalAmount * (appliedPromo.getDiscountPercent() / 100);
                } else {
                    discountValue = appliedPromo.getDiscountAmount();
                }
                if (discountValue > totalAmount) discountValue = totalAmount;
            }

            double finalAmount = totalAmount - discountValue;

            // Tiến hành lưu đơn hàng
            String orderCode = orderDAO.createOrder(user.getUserId(), promoId, finalAmount, shippingName, shippingPhone, shippingAddress, paymentMethod, cartItems);

            if (orderCode != null) {
                session.removeAttribute("appliedPromo"); // Xóa mã KM đã dùng
                
                // Nếu chọn VNPAY -> Chuyển sang trang mã QR
                if ("VNPAY".equals(paymentMethod)) {
                    response.sendRedirect(request.getContextPath() + "/payment-qr?code=" + orderCode + "&amount=" + (long)finalAmount);
                } else {
                    // Nếu chọn COD -> Chuyển sang trang danh sách đơn hàng
                    session.setAttribute("successMsg", "Đặt hàng thành công! Mã đơn của bạn là: " + orderCode);
                    response.sendRedirect(request.getContextPath() + "/my-orders");
                }
            } else {
                session.setAttribute("errorMsg", "Có lỗi xảy ra trong quá trình đặt hàng. Vui lòng thử lại.");
                response.sendRedirect(request.getContextPath() + "/checkout");
            } 
            
        } else if ("/admin/update-order-status".equals(path)) {
            // Chặn không cho người dùng thường gửi request cập nhật
            if (user.getRoleId() > 3) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String status = request.getParameter("status"); // VD: N'Đang giao hàng'
            
            orderDAO.updateOrderStatus(orderId, status);
            session.setAttribute("successMsg", "Đã cập nhật trạng thái đơn hàng!");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
        }
    }
}