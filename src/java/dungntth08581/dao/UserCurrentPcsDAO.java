package dungntth08581.dao;

import model.UserCurrentPcs;
import url.DBConnect;
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
                UserCurrentPcs obj = new UserCurrentPcs();
                obj.setCurrentPcId(rs.getInt("current_pc_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setCurrentCpuId(rs.getInt("current_cpu_id"));
                obj.setCurrentMainboardId(rs.getInt("current_mainboard_id"));
                obj.setCurrentRamId(rs.getInt("current_ram_id"));
                obj.setCurrentVgaId(rs.getInt("current_vga_id"));
                obj.setCurrentPsuId(rs.getInt("current_psu_id"));
                obj.setCurrentCaseId(rs.getInt("current_case_id"));
                obj.setCurrentStorageId(rs.getInt("current_storage_id"));
                obj.setCurrentCoolerId(rs.getInt("current_cooler_id"));
                obj.setCurrentRamQuantity(rs.getInt("current_ram_quantity"));
                obj.setUpgradeBudget(rs.getDouble("upgrade_budget"));
                obj.setCreatedAt(rs.getDate("created_at"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
