package DAO;

import Models.Brand;
import Url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {
    // Lấy toàn bộ Hãng
    public List<Brand> getAllBrands() {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT * FROM Brands";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Brand b = new Brand();
                b.setBrandId(rs.getInt("BrandID"));
                b.setBrandName(rs.getString("BrandName"));
                b.setCategoryId(rs.getInt("CategoryID"));
                list.add(b);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Brand> getBrandsByCategory(int categoryId) {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT * FROM Brands WHERE CategoryID = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Brand b = new Brand();
                    b.setBrandId(rs.getInt("BrandID"));
                    b.setBrandName(rs.getString("BrandName"));
                    b.setCategoryId(rs.getInt("CategoryID"));
                    list.add(b);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void insertBrand(String name, int categoryId) {
        String sql = "INSERT INTO Brands (BrandName, CategoryID) VALUES (?, ?)";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name); ps.setInt(2, categoryId); ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateBrand(int id, String name, int categoryId) {
        String sql = "UPDATE Brands SET BrandName = ?, CategoryID = ? WHERE BrandID = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name); ps.setInt(2, categoryId); ps.setInt(3, id); ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void deleteBrand(int id) {
        String sql = "DELETE FROM Brands WHERE BrandID = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}