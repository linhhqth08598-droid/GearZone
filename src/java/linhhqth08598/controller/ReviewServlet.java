package linhhqth08598.controller;

import java.io.IOException;
import java.util.Base64;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import fourSome.model.*;
import linhhqth08598.dao.PartReviewsDAO;
import linhhqth08598.service.ReviewService;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/review"})
@MultipartConfig(maxFileSize = 1024 * 1024 * 5) // Giới hạn 5MB
public class ReviewServlet extends HttpServlet {
    
    private ReviewService reviewService = new ReviewService();

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
            int partId = Integer.parseInt(request.getParameter("partId"));
            
            // Xử lý điểm đánh giá (có thể là 4.5 nên dùng Double)
            double ratingDouble = Double.parseDouble(request.getParameter("qualityRating"));
            int qualityRating = (int) ratingDouble; // Ép về int để lưu DB (4.5 thành 4)
            
            String comment = request.getParameter("comment");

            // Xử lý ảnh (nếu có)
            String base64Image = null;
            Part filePart = request.getPart("reviewImage");
            if (filePart != null && filePart.getSize() > 0) {
                byte[] bytes = filePart.getInputStream().readAllBytes();
                base64Image = "data:" + filePart.getContentType() + ";base64," + Base64.getEncoder().encodeToString(bytes);
            }

            PartReviews review = new PartReviews();
            review.setUserId(user.getUserId());
            review.setPartId(partId);
            review.setQualityRating(qualityRating);
            review.setComment(comment);

            String error = reviewService.addReview(review);
            
            if (error == null) {
                // Lưu ảnh vào bảng part_review_images
                if (base64Image != null) {
                    PartReviewsDAO dao = new PartReviewsDAO();
                    dao.insertReviewImage(review.getReviewId(), base64Image);
                }
                session.setAttribute("message", "Cảm ơn bạn đã gửi đánh giá!");
            } else {
                session.setAttribute("error", error);
            }
            
            response.sendRedirect(request.getContextPath() + "/product?action=detail&id=" + partId);
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Dữ liệu nhập vào không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}