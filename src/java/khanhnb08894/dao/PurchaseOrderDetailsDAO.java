package khanhnb08894.dao;

import fourSome.model.PurchaseOrderDetails;
import khanhnb08894.PurchaseOrderDetailDTO;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDetailsDAO {

    // Lấy chi tiết đơn hàng (Dùng DTO JOIN sang bảng parts để lấy Tên và SKU)
    public List<PurchaseOrderDetailDTO> getDetailsByPoId(int poId) {
        List<PurchaseOrderDetailDTO> list = new ArrayList<>();
        String sql = "SELECT pod.*, p.product_code, p.part_name FROM purchase_order_details pod "
                   + "JOIN parts p ON pod.part_id = p.part_id "
                   + "WHERE pod.po_id = ?";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, poId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrderDetailDTO dto = new PurchaseOrderDetailDTO();
                    dto.setPoDetailId(rs.getInt("po_detail_id"));
                    dto.setPoId(rs.getInt("po_id"));
                    dto.setPartId(rs.getInt("part_id"));
                    dto.setImportQuantity(rs.getInt("import_quantity"));
                    dto.setImportUnitPrice(rs.getDouble("import_unit_price"));
                    dto.setReceivedQuantity(rs.getInt("received_quantity"));
                    dto.setMissingQuantity(rs.getInt("missing_quantity"));
                    dto.setWrongItemQuantity(rs.getInt("wrong_item_quantity"));
                    dto.setDamagedQuantity(rs.getInt("damaged_quantity"));
                    dto.setStatus(rs.getString("status"));
                    
                    dto.setProductCode(rs.getString("product_code"));
                    dto.setPartName(rs.getString("part_name"));
                    
                    list.add(dto);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Thêm linh kiện vào đơn nhập và tự động cập nhật lại tổng tiền của PO
    public boolean insertDetail(PurchaseOrderDetails detail) {
        String sql = "INSERT INTO purchase_order_details (po_id, part_id, import_quantity, import_unit_price, received_quantity, missing_quantity, wrong_item_quantity, damaged_quantity, status) "
                   + "VALUES (?, ?, ?, ?, 0, 0, 0, 0, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, detail.getPoId());
            ps.setInt(2, detail.getPartId());
            ps.setInt(3, detail.getImportQuantity());
            ps.setDouble(4, detail.getImportUnitPrice());
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                updatePoTotalCost(detail.getPoId(), con); // Tự động cập nhật tổng tiền
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Xóa linh kiện khỏi đơn nhập và tự động cập nhật lại tổng tiền của PO
    public boolean deleteDetail(int poDetailId) {
        int poId = getPoIdByDetailId(poDetailId); // Lấy poId trước khi xóa
        String sql = "DELETE FROM purchase_order_details WHERE po_detail_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, poDetailId);
            int rows = ps.executeUpdate();
            if (rows > 0 && poId != -1) {
                updatePoTotalCost(poId, con); // Tự động cập nhật lại tổng tiền sau khi xóa
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Hàm nội bộ: Tính tổng tiền từ các chi tiết và update thẳng vào bảng purchase_orders
    private void updatePoTotalCost(int poId, Connection con) {
        String calcSql = "SELECT SUM(import_quantity * import_unit_price) AS total FROM purchase_order_details WHERE po_id = ?";
        String updateSql = "UPDATE purchase_orders SET total_cost = ? WHERE po_id = ?";
        
        try (PreparedStatement psCalc = con.prepareStatement(calcSql)) {
            psCalc.setInt(1, poId);
            try (ResultSet rs = psCalc.executeQuery()) {
                double total = 0;
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
                try (PreparedStatement psUpdate = con.prepareStatement(updateSql)) {
                    psUpdate.setDouble(1, total);
                    psUpdate.setInt(2, poId);
                    psUpdate.executeUpdate();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Hàm phụ trợ lấy poId từ poDetailId
    private int getPoIdByDetailId(int poDetailId) {
        String sql = "SELECT po_id FROM purchase_order_details WHERE po_detail_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, poDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("po_id");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }
}