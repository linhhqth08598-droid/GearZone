package DAO;

import Models.ComboPromotions;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboPromotionsDAO {

    public List<ComboPromotions> getAll() {
        List<ComboPromotions> list = new ArrayList<>();
        String sql = "SELECT * FROM [combo_promotions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComboPromotions cp = new ComboPromotions();
                    cp.setComboId(rs.getInt("combo_id"));
                    cp.setComboName(rs.getString("combo_name"));
                    cp.setDiscountPercentage(rs.getDouble("discount_percentage"));
                    cp.setMinPartsCount(rs.getInt("min_parts_count"));
                    cp.setComboType(rs.getString("combo_type"));
                    cp.setIsActive(rs.getBoolean("is_active"));
                    cp.setStartDate(rs.getDate("start_date"));
                    cp.setEndDate(rs.getDate("end_date"));
                    list.add(cp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
