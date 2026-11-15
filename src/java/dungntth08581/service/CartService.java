package dungntth08581.service;

import fourSome.url.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Giỏ hàng (bản gọn, làm hộ Dũng để UC 98/221 "thêm cả cấu hình vào giỏ" và UC 99 "kiểm tra tồn kho cả cấu hình" chạy được).
 * Dũng mở rộng tiếp: mã giảm giá, phí vận chuyển, chuyển sang checkout...
 */
public class CartService {

    public static class CartLine {
        private int cartId, partId, quantity, available;
        private String partName;
        private double unitPrice;
        public int getCartId() { return cartId; }
        public int getPartId() { return partId; }
        public int getQuantity() { return quantity; }
        public int getAvailable() { return available; }
        public String getPartName() { return partName; }
        public double getUnitPrice() { return unitPrice; }
        public double getLineTotal() { return unitPrice * quantity; }
        public boolean isShortage() { return available < quantity; }
    }

    public static class AddResult {
        public boolean ok;
        public String message;
        public List<String> shortages = new ArrayList<>();
    }

    public List<CartLine> getCart(int userId) {
        List<CartLine> list = new ArrayList<>();
        String sql = "SELECT c.cart_id, c.part_id, c.quantity, p.part_name, ISNULL(p.sale_price, p.base_price) AS price, "
                   + "ISNULL(i.stock_quantity - i.reserved_quantity, 0) AS available "
                   + "FROM cart_items c JOIN parts p ON p.part_id = c.part_id LEFT JOIN inventory i ON i.part_id = c.part_id "
                   + "WHERE c.user_id = ? AND c.status = 'ACTIVE' ORDER BY c.added_at DESC, c.cart_id DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartLine l = new CartLine();
                    l.cartId = rs.getInt(1); l.partId = rs.getInt(2); l.quantity = rs.getInt(3);
                    l.partName = rs.getString(4); l.unitPrice = rs.getDouble(5); l.available = rs.getInt(6);
                    list.add(l);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Số dòng trong giỏ (hiện ở header: session "cartCount"). */
    public int countLines(int userId) {
        String sql = "SELECT COUNT(*) FROM cart_items WHERE user_id = ? AND status = 'ACTIVE'";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * UC 98 / 221: thêm toàn bộ linh kiện của cấu hình vào giỏ (món đã có thì cộng dồn số lượng).
     * UC 99: kiểm tra tồn kho cả cấu hình - món thiếu hàng vẫn được thêm nhưng báo trong shortages.
     * Linh kiện đã ngừng bán bị bỏ qua và báo lại.
     */
    public AddResult addBuildToCart(int userId, int buildId) {
        AddResult res = new AddResult();
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                // chỉ chủ cấu hình mới thêm được
                try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM pc_builds WHERE build_id = ? AND user_id = ? AND status IN ('DRAFT','SAVED')")) {
                    ps.setInt(1, buildId); ps.setInt(2, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || rs.getInt(1) == 0) { res.ok = false; res.message = "Không tìm thấy cấu hình của bạn!"; return res; }
                    }
                }
                String sel = "SELECT bd.part_id, bd.quantity, p.part_name, p.status, ISNULL(i.stock_quantity - i.reserved_quantity, 0) "
                           + "FROM pc_build_details bd JOIN parts p ON p.part_id = bd.part_id LEFT JOIN inventory i ON i.part_id = bd.part_id "
                           + "WHERE bd.build_id = ? AND bd.status = 'ACTIVE'";
                int added = 0;
                try (PreparedStatement ps = con.prepareStatement(sel)) {
                    ps.setInt(1, buildId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int partId = rs.getInt(1), qty = rs.getInt(2), avail = rs.getInt(5);
                            String name = rs.getString(3);
                            if (!"ACTIVE".equals(rs.getString(4))) { res.shortages.add(name + " (đã ngừng bán, không thêm)"); continue; }
                            if (avail < qty) res.shortages.add(name + " (còn " + Math.max(avail, 0) + ", cần " + qty + ")");
                            upsert(con, userId, partId, qty);
                            added++;
                        }
                    }
                }
                con.commit();
                res.ok = added > 0;
                res.message = added > 0 ? "Đã thêm " + added + " linh kiện của cấu hình vào giỏ hàng." : "Cấu hình chưa có linh kiện nào để thêm!";
                return res;
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) {
            e.printStackTrace();
            res.ok = false; res.message = "Không thêm được vào giỏ hàng, vui lòng thử lại!";
            return res;
        }
    }

    private void upsert(Connection con, int userId, int partId, int qty) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE cart_items SET quantity = quantity + ?, status = 'ACTIVE', added_at = SYSDATETIME() WHERE user_id = ? AND part_id = ?")) {
            ps.setInt(1, qty); ps.setInt(2, userId); ps.setInt(3, partId);
            if (ps.executeUpdate() > 0) return;
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO cart_items (user_id, part_id, quantity, added_at, status) VALUES (?, ?, ?, SYSDATETIME(), 'ACTIVE')")) {
            ps.setInt(1, userId); ps.setInt(2, partId); ps.setInt(3, qty);
            ps.executeUpdate();
        }
    }

    public boolean updateQuantity(int userId, int cartId, int qty) {
        if (qty < 1 || qty > 99) return false;
        return exec("UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND user_id = ?", qty, cartId, userId);
    }

    public boolean remove(int userId, int cartId) {
        return exec("DELETE FROM cart_items WHERE cart_id = ? AND user_id = ?", cartId, userId);
    }

    private boolean exec(String sql, int... params) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setInt(i + 1, params[i]);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
