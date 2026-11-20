package khanhnb08894.dao;

import fourSome.model.PartSerials;
import fourSome.url.DBConnect;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý số Serial (S/N) từng chiếc linh kiện.
 * Trạng thái: IN_STOCK -> SOLD -> (RETURNED / DEFECTIVE).
 * Các hàm có tham số Connection dùng chung giao dịch của nơi gọi (đơn hàng, nhập kho).
 */
public class PartSerialsDAO {

    private PartSerials map(ResultSet rs) throws SQLException {
        PartSerials obj = new PartSerials();
        obj.setSerialId(rs.getInt("serial_id"));
        obj.setPartId(rs.getInt("part_id"));
        obj.setSerialNumber(rs.getString("serial_number"));
        obj.setStatus(rs.getString("status"));
        obj.setImportedAt(rs.getTimestamp("imported_at"));
        return obj;
    }

    public List<PartSerials> getAll() {
        List<PartSerials> list = new ArrayList<>();
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM [part_serials]");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Kiểm tra mã serial đã tồn tại trong hệ thống chưa. */
    public boolean exists(Connection con, String serialNumber) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM part_serials WHERE serial_number = ?")) {
            ps.setString(1, serialNumber);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public boolean exists(String serialNumber) {
        try (Connection con = DBConnect.getConnection()) {
            return exists(con, serialNumber);
        } catch (Exception e) { e.printStackTrace(); return true; }
    }

    /** Thêm nhiều serial cùng lúc (trạng thái IN_STOCK). Ném lỗi nếu có mã trùng. */
    public void insertBatch(Connection con, int partId, List<String> serials) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO part_serials (part_id, serial_number, status, imported_at) VALUES (?, ?, 'IN_STOCK', SYSDATETIME())")) {
            for (String sn : serials) {
                ps.setInt(1, partId);
                ps.setString(2, sn);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<PartSerials> getByPart(int partId, String status) {
        List<PartSerials> list = new ArrayList<>();
        boolean f = status != null && !status.isEmpty();
        String sql = "SELECT * FROM part_serials WHERE part_id = ?" + (f ? " AND status = ?" : "")
                + " ORDER BY imported_at ASC, serial_id ASC";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            if (f) ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateStatus(Connection con, String serialNumber, String status) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE part_serials SET status = ? WHERE serial_number = ?")) {
            ps.setString(1, status);
            ps.setString(2, serialNumber);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Bốc {@code qty} serial IN_STOCK cũ nhất của linh kiện, đổi sang SOLD.
     * Trả về danh sách mã (có thể ít hơn qty nếu kho chưa nhập đủ serial - dữ liệu cũ).
     */
    public List<String> pickForSale(Connection con, int partId, int qty) throws SQLException {
        List<String> picked = new ArrayList<>();
        // UPDLOCK + READPAST: 2 đơn duyệt cùng lúc không bốc trùng 1 serial
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT TOP (?) serial_id, serial_number FROM part_serials WITH (UPDLOCK, READPAST) "
              + "WHERE part_id = ? AND status = 'IN_STOCK' ORDER BY imported_at ASC, serial_id ASC")) {
            ps.setInt(1, qty);
            ps.setInt(2, partId);
            List<Integer> ids = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { ids.add(rs.getInt(1)); picked.add(rs.getString(2)); }
            }
            try (PreparedStatement up = con.prepareStatement("UPDATE part_serials SET status = 'SOLD' WHERE serial_id = ?")) {
                for (int id : ids) { up.setInt(1, id); up.addBatch(); }
                up.executeBatch();
            }
        }
        return picked;
    }

    /** Tra serial trong kho: {tên linh kiện, trạng thái, mã SP}; null nếu không có. */
    public String[] findInfo(String serialNumber) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT p.part_name, s.status, p.product_code FROM part_serials s JOIN parts p ON p.part_id = s.part_id WHERE s.serial_number = ?")) {
            ps.setString(1, serialNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new String[]{rs.getString(1), rs.getString(2), rs.getString(3)} : null;
            }
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    /**
     * Đổi trạng thái 1 serial CHỈ KHI đúng linh kiện và đang ở trạng thái {@code from}.
     * Trả về false nếu không khớp (serial lạ, sai linh kiện, hoặc đã bị đơn khác bốc).
     */
    public boolean moveStatus(Connection con, String serialNumber, int partId, String from, String to) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE part_serials SET status = ? WHERE serial_number = ? AND part_id = ? AND status = ?")) {
            ps.setString(1, to);
            ps.setString(2, serialNumber);
            ps.setInt(3, partId);
            ps.setString(4, from);
            return ps.executeUpdate() == 1;
        }
    }

    /** Tách danh sách serial do người dùng nhập: chấp nhận dấu phẩy, chấm phẩy hoặc xuống dòng. */
    public static List<String> parseInput(String raw) {
        List<String> r = new ArrayList<>();
        if (raw == null) return r;
        for (String s : raw.split("[,;\\r\\n]+")) { if (!s.trim().isEmpty()) r.add(s.trim()); }
        return r;
    }

    /** Nhả danh sách serial "SN1, SN2" từ SOLD về IN_STOCK (hủy đơn). */
    public void releaseSerials(Connection con, String serialCsv) throws SQLException {
        for (String sn : splitSerials(serialCsv)) updateStatus(con, sn, "IN_STOCK");
    }

    public static List<String> splitSerials(String csv) {
        List<String> r = new ArrayList<>();
        if (csv == null) return r;
        for (String s : csv.split(",")) { if (!s.trim().isEmpty()) r.add(s.trim()); }
        return r;
    }
}