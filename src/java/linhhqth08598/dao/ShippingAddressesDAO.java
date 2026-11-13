package linhhqth08598.dao;

import fourSome.model.ShippingAddresses;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShippingAddressesDAO {

    public List<ShippingAddresses> findByUserId(int userId) {
        List<ShippingAddresses> list = new ArrayList<>();
        String sql = "SELECT * FROM shipping_addresses WHERE user_id = ? AND status = 'ACTIVE' ORDER BY is_default DESC, address_id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
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
                    list.add(sa);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(ShippingAddresses address) {
        if (address.getIsDefault()) clearDefaultAddress(address.getUserId());
        String sql = "INSERT INTO shipping_addresses (user_id, recipient_name, phone, province, district, ward, detailed_address, is_default, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, address.getUserId());
            ps.setString(2, address.getRecipientName());
            ps.setString(3, address.getPhone());
            ps.setString(4, address.getProvince());
            ps.setString(5, address.getDistrict());
            ps.setString(6, address.getWard());
            ps.setString(7, address.getDetailedAddress());
            ps.setBoolean(8, address.getIsDefault());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(ShippingAddresses address) {
        if (address.getIsDefault()) clearDefaultAddress(address.getUserId());
        String sql = "UPDATE shipping_addresses SET recipient_name = ?, phone = ?, province = ?, district = ?, ward = ?, detailed_address = ?, is_default = ? " +
                     "WHERE address_id = ? AND user_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, address.getRecipientName());
            ps.setString(2, address.getPhone());
            ps.setString(3, address.getProvince());
            ps.setString(4, address.getDistrict());
            ps.setString(5, address.getWard());
            ps.setString(6, address.getDetailedAddress());
            ps.setBoolean(7, address.getIsDefault());
            ps.setInt(8, address.getAddressId());
            ps.setInt(9, address.getUserId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int addressId, int userId) {
        String sql = "UPDATE shipping_addresses SET status = 'INACTIVE' WHERE address_id = ? AND user_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean setDefault(int addressId, int userId) {
        clearDefaultAddress(userId);
        String sql = "UPDATE shipping_addresses SET is_default = 1 WHERE address_id = ? AND user_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private void clearDefaultAddress(int userId) {
        String sql = "UPDATE shipping_addresses SET is_default = 0 WHERE user_id = ?";
        try (Connection conn = DBConnect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}