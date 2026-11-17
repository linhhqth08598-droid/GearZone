package dungntth08581.service;

import fourSome.url.DBConnect;
import linhhqth08598.service.NotificationService;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Module D – Dịch vụ lắp ráp PC (Đợt D1: UC 251–255).
 * 251 đăng ký dịch vụ · 252 tại cửa hàng · 253 giao PC đã lắp · 254 đặt lịch · 255 xác nhận lịch.
 * Trạng thái (CHECK trong DB): SCHEDULED → CONFIRMED → IN_PROGRESS → TESTING → COMPLETED → HANDED_OVER, hoặc CANCELLED.
 */
public class AssemblyService {

    public static final String AT_STORE = "AT_STORE", DELIVER = "DELIVER_ASSEMBLED",
            CLEANING = "PC_CLEANING", PASTE = "THERMAL_PASTE_REPLACEMENT";

    /** Phí dịch vụ (giả định, đổi ở đây nếu cửa hàng có bảng giá khác). */
    public static double feeOf(String type) {
        if (AT_STORE.equals(type)) return 150000;
        if (DELIVER.equals(type)) return 250000;
        if (CLEANING.equals(type)) return 100000;
        if (PASTE.equals(type)) return 120000;
        return -1;
    }

    public static String typeLabel(String type) {
        if (AT_STORE.equals(type)) return "Lắp tại cửa hàng";
        if (DELIVER.equals(type)) return "Lắp xong, giao PC tận nơi";
        if (CLEANING.equals(type)) return "Vệ sinh PC";
        if (PASTE.equals(type)) return "Thay keo tản nhiệt";
        return type;
    }

    private final NotificationService notify = new NotificationService();
    private static final DateTimeFormatter FORM = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter SHOW = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ---------------- Dữ liệu cho form đăng ký ----------------
    public static class Option {
        public int id; public String label;
        public int getId() { return id; } public String getLabel() { return label; }
    }

    /** Đơn hàng của khách (không tính đơn đã hủy) để chọn "PC cần lắp". */
    public List<Option> ordersOf(int userId) {
        List<Option> l = new ArrayList<>();
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT order_id, order_code FROM orders WHERE user_id = ? AND order_status <> 'CANCELLED' ORDER BY order_id DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { Option o = new Option(); o.id = rs.getInt(1); o.label = "Đơn " + rs.getString(2); l.add(o); }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return l;
    }

    /** Cấu hình PC đã lưu của khách. */
    public List<Option> buildsOf(int userId) {
        List<Option> l = new ArrayList<>();
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT build_id, build_name FROM pc_builds WHERE user_id = ? AND status = 'SAVED' ORDER BY build_id DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { Option o = new Option(); o.id = rs.getInt(1); o.label = rs.getString(2); l.add(o); }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return l;
    }

    // ---------------- UC 251–254: đăng ký + đặt lịch ----------------
    /** Trả null nếu thành công, ngược lại là thông báo lỗi. */
    public String register(int userId, String type, Integer orderId, Integer buildId, String when, String note) {
        if (feeOf(type) < 0) return "Hình thức lắp ráp không hợp lệ!";
        if (orderId == null && buildId == null) return "Vui lòng chọn đơn hàng hoặc cấu hình PC cần lắp!";
        LocalDateTime t;
        try { t = LocalDateTime.parse(when, FORM); } catch (Exception e) { return "Vui lòng chọn ngày giờ hẹn!"; }
        LocalDateTime now = LocalDateTime.now();
        if (t.isBefore(now.plusHours(2))) return "Lịch hẹn phải sau thời điểm hiện tại ít nhất 2 giờ!";
        if (t.isAfter(now.plusDays(60))) return "Chỉ nhận đặt lịch trong vòng 60 ngày!";
        if (t.getHour() < 8 || t.getHour() > 19 || (t.getHour() == 19 && t.getMinute() > 0)) return "Cửa hàng làm việc 08:00–19:00, vui lòng chọn giờ trong khung này!";
        if (note != null && note.length() > 500) return "Ghi chú tối đa 500 ký tự!";
        try (Connection c = DBConnect.getConnection()) {
            if (orderId != null && !owns(c, "SELECT COUNT(*) FROM orders WHERE order_id = ? AND user_id = ? AND order_status <> 'CANCELLED'", orderId, userId)) return "Đơn hàng không hợp lệ!";
            if (buildId != null && !owns(c, "SELECT COUNT(*) FROM pc_builds WHERE build_id = ? AND user_id = ? AND status = 'SAVED'", buildId, userId)) return "Cấu hình PC không hợp lệ!";
            // Không đăng ký trùng: cùng đơn/cấu hình đang có lịch chưa kết thúc
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM assembly_orders WHERE user_id = ? AND assembly_type = ? AND status NOT IN ('CANCELLED','HANDED_OVER','COMPLETED') "
                  + "AND ((? IS NOT NULL AND order_id = ?) OR (? IS NOT NULL AND build_id = ?))")) {
                ps.setInt(1, userId); ps.setString(2, type);
                setInt(ps, 3, orderId); setInt(ps, 4, orderId); setInt(ps, 5, buildId); setInt(ps, 6, buildId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next() && rs.getInt(1) > 0) return "Bạn đã đăng ký dịch vụ này rồi, đang chờ xử lý!"; }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO assembly_orders (user_id, order_id, build_id, assembly_type, assembly_fee, status, scheduled_date, note, created_at) "
                  + "VALUES (?, ?, ?, ?, ?, 'SCHEDULED', ?, ?, SYSDATETIME())", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId); setInt(ps, 2, orderId); setInt(ps, 3, buildId);
                ps.setString(4, type); ps.setDouble(5, feeOf(type));
                ps.setTimestamp(6, Timestamp.valueOf(t));
                ps.setString(7, note == null || note.trim().isEmpty() ? null : note.trim());
                ps.executeUpdate();
                int id = 0; try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) id = k.getInt(1); }
                notify.send(userId, "SYSTEM", "Đăng ký lắp ráp #" + id,
                        "Đã nhận đăng ký \"" + typeLabel(type) + "\" hẹn " + t.format(SHOW) + ". Cửa hàng sẽ xác nhận lịch sớm.", "ASSEMBLY", id);
            }
            return null;
        } catch (Exception e) { e.printStackTrace(); return "Không đăng ký được, vui lòng thử lại!"; }
    }

    private boolean owns(Connection c, String sql, int id, int userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }
    private void setInt(PreparedStatement ps, int i, Integer v) throws SQLException { if (v == null) ps.setNull(i, Types.INTEGER); else ps.setInt(i, v); }

    // ---------------- Khách hủy lịch ----------------
    public String cancelByUser(int userId, int assemblyId) {
        return changeStatus(assemblyId, userId, "CANCELLED", null, null, "Bạn đã hủy lịch lắp ráp.", true);
    }

    // ---------------- UC 255: admin xác nhận / đổi lịch / từ chối ----------------
    public static class Row {
        public int assemblyId, userId; public String customerName, phone, type, status, note;
        public Integer orderId, buildId, technicianId; public String technicianName; public double fee; public Timestamp scheduledDate, createdAt;
        public int getAssemblyId() { return assemblyId; } public int getUserId() { return userId; }
        public String getCustomerName() { return customerName; } public String getPhone() { return phone; }
        public String getType() { return type; } public String getTypeLabel() { return typeLabel(type); }
        public String getStatus() { return status; } public String getNote() { return note; }
        public Integer getOrderId() { return orderId; } public Integer getBuildId() { return buildId; }
        public double getFee() { return fee; } public Timestamp getScheduledDate() { return scheduledDate; }
        public Timestamp getCreatedAt() { return createdAt; }
        public Integer getTechnicianId() { return technicianId; } public String getTechnicianName() { return technicianName; }
    }

    public List<Row> listForAdmin(String status) { return query(status, null); }

    public Row getRow(int id) { List<Row> l = query(null, id); return l.isEmpty() ? null : l.get(0); }

    private List<Row> query(String status, Integer id) {
        List<Row> l = new ArrayList<>();
        String sql = "SELECT a.assembly_id, a.user_id, u.full_name, u.phone_number, a.assembly_type, a.status, a.note, a.order_id, a.build_id, "
                   + "a.assembly_fee, a.scheduled_date, a.created_at, a.technician_id, t.full_name FROM assembly_orders a JOIN users u ON u.user_id = a.user_id LEFT JOIN users t ON t.user_id = a.technician_id "
                   + (status != null && !status.isEmpty() ? "WHERE a.status = ? " : "") + (id != null ? (status != null && !status.isEmpty() ? "AND" : "WHERE") + " a.assembly_id = ? " : "") + "ORDER BY a.assembly_id DESC";
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            int k = 1; if (status != null && !status.isEmpty()) ps.setString(k++, status); if (id != null) ps.setInt(k, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Row r = new Row();
                    r.assemblyId = rs.getInt(1); r.userId = rs.getInt(2); r.customerName = rs.getString(3); r.phone = rs.getString(4);
                    r.type = rs.getString(5); r.status = rs.getString(6); r.note = rs.getString(7);
                    r.orderId = rs.getObject(8, Integer.class); r.buildId = rs.getObject(9, Integer.class);
                    r.fee = rs.getDouble(10); r.scheduledDate = rs.getTimestamp(11); r.createdAt = rs.getTimestamp(12);
                    r.technicianId = rs.getObject(13, Integer.class); r.technicianName = rs.getString(14);
                    l.add(r);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return l;
    }

    /** action: confirm (SCHEDULED→CONFIRMED, có thể kèm giờ mới) | reject (→CANCELLED, kèm lý do). */
    public String adminAction(int assemblyId, String action, String newWhen, String reason) {
        if ("confirm".equals(action)) {
            Timestamp ts = null;
            if (newWhen != null && !newWhen.trim().isEmpty()) {
                try { ts = Timestamp.valueOf(LocalDateTime.parse(newWhen, FORM)); } catch (Exception e) { return "Giờ hẹn không hợp lệ!"; }
            }
            return changeStatus(assemblyId, null, "CONFIRMED", ts, null, "Cửa hàng đã xác nhận lịch lắp ráp của bạn.", false);
        }
        if ("reject".equals(action)) {
            if (reason == null || reason.trim().length() < 5) return "Vui lòng nhập lý do từ chối (ít nhất 5 ký tự)!";
            return changeStatus(assemblyId, null, "CANCELLED", null, reason.trim(), "Cửa hàng không nhận được lịch này. Lý do: " + reason.trim(), false);
        }
        return "Thao tác không hợp lệ!";
    }

    /** Đổi trạng thái có kiểm tra: chỉ SCHEDULED/CONFIRMED mới đổi được ở đợt D1. ownerId != null = khách tự thao tác. */
    private String changeStatus(int id, Integer ownerId, String next, Timestamp newDate, String reason, String msg, boolean byUser) {
        try (Connection c = DBConnect.getConnection()) {
            String cur; int uid;
            try (PreparedStatement ps = c.prepareStatement("SELECT status, user_id FROM assembly_orders WHERE assembly_id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) { if (!rs.next()) return "Không tìm thấy đơn lắp ráp!"; cur = rs.getString(1); uid = rs.getInt(2); }
            }
            if (ownerId != null && uid != ownerId) return "Không tìm thấy đơn lắp ráp!";
            boolean ok = byUser ? ("SCHEDULED".equals(cur) || "CONFIRMED".equals(cur))
                    : ("CONFIRMED".equals(next) ? "SCHEDULED".equals(cur) : ("SCHEDULED".equals(cur) || "CONFIRMED".equals(cur)));
            if (!ok) return "Đơn đang ở trạng thái " + cur + ", không thể thực hiện thao tác này!";
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE assembly_orders SET status = ?, scheduled_date = COALESCE(?, scheduled_date), note = CASE WHEN ? IS NULL THEN note ELSE ? END WHERE assembly_id = ? AND status = ?")) {
                ps.setString(1, next); ps.setTimestamp(2, newDate); ps.setString(3, reason); ps.setString(4, reason == null ? null : "Từ chối: " + reason);
                ps.setInt(5, id); ps.setString(6, cur);
                if (ps.executeUpdate() == 0) return "Đơn vừa được cập nhật bởi người khác, vui lòng tải lại!";
            }
            notify.send(uid, "SYSTEM", "Lắp ráp #" + id, msg, "ASSEMBLY", id);
            return null;
        } catch (Exception e) { e.printStackTrace(); return "Lỗi hệ thống, vui lòng thử lại!"; }
    }

    // =====================================================================
    // ĐỢT D2: UC 256 phân công kỹ thuật viên · 257 ghi linh kiện sử dụng · 258 cập nhật tiến độ
    // =====================================================================

    /** Người có thể làm kỹ thuật viên: tài khoản vai trò STAFF (và ADMIN/OWNER để dễ test). */
    public List<Option> technicians() {
        List<Option> l = new ArrayList<>();
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT DISTINCT u.user_id, u.full_name FROM users u JOIN user_roles ur ON ur.user_id = u.user_id AND ur.status = 'ACTIVE' "
              + "JOIN roles r ON r.role_id = ur.role_id AND r.status = 'ACTIVE' WHERE u.status = 'ACTIVE' AND r.role_code IN ('STAFF','ADMIN','OWNER') ORDER BY u.full_name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { Option o = new Option(); o.id = rs.getInt(1); o.label = rs.getString(2); l.add(o); }
        } catch (Exception e) { e.printStackTrace(); }
        return l;
    }

    /** UC 256. Chỉ phân công khi đơn đã được xác nhận lịch và chưa bàn giao. */
    public String assign(int assemblyId, int technicianId) {
        try (Connection c = DBConnect.getConnection()) {
            boolean valid = false;
            for (Option o : technicians()) if (o.id == technicianId) valid = true;
            if (!valid) return "Kỹ thuật viên không hợp lệ!";
            int uid;
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE assembly_orders SET technician_id = ? WHERE assembly_id = ? AND status IN ('CONFIRMED','IN_PROGRESS','TESTING')")) {
                ps.setInt(1, technicianId); ps.setInt(2, assemblyId);
                if (ps.executeUpdate() == 0) return "Chỉ phân công được khi đơn đã xác nhận lịch và chưa hoàn tất!";
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT user_id FROM assembly_orders WHERE assembly_id = ?")) {
                ps.setInt(1, assemblyId);
                try (ResultSet rs = ps.executeQuery()) { uid = rs.next() ? rs.getInt(1) : 0; }
            }
            notify.send(technicianId, "SYSTEM", "Được phân công lắp ráp #" + assemblyId, "Bạn được phân công đơn lắp ráp #" + assemblyId + ".", "ASSEMBLY", assemblyId);
            if (uid > 0) notify.send(uid, "SYSTEM", "Lắp ráp #" + assemblyId, "Đơn lắp ráp của bạn đã được phân công cho kỹ thuật viên.", "ASSEMBLY", assemblyId);
            return null;
        } catch (Exception e) { e.printStackTrace(); return "Lỗi hệ thống, vui lòng thử lại!"; }
    }

    public static class PartLine {
        public int usedId, partId, quantity; public String partName, serial;
        public int getUsedId() { return usedId; } public int getPartId() { return partId; } public int getQuantity() { return quantity; }
        public String getPartName() { return partName; } public String getSerial() { return serial; }
    }

    /** Linh kiện cần lắp (từ đơn hàng và/hoặc cấu hình PC của đơn lắp ráp), gộp theo linh kiện. */
    public List<PartLine> sourceParts(int assemblyId) {
        List<PartLine> l = new ArrayList<>();
        String sql = "SELECT p.part_id, p.part_name, SUM(x.q) FROM ("
                   + " SELECT oi.part_id, oi.quantity q FROM order_items oi JOIN assembly_orders a ON a.order_id = oi.order_id WHERE a.assembly_id = ?"
                   + " UNION ALL SELECT d.part_id, d.quantity q FROM pc_build_details d JOIN assembly_orders a ON a.build_id = d.build_id WHERE a.assembly_id = ?"
                   + ") x JOIN parts p ON p.part_id = x.part_id GROUP BY p.part_id, p.part_name ORDER BY p.part_name";
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, assemblyId); ps.setInt(2, assemblyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { PartLine p = new PartLine(); p.partId = rs.getInt(1); p.partName = rs.getString(2); p.quantity = rs.getInt(3); l.add(p); }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return l;
    }

    public List<PartLine> usedParts(int assemblyId) {
        List<PartLine> l = new ArrayList<>();
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT u.used_id, u.part_id, p.part_name, u.quantity, u.serial_number FROM assembly_parts_used u JOIN parts p ON p.part_id = u.part_id WHERE u.assembly_id = ? ORDER BY u.used_id")) {
            ps.setInt(1, assemblyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { PartLine p = new PartLine(); p.usedId = rs.getInt(1); p.partId = rs.getInt(2); p.partName = rs.getString(3); p.quantity = rs.getInt(4); p.serial = rs.getString(5); l.add(p); }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return l;
    }

    private String currentStatus(Connection c, int assemblyId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT status FROM assembly_orders WHERE assembly_id = ?")) {
            ps.setInt(1, assemblyId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getString(1) : null; }
        }
    }

    /** UC 257. Chỉ ghi khi đang lắp/kiểm tra; linh kiện phải nằm trong danh sách cần lắp, tổng số lượng không vượt quá số cần. */
    public String addUsedPart(int assemblyId, int partId, int qty, String serial) {
        if (qty <= 0) return "Số lượng phải lớn hơn 0!";
        if (serial != null && serial.length() > 100) return "Số serial tối đa 100 ký tự!";
        try (Connection c = DBConnect.getConnection()) {
            String st = currentStatus(c, assemblyId);
            if (st == null) return "Không tìm thấy đơn lắp ráp!";
            if (!"IN_PROGRESS".equals(st) && !"TESTING".equals(st)) return "Chỉ ghi linh kiện khi đơn đang lắp hoặc đang kiểm tra!";
            int need = -1;
            for (PartLine p : sourceParts(assemblyId)) if (p.partId == partId) need = p.quantity;
            if (need < 0) return "Linh kiện này không thuộc đơn lắp ráp!";
            int used = 0;
            for (PartLine p : usedParts(assemblyId)) if (p.partId == partId) used += p.quantity;
            if (used + qty > need) return "Vượt số lượng cần lắp (cần " + need + ", đã ghi " + used + ")!";
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO assembly_parts_used (assembly_id, part_id, quantity, serial_number) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, assemblyId); ps.setInt(2, partId); ps.setInt(3, qty);
                ps.setString(4, serial == null || serial.trim().isEmpty() ? null : serial.trim());
                ps.executeUpdate();
            }
            return null;
        } catch (Exception e) { e.printStackTrace(); return "Không ghi được, vui lòng thử lại!"; }
    }

    public String removeUsedPart(int assemblyId, int usedId) {
        try (Connection c = DBConnect.getConnection()) {
            String st = currentStatus(c, assemblyId);
            if (!"IN_PROGRESS".equals(st) && !"TESTING".equals(st)) return "Chỉ sửa được khi đơn đang lắp hoặc đang kiểm tra!";
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM assembly_parts_used WHERE used_id = ? AND assembly_id = ?")) {
                ps.setInt(1, usedId); ps.setInt(2, assemblyId);
                return ps.executeUpdate() == 0 ? "Không tìm thấy dòng linh kiện!" : null;
            }
        } catch (Exception e) { e.printStackTrace(); return "Lỗi hệ thống, vui lòng thử lại!"; }
    }

    /** UC 258: CONFIRMED→IN_PROGRESS (cần đã có kỹ thuật viên) · IN_PROGRESS→TESTING (cần đã ghi ít nhất 1 linh kiện). */
    public String advance(int assemblyId, String next) {
        try (Connection c = DBConnect.getConnection()) {
            String cur = null; Integer tech = null; int uid = 0;
            try (PreparedStatement ps = c.prepareStatement("SELECT status, technician_id, user_id FROM assembly_orders WHERE assembly_id = ?")) {
                ps.setInt(1, assemblyId);
                try (ResultSet rs = ps.executeQuery()) { if (!rs.next()) return "Không tìm thấy đơn lắp ráp!"; cur = rs.getString(1); tech = rs.getObject(2, Integer.class); uid = rs.getInt(3); }
            }
            String msg;
            if ("IN_PROGRESS".equals(next) && "CONFIRMED".equals(cur)) {
                if (tech == null) return "Hãy phân công kỹ thuật viên trước khi bắt đầu lắp!";
                msg = "Kỹ thuật viên đã bắt đầu lắp PC của bạn.";
            } else if ("TESTING".equals(next) && "IN_PROGRESS".equals(cur)) {
                if (usedParts(assemblyId).isEmpty()) return "Hãy ghi linh kiện đã dùng trước khi chuyển sang kiểm tra!";
                msg = "PC của bạn đã lắp xong và đang được kiểm tra.";
            } else return "Không thể chuyển từ " + cur + " sang " + next + "!";
            try (PreparedStatement ps = c.prepareStatement("UPDATE assembly_orders SET status = ? WHERE assembly_id = ? AND status = ?")) {
                ps.setString(1, next); ps.setInt(2, assemblyId); ps.setString(3, cur);
                if (ps.executeUpdate() == 0) return "Đơn vừa được cập nhật bởi người khác, vui lòng tải lại!";
            }
            notify.send(uid, "SYSTEM", "Lắp ráp #" + assemblyId, msg, "ASSEMBLY", assemblyId);
            return null;
        } catch (Exception e) { e.printStackTrace(); return "Lỗi hệ thống, vui lòng thử lại!"; }
    }
}