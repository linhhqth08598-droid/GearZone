package DAO;

import Models.PartReviews;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartReviewsDAO {

    public List<PartReviews> getAll() {
        List<PartReviews> list = new ArrayList<>();
        String sql = "SELECT * FROM [part_reviews]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PartReviews pr = new PartReviews();
                    pr.setReviewId(rs.getInt("review_id"));
                    pr.setUserId(rs.getInt("user_id"));
                    pr.setPartId(rs.getInt("part_id"));
                    pr.setOrderId(rs.getInt("order_id"));
                    pr.setQualityRating(rs.getInt("quality_rating"));
                    pr.setPerformanceRating(rs.getInt("performance_rating"));
                    pr.setComment(rs.getString("comment"));
                    pr.setStatus(rs.getString("status"));
                    pr.setCreatedAt(rs.getDate("created_at"));
                    list.add(pr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
