package Service;

import DAO.ProductDAO;
import Models.Product;
import java.util.List;

public class ProductService {
    private ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    // Đã bổ sung thêm int stock vào tham số
    public boolean saveProduct(String actionType, int productId, String name, double price, int stock, String desc, String base64) {
        // Khởi tạo đối tượng Product khớp hoàn toàn với Constructor có 6 tham số
        Product p = new Product(productId, name, price, stock, desc, base64);
        
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