package khanhnb08894.service;

import fourSome.url.DBConnect;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Đọc thông số kỹ thuật của linh kiện từ các bảng spec_* (spec_cpu, spec_vga, ...)
 * để hiển thị ra giao diện người dùng.
 *
 * - getGeneralInfo(partId): thông tin chung (danh mục, thương hiệu, dòng sản phẩm, RGB)
 * - getTechSpecs(partId):   bảng thông số kỹ thuật chi tiết (nhãn tiếng Việt + giá trị)
 * - getSummaries():         tóm tắt 1 dòng cho mọi sản phẩm (hiện dưới tên ở thẻ sản phẩm)
 *
 * Mỗi hàm trả về cặp {nhãn, giá trị}. Không có dữ liệu thì trả về danh sách rỗng.
 */
public class SpecService {

    /** Mô tả 1 bảng spec: tên bảng + danh sách cột {tên cột, nhãn hiển thị, đơn vị}. */
    private static class Def {
        final String table;
        final String[][] cols;
        Def(String table, String[][] cols) {
            this.table = table;
            this.cols = cols;
        }
    }

    private static final Def[] DEFS = {
        new Def("spec_cpu", new String[][]{
            {"socket_id", "Socket", ""},
            {"cores", "Số nhân", ""},
            {"threads", "Số luồng", ""},
            {"base_clock_ghz", "Xung nhịp cơ bản", " GHz"},
            {"boost_clock_ghz", "Xung nhịp tối đa", " GHz"},
            {"tdp_watts", "TDP", " W"},
            {"has_integrated_gpu", "Đồ họa tích hợp", ""},
            {"supported_ram_type", "Loại RAM hỗ trợ", ""},
            {"generation", "Thế hệ", ""}}),
        new Def("spec_mainboard", new String[][]{
            {"socket_id", "Socket", ""},
            {"chipset", "Chipset", ""},
            {"form_factor", "Kích thước", ""},
            {"ram_standard", "Chuẩn RAM", ""},
            {"ram_slots", "Số khe RAM", ""},
            {"max_ram_speed_mhz", "Tốc độ RAM tối đa", " MHz"},
            {"max_ram_capacity_gb", "Dung lượng RAM tối đa", " GB"},
            {"m2_slots_count", "Số khe M.2", ""},
            {"supports_nvme", "Hỗ trợ NVMe", ""},
            {"pcie_generation", "Chuẩn PCIe", ""},
            {"sata_ports_count", "Số cổng SATA", ""},
            {"cpu_power_connectors", "Nguồn CPU", ""},
            {"supported_cpu_generations", "Thế hệ CPU hỗ trợ", ""}}),
        new Def("spec_ram", new String[][]{
            {"ddr_type", "Chuẩn RAM", ""},
            {"capacity_gb", "Dung lượng", " GB"},
            {"bus_speed_mhz", "Tốc độ bus", " MHz"},
            {"module_count", "Số thanh", ""},
            {"cas_latency", "Độ trễ CAS", ""}}),
        new Def("spec_vga", new String[][]{
            {"gpu_chipset", "Chip đồ họa", ""},
            {"vram_capacity_gb", "Dung lượng VRAM", " GB"},
            {"vram_type", "Loại VRAM", ""},
            {"tgp_watts", "Công suất (TGP)", " W"},
            {"recommended_psu_watts", "Nguồn đề xuất", " W"},
            {"power_connectors", "Đầu cấp nguồn", ""},
            {"length_mm", "Chiều dài card", " mm"},
            {"pcie_interface", "Giao tiếp PCIe", ""},
            {"hdmi_ports", "Cổng HDMI", ""},
            {"displayport_ports", "Cổng DisplayPort", ""}}),
        new Def("spec_storage", new String[][]{
            {"storage_type", "Loại ổ", ""},
            {"form_factor", "Kích thước", ""},
            {"interface", "Chuẩn giao tiếp", ""},
            {"capacity_gb", "Dung lượng", " GB"},
            {"read_speed_mbps", "Tốc độ đọc", " MB/s"},
            {"write_speed_mbps", "Tốc độ ghi", " MB/s"}}),
        new Def("spec_psu", new String[][]{
            {"wattage", "Công suất", " W"},
            {"efficiency_rating", "Chứng nhận hiệu suất", ""},
            {"modularity", "Kiểu dây", ""},
            {"pcie_connectors_count", "Số đầu PCIe", ""},
            {"has_12vhpwr", "Đầu 12VHPWR", ""},
            {"eps_connectors_count", "Số đầu EPS", ""},
            {"form_factor", "Kích thước", ""}}),
        new Def("spec_case", new String[][]{
            {"supported_motherboards", "Mainboard hỗ trợ", ""},
            {"max_vga_length_mm", "Card đồ họa tối đa", " mm"},
            {"max_cpu_cooler_height_mm", "Tản nhiệt CPU tối đa", " mm"},
            {"fan_support_info", "Hỗ trợ quạt", ""}}),
        new Def("spec_cooler", new String[][]{
            {"cooler_type", "Loại tản nhiệt", ""},
            {"height_mm", "Chiều cao", " mm"},
            {"radiator_size_mm", "Kích thước radiator", " mm"},
            {"supported_sockets", "Socket hỗ trợ", ""},
            {"max_tdp_cooling_watts", "Công suất tản tối đa", " W"}}),
        new Def("spec_monitor", new String[][]{
            {"screen_size_inch", "Kích thước màn hình", " inch"},
            {"resolution", "Độ phân giải", ""},
            {"refresh_rate_hz", "Tần số quét", " Hz"},
            {"panel_type", "Tấm nền", ""},
            {"response_time_ms", "Thời gian phản hồi", " ms"},
            {"adaptive_sync", "Đồng bộ hình ảnh", ""},
            {"hdmi_ports", "Cổng HDMI", ""},
            {"displayport_ports", "Cổng DisplayPort", ""},
            {"hdmi_version", "Phiên bản HDMI", ""},
            {"displayport_version", "Phiên bản DisplayPort", ""}}),
        new Def("spec_peripheral", new String[][]{
            {"peripheral_type", "Loại thiết bị", ""},
            {"connection_type", "Kết nối", ""},
            {"switch_type", "Loại switch", ""},
            {"layout", "Layout", ""},
            {"dpi_max", "DPI tối đa", ""},
            {"polling_rate_hz", "Tần số phản hồi", " Hz"},
            {"weight_g", "Trọng lượng", " g"}})
    };

    // ------------------------------------------------------------------
    // 1. Thông tin chung (danh mục, thương hiệu, dòng sản phẩm, RGB)
    // ------------------------------------------------------------------
    public List<String[]> getGeneralInfo(int partId) {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT c.category_name, b.brand_name, s.series_name, p.is_rgb "
                + "FROM parts p "
                + "LEFT JOIN categories c ON c.category_id = p.category_id "
                + "LEFT JOIN brands b ON b.brand_id = p.brand_id "
                + "LEFT JOIN product_series s ON s.series_id = p.series_id "
                + "WHERE p.part_id = ?";
        try (Connection con = DBConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    add(rows, "Danh mục", rs.getString("category_name"));
                    add(rows, "Thương hiệu", rs.getString("brand_name"));
                    add(rows, "Dòng sản phẩm", rs.getString("series_name"));
                    add(rows, "Đèn RGB", rs.getBoolean("is_rgb") ? "Có" : "Không");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    // ------------------------------------------------------------------
    // 2. Thông số kỹ thuật chi tiết của 1 sản phẩm
    // ------------------------------------------------------------------
    public List<String[]> getTechSpecs(int partId) {
        List<String[]> rows = new ArrayList<>();
        try (Connection con = DBConnect.getConnection()) {
            Map<Integer, String> sockets = loadSockets(con);
            for (Def d : DEFS) {
                Map<Integer, Map<String, Object>> data = loadRows(con, d.table, partId);
                Map<String, Object> row = data.get(partId);
                if (row == null) {
                    continue;
                }
                for (String[] c : d.cols) {
                    add(rows, c[1], format(c[0], row.get(c[0]), c[2], sockets));
                }
                break; // mỗi linh kiện chỉ thuộc đúng 1 loại
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    // ------------------------------------------------------------------
    // 3. Tóm tắt 1 dòng cho tất cả sản phẩm (partId -> "6 nhân 12 luồng · 4.6 GHz · AM4")
    // ------------------------------------------------------------------
    public Map<Integer, String> getSummaries() {
        Map<Integer, String> result = new HashMap<>();
        try (Connection con = DBConnect.getConnection()) {
            Map<Integer, String> sockets = loadSockets(con);
            for (Def d : DEFS) {
                Map<Integer, Map<String, Object>> data = loadRows(con, d.table, null);
                for (Map.Entry<Integer, Map<String, Object>> e : data.entrySet()) {
                    String s = summarize(d.table, e.getValue(), sockets);
                    if (s != null && !s.isEmpty()) {
                        result.put(e.getKey(), s);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // ------------------------------------------------------------------
    // Nội bộ
    // ------------------------------------------------------------------
    private String summarize(String table, Map<String, Object> r, Map<Integer, String> sockets) {
        List<String> parts = new ArrayList<>();
        switch (table) {
            case "spec_cpu":
                parts.add(num(r.get("cores")) + " nhân " + num(r.get("threads")) + " luồng");
                parts.add(num(r.get("boost_clock_ghz")) + " GHz");
                parts.add(socketName(r.get("socket_id"), sockets));
                break;
            case "spec_mainboard":
                parts.add(socketName(r.get("socket_id"), sockets));
                parts.add(str(r.get("chipset")));
                parts.add(str(r.get("form_factor")));
                break;
            case "spec_ram":
                parts.add(str(r.get("ddr_type")));
                parts.add(num(r.get("capacity_gb")) + " GB");
                parts.add(num(r.get("bus_speed_mhz")) + " MHz");
                break;
            case "spec_vga":
                parts.add(num(r.get("vram_capacity_gb")) + " GB " + str(r.get("vram_type")));
                parts.add(str(r.get("gpu_chipset")));
                break;
            case "spec_storage":
                parts.add(str(r.get("storage_type")));
                parts.add(num(r.get("capacity_gb")) + " GB");
                parts.add(str(r.get("interface")));
                break;
            case "spec_psu":
                parts.add(num(r.get("wattage")) + " W");
                parts.add(str(r.get("efficiency_rating")));
                break;
            case "spec_case":
                parts.add(str(r.get("supported_motherboards")));
                break;
            case "spec_cooler":
                parts.add(str(r.get("cooler_type")));
                parts.add(num(r.get("max_tdp_cooling_watts")) + " W");
                break;
            case "spec_monitor":
                parts.add(num(r.get("screen_size_inch")) + " inch");
                parts.add(str(r.get("resolution")));
                parts.add(num(r.get("refresh_rate_hz")) + " Hz");
                break;
            case "spec_peripheral":
                parts.add(str(r.get("peripheral_type")));
                parts.add(str(r.get("connection_type")));
                break;
            default:
                break;
        }
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p == null || p.trim().isEmpty() || p.trim().startsWith("null") || p.trim().equals("0 GB")) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(" · ");
            }
            sb.append(p.trim());
        }
        return sb.toString();
    }

    /** Đọc 1 bảng spec (toàn bộ hoặc 1 part_id). Khoá map ngoài = part_id, map trong = cột (chữ thường) -> giá trị. */
    private Map<Integer, Map<String, Object>> loadRows(Connection con, String table, Integer partId) {
        Map<Integer, Map<String, Object>> data = new LinkedHashMap<>();
        String sql = "SELECT * FROM [" + table + "]" + (partId != null ? " WHERE part_id = ?" : "");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (partId != null) {
                ps.setInt(1, partId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        row.put(md.getColumnLabel(i).toLowerCase(), rs.getObject(i));
                    }
                    data.put(rs.getInt("part_id"), row);
                }
            }
        } catch (Exception e) {
            // Bảng chưa có/lỗi -> bỏ qua, không làm hỏng cả trang
            e.printStackTrace();
        }
        return data;
    }

    private Map<Integer, String> loadSockets(Connection con) {
        Map<Integer, String> map = new HashMap<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT socket_id, socket_name FROM socket_types");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getInt(1), rs.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private String format(String col, Object v, String unit, Map<Integer, String> sockets) {
        if (v == null) {
            return null;
        }
        if (col.equals("socket_id")) {
            return socketName(v, sockets);
        }
        if (v instanceof Boolean) {
            return ((Boolean) v) ? "Có" : "Không";
        }
        String s = (v instanceof Number) ? num(v) : v.toString().trim();
        if (s.isEmpty()) {
            return null;
        }
        return s + unit;
    }

    private String socketName(Object id, Map<Integer, String> sockets) {
        if (id == null) {
            return null;
        }
        return sockets.get(((Number) id).intValue());
    }

    private String num(Object v) {
        if (v == null) {
            return "null";
        }
        if (v instanceof BigDecimal) {
            return ((BigDecimal) v).stripTrailingZeros().toPlainString();
        }
        return v.toString();
    }

    private String str(Object v) {
        return v == null ? null : v.toString();
    }

    private void add(List<String[]> rows, String label, String value) {
        if (value != null && !value.trim().isEmpty()) {
            rows.add(new String[]{label, value.trim()});
        }
    }
}
