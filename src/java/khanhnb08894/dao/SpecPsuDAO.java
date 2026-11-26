package khanhnb08894.dao;

import fourSome.model.SpecPsu;
import fourSome.url.DBConnect;
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
    public boolean insert(int partId, int wattage, String efficiencyRating, String modularType) {
        return insert(partId, wattage, efficiencyRating, modularType, 2, false, 1, "ATX");
    }

    public boolean insert(int partId, int wattage, String efficiencyRating, String modularity,
                          int pcieConnectorsCount, boolean has12vhpwr, int epsConnectorsCount, String formFactor) {
        String sql = "INSERT INTO [spec_psu] (part_id, wattage, efficiency_rating, modularity, pcie_connectors_count, has_12vhpwr, eps_connectors_count, form_factor, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            ps.setInt(2, wattage);
            ps.setString(3, (efficiencyRating == null || efficiencyRating.trim().isEmpty()) ? "80 Plus Bronze" : efficiencyRating.trim());
            ps.setString(4, (modularity == null || modularity.trim().isEmpty()) ? "Non-Modular" : modularity.trim());
            ps.setInt(5, Math.max(0, pcieConnectorsCount));
            ps.setBoolean(6, has12vhpwr);
            ps.setInt(7, Math.max(1, epsConnectorsCount));
            ps.setString(8, (formFactor == null || formFactor.trim().isEmpty()) ? "ATX" : formFactor.trim());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int partId, int wattage, String efficiencyRating, String modularity,
                          int pcieConnectorsCount, boolean has12vhpwr, int epsConnectorsCount, String formFactor) {
        String sql = "UPDATE [spec_psu] SET wattage = ?, efficiency_rating = ?, modularity = ?, pcie_connectors_count = ?, "
                   + "has_12vhpwr = ?, eps_connectors_count = ?, form_factor = ? WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, wattage);
            ps.setString(2, (efficiencyRating == null || efficiencyRating.trim().isEmpty()) ? "80 Plus Bronze" : efficiencyRating.trim());
            ps.setString(3, (modularity == null || modularity.trim().isEmpty()) ? "Non-Modular" : modularity.trim());
            ps.setInt(4, Math.max(0, pcieConnectorsCount));
            ps.setBoolean(5, has12vhpwr);
            ps.setInt(6, Math.max(1, epsConnectorsCount));
            ps.setString(7, (formFactor == null || formFactor.trim().isEmpty()) ? "ATX" : formFactor.trim());
            ps.setInt(8, partId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
