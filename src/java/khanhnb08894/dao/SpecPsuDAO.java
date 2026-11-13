package khanhnb08894.dao;

import model.SpecPsu;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecPsuDAO {

    public List<SpecPsu> getAll() {
        List<SpecPsu> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_psu]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SpecPsu obj = new SpecPsu();
                obj.setPartId(rs.getInt("part_id"));
                obj.setWattage(rs.getInt("wattage"));
                obj.setEfficiencyRating(rs.getString("efficiency_rating"));
                obj.setModularity(rs.getString("modularity"));
                obj.setPcieConnectorsCount(rs.getInt("pcie_connectors_count"));
                obj.setHas12vhpwr(rs.getBoolean("has_12vhpwr"));
                obj.setEpsConnectorsCount(rs.getInt("eps_connectors_count"));
                obj.setFormFactor(rs.getString("form_factor"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
