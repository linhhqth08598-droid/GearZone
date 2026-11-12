package DAO;

import Models.SpecStorage;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecStorageDAO {

    public List<SpecStorage> getAll() {
        List<SpecStorage> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_storage]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecStorage ss = new SpecStorage();
                    ss.setPartId(rs.getInt("part_id"));
                    ss.setStorageType(rs.getString("storage_type"));
                    ss.setFormFactor(rs.getString("form_factor"));
                    ss.set_interface(rs.getString("interface"));
                    ss.setCapacityGb(rs.getInt("capacity_gb"));
                    ss.setReadSpeedMbps(rs.getInt("read_speed_mbps"));
                    ss.setWriteSpeedMbps(rs.getInt("write_speed_mbps"));
                    ss.setStatus(rs.getString("status"));
                    list.add(ss);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
