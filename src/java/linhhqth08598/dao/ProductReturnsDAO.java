package linhhqth08598.dao;

import fourSome.model.ProductReturns;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductReturnsDAO {

    public List<ProductReturns> getAll() {
        List<ProductReturns> list = new ArrayList<>();
        String sql = "SELECT * FROM [product_returns]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProductReturns obj = new ProductReturns();
                obj.setReturnId(rs.getInt("return_id"));
                obj.setOrderId(rs.getInt("order_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setQuantity(rs.getInt("quantity"));
                obj.setSerialNumber(rs.getString("serial_number"));
                obj.setReason(rs.getString("reason"));
                obj.setRefundAmount(rs.getDouble("refund_amount"));
                obj.setRefundMethod(rs.getString("refund_method"));
                obj.setRefundTransactionCode(rs.getString("refund_transaction_code"));
                obj.setRefundedAt(rs.getDate("refunded_at"));
                obj.setStatus(rs.getString("status"));
                obj.setRejectionReason(rs.getString("rejection_reason"));
                obj.setReceivedAt(rs.getDate("received_at"));
                obj.setReturnedCondition(rs.getString("returned_condition"));
                obj.setProcessedBy(rs.getInt("processed_by"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
        // 1. Khách hàng gửi yêu cầu đổi trả
    public boolean insertReturnRequest(ProductReturns request) {
        String sql = "INSERT INTO product_returns (order_id, part_id, quantity, reason, status, created_at) "
                   + "VALUES (?, ?, ?, ?, 'PENDING', GETDATE())";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, request.getOrderId());
            ps.setInt(2, request.getPartId());
            ps.setInt(3, request.getQuantity());
            ps.setString(4, request.getReason());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Lấy danh sách yêu cầu đổi trả của user
    public List<ProductReturns> getByUserId(int userId) {
        List<ProductReturns> list = new ArrayList<>();
        String sql = "SELECT r.*, p.part_name, m.media_url AS image_url "
                   + "FROM product_returns r "
                   + "JOIN orders o ON r.order_id = o.order_id "
                   + "JOIN parts p ON r.part_id = p.part_id "
                   + "LEFT JOIN part_media m ON p.part_id = m.part_id AND m.is_primary = 1 "
                   + "WHERE o.user_id = ? "
                   + "ORDER BY r.created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductReturns r = new ProductReturns();
                    r.setReturnId(rs.getInt("return_id"));
                    r.setOrderId(rs.getInt("order_id"));
                    r.setPartId(rs.getInt("part_id"));
                    r.setQuantity(rs.getInt("quantity"));
                    r.setReason(rs.getString("reason"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    // Các trường join thêm (nếu Model có)
                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
        // Admin: Lấy tất cả yêu cầu đổi trả kèm thông tin user và sản phẩm
    public List<ProductReturns> getAllReturnsForAdmin() {
        List<ProductReturns> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name, u.email, p.part_name "
                   + "FROM product_returns r "
                   + "JOIN orders o ON r.order_id = o.order_id "
                   + "JOIN users u ON o.user_id = u.user_id "
                   + "JOIN parts p ON r.part_id = p.part_id "
                   + "ORDER BY r.created_at DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProductReturns r = new ProductReturns();
                r.setReturnId(rs.getInt("return_id"));
                r.setOrderId(rs.getInt("order_id"));
                r.setPartId(rs.getInt("part_id"));
                r.setQuantity(rs.getInt("quantity"));
                r.setReason(rs.getString("reason"));
                r.setStatus(rs.getString("status"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                // Bạn có thể thêm các trường join vào Model nếu muốn hiển thị (full_name, part_name)
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Admin: Cập nhật trạng thái yêu cầu (DUYỆT hoặc TỪ CHỐI)
    public boolean updateReturnStatus(int returnId, String status, String rejectionReason) {
        String sql = "UPDATE product_returns SET status = ?, rejection_reason = ? WHERE return_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, rejectionReason);
            ps.setInt(3, returnId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
