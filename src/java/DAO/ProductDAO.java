package DAO;

import Models.Product;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDAO {

    // 1. LẤY TẤT CẢ SẢN PHẨM (Kết hợp bảng Products và Product_Images)
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, p.BasePrice, p.ShortDescription, i.ImageBase64 " +
                     "FROM Products p " +
                     "LEFT JOIN Product_Images i ON p.ProductID = i.ProductID";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setBasePrice(rs.getDouble("BasePrice"));
                p.setDescription(rs.getString("ShortDescription"));
                p.setImageBase64(rs.getString("ImageBase64"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. THÊM SẢN PHẨM MỚI
    public boolean insertProduct(Product p) {
        String sqlProduct = "INSERT INTO Products (CategoryID, BrandID, ProductName, SKU, BasePrice, ShortDescription) VALUES (1, 1, ?, ?, ?, ?)";
        String sqlImage = "INSERT INTO Product_Images (ProductID, ImageName, ImageBase64) VALUES (?, 'UploadedImage', ?)";
        
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false); // Bắt đầu transaction
            try (PreparedStatement psProduct = con.prepareStatement(sqlProduct, Statement.RETURN_GENERATED_KEYS)) {
                // Tự sinh SKU ngẫu nhiên để không bị trùng
                String randomSKU = "SP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                
                psProduct.setString(1, p.getProductName());
                psProduct.setString(2, randomSKU);
                psProduct.setDouble(3, p.getBasePrice());
                psProduct.setString(4, p.getDescription());
                psProduct.executeUpdate();

                // Lấy ID sản phẩm vừa tạo để lưu vào bảng ảnh
                ResultSet rs = psProduct.getGeneratedKeys();
                if (rs.next()) {
                    int newProductId = rs.getInt(1);
                    // Nếu có ảnh Base64 thì mới lưu vào bảng Product_Images
                    if (p.getImageBase64() != null && !p.getImageBase64().isEmpty()) {
                        try (PreparedStatement psImage = con.prepareStatement(sqlImage)) {
                            psImage.setInt(1, newProductId);
                            psImage.setString(2, p.getImageBase64());
                            psImage.executeUpdate();
                        }
                    }
                }
                con.commit(); // Hoàn tất transaction
                return true;
            } catch (Exception ex) {
                con.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. SỬA SẢN PHẨM
    public boolean updateProduct(Product p) {
        String sqlProduct = "UPDATE Products SET ProductName = ?, BasePrice = ?, ShortDescription = ? WHERE ProductID = ?";
        String checkImgSql = "SELECT ImageID FROM Product_Images WHERE ProductID = ?";
        String updateImgSql = "UPDATE Product_Images SET ImageBase64 = ? WHERE ProductID = ?";
        String insertImgSql = "INSERT INTO Product_Images (ProductID, ImageName, ImageBase64) VALUES (?, 'UploadedImage', ?)";
        
        try (Connection con = DBConnect.getConnection();
             PreparedStatement psProduct = con.prepareStatement(sqlProduct)) {
            
            psProduct.setString(1, p.getProductName());
            psProduct.setDouble(2, p.getBasePrice());
            psProduct.setString(3, p.getDescription());
            psProduct.setInt(4, p.getProductId());
            psProduct.executeUpdate();

            // Nếu người dùng có chọn ảnh mới thì cập nhật
            if (p.getImageBase64() != null && !p.getImageBase64().trim().isEmpty()) {
                boolean hasImage = false;
                try (PreparedStatement psCheck = con.prepareStatement(checkImgSql)) {
                    psCheck.setInt(1, p.getProductId());
                    ResultSet rs = psCheck.executeQuery();
                    if(rs.next()) hasImage = true;
                }
                
                if(hasImage) { // Đã có ảnh -> Update
                    try (PreparedStatement psImg = con.prepareStatement(updateImgSql)) {
                        psImg.setString(1, p.getImageBase64());
                        psImg.setInt(2, p.getProductId());
                        psImg.executeUpdate();
                    }
                } else { // Chưa có ảnh -> Insert
                    try (PreparedStatement psImg = con.prepareStatement(insertImgSql)) {
                        psImg.setInt(1, p.getProductId());
                        psImg.setString(2, p.getImageBase64());
                        psImg.executeUpdate();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. XÓA SẢN PHẨM
    public boolean deleteProduct(int productId) {
        String deleteImage = "DELETE FROM Product_Images WHERE ProductID = ?";
        String deleteProduct = "DELETE FROM Products WHERE ProductID = ?";
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                // Phải xóa ảnh trước (do ràng buộc khóa ngoại)
                try (PreparedStatement psImg = con.prepareStatement(deleteImage)) {
                    psImg.setInt(1, productId);
                    psImg.executeUpdate();
                }
                // Sau đó xóa Sản phẩm
                try (PreparedStatement psProd = con.prepareStatement(deleteProduct)) {
                    psProd.setInt(1, productId);
                    psProd.executeUpdate();
                }
                con.commit();
                return true;
            } catch (Exception ex) {
                con.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}