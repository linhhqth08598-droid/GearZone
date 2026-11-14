package linhhqth08598.service;

import fourSome.model.PartReviews;
import linhhqth08598.dao.PartReviewsDAO;
import java.util.List;

public class ReviewService {
    private PartReviewsDAO reviewDAO = new PartReviewsDAO();

    public String addReview(PartReviews review) {
        if (review.getQualityRating() == null || review.getQualityRating() < 1 || review.getQualityRating() > 5) {
            return "Điểm chất lượng phải từ 1 đến 5 sao!";
        }
        if (review.getPerformanceRating() != null && (review.getPerformanceRating() < 1 || review.getPerformanceRating() > 5)) {
            return "Điểm hiệu năng phải từ 1 đến 5 sao!";
        }
        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            return "Vui lòng nhập nhận xét của bạn!";
        }

        if (reviewDAO.insertReview(review)) {
            return null; // Thành công
        }
        return "Lỗi hệ thống khi gửi đánh giá!";
    }

    public List<PartReviews> getReviewsByPart(int partId) {
        return reviewDAO.getReviewsByPartId(partId);
    }
}