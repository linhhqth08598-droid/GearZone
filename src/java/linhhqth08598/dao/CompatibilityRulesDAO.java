package linhhqth08598.dao;

import model.CompatibilityRules;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompatibilityRulesDAO {

    public List<CompatibilityRules> getAll() {
        List<CompatibilityRules> list = new ArrayList<>();
        String sql = "SELECT * FROM [compatibility_rules]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CompatibilityRules obj = new CompatibilityRules();
                obj.setRuleId(rs.getInt("rule_id"));
                obj.setRuleName(rs.getString("rule_name"));
                obj.setPrimaryCategoryId(rs.getInt("primary_category_id"));
                obj.setTargetCategoryId(rs.getInt("target_category_id"));
                obj.setCheckType(rs.getString("check_type"));
                obj.setErrorMessage(rs.getString("error_message"));
                obj.setSeverity(rs.getString("severity"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
