package DAO;

import Models.PartMedia;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartMediaDAO {

    public List<PartMedia> getAll() {
        List<PartMedia> list = new ArrayList<>();
        String sql = "SELECT * FROM [part_media]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PartMedia pm = new PartMedia();
                    pm.setMediaId(rs.getInt("media_id"));
                    pm.setPartId(rs.getInt("part_id"));
                    pm.setMediaType(rs.getString("media_type"));
                    pm.setMediaUrl(rs.getString("media_url"));
                    pm.setIsPrimary(rs.getBoolean("is_primary"));
                    pm.setStatus(rs.getString("status"));
                    list.add(pm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
