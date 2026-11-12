package DAO;

import Models.PartReviewImages;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartReviewImagesDAO {

    public List<PartReviewImages> getAll() {
        List<PartReviewImages> list = new ArrayList<>();
        String sql = "SELECT * FROM [part_review_images]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PartReviewImages pri = new PartReviewImages();
                    pri.setImageId(rs.getInt("image_id"));
                    pri.setReviewId(rs.getInt("review_id"));
                    pri.setImageUrl(rs.getString("image_url"));
                    pri.setStatus(rs.getString("status"));
                    list.add(pri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
