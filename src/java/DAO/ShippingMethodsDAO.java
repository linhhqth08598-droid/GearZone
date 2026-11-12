package DAO;

import Models.ShippingMethods;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShippingMethodsDAO {

    public List<ShippingMethods> getAll() {
        List<ShippingMethods> list = new ArrayList<>();
        String sql = "SELECT * FROM [shipping_methods]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ShippingMethods sm = new ShippingMethods();
                    sm.setShippingMethodId(rs.getInt("shipping_method_id"));
                    sm.setMethodCode(rs.getString("method_code"));
                    sm.setMethodName(rs.getString("method_name"));
                    sm.setCarrierCode(rs.getString("carrier_code"));
                    sm.setBaseFee(rs.getDouble("base_fee"));
                    sm.setEstimatedDays(rs.getInt("estimated_days"));
                    sm.setStatus(rs.getString("status"));
                    list.add(sm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
