package linhhqth08598.dao;

import fourSome.model.PartReviewImages;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartReviewImagesDAO {

    public List<PartReviewImages> getAll() {
        List<PartReviewImages> list = new ArrayList<>();
        String sql = "SELECT * FROM [part_review_images]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PartReviewImages obj = new PartReviewImages();
                obj.setImageId(rs.getInt("image_id"));
                obj.setReviewId(rs.getInt("review_id"));
                obj.setImageUrl(rs.getString("image_url"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
