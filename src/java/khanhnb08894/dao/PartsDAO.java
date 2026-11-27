package khanhnb08894.dao;

import fourSome.model.Parts;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartsDAO {

    /**
     * Hàm phụ trợ dùng chung: tự động gán ảnh chính (từ part_media) cho danh
     * sách linh kiện
     */
    private List<Parts> attachImages(List<Parts> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        java.util.Map<Integer, String> imgMap = new java.util.HashMap<>();
        String imgSql = "SELECT part_id, media_url FROM part_media WHERE status = 'ACTIVE' AND media_type = 'IMAGE' ORDER BY is_primary DESC, media_id DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(imgSql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int partId = rs.getInt(1);
                if (!imgMap.containsKey(partId)) {
                    imgMap.put(partId, rs.getString(2));
                }
            }
        } catch (Exception e) {
            System.err.println("[attachImages] LOI DOC ANH: " + e);
            e.printStackTrace();
        }

        for (Parts p : list) {
            p.setImageUrl(imgMap.get(p.getPartId()));
        }
        return list;
    }

    public List<Parts> getAll() {
        List<Parts> list = new ArrayList<>();
        String sql = "SELECT * FROM [parts] ORDER BY part_id DESC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Parts obj = new Parts();
                obj.setPartId(rs.getInt("part_id"));
                obj.setCategoryId(rs.getInt("category_id"));
                obj.setBrandId(rs.getInt("brand_id"));
                obj.setSeriesId(rs.getInt("series_id"));
                obj.setProductCode(rs.getString("product_code"));
                obj.setPartName(rs.getString("part_name"));
                obj.setBasePrice(rs.getDouble("base_price"));
                double sp = rs.getDouble("sale_price");
                obj.setSalePrice(rs.wasNull() ? null : sp);
                obj.setIsRgb(rs.getBoolean("is_rgb"));
                obj.setGamingPerformanceScore(rs.getInt("gaming_performance_score"));
                obj.setDescription(rs.getString("description"));
                obj.setWarrantyMonths(rs.getInt("warranty_months"));
                obj.setViewCount(rs.getInt("view_count"));
                obj.setSoldCount(rs.getInt("sold_count"));
                obj.setAvgCostPrice(rs.getDouble("avg_cost_price"));
                obj.setStatus(rs.getString("status"));
                obj.setCreatedAt(rs.getDate("created_at"));
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attachImages(list);
    }

    /**
     * Như getAll() nhưng gắn thêm ảnh chính - tương thích với các Servlet khác
     * của nhóm.
     */
    public List<Parts> getAllWithImages() {
        return getAll();
    }

    // Thêm linh kiện mới
    public boolean insert(Parts part) {
        String sql = "INSERT INTO parts (category_id, brand_id, product_code, part_name, base_price, sale_price, is_rgb, gaming_performance_score, description, warranty_months, view_count, sold_count, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, part.getCategoryId());
            ps.setInt(2, part.getBrandId());
            ps.setString(3, part.getProductCode());
            ps.setString(4, part.getPartName());
            ps.setDouble(5, part.getBasePrice());

            if (part.getSalePrice() != null) {
                ps.setDouble(6, part.getSalePrice());
            } else {
                ps.setNull(6, java.sql.Types.DECIMAL);
            }

            ps.setBoolean(7, part.getIsRgb() != null ? part.getIsRgb() : false);
            ps.setInt(8, part.getGamingPerformanceScore() != null ? part.getGamingPerformanceScore() : 0);
            ps.setString(9, part.getDescription());
            ps.setInt(10, part.getWarrantyMonths());
            ps.setInt(11, part.getViewCount() != null ? part.getViewCount() : 0);
            ps.setInt(12, part.getSoldCount() != null ? part.getSoldCount() : 0);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Ẩn linh kiện (Xóa mềm)
    public boolean softDelete(int partId) {
        String sql = "UPDATE parts SET status = 'HIDDEN' WHERE part_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private volatile String lastError = "";

    public String getLastError() {
        return lastError;
    }

    // Cập nhật thông tin linh kiện
    public boolean update(Parts part) {
        lastError = "";
        String sql = "UPDATE parts SET category_id = ?, brand_id = ?, product_code = ?, part_name = ?, "
                + "base_price = ?, sale_price = ?, description = ?, warranty_months = ? WHERE part_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, part.getCategoryId());
            ps.setInt(2, part.getBrandId());
            ps.setString(3, part.getProductCode());
            ps.setString(4, part.getPartName());
            ps.setBigDecimal(5, part.getBasePrice() != null ? java.math.BigDecimal.valueOf(part.getBasePrice()) : java.math.BigDecimal.ZERO);
            if (part.getSalePrice() != null) {
                ps.setBigDecimal(6, java.math.BigDecimal.valueOf(part.getSalePrice()));
            } else {
                ps.setNull(6, java.sql.Types.DECIMAL);
            }
            ps.setString(7, part.getDescription());
            ps.setInt(8, part.getWarrantyMonths());
            ps.setInt(9, part.getPartId());
            int rows = ps.executeUpdate();
            System.err.println("[PartsDAO.update] part_id=" + part.getPartId()
                    + ", basePrice=" + part.getBasePrice()
                    + ", salePrice=" + part.getSalePrice()
                    + ", rowsUpdated=" + rows);
            if (rows <= 0) {
                lastError = "Không có bản ghi nào được cập nhật cho part_id=" + part.getPartId();
                System.err.println("[PartsDAO.update] " + lastError);
            }
            return rows > 0;
        } catch (Exception e) {
            lastError = e.getMessage() != null ? e.getMessage() : e.toString();
            System.err.println("[PartsDAO.update] Lỗi cập nhật part_id=" + part.getPartId() + ": " + lastError);
            e.printStackTrace();
            return false;
        }
    }

    // Đổi trạng thái ACTIVE <-> HIDDEN
    public boolean setStatus(int partId, String status) {
        String sql = "UPDATE parts SET status = ? WHERE part_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm sản phẩm theo ID
    public Parts findById(int partId) {
        String sql = "SELECT * FROM parts WHERE part_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Parts obj = new Parts();
                    obj.setPartId(rs.getInt("part_id"));
                    obj.setCategoryId(rs.getInt("category_id"));
                    obj.setBrandId(rs.getInt("brand_id"));
                    obj.setSeriesId(rs.getInt("series_id"));
                    obj.setProductCode(rs.getString("product_code"));
                    obj.setPartName(rs.getString("part_name"));
                    obj.setBasePrice(rs.getDouble("base_price"));
                    double sp = rs.getDouble("sale_price");
                    obj.setSalePrice(rs.wasNull() ? null : sp);
                    obj.setIsRgb(rs.getBoolean("is_rgb"));
                    obj.setGamingPerformanceScore(rs.getInt("gaming_performance_score"));
                    obj.setDescription(rs.getString("description"));
                    obj.setWarrantyMonths(rs.getInt("warranty_months"));
                    obj.setViewCount(rs.getInt("view_count"));
                    obj.setSoldCount(rs.getInt("sold_count"));
                    obj.setAvgCostPrice(rs.getDouble("avg_cost_price"));
                    obj.setStatus(rs.getString("status"));
                    obj.setCreatedAt(rs.getDate("created_at"));

                    // Gán ảnh cho sản phẩm tìm được theo ID
                    List<Parts> singleList = new ArrayList<>();
                    singleList.add(obj);
                    attachImages(singleList);
                    return obj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lọc và tìm kiếm linh kiện (Dùng chung cho trang chủ và các trang danh sách)
    public List<Parts> filterParts(Integer categoryId, Integer brandId, double minPrice, double maxPrice, String keyword) {
        List<Parts> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM parts WHERE status = 'ACTIVE'");
        List<Object> params = new ArrayList<>();

        if (categoryId != null) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }
        if (brandId != null) {
            sql.append(" AND brand_id = ?");
            params.add(brandId);
        }
        if (minPrice >= 0 && maxPrice > minPrice) {
            sql.append(" AND ISNULL(sale_price, base_price) BETWEEN ? AND ?");
            params.add(minPrice);
            params.add(maxPrice);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (part_name LIKE ? OR product_code LIKE ?)");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }
        sql.append(" ORDER BY part_id DESC");

        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Parts obj = new Parts();
                    obj.setPartId(rs.getInt("part_id"));
                    obj.setCategoryId(rs.getInt("category_id"));
                    obj.setBrandId(rs.getInt("brand_id"));
                    obj.setSeriesId(rs.getInt("series_id"));
                    obj.setProductCode(rs.getString("product_code"));
                    obj.setPartName(rs.getString("part_name"));
                    obj.setBasePrice(rs.getDouble("base_price"));
                    double sp = rs.getDouble("sale_price");
                    obj.setSalePrice(rs.wasNull() ? null : sp);
                    obj.setIsRgb(rs.getBoolean("is_rgb"));
                    obj.setGamingPerformanceScore(rs.getInt("gaming_performance_score"));
                    obj.setDescription(rs.getString("description"));
                    obj.setWarrantyMonths(rs.getInt("warranty_months"));
                    obj.setViewCount(rs.getInt("view_count"));
                    obj.setSoldCount(rs.getInt("sold_count"));
                    obj.setAvgCostPrice(rs.getDouble("avg_cost_price"));
                    obj.setStatus(rs.getString("status"));
                    obj.setCreatedAt(rs.getDate("created_at"));
                    list.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachImages(list);
    }

    // Tra cứu linh kiện theo mã sản phẩm
    public Parts getPartByProductCode(String productCode) {
        String sql = "SELECT * FROM parts WHERE product_code = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Parts obj = new Parts();
                    obj.setPartId(rs.getInt("part_id"));
                    obj.setCategoryId(rs.getInt("category_id"));
                    obj.setBrandId(rs.getInt("brand_id"));
                    obj.setSeriesId(rs.getInt("series_id"));
                    obj.setProductCode(rs.getString("product_code"));
                    obj.setPartName(rs.getString("part_name"));
                    obj.setBasePrice(rs.getDouble("base_price"));
                    double sp = rs.getDouble("sale_price");
                    obj.setSalePrice(rs.wasNull() ? null : sp);
                    obj.setIsRgb(rs.getBoolean("is_rgb"));
                    obj.setGamingPerformanceScore(rs.getInt("gaming_performance_score"));
                    obj.setDescription(rs.getString("description"));
                    obj.setWarrantyMonths(rs.getInt("warranty_months"));
                    obj.setViewCount(rs.getInt("view_count"));
                    obj.setSoldCount(rs.getInt("sold_count"));
                    obj.setAvgCostPrice(rs.getDouble("avg_cost_price"));
                    obj.setStatus(rs.getString("status"));
                    obj.setCreatedAt(rs.getDate("created_at"));

                    List<Parts> singleList = new ArrayList<>();
                    singleList.add(obj);
                    attachImages(singleList);
                    return obj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm linh kiện mới và trả về part_id tự sinh
    public int insertAndReturnId(Parts part) {
        String sql = "INSERT INTO parts (category_id, brand_id, product_code, part_name, base_price, sale_price, is_rgb, gaming_performance_score, description, warranty_months, view_count, sold_count, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, part.getCategoryId());
            ps.setInt(2, part.getBrandId());
            ps.setString(3, part.getProductCode());
            ps.setString(4, part.getPartName());
            ps.setDouble(5, part.getBasePrice());

            if (part.getSalePrice() != null) {
                ps.setDouble(6, part.getSalePrice());
            } else {
                ps.setNull(6, java.sql.Types.DECIMAL);
            }

            ps.setBoolean(7, part.getIsRgb() != null ? part.getIsRgb() : false);
            ps.setInt(8, part.getGamingPerformanceScore() != null ? part.getGamingPerformanceScore() : 0);
            ps.setString(9, part.getDescription());
            ps.setInt(10, part.getWarrantyMonths());
            ps.setInt(11, part.getViewCount() != null ? part.getViewCount() : 0);
            ps.setInt(12, part.getSoldCount() != null ? part.getSoldCount() : 0);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public boolean delete(int partId) {
        // Danh sách các bảng thông số chuyên sâu có thể chứa khóa ngoại trỏ tới part_id
        String[] specTables = {
            "part_media", "spec_cpu", "spec_mainboard", "spec_ram", 
            "spec_vga", "spec_storage", "spec_psu", "spec_case", 
            "spec_cooler", "spec_monitor", "spec_gear", "spec_peripheral"
        };
        
        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Xóa sạch dữ liệu liên quan ở tất cả các bảng phụ trước
                for (String table : specTables) {
                    try (PreparedStatement psSpec = conn.prepareStatement("DELETE FROM " + table + " WHERE part_id = ?")) {
                        psSpec.setInt(1, partId);
                        psSpec.executeUpdate();
                    } catch (Exception ex) {
                        // Bỏ qua nếu bảng nào không tồn tại trong DB của nhóm
                    }
                }
                
                // 2. Cuối cùng tiến hành xóa linh kiện chính trong bảng parts
                try (PreparedStatement psPart = conn.prepareStatement("DELETE FROM parts WHERE part_id = ?")) {
                    psPart.setInt(1, partId);
                    int rows = psPart.executeUpdate();
                    conn.commit();
                    return rows > 0;
                }
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Lọc và Tìm kiếm linh kiện dành riêng cho Admin
    public List<Parts> filterAdminParts(Integer categoryId, Integer brandId, String keyword) {
        List<Parts> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM parts WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (categoryId != null && categoryId > 0) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }
        if (brandId != null && brandId > 0) {
            sql.append(" AND brand_id = ?");
            params.add(brandId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (part_name LIKE ? OR product_code LIKE ?)");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }
        sql.append(" ORDER BY part_id DESC");

        try (Connection con = DBConnect.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Parts obj = new Parts();
                    obj.setPartId(rs.getInt("part_id"));
                    obj.setCategoryId(rs.getInt("category_id"));
                    obj.setBrandId(rs.getInt("brand_id"));
                    obj.setProductCode(rs.getString("product_code"));
                    obj.setPartName(rs.getString("part_name"));
                    obj.setBasePrice(rs.getDouble("base_price"));
                    double sp = rs.getDouble("sale_price");
                    obj.setSalePrice(rs.wasNull() ? null : sp);
                    obj.setWarrantyMonths(rs.getInt("warranty_months"));
                    obj.setStatus(rs.getString("status"));
                    list.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Gọi hàm attachImages để tự động gắn ảnh vào danh sách lọc
        return attachImages(list); 
    }
}
