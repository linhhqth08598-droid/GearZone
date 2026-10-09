package Controller;

import DAO.ReviewDAO;
import Models.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/submit-review"})
public class ReviewServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("userAcc");

        // Bắt buộc phải đăng nhập mới được đánh giá
        if (user == null) {
            session.setAttribute("errorMsg", "Vui lòng đăng nhập để đánh giá sản phẩm!");
            response.sendRedirect(request.getHeader("Referer")); // Quay lại trang cũ
            return;
        }

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");

            ReviewDAO reviewDAO = new ReviewDAO();
            boolean success = reviewDAO.insertReview(productId, user.getUserId(), rating, comment);

            if (success) {
                session.setAttribute("successMsg", "Cảm ơn bạn đã để lại đánh giá!");
            } else {
                session.setAttribute("errorMsg", "Có lỗi xảy ra khi lưu đánh giá. Vui lòng thử lại!");
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Dữ liệu không hợp lệ!");
        }

        // Quay lại trang chi tiết sản phẩm khách vừa xem
        response.sendRedirect(request.getHeader("Referer")); 
    }
}