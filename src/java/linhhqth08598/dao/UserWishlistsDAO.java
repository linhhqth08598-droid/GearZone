package linhhqth08598.dao;

import fourSome.model.UserWishlists;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserWishlistsDAO {

    public List<UserWishlists> getAll() {
        List<UserWishlists> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_wishlists]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserWishlists obj = new UserWishlists();
                obj.setWishlistId(rs.getInt("wishlist_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
