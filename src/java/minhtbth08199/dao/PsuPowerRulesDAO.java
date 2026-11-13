package minhtbth08199.dao;

import model.PsuPowerRules;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PsuPowerRulesDAO {

    public List<PsuPowerRules> getAll() {
        List<PsuPowerRules> list = new ArrayList<>();
        String sql = "SELECT * FROM [psu_power_rules]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PsuPowerRules obj = new PsuPowerRules();
                obj.setRuleId(rs.getInt("rule_id"));
                obj.setMinSystemWattage(rs.getInt("min_system_wattage"));
                obj.setRecommendedPsuHeadroomPercent(rs.getDouble("recommended_psu_headroom_percent"));
                obj.setDescription(rs.getString("description"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
