package dungntth08581.dao;

import fourSome.model.ComboPromotions;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboPromotionsDAO {

    public List<ComboPromotions> getAll() {
        List<ComboPromotions> list = new ArrayList<>();
        String sql = "SELECT * FROM [combo_promotions]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ComboPromotions obj = new ComboPromotions();
                obj.setComboId(rs.getInt("combo_id"));
                obj.setComboName(rs.getString("combo_name"));
                obj.setDiscountPercentage(rs.getDouble("discount_percentage"));
                obj.setMinPartsCount(rs.getInt("min_parts_count"));
                obj.setComboType(rs.getString("combo_type"));
                obj.setStatus(rs.getString("status"));
                obj.setStartDate(rs.getDate("start_date"));
                obj.setEndDate(rs.getDate("end_date"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
