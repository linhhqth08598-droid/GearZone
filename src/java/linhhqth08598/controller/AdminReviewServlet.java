package linhhqth08598.controller;

import fourSome.model.PartReviews;
import fourSome.model.Users;
import linhhqth08598.dao.PartReviewsDAO;
import linhhqth08598.service.AuditLogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminReviewServlet", urlPatterns = {"/admin/reviews"})
public class AdminReviewServlet extends HttpServlet {
    private PartReviewsDAO reviewDAO = new PartReviewsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // SỬA DÒNG NÀY: Dùng hàm getAllForAdmin() thay vì getAll()
        List<PartReviews> reviews = reviewDAO.getAllForAdmin();
        request.setAttribute("reviews", reviews);
        request.getRequestDispatcher("/views/linhhqth08598/admin/reviews.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Users adminUser = (Users) session.getAttribute("loggedUser");

        try {
            if ("toggleStatus".equals(action)) {
                int reviewId = Integer.parseInt(request.getParameter("reviewId"));
                String currentStatus = request.getParameter("status");
                String newStatus = "VISIBLE".equals(currentStatus) ? "HIDDEN" : "VISIBLE";
                
                if (reviewDAO.updateStatus(reviewId, newStatus)) {
                    session.setAttribute("message", "Đã cập nhật trạng thái đánh giá!");
                    
                    AuditLogService log = new AuditLogService();
                    String logAction = "VISIBLE".equals(newStatus) ? "SHOW_REVIEW" : "HIDE_REVIEW";
                    log.logAction(adminUser.getUserId(), logAction, "part_reviews", reviewId, 
                        "Admin đã " + ("VISIBLE".equals(newStatus) ? "hiển thị" : "ẩn") + " đánh giá ID: " + reviewId);
                } else {
                    session.setAttribute("error", "Lỗi khi cập nhật trạng thái!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
        }
        response.sendRedirect(request.getContextPath() + "/admin/reviews");
    }
}