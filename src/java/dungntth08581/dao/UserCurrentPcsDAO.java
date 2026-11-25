package dungntth08581.dao;

import fourSome.model.UserCurrentPcs;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserCurrentPcsDAO {

    public List<UserCurrentPcs> getAll() {
        List<UserCurrentPcs> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_current_pcs]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public UserCurrentPcs findById(int currentPcId) {
        String sql = "SELECT * FROM [user_current_pcs] WHERE current_pc_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, currentPcId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public UserCurrentPcs findActiveByUserId(int userId) {
        String sql = "SELECT TOP 1 * FROM [user_current_pcs] WHERE user_id = ? AND status = 'ACTIVE' ORDER BY current_pc_id DESC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public int saveCurrentPc(UserCurrentPcs pc) {
        String sql = "INSERT INTO [user_current_pcs] (user_id, current_cpu_id, current_mainboard_id, current_ram_id, "
                   + "current_vga_id, current_psu_id, current_case_id, current_storage_id, current_cooler_id, "
                   + "current_ram_quantity, upgrade_budget, created_at, status) "
                   + "OUTPUT INSERTED.current_pc_id VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), 'ACTIVE')";
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement deactivate = con.prepareStatement(
                        "UPDATE [user_current_pcs] SET status = 'INACTIVE' WHERE user_id = ? AND status = 'ACTIVE'")) {
                    deactivate.setInt(1, pc.getUserId());
                    deactivate.executeUpdate();
                }
                int newId = 0;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, pc.getUserId());
                    setNullableInt(ps, 2, pc.getCurrentCpuId());
                    setNullableInt(ps, 3, pc.getCurrentMainboardId());
                    setNullableInt(ps, 4, pc.getCurrentRamId());
                    setNullableInt(ps, 5, pc.getCurrentVgaId());
                    setNullableInt(ps, 6, pc.getCurrentPsuId());
                    setNullableInt(ps, 7, pc.getCurrentCaseId());
                    setNullableInt(ps, 8, pc.getCurrentStorageId());
                    setNullableInt(ps, 9, pc.getCurrentCoolerId());
                    ps.setInt(10, pc.getCurrentRamQuantity() != null && pc.getCurrentRamQuantity() > 0 ? pc.getCurrentRamQuantity() : 1);
                    ps.setDouble(11, pc.getUpgradeBudget() != null && pc.getUpgradeBudget() >= 0 ? pc.getUpgradeBudget() : 0.0);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) newId = rs.getInt(1);
                    }
                }
                if (newId > 0) {
                    con.commit();
                    return newId;
                }
                con.rollback();
            } catch (Exception ex) {
                con.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private UserCurrentPcs mapRow(ResultSet rs) throws SQLException {
        UserCurrentPcs obj = new UserCurrentPcs();
        obj.setCurrentPcId(rs.getInt("current_pc_id"));
        obj.setUserId(rs.getInt("user_id"));
        obj.setCurrentCpuId(getNullableInt(rs, "current_cpu_id"));
        obj.setCurrentMainboardId(getNullableInt(rs, "current_mainboard_id"));
        obj.setCurrentRamId(getNullableInt(rs, "current_ram_id"));
        obj.setCurrentVgaId(getNullableInt(rs, "current_vga_id"));
        obj.setCurrentPsuId(getNullableInt(rs, "current_psu_id"));
        obj.setCurrentCaseId(getNullableInt(rs, "current_case_id"));
        obj.setCurrentStorageId(getNullableInt(rs, "current_storage_id"));
        obj.setCurrentCoolerId(getNullableInt(rs, "current_cooler_id"));
        obj.setCurrentRamQuantity(rs.getInt("current_ram_quantity"));
        obj.setUpgradeBudget(rs.getDouble("upgrade_budget"));
        obj.setCreatedAt(rs.getTimestamp("created_at"));
        obj.setStatus(rs.getString("status"));
        return obj;
    }

    private void setNullableInt(PreparedStatement ps, int idx, Integer val) throws SQLException {
        if (val == null || val <= 0) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, val);
    }

    private Integer getNullableInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }
}
