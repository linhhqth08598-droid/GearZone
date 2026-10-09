package DAO;

import Models.Specification;
import Url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SpecificationDAO {
    public List<Specification> getSpecsByCategoryId(int categoryId) {
        List<Specification> list = new ArrayList<>();
        String sql = "SELECT * FROM Specifications WHERE CategoryID = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Specification(
                        rs.getInt("SpecID"),
                        rs.getInt("CategoryID"),
                        rs.getString("SpecName")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}