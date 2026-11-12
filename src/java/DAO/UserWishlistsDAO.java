package DAO;

import Models.UserWishlists;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserWishlistsDAO {

    public List<UserWishlists> getAll() {
        List<UserWishlists> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_wishlists]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserWishlists uw = new UserWishlists();
                    uw.setWishlistId(rs.getInt("wishlist_id"));
                    uw.setUserId(rs.getInt("user_id"));
                    uw.setPartId(rs.getInt("part_id"));
                    uw.setCreatedAt(rs.getDate("created_at"));
                    uw.setStatus(rs.getString("status"));
                    list.add(uw);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
