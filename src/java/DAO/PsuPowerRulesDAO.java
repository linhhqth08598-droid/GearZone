package DAO;

import Models.PsuPowerRules;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PsuPowerRulesDAO {

    public List<PsuPowerRules> getAll() {
        List<PsuPowerRules> list = new ArrayList<>();
        String sql = "SELECT * FROM [psu_power_rules]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PsuPowerRules ppr = new PsuPowerRules();
                    ppr.setRuleId(rs.getInt("rule_id"));
                    ppr.setMinSystemWattage(rs.getInt("min_system_wattage"));
                    ppr.setRecommendedPsuHeadroomPercent(rs.getDouble("recommended_psu_headroom_percent"));
                    ppr.setDescription(rs.getString("description"));
                    ppr.setStatus(rs.getString("status"));
                    list.add(ppr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
