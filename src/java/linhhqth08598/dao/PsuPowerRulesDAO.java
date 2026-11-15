package linhhqth08598.dao;

import fourSome.model.PsuPowerRules;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PsuPowerRulesDAO {

    public List<PsuPowerRules> getAll() {
        List<PsuPowerRules> list = new ArrayList<>();
        String sql = "SELECT * FROM [psu_power_rules] ORDER BY min_system_wattage";

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

    public boolean insert(int minWattage, double headroomPercent, String description) {
        String sql = "INSERT INTO psu_power_rules (min_system_wattage, recommended_psu_headroom_percent, description, status) VALUES (?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, minWattage);
            ps.setDouble(2, headroomPercent);
            ps.setString(3, description);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean update(int ruleId, int minWattage, double headroomPercent, String description) {
        String sql = "UPDATE psu_power_rules SET min_system_wattage = ?, recommended_psu_headroom_percent = ?, description = ? WHERE rule_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, minWattage);
            ps.setDouble(2, headroomPercent);
            ps.setString(3, description);
            ps.setInt(4, ruleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean setStatus(int ruleId, String status) {
        String sql = "UPDATE psu_power_rules SET status = ? WHERE rule_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, ruleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
