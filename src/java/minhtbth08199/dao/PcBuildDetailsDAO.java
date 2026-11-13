package minhtbth08199.dao;

import fourSome.model.PcBuildDetails;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PcBuildDetailsDAO {

    public List<PcBuildDetails> getAll() {
        List<PcBuildDetails> list = new ArrayList<>();
        String sql = "SELECT * FROM [pc_build_details]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PcBuildDetails obj = new PcBuildDetails();
                obj.setBuildDetailId(rs.getInt("build_detail_id"));
                obj.setBuildId(rs.getInt("build_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setQuantity(rs.getInt("quantity"));
                obj.setUnitPrice(rs.getDouble("unit_price"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
