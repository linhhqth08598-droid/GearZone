package khanhnb08894.dao;

import fourSome.model.PartMedia;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartMediaDAO {

    public List<PartMedia> getAll() {
        List<PartMedia> list = new ArrayList<>();
        String sql = "SELECT * FROM [part_media]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PartMedia obj = new PartMedia();
                obj.setMediaId(rs.getInt("media_id"));
                obj.setPartId(rs.getInt("part_id"));
                obj.setMediaType(rs.getString("media_type"));
                obj.setMediaUrl(rs.getString("media_url"));
                obj.setIsPrimary(rs.getBoolean("is_primary"));
                obj.setStatus(rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Đặt ảnh chính của linh kiện (xóa cờ chính cũ, thêm ảnh mới làm ảnh chính). */
    public boolean setPrimary(int partId, String url) {
        if (url == null || url.trim().isEmpty()) return false;
        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("UPDATE part_media SET is_primary = 0 WHERE part_id = ?")) {
                    ps.setInt(1, partId); ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO part_media (part_id, media_type, media_url, is_primary, status) VALUES (?, 'IMAGE', ?, 1, 'ACTIVE')")) {
                    ps.setInt(1, partId); ps.setString(2, url.trim()); ps.executeUpdate();
                }
                con.commit();
                return true;
            } catch (Exception e) { con.rollback(); throw e; }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    /** Lấy part_id theo mã sản phẩm (dùng ngay sau khi thêm mới). */
    public int findPartIdByCode(String productCode) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT part_id FROM parts WHERE product_code = ?")) {
            ps.setString(1, productCode);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    /** Ảnh chính (đang hiển thị) của 1 linh kiện, null nếu chưa có. */
    public String getPrimaryUrl(int partId) {
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT TOP 1 media_url FROM part_media WHERE part_id = ? AND status = 'ACTIVE' AND media_type = 'IMAGE' ORDER BY is_primary DESC, media_id DESC")) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString(1); }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
