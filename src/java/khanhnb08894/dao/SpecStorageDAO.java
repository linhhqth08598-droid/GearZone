package khanhnb08894.dao;

import fourSome.model.SpecStorage;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecStorageDAO {

    public List<SpecStorage> getAll() {
        List<SpecStorage> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_storage]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecStorage obj = new SpecStorage();
                obj.setPartId(rs.getInt("part_id"));
                obj.setStorageType(rs.getString("storage_type"));
                obj.setFormFactor(rs.getString("form_factor"));
                obj.set_interface(rs.getString("interface"));
                obj.setCapacityGb(rs.getInt("capacity_gb"));
                obj.setReadSpeedMbps(rs.getInt("read_speed_mbps"));
                obj.setWriteSpeedMbps(rs.getInt("write_speed_mbps"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
