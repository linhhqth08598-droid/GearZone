package khanhnb08894.service;

import fourSome.model.Brands;
import fourSome.model.Categories;
import fourSome.model.Users;
import java.util.List;
import khanhnb08894.dao.BrandsDAO;
import khanhnb08894.dao.CategoriesDAO;
import linhhqth08598.service.AuditLogService;

public class CatalogService {
    private BrandsDAO brandsDAO = new BrandsDAO();

    // --- Quản lý Thương hiệu ---
    public List<Brands> getAllBrands() {
        return brandsDAO.getAll();
    }

    public String saveBrand(Brands brand, boolean isEdit, Users adminUser) {
        if (brand.getBrandName() == null || brand.getBrandName().trim().isEmpty()) {
            return "Tên thương hiệu không được để trống!";
        }

        int excludeId = isEdit ? brand.getBrandId() : -1;
        if (brandsDAO.checkNameExist(brand.getBrandName(), excludeId)) {
            return "Tên thương hiệu đã tồn tại!";
        }

        boolean success = isEdit ? brandsDAO.update(brand) : brandsDAO.insert(brand);
        
        if (success) {
            AuditLogService auditLog = new AuditLogService();
            String action = isEdit ? "UPDATE_BRAND" : "INSERT_BRAND";
            auditLog.logAction(adminUser.getUserId(), action, "brands", brand.getBrandId(), 
                "Admin đã " + (isEdit ? "cập nhật" : "thêm mới") + " thương hiệu: " + brand.getBrandName());
            return null;
        }
        return "Lỗi hệ thống khi lưu thương hiệu!";
    }

    public String deleteBrand(int brandId, Users adminUser) {
        if (brandsDAO.softDelete(brandId)) {
            AuditLogService auditLog = new AuditLogService();
            auditLog.logAction(adminUser.getUserId(), "DELETE_BRAND", "brands", brandId, "Admin đã ẩn thương hiệu ID: " + brandId);
            return null;
        }
        return "Lỗi hệ thống khi xóa thương hiệu!";
    }
    
        private CategoriesDAO categoriesDAO = new CategoriesDAO();

    // --- Quản lý Danh mục ---
    public List<Categories> getAllCategories() {
        return categoriesDAO.getAll();
    }

    public String saveCategory(Categories cat, boolean isEdit, Users adminUser) {
        if (cat.getCategoryCode() == null || cat.getCategoryCode().trim().isEmpty()) {
            return "Mã danh mục không được để trống!";
        }
        if (cat.getCategoryName() == null || cat.getCategoryName().trim().isEmpty()) {
            return "Tên danh mục không được để trống!";
        }

        int excludeId = isEdit ? cat.getCategoryId() : -1;
        if (categoriesDAO.checkCodeExist(cat.getCategoryCode(), excludeId)) {
            return "Mã danh mục đã tồn tại!";
        }

        boolean success = isEdit ? categoriesDAO.update(cat) : categoriesDAO.insert(cat);
        
        if (success) {
            AuditLogService auditLog = new AuditLogService();
            String action = isEdit ? "UPDATE_CATEGORY" : "INSERT_CATEGORY";
            auditLog.logAction(adminUser.getUserId(), action, "categories", cat.getCategoryId(), 
                "Admin đã " + (isEdit ? "cập nhật" : "thêm mới") + " danh mục: " + cat.getCategoryName());
            return null;
        }
        return "Lỗi hệ thống khi lưu danh mục!";
    }

    public String deleteCategory(int categoryId, Users adminUser) {
        if (categoriesDAO.softDelete(categoryId)) {
            AuditLogService auditLog = new AuditLogService();
            auditLog.logAction(adminUser.getUserId(), "DELETE_CATEGORY", "categories", categoryId, "Admin đã ẩn danh mục ID: " + categoryId);
            return null;
        }
        return "Lỗi hệ thống khi xóa danh mục!";
    }
}
