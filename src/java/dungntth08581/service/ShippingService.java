package dungntth08581.service;

import fourSome.url.DBConnect;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Quản lý phương thức vận chuyển (admin) - bảng shipping_methods. Khách chọn phương thức ở checkout qua OrderService.getShippingMethods(). */
public class ShippingService {

    public static class Row {
        public int id; public String code, name, carrier, status; public double fee; public Integer days;
        public int getId() { return id; } public String getCode() { return code; } public String getName() { return name; }
        public String getCarrier() { return carrier; } public String getStatus() { return status; } public double getFee() { return fee; } public Integer getDays() { return days; }
    }

    public List<Row> listAll() {
        List<Row> list = new ArrayList<>();
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT shipping_method_id, method_code, method_name, carrier_code, base_fee, estimated_days, status FROM shipping_methods ORDER BY shipping_method_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Row r = new Row(); r.id = rs.getInt(1); r.code = rs.getString(2); r.name = rs.getString(3); r.carrier = rs.getString(4);
                r.fee = rs.getDouble(5); int d = rs.getInt(6); r.days = rs.wasNull() ? null : d; r.status = rs.getString(7); list.add(r);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** id = 0 -> thêm mới. Trả về null nếu OK. */
    public String save(int id, String code, String name, String carrier, String feeS, String daysS) {
        try {
            name = name == null ? "" : name.trim();
            if (name.isEmpty() || name.length() > 100) return "Tên phương thức không được để trống (tối đa 100 ký tự)!";
            BigDecimal fee = new BigDecimal(feeS.trim());
            if (fee.signum() < 0) return "Phí vận chuyển không được âm!";
            Integer days = daysS == null || daysS.trim().isEmpty() ? null : Integer.valueOf(daysS.trim());
            if (days != null && (days < 0 || days > 60)) return "Số ngày dự kiến phải từ 0 đến 60!";
            carrier = carrier == null ? "" : carrier.trim();
            try (Connection con = DBConnect.getConnection()) {
                if (id == 0) {
                    code = code == null ? "" : code.trim().toUpperCase();
                    if (!code.matches("[A-Z0-9_-]{2,30}")) return "Mã phương thức chỉ gồm chữ/số/gạch (2-30 ký tự)!";
                    try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM shipping_methods WHERE method_code = ?")) {
                        ps.setString(1, code);
                        try (ResultSet rs = ps.executeQuery()) { rs.next(); if (rs.getInt(1) > 0) return "Mã \"" + code + "\" đã tồn tại!"; }
                    }
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO shipping_methods (method_code, method_name, carrier_code, base_fee, estimated_days, status) VALUES (?,?,?,?,?,'ACTIVE')")) {
                        ps.setString(1, code); ps.setString(2, name); ps.setString(3, carrier.isEmpty() ? code : carrier); ps.setBigDecimal(4, fee);
                        if (days == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, days);
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE shipping_methods SET method_name = ?, carrier_code = ISNULL(NULLIF(?, ''), carrier_code), base_fee = ?, estimated_days = ? WHERE shipping_method_id = ?")) {
                        ps.setString(1, name); ps.setString(2, carrier); ps.setBigDecimal(3, fee);
                        if (days == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, days);
                        ps.setInt(5, id); ps.executeUpdate();
                    }
                }
            }
            return null;
        } catch (NumberFormatException | NullPointerException e) { return "Dữ liệu nhập không hợp lệ!"; }
        catch (Exception e) { e.printStackTrace(); return "Không lưu được phương thức vận chuyển!"; }
    }

    /** Bật/tắt (đơn cũ vẫn giữ tham chiếu nên không xóa cứng). */
    public boolean toggle(int id) {
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(
                "UPDATE shipping_methods SET status = CASE WHEN status = 'ACTIVE' THEN 'INACTIVE' ELSE 'ACTIVE' END WHERE shipping_method_id = ?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
