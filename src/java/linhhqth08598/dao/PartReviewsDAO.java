package linhhqth08598.dao;

import model.PartReviews;
import url.DBConnect;
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
}
