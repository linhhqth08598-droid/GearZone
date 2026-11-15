package linhhqth08598.dao;

import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RolePermissionsDAO {

    // Lấy danh sách permission_id mà 1 role đang sở hữu
    public List<Integer> getPermissionIdsByRoleId(int roleId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT permission_id FROM role_permissions WHERE role_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("permission_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tập mã quyền (permission_code) mà 1 user có được thông qua tất cả vai trò đang ACTIVE
    public Set<String> getPermissionCodesByUserId(int userId) {
        Set<String> codes = new HashSet<>();
        String sql = "SELECT DISTINCT p.permission_code "
                   + "FROM user_roles ur "
                   + "JOIN roles r ON r.role_id = ur.role_id AND r.status = 'ACTIVE' "
                   + "JOIN role_permissions rp ON rp.role_id = ur.role_id AND rp.status = 'ACTIVE' "
                   + "JOIN permissions p ON p.permission_id = rp.permission_id AND p.status = 'ACTIVE' "
                   + "WHERE ur.user_id = ? AND ur.status = 'ACTIVE'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    codes.add(rs.getString("permission_code"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codes;
    }

    // Cập nhật lại toàn bộ quyền cho một Role (xóa cũ, thêm mới) trong 1 transaction
    public boolean updateRolePermissions(int roleId, String[] permissionIds) {
        String deleteSql = "DELETE FROM role_permissions WHERE role_id = ?";
        String insertSql = "INSERT INTO role_permissions (role_id, permission_id, status) VALUES (?, ?, 'ACTIVE')";

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                    psDel.setInt(1, roleId);
                    psDel.executeUpdate();
                }
                if (permissionIds != null) {
                    Set<Integer> unique = new HashSet<>();
                    for (String pId : permissionIds) {
                        unique.add(Integer.parseInt(pId.trim()));
                    }
                    try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                        for (Integer pId : unique) {
                            psIns.setInt(1, roleId);
                            psIns.setInt(2, pId);
                            psIns.addBatch();
                        }
                        psIns.executeBatch();
                    }
                }
                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback(); // lỗi giữa chừng thì trả lại quyền cũ, không để mất sạch quyền
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
