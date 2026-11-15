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
                    sa.setWard(rs.getString("ward"));
                    sa.setDetailedAddress(rs.getString("detailed_address"));
                    sa.setIsDefault(rs.getBoolean("is_default"));
                    list.add(sa);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Lỗi SQL gần nhất của thao tác lưu (để hiện cho người dùng/dev biết nguyên nhân thật). */
    private static final ThreadLocal<String> LAST_ERROR = new ThreadLocal<>();
    public static String getLastError() { return LAST_ERROR.get(); }

    /** Bỏ mặc định cũ + ghi địa chỉ trong CÙNG một giao dịch: lỗi thì không mất địa chỉ mặc định cũ. */
    public boolean insert(ShippingAddresses address) {
        String sql = "INSERT INTO shipping_addresses (user_id, recipient_name, phone, province, ward, detailed_address, is_default, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        return save(address, sql, false);
    }

    public boolean update(ShippingAddresses address) {
        String sql = "UPDATE shipping_addresses SET recipient_name = ?, phone = ?, province = ?, ward = ?, detailed_address = ?, is_default = ? "
                   + "WHERE address_id = ? AND user_id = ? AND status = 'ACTIVE'";
        return save(address, sql, true);
    }

    private boolean save(ShippingAddresses a, String sql, boolean isUpdate) {
        LAST_ERROR.remove();
        boolean makeDefault = Boolean.TRUE.equals(a.getIsDefault());
        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (makeDefault) {
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE shipping_addresses SET is_default = 0 WHERE user_id = ?")) {
                        ps.setInt(1, a.getUserId());
                        ps.executeUpdate();
                    }
                }
                int rows;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    if (isUpdate) {
                        ps.setString(1, a.getRecipientName());
                        ps.setString(2, a.getPhone());
                        ps.setString(3, a.getProvince());
                        ps.setString(4, a.getWard());
                        ps.setString(5, a.getDetailedAddress());
                        ps.setBoolean(6, makeDefault);
                        ps.setInt(7, a.getAddressId());
                        ps.setInt(8, a.getUserId());
                    } else {
                        ps.setInt(1, a.getUserId());
                        ps.setString(2, a.getRecipientName());
                        ps.setString(3, a.getPhone());
                        ps.setString(4, a.getProvince());
                        ps.setString(5, a.getWard());
                        ps.setString(6, a.getDetailedAddress());
                        ps.setBoolean(7, makeDefault);
                    }
                    rows = ps.executeUpdate();
                }
                if (rows > 0) { conn.commit(); return true; }
                conn.rollback();
                LAST_ERROR.set(isUpdate ? "không tìm thấy địa chỉ cần sửa (đã bị xóa hoặc không thuộc tài khoản này)" : "không ghi được dòng nào");
                return false;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LAST_ERROR.set(e.getMessage());
            return false;
        }
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