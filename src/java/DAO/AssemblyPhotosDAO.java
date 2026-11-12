package DAO;

import Models.AssemblyPhotos;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssemblyPhotosDAO {

    public List<AssemblyPhotos> getAll() {
        List<AssemblyPhotos> list = new ArrayList<>();
        String sql = "SELECT * FROM [assembly_photos]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssemblyPhotos ap = new AssemblyPhotos();
                    ap.setPhotoId(rs.getInt("photo_id"));
                    ap.setAssemblyId(rs.getInt("assembly_id"));
                    ap.setPhotoUrl(rs.getString("photo_url"));
                    ap.setStatus(rs.getString("status"));
                    list.add(ap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
