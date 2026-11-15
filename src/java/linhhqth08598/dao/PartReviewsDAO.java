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
                obj.setOrderId(rs.getObject("order_id", Integer.class));
                obj.setQualityRating(rs.getDouble("quality_rating"));
                obj.setPerformanceRating(rs.getObject("performance_rating", Integer.class));
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
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(review.getQualityRating()));
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
                    review.setQualityRating(rs.getDouble("quality_rating"));
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
                review.setQualityRating(rs.getDouble("quality_rating"));
                review.setComment(rs.getString("comment"));
                review.setStatus(rs.getString("status"));
                review.setPerformanceRating(rs.getObject("performance_rating", Integer.class));
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

    // ===================== UC 296: chỉ người đã nhận hàng mới được đánh giá =====================
    /** Trả về order_id (đơn DELIVERED có linh kiện này mà user CHƯA đánh giá theo đơn đó), hoặc -1 nếu không đủ điều kiện. */
    public int findReviewableOrderId(int userId, int partId) {
        String sql = "SELECT TOP 1 o.order_id FROM orders o JOIN order_items oi ON oi.order_id = o.order_id "
                   + "WHERE o.user_id = ? AND oi.part_id = ? AND o.order_status = 'DELIVERED' "
                   + "AND NOT EXISTS (SELECT 1 FROM part_reviews r WHERE r.user_id = o.user_id AND r.part_id = oi.part_id AND r.order_id = o.order_id) "
                   + "ORDER BY o.order_id DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, partId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    // ===================== UC 300: đánh giá cấu hình PC (pc_builds.review_*) =====================
    public boolean reviewBuild(int buildId, int userId, int rating, String comment) {
        String sql = "UPDATE pc_builds SET review_rating = ?, review_comment = ? WHERE build_id = ? AND user_id = ? AND status = 'SAVED'";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, buildId);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ===================== UC 301: đánh giá dịch vụ lắp ráp (assembly_orders.customer_*) =====================
    public boolean reviewAssembly(int assemblyId, int userId, int rating, String feedback) {
        String sql = "UPDATE assembly_orders SET customer_rating = ?, customer_feedback = ? "
                   + "WHERE assembly_id = ? AND user_id = ? AND status IN ('HANDED_OVER','COMPLETED') AND customer_rating IS NULL";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, feedback);
            ps.setInt(3, assemblyId);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Danh sách đơn lắp ráp của user (để chọn đánh giá). Mỗi phần tử: id, loại, trạng thái, ngày hẹn, sao, nhận xét. */
    public List<java.util.Map<String, Object>> findAssembliesOfUser(int userId) {
        List<java.util.Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT assembly_id, assembly_type, status, scheduled_date, customer_rating, customer_feedback "
                   + "FROM assembly_orders WHERE user_id = ? ORDER BY assembly_id DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("assemblyId", rs.getInt("assembly_id"));
                    m.put("type", rs.getString("assembly_type"));
                    m.put("status", rs.getString("status"));
                    m.put("scheduledDate", rs.getTimestamp("scheduled_date"));
                    m.put("rating", rs.getObject("customer_rating", Integer.class));
                    m.put("feedback", rs.getString("customer_feedback"));
                    list.add(m);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ===================== UC 302: báo cáo thông tin sai => tạo ticket PRODUCT_REPORT gắn part_id =====================
    public boolean insertPartReport(int userId, int partId, String code, String subject, String content) {
        String sql = "INSERT INTO support_tickets (ticket_code, user_id, ticket_type, subject, content, part_id, priority, status, created_at) "
                   + "VALUES (?, ?, 'PRODUCT_REPORT', ?, ?, ?, 'NORMAL', 'OPEN', GETDATE())";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, userId);
            ps.setString(3, subject);
            ps.setString(4, content);
            ps.setInt(5, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
