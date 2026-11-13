package dungntth08581.dao;

import fourSome.model.CartItems;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartItemsDAO {

    public List<CartItems> getAll() {
        List<CartItems> list = new ArrayList<>();
        String sql = "SELECT * FROM [cart_items]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CartItems obj = new CartItems();
                obj.setCartId(rs.getInt("cart_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setQuantity(rs.getInt("quantity"));
                obj.setAddedAt(rs.getDate("added_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
