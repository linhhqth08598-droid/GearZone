package DAO;

import Models.Brands;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandsDAO {

    public List<Brands> getAll() {
        List<Brands> list = new ArrayList<>();
        String sql = "SELECT * FROM [brands]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Brands b = new Brands();
                    b.setBrandId(rs.getInt("brand_id"));
                    b.setBrandName(rs.getString("brand_name"));
                    b.setLogoUrl(rs.getString("logo_url"));
                    b.setCountry(rs.getString("country"));
                    b.setStatus(rs.getString("status"));
                    list.add(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
