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

@WebServlet(name = "AdminReturnServlet", urlPatterns = {"/admin/returns"})
public class AdminReturnServlet extends HttpServlet {

    private ReturnService returnService = new ReturnService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<ProductReturns> returns = returnService.getAllReturnsForAdmin();
        request.setAttribute("returnsList", returns);
        request.getRequestDispatcher("/views/linhhqth08598/admin/returns.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        try {
            int returnId = Integer.parseInt(request.getParameter("returnId"));
            String action = request.getParameter("action"); // "approve" or "reject"
            String reason = request.getParameter("reason");

            String status = "approve".equals(action) ? "APPROVED" : "REJECTED";
            String error = returnService.processReturnRequest(returnId, status, reason, adminUser);

            if (error == null) {
                session.setAttribute("message", "Xử lý yêu cầu đổi trả thành công!");
            } else {
                session.setAttribute("error", error);
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/returns");
    }
}