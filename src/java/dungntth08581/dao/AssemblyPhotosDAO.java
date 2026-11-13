package dungntth08581.dao;

import model.AssemblyPhotos;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssemblyPhotosDAO {

    public List<AssemblyPhotos> getAll() {
        List<AssemblyPhotos> list = new ArrayList<>();
        String sql = "SELECT * FROM [assembly_photos]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AssemblyPhotos obj = new AssemblyPhotos();
                obj.setPhotoId(rs.getInt("photo_id"));
                obj.setAssemblyId(rs.getInt("assembly_id"));
                obj.setPhotoUrl(rs.getString("photo_url"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
