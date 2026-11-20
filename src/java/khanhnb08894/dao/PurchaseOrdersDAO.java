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

    // Cập nhật trạng thái đơn hàng
    public boolean updateStatus(int poId, String status) {
        String updatePoSql = "UPDATE purchase_orders SET status = ? WHERE po_id = ?";
        
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false); // Bật Transaction để đảm bảo an toàn dữ liệu
            
            try (PreparedStatement psPo = con.prepareStatement(updatePoSql)) {
                psPo.setString(1, status);
                psPo.setInt(2, poId);
                int result = psPo.executeUpdate();
                
                if (result > 0) {
                    // Nếu trạng thái chuyển thành RECEIVED -> Cộng kho tự động
                    if ("RECEIVED".equalsIgnoreCase(status)) {
                        String getDetailsSql = "SELECT part_id, import_quantity, import_quantity - received_quantity AS remain FROM purchase_order_details WHERE po_id = ?";
                        String updateStockSql = "UPDATE parts SET stock_quantity = stock_quantity + ? WHERE part_id = ?"; 
                        // Lưu ý: Nếu cột số lượng tồn kho trong bảng parts của team bạn tên khác (ví dụ: quantity hoặc inventory), bạn sửa lại cho khớp nhé!
                        
                        try (PreparedStatement psDetails = con.prepareStatement(getDetailsSql)) {
                            psDetails.setInt(1, poId);
                            try (ResultSet rs = psDetails.executeQuery()) {
                                while (rs.next()) {
                                    int partId = rs.getInt("part_id");
                                    int qty = rs.getInt("import_quantity");
                                    // Phần chưa nhập qua "Nhập Serial" thì cộng vào bảng inventory tại đây (phần có serial đã cộng lúc nhập serial)
                                    new khanhnb08894.service.InventoryService().importStock(con, partId, Math.max(rs.getInt("remain"), 0), poId);
                                    
                                    try (PreparedStatement psStock = con.prepareStatement(updateStockSql)) {
                                        psStock.setInt(1, qty);
                                        psStock.setInt(2, partId);
                                        psStock.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                    con.commit();
                    return true;
                }
                con.rollback();
            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    
}