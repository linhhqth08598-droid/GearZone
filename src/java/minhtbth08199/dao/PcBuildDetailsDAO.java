package minhtbth08199.dao;

import fourSome.model.PcBuildDetails;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PcBuildDetailsDAO {

    private PcBuildDetails map(ResultSet rs) throws SQLException {
        PcBuildDetails obj = new PcBuildDetails();
        obj.setBuildDetailId(rs.getInt("build_detail_id")); obj.setBuildId(rs.getInt("build_id"));
        obj.setPartId(rs.getInt("part_id")); obj.setQuantity(rs.getInt("quantity"));
        obj.setUnitPrice(rs.getDouble("unit_price")); obj.setStatus(rs.getString("status")); return obj;
    }

    public List<PcBuildDetails> getAll() {
        List<PcBuildDetails> list = new ArrayList<>();
        String sql = "SELECT * FROM [pc_build_details]";

        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<PcBuildDetails> findByBuildId(int buildId) {
        List<PcBuildDetails> list = new ArrayList<>();
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM pc_build_details WHERE build_id = ? AND status = 'ACTIVE'")) {
            ps.setInt(1, buildId); try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { throw new IllegalStateException("Không thể đọc linh kiện cấu hình", e); }
        return list;
    }

    public void upsert(Connection con, int buildId, int partId, int quantity, double price) throws SQLException {
        String sql = "UPDATE pc_build_details SET quantity = ?, unit_price = ?, status = 'ACTIVE' WHERE build_id = ? AND part_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) { ps.setInt(1, quantity); ps.setDouble(2, price); ps.setInt(3, buildId); ps.setInt(4, partId);
            if (ps.executeUpdate() == 0) try (PreparedStatement add = con.prepareStatement("INSERT INTO pc_build_details (build_id, part_id, quantity, unit_price, status) VALUES (?, ?, ?, ?, 'ACTIVE')")) {
                add.setInt(1, buildId); add.setInt(2, partId); add.setInt(3, quantity); add.setDouble(4, price); add.executeUpdate();
            }
        }
    }

    public boolean remove(Connection con, int buildId, int partId) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE pc_build_details SET status = 'REMOVED' WHERE build_id = ? AND part_id = ? AND status = 'ACTIVE'")) { ps.setInt(1, buildId); ps.setInt(2, partId); return ps.executeUpdate() > 0; }
    }

    /** Mỗi ô Build PC chỉ giữ một linh kiện; RAM vẫn có quantity riêng. */
    public void removeOtherPartsInCategory(Connection con, int buildId, int categoryId, int keepPartId) throws SQLException {
        String sql = "UPDATE d SET d.status = 'REMOVED' FROM pc_build_details d "
                + "JOIN parts p ON p.part_id = d.part_id "
                + "WHERE d.build_id = ? AND d.status = 'ACTIVE' AND p.category_id = ? AND d.part_id <> ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buildId); ps.setInt(2, categoryId); ps.setInt(3, keepPartId); ps.executeUpdate();
        }
    }
}
