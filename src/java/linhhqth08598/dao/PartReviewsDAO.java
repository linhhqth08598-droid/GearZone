package linhhqth08598.dao;

import fourSome.model.PartReviewImages;
import fourSome.model.PartReviews;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartReviewsDAO {

    public List<PartReviews> getAll() {
        List<PartReviews> list = new ArrayList<>();
        String sql = "SELECT * FROM [part_reviews]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PartReviews obj = new PartReviews();
                obj.setReviewId(rs.getInt("review_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setOrderId(rs.getInt("order_id"));
                obj.setQualityRating(rs.getInt("quality_rating"));
                obj.setPerformanceRating(rs.getInt("performance_rating"));
                obj.setComment(rs.getString("comment"));
                obj.setStatus(rs.getString("status"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
        // Thêm vào PartReviewsDAO.java

    // Sửa lại hàm insertReviewImage
    public boolean insertReviewImage(int reviewId, String base64Image) {
        String sql = "INSERT INTO part_review_images (review_id, image_url, status) VALUES (?, ?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ps.setString(2, base64Image);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm lấy danh sách ảnh của một review
    public List<PartReviewImages> getImagesByReviewId(int reviewId) {
        List<PartReviewImages> list = new ArrayList<>();
        String sql = "SELECT * FROM part_review_images WHERE review_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PartReviewImages img = new PartReviewImages();
                    img.setImageId(rs.getInt("image_id"));
                    img.setReviewId(rs.getInt("review_id"));
                    img.setImageBase64(rs.getString("image_url")); // Lưu base64 vào đây
                    img.setStatus(rs.getString("status"));
                    list.add(img);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
        public boolean insertReview(PartReviews review) {
        String sql = "INSERT INTO part_reviews (user_id, part_id, order_id, quality_rating, performance_rating, comment, status, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 'VISIBLE', GETDATE())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, review.getUserId());
            ps.setInt(2, review.getPartId());
            ps.setObject(3, review.getOrderId(), Types.INTEGER);
            ps.setInt(4, review.getQualityRating());
            ps.setObject(5, review.getPerformanceRating(), Types.INTEGER);
            ps.setString(6, review.getComment());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        review.setReviewId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Sửa lại hàm getReviewsByPartId (Đã join đúng bảng part_review_images)
    public List<PartReviews> getReviewsByPartId(int partId) {
        List<PartReviews> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name, i.image_url " +
                     "FROM part_reviews r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "LEFT JOIN part_review_images i ON r.review_id = i.review_id " +
                     "WHERE r.part_id = ? AND r.status = 'VISIBLE' ORDER BY r.created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PartReviews review = new PartReviews();
                    review.setReviewId(rs.getInt("review_id"));
                    review.setUserId(rs.getInt("user_id"));
                    review.setPartId(rs.getInt("part_id"));
                    review.setOrderId(rs.getObject("order_id", Integer.class));
                    review.setQualityRating(rs.getInt("quality_rating"));
                    review.setPerformanceRating(rs.getObject("performance_rating", Integer.class));
                    review.setComment(rs.getString("comment"));
                    review.setStatus(rs.getString("status"));
                    review.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    // SET ẢNH VÀO THUỘC TÍNH MỚI
                    review.setImageUrl(rs.getString("image_url"));
                    // (Tùy chọn) Lấy thêm tên người dùng nếu cần
                    review.setUserName(rs.getString("full_name")); 
                    
                    list.add(review);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
        // Lấy tất cả đánh giá cho Admin (Bao gồm cả các đánh giá bị ẩn)
    public List<PartReviews> getAllForAdmin() {
        List<PartReviews> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name, p.part_name, i.image_url " +
                     "FROM part_reviews r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "JOIN parts p ON r.part_id = p.part_id " +
                     "LEFT JOIN part_review_images i ON r.review_id = i.review_id " +
                     "ORDER BY r.created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PartReviews review = new PartReviews();
                review.setReviewId(rs.getInt("review_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setPartId(rs.getInt("part_id"));
                review.setQualityRating(rs.getInt("quality_rating"));
                review.setComment(rs.getString("comment"));
                review.setStatus(rs.getString("status"));
                review.setCreatedAt(rs.getTimestamp("created_at"));
                review.setUserName(rs.getString("full_name")); // Tên khách
                review.setPartName(rs.getString("part_name")); // Tên sản phẩm
                review.setImageUrl(rs.getString("image_url"));
                list.add(review);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Cập nhật trạng thái đánh giá (VISIBLE / HIDDEN)
    public boolean updateStatus(int reviewId, String status) {
        String sql = "UPDATE part_reviews SET status = ? WHERE review_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reviewId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
