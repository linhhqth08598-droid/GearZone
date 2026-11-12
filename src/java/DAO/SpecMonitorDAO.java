package DAO;

import Models.SpecMonitor;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecMonitorDAO {

    public List<SpecMonitor> getAll() {
        List<SpecMonitor> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_monitor]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecMonitor sm = new SpecMonitor();
                    sm.setPartId(rs.getInt("part_id"));
                    sm.setScreenSizeInch(rs.getDouble("screen_size_inch"));
                    sm.setResolution(rs.getString("resolution"));
                    sm.setRefreshRateHz(rs.getInt("refresh_rate_hz"));
                    sm.setPanelType(rs.getString("panel_type"));
                    sm.setResponseTimeMs(rs.getDouble("response_time_ms"));
                    sm.setAdaptiveSync(rs.getString("adaptive_sync"));
                    sm.setHdmiPorts(rs.getInt("hdmi_ports"));
                    sm.setDisplayportPorts(rs.getInt("displayport_ports"));
                    sm.setHdmiVersion(rs.getString("hdmi_version"));
                    sm.setDisplayportVersion(rs.getString("displayport_version"));
                    sm.setStatus(rs.getString("status"));
                    list.add(sm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
