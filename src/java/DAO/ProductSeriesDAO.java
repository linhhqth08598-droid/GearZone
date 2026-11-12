package DAO;

import Models.ProductSeries;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductSeriesDAO {

    public List<ProductSeries> getAll() {
        List<ProductSeries> list = new ArrayList<>();
        String sql = "SELECT * FROM [product_series]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductSeries psObj = new ProductSeries();
                    psObj.setSeriesId(rs.getInt("series_id"));
                    psObj.setBrandId(rs.getInt("brand_id"));
                    psObj.setSeriesName(rs.getString("series_name"));
                    psObj.setStatus(rs.getString("status"));
                    list.add(psObj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
