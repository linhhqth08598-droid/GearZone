package DAO;

import Models.ShippingAddresses;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShippingAddressesDAO {

    public List<ShippingAddresses> getAll() {
        List<ShippingAddresses> list = new ArrayList<>();
        String sql = "SELECT * FROM [shipping_addresses]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ShippingAddresses sa = new ShippingAddresses();
                    sa.setAddressId(rs.getInt("address_id"));
                    sa.setUserId(rs.getInt("user_id"));
                    sa.setRecipientName(rs.getString("recipient_name"));
                    sa.setPhone(rs.getString("phone"));
                    sa.setProvince(rs.getString("province"));
                    sa.setDistrict(rs.getString("district"));
                    sa.setWard(rs.getString("ward"));
                    sa.setDetailedAddress(rs.getString("detailed_address"));
                    sa.setIsDefault(rs.getBoolean("is_default"));
                    sa.setStatus(rs.getString("status"));
                    list.add(sa);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
