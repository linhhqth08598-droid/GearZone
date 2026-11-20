package khanhnb08894.dao;

import fourSome.model.PurchaseOrders;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrdersDAO {

    // Lấy danh sách phiếu nhập (kèm tên Nhà cung cấp bằng JOIN)
    public List<PurchaseOrders> getAll() {
        List<PurchaseOrders> list = new ArrayList<>();
        String sql = "SELECT p.*, s.supplier_name FROM purchase_orders p "
                   + "JOIN suppliers s ON p.supplier_id = s.supplier_id ORDER BY p.po_id DESC";
        
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PurchaseOrders po = new PurchaseOrders();
                po.setPoId(rs.getInt("po_id"));
                po.setPoCode(rs.getString("po_code"));
                po.setSupplierId(rs.getInt("supplier_id"));
                po.setNote(rs.getString("supplier_name")); // Mượn trường note chứa Tên NCC
                po.setTotalCost(rs.getDouble("total_cost"));
                po.setStatus(rs.getString("status"));
                po.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
                po.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(po);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Khởi tạo Đơn nhập hàng mới
    public boolean insert(PurchaseOrders po) {
        String sql = "INSERT INTO purchase_orders (po_code, supplier_id, status, expected_delivery_date, total_cost) VALUES (?, ?, 'PENDING', ?, 0)";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, po.getPoCode());
            ps.setInt(2, po.getSupplierId());
            if (po.getExpectedDeliveryDate() != null) {
                ps.setDate(3, new java.sql.Date(po.getExpectedDeliveryDate().getTime()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Cập nhật trạng thái đơn nhập. Ném lỗi (có thông báo rõ) nếu không đổi được.
     * RECEIVED và CANCELLED là trạng thái cuối: đã cộng kho / đã hủy thì không đổi ngược lại,
     * vì đổi ngược sẽ không trừ lại tồn kho và làm tồn kho bị cộng đôi nếu nhập kho lần nữa.
     */
    public boolean updateStatus(int poId, String status) throws SQLException {
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                String current = null;
                try (PreparedStatement ps = con.prepareStatement("SELECT status FROM purchase_orders WHERE po_id = ?")) {
                    ps.setInt(1, poId);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) current = rs.getString(1); }
                }
                if (current == null) throw new IllegalStateException("Không tìm thấy đơn nhập #" + poId + ".");
                if (current.equalsIgnoreCase(status)) throw new IllegalStateException("Đơn nhập đã ở trạng thái này rồi.");
                if ("RECEIVED".equalsIgnoreCase(current) || "CANCELLED".equalsIgnoreCase(current)) {
                    throw new IllegalStateException("Đơn nhập đã ở trạng thái cuối (Đã nhập kho / Đã hủy) nên không đổi lại được. Cần nhập thêm hàng thì tạo đơn nhập mới.");
                }

                try (PreparedStatement psPo = con.prepareStatement("UPDATE purchase_orders SET status = ? WHERE po_id = ?")) {
                    psPo.setString(1, status);
                    psPo.setInt(2, poId);
                    if (psPo.executeUpdate() == 0) throw new IllegalStateException("Không cập nhật được trạng thái.");
                }

                if ("RECEIVED".equalsIgnoreCase(status)) {
                    // Phần chưa nhập qua "Nhập Serial" thì cộng vào bảng inventory tại đây (phần có serial đã cộng lúc nhập serial)
                    String getDetailsSql = "SELECT part_id, import_quantity, import_quantity - received_quantity AS remain FROM purchase_order_details WHERE po_id = ?";
                    try (PreparedStatement psDetails = con.prepareStatement(getDetailsSql)) {
                        psDetails.setInt(1, poId);
                        try (ResultSet rs = psDetails.executeQuery()) {
                            while (rs.next()) {
                                int partId = rs.getInt("part_id");
                                new khanhnb08894.service.InventoryService().importStock(con, partId, Math.max(rs.getInt("remain"), 0), poId);
                            }
                        }
                    }
                }
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                if (e instanceof IllegalStateException) throw (IllegalStateException) e;
                throw new SQLException("Lỗi khi cập nhật trạng thái: " + e.getMessage(), e);
            }
        }
    }

}
