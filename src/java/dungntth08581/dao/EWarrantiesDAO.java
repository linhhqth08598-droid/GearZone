package dungntth08581.dao;

import fourSome.model.EWarranties;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EWarrantiesDAO {

    private EWarranties mapRow(ResultSet rs) throws SQLException {
        EWarranties w = new EWarranties();
        w.setWarrantyId(rs.getInt("warranty_id"));
        w.setWarrantyCode(rs.getString("warranty_code"));
        w.setSerialNumber(rs.getString("serial_number"));
        w.setPartId(rs.getInt("part_id"));
        w.setOrderId(rs.getInt("order_id"));
        w.setStartDate(rs.getTimestamp("start_date"));
        w.setEndDate(rs.getTimestamp("end_date"));
        w.setStatus(rs.getString("status"));
        w.setDocumentUrl(rs.getString("document_url"));
        return w;
    }

    public List<EWarranties> getAll() {
        List<EWarranties> list = new ArrayList<>();
        String sql = "SELECT * FROM e_warranties ORDER BY warranty_id DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public EWarranties findById(int warrantyId) {
        String sql = "SELECT * FROM e_warranties WHERE warranty_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warrantyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EWarranties findBySerialNumber(String serialNumber) {
        String sql = "SELECT * FROM e_warranties WHERE serial_number = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serialNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<EWarranties> findByOrderId(int orderId) {
        List<EWarranties> list = new ArrayList<>();
        String sql = "SELECT * FROM e_warranties WHERE order_id = ? ORDER BY warranty_id ASC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tra cứu chính sách bảo hành chung của sản phẩm theo mã SP (product_code)
     */
    public HashMap<String, Object> findPartWarrantyPolicy(String productCode) {
        String sql = "SELECT p.part_id, p.product_code, p.part_name, p.warranty_months, c.category_name, b.brand_name "
                + "FROM parts p "
                + "LEFT JOIN categories c ON p.category_id = c.category_id "
                + "LEFT JOIN brands b ON p.brand_id = b.brand_id "
                + "WHERE p.product_code = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("partId", rs.getInt("part_id"));
                    row.put("productCode", rs.getString("product_code"));
                    row.put("partName", rs.getString("part_name"));
                    row.put("warrantyMonths", rs.getInt("warranty_months"));
                    row.put("categoryName", rs.getString("category_name"));
                    row.put("brandName", rs.getString("brand_name"));
                    return row;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * UC 273 & 274: Tra cứu bảo hành công khai theo mã bảo hành, số serial hoặc
     * mã sản phẩm (product_code)
     */
    public List<HashMap<String, Object>> searchPublic(String keyword) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        String sql = "SELECT w.*, p.part_name, p.product_code, p.warranty_months, o.order_code, o.user_id "
                + "FROM e_warranties w "
                + "JOIN parts p ON w.part_id = p.part_id "
                + "JOIN orders o ON w.order_id = o.order_id "
                + "WHERE w.serial_number = ? OR w.warranty_code = ? OR p.product_code = ? "
                + "ORDER BY w.start_date DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = keyword.trim();
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDetailRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách phiếu bảo hành điện tử của riêng 1 khách hàng
     */
    public List<HashMap<String, Object>> findByUserId(int userId) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        String sql = "SELECT w.*, p.part_name, p.product_code, p.warranty_months, o.order_code, o.user_id "
                + "FROM e_warranties w "
                + "JOIN parts p ON w.part_id = p.part_id "
                + "JOIN orders o ON w.order_id = o.order_id "
                + "WHERE o.user_id = ? "
                + "ORDER BY w.start_date DESC";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDetailRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(Connection conn, EWarranties w) throws SQLException {
        String sql = "INSERT INTO e_warranties (warranty_code, serial_number, part_id, order_id, start_date, end_date, status, document_url) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, w.getWarrantyCode());
            ps.setString(2, w.getSerialNumber());
            ps.setInt(3, w.getPartId());
            ps.setInt(4, w.getOrderId());
            ps.setTimestamp(5, new Timestamp(w.getStartDate().getTime()));
            ps.setTimestamp(6, new Timestamp(w.getEndDate().getTime()));
            ps.setString(7, w.getStatus());
            ps.setString(8, w.getDocumentUrl());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updateSerialNumber(Connection conn, int warrantyId, String newSerial) throws SQLException {
        String sql = "UPDATE e_warranties SET serial_number = ? WHERE warranty_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newSerial);
            ps.setInt(2, warrantyId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int warrantyId, String status) {
        String sql = "UPDATE e_warranties SET status = ? WHERE warranty_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, warrantyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UC 271: Thiết lập thời hạn bảo hành theo loại linh kiện (category)
     */
    public int updateWarrantyMonthsByCategory(int categoryId, int warrantyMonths) throws SQLException {
        String sql = "UPDATE parts SET warranty_months = ? WHERE category_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warrantyMonths);
            ps.setInt(2, categoryId);
            return ps.executeUpdate();
        }
    }

    /**
     * UC 271: Thiết lập thời hạn bảo hành theo từng linh kiện cụ thể
     */
    public boolean updateWarrantyMonthsByPart(int partId, int warrantyMonths) throws SQLException {
        String sql = "UPDATE parts SET warranty_months = ? WHERE part_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warrantyMonths);
            ps.setInt(2, partId);
            return ps.executeUpdate() > 0;
        }
    }

    private HashMap<String, Object> mapDetailRow(ResultSet rs) throws SQLException {
        HashMap<String, Object> row = new HashMap<>();
        row.put("warrantyId", rs.getInt("warranty_id"));
        row.put("warrantyCode", rs.getString("warranty_code"));
        row.put("serialNumber", rs.getString("serial_number"));
        row.put("partId", rs.getInt("part_id"));
        row.put("partName", rs.getString("part_name"));
        row.put("productCode", rs.getString("product_code"));
        row.put("warrantyMonths", rs.getInt("warranty_months"));
        row.put("orderId", rs.getInt("order_id"));
        row.put("orderCode", rs.getString("order_code"));
        row.put("userId", rs.getInt("user_id"));
        row.put("startDate", rs.getTimestamp("start_date"));
        row.put("endDate", rs.getTimestamp("end_date"));
        row.put("status", rs.getString("status"));
        row.put("documentUrl", rs.getString("document_url"));
        return row;
    }
}
