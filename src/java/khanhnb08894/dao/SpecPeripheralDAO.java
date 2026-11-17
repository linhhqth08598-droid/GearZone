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
    String sql = "INSERT INTO spec_gear (part_id, gear_type, connectivity, switch_or_dpi) VALUES (?, ?, ?, ?)";
    try (Connection conn = new DBConnect().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, partId);
        ps.setString(2, gearType);
        ps.setString(3, connectivity);
        ps.setString(4, switchOrDpi);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}
