package linhhqth08598.dao;

import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserRolesDAO {
    
    public boolean assignRole(int userId, int roleId) {
        String sql = "INSERT INTO user_roles (user_id, role_id, assigned_at, status) VALUES (?, ?, GETDATE(), 'ACTIVE')";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách các mã quyền (role_code) của 1 user
    public List<String> getRoleCodesByUserId(int userId) {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT r.role_code FROM user_roles ur " +
                     "JOIN roles r ON ur.role_id = r.role_id " +
                     "WHERE ur.user_id = ? AND ur.status = 'ACTIVE' AND r.status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("role_code"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }

    // ====== Dành cho trang Quản lý người dùng ======

    /** Lấy vai trò (đầy đủ) của nhiều user cùng lúc để hiển thị trong bảng. */
    public java.util.Map<Integer, java.util.List<fourSome.model.Roles>> getRolesOfUsers(java.util.List<Integer> userIds) {
        java.util.Map<Integer, java.util.List<fourSome.model.Roles>> map = new java.util.HashMap<>();
        if (userIds == null || userIds.isEmpty()) return map;
        StringBuilder in = new StringBuilder();
        for (int i = 0; i < userIds.size(); i++) in.append(i == 0 ? "?" : ",?");
        String sql = "SELECT ur.user_id, r.role_id, r.role_code, r.role_name FROM user_roles ur "
                   + "JOIN roles r ON r.role_id = ur.role_id "
                   + "WHERE ur.status = 'ACTIVE' AND r.status = 'ACTIVE' AND ur.user_id IN (" + in + ") ORDER BY r.role_id";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < userIds.size(); i++) ps.setInt(i + 1, userIds.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    fourSome.model.Roles r = new fourSome.model.Roles();
                    r.setRoleId(rs.getInt("role_id"));
                    r.setRoleCode(rs.getString("role_code"));
                    r.setRoleName(rs.getString("role_name"));
                    map.computeIfAbsent(rs.getInt("user_id"), k -> new ArrayList<>()).add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<Integer> getRoleIdsByUserId(int userId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT role_id FROM user_roles WHERE user_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    /** Thay toàn bộ vai trò của user bằng danh sách mới (1 transaction). */
    public boolean replaceRoles(int userId, List<Integer> roleIds) {
        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM user_roles WHERE user_id = ?")) {
                    del.setInt(1, userId);
                    del.executeUpdate();
                }
                try (PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO user_roles (user_id, role_id, assigned_at, status) VALUES (?, ?, GETDATE(), 'ACTIVE')")) {
                    for (Integer rid : new java.util.LinkedHashSet<>(roleIds)) {
                        ins.setInt(1, userId);
                        ins.setInt(2, rid);
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
