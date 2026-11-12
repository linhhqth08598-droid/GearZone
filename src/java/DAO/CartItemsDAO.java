package DAO;

import Models.CartItems;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartItemsDAO {

    public List<CartItems> getAll() {
        List<CartItems> list = new ArrayList<>();
        String sql = "SELECT * FROM [cart_items]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItems ci = new CartItems();
                    ci.setCartId(rs.getInt("cart_id"));
                    ci.setUserId(rs.getInt("user_id"));
                    ci.setPartId(rs.getInt("part_id"));
                    ci.setQuantity(rs.getInt("quantity"));
                    ci.setAddedAt(rs.getDate("added_at"));
                    ci.setStatus(rs.getString("status"));
                    list.add(ci);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
