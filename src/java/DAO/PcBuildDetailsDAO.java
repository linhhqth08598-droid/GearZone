package DAO;

import Models.PcBuildDetails;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PcBuildDetailsDAO {

    public List<PcBuildDetails> getAll() {
        List<PcBuildDetails> list = new ArrayList<>();
        String sql = "SELECT * FROM [pc_build_details]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PcBuildDetails pbd = new PcBuildDetails();
                    pbd.setBuildDetailId(rs.getInt("build_detail_id"));
                    pbd.setBuildId(rs.getInt("build_id"));
                    pbd.setPartId(rs.getInt("part_id"));
                    pbd.setQuantity(rs.getInt("quantity"));
                    pbd.setUnitPrice(rs.getDouble("unit_price"));
                    pbd.setStatus(rs.getString("status"));
                    list.add(pbd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
