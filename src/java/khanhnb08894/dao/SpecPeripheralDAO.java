package khanhnb08894.dao;

import fourSome.model.SpecPeripheral;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecPeripheralDAO {

    public List<SpecPeripheral> getAll() {
        List<SpecPeripheral> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_peripheral]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecPeripheral obj = new SpecPeripheral();
                obj.setPartId(rs.getInt("part_id"));
                obj.setPeripheralType(rs.getString("peripheral_type"));
                obj.setConnectionType(rs.getString("connection_type"));
                obj.setSwitchType(rs.getString("switch_type"));
                obj.setLayout(rs.getString("layout"));
                obj.setDpiMax(rs.getInt("dpi_max"));
                obj.setPollingRateHz(rs.getInt("polling_rate_hz"));
                obj.setWeightG(rs.getInt("weight_g"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public boolean insert(int partId, String gearType, String connectivity, String switchOrDpi) {
        String pType = mapPeripheralType(gearType);
        String cType = mapConnectionType(connectivity);
        String sql = "INSERT INTO [spec_peripheral] (part_id, peripheral_type, connection_type, switch_type, layout, dpi_max, polling_rate_hz, weight_g, status) "
                   + "VALUES (?, ?, ?, ?, NULL, NULL, NULL, NULL, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setString(2, pType);
            ps.setString(3, cType);
            if (switchOrDpi == null || switchOrDpi.trim().isEmpty()) {
                ps.setNull(4, Types.NVARCHAR);
            } else {
                ps.setString(4, switchOrDpi.trim());
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, String gearType, String connectivity, String switchOrDpi) {
        String pType = mapPeripheralType(gearType);
        String cType = mapConnectionType(connectivity);
        String sql = "UPDATE [spec_peripheral] SET peripheral_type = ?, connection_type = ?, switch_type = ? WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pType);
            ps.setString(2, cType);
            if (switchOrDpi == null || switchOrDpi.trim().isEmpty()) {
                ps.setNull(3, Types.NVARCHAR);
            } else {
                ps.setString(3, switchOrDpi.trim());
            }
            ps.setInt(4, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String mapPeripheralType(String raw) {
        if (raw == null) return "OTHER";
        String u = raw.trim().toUpperCase();
        if (u.contains("KEYBOARD") || u.contains("PHÍM")) return "KEYBOARD";
        if (u.contains("MOUSEPAD") || u.contains("PAD") || u.contains("LÓT")) return "MOUSEPAD";
        if (u.contains("MOUSE") || u.contains("CHUỘT")) return "MOUSE";
        if (u.contains("HEADSET") || u.contains("TAI NGHE")) return "HEADSET";
        return "OTHER";
    }

    private static String mapConnectionType(String raw) {
        if (raw == null) return "WIRED";
        String u = raw.trim().toUpperCase();
        if (u.contains("BOTH") || (u.contains("WIRELESS") && u.contains("WIRED")) || u.contains("MULTI") || u.contains("3 MODE")) return "BOTH";
        if (u.contains("WIRELESS") || u.contains("BLUETOOTH") || u.contains("2.4G") || u.contains("KHÔNG DÂY")) return "WIRELESS";
        return "WIRED";
    }
}
