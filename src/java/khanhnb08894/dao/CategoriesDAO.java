package khanhnb08894.dao;

import model.Categories;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriesDAO {

    public List<Categories> getAll() {
        List<Categories> list = new ArrayList<>();
        String sql = "SELECT * FROM [categories]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categories obj = new Categories();
                obj.setCategoryId(rs.getInt("category_id"));
                obj.setCategoryCode(rs.getString("category_code"));
                obj.setCategoryName(rs.getString("category_name"));
                obj.setDescription(rs.getString("description"));
                obj.setIsRequiredInBuild(rs.getBoolean("is_required_in_build"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
