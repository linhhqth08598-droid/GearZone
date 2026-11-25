package khanhnb08894.service;

import fourSome.model.PurchaseOrderDetails;
import fourSome.model.PurchaseOrders;
import fourSome.model.SupplierReturns;
import fourSome.model.Suppliers;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import khanhnb08894.PurchaseOrderDetailDTO;
import khanhnb08894.dao.PurchaseOrderDetailsDAO;
import khanhnb08894.dao.PurchaseOrdersDAO;
import khanhnb08894.dao.SupplierReturnsDAO;
import khanhnb08894.dao.SuppliersDAO;

/**
 * Nghiệp vụ Nhà cung cấp, Đơn nhập hàng (PO) và Trả hàng Nhà cung cấp (Module E).
 */
public class SupplierService {

    private final SuppliersDAO suppliersDAO = new SuppliersDAO();
    private final PurchaseOrdersDAO purchaseOrdersDAO = new PurchaseOrdersDAO();
    private final PurchaseOrderDetailsDAO purchaseOrderDetailsDAO = new PurchaseOrderDetailsDAO();
    private final SupplierReturnsDAO supplierReturnsDAO = new SupplierReturnsDAO();

    public List<Suppliers> getSuppliers(String keyword, String status) throws SQLException {
        return suppliersDAO.getAll(keyword, status);
    }

    public List<Suppliers> getAllSuppliers() {
        return suppliersDAO.getAll();
    }

    public boolean createSupplier(Suppliers s) throws SQLException {
        if (s == null || isBlank(s.getSupplierName()) || isBlank(s.getPhone())) {
            return false;
        }
        s.setStatus("ACTIVE");
        return suppliersDAO.insertSupplier(s);
    }

    public boolean updateSupplier(Suppliers s) throws SQLException {
        if (s == null || s.getSupplierId() == null || s.getSupplierId() <= 0
                || isBlank(s.getSupplierName()) || isBlank(s.getPhone())) {
            return false;
        }
        if (isBlank(s.getStatus())) {
            s.setStatus("ACTIVE");
        }
        return suppliersDAO.updateSupplier(s);
    }

    public String deleteSupplierSafely(int supplierId) throws SQLException {
        int refs = suppliersDAO.countReferences(supplierId);
        if (refs > 0) {
            return "Không thể xóa: nhà cung cấp này đã có " + refs
                    + " phiếu nhập/trả hàng. Hãy chuyển sang \"Ngừng hợp tác\" bằng nút Sửa.";
        }
        return suppliersDAO.deleteSupplier(supplierId) ? null : "Không tìm thấy nhà cung cấp!";
    }

    public List<PurchaseOrders> getAllPurchaseOrders() {
        return purchaseOrdersDAO.getAll();
    }

    public boolean createPurchaseOrder(String poCode, int supplierId, Date expectedDeliveryDate) {
        if (isBlank(poCode) || supplierId <= 0) {
            return false;
        }
        PurchaseOrders po = new PurchaseOrders();
        po.setPoCode(poCode.trim());
        po.setSupplierId(supplierId);
        po.setExpectedDeliveryDate(expectedDeliveryDate);
        return purchaseOrdersDAO.insert(po);
    }

    public boolean updatePurchaseOrderStatus(int poId, String status) throws SQLException {
        if (poId <= 0 || isBlank(status)) {
            return false;
        }
        String normalized = status.trim().toUpperCase();
        boolean updated = purchaseOrdersDAO.updateStatus(poId, normalized);
        if (updated) {
            stampOrderMilestone(poId, normalized);
        }
        return updated;
    }

    private void stampOrderMilestone(int poId, String status) {
        String col = null;
        if ("CONFIRMED".equals(status)) {
            col = "confirmed_at";
        } else if ("RECEIVED".equals(status) || "PARTIALLY_RECEIVED".equals(status)) {
            col = "received_at";
        }
        if (col == null) {
            return;
        }
        String sql = "UPDATE [purchase_orders] SET " + col + " = COALESCE(" + col + ", SYSDATETIME()) WHERE po_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, poId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<java.util.Map<String, Object>> getSupplierTransactionHistory(Integer supplierId) {
        List<java.util.Map<String, Object>> rows = new java.util.ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT po.po_id, po.po_code, po.supplier_id, s.supplier_name, po.total_cost, "
                + "po.status, po.expected_delivery_date, po.confirmed_at, po.received_at, po.created_at, "
                + "(SELECT COALESCE(SUM(sr.quantity), 0) FROM [supplier_returns] sr WHERE sr.po_id = po.po_id) AS returned_qty "
                + "FROM [purchase_orders] po "
                + "JOIN [suppliers] s ON s.supplier_id = po.supplier_id WHERE 1=1 ");
        List<Object> params = new java.util.ArrayList<>();
        if (supplierId != null && supplierId > 0) {
            sql.append("AND po.supplier_id = ? ");
            params.add(supplierId);
        }
        sql.append("ORDER BY po.po_id DESC");
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("poId", rs.getInt("po_id"));
                    m.put("poCode", rs.getString("po_code"));
                    m.put("supplierId", rs.getInt("supplier_id"));
                    m.put("supplierName", rs.getString("supplier_name"));
                    m.put("totalCost", rs.getDouble("total_cost"));
                    m.put("status", rs.getString("status"));
                    m.put("expectedDeliveryDate", rs.getDate("expected_delivery_date"));
                    m.put("confirmedAt", rs.getTimestamp("confirmed_at"));
                    m.put("receivedAt", rs.getTimestamp("received_at"));
                    m.put("createdAt", rs.getTimestamp("created_at"));
                    m.put("returnedQty", rs.getInt("returned_qty"));
                    rows.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<PurchaseOrderDetailDTO> getPurchaseOrderDetails(int poId) {
        return purchaseOrderDetailsDAO.getDetailsByPoId(poId);
    }

    public boolean addPurchaseOrderDetail(int poId, int partId, int importQuantity, double importUnitPrice) {
        if (poId <= 0 || partId <= 0 || importQuantity <= 0 || importUnitPrice < 0) {
            return false;
        }
        PurchaseOrderDetails detail = new PurchaseOrderDetails();
        detail.setPoId(poId);
        detail.setPartId(partId);
        detail.setImportQuantity(importQuantity);
        detail.setImportUnitPrice(importUnitPrice);
        return purchaseOrderDetailsDAO.insertDetail(detail);
    }

    public boolean removePurchaseOrderDetail(int poDetailId) {
        if (poDetailId <= 0) {
            return false;
        }
        return purchaseOrderDetailsDAO.deleteDetail(poDetailId);
    }

    public List<SupplierReturns> getAllSupplierReturns() {
        return supplierReturnsDAO.getAll();
    }

    public String createSupplierReturn(int poId, int partId, int quantity, String defectType, String reason, Integer createdByUserId) throws SQLException {
        if (poId <= 0 || partId <= 0 || quantity <= 0) {
            throw new IllegalArgumentException("Thông tin phiếu trả hàng không hợp lệ.");
        }
        String normalizedDefect = normalizeDefectType(defectType);
        String returnCode = "RET-" + System.currentTimeMillis();
        int supplierId = resolveSupplierIdFromPo(poId);
        int creatorId = (createdByUserId != null && createdByUserId > 0) ? createdByUserId : resolveFallbackUserId();

        String sql = "INSERT INTO [supplier_returns] (return_code, supplier_id, po_id, part_id, quantity, defect_type, reason, status, created_by, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'PENDING', ?, GETDATE())";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, returnCode);
            ps.setInt(2, supplierId);
            ps.setInt(3, poId);
            ps.setInt(4, partId);
            ps.setInt(5, quantity);
            ps.setString(6, normalizedDefect);
            ps.setString(7, trimOrNull(reason));
            ps.setInt(8, creatorId);
            ps.executeUpdate();
            return returnCode;
        }
    }

    public boolean updateSupplierReturnStatus(int returnId, String nextStatus) throws SQLException {
        if (returnId <= 0 || isBlank(nextStatus)) {
            return false;
        }
        String status = nextStatus.trim().toUpperCase();
        if (!status.matches("PENDING|SENT|COMPLETED|REJECTED")) {
            throw new IllegalArgumentException("Trạng thái phiếu trả hàng không hợp lệ.");
        }
        String sql = "UPDATE [supplier_returns] SET status = ? WHERE supplier_return_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, returnId);
            return ps.executeUpdate() > 0;
        }
    }

    private int resolveSupplierIdFromPo(int poId) throws SQLException {
        String sql = "SELECT supplier_id FROM [purchase_orders] WHERE po_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, poId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("supplier_id");
                }
            }
        }
        throw new IllegalArgumentException("Không tìm thấy đơn nhập #" + poId);
    }

    private int resolveFallbackUserId() {
        String sql = "SELECT TOP 1 user_id FROM [users] ORDER BY user_id ASC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private String normalizeDefectType(String defectType) {
        if (defectType == null) {
            return "DEFECTIVE";
        }
        String d = defectType.trim().toUpperCase();
        if (d.matches("DEFECTIVE|DAMAGED|WRONG_ITEM|MISSING")) {
            return d;
        }
        return "DEFECTIVE";
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String trimOrNull(String s) {
        return isBlank(s) ? null : s.trim();
    }
}
