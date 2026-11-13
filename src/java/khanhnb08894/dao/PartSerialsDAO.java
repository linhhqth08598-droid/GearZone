package khanhnb08894.dao;

import model.PartSerials;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartSerialsDAO {

    public List<PartSerials> getAll() {
        List<PartSerials> list = new ArrayList<>();
        String sql = "SELECT * FROM [part_serials]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PartSerials obj = new PartSerials();
                obj.setSerialId(rs.getInt("serial_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setSerialNumber(rs.getString("serial_number"));
                obj.setStatus(rs.getString("status"));
                obj.setImportedAt(rs.getDate("imported_at"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
