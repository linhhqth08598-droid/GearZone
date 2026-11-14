package linhhqth08598.controller;

import fourSome.model.Users;
import fourSome.model.ProductReturns;
import linhhqth08598.service.ReturnService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ReturnServlet", urlPatterns = {"/user/returns"})
public class ReturnServlet extends HttpServlet {

    private ReturnService returnService = new ReturnService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        List<ProductReturns> returns = returnService.getUserReturns(user.getUserId());
        request.setAttribute("returnsList", returns);
        request.getRequestDispatcher("/views/linhhqth08598/user/returns.jsp").forward(request, response);
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

        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            int partId = Integer.parseInt(request.getParameter("partId"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            String reason = request.getParameter("reason");

            String error = returnService.createReturnRequest(orderId, partId, quantity, reason);
            
            if (error == null) {
                session.setAttribute("message", "Gửi yêu cầu đổi trả thành công! Vui lòng chờ duyệt.");
            } else {
                session.setAttribute("error", error);
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu nhập vào không hợp lệ!");
        }
        
        response.sendRedirect(request.getContextPath() + "/user/returns");
    }
}