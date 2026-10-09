package DAO;

import Models.Product;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDAO {

    public List<Product> getAllProducts() {
        return fetchProducts("SELECT p.ProductID, p.CategoryID, p.BrandID, p.ProductName, p.BasePrice, p.StockQuantity, p.ShortDescription, i.ImageBase64 FROM Products p LEFT JOIN Product_Images i ON p.ProductID = i.ProductID ORDER BY p.ProductID DESC");
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return fetchProducts("SELECT p.ProductID, p.CategoryID, p.BrandID, p.ProductName, p.BasePrice, p.StockQuantity, p.ShortDescription, i.ImageBase64 FROM Products p LEFT JOIN Product_Images i ON p.ProductID = i.ProductID WHERE p.CategoryID = " + categoryId + " ORDER BY p.ProductID DESC");
    }

    public List<Product> getProductsByBrand(int brandId) {
        return fetchProducts("SELECT p.ProductID, p.CategoryID, p.BrandID, p.ProductName, p.BasePrice, p.StockQuantity, p.ShortDescription, i.ImageBase64 FROM Products p LEFT JOIN Product_Images i ON p.ProductID = i.ProductID WHERE p.BrandID = " + brandId + " ORDER BY p.ProductID DESC");
    }

    public List<Product> getProductsByPriceRange(double min, double max) {
        return fetchProducts("SELECT p.ProductID, p.CategoryID, p.BrandID, p.ProductName, p.BasePrice, p.StockQuantity, p.ShortDescription, i.ImageBase64 FROM Products p LEFT JOIN Product_Images i ON p.ProductID = i.ProductID WHERE p.BasePrice >= " + min + " AND p.BasePrice <= " + max + " ORDER BY p.ProductID DESC");
    }

    private List<Product> fetchProducts(String sql) {
        List<Product> list = new ArrayList<>();
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("ProductID"));
                p.setCategoryId(rs.getInt("CategoryID")); 
                p.setBrandId(rs.getInt("BrandID"));       
                p.setProductName(rs.getString("ProductName"));
                p.setBasePrice(rs.getDouble("BasePrice"));
                p.setStockQuantity(rs.getInt("StockQuantity"));
                p.setDescription(rs.getString("ShortDescription"));
                p.setImageBase64(rs.getString("ImageBase64"));
                list.add(p);
            }
        } catch (Exception e) { 
            throw new RuntimeException("Lỗi lấy danh sách sản phẩm: " + e.getMessage()); 
        }
        return list;
    }

    public void insertProduct(int categoryId, int brandId, String name, double price, int stock, String desc, String base64) {
        String sqlProduct = "INSERT INTO Products (CategoryID, BrandID, ProductName, SKU, BasePrice, StockQuantity, ShortDescription) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlImage = "INSERT INTO Product_Images (ProductID, ImageName, ImageBase64) VALUES (?, 'UploadedImage', ?)";
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            
            // ĐÃ SỬA LẠI DÒNG NÀY: Dùng new String[]{"ProductID"} thay cho Statement.RETURN_GENERATED_KEYS
            try (PreparedStatement psProduct = con.prepareStatement(sqlProduct, new String[]{"ProductID"})) {
                
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
            } catch (Exception ex) { 
                con.rollback(); 
                throw new RuntimeException("Lỗi SQL khi lưu dữ liệu Sản phẩm: " + ex.getMessage()); 
            }
        } catch (Exception e) { throw new RuntimeException("Lỗi kết nối DB: " + e.getMessage()); }
    }

    public void updateProduct(int productId, int categoryId, int brandId, String name, double price, int stock, String desc, String base64) {
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
        } catch (Exception e) { throw new RuntimeException("Lỗi SQL khi Cập nhật Sản phẩm: " + e.getMessage()); }
    }

    public void deleteProduct(int productId) {
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                con.prepareStatement("DELETE FROM Cart_Items WHERE ProductID = " + productId).executeUpdate();
                con.prepareStatement("DELETE FROM Order_Details WHERE ProductID = " + productId).executeUpdate();
                con.prepareStatement("DELETE FROM Product_Specifications WHERE ProductID = " + productId).executeUpdate();
                con.prepareStatement("DELETE FROM Reviews WHERE ProductID = " + productId).executeUpdate();
                con.prepareStatement("DELETE FROM Product_Images WHERE ProductID = " + productId).executeUpdate();
                
                con.prepareStatement("DELETE FROM Products WHERE ProductID = " + productId).executeUpdate();
                con.commit();
            } catch (Exception ex) { 
                con.rollback(); 
                throw new RuntimeException("Lỗi SQL khi Xóa Sản phẩm: " + ex.getMessage()); 
            }
        } catch (Exception e) { throw new RuntimeException("Lỗi kết nối DB: " + e.getMessage()); }
    }
    
    public Product getProductById(int productId) {
        String sql = "SELECT p.ProductID, p.CategoryID, p.BrandID, p.ProductName, p.BasePrice, p.StockQuantity, p.ShortDescription, i.ImageBase64 " +
                     "FROM Products p LEFT JOIN Product_Images i ON p.ProductID = i.ProductID " +
                     "WHERE p.ProductID = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getInt("ProductID"));
                    p.setCategoryId(rs.getInt("CategoryID"));
                    p.setBrandId(rs.getInt("BrandID"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setBasePrice(rs.getDouble("BasePrice"));
                    p.setStockQuantity(rs.getInt("StockQuantity"));
                    p.setDescription(rs.getString("ShortDescription"));
                    p.setImageBase64(rs.getString("ImageBase64"));
                    return p;
                }
            }
        } catch (Exception e) { throw new RuntimeException("Lỗi SQL khi lấy chi tiết: " + e.getMessage()); }
        return null;
    }
}