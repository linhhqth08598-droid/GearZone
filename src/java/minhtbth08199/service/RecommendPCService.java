package minhtbth08199.service;

import fourSome.model.Parts;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import minhtbth08199.dao.BuildPartsDAO;

/** Lập cấu hình mẫu theo ngân sách, ưu tiên linh kiện còn hàng và điểm hiệu năng. */
public class RecommendPCService {
    private final BuildPartsDAO catalog = new BuildPartsDAO();
    // Leave only case and peripherals to the customer. Include a monitor in the recommendation.
    private static final int[] CATEGORIES = {1, 2, 3, 4, 5, 6, 7, 9, 10};

    public List<Integer> availableRamSizes() {
        TreeSet<Integer> values = new TreeSet<>();
        for (Parts p : catalog.findAvailable(3, "", 100)) {
            int size = ramSize(p.getPartName());
            if (size > 0) values.add(size);
        }
        return new ArrayList<>(values);
    }

    public List<Integer> availableMonitorRefreshRates() {
        TreeSet<Integer> values = new TreeSet<>();
        for (Parts p : catalog.findAvailable(10, "", 100)) {
            int hz = monitorHz(p.getPartName());
            if (hz > 0) values.add(hz);
        }
        return new ArrayList<>(values);
    }

    public List<Parts> recommend(double budget, String purpose, String priority, int ramGb, int fps) {
        if (budget < 3_000_000) throw new IllegalArgumentException("Ngân sách quá thấp để đề xuất cấu hình.");
        double[] weights = "office".equals(purpose)
                ? new double[]{.16,.14,.09,.02,.11,.06,.12,.04,.16}
                : new double[]{.20,.13,.09,.27,.07,.035,.11,.035,.10};
        if ("cpu".equals(priority)) { weights[0] += .04; weights[3] -= .04; }
        if ("gpu".equals(priority)) { weights[3] += .05; weights[0] -= .05; }
        if ("cost".equals(priority)) { weights[0] -= .02; weights[3] -= .04; weights[5] += .02; weights[7] += .02; }
        double ramReserve = minimumPrice(catalog.findAvailable(3, "", 100), 3, ramGb, fps);
        double monitorReserve = minimumPrice(catalog.findAvailable(10, "", 100), 10, ramGb, fps);
        if (!Double.isFinite(ramReserve)) throw new IllegalArgumentException("Không còn RAM " + ramGb + " GB trong kho.");
        if (!Double.isFinite(monitorReserve)) throw new IllegalArgumentException("Không có màn hình phù hợp từ " + fps + " Hz đang còn hàng.");
        if (ramReserve + monitorReserve > budget) throw new IllegalArgumentException("Ngân sách chưa đủ cho RAM và màn hình theo lựa chọn này.");
        List<Parts> picked = new ArrayList<>();
        double used = 0;
        for (int index=0; index<CATEGORIES.length; index++) {
            int cat = CATEGORIES[index];
            List<Parts> candidates = catalog.findAvailable(cat, "", 100);
            double cap = budget * weights[index];
            if (cat == 3) cap = budget;
            double reserved = (cat < 3 ? ramReserve : 0) + (cat != 10 ? monitorReserve : 0);
            double availableForPart = budget - used - reserved;
            Parts choice = null;
            for (Parts p : candidates) {
                double price = price(p);
                if (cat == 3 && ramSize(p.getPartName()) != ramGb) continue;
                if (cat == 10 && monitorHz(p.getPartName()) < fps) continue;
                if (price <= Math.min(cap, availableForPart) && (choice == null || score(p) > score(choice) || (score(p)==score(choice) && price>price(choice)))) choice=p;
            }
            // Keep the selected RAM capacity and requested display refresh rate even in fallback.
            if (choice == null) for (Parts p : candidates) {
                if (cat == 3 && ramSize(p.getPartName()) != ramGb) continue;
                if (cat == 10 && monitorHz(p.getPartName()) < fps) continue;
                if (price(p) <= availableForPart && (choice==null || price(p)>price(choice))) choice=p;
            }
            if (choice != null && price(choice) <= budget-used) { picked.add(choice); used += price(choice); }
        }
        if (picked.isEmpty()) throw new IllegalStateException("Không tìm thấy linh kiện còn hàng phù hợp ngân sách này.");
        return picked;
    }

    public double price(Parts p) { return p.getSalePrice()!=null ? p.getSalePrice() : p.getBasePrice(); }
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
