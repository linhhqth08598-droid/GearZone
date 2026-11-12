package DAO;

import Models.UsageProfiles;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsageProfilesDAO {

    public List<UsageProfiles> getAll() {
        List<UsageProfiles> list = new ArrayList<>();
        String sql = "SELECT * FROM [usage_profiles]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsageProfiles up = new UsageProfiles();
                    up.setProfileId(rs.getInt("profile_id"));
                    up.setProfileName(rs.getString("profile_name"));
                    up.setTargetGameList(rs.getString("target_game_list"));
                    up.setTargetResolution(rs.getString("target_resolution"));
                    up.setTargetFps(rs.getString("target_fps"));
                    up.setStatus(rs.getString("status"));
                    list.add(up);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
