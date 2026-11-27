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
    public boolean insert(int partId, String screenSize, String resolution, int refreshRate, String panelType) {
        return insert(partId, screenSize, resolution, refreshRate, panelType, null, null, 1, 1);
    }

    public boolean insert(int partId, String screenSize, String resolution, int refreshRate, String panelType,
                          Double responseTimeMs, String adaptiveSync, int hdmiPorts, int displayportPorts) {
        double sizeInch = parseScreenSizeInch(screenSize);
        String sql = "INSERT INTO [spec_monitor] (part_id, screen_size_inch, resolution, refresh_rate_hz, panel_type, "
                   + "response_time_ms, adaptive_sync, hdmi_ports, displayport_ports, hdmi_version, displayport_version, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setDouble(2, sizeInch);
            ps.setString(3, (resolution == null || resolution.trim().isEmpty()) ? "1920x1080" : resolution.trim());
            ps.setInt(4, refreshRate > 0 ? refreshRate : 60);
            ps.setString(5, (panelType == null || panelType.trim().isEmpty()) ? "IPS" : panelType.trim());
            if (responseTimeMs == null) ps.setNull(6, Types.DECIMAL); else ps.setDouble(6, responseTimeMs);
            if (adaptiveSync == null || adaptiveSync.trim().isEmpty()) ps.setNull(7, Types.VARCHAR); else ps.setString(7, adaptiveSync.trim());
            ps.setInt(8, Math.max(0, hdmiPorts));
            ps.setInt(9, Math.max(0, displayportPorts));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, String screenSize, String resolution, int refreshRate, String panelType,
                          Double responseTimeMs, String adaptiveSync, int hdmiPorts, int displayportPorts) {
        double sizeInch = parseScreenSizeInch(screenSize);
        String sql = "UPDATE [spec_monitor] SET screen_size_inch = ?, resolution = ?, refresh_rate_hz = ?, panel_type = ?, "
                   + "response_time_ms = ?, adaptive_sync = ?, hdmi_ports = ?, displayport_ports = ? WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, sizeInch);
            ps.setString(2, (resolution == null || resolution.trim().isEmpty()) ? "1920x1080" : resolution.trim());
            ps.setInt(3, refreshRate > 0 ? refreshRate : 60);
            ps.setString(4, (panelType == null || panelType.trim().isEmpty()) ? "IPS" : panelType.trim());
            if (responseTimeMs == null) ps.setNull(5, Types.DECIMAL); else ps.setDouble(5, responseTimeMs);
            if (adaptiveSync == null || adaptiveSync.trim().isEmpty()) ps.setNull(6, Types.VARCHAR); else ps.setString(6, adaptiveSync.trim());
            ps.setInt(7, Math.max(0, hdmiPorts));
            ps.setInt(8, Math.max(0, displayportPorts));
            ps.setInt(9, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static double parseScreenSizeInch(String raw) {
        if (raw == null || raw.trim().isEmpty()) return 24.0;
        String cleaned = raw.replaceAll("[^0-9.]", "");
        try {
            double v = Double.parseDouble(cleaned);
            return v > 0 ? v : 24.0;
        } catch (NumberFormatException e) {
            return 24.0;
        }
    }

    public boolean insertFull(int partId, String screenSize, String resolution, int refreshRate, String panelType,
                             Double responseTimeMs, String adaptiveSync, int hdmiPorts, int displayportPorts,
                             String hdmiVersion, String displayportVersion) {
        double sizeInch = parseScreenSizeInch(screenSize);
        String sql = "INSERT INTO [spec_monitor] (part_id, screen_size_inch, resolution, refresh_rate_hz, panel_type, "
                   + "response_time_ms, adaptive_sync, hdmi_ports, displayport_ports, hdmi_version, displayport_version, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setDouble(2, sizeInch);
            ps.setString(3, (resolution == null || resolution.trim().isEmpty()) ? "1920x1080" : resolution.trim());
            ps.setInt(4, refreshRate > 0 ? refreshRate : 60);
            ps.setString(5, (panelType == null || panelType.trim().isEmpty()) ? "IPS" : panelType.trim());
            if (responseTimeMs == null) ps.setNull(6, Types.DECIMAL); else ps.setDouble(6, responseTimeMs);
            if (adaptiveSync == null || adaptiveSync.trim().isEmpty()) ps.setNull(7, Types.VARCHAR); else ps.setString(7, adaptiveSync.trim());
            ps.setInt(8, Math.max(0, hdmiPorts));
            ps.setInt(9, Math.max(0, displayportPorts));
            if (hdmiVersion == null || hdmiVersion.trim().isEmpty()) ps.setNull(10, Types.VARCHAR); else ps.setString(10, hdmiVersion.trim());
            if (displayportVersion == null || displayportVersion.trim().isEmpty()) ps.setNull(11, Types.VARCHAR); else ps.setString(11, displayportVersion.trim());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFull(int partId, String screenSize, String resolution, int refreshRate, String panelType,
                             Double responseTimeMs, String adaptiveSync, int hdmiPorts, int displayportPorts,
                             String hdmiVersion, String displayportVersion) {
        double sizeInch = parseScreenSizeInch(screenSize);
        String sql = "UPDATE [spec_monitor] SET screen_size_inch = ?, resolution = ?, refresh_rate_hz = ?, panel_type = ?, "
                   + "response_time_ms = ?, adaptive_sync = ?, hdmi_ports = ?, displayport_ports = ?, "
                   + "hdmi_version = ?, displayport_version = ? WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, sizeInch);
            ps.setString(2, (resolution == null || resolution.trim().isEmpty()) ? "1920x1080" : resolution.trim());
            ps.setInt(3, refreshRate > 0 ? refreshRate : 60);
            ps.setString(4, (panelType == null || panelType.trim().isEmpty()) ? "IPS" : panelType.trim());
            if (responseTimeMs == null) ps.setNull(5, Types.DECIMAL); else ps.setDouble(5, responseTimeMs);
            if (adaptiveSync == null || adaptiveSync.trim().isEmpty()) ps.setNull(6, Types.VARCHAR); else ps.setString(6, adaptiveSync.trim());
            ps.setInt(7, Math.max(0, hdmiPorts));
            ps.setInt(8, Math.max(0, displayportPorts));
            if (hdmiVersion == null || hdmiVersion.trim().isEmpty()) ps.setNull(9, Types.VARCHAR); else ps.setString(9, hdmiVersion.trim());
            if (displayportVersion == null || displayportVersion.trim().isEmpty()) ps.setNull(10, Types.VARCHAR); else ps.setString(10, displayportVersion.trim());
            ps.setInt(11, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
