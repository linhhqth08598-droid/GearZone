package linhhqth08598.controller;

import fourSome.model.Users;
import fourSome.model.PriceDropNotifications;
import linhhqth08598.service.PriceAlertService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PriceAlertServlet", urlPatterns = {"/user/price-alert"})
public class PriceAlertServlet extends HttpServlet {

    private PriceAlertService service = new PriceAlertService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        List<PriceDropNotifications> alerts = service.getAlerts(user.getUserId());
        request.setAttribute("alerts", alerts);
        request.getRequestDispatcher("/views/linhhqth08598/user/price_alert.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if ("register".equals(action)) {
            try {
                int partId = Integer.parseInt(request.getParameter("partId"));
                double targetPrice = Double.parseDouble(request.getParameter("targetPrice"));
                String error = service.registerPriceAlert(user.getUserId(), partId, targetPrice);
                if (error == null) {
                    session.setAttribute("message", "Đăng ký theo dõi giá thành công!");
                } else {
                    session.setAttribute("error", error);
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Dữ liệu nhập vào không hợp lệ!");
            }
        } else if ("delete".equals(action)) {
            try {
                int notificationId = Integer.parseInt(request.getParameter("notificationId"));
                String error = service.deleteAlert(notificationId, user.getUserId());
                if (error == null) {
                    session.setAttribute("message", "Đã hủy theo dõi sản phẩm.");
                } else {
                    session.setAttribute("error", error);
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Mã không hợp lệ!");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/user/price-alert");
    }
}