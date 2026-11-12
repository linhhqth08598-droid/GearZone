package DAO;

import Models.PartSerials;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartSerialsDAO {

    public List<PartSerials> getAll() {
        List<PartSerials> list = new ArrayList<>();
        String sql = "SELECT * FROM [part_serials]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PartSerials psObj = new PartSerials();
                    psObj.setSerialId(rs.getInt("serial_id"));
                    psObj.setPartId(rs.getInt("part_id"));
                    psObj.setSerialNumber(rs.getString("serial_number"));
                    psObj.setStatus(rs.getString("status"));
                    psObj.setImportedAt(rs.getDate("imported_at"));
                    list.add(psObj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
