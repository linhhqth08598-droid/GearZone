package linhhqth08598.service;

import fourSome.model.PartReviews;
import linhhqth08598.dao.PartReviewsDAO;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** UC 296–302: đánh giá chất lượng/hiệu năng, nhận xét, ảnh, đánh giá cấu hình PC, đánh giá lắp ráp, báo thông tin sai. */
public class ReviewService {
    private final PartReviewsDAO reviewDAO = new PartReviewsDAO();

    /** Thêm đánh giá linh kiện. Chỉ người đã nhận hàng (đơn DELIVERED) mới được đánh giá, mỗi đơn 1 lần. Trả null nếu thành công. */
    public String addReview(PartReviews review) {
        Double q = review.getQualityRating();
        if (q == null || q < 1 || q > 5 || (q * 2) != Math.rint(q * 2)) {
            return "Điểm chất lượng phải từ 1 đến 5 sao (bước 0,5)!";
        }
        if (review.getPerformanceRating() != null && (review.getPerformanceRating() < 1 || review.getPerformanceRating() > 5)) {
            return "Điểm hiệu năng phải từ 1 đến 5 sao!";
        }
        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            return "Vui lòng nhập nhận xét của bạn!";
        }
        if (review.getComment().length() > 2000) return "Nhận xét tối đa 2000 ký tự!";
        review.setComment(review.getComment().trim());

        int orderId = reviewDAO.findReviewableOrderId(review.getUserId(), review.getPartId());
        if (orderId < 0) {
            return "Bạn chỉ có thể đánh giá linh kiện sau khi đã nhận hàng (và mỗi đơn chỉ đánh giá 1 lần)!";
        }
        review.setOrderId(orderId);

        return reviewDAO.insertReview(review) ? null : "Lỗi hệ thống khi gửi đánh giá!";
    }

    public boolean canReview(int userId, int partId) {
        return reviewDAO.findReviewableOrderId(userId, partId) > 0;
    }

    public List<PartReviews> getReviewsByPart(int partId) {
        return reviewDAO.getReviewsByPartId(partId);
    }

    /** UC 300 - Đánh giá cấu hình PC (chỉ cấu hình đã lưu). */
    public String reviewBuild(int buildId, int userId, int rating, String comment) {
        if (rating < 1 || rating > 5) return "Điểm đánh giá phải từ 1 đến 5 sao!";
        if (comment != null && comment.length() > 2000) return "Nhận xét tối đa 2000 ký tự!";
        String c = comment == null || comment.trim().isEmpty() ? null : comment.trim();
        return reviewDAO.reviewBuild(buildId, userId, rating, c) ? null
                : "Chỉ đánh giá được cấu hình đã lưu của chính bạn!";
    }

    /** UC 301 - Đánh giá dịch vụ lắp ráp (đã bàn giao/hoàn tất, chỉ 1 lần). */
    public String reviewAssembly(int assemblyId, int userId, int rating, String feedback) {
        if (rating < 1 || rating > 5) return "Điểm đánh giá phải từ 1 đến 5 sao!";
        if (feedback != null && feedback.length() > 2000) return "Nhận xét tối đa 2000 ký tự!";
        String f = feedback == null || feedback.trim().isEmpty() ? null : feedback.trim();
        return reviewDAO.reviewAssembly(assemblyId, userId, rating, f) ? null
                : "Chỉ đánh giá được đơn lắp ráp của bạn đã bàn giao và chưa đánh giá!";
    }

    public List<Map<String, Object>> getAssemblies(int userId) {
        return reviewDAO.findAssembliesOfUser(userId);
    }

    /** UC 302 - Báo cáo thông tin sai về linh kiện => ticket PRODUCT_REPORT để nhân viên xử lý. */
    public String reportPartInfo(int userId, int partId, String content) {
        if (content == null || content.trim().length() < 10) return "Vui lòng mô tả thông tin sai (ít nhất 10 ký tự)!";
        if (content.length() > 2000) return "Nội dung tối đa 2000 ký tự!";
        String code = "TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return reviewDAO.insertPartReport(userId, partId, code, "Báo cáo thông tin sai về linh kiện #" + partId, content.trim())
                ? null : "Không gửi được báo cáo, vui lòng thử lại!";
    }
}
