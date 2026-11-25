package khanhnb08894.service;

import fourSome.model.Inventory;
import fourSome.model.InventoryTransactions;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import khanhnb08894.dao.InventoryDAO;
import khanhnb08894.dao.InventoryTransactionsDAO;

/**
 * Nghiệp vụ quản lý kho linh kiện (Module D: UC 197-208, 210-215, 345).
 * Mọi thay đổi tồn kho đều đi qua service này và ghi lịch sử vào inventory_transactions.
 */
public class InventoryService {

    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final InventoryTransactionsDAO transactionsDAO = new InventoryTransactionsDAO();

    public List<Inventory> getAllInventory() {
        return inventoryDAO.getAll();
    }

    public List<InventoryTransactions> getAllTransactions() {
        return transactionsDAO.getAll();
    }

    /** Tồn khả dụng = tồn kho - đang giữ (0 nếu chưa có dòng tồn kho). */
    public int getAvailable(Connection con, int partId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT stock_quantity - reserved_quantity FROM inventory WHERE part_id = ?")) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Math.max(rs.getInt(1), 0) : 0;
            }
        }
    }

    public int getAvailable(int partId) {
        try (Connection con = DBConnect.getConnection()) {
            return con == null ? 0 : getAvailable(con, partId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** Giữ hàng cho đơn. Trả false nếu không đủ hàng (không thay đổi gì). */
    public boolean reserve(Connection con, int partId, int qty, int orderId) throws SQLException {
        if (qty <= 0) {
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE inventory SET reserved_quantity = reserved_quantity + ?, updated_at = SYSDATETIME() "
              + "WHERE part_id = ? AND stock_quantity - reserved_quantity >= ?")) {
            ps.setInt(1, qty);
            ps.setInt(2, partId);
            ps.setInt(3, qty);
            if (ps.executeUpdate() == 0) {
                return false;
            }
        }
        log(con, partId, "RESERVE", qty, orderId, "ORDER", "Giữ hàng cho đơn #" + orderId);
        return true;
    }

    public void release(Connection con, int partId, int qty, int orderId) throws SQLException {
        if (qty <= 0) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE inventory SET reserved_quantity = CASE WHEN reserved_quantity >= ? THEN reserved_quantity - ? ELSE 0 END, updated_at = SYSDATETIME() WHERE part_id = ?")) {
            ps.setInt(1, qty);
            ps.setInt(2, qty);
            ps.setInt(3, partId);
            ps.executeUpdate();
        }
        log(con, partId, "RELEASE", -qty, orderId, "ORDER", "Hoàn hàng giữ do hủy đơn #" + orderId);
    }

    public void exportStock(Connection con, int partId, int qty, int orderId) throws SQLException {
        if (qty <= 0) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE inventory SET stock_quantity = CASE WHEN stock_quantity >= ? THEN stock_quantity - ? ELSE 0 END, "
              + "reserved_quantity = CASE WHEN reserved_quantity >= ? THEN reserved_quantity - ? ELSE 0 END, updated_at = SYSDATETIME() WHERE part_id = ?")) {
            ps.setInt(1, qty);
            ps.setInt(2, qty);
            ps.setInt(3, qty);
            ps.setInt(4, qty);
            ps.setInt(5, partId);
            ps.executeUpdate();
        }
        log(con, partId, "EXPORT", -qty, orderId, "ORDER", "Xuất kho giao đơn #" + orderId);
    }

    /** Nhập kho: cộng bảng inventory (tạo dòng nếu chưa có) và ghi inventory_transactions (IMPORT). */
    public void importStock(Connection con, int partId, int qty, int poId) throws SQLException {
        if (qty <= 0) {
            return;
        }
        int before = 0;
        boolean exists = false;
        try (PreparedStatement ps = con.prepareStatement("SELECT stock_quantity FROM inventory WHERE part_id = ?")) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                    before = rs.getInt(1);
                }
            }
        }
        if (exists) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE inventory SET stock_quantity = stock_quantity + ?, updated_at = SYSDATETIME() WHERE part_id = ?")) {
                ps.setInt(1, qty);
                ps.setInt(2, partId);
                ps.executeUpdate();
            }
        } else {
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO inventory (part_id, stock_quantity, reserved_quantity, damaged_quantity, low_stock_threshold, updated_at, status) VALUES (?, ?, 0, 0, 5, SYSDATETIME(), 'ACTIVE')")) {
                ps.setInt(1, partId);
                ps.setInt(2, qty);
                ps.executeUpdate();
            }
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO inventory_transactions (part_id, transaction_type, quantity_change, quantity_before, quantity_after, reference_type, reference_id, reason, created_at, status) "
              + "VALUES (?, 'IMPORT', ?, ?, ?, 'PO', ?, ?, SYSDATETIME(), 'ACTIVE')")) {
            ps.setInt(1, partId);
            ps.setInt(2, qty);
            ps.setInt(3, before);
            ps.setInt(4, before + qty);
            ps.setInt(5, poId);
            ps.setString(6, "Nhập kho từ phiếu nhập #" + poId);
            ps.executeUpdate();
        }
    }

    /** Điều chỉnh tồn kho sau kiểm kê hoặc ghi nhận lỗi/hỏng/thất thoát (UC 200-203). */
    public boolean adjustStock(int partId, String txType, int deltaQty, String reason, Integer staffId) throws SQLException {
        if (partId <= 0 || txType == null) {
            return false;
        }
        String normalized = txType.trim().toUpperCase();
        if (!normalized.matches("ADJUSTMENT|DAMAGED|LOST")) {
            throw new IllegalArgumentException("Loại điều chỉnh tồn kho không hợp lệ.");
        }
        if ("DAMAGED".equals(normalized) || "LOST".equals(normalized)) {
            if (deltaQty <= 0) {
                throw new IllegalArgumentException("Số lượng lỗi/hỏng hoặc thất thoát phải lớn hơn 0.");
            }
        } else if (deltaQty == 0) {
            throw new IllegalArgumentException("Số lượng điều chỉnh kiểm kê không được bằng 0.");
        }

        try (Connection con = DBConnect.getConnection()) {
            if (con == null) {
                throw new SQLException("Không thể kết nối CSDL.");
            }
            con.setAutoCommit(false);
            try {
                int before = 0;
                int damagedBefore = 0;
                boolean exists = false;
                try (PreparedStatement ps = con.prepareStatement("SELECT stock_quantity, damaged_quantity FROM inventory WHERE part_id = ?")) {
                    ps.setInt(1, partId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            exists = true;
                            before = rs.getInt(1);
                            damagedBefore = rs.getInt(2);
                        }
                    }
                }
                int change;
                int after;
                int damagedAfter = damagedBefore;
                if ("ADJUSTMENT".equals(normalized)) {
                    change = deltaQty;
                    after = Math.max(0, before + change);
                } else {
                    change = -Math.min(deltaQty, Math.max(0, before));
                    after = Math.max(0, before + change);
                    if ("DAMAGED".equals(normalized)) {
                        damagedAfter = damagedBefore + Math.abs(change);
                    }
                }

                if (exists) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE inventory SET stock_quantity = ?, damaged_quantity = ?, updated_at = SYSDATETIME() WHERE part_id = ?")) {
                        ps.setInt(1, after);
                        ps.setInt(2, damagedAfter);
                        ps.setInt(3, partId);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO inventory (part_id, stock_quantity, reserved_quantity, damaged_quantity, low_stock_threshold, updated_at, status) VALUES (?, ?, 0, ?, 5, SYSDATETIME(), 'ACTIVE')")) {
                        ps.setInt(1, partId);
                        ps.setInt(2, Math.max(0, after));
                        ps.setInt(3, Math.max(0, damagedAfter));
                        ps.executeUpdate();
                    }
                }

                String sqlTx = "INSERT INTO inventory_transactions (part_id, transaction_type, quantity_change, quantity_before, quantity_after, reference_type, reference_id, reason, created_by, created_at, status) "
                        + "VALUES (?, ?, ?, ?, ?, 'STOCKTAKE', NULL, ?, ?, SYSDATETIME(), 'ACTIVE')";
                try (PreparedStatement psTx = con.prepareStatement(sqlTx)) {
                    psTx.setInt(1, partId);
                    psTx.setString(2, normalized);
                    psTx.setInt(3, change);
                    psTx.setInt(4, before);
                    psTx.setInt(5, after);
                    psTx.setString(6, reason == null || reason.trim().isEmpty() ? ("Điều chỉnh kho (" + normalized + ")") : reason.trim());
                    if (staffId != null && staffId > 0) {
                        psTx.setInt(7, staffId);
                    } else {
                        psTx.setNull(7, java.sql.Types.INTEGER);
                    }
                    psTx.executeUpdate();
                }

                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                if (e instanceof IllegalArgumentException) {
                    throw (IllegalArgumentException) e;
                }
                throw new SQLException("Lỗi điều chỉnh tồn kho: " + e.getMessage(), e);
            }
        }
    }

    /** Thống kê tồn kho theo danh mục (UC 345). */
    public List<Map<String, Object>> getCategoryStockSummary() {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "SELECT c.category_id, c.category_name, "
                + "COUNT(DISTINCT p.part_id) AS part_count, "
                + "COALESCE(SUM(i.stock_quantity), 0) AS total_stock, "
                + "COALESCE(SUM(i.reserved_quantity), 0) AS total_reserved, "
                + "COALESCE(SUM(i.damaged_quantity), 0) AS total_damaged "
                + "FROM [categories] c "
                + "LEFT JOIN [parts] p ON p.category_id = c.category_id AND p.status <> 'DISCONTINUED' "
                + "LEFT JOIN [inventory] i ON i.part_id = p.part_id "
                + "GROUP BY c.category_id, c.category_name "
                + "ORDER BY c.category_id ASC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                int stock = rs.getInt("total_stock");
                int reserved = rs.getInt("total_reserved");
                m.put("categoryId", rs.getInt("category_id"));
                m.put("categoryName", rs.getString("category_name"));
                m.put("partCount", rs.getInt("part_count"));
                m.put("totalStock", stock);
                m.put("totalReserved", reserved);
                m.put("availableStock", Math.max(0, stock - reserved));
                m.put("totalDamaged", rs.getInt("total_damaged"));
                rows.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    private void log(Connection con, int partId, String type, int change, int refId, String refType, String reason) throws SQLException {
        int stock = 0;
        try (PreparedStatement ps = con.prepareStatement("SELECT stock_quantity FROM inventory WHERE part_id = ?")) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stock = rs.getInt(1);
                }
            }
        }
        int before = "EXPORT".equals(type) ? stock - change : stock;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO inventory_transactions (part_id, transaction_type, quantity_change, quantity_before, quantity_after, reference_type, reference_id, reason, created_at, status) "
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME(), 'ACTIVE')")) {
            ps.setInt(1, partId);
            ps.setString(2, type);
            ps.setInt(3, change);
            ps.setInt(4, before);
            ps.setInt(5, stock);
            ps.setString(6, refType);
            ps.setInt(7, refId);
            ps.setString(8, reason);
            ps.executeUpdate();
        }
    }
}
