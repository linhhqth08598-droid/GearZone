package minhtbth08199.dao;

import model.UsageProfiles;
import url.DBConnect;
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
}
