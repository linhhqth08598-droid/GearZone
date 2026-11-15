package khanhnb08894.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Tồn kho (UC 210-211) - bản gọn làm hộ Khánh để luồng đặt hàng của Dũng chạy được.
 * Mọi hàm nhận Connection của giao dịch đang mở (đơn hàng + kho phải cùng commit/rollback)
 * và ghi inventory_transactions đúng quy ước "đổi tồn kho luôn có lịch sử".
 *   reserve : giữ hàng khi đặt   (reserved += qty)
 *   release : trả hàng giữ khi hủy (reserved -= qty)
 *   export  : xuất kho khi giao xong (stock -= qty, reserved -= qty)
 */
public class InventoryService {

    /** Tồn khả dụng = tồn kho - đang giữ (0 nếu chưa có dòng tồn kho). */
    public int getAvailable(Connection con, int partId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT stock_quantity - reserved_quantity FROM inventory WHERE part_id = ?")) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Math.max(rs.getInt(1), 0) : 0;
            }
        }
    }

    /** Giữ hàng cho đơn. Trả false nếu không đủ hàng (không thay đổi gì). */
    public boolean reserve(Connection con, int partId, int qty, int orderId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE inventory SET reserved_quantity = reserved_quantity + ?, updated_at = SYSDATETIME() "
              + "WHERE part_id = ? AND stock_quantity - reserved_quantity >= ?")) {
            ps.setInt(1, qty); ps.setInt(2, partId); ps.setInt(3, qty);
            if (ps.executeUpdate() == 0) return false;
        }
        log(con, partId, "RESERVE", qty, orderId, "Giữ hàng cho đơn #" + orderId);
        return true;
    }

    public void release(Connection con, int partId, int qty, int orderId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE inventory SET reserved_quantity = CASE WHEN reserved_quantity >= ? THEN reserved_quantity - ? ELSE 0 END, updated_at = SYSDATETIME() WHERE part_id = ?")) {
            ps.setInt(1, qty); ps.setInt(2, qty); ps.setInt(3, partId);
            ps.executeUpdate();
        }
        log(con, partId, "RELEASE", -qty, orderId, "Hoàn hàng giữ do hủy đơn #" + orderId);
    }

    public void exportStock(Connection con, int partId, int qty, int orderId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE inventory SET stock_quantity = CASE WHEN stock_quantity >= ? THEN stock_quantity - ? ELSE 0 END, "
              + "reserved_quantity = CASE WHEN reserved_quantity >= ? THEN reserved_quantity - ? ELSE 0 END, updated_at = SYSDATETIME() WHERE part_id = ?")) {
            ps.setInt(1, qty); ps.setInt(2, qty); ps.setInt(3, qty); ps.setInt(4, qty); ps.setInt(5, partId);
            ps.executeUpdate();
        }
        log(con, partId, "EXPORT", -qty, orderId, "Xuất kho giao đơn #" + orderId);
    }

    private void log(Connection con, int partId, String type, int change, int orderId, String reason) throws SQLException {
        int stock = 0;
        try (PreparedStatement ps = con.prepareStatement("SELECT stock_quantity FROM inventory WHERE part_id = ?")) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) stock = rs.getInt(1); }
        }
        // RESERVE/RELEASE không đổi tồn kho thực (chỉ đổi số đang giữ); EXPORT đổi thực.
        int before = "EXPORT".equals(type) ? stock - change : stock;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO inventory_transactions (part_id, transaction_type, quantity_change, quantity_before, quantity_after, reference_type, reference_id, reason, created_at, status) "
              + "VALUES (?, ?, ?, ?, ?, 'ORDER', ?, ?, SYSDATETIME(), 'ACTIVE')")) {
            ps.setInt(1, partId); ps.setString(2, type); ps.setInt(3, change);
            ps.setInt(4, before); ps.setInt(5, stock); ps.setInt(6, orderId); ps.setString(7, reason);
            ps.executeUpdate();
        }
    }
}
