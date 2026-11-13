package khanhnb08894.dao;

import fourSome.model.Brands;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandsDAO {

    public List<Brands> getAll() {
        List<Brands> list = new ArrayList<>();
        String sql = "SELECT * FROM [brands]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Brands obj = new Brands();
                obj.setBrandId(rs.getInt("brand_id"));
                obj.setBrandName(rs.getString("brand_name"));
                obj.setLogoUrl(rs.getString("logo_url"));
                obj.setCountry(rs.getString("country"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
