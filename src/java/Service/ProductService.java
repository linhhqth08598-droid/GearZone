package Service;

import DAO.ProductDAO;
import Models.Product;
import java.util.List;

public class ProductService {
    private ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() { return productDAO.getAllProducts(); }
    public List<Product> getProductsByCategory(int categoryId) { return productDAO.getProductsByCategory(categoryId); }
    public List<Product> getProductsByBrand(int brandId) { return productDAO.getProductsByBrand(brandId); }
    public List<Product> getProductsByPriceRange(double min, double max) { return productDAO.getProductsByPriceRange(min, max); }

    public void saveProduct(String actionType, int productId, int categoryId, int brandId, String productName, double basePrice, int stockQuantity, String description, String imageBase64) {
        if ("add".equals(actionType)) {
            productDAO.insertProduct(categoryId, brandId, productName, basePrice, stockQuantity, description, imageBase64);
        } else if ("edit".equals(actionType)) {
            productDAO.updateProduct(productId, categoryId, brandId, productName, basePrice, stockQuantity, description, imageBase64);
        }
    }

    public void deleteProduct(int productId) {
        productDAO.deleteProduct(productId);
    }
}