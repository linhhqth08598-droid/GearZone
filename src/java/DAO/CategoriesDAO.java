package DAO;

import Models.Categories;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriesDAO {

    public List<Categories> getAll() {
        List<Categories> list = new ArrayList<>();
        String sql = "SELECT * FROM [categories]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categories c = new Categories();
                    c.setCategoryId(rs.getInt("category_id"));
                    c.setCategoryCode(rs.getString("category_code"));
                    c.setCategoryName(rs.getString("category_name"));
                    c.setDescription(rs.getString("description"));
                    c.setIsRequiredInBuild(rs.getBoolean("is_required_in_build"));
                    c.setStatus(rs.getString("status"));
                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
