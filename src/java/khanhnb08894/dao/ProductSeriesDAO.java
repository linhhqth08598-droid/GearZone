package khanhnb08894.dao;

import model.ProductSeries;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductSeriesDAO {

    public List<ProductSeries> getAll() {
        List<ProductSeries> list = new ArrayList<>();
        String sql = "SELECT * FROM [product_series]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProductSeries obj = new ProductSeries();
                obj.setSeriesId(rs.getInt("series_id"));
                obj.setBrandId(rs.getInt("brand_id"));
                obj.setSeriesName(rs.getString("series_name"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
