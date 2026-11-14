package khanhnb08894.dao;

import fourSome.model.Brands;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandsDAO {

        // Lấy tất cả thương hiệu (Admin)
    public List<Brands> getAll() {
        List<Brands> list = new ArrayList<>();
        String sql = "SELECT * FROM brands ORDER BY brand_id DESC";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Brands b = new Brands();
                b.setBrandId(rs.getInt("brand_id"));
                b.setBrandName(rs.getString("brand_name"));
                b.setCountry(rs.getString("country"));
                b.setLogoUrl(rs.getString("logo_url"));
                b.setStatus(rs.getString("status"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm thương hiệu mới
    public boolean insert(Brands brand) {
        String sql = "INSERT INTO brands (brand_name, country, logo_url, status) VALUES (?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, brand.getBrandName());
            ps.setString(2, brand.getCountry());
            ps.setString(3, brand.getLogoUrl());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thương hiệu
    public boolean update(Brands brand) {
        String sql = "UPDATE brands SET brand_name = ?, country = ?, logo_url = ?, status = ? WHERE brand_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, brand.getBrandName());
            ps.setString(2, brand.getCountry());
            ps.setString(3, brand.getLogoUrl());
            ps.setString(4, brand.getStatus());
            ps.setInt(5, brand.getBrandId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa mềm (đổi trạng thái thành INACTIVE)
    public boolean softDelete(int brandId) {
        String sql = "UPDATE brands SET status = 'INACTIVE' WHERE brand_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, brandId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Kiểm tra trùng tên
    public boolean checkNameExist(String brandName, int excludeId) {
        String sql = "SELECT 1 FROM brands WHERE brand_name = ? AND brand_id != ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, brandName);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
