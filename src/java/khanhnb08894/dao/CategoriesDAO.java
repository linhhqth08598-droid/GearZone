package khanhnb08894.dao;

import fourSome.model.Categories;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriesDAO {

    public List<Categories> getAll() {
        List<Categories> list = new ArrayList<>();
        String sql = "SELECT * FROM [categories]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categories obj = new Categories();
                obj.setCategoryId(rs.getInt("category_id"));
                obj.setCategoryCode(rs.getString("category_code"));
                obj.setCategoryName(rs.getString("category_name"));
                obj.setDescription(rs.getString("description"));
                obj.setIsRequiredInBuild(rs.getBoolean("is_required_in_build"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
        // Thêm danh mục mới
    public boolean insert(Categories category) {
        String sql = "INSERT INTO categories (category_code, category_name, description, is_required_in_build, status) "
                   + "VALUES (?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryCode());
            ps.setString(2, category.getCategoryName());
            ps.setString(3, category.getDescription());
            ps.setBoolean(4, category.getIsRequiredInBuild());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật danh mục
    public boolean update(Categories category) {
        String sql = "UPDATE categories SET category_code = ?, category_name = ?, description = ?, is_required_in_build = ?, status = ? WHERE category_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryCode());
            ps.setString(2, category.getCategoryName());
            ps.setString(3, category.getDescription());
            ps.setBoolean(4, category.getIsRequiredInBuild());
            ps.setString(5, category.getStatus());
            ps.setInt(6, category.getCategoryId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa mềm danh mục
    public boolean softDelete(int categoryId) {
        String sql = "UPDATE categories SET status = 'INACTIVE' WHERE category_id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra trùng mã code
    public boolean checkCodeExist(String code, int excludeId) {
        String sql = "SELECT 1 FROM categories WHERE category_code = ? AND category_id != ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
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
