package linhhqth08598.dao;

import fourSome.model.UsageProfiles;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsageProfilesDAO {

    public List<UsageProfiles> getAll() {
        List<UsageProfiles> list = new ArrayList<>();
        String sql = "SELECT * FROM [usage_profiles]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UsageProfiles obj = new UsageProfiles();
                obj.setProfileId(rs.getInt("profile_id"));
                obj.setProfileName(rs.getString("profile_name"));
                obj.setTargetGameList(rs.getString("target_game_list"));
                obj.setTargetResolution(rs.getString("target_resolution"));
                obj.setTargetFps(rs.getString("target_fps"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(String name, String games, String resolution, String fps) {
        String sql = "INSERT INTO usage_profiles (profile_name, target_game_list, target_resolution, target_fps, status) VALUES (?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, games);
            ps.setString(3, resolution);
            ps.setString(4, fps);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean update(int profileId, String name, String games, String resolution, String fps) {
        String sql = "UPDATE usage_profiles SET profile_name = ?, target_game_list = ?, target_resolution = ?, target_fps = ? WHERE profile_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, games);
            ps.setString(3, resolution);
            ps.setString(4, fps);
            ps.setInt(5, profileId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean setStatus(int profileId, String status) {
        String sql = "UPDATE usage_profiles SET status = ? WHERE profile_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, profileId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
