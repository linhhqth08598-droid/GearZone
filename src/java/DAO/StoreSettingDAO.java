package DAO;

import Models.StoreSetting;
import Url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StoreSettingDAO {

    // Lấy thông tin cấu hình cửa hàng (lấy record đầu tiên)
    public StoreSetting getStoreSetting() {
        StoreSetting setting = null;
        String sql = "SELECT TOP 1 * FROM Store_Settings ORDER BY SettingID ASC";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                setting = new StoreSetting();
                setting.setSettingId(rs.getInt("SettingID"));
                setting.setCompanyName(rs.getString("CompanyName"));
                setting.setBusinessLicense(rs.getString("BusinessLicense"));
                setting.setAddress1(rs.getString("Address1"));
                setting.setAddress2(rs.getString("Address2"));
                setting.setHotline(rs.getString("Hotline"));
                setting.setWebsiteUrl(rs.getString("WebsiteUrl"));
                setting.setFanpageUrl(rs.getString("FanpageUrl"));
                setting.setEmail(rs.getString("Email"));
                setting.setGoogleMapIframe(rs.getString("GoogleMapIframe"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return setting;
    }

    // Cập nhật thông tin cửa hàng
    public void updateStoreSetting(StoreSetting s) {
        String sql = "UPDATE Store_Settings SET CompanyName=?, BusinessLicense=?, Address1=?, Address2=?, Hotline=?, WebsiteUrl=?, FanpageUrl=?, Email=?, GoogleMapIframe=?, UpdatedAt=GETDATE() WHERE SettingID=?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getCompanyName());
            ps.setString(2, s.getBusinessLicense());
            ps.setString(3, s.getAddress1());
            ps.setString(4, s.getAddress2());
            ps.setString(5, s.getHotline());
            ps.setString(6, s.getWebsiteUrl());
            ps.setString(7, s.getFanpageUrl());
            ps.setString(8, s.getEmail());
            ps.setString(9, s.getGoogleMapIframe());
            ps.setInt(10, s.getSettingId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}