package khanhnb08894.dao;

import fourSome.model.SpecMonitor;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecMonitorDAO {

    public List<SpecMonitor> getAll() {
        List<SpecMonitor> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_monitor]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecMonitor obj = new SpecMonitor();
                obj.setPartId(rs.getInt("part_id"));
                obj.setScreenSizeInch(rs.getDouble("screen_size_inch"));
                obj.setResolution(rs.getString("resolution"));
                obj.setRefreshRateHz(rs.getInt("refresh_rate_hz"));
                obj.setPanelType(rs.getString("panel_type"));
                obj.setResponseTimeMs(rs.getDouble("response_time_ms"));
                obj.setAdaptiveSync(rs.getString("adaptive_sync"));
                obj.setHdmiPorts(rs.getInt("hdmi_ports"));
                obj.setDisplayportPorts(rs.getInt("displayport_ports"));
                obj.setHdmiVersion(rs.getString("hdmi_version"));
                obj.setDisplayportVersion(rs.getString("displayport_version"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
