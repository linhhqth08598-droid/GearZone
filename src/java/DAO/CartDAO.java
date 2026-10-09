package DAO;

import Models.CartItem;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    // 1. Lấy ID giỏ hàng của User. Nếu chưa có thì tạo mới.
    public int getOrCreateCartId(int userId) {
        int cartId = -1;
        try (Connection con = DBConnect.getConnection()) {
            // Kiểm tra xem user đã có giỏ hàng chưa
            PreparedStatement psCheck = con.prepareStatement("SELECT CartID FROM Carts WHERE UserID = ?");
            psCheck.setInt(1, userId);
            ResultSet rs = psCheck.executeQuery();
            
            if (rs.next()) {
                cartId = rs.getInt("CartID");
            } else {
                // Nếu chưa có, tạo giỏ hàng mới
                PreparedStatement psInsert = con.prepareStatement("INSERT INTO Carts (UserID, CreatedAt, UpdatedAt) VALUES (?, GETDATE(), GETDATE())", Statement.RETURN_GENERATED_KEYS);
                psInsert.setInt(1, userId);
                psInsert.executeUpdate();
                ResultSet rsNew = psInsert.getGeneratedKeys();
                if (rsNew.next()) {
                    cartId = rsNew.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartId;
    }

    // 2. Thêm sản phẩm vào giỏ hàng
    public void addToCart(int userId, int productId, int quantity) {
        int cartId = getOrCreateCartId(userId);
        if (cartId == -1) return;

        try (Connection con = DBConnect.getConnection()) {
            // Kiểm tra xem sản phẩm đã có trong giỏ chưa
            PreparedStatement psCheck = con.prepareStatement("SELECT CartItemID, Quantity FROM Cart_Items WHERE CartID = ? AND ProductID = ?");
            psCheck.setInt(1, cartId);
            psCheck.setInt(2, productId);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // Đã có -> Cộng dồn số lượng
                int currentQty = rs.getInt("Quantity");
                PreparedStatement psUpdate = con.prepareStatement("UPDATE Cart_Items SET Quantity = ?, AddedAt = GETDATE() WHERE CartID = ? AND ProductID = ?");
                psUpdate.setInt(1, currentQty + quantity);
                psUpdate.setInt(2, cartId);
                psUpdate.setInt(3, productId);
                psUpdate.executeUpdate();
            } else {
                // Chưa có -> Thêm mới
                PreparedStatement psInsert = con.prepareStatement("INSERT INTO Cart_Items (CartID, ProductID, Quantity, AddedAt) VALUES (?, ?, ?, GETDATE())");
                psInsert.setInt(1, cartId);
                psInsert.setInt(2, productId);
                psInsert.setInt(3, quantity);
                psInsert.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 3. Lấy danh sách sản phẩm trong giỏ hàng để hiển thị
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> list = new ArrayList<>();
        int cartId = getOrCreateCartId(userId);
        
        String sql = "SELECT ci.CartItemID, ci.CartID, ci.ProductID, ci.Quantity, " +
                     "p.ProductName, p.BasePrice, i.ImageBase64 " +
                     "FROM Cart_Items ci " +
                     "JOIN Products p ON ci.ProductID = p.ProductID " +
                     "LEFT JOIN Product_Images i ON p.ProductID = i.ProductID " +
                     "WHERE ci.CartID = ? ORDER BY ci.AddedAt DESC";
                     
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(rs.getInt("CartItemID"));
                item.setCartId(rs.getInt("CartID"));
                item.setProductId(rs.getInt("ProductID"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setProductName(rs.getString("ProductName"));
                item.setBasePrice(rs.getDouble("BasePrice"));
                item.setImageBase64(rs.getString("ImageBase64"));
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // 4. Xóa 1 sản phẩm khỏi giỏ
    public void removeCartItem(int cartItemId) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Cart_Items WHERE CartItemID = ?")) {
            ps.setInt(1, cartItemId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    // 5. Cập nhật số lượng sản phẩm trong giỏ
    public void updateCartItemQuantity(int cartItemId, int newQuantity) {
        if (newQuantity <= 0) {
            removeCartItem(cartItemId);
            return;
        }
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE Cart_Items SET Quantity = ? WHERE CartItemID = ?")) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartItemId);
            ps.executeUpdate();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}