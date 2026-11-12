package DAO;

import Models.SpecPsu;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecPsuDAO {

    public List<SpecPsu> getAll() {
        List<SpecPsu> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_psu]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecPsu sp = new SpecPsu();
                    sp.setPartId(rs.getInt("part_id"));
                    sp.setWattage(rs.getInt("wattage"));
                    sp.setEfficiencyRating(rs.getString("efficiency_rating"));
                    sp.setModularity(rs.getString("modularity"));
                    sp.setPcieConnectorsCount(rs.getInt("pcie_connectors_count"));
                    sp.setHas12vhpwr(rs.getBoolean("has_12vhpwr"));
                    sp.setEpsConnectorsCount(rs.getInt("eps_connectors_count"));
                    sp.setFormFactor(rs.getString("form_factor"));
                    sp.setStatus(rs.getString("status"));
                    list.add(sp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
