package minhtbth08199.service;

import fourSome.model.Parts;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;
import minhtbth08199.dao.BuildPartsDAO;

/** Lập cấu hình mẫu theo ngân sách, ưu tiên linh kiện còn hàng và điểm hiệu năng. */
public class RecommendPCService {
    private final BuildPartsDAO catalog = new BuildPartsDAO();
    // Leave only case and peripherals to the customer. Include a monitor in the recommendation.
    // Pick RAM before the motherboard so the selected board can match its DDR generation.
    private static final int[] CATEGORIES = {3, 1, 2, 4, 5, 6, 7, 9, 10};

    public List<Integer> availableRamSizes() {
        TreeSet<Integer> values = new TreeSet<>();
        for (Parts p : catalog.findAvailable(3, "", 5000)) {
            int size = ramSize(p.getPartName());
            if (size > 0) values.add(size);
        }
        return new ArrayList<>(values);
    }

    public List<Integer> availableMonitorRefreshRates() {
        TreeSet<Integer> values = new TreeSet<>();
        for (Parts p : catalog.findAvailable(10, "", 5000)) {
            int hz = monitorHz(p.getPartName());
            if (hz > 0) values.add(hz);
        }
        return new ArrayList<>(values);
    }

    public List<Parts> recommend(double budget, String purpose, String priority, String game, int ramGb, int fps) {
        if (budget < 3_000_000) throw new IllegalArgumentException("Ngân sách quá thấp để đề xuất cấu hình.");
        double[] weights = "office".equals(purpose)
                ? new double[]{.09,.16,.14,.02,.11,.06,.12,.04,.16}
                : new double[]{.09,.20,.13,.27,.07,.035,.11,.035,.10};
        if ("cs2".equals(game)) { weights[1] += .06; weights[3] -= .06; }
        if ("valorant".equals(game) || "lol".equals(game)) { weights[1] += .03; weights[3] -= .03; }
        if ("cpu".equals(priority)) { weights[1] += .04; weights[3] -= .04; }
        if ("gpu".equals(priority)) { weights[3] += .05; weights[1] -= .05; }
        if ("cost".equals(priority)) { weights[1] -= .02; weights[3] -= .04; weights[5] += .02; weights[7] += .02; }
        double ramReserve = minimumPrice(catalog.findAvailable(3, "", 5000), 3, ramGb, fps);
        double monitorReserve = minimumPrice(catalog.findAvailable(10, "", 5000), 10, ramGb, fps);
        if (!Double.isFinite(ramReserve)) throw new IllegalArgumentException("Không còn RAM " + ramGb + " GB trong kho.");
        if (!Double.isFinite(monitorReserve)) throw new IllegalArgumentException("Không có màn hình phù hợp từ " + fps + " Hz đang còn hàng.");
        if (ramReserve + monitorReserve > budget) throw new IllegalArgumentException("Ngân sách chưa đủ cho RAM và màn hình theo lựa chọn này.");
        List<Parts> picked = new ArrayList<>();
        Map<Integer, Parts> byCategory = new HashMap<>();
        double used = 0;
        for (int index=0; index<CATEGORIES.length; index++) {
            int cat = CATEGORIES[index];
            List<Parts> candidates = catalog.findAvailable(cat, "", 5000);
            double cap = budget * weights[index];
            if (cat == 3) cap = budget;
            double reserved = (cat != 3 && !byCategory.containsKey(3) ? ramReserve : 0)
                    + (cat != 10 && !byCategory.containsKey(10) ? monitorReserve : 0);
            double availableForPart = budget - used - reserved;
            Parts choice = null;
            for (Parts p : candidates) {
                double price = price(p);
                if (cat == 3 && ramSize(p.getPartName()) != ramGb) continue;
                if (cat == 10 && monitorHz(p.getPartName()) < fps) continue;
                if (!fitsCompatibility(cat, p, byCategory)) continue;
                if (price <= Math.min(cap, availableForPart) && betterChoice(cat, p, choice)) choice=p;
            }
            // Keep the selected RAM capacity and requested display refresh rate even in fallback.
            if (choice == null) for (Parts p : candidates) {
                if (cat == 3 && ramSize(p.getPartName()) != ramGb) continue;
                if (cat == 10 && monitorHz(p.getPartName()) < fps) continue;
                if (!fitsCompatibility(cat, p, byCategory)) continue;
                if (price(p) <= availableForPart && betterChoice(cat, p, choice)) choice=p;
            }
            if (choice != null && price(choice) <= budget-used) { picked.add(choice); byCategory.put(cat, choice); used += price(choice); }
        }
        if (picked.isEmpty()) throw new IllegalStateException("Không tìm thấy linh kiện còn hàng phù hợp ngân sách này.");
        int[] required = {1, 2, 3, 4, 5, 7, 10};
        String[] labels = {"CPU", "Mainboard", "RAM", "Card đồ họa", "SSD", "Nguồn", "Màn hình"};
        List<String> missing = new ArrayList<>();
        for (int i = 0; i < required.length; i++) if (!byCategory.containsKey(required[i])) missing.add(labels[i]);
        if (!missing.isEmpty()) throw new IllegalArgumentException("Ngân sách hoặc tồn kho hiện tại chưa đủ để tạo build đầy đủ. Chưa chọn được: " + String.join(", ", missing) + ". Hãy thử mức ngân sách cao hơn.");
        return picked;
    }

    public double price(Parts p) { return p.getSalePrice()!=null ? p.getSalePrice() : p.getBasePrice(); }
    private boolean betterChoice(int category, Parts candidate, Parts current) {
        if (current == null) return true;
        // Preserve more budget for core components while honoring exact RAM/Hz requirements.
        if (category == 3 || category == 10) return price(candidate) < price(current);
        int candidateScore = score(candidate);
        int currentScore = score(current);
        // A zero score means the catalog has no performance data. In that case,
        // prefer the cheaper part so greedy selection does not consume the
        // budget needed for the remaining required components.
        return candidateScore > currentScore
                || (candidateScore == currentScore && price(candidate) < price(current));
    }
    public List<String> compatibilityNotes(List<Parts> parts) {
        List<String> notes = new ArrayList<>();
        for (Parts p : parts) if (!catalog.hasCompatibilitySpecs(p.getCategoryId(), p.getPartId()))
            notes.add("Chưa có thông số tương thích của " + p.getPartName() + " trong dữ liệu kho; cần xác nhận thủ công.");
        notes.add("Chuẩn DDR4/DDR5 của RAM hệ thống phải khớp với Mainboard. Bộ nhớ GDDR trên card đồ họa là bộ nhớ riêng, không cần cùng chuẩn DDR với RAM.");
        return notes;
    }
    private boolean fitsCompatibility(int category, Parts candidate, Map<Integer, Parts> selected) {
        if (category == 1 && selected.containsKey(3)) {
            Parts ram = selected.get(3);
            if (catalog.hasCompatibilitySpecs(1, candidate.getPartId()) && catalog.hasCompatibilitySpecs(3, ram.getPartId())
                    && !catalog.hasCompatibleMainboardFor(candidate.getPartId(), ram.getPartId())) return false;
        }
        if (category == 2) {
            if (selected.containsKey(1)) {
                Parts cpu = selected.get(1);
                if (catalog.hasCompatibilitySpecs(1, cpu.getPartId()) && catalog.hasCompatibilitySpecs(2, candidate.getPartId())
                        && !catalog.isCpuMainboardCompatible(cpu.getPartId(), candidate.getPartId())) return false;
            }
            if (selected.containsKey(3)) {
                Parts ram = selected.get(3);
                String ramDdr = ddrType(ram.getPartName());
                String boardDdr = ddrType(candidate.getPartName());
                if (ramDdr != null && boardDdr != null) return ramDdr.equals(boardDdr);
                if (catalog.hasCompatibilitySpecs(3, ram.getPartId()) && catalog.hasCompatibilitySpecs(2, candidate.getPartId())
                        && !catalog.isRamMainboardCompatible(ram.getPartId(), candidate.getPartId())) return false;
            }
            return true;
        }
        if (category == 3 && selected.containsKey(2)) {
            Parts board = selected.get(2);
            String ramDdr = ddrType(candidate.getPartName());
            String boardDdr = ddrType(board.getPartName());
            if (ramDdr != null && boardDdr != null) return ramDdr.equals(boardDdr);
            if (!catalog.hasCompatibilitySpecs(3, candidate.getPartId()) || !catalog.hasCompatibilitySpecs(2, board.getPartId())) return true;
            return catalog.isRamMainboardCompatible(candidate.getPartId(), board.getPartId());
        }
        if (category == 7 && selected.containsKey(1) && selected.containsKey(4)
                && catalog.hasCompatibilitySpecs(7, candidate.getPartId())) {
            return catalog.isPsuEnough(candidate.getPartId(), selected.get(1).getPartId(), selected.get(4).getPartId());
        }
        return true;
    }
    private String ddrType(String name) {
        if (name == null) return null;
        String normalized = name.toUpperCase(java.util.Locale.ROOT).replaceAll("\\s+", "");
        if (normalized.contains("DDR5")) return "DDR5";
        if (normalized.contains("DDR4")) return "DDR4";
        return null;
    }
    private int score(Parts p) { return p.getGamingPerformanceScore()==null ? 0 : p.getGamingPerformanceScore(); }
    private int ramSize(String name) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?i)(\\d+)\\s*GB").matcher(name == null ? "" : name);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }
    private int monitorHz(String name) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?i)(\\d+)\\s*Hz").matcher(name == null ? "" : name);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }
    private double minimumPrice(List<Parts> parts, int category, int ramGb, int fps) {
        double minimum = Double.POSITIVE_INFINITY;
        for (Parts p : parts) {
            if (category == 3 && ramSize(p.getPartName()) != ramGb) continue;
            if (category == 10 && monitorHz(p.getPartName()) < fps) continue;
            minimum = Math.min(minimum, price(p));
        }
        return minimum;
    }
}
