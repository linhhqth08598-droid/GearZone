package dungntth08581.service;

import fourSome.url.DBConnect;
import fourSome.util.Constants;
import javax.imageio.*;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import jakarta.servlet.http.Part;
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
        if (!isServiceType(type) && orderId == null && buildId == null) return "Vui lòng chọn đơn hàng hoặc cấu hình PC cần lắp!";
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
            if (isServiceType(type)) {
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT COUNT(*) FROM assembly_orders WHERE user_id = ? AND assembly_type = ? AND status NOT IN ('CANCELLED','HANDED_OVER','COMPLETED')")) {
                    ps.setInt(1, userId); ps.setString(2, type);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next() && rs.getInt(1) > 0) return "Bạn đã đăng ký dịch vụ này rồi, đang chờ xử lý!"; }
                }
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
                if (isServiceOnly(c, assemblyId)) return "Dịch vụ vệ sinh/thay keo không cần kiểm tra – bấm \"Hoàn tất\" luôn!";
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

    // =====================================================================
    // ĐỢT D3: UC 259–266 báo cáo kiểm tra · 267 ảnh PC · 268 hoàn tất & bàn giao · 269–270 vệ sinh / thay keo
    // =====================================================================

    public static final int MAX_PHOTOS = 10;

    public static boolean isServiceType(String type) { return CLEANING.equals(type) || PASTE.equals(type); }

    private boolean isServiceOnly(Connection c, int assemblyId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT assembly_type FROM assembly_orders WHERE assembly_id = ?")) {
            ps.setInt(1, assemblyId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && isServiceType(rs.getString(1)); }
        }
    }

    /** Báo cáo kiểm tra (đọc từ assembly_test_reports). Tên getter khớp user/assembly_detail.jsp. */
    public static class Report {
        public boolean bootPassed, stabilityPassed;
        public Integer cpuIdle, cpuLoad, gpuIdle, gpuLoad, cinebench, dmark, stabilityMinutes;
        public String note, finalResult; public Timestamp completedAt;
        public boolean isBootPassed() { return bootPassed; } public boolean isStabilityPassed() { return stabilityPassed; }
        public Integer getCpuIdle() { return cpuIdle; } public Integer getCpuLoad() { return cpuLoad; }
        public Integer getGpuIdle() { return gpuIdle; } public Integer getGpuLoad() { return gpuLoad; }
        public Integer getCinebench() { return cinebench; } public Integer getDmark() { return dmark; }
        public Integer getStabilityMinutes() { return stabilityMinutes; }
        public String getNote() { return note; } public String getFinalResult() { return finalResult; }
        public Timestamp getCompletedAt() { return completedAt; }
    }

    public static class Photo {
        public int photoId; public String url;
        public int getPhotoId() { return photoId; } public String getUrl() { return url; }
    }

    public Report getReport(int assemblyId) {
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT TOP 1 boot_test_passed, cpu_idle_temp_c, cpu_load_temp_c, gpu_idle_temp_c, gpu_load_temp_c, cinebench_score, [3dmark_score], "
              + "stability_test_passed, stability_test_minutes, note, final_result, completed_at FROM assembly_test_reports WHERE assembly_id = ? AND status = 'ACTIVE' ORDER BY report_id DESC")) {
            ps.setInt(1, assemblyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Report r = new Report();
                r.bootPassed = rs.getBoolean(1);
                r.cpuIdle = rs.getObject(2, Integer.class); r.cpuLoad = rs.getObject(3, Integer.class);
                r.gpuIdle = rs.getObject(4, Integer.class); r.gpuLoad = rs.getObject(5, Integer.class);
                r.cinebench = rs.getObject(6, Integer.class); r.dmark = rs.getObject(7, Integer.class);
                r.stabilityPassed = rs.getBoolean(8); r.stabilityMinutes = rs.getObject(9, Integer.class);
                r.note = rs.getString(10); r.finalResult = rs.getString(11); r.completedAt = rs.getTimestamp(12);
                return r;
            }
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    /** Danh sách ảnh: chỉ lấy id (không kéo base64 về); url = "assembly/{id}" → trang hiển thị qua /media/assembly/{id}. */
    public List<Photo> photos(int assemblyId) {
        List<Photo> l = new ArrayList<>();
        try (Connection c = DBConnect.getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT photo_id FROM assembly_photos WHERE assembly_id = ? AND status = 'ACTIVE' ORDER BY photo_id")) {
            ps.setInt(1, assemblyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { Photo p = new Photo(); p.photoId = rs.getInt(1); p.url = "assembly/" + p.photoId; l.add(p); }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return l;
    }

    /** Khách xem chi tiết đơn của chính mình (null nếu không phải chủ đơn). */
    public Row getRowOfUser(int userId, int assemblyId) {
        Row r = getRow(assemblyId);
        return r != null && r.userId == userId ? r : null;
    }

    private static Integer parseTemp(String s, String label, StringBuilder err) {
        if (s == null || s.trim().isEmpty()) { err.append("Vui lòng nhập ").append(label).append("! "); return null; }
        try {
            int v = Integer.parseInt(s.trim());
            if (v < 0 || v > 150) { err.append(label).append(" phải từ 0 đến 150°C! "); return null; }
            return v;
        } catch (NumberFormatException e) { err.append(label).append(" không hợp lệ! "); return null; }
    }

    private static Integer parseScore(String s, String label, StringBuilder err) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            int v = Integer.parseInt(s.trim());
            if (v < 0 || v > 1000000) { err.append(label).append(" không hợp lệ! "); return null; }
            return v;
        } catch (NumberFormatException e) { err.append(label).append(" không hợp lệ! "); return null; }
    }

    /**
     * UC 259–266: ghi kết quả lắp ráp + 7 hạng mục kiểm tra. Chỉ khi đơn đang TESTING (và không phải dịch vụ vệ sinh/thay keo).
     * Lưu lại nhiều lần thì ghi đè báo cáo cũ. Kết luận Đạt bị chặn nếu khởi động hoặc ổn định chưa đạt.
     */
    public String saveReport(int assemblyId, boolean boot, String cpuIdle, String cpuLoad, String gpuIdle, String gpuLoad,
                             boolean stable, String stableMin, String cinebench, String dmark, String note, String finalResult) {
        if (!"PASSED".equals(finalResult) && !"FAILED".equals(finalResult)) return "Vui lòng chọn kết luận Đạt / Không đạt!";
        if (note != null && note.trim().length() > 500) return "Ghi chú tối đa 500 ký tự!";
        StringBuilder err = new StringBuilder();
        Integer ci = parseTemp(cpuIdle, "Nhiệt độ CPU lúc nghỉ", err), cl = parseTemp(cpuLoad, "Nhiệt độ CPU khi tải", err);
        Integer gi = parseTemp(gpuIdle, "Nhiệt độ GPU lúc nghỉ", err), gl = parseTemp(gpuLoad, "Nhiệt độ GPU khi tải", err);
        Integer cb = parseScore(cinebench, "Điểm Cinebench", err), dm = parseScore(dmark, "Điểm 3DMark", err);
        Integer sm = null;
        try {
            sm = Integer.parseInt(stableMin == null ? "" : stableMin.trim());
            if (sm < 0 || sm > 1440) err.append("Số phút test ổn định phải từ 0 đến 1440! ");
        } catch (NumberFormatException e) { err.append("Vui lòng nhập số phút test ổn định! "); }
        if (err.length() > 0) return err.toString().trim();
        if ("PASSED".equals(finalResult) && (!boot || !stable)) return "Không thể kết luận Đạt khi khởi động hoặc test ổn định chưa đạt!";
        if (ci != null && cl != null && cl < ci) return "Nhiệt độ CPU khi tải không thể thấp hơn lúc nghỉ!";
        if (gi != null && gl != null && gl < gi) return "Nhiệt độ GPU khi tải không thể thấp hơn lúc nghỉ!";

        try (Connection c = DBConnect.getConnection()) {
            String st = currentStatus(c, assemblyId);
            if (st == null) return "Không tìm thấy đơn lắp ráp!";
            if (isServiceOnly(c, assemblyId)) return "Dịch vụ vệ sinh/thay keo không cần báo cáo kiểm tra!";
            if (!"TESTING".equals(st)) return "Chỉ ghi báo cáo khi đơn đang ở bước kiểm tra!";
            String n = note == null || note.trim().isEmpty() ? null : note.trim();
            int exist = 0;
            try (PreparedStatement ps = c.prepareStatement("SELECT TOP 1 report_id FROM assembly_test_reports WHERE assembly_id = ? ORDER BY report_id DESC")) {
                ps.setInt(1, assemblyId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) exist = rs.getInt(1); }
            }
            String sql = exist > 0
                ? "UPDATE assembly_test_reports SET boot_test_passed = ?, cpu_idle_temp_c = ?, cpu_load_temp_c = ?, gpu_idle_temp_c = ?, gpu_load_temp_c = ?, "
                + "cinebench_score = ?, [3dmark_score] = ?, stability_test_passed = ?, stability_test_minutes = ?, note = ?, final_result = ?, completed_at = SYSDATETIME(), status = 'ACTIVE' WHERE report_id = ?"
                : "INSERT INTO assembly_test_reports (boot_test_passed, cpu_idle_temp_c, cpu_load_temp_c, gpu_idle_temp_c, gpu_load_temp_c, cinebench_score, [3dmark_score], "
                + "stability_test_passed, stability_test_minutes, note, final_result, completed_at, status, assembly_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME(), 'ACTIVE', ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setBoolean(1, boot); setInt(ps, 2, ci); setInt(ps, 3, cl); setInt(ps, 4, gi); setInt(ps, 5, gl);
                setInt(ps, 6, cb); setInt(ps, 7, dm); ps.setBoolean(8, stable); setInt(ps, 9, sm);
                ps.setString(10, n); ps.setString(11, finalResult);
                ps.setInt(12, exist > 0 ? exist : assemblyId);
                ps.executeUpdate();
            }
            return null;
        } catch (Exception e) { e.printStackTrace(); return "Không lưu được báo cáo, vui lòng thử lại!"; }
    }

    /** Nén ảnh: thu nhỏ cạnh dài ≤ 1280px, xuất JPEG chất lượng 0.8. Trả null nếu ImageIO không đọc được định dạng (vd webp). */
    private static byte[] compress(byte[] raw) throws java.io.IOException {
        BufferedImage src = ImageIO.read(new ByteArrayInputStream(raw));
        if (src == null) return null;
        int w = src.getWidth(), h = src.getHeight(), max = 1280;
        double k = Math.min(1.0, (double) max / Math.max(w, h));
        int nw = Math.max(1, (int) Math.round(w * k)), nh = Math.max(1, (int) Math.round(h * k));
        BufferedImage out = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE); g.fillRect(0, 0, nw, nh);
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();
        ImageWriter wr = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam prm = wr.getDefaultWriteParam();
        prm.setCompressionMode(ImageWriteParam.MODE_EXPLICIT); prm.setCompressionQuality(0.8f);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (MemoryCacheImageOutputStream ios = new MemoryCacheImageOutputStream(bos)) {
            wr.setOutput(ios); wr.write(null, new IIOImage(out, null, null), prm);
        } finally { wr.dispose(); }
        return bos.toByteArray();
    }

    /**
     * UC 267: tải 1 ảnh PC hoàn thiện, nén rồi mã hóa base64 lưu thẳng vào assembly_photos.photo_url
     * (dạng data URI, cột NVARCHAR(MAX) – xem file sql/assembly_photos_base64.sql). ≤ 10 ảnh/đơn, ≤ 10 MB/ảnh gốc.
     */
    public String addPhoto(int assemblyId, Part part) {
        if (part == null || part.getSize() == 0) return "Vui lòng chọn ảnh!";
        if (part.getSize() > Constants.MAX_IMAGE_SIZE) return "Ảnh vượt quá " + (Constants.MAX_IMAGE_SIZE / 1024 / 1024) + "MB!";
        String name = part.getSubmittedFileName() == null ? "" : part.getSubmittedFileName().toLowerCase();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "";
        if (!Arrays.asList(Constants.IMAGE_EXTENSIONS).contains(ext)) return "Định dạng ảnh không được hỗ trợ: ." + ext;
        try (Connection c = DBConnect.getConnection()) {
            String st = currentStatus(c, assemblyId);
            if (st == null) return "Không tìm thấy đơn lắp ráp!";
            if (isServiceOnly(c, assemblyId)) return "Dịch vụ vệ sinh/thay keo không cần ảnh PC hoàn thiện!";
            if (!"TESTING".equals(st) && !"COMPLETED".equals(st)) return "Chỉ thêm ảnh khi đơn đang kiểm tra hoặc đã hoàn tất!";
            if (photos(assemblyId).size() >= MAX_PHOTOS) return "Mỗi đơn tối đa " + MAX_PHOTOS + " ảnh!";
            byte[] raw;
            try (java.io.InputStream in = part.getInputStream()) { raw = in.readAllBytes(); }
            byte[] jpg = compress(raw);
            String mime = "image/jpeg";
            if (jpg == null) {   // định dạng ImageIO không đọc được: giữ nguyên nếu nhỏ
                if (raw.length > 1536 * 1024) return "Không xử lý được định dạng ảnh này, hãy dùng jpg/png (hoặc ảnh ≤ 1,5 MB)!";
                jpg = raw; mime = "webp".equals(ext) ? "image/webp" : ("gif".equals(ext) ? "image/gif" : "image/" + ext);
            }
            ensurePhotoColumn(c);   // tự mở rộng cột photo_url sang NVARCHAR(MAX) nếu chưa chạy file sql
            String data = "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(jpg);
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO assembly_photos (assembly_id, photo_url, status) VALUES (?, ?, 'ACTIVE')")) {
                ps.setInt(1, assemblyId); ps.setString(2, data); ps.executeUpdate();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            String m = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage().replaceAll("\\s+", " ");
            return "Không tải được ảnh: " + (m.length() > 160 ? m.substring(0, 160) + "…" : m);
        }
    }

    private static volatile boolean photoColReady = false;

    /** assembly_photos.photo_url gốc là NVARCHAR(500) – quá nhỏ cho base64. Kiểm tra 1 lần, nếu chưa phải MAX thì ALTER. */
    private static void ensurePhotoColumn(Connection c) throws SQLException {
        if (photoColReady) return;
        int len = 0;
        try (PreparedStatement ps = c.prepareStatement("SELECT CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'assembly_photos' AND COLUMN_NAME = 'photo_url'");
             ResultSet rs = ps.executeQuery()) { if (rs.next()) len = rs.getInt(1); }
        if (len != -1) {
            try (Statement st = c.createStatement()) { st.executeUpdate("ALTER TABLE dbo.assembly_photos ALTER COLUMN photo_url NVARCHAR(MAX) NOT NULL"); }
        }
        photoColReady = true;
    }

    public String removePhoto(int assemblyId, int photoId) {
        try (Connection c = DBConnect.getConnection()) {
            String st = currentStatus(c, assemblyId);
            if (!"TESTING".equals(st) && !"COMPLETED".equals(st)) return "Chỉ xóa ảnh khi đơn đang kiểm tra hoặc đã hoàn tất!";
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM assembly_photos WHERE photo_id = ? AND assembly_id = ?")) {
                ps.setInt(1, photoId); ps.setInt(2, assemblyId);
                return ps.executeUpdate() == 0 ? "Không tìm thấy ảnh!" : null;
            }
        } catch (Exception e) { e.printStackTrace(); return "Lỗi hệ thống, vui lòng thử lại!"; }
    }

    /**
     * Hoàn tất: TESTING → COMPLETED (cần báo cáo kết luận Đạt) · với vệ sinh/thay keo: IN_PROGRESS → COMPLETED (đi tắt, UC 269–270).
     */
    public String complete(int assemblyId) {
        try (Connection c = DBConnect.getConnection()) {
            String cur = null; Integer tech = null; int uid = 0; String type = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT status, technician_id, user_id, assembly_type FROM assembly_orders WHERE assembly_id = ?")) {
                ps.setInt(1, assemblyId);
                try (ResultSet rs = ps.executeQuery()) { if (!rs.next()) return "Không tìm thấy đơn lắp ráp!"; cur = rs.getString(1); tech = rs.getObject(2, Integer.class); uid = rs.getInt(3); type = rs.getString(4); }
            }
            if (isServiceType(type)) {
                if (!"IN_PROGRESS".equals(cur)) return "Đơn phải đang thực hiện mới hoàn tất được (hiện: " + cur + ")!";
            } else {
                if (!"TESTING".equals(cur)) return "Chỉ hoàn tất khi đơn đang ở bước kiểm tra!";
                Report r = getReport(assemblyId);
                if (r == null) return "Hãy ghi báo cáo kiểm tra trước khi hoàn tất!";
                if (!"PASSED".equals(r.finalResult)) return "Báo cáo kiểm tra chưa đạt – không thể hoàn tất!";
            }
            try (PreparedStatement ps = c.prepareStatement("UPDATE assembly_orders SET status = 'COMPLETED' WHERE assembly_id = ? AND status = ?")) {
                ps.setInt(1, assemblyId); ps.setString(2, cur);
                if (ps.executeUpdate() == 0) return "Đơn vừa được cập nhật bởi người khác, vui lòng tải lại!";
            }
            notify.send(uid, "SYSTEM", "Lắp ráp #" + assemblyId,
                    isServiceType(type) ? "Dịch vụ \"" + typeLabel(type) + "\" đã hoàn tất, cửa hàng sẽ bàn giao PC cho bạn." : "PC của bạn đã lắp xong và kiểm tra đạt. Cửa hàng sẽ bàn giao cho bạn.", "ASSEMBLY", assemblyId);
            return null;
        } catch (Exception e) { e.printStackTrace(); return "Lỗi hệ thống, vui lòng thử lại!"; }
    }

    /** UC 268: COMPLETED → HANDED_OVER, ghi handed_over_at. Lắp PC cần ≥ 1 ảnh; vệ sinh/thay keo không cần. */
    public String handOver(int assemblyId) {
        try (Connection c = DBConnect.getConnection()) {
            String cur = null; int uid = 0; String type = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT status, user_id, assembly_type FROM assembly_orders WHERE assembly_id = ?")) {
                ps.setInt(1, assemblyId);
                try (ResultSet rs = ps.executeQuery()) { if (!rs.next()) return "Không tìm thấy đơn lắp ráp!"; cur = rs.getString(1); uid = rs.getInt(2); type = rs.getString(3); }
            }
            if (!"COMPLETED".equals(cur)) return "Chỉ bàn giao khi đơn đã hoàn tất!";
            if (!isServiceType(type) && photos(assemblyId).isEmpty()) return "Hãy tải ít nhất 1 ảnh PC hoàn thiện trước khi bàn giao!";
            try (PreparedStatement ps = c.prepareStatement("UPDATE assembly_orders SET status = 'HANDED_OVER', handed_over_at = SYSDATETIME() WHERE assembly_id = ? AND status = 'COMPLETED'")) {
                ps.setInt(1, assemblyId);
                if (ps.executeUpdate() == 0) return "Đơn vừa được cập nhật bởi người khác, vui lòng tải lại!";
            }
            notify.send(uid, "SYSTEM", "Lắp ráp #" + assemblyId, "Cửa hàng đã bàn giao PC cho bạn. Cảm ơn bạn đã sử dụng dịch vụ – hãy để lại đánh giá nhé!", "ASSEMBLY", assemblyId);
            return null;
        } catch (Exception e) { e.printStackTrace(); return "Lỗi hệ thống, vui lòng thử lại!"; }
    }
}
