package DAO;

import Models.Review;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    // 1. Lấy danh sách đánh giá của 1 sản phẩm cụ thể (Mới nhất xếp trước)
    public List<Review> getReviewsByProduct(int productId) {
        List<Review> list = new ArrayList<>();
        // JOIN với bảng Users để lấy tên khách hàng
        String sql = "SELECT r.*, u.FullName FROM Reviews r JOIN Users u ON r.UserID = u.UserID WHERE r.ProductID = ? ORDER BY r.CreatedAt DESC";
        
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review rv = new Review();
                    rv.setReviewId(rs.getInt("ReviewID"));
                    rv.setProductId(rs.getInt("ProductID"));
                    rv.setUserId(rs.getInt("UserID"));
                    rv.setRating(rs.getInt("Rating"));
                    rv.setComment(rs.getString("Comment"));
                    rv.setCreatedAt(rs.getDate("CreatedAt"));
                    rv.setCustomerName(rs.getString("FullName"));
                    list.add(rv);
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    // 2. Thêm đánh giá mới
    public boolean insertReview(int productId, int userId, int rating, String comment) {
        String sql = "INSERT INTO Reviews (ProductID, UserID, Rating, Comment) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, productId);
            ps.setInt(2, userId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
}