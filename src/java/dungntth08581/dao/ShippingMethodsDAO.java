package dungntth08581.dao;

import model.ShippingMethods;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShippingMethodsDAO {

    public List<ShippingMethods> getAll() {
        List<ShippingMethods> list = new ArrayList<>();
        String sql = "SELECT * FROM [shipping_methods]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ShippingMethods obj = new ShippingMethods();
                obj.setShippingMethodId(rs.getInt("shipping_method_id"));
                obj.setMethodCode(rs.getString("method_code"));
                obj.setMethodName(rs.getString("method_name"));
                obj.setCarrierCode(rs.getString("carrier_code"));
                obj.setBaseFee(rs.getDouble("base_fee"));
                obj.setEstimatedDays(rs.getInt("estimated_days"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
