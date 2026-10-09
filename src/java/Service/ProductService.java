package Service;

import DAO.ProductDAO;
import Models.Product;
import java.util.List;

public class ProductService {
    private ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public boolean saveProduct(String actionType, int productId, String name, double price, String desc, String base64) {
        Product p = new Product(productId, name, price, desc, base64);
        
        if (actionType.equals("add")) {
            return productDAO.insertProduct(p);
        } else if (actionType.equals("edit")) {
            return productDAO.updateProduct(p);
        }
        return false;
    }

    public boolean deleteProduct(int productId) {
        return productDAO.deleteProduct(productId);
    }
}