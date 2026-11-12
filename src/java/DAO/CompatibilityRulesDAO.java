package DAO;

import Models.CompatibilityRules;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompatibilityRulesDAO {

    public List<CompatibilityRules> getAll() {
        List<CompatibilityRules> list = new ArrayList<>();
        String sql = "SELECT * FROM [compatibility_rules]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CompatibilityRules cr = new CompatibilityRules();
                    cr.setRuleId(rs.getInt("rule_id"));
                    cr.setRuleName(rs.getString("rule_name"));
                    cr.setPrimaryCategoryId(rs.getInt("primary_category_id"));
                    cr.setTargetCategoryId(rs.getInt("target_category_id"));
                    cr.setCheckType(rs.getString("check_type"));
                    cr.setErrorMessage(rs.getString("error_message"));
                    cr.setSeverity(rs.getString("severity"));
                    cr.setIsActive(rs.getBoolean("is_active"));
                    list.add(cr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
