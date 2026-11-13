package linhhqth08598.dao;

import fourSome.model.UserWishlists;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserWishlistsDAO {

    // Hàm lấy danh sách Wishlist của 1 User kèm thông tin sản phẩm
    public List<UserWishlists> getWishlistByUserId(int userId) {
        List<UserWishlists> list = new ArrayList<>();
        // Dùng LEFT JOIN để lấy thêm ảnh chính (is_primary = 1) từ bảng part_media
        String sql = "SELECT w.wishlist_id, w.user_id, w.part_id, w.created_at, w.status, "
                   + "p.part_name, p.product_code, p.sale_price, "
                   + "m.media_url AS primary_image_url "
                   + "FROM user_wishlists w "
                   + "JOIN parts p ON w.part_id = p.part_id "
                   + "LEFT JOIN part_media m ON p.part_id = m.part_id AND m.is_primary = 1 AND m.status = 'ACTIVE' "
                   + "WHERE w.user_id = ? AND w.status = 'ACTIVE' "
                   + "ORDER BY w.created_at DESC";
                   
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserWishlists w = new UserWishlists();
                    w.setWishlistId(rs.getInt("wishlist_id"));
                    w.setUserId(rs.getInt("user_id"));
                    w.setPartId(rs.getInt("part_id"));
                    w.setCreatedAt(rs.getTimestamp("created_at"));
                    w.setStatus(rs.getString("status"));
                    
                    w.setPartName(rs.getString("part_name"));
                    w.setProductCode(rs.getString("product_code"));
                    w.setSalePrice(rs.getDouble("sale_price"));
                    w.setPrimaryImageUrl(rs.getString("primary_image_url"));
                    
                    list.add(w);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm mới hoặc khôi phục sản phẩm trong Wishlist.
    // Không xóa cứng để giữ lịch sử và tránh vi phạm khóa ngoại.
    public boolean addToWishlist(int userId, int partId) {
        String findSql = "SELECT status FROM user_wishlists WHERE user_id = ? AND part_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement find = conn.prepareStatement(findSql)) {
            find.setInt(1, userId);
            find.setInt(2, partId);
            try (ResultSet rs = find.executeQuery()) {
                if (rs.next()) {
                    if ("ACTIVE".equalsIgnoreCase(rs.getString("status"))) {
                        return true;
                    }
                    String restoreSql = "UPDATE user_wishlists SET status = 'ACTIVE', created_at = GETDATE() "
                                      + "WHERE user_id = ? AND part_id = ?";
                    try (PreparedStatement restore = conn.prepareStatement(restoreSql)) {
                        restore.setInt(1, userId);
                        restore.setInt(2, partId);
                        return restore.executeUpdate() > 0;
                    }
                }
            }

            String insertSql = "INSERT INTO user_wishlists (user_id, part_id, created_at, status) "
                             + "VALUES (?, ?, GETDATE(), 'ACTIVE')";
            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setInt(1, userId);
                insert.setInt(2, partId);
                return insert.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa mềm khỏi Wishlist.
    public boolean removeFromWishlist(int userId, int partId) {
        String sql = "UPDATE user_wishlists SET status = 'INACTIVE' "
                   + "WHERE user_id = ? AND part_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Chỉ xem là đã tồn tại khi mục đang hoạt động.
    public boolean checkExist(int userId, int partId) {
        String sql = "SELECT 1 FROM user_wishlists WHERE user_id = ? AND part_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, partId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Thêm vào file UserWishlistsDAO.java
    public java.util.List<fourSome.model.OrderHistoryItem> getOrderHistory(int userId) {
        java.util.List<fourSome.model.OrderHistoryItem> list = new java.util.ArrayList<>();
        // JOIN bảng orders, order_items, parts, part_media
        String sql = "SELECT o.order_id, o.order_code, o.order_status, o.created_at, o.final_amount, "
                   + "oi.part_id, oi.quantity, oi.unit_price, p.part_name, m.media_url "
                   + "FROM orders o "
                   + "JOIN order_items oi ON o.order_id = oi.order_id "
                   + "JOIN parts p ON oi.part_id = p.part_id "
                   + "LEFT JOIN part_media m ON p.part_id = m.part_id AND m.is_primary = 1 "
                   + "WHERE o.user_id = ? "
                   + "ORDER BY o.created_at DESC";
                   
        try (java.sql.Connection conn = fourSome.url.DBConnect.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    fourSome.model.OrderHistoryItem item = new fourSome.model.OrderHistoryItem();
                    item.setOrderId(rs.getInt("order_id"));
                    item.setOrderCode(rs.getString("order_code"));
                    item.setOrderStatus(rs.getString("order_status"));
                    item.setOrderDate(rs.getTimestamp("created_at"));
                    item.setTotalAmount(rs.getDouble("final_amount"));
                    
                    item.setPartId(rs.getInt("part_id"));
                    item.setPartName(rs.getString("part_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getDouble("unit_price"));
                    item.setImageUrl(rs.getString("media_url"));
                    
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}