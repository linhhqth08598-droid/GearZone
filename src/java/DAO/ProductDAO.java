package DAO;

import Models.Product;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDAO {

    // Lấy toàn bộ
    public List<Product> getAllProducts() {
        return fetchProducts("SELECT p.ProductID, p.ProductName, p.BasePrice, p.StockQuantity, p.ShortDescription, i.ImageBase64 FROM Products p LEFT JOIN Product_Images i ON p.ProductID = i.ProductID");
    }

    // Lọc theo Category
    public List<Product> getProductsByCategory(int categoryId) {
        return fetchProducts("SELECT p.ProductID, p.ProductName, p.BasePrice, p.StockQuantity, p.ShortDescription, i.ImageBase64 FROM Products p LEFT JOIN Product_Images i ON p.ProductID = i.ProductID WHERE p.CategoryID = " + categoryId);
    }

    // Lọc theo Brand
    public List<Product> getProductsByBrand(int brandId) {
        return fetchProducts("SELECT p.ProductID, p.ProductName, p.BasePrice, p.StockQuantity, p.ShortDescription, i.ImageBase64 FROM Products p LEFT JOIN Product_Images i ON p.ProductID = i.ProductID WHERE p.BrandID = " + brandId);
    }

    // Lọc theo Giá
    public List<Product> getProductsByPriceRange(double min, double max) {
        return fetchProducts("SELECT p.ProductID, p.ProductName, p.BasePrice, p.StockQuantity, p.ShortDescription, i.ImageBase64 FROM Products p LEFT JOIN Product_Images i ON p.ProductID = i.ProductID WHERE p.BasePrice >= " + min + " AND p.BasePrice <= " + max);
    }

    // Hàm phụ trợ duyệt List
    private List<Product> fetchProducts(String sql) {
        List<Product> list = new ArrayList<>();
        // SỬA CÂU LỆNH SQL: Thêm p.CategoryID và p.BrandID
        String query = sql.replace("SELECT p.ProductID, p.ProductName", "SELECT p.ProductID, p.CategoryID, p.BrandID, p.ProductName");
        
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("ProductID"));
                p.setCategoryId(rs.getInt("CategoryID")); // Thêm dòng này
                p.setBrandId(rs.getInt("BrandID"));       // Thêm dòng này
                p.setProductName(rs.getString("ProductName"));
                p.setBasePrice(rs.getDouble("BasePrice"));
                p.setStockQuantity(rs.getInt("StockQuantity"));
                p.setDescription(rs.getString("ShortDescription"));
                p.setImageBase64(rs.getString("ImageBase64"));
                list.add(p);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Thêm Sản phẩm có chọn Category và Brand
    public boolean insertProduct(int categoryId, int brandId, String name, double price, int stock, String desc, String base64) {
        String sqlProduct = "INSERT INTO Products (CategoryID, BrandID, ProductName, SKU, BasePrice, StockQuantity, ShortDescription) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlImage = "INSERT INTO Product_Images (ProductID, ImageName, ImageBase64) VALUES (?, 'UploadedImage', ?)";
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psProduct = con.prepareStatement(sqlProduct, Statement.RETURN_GENERATED_KEYS)) {
                String randomSKU = "SP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                psProduct.setInt(1, categoryId);
                psProduct.setInt(2, brandId);
                psProduct.setString(3, name);
                psProduct.setString(4, randomSKU);
                psProduct.setDouble(5, price);
                psProduct.setInt(6, stock);
                psProduct.setString(7, desc);
                psProduct.executeUpdate();

                ResultSet rs = psProduct.getGeneratedKeys();
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    if (base64 != null && !base64.isEmpty()) {
                        try (PreparedStatement psImage = con.prepareStatement(sqlImage)) {
                            psImage.setInt(1, newId);
                            psImage.setString(2, base64);
                            psImage.executeUpdate();
                        }
                    }
                }
                con.commit();
                return true;
            } catch (Exception ex) { con.rollback(); ex.printStackTrace(); }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Sửa Sản phẩm có cập nhật Category và Brand
    public boolean updateProduct(int productId, int categoryId, int brandId, String name, double price, int stock, String desc, String base64) {
        String sqlProduct = "UPDATE Products SET CategoryID = ?, BrandID = ?, ProductName = ?, BasePrice = ?, StockQuantity = ?, ShortDescription = ? WHERE ProductID = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement psProduct = con.prepareStatement(sqlProduct)) {
            psProduct.setInt(1, categoryId);
            psProduct.setInt(2, brandId);
            psProduct.setString(3, name);
            psProduct.setDouble(4, price);
            psProduct.setInt(5, stock);
            psProduct.setString(6, desc);
            psProduct.setInt(7, productId);
            psProduct.executeUpdate();

            if (base64 != null && !base64.trim().isEmpty()) {
                String checkSql = "SELECT ImageID FROM Product_Images WHERE ProductID = " + productId;
                boolean hasImg = false;
                try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(checkSql)) {
                    if (rs.next()) hasImg = true;
                }
                if (hasImg) {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE Product_Images SET ImageBase64 = ? WHERE ProductID = ?")) {
                        ps.setString(1, base64); ps.setInt(2, productId); ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO Product_Images (ProductID, ImageName, ImageBase64) VALUES (?, 'UploadedImage', ?)")) {
                        ps.setInt(1, productId); ps.setString(2, base64); ps.executeUpdate();
                    }
                }
            }
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Xóa Sản phẩm
    public boolean deleteProduct(int productId) {
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM Product_Images WHERE ProductID = ?")) {
                    ps.setInt(1, productId); ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM Products WHERE ProductID = ?")) {
                    ps.setInt(1, productId); ps.executeUpdate();
                }
                con.commit();
                return true;
            } catch (Exception ex) { con.rollback(); ex.printStackTrace(); }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}