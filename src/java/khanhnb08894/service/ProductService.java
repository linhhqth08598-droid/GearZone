package khanhnb08894.service;

import fourSome.model.Parts;
import java.util.Collections;
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
