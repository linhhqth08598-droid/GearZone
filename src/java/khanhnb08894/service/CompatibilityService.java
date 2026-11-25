package khanhnb08894.service;

import fourSome.model.CompatibilityResult;
import fourSome.url.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompatibilityService {

    /** Kiểm tra tương thích nhận danh sách partId trực tiếp (tự tra loại linh kiện từ bảng parts). */
    public CompatibilityResult checkBuild(List<Integer> partIds) {
        return checkBuild(partIds, null, null);
    }

    public CompatibilityResult checkBuild(List<Integer> partIds, Integer userId, Integer buildId) {
        Map<String, Integer> mapped = new java.util.HashMap<>();
        if (partIds != null && !partIds.isEmpty()) {
            try (Connection conn = DBConnect.getConnection()) {
                if (conn != null) {
                    for (Integer pid : partIds) {
                        if (pid == null || pid <= 0) {
                            continue;
                        }
                        String key = resolveSlotKeyByPartId(conn, pid);
                        if (key != null && !mapped.containsKey(key)) {
                            mapped.put(key, pid);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return checkBuild(mapped, userId, buildId);
    }

    private String resolveSlotKeyByPartId(Connection conn, int partId) {
        String sql = "SELECT c.category_code, c.category_name FROM [parts] p "
                + "LEFT JOIN [categories] c ON c.category_id = p.category_id WHERE p.part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String raw = ((rs.getString("category_code") != null ? rs.getString("category_code") : "")
                            + " " + (rs.getString("category_name") != null ? rs.getString("category_name") : ""))
                            .toUpperCase();
                    if (raw.contains("CPU")) return "cpu";
                    if (raw.contains("MAIN") || raw.contains("BOARD")) return "main";
                    if (raw.contains("RAM")) return "ram";
                    if (raw.contains("VGA") || raw.contains("GPU") || raw.contains("GRAPH")) return "vga";
                    if (raw.contains("SSD") || raw.contains("NVME") || raw.contains("STORAGE")) return "ssd";
                    if (raw.contains("PSU") || raw.contains("POWER")) return "psu";
                    if (raw.contains("CASE")) return "case";
                    if (raw.contains("COOLER") || raw.contains("FAN") || raw.contains("TAN")) return "cooler";
                    if (raw.contains("MONITOR") || raw.contains("MAN")) return "monitor";
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Kiểm tra tương thích toàn cấu hình (UC 41-69)
     * @param parts Map key: "cpu", "main", "ram", "vga", "ssd", "hdd", "psu", "case", "cooler", "monitor"
     */
    public CompatibilityResult checkBuild(Map<String, Integer> parts) {
        return checkBuild(parts, null, null);
    }

    public CompatibilityResult checkBuild(Map<String, Integer> parts, Integer userId, Integer buildId) {
        CompatibilityResult result = new CompatibilityResult();
        if (parts == null || parts.isEmpty()) {
            return result;
        }

        int cpuId = parts.getOrDefault("cpu", 0);
        int mainId = parts.getOrDefault("main", 0);
        int ramId = parts.getOrDefault("ram", 0);
        int ramQty = Math.max(1, parts.getOrDefault("ramQty", 1));
        int vgaId = parts.getOrDefault("vga", 0);
        int ssdId = parts.getOrDefault("ssd", 0);
        int psuId = parts.getOrDefault("psu", 0);
        int caseId = parts.getOrDefault("case", 0);
        int coolerId = parts.getOrDefault("cooler", 0);
        int monitorId = parts.getOrDefault("monitor", 0);

        List<String> loggedReasons = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                result.addError("Không thể kết nối cơ sở dữ liệu để kiểm tra tương thích.");
                return result;
            }

            // Đọc thông số từng linh kiện đã chọn
            CpuInfo cpu = cpuId > 0 ? loadCpu(conn, cpuId) : null;
            MainInfo main = mainId > 0 ? loadMain(conn, mainId) : null;
            RamInfo ram = ramId > 0 ? loadRam(conn, ramId) : null;
            VgaInfo vga = vgaId > 0 ? loadVga(conn, vgaId) : null;
            PsuInfo psu = psuId > 0 ? loadPsu(conn, psuId) : null;
            CaseInfo pcCase = caseId > 0 ? loadCase(conn, caseId) : null;
            CoolerInfo cooler = coolerId > 0 ? loadCooler(conn, coolerId) : null;
            StorageInfo ssd = ssdId > 0 ? loadStorage(conn, ssdId) : null;
            MonitorInfo monitor = monitorId > 0 ? loadMonitor(conn, monitorId) : null;

            // Áp dụng quy tắc cấu hình động từ bảng compatibility_rules (nếu có quy tắc ACTIVE tương ứng)
            applyDynamicCompatibilityRules(conn, cpu, main, ram, vga, psu, pcCase, cooler, ssd, monitor, result, loggedReasons);

            // 1. CPU <-> Mainboard (UC 41-44: Socket, thế hệ CPU)
            if (cpu != null && main != null) {
                if (cpu.socketId > 0 && main.socketId > 0 && cpu.socketId != main.socketId) {
                    String sug = suggestMainboard(conn, cpu.socketId, ram != null ? ram.ddrType : null);
                    String msg = "Socket CPU (" + cpu.socketName + ") không khớp với Mainboard (" + main.socketName + ")." + sug;
                    result.addError(msg);
                    loggedReasons.add(msg);
                } else if (notBlank(cpu.generation) && notBlank(main.supportedCpuGenerations)
                        && !containsIgnoreCase(main.supportedCpuGenerations, cpu.generation)) {
                    String msg = "Mainboard (" + main.chipset + ") chỉ hỗ trợ CPU thế hệ: " + main.supportedCpuGenerations
                            + ", không hỗ trợ thế hệ " + cpu.generation + ".";
                    result.addError(msg);
                    loggedReasons.add(msg);
                }
            }

            // 2. RAM <-> Mainboard & CPU (UC 45-49: DDR4/DDR5, bus, dung lượng tối đa, số khe)
            if (ram != null && main != null) {
                if (notBlank(ram.ddrType) && notBlank(main.ramStandard)
                        && !ram.ddrType.equalsIgnoreCase(main.ramStandard.trim())) {
                    String sug = suggestRam(conn, main.ramStandard);
                    String msg = "Mainboard hỗ trợ chuẩn " + main.ramStandard + ", không lắp được RAM " + ram.ddrType + "." + sug;
                    result.addError(msg);
                    loggedReasons.add(msg);
                }
                int totalModules = ram.moduleCount * ramQty;
                if (main.ramSlots > 0 && totalModules > main.ramSlots) {
                    String msg = "Tổng số thanh RAM (" + totalModules + " thanh) vượt quá số khe cắm của Mainboard (" + main.ramSlots + " khe).";
                    result.addError(msg);
                    loggedReasons.add(msg);
                }
                int totalCap = ram.capacityGb * ramQty;
                if (main.maxRamCapacityGb > 0 && totalCap > main.maxRamCapacityGb) {
                    String msg = "Tổng dung lượng RAM (" + totalCap + "GB) vượt quá mức tối đa Mainboard hỗ trợ (" + main.maxRamCapacityGb + "GB).";
                    result.addError(msg);
                    loggedReasons.add(msg);
                }
                if (main.maxRamSpeedMhz > 0 && ram.busSpeedMhz > main.maxRamSpeedMhz) {
                    result.addWarning("Bus RAM (" + ram.busSpeedMhz + "MHz) cao hơn mức tối đa của Mainboard ("
                            + main.maxRamSpeedMhz + "MHz), RAM sẽ tự hạ xung khi chạy.");
                }
            }
            if (ram != null && cpu != null && notBlank(ram.ddrType) && notBlank(cpu.supportedRamType)
                    && !containsIgnoreCase(cpu.supportedRamType, ram.ddrType)) {
                String msg = "CPU chỉ hỗ trợ chuẩn RAM " + cpu.supportedRamType + ", không tương thích với " + ram.ddrType + ".";
                result.addError(msg);
                loggedReasons.add(msg);
            }

            // 3. VGA <-> Mainboard (UC 50: khe PCIe)
            if (vga != null && main != null && notBlank(vga.pcieInterface) && notBlank(main.pcieGeneration)) {
                if (vga.pcieInterface.contains("5.0") && main.pcieGeneration.contains("3.0")) {
                    result.addWarning("VGA chuẩn " + vga.pcieInterface + " cắm trên Mainboard " + main.pcieGeneration + " vẫn chạy được nhưng có thể giảm nhẹ băng thông.");
                }
            }

            // 4. VGA <-> Case (UC 51: chiều dài VGA)
            if (vga != null && pcCase != null && vga.lengthMm > 0 && pcCase.maxVgaLengthMm > 0) {
                if (vga.lengthMm > pcCase.maxVgaLengthMm) {
                    String msg = "Chiều dài VGA (" + vga.lengthMm + "mm) vượt quá kích thước tối đa Case hỗ trợ (" + pcCase.maxVgaLengthMm + "mm).";
                    result.addError(msg);
                    loggedReasons.add(msg);
                }
            }

            // 5. Tản nhiệt <-> Case & Socket CPU (UC 52-53)
            if (cooler != null) {
                if (pcCase != null && cooler.heightMm > 0 && pcCase.maxCpuCoolerHeightMm > 0
                        && cooler.heightMm > pcCase.maxCpuCoolerHeightMm) {
                    String msg = "Chiều cao tản nhiệt (" + cooler.heightMm + "mm) vượt quá giới hạn của Case (" + pcCase.maxCpuCoolerHeightMm + "mm).";
                    result.addError(msg);
                    loggedReasons.add(msg);
                }
                if (cpu != null && notBlank(cpu.socketName) && notBlank(cooler.supportedSockets)
                        && !containsIgnoreCase(cooler.supportedSockets, cpu.socketName)) {
                    String msg = "Tản nhiệt chỉ hỗ trợ socket (" + cooler.supportedSockets + "), không khớp socket CPU (" + cpu.socketName + ").";
                    result.addError(msg);
                    loggedReasons.add(msg);
                }
                if (cpu != null && cooler.maxTdpCoolingWatts > 0 && cpu.tdpWatts > cooler.maxTdpCoolingWatts) {
                    result.addWarning("TDP của CPU (" + cpu.tdpWatts + "W) cao hơn mức tản nhiệt khuyến nghị (" + cooler.maxTdpCoolingWatts + "W).");
                }
            }

            // 6. Mainboard <-> Case (UC 54: Form Factor ATX / mATX / ITX)
            if (main != null && pcCase != null && notBlank(main.formFactor) && notBlank(pcCase.supportedMotherboards)) {
                if (!matchFormFactor(main.formFactor, pcCase.supportedMotherboards)) {
                    String msg = "Case chỉ hỗ trợ chuẩn Mainboard (" + pcCase.supportedMotherboards + "), không lắp vừa Mainboard " + main.formFactor + ".";
                    result.addError(msg);
                    loggedReasons.add(msg);
                }
            }

            // 7. Ổ cứng SSD/NVMe <-> Mainboard (UC 60-62)
            if (ssd != null && main != null) {
                boolean isM2 = containsIgnoreCase(ssd.formFactor, "M.2") || containsIgnoreCase(ssd.storageType, "NVMe");
                boolean isNvme = containsIgnoreCase(ssd.storageType, "NVMe") || containsIgnoreCase(ssd.iface, "NVMe");
                if (isM2 && main.m2SlotsCount <= 0) {
                    String msg = "Mainboard không có khe M.2 để lắp ổ cứng " + ssd.name + ".";
                    result.addError(msg);
                    loggedReasons.add(msg);
                } else if (isNvme && !main.supportsNvme) {
                    String msg = "Khe M.2 của Mainboard không hỗ trợ giao thức NVMe cho ổ " + ssd.name + ".";
                    result.addError(msg);
                    loggedReasons.add(msg);
                }
            }

            // 8. Công suất PSU & đầu cấp nguồn (UC 55-59, 69)
            if (psu != null && (cpu != null || vga != null)) {
                int systemWatt = (cpu != null ? cpu.tdpWatts : 0)
                        + (vga != null ? vga.tgpWatts : 0)
                        + 75; // phần nền: main, RAM, quạt, ổ cứng
                double headroomPercent = loadHeadroomPercent(conn, systemWatt);
                int recommendedByTdp = (int) Math.ceil(systemWatt * (1.0 + headroomPercent / 100.0));
                int vgaRecPsu = vga != null ? vga.recommendedPsuWatts : 0;
                int finalRecommended = Math.max(recommendedByTdp, vgaRecPsu);

                if (psu.wattage < systemWatt) {
                    String sug = suggestPsu(conn, finalRecommended);
                    String msg = "Nguồn PSU (" + psu.wattage + "W) không đủ công suất tải thực tế (~" + systemWatt + "W)." + sug;
                    result.addError(msg);
                    loggedReasons.add(msg);
                } else if (psu.wattage < finalRecommended) {
                    String sug = suggestPsu(conn, finalRecommended);
                    result.addWarning("Nguồn PSU (" + psu.wattage + "W) thấp hơn mức khuyến nghị (" + finalRecommended + "W) cho cấu hình này." + sug);
                }

                // Kiểm tra đầu cấp nguồn VGA (12VHPWR / 8-pin PCIe)
                if (vga != null && notBlank(vga.powerConnectors)) {
                    if (containsIgnoreCase(vga.powerConnectors, "12VHPWR") && !psu.has12vhpwr) {
                        result.addWarning("VGA yêu cầu đầu cấp nguồn 12VHPWR, PSU hiện tại chưa có sẵn cổng 12VHPWR (cần dùng cáp chuyển).");
                    }
                }
            }

            // 9. Màn hình <-> VGA (UC 63-64: cổng HDMI / DisplayPort)
            if (monitor != null && vga != null) {
                boolean hasCommonPort = (monitor.hdmiPorts > 0 && vga.hdmiPorts > 0)
                        || (monitor.displayportPorts > 0 && vga.displayportPorts > 0);
                if (!hasCommonPort) {
                    result.addWarning("Cổng xuất hình giữa VGA và Màn hình không trùng trực tiếp (HDMI/DisplayPort), có thể cần cáp chuyển đổi.");
                }
            }

            // Ghi log xung đột nếu có lỗi (UC 66-67)
            if (!loggedReasons.isEmpty()) {
                saveConflictLog(conn, userId, buildId, cpuId, mainId, vgaId, psuId, caseId, String.join(" | ", loggedReasons));
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.addError("Lỗi hệ thống khi kiểm tra tương thích: " + e.getMessage());
        }

        return result;
    }

    // --- Các hàm đọc dữ liệu từ DB ---
    private CpuInfo loadCpu(Connection conn, int id) throws Exception {
        String sql = "SELECT c.socket_id, s.socket_name, c.tdp_watts, c.supported_ram_type, c.generation "
                   + "FROM spec_cpu c LEFT JOIN socket_types s ON c.socket_id = s.socket_id WHERE c.part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                CpuInfo info = new CpuInfo();
                info.socketId = rs.getInt("socket_id");
                info.socketName = rs.getString("socket_name");
                info.tdpWatts = rs.getInt("tdp_watts");
                info.supportedRamType = rs.getString("supported_ram_type");
                info.generation = rs.getString("generation");
                return info;
            }
        }
    }

        private MainInfo loadMain(Connection conn, int id) throws Exception {
        String sql = "SELECT m.socket_id, s.socket_name, m.chipset, m.form_factor, m.ram_standard, "
                   + "m.ram_slots, m.max_ram_speed_mhz, m.max_ram_capacity_gb, m.m2_slots_count, "
                   + "m.supports_nvme, m.pcie_generation, m.cpu_power_connectors, m.supported_cpu_generations "
                   + "FROM spec_mainboard m LEFT JOIN socket_types s ON m.socket_id = s.socket_id "
                   + "WHERE m.part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                MainInfo info = new MainInfo();
                info.socketId = rs.getInt("socket_id");
                info.socketName = rs.getString("socket_name");
                info.chipset = rs.getString("chipset");
                info.formFactor = rs.getString("form_factor");
                info.ramStandard = rs.getString("ram_standard");
                info.ramSlots = rs.getInt("ram_slots");
                info.maxRamSpeedMhz = rs.getInt("max_ram_speed_mhz");
                info.maxRamCapacityGb = rs.getInt("max_ram_capacity_gb");
                info.m2SlotsCount = rs.getInt("m2_slots_count");
                info.supportsNvme = rs.getBoolean("supports_nvme");
                info.pcieGeneration = rs.getString("pcie_generation");
                info.supportedCpuGenerations = rs.getString("supported_cpu_generations");
                return info;
            }
        }
    }

    private RamInfo loadRam(Connection conn, int id) throws Exception {
        String sql = "SELECT ddr_type, capacity_gb, bus_speed_mhz, module_count FROM spec_ram WHERE part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                RamInfo info = new RamInfo();
                info.ddrType = rs.getString("ddr_type");
                info.capacityGb = rs.getInt("capacity_gb");
                info.busSpeedMhz = rs.getInt("bus_speed_mhz");
                info.moduleCount = Math.max(1, rs.getInt("module_count"));
                return info;
            }
        }
    }

    private VgaInfo loadVga(Connection conn, int id) throws Exception {
        String sql = "SELECT recommended_psu_watts, tgp_watts, power_connectors, length_mm, pcie_interface, hdmi_ports, displayport_ports FROM spec_vga WHERE part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                VgaInfo info = new VgaInfo();
                info.recommendedPsuWatts = rs.getInt("recommended_psu_watts");
                info.tgpWatts = rs.getInt("tgp_watts");
                info.powerConnectors = rs.getString("power_connectors");
                info.lengthMm = rs.getInt("length_mm");
                info.pcieInterface = rs.getString("pcie_interface");
                info.hdmiPorts = rs.getInt("hdmi_ports");
                info.displayportPorts = rs.getInt("displayport_ports");
                return info;
            }
        }
    }

    private PsuInfo loadPsu(Connection conn, int id) throws Exception {
        String sql = "SELECT wattage, pcie_connectors_count, has_12vhpwr, eps_connectors_count, form_factor FROM spec_psu WHERE part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                PsuInfo info = new PsuInfo();
                info.wattage = rs.getInt("wattage");
                info.pcieConnectorsCount = rs.getInt("pcie_connectors_count");
                info.has12vhpwr = rs.getBoolean("has_12vhpwr");
                info.epsConnectorsCount = rs.getInt("eps_connectors_count");
                info.formFactor = rs.getString("form_factor");
                return info;
            }
        }
    }

    private CaseInfo loadCase(Connection conn, int id) throws Exception {
        String sql = "SELECT supported_motherboards, max_vga_length_mm, max_cpu_cooler_height_mm FROM spec_case WHERE part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                CaseInfo info = new CaseInfo();
                info.supportedMotherboards = rs.getString("supported_motherboards");
                info.maxVgaLengthMm = rs.getInt("max_vga_length_mm");
                info.maxCpuCoolerHeightMm = rs.getInt("max_cpu_cooler_height_mm");
                return info;
            }
        }
    }

    private CoolerInfo loadCooler(Connection conn, int id) throws Exception {
        String sql = "SELECT height_mm, supported_sockets, max_tdp_cooling_watts FROM spec_cooler WHERE part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                CoolerInfo info = new CoolerInfo();
                info.heightMm = rs.getInt("height_mm");
                info.supportedSockets = rs.getString("supported_sockets");
                info.maxTdpCoolingWatts = rs.getInt("max_tdp_cooling_watts");
                return info;
            }
        }
    }

    private StorageInfo loadStorage(Connection conn, int id) throws Exception {
        String sql = "SELECT p.part_name, s.storage_type, s.form_factor, s.interface FROM spec_storage s JOIN parts p ON s.part_id = p.part_id WHERE s.part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                StorageInfo info = new StorageInfo();
                info.name = rs.getString("part_name");
                info.storageType = rs.getString("storage_type");
                info.formFactor = rs.getString("form_factor");
                info.iface = rs.getString("interface");
                return info;
            }
        }
    }

    private MonitorInfo loadMonitor(Connection conn, int id) throws Exception {
        String sql = "SELECT hdmi_ports, displayport_ports FROM spec_monitor WHERE part_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                MonitorInfo info = new MonitorInfo();
                info.hdmiPorts = rs.getInt("hdmi_ports");
                info.displayportPorts = rs.getInt("displayport_ports");
                return info;
            }
        }
    }

    private double loadHeadroomPercent(Connection conn, int systemWatt) {
        String sql = "SELECT TOP 1 recommended_psu_headroom_percent FROM psu_power_rules "
                   + "WHERE status = 'ACTIVE' AND min_system_wattage <= ? ORDER BY min_system_wattage DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, systemWatt);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (Exception ignored) {}
        return 20.0;
    }

    // --- Gợi ý linh kiện thay thế phù hợp trong kho (UC 68) ---
    private String suggestMainboard(Connection conn, int socketId, String ramStd) {
        String sql = "SELECT TOP 1 p.part_name FROM parts p JOIN spec_mainboard m ON p.part_id = m.part_id "
                   + "WHERE p.status = 'ACTIVE' AND m.socket_id = ? "
                   + (notBlank(ramStd) ? "AND m.ram_standard = ? " : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, socketId);
            if (notBlank(ramStd)) ps.setString(2, ramStd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return " Gợi ý thay bằng: " + rs.getString(1) + ".";
            }
        } catch (Exception ignored) {}
        return "";
    }

    private String suggestRam(Connection conn, String ramStd) {
        String sql = "SELECT TOP 1 p.part_name FROM parts p JOIN spec_ram r ON p.part_id = r.part_id "
                   + "WHERE p.status = 'ACTIVE' AND r.ddr_type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ramStd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return " Gợi ý chọn RAM: " + rs.getString(1) + ".";
            }
        } catch (Exception ignored) {}
        return "";
    }

    private String suggestPsu(Connection conn, int minWatt) {
        String sql = "SELECT TOP 1 p.part_name FROM parts p JOIN spec_psu u ON p.part_id = u.part_id "
                   + "WHERE p.status = 'ACTIVE' AND u.wattage >= ? ORDER BY u.wattage ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, minWatt);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return " Gợi ý nâng cấp PSU: " + rs.getString(1) + ".";
            }
        } catch (Exception ignored) {}
        return "";
    }

    private void applyDynamicCompatibilityRules(
            Connection conn,
            CpuInfo cpu,
            MainInfo main,
            RamInfo ram,
            VgaInfo vga,
            PsuInfo psu,
            CaseInfo pcCase,
            CoolerInfo cooler,
            StorageInfo ssd,
            MonitorInfo monitor,
            CompatibilityResult result,
            List<String> loggedReasons) {
        String sql = "SELECT rule_name, check_type, error_message, severity FROM [compatibility_rules] WHERE status = 'ACTIVE'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String checkType = rs.getString("check_type");
                String severity = rs.getString("severity");
                String errMsg = rs.getString("error_message");
                if (checkType == null || errMsg == null) {
                    continue;
                }
                String ct = checkType.trim().toUpperCase();
                boolean violated = false;
                if (("SOCKET_MATCH".equals(ct) || "CPU_MAINBOARD_SOCKET".equals(ct)) && cpu != null && main != null) {
                    violated = cpu.socketId > 0 && main.socketId > 0 && cpu.socketId != main.socketId;
                } else if (("RAM_DDR_MATCH".equals(ct) || "RAM_STANDARD".equals(ct)) && ram != null && main != null) {
                    violated = notBlank(ram.ddrType) && notBlank(main.ramStandard)
                            && !ram.ddrType.trim().equalsIgnoreCase(main.ramStandard.trim());
                } else if (("CASE_VGA_LENGTH".equals(ct) || "VGA_CASE_LENGTH".equals(ct)) && vga != null && pcCase != null) {
                    violated = vga.lengthMm > 0 && pcCase.maxVgaLengthMm > 0 && vga.lengthMm > pcCase.maxVgaLengthMm;
                } else if ("CASE_COOLER_HEIGHT".equals(ct) && cooler != null && pcCase != null) {
                    violated = cooler.heightMm > 0 && pcCase.maxCpuCoolerHeightMm > 0 && cooler.heightMm > pcCase.maxCpuCoolerHeightMm;
                } else if ("COOLER_SOCKET".equals(ct) && cooler != null && cpu != null) {
                    violated = notBlank(cpu.socketName) && notBlank(cooler.supportedSockets)
                            && !containsIgnoreCase(cooler.supportedSockets, cpu.socketName);
                }

                if (violated) {
                    String prefix = "[Rule: " + (rs.getString("rule_name") != null ? rs.getString("rule_name") : ct) + "] ";
                    String full = prefix + errMsg.trim();
                    if ("WARNING".equalsIgnoreCase(severity)) {
                        result.addWarning(full);
                    } else {
                        result.addError(full);
                        loggedReasons.add(full);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void saveConflictLog(Connection conn, Integer userId, Integer buildId, int cpuId, int mainId, int vgaId, int psuId, int caseId, String reason) {
        String sql = "INSERT INTO compatibility_conflict_logs (user_id, build_id, cpu_id, mainboard_id, vga_id, psu_id, case_id, conflict_reason, created_at, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME(), 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null && userId > 0) ps.setInt(1, userId); else ps.setNull(1, Types.INTEGER);
            if (buildId != null && buildId > 0) ps.setInt(2, buildId); else ps.setNull(2, Types.INTEGER);
            if (cpuId > 0) ps.setInt(3, cpuId); else ps.setNull(3, Types.INTEGER);
            if (mainId > 0) ps.setInt(4, mainId); else ps.setNull(4, Types.INTEGER);
            if (vgaId > 0) ps.setInt(5, vgaId); else ps.setNull(5, Types.INTEGER);
            if (psuId > 0) ps.setInt(6, psuId); else ps.setNull(6, Types.INTEGER);
            if (caseId > 0) ps.setInt(7, caseId); else ps.setNull(7, Types.INTEGER);
            ps.setString(8, reason);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }
    private boolean containsIgnoreCase(String haystack, String needle) {
        return haystack != null && needle != null && haystack.toLowerCase().contains(needle.trim().toLowerCase());
    }
    private boolean matchFormFactor(String mbForm, String caseSupported) {
        String f = mbForm.trim().toUpperCase();
        for (String token : caseSupported.toUpperCase().split("[,;/\\s]+")) {
            if (token.equals(f)) return true;
        }
        return false;
    }

    // Inner DTOs nội bộ
    private static class CpuInfo { int socketId, tdpWatts; String socketName, supportedRamType, generation; }
    private static class MainInfo { int socketId, ramSlots, maxRamSpeedMhz, maxRamCapacityGb, m2SlotsCount; boolean supportsNvme; String socketName, chipset, formFactor, ramStandard, pcieGeneration, supportedCpuGenerations; }
    private static class RamInfo { String ddrType; int capacityGb, busSpeedMhz, moduleCount; }
    private static class VgaInfo { int recommendedPsuWatts, tgpWatts, lengthMm, hdmiPorts, displayportPorts; String powerConnectors, pcieInterface; }
    private static class PsuInfo { int wattage, pcieConnectorsCount, epsConnectorsCount; boolean has12vhpwr; String formFactor; }
    private static class CaseInfo { String supportedMotherboards; int maxVgaLengthMm, maxCpuCoolerHeightMm; }
    private static class CoolerInfo { int heightMm, maxTdpCoolingWatts; String supportedSockets; }
    private static class StorageInfo { String name, storageType, formFactor, iface; }
    private static class MonitorInfo { int hdmiPorts, displayportPorts; }
}
