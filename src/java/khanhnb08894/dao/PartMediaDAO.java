package khanhnb08894.dao;

import fourSome.model.PartMedia;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartMediaDAO {

    public List<PartMedia> getAll() {
        List<PartMedia> list = new ArrayList<>();
        String sql = "SELECT * FROM [part_media]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PartMedia obj = new PartMedia();
                obj.setMediaId(rs.getInt("media_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setMediaType(rs.getString("media_type"));
                obj.setMediaUrl(rs.getString("media_url"));
                obj.setIsPrimary(rs.getBoolean("is_primary"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
