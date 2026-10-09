package DAO;

import Models.ProductSpecification;
import Url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecificationDAO {
    
    // Hàm lấy danh sách thông số của 1 sản phẩm cụ thể (Có JOIN để lấy tên thông số)
    public List<ProductSpecification> getSpecsByProductId(int productId) {
        List<ProductSpecification> list = new ArrayList<>();
        String sql = "SELECT ps.ProductID, ps.SpecID, ps.SpecValue, s.SpecName " +
                     "FROM Product_Specifications ps " +
                     "JOIN Specifications s ON ps.SpecID = s.SpecID " +
                     "WHERE ps.ProductID = ?";
                     
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductSpecification spec = new ProductSpecification();
                    spec.setProductId(rs.getInt("ProductID"));
                    spec.setSpecId(rs.getInt("SpecID"));
                    spec.setSpecName(rs.getString("SpecName"));
                    spec.setSpecValue(rs.getString("SpecValue"));
                    list.add(spec);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}