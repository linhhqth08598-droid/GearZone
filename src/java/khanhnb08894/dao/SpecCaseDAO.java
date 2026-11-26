package khanhnb08894.dao;

import fourSome.model.SpecCase;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecCaseDAO {

    public List<SpecCase> getAll() {
        List<SpecCase> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_case]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecCase obj = new SpecCase();
                obj.setPartId(rs.getInt("part_id"));
                obj.setSupportedMotherboards(rs.getString("supported_motherboards"));
                obj.setMaxVgaLengthMm(rs.getInt("max_vga_length_mm"));
                obj.setMaxCpuCoolerHeightMm(rs.getInt("max_cpu_cooler_height_mm"));
                obj.setFanSupportInfo(rs.getString("fan_support_info"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public boolean insert(int partId, String caseFormFactor) {
        return insert(partId, caseFormFactor, 320, 160, null);
    }

    public boolean insert(int partId, String supportedMotherboards, int maxVgaLengthMm, int maxCpuCoolerHeightMm, String fanSupportInfo) {
        String sql = "INSERT INTO [spec_case] (part_id, supported_motherboards, max_vga_length_mm, max_cpu_cooler_height_mm, fan_support_info, status) "
                   + "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setString(2, (supportedMotherboards == null || supportedMotherboards.trim().isEmpty()) ? "ATX, mATX, Mini-ITX" : supportedMotherboards.trim());
            ps.setInt(3, maxVgaLengthMm > 0 ? maxVgaLengthMm : 320);
            ps.setInt(4, maxCpuCoolerHeightMm > 0 ? maxCpuCoolerHeightMm : 160);
            if (fanSupportInfo == null || fanSupportInfo.trim().isEmpty()) {
                ps.setNull(5, Types.NVARCHAR);
            } else {
                ps.setString(5, fanSupportInfo.trim());
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, String supportedMotherboards, int maxVgaLengthMm, int maxCpuCoolerHeightMm, String fanSupportInfo) {
        String sql = "UPDATE [spec_case] SET supported_motherboards = ?, max_vga_length_mm = ?, max_cpu_cooler_height_mm = ?, fan_support_info = ? "
                   + "WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, (supportedMotherboards == null || supportedMotherboards.trim().isEmpty()) ? "ATX, mATX, Mini-ITX" : supportedMotherboards.trim());
            ps.setInt(2, maxVgaLengthMm > 0 ? maxVgaLengthMm : 320);
            ps.setInt(3, maxCpuCoolerHeightMm > 0 ? maxCpuCoolerHeightMm : 160);
            if (fanSupportInfo == null || fanSupportInfo.trim().isEmpty()) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setString(4, fanSupportInfo.trim());
            }
            ps.setInt(5, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
