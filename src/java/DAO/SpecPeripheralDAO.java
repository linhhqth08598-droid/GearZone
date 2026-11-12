package DAO;

import Models.SpecPeripheral;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecPeripheralDAO {

    public List<SpecPeripheral> getAll() {
        List<SpecPeripheral> list = new ArrayList<>();
        String sql = "SELECT * FROM [spec_peripheral]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SpecPeripheral sp = new SpecPeripheral();
                    sp.setPartId(rs.getInt("part_id"));
                    sp.setPeripheralType(rs.getString("peripheral_type"));
                    sp.setConnectionType(rs.getString("connection_type"));
                    sp.setSwitchType(rs.getString("switch_type"));
                    sp.setLayout(rs.getString("layout"));
                    sp.setDpiMax(rs.getInt("dpi_max"));
                    sp.setPollingRateHz(rs.getInt("polling_rate_hz"));
                    sp.setWeightG(rs.getInt("weight_g"));
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
