package DAO;

import Models.UserCurrentPcs;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserCurrentPcsDAO {

    public List<UserCurrentPcs> getAll() {
        List<UserCurrentPcs> list = new ArrayList<>();
        String sql = "SELECT * FROM [user_current_pcs]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserCurrentPcs ucp = new UserCurrentPcs();
                    ucp.setCurrentPcId(rs.getInt("current_pc_id"));
                    ucp.setUserId(rs.getInt("user_id"));
                    ucp.setCurrentCpuId(rs.getInt("current_cpu_id"));
                    ucp.setCurrentMainboardId(rs.getInt("current_mainboard_id"));
                    ucp.setCurrentRamId(rs.getInt("current_ram_id"));
                    ucp.setCurrentVgaId(rs.getInt("current_vga_id"));
                    ucp.setCurrentPsuId(rs.getInt("current_psu_id"));
                    ucp.setCurrentCaseId(rs.getInt("current_case_id"));
                    ucp.setCurrentStorageId(rs.getInt("current_storage_id"));
                    ucp.setCurrentCoolerId(rs.getInt("current_cooler_id"));
                    ucp.setCurrentRamQuantity(rs.getInt("current_ram_quantity"));
                    ucp.setUpgradeBudget(rs.getDouble("upgrade_budget"));
                    ucp.setCreatedAt(rs.getDate("created_at"));
                    ucp.setStatus(rs.getString("status"));
                    list.add(ucp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
