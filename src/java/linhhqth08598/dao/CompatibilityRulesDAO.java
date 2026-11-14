package linhhqth08598.dao;

import fourSome.model.CompatibilityRules;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompatibilityRulesDAO {
    
    public List<CompatibilityRules> getAll() {
        List<CompatibilityRules> list = new ArrayList<>();
        String sql = "SELECT * FROM compatibility_rules ORDER BY rule_id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CompatibilityRules r = new CompatibilityRules();
                r.setRuleId(rs.getInt("rule_id"));
                r.setRuleName(rs.getString("rule_name"));
                r.setPrimaryCategoryId(rs.getInt("primary_category_id"));
                r.setTargetCategoryId(rs.getInt("target_category_id"));
                r.setCheckType(rs.getString("check_type"));
                r.setErrorMessage(rs.getString("error_message"));
                r.setSeverity(rs.getString("severity"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(CompatibilityRules rule) {
        String sql = "INSERT INTO compatibility_rules (rule_name, primary_category_id, target_category_id, check_type, error_message, severity, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rule.getRuleName());
            ps.setInt(2, rule.getPrimaryCategoryId());
            ps.setInt(3, rule.getTargetCategoryId());
            ps.setString(4, rule.getCheckType());
            ps.setString(5, rule.getErrorMessage());
            ps.setString(6, rule.getSeverity());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean softDelete(int ruleId) {
        String sql = "UPDATE compatibility_rules SET status = 'INACTIVE' WHERE rule_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ruleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}