package khanhnb08894.service;

import fourSome.model.Parts;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import khanhnb08894.dao.PartsDAO;

/**
 * Tầng nghiệp vụ sản phẩm / linh kiện cho package khanhnb08894
 * và các module liên quan của nhóm.
 */
public class ProductService {

    private final PartsDAO partsDAO = new PartsDAO();
    private final SpecService specService = new SpecService();

    public Parts findById(int partId) {
        if (partId <= 0) {
            return null;
        }
        return partsDAO.findById(partId);
    }

    public Parts findByProductCode(String productCode) {
        if (productCode == null || productCode.trim().isEmpty()) {
            return null;
        }
        return partsDAO.getPartByProductCode(productCode.trim());
    }

    public List<Parts> getAllWithImages() {
        return partsDAO.getAllWithImages();
    }

    public List<Parts> search(Integer categoryId, Integer brandId, Double minPrice, Double maxPrice, String keyword) {
        double min = minPrice != null && minPrice >= 0 ? minPrice : 0;
        double max = maxPrice != null && maxPrice > min ? maxPrice : 0;
        return partsDAO.filterParts(categoryId, brandId, min, max, keyword);
    }

    public List<Parts> filterAndSortParts(
            Integer categoryId,
            Integer brandId,
            Double minPrice,
            Double maxPrice,
            String keyword,
            boolean inStockOnly,
            boolean saleOnly,
            Double minRating,
            String socket,
            String chipset,
            String ramDdr,
            Integer ramCapacityGb,
            Integer minVramGb,
            String sortBy) {

        StringBuilder sql = new StringBuilder(
                "SELECT p.*, "
                + "COALESCE(r.avg_rating, 0.0) AS avg_rating, "
                + "COALESCE(inv.avail_qty, 0) AS avail_qty "
                + "FROM [parts] p "
                + "LEFT JOIN ( "
                + "  SELECT part_id, AVG(CAST(quality_rating AS FLOAT)) AS avg_rating "
                + "  FROM [part_reviews] WHERE status = 'VISIBLE' GROUP BY part_id "
                + ") r ON r.part_id = p.part_id "
                + "LEFT JOIN ( "
                + "  SELECT part_id, SUM(CASE WHEN stock_quantity - reserved_quantity > 0 THEN stock_quantity - reserved_quantity ELSE 0 END) AS avail_qty "
                + "  FROM [inventory] WHERE status = 'ACTIVE' GROUP BY part_id "
                + ") inv ON inv.part_id = p.part_id "
                + "LEFT JOIN [spec_cpu] scpu ON scpu.part_id = p.part_id "
                + "LEFT JOIN [socket_types] sk_cpu ON sk_cpu.socket_id = scpu.socket_id "
                + "LEFT JOIN [spec_mainboard] smain ON smain.part_id = p.part_id "
                + "LEFT JOIN [socket_types] sk_main ON sk_main.socket_id = smain.socket_id "
                + "LEFT JOIN [spec_ram] sram ON sram.part_id = p.part_id "
                + "LEFT JOIN [spec_vga] svga ON svga.part_id = p.part_id "
                + "WHERE p.status = 'ACTIVE' ");

        List<Object> params = new ArrayList<>();

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        if (brandId != null && brandId > 0) {
            sql.append("AND p.brand_id = ? ");
            params.add(brandId);
        }
        if (minPrice != null && minPrice > 0) {
            sql.append("AND COALESCE(p.sale_price, p.base_price) >= ? ");
            params.add(minPrice);
        }
        if (maxPrice != null && maxPrice > 0) {
            sql.append("AND COALESCE(p.sale_price, p.base_price) <= ? ");
            params.add(maxPrice);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            sql.append("AND (p.part_name LIKE ? OR p.product_code LIKE ? OR p.description LIKE ?) ");
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        if (inStockOnly) {
            sql.append("AND COALESCE(inv.avail_qty, 0) > 0 ");
        }
        if (saleOnly) {
            sql.append("AND p.sale_price IS NOT NULL AND p.sale_price > 0 AND p.sale_price < p.base_price ");
        }
        if (minRating != null && minRating > 0) {
            sql.append("AND COALESCE(r.avg_rating, 0.0) >= ? ");
            params.add(minRating);
        }
        if (socket != null && !socket.trim().isEmpty()) {
            String sk = "%" + socket.trim() + "%";
            sql.append("AND (sk_cpu.socket_name LIKE ? OR sk_main.socket_name LIKE ?) ");
            params.add(sk);
            params.add(sk);
        }
        if (chipset != null && !chipset.trim().isEmpty()) {
            String cs = "%" + chipset.trim() + "%";
            sql.append("AND (smain.chipset LIKE ? OR svga.gpu_chipset LIKE ?) ");
            params.add(cs);
            params.add(cs);
        }
        if (ramDdr != null && !ramDdr.trim().isEmpty()) {
            String ddr = ramDdr.trim();
            sql.append("AND (UPPER(sram.ddr_type) = UPPER(?) OR UPPER(smain.ram_standard) = UPPER(?) OR UPPER(scpu.supported_ram_type) LIKE UPPER(?)) ");
            params.add(ddr);
            params.add(ddr);
            params.add("%" + ddr + "%");
        }
        if (ramCapacityGb != null && ramCapacityGb > 0) {
            sql.append("AND sram.capacity_gb = ? ");
            params.add(ramCapacityGb);
        }
        if (minVramGb != null && minVramGb > 0) {
            sql.append("AND svga.vram_capacity_gb >= ? ");
            params.add(minVramGb);
        }

        // Whitelist cột sắp xếp để tránh SQL injection
        String orderBy = resolveOrderBy(sortBy);
        sql.append("ORDER BY ").append(orderBy);

        List<Parts> list = new ArrayList<>();
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
                    int serId = rs.getInt("series_id");
                    obj.setSeriesId(rs.wasNull() ? null : serId);
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
        return attachPrimaryImages(list);
    }

    private String resolveOrderBy(String sortBy) {
        if (sortBy == null) {
            return "p.part_id DESC";
        }
        switch (sortBy.trim().toLowerCase()) {
            case "price_asc":
                return "COALESCE(p.sale_price, p.base_price) ASC, p.part_id DESC";
            case "price_desc":
                return "COALESCE(p.sale_price, p.base_price) DESC, p.part_id DESC";
            case "perf_desc":
                return "COALESCE(p.gaming_performance_score, 0) DESC, p.part_id DESC";
            case "popular":
                return "COALESCE(p.sold_count, 0) DESC, COALESCE(p.view_count, 0) DESC, p.part_id DESC";
            default:
                return "p.part_id DESC";
        }
    }

    private List<Parts> attachPrimaryImages(List<Parts> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        Map<Integer, String> imgMap = new HashMap<>();
        String imgSql = "SELECT part_id, media_url FROM [part_media] "
                + "WHERE status = 'ACTIVE' AND media_type = 'IMAGE' "
                + "ORDER BY is_primary DESC, media_id DESC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(imgSql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int pid = rs.getInt(1);
                if (!imgMap.containsKey(pid)) {
                    imgMap.put(pid, rs.getString(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Parts p : list) {
            p.setImageUrl(imgMap.get(p.getPartId()));
        }
        return list;
    }

    public List<Parts> filterAdminParts(Integer categoryId, Integer brandId, String keyword) {
        return partsDAO.filterAdminParts(categoryId, brandId, keyword);
    }

    public List<String[]> getGeneralSpec(int partId) {
        if (partId <= 0) {
            return Collections.emptyList();
        }
        return specService.getGeneralInfo(partId);
    }

    public List<String[]> getTechSpecs(int partId) {
        if (partId <= 0) {
            return Collections.emptyList();
        }
        return specService.getTechSpecs(partId);
    }

    public Map<Integer, String> getSpecSummaries() {
        return specService.getSummaries();
    }

    public boolean addPart(Parts part) {
        if (part == null || part.getPartName() == null || part.getPartName().trim().isEmpty()) {
            return false;
        }
        return partsDAO.insertAndReturnId(part) > 0;
    }

    public boolean updatePart(Parts part) {
        if (part == null || part.getPartId() == null || part.getPartId() <= 0) {
            return false;
        }
        return partsDAO.update(part);
    }

    public boolean setStatus(int partId, String status) {
        if (partId <= 0 || status == null || status.trim().isEmpty()) {
            return false;
        }
        return partsDAO.setStatus(partId, status.trim());
    }
}
