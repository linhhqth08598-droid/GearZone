package DAO;

import Models.ProductLine;
import Url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductLineDAO {
    public List<ProductLine> getLinesByBrandId(int brandId) {
        List<ProductLine> list = new ArrayList<>();
        String sql = "SELECT * FROM ProductLines WHERE BrandID = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, brandId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProductLine(
                        rs.getInt("LineID"),
                        rs.getInt("BrandID"),
                        rs.getString("LineName"),
                        rs.getString("Description")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}