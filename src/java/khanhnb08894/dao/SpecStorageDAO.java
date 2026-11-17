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
    public boolean insert(int partId, String storageType, int capacityGb) {
    String sql = "INSERT INTO spec_storage (part_id, storage_type, capacity_gb) VALUES (?, ?, ?)";
    try (Connection conn = new DBConnect().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, partId);
        ps.setString(2, storageType);
        ps.setInt(3, capacityGb);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}
