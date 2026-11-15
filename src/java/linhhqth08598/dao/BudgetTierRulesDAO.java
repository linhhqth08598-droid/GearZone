package linhhqth08598.dao;

import fourSome.model.BudgetTierRules;
import fourSome.url.DBConnect;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** UC 343 - CRUD khung ngân sách và tỉ lệ phân bổ cho đề xuất cấu hình. */
public class BudgetTierRulesDAO {

    public List<BudgetTierRules> getAll() {
        List<BudgetTierRules> list = new ArrayList<>();
        String sql = "SELECT * FROM [budget_tier_rules] ORDER BY min_budget";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BudgetTierRules o = new BudgetTierRules();
                o.setTierId(rs.getInt("tier_id"));
                o.setTierName(rs.getString("tier_name"));
                o.setMinBudget(rs.getBigDecimal("min_budget"));
                o.setMaxBudget(rs.getBigDecimal("max_budget"));
                o.setCpuPercent(rs.getInt("cpu_percent"));
                o.setGpuPercent(rs.getInt("gpu_percent"));
                o.setRamPercent(rs.getInt("ram_percent"));
                o.setStoragePercent(rs.getInt("storage_percent"));
                o.setOtherPercent(rs.getInt("other_percent"));
                o.setDescription(rs.getString("description"));
                o.setStatus(rs.getString("status"));
                list.add(o);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Khung [min, max) có chồng lên khung ACTIVE khác không? (excludeId = 0 khi thêm mới). max null = vô hạn. */
    public boolean overlaps(int excludeId, BigDecimal min, BigDecimal max) {
        String sql = "SELECT COUNT(*) FROM budget_tier_rules WHERE status = 'ACTIVE' AND tier_id <> ? "
                   + "AND (? IS NULL OR min_budget < ?) AND (max_budget IS NULL OR max_budget > ?)";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, excludeId);
            ps.setBigDecimal(2, max);
            ps.setBigDecimal(3, max);
            ps.setBigDecimal(4, min);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) { e.printStackTrace(); return true; } // lỗi DB: chặn lưu cho an toàn
    }

    public boolean insert(BudgetTierRules t) {
        String sql = "INSERT INTO budget_tier_rules (tier_name, min_budget, max_budget, cpu_percent, gpu_percent, "
                   + "ram_percent, storage_percent, other_percent, description, status) VALUES (?,?,?,?,?,?,?,?,?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, t);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean update(BudgetTierRules t) {
        String sql = "UPDATE budget_tier_rules SET tier_name = ?, min_budget = ?, max_budget = ?, cpu_percent = ?, gpu_percent = ?, "
                   + "ram_percent = ?, storage_percent = ?, other_percent = ?, description = ? WHERE tier_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, t);
            ps.setInt(10, t.getTierId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean setStatus(int tierId, String status) {
        String sql = "UPDATE budget_tier_rules SET status = ? WHERE tier_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, tierId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private void bind(PreparedStatement ps, BudgetTierRules t) throws SQLException {
        ps.setString(1, t.getTierName());
        ps.setBigDecimal(2, t.getMinBudget());
        if (t.getMaxBudget() == null) ps.setNull(3, Types.DECIMAL); else ps.setBigDecimal(3, t.getMaxBudget());
        ps.setInt(4, t.getCpuPercent());
        ps.setInt(5, t.getGpuPercent());
        ps.setInt(6, t.getRamPercent());
        ps.setInt(7, t.getStoragePercent());
        ps.setInt(8, t.getOtherPercent());
        ps.setString(9, t.getDescription());
    }
}
