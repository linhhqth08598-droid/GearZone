package khanhnb08894.service;

import fourSome.model.CompatibilityResult;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class CompatibilityService {

    /**
     * Kiểm tra tương thích dựa trên danh sách ID linh kiện
     * @param parts Map chứa các cặp key-value: "cpu"->id, "main"->id, "ram"->id, "vga"->id, "psu"->id
     * @return CompatibilityResult chứa danh sách lỗi và cảnh báo
     */
    public CompatibilityResult checkBuild(Map<String, Integer> parts) {
        CompatibilityResult result = new CompatibilityResult();

        int cpuId = parts.getOrDefault("cpu", 0);
        int mainId = parts.getOrDefault("main", 0);
        int ramId = parts.getOrDefault("ram", 0);
        int vgaId = parts.getOrDefault("vga", 0);
        int psuId = parts.getOrDefault("psu", 0);

        try (Connection conn = DBConnect.getConnection()) {
            
            // 1. Kiểm tra CPU và Mainboard (Socket)
            if (cpuId > 0 && mainId > 0) {
                String sql = "SELECT c.socket_id as cpu_socket, m.socket_id as main_socket " +
                             "FROM spec_cpu c, spec_mainboard m " +
                             "WHERE c.part_id = ? AND m.part_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, cpuId);
                    ps.setInt(2, mainId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.getInt("cpu_socket") != rs.getInt("main_socket")) {
                            result.addError("Socket của CPU không khớp với Mainboard!");
                        }
                    }
                }
            }

            // 2. Kiểm tra RAM và Mainboard (Chuẩn RAM DDR4/DDR5)
            if (ramId > 0 && mainId > 0) {
                String sql = "SELECT r.ddr_type as ram_type, m.ram_standard as main_type " +
                             "FROM spec_ram r, spec_mainboard m " +
                             "WHERE r.part_id = ? AND m.part_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, ramId);
                    ps.setInt(2, mainId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        if (!rs.getString("ram_type").equals(rs.getString("main_type"))) {
                            result.addError("Mainboard không hỗ trợ chuẩn RAM " + rs.getString("ram_type") + "!");
                        }
                    }
                }
            }

            // 3. Kiểm tra công suất nguồn (PSU) với VGA và CPU
            if (psuId > 0 && (cpuId > 0 || vgaId > 0)) {
                int totalWattage = 0;
                
                // Lấy công suất CPU (TDP)
                if (cpuId > 0) {
                    String sqlCpu = "SELECT tdp_watts FROM spec_cpu WHERE part_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlCpu)) {
                        ps.setInt(1, cpuId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) totalWattage += rs.getInt("tdp_watts");
                    }
                }
                
                // Lấy công suất VGA (TGP)
                if (vgaId > 0) {
                    String sqlVga = "SELECT tgp_watts FROM spec_vga WHERE part_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlVga)) {
                        ps.setInt(1, vgaId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) totalWattage += rs.getInt("tgp_watts");
                    }
                }
                
                // Cộng thêm 20% dự phòng (theo chuẩn psu_power_rules)
                int recommendedPsu = (int) (totalWattage * 1.2);
                
                // Lấy công suất thực tế của PSU
                String sqlPsu = "SELECT wattage FROM spec_psu WHERE part_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlPsu)) {
                    ps.setInt(1, psuId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int psuWattage = rs.getInt("wattage");
                        if (psuWattage < totalWattage) {
                            result.addError("Nguồn PSU không đủ công suất! Cần tối thiểu " + totalWattage + "W, nhưng PSU chỉ có " + psuWattage + "W.");
                        } else if (psuWattage < recommendedPsu) {
                            result.addWarning("Nguồn PSU đủ dùng nhưng không có nhiều dự phòng. Khuyến nghị dùng PSU " + recommendedPsu + "W trở lên.");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.addError("Lỗi hệ thống khi kiểm tra tương thích.");
        }

        return result;
    }
}