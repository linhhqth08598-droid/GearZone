package dungntth08581.dao;

import model.ShippingAddresses;
import url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShippingAddressesDAO {

    public List<ShippingAddresses> getAll() {
        List<ShippingAddresses> list = new ArrayList<>();
        String sql = "SELECT * FROM [shipping_addresses]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ShippingAddresses obj = new ShippingAddresses();
                obj.setAddressId(rs.getInt("address_id"));
                obj.setUserId(rs.getInt("user_id"));
                obj.setRecipientName(rs.getString("recipient_name"));
                obj.setPhone(rs.getString("phone"));
                obj.setProvince(rs.getString("province"));
                obj.setDistrict(rs.getString("district"));
                obj.setWard(rs.getString("ward"));
                obj.setDetailedAddress(rs.getString("detailed_address"));
                obj.setIsDefault(rs.getBoolean("is_default"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
