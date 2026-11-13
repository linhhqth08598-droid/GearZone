package linhhqth08598.dao;

import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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

    // Cập nhật lại toàn bộ quyền cho một Role (Xóa cũ, thêm mới)
    public boolean updateRolePermissions(int roleId, String[] permissionIds) {
        String deleteSql = "DELETE FROM role_permissions WHERE role_id = ?";
        String insertSql = "INSERT INTO role_permissions (role_id, permission_id, status) VALUES (?, ?, 'ACTIVE')";
        
        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false); // Dùng Transaction đảm bảo an toàn dữ liệu
            
            try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                psDel.setInt(1, roleId);
                psDel.executeUpdate();
            }

            if (permissionIds != null) {
                try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                    for (String pId : permissionIds) {
                        psIns.setInt(1, roleId);
                        psIns.setInt(2, Integer.parseInt(pId));
                        psIns.addBatch();
                    }
                    psIns.executeBatch();
                }
            }
            
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}