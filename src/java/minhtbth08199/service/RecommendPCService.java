package minhtbth08199.service;

import fourSome.model.BudgetTierRules;
import fourSome.model.Parts;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;
import linhhqth08598.service.BudgetTierService;
import minhtbth08199.dao.BuildPartsDAO;

/** Lập cấu hình mẫu theo ngân sách, ưu tiên linh kiện còn hàng và điểm hiệu năng. */
public class RecommendPCService {
    private final BuildPartsDAO catalog = new BuildPartsDAO();
    private final BudgetTierService budgetTierService = new BudgetTierService();
    // Leave only case and peripherals to the customer. Include a monitor in the recommendation.
    // Pick RAM before the motherboard so the selected board can match its DDR generation.
    private static final int[] CATEGORIES = {3, 1, 2, 4, 5, 6, 7, 9, 10};
    private static final int[] REQUIRED_CATS = {1, 2, 3, 4, 5, 7, 10};
    private static final String[] REQUIRED_LABELS = {"CPU", "Mainboard", "RAM", "Card đồ họa", "SSD", "Nguồn", "Màn hình"};

    public List<Integer> availableRamSizes() {
        TreeSet<Integer> values = new TreeSet<>(java.util.Arrays.asList(8, 16, 32, 64, 128, 256));
        for (Parts p : catalog.findAvailable(3, "", 5000)) {
            int size = ramSize(p.getPartName());
            if (size > 0 && size <= 256) values.add(size);
        }
        return new ArrayList<>(values);
    }

    public List<Integer> availableMonitorRefreshRates() {
        TreeSet<Integer> values = new TreeSet<>(java.util.Arrays.asList(360, 520, 1000));
        for (Parts p : catalog.findAvailable(10, "", 5000)) {
            int hz = monitorHz(p.getPartName());
            if (hz > 0) values.add(hz);
        }
        return new ArrayList<>(values);
    }

    public List<Parts> recommend(double budget, String purpose, String priority, String game, int ramGb, int fps) {
        if (budget < 3_000_000) throw new IllegalArgumentException("Ngân sách quá thấp để đề xuất cấu hình.");
        double[] weights = resolveWeights(budget, purpose, priority, game);
        if (ramGb < 1 || ramGb > 256) throw new IllegalArgumentException("Dung lượng RAM hỗ trợ tối đa 256 GB.");
        int[] required = REQUIRED_CATS;
        String[] labels = REQUIRED_LABELS;
        Map<Integer, List<Parts>> candidatesByCat = new HashMap<>();
        Map<Integer, Double> minPriceByCat = new HashMap<>();
        for (int req : required) {
            List<Parts> list = catalog.findAvailable(req, "", 5000);
            candidatesByCat.put(req, list);
            minPriceByCat.put(req, minimumPrice(list, req, ramGb, fps));
        }
        double ramReserve = minPriceByCat.get(3);
        double monitorReserve = minPriceByCat.get(10);
        if (!Double.isFinite(ramReserve)) throw new IllegalArgumentException("Không còn RAM " + ramGb + " GB trong kho.");
        if (!Double.isFinite(monitorReserve)) throw new IllegalArgumentException("Không có màn hình phù hợp từ " + fps + " Hz đang còn hàng.");
        double totalMinRequired = 0;
        for (int req : required) {
            double mp = minPriceByCat.get(req);
            if (!Double.isFinite(mp)) {
                throw new IllegalArgumentException("Kho hiện tại chưa đủ linh kiện còn hàng cho nhóm bắt buộc: " + labels[indexOfRequired(required, req)] + ".");
            }
            totalMinRequired += mp;
        }
        if (totalMinRequired > budget) {
            throw new IllegalArgumentException("Ngân sách hiện tại chưa đủ để ghép đủ 7/7 linh kiện bắt buộc (tối thiểu cần khoảng " + formatVnd(totalMinRequired) + ").");
        }
        return assembleBuild(budget, weights, priority, ramGb, fps, required, labels, candidatesByCat, minPriceByCat);
    }

    private List<Parts> assembleBuild(double budget, double[] weights, String priority, int ramGb, int fps,
                                      int[] required, String[] labels,
                                      Map<Integer, List<Parts>> candidatesByCat,
                                      Map<Integer, Double> minPriceByCat) {
        List<Parts> picked = new ArrayList<>();
        Map<Integer, Parts> byCategory = new HashMap<>();
        double used = 0;
        double carryOver = 0;
        for (int index=0; index<CATEGORIES.length; index++) {
            int cat = CATEGORIES[index];
            List<Parts> candidates = candidatesByCat.getOrDefault(cat, catalog.findAvailable(cat, "", 5000));
            double baseCap = budget * weights[index];
            double cap = (cat == 3) ? budget : (baseCap + carryOver);
            double reservedForLaterRequired = sumRemainingMinRequired(required, minPriceByCat, byCategory, cat);
            double availableForPart = budget - used - reservedForLaterRequired;
            Parts choice = null;
            for (Parts p : candidates) {
                double price = price(p);
                if (cat == 3 && ramSize(p.getPartName()) != ramGb) continue;
                if (cat == 10 && monitorHz(p.getPartName()) < fps) continue;
                if (!fitsCompatibility(cat, p, byCategory)) continue;
                if (price <= Math.min(cap, availableForPart) && betterChoice(cat, p, choice, budget)) choice=p;
            }
            // Keep the selected RAM capacity and requested display refresh rate even in fallback.
            if (choice == null) for (Parts p : candidates) {
                double price = price(p);
                if (cat == 3 && ramSize(p.getPartName()) != ramGb) continue;
                if (cat == 10 && monitorHz(p.getPartName()) < fps) continue;
                if (!fitsCompatibility(cat, p, byCategory)) continue;
                if (price <= availableForPart && betterChoice(cat, p, choice, budget)) choice=p;
            }
            // Nếu ngân sách còn dư sau khi đã thử lọc chặt, nới điều kiện tương thích nhẹ cho các linh kiện chưa ghép được
            if (choice == null) for (Parts p : candidates) {
                double price = price(p);
                if (cat == 3 && ramSize(p.getPartName()) != ramGb) continue;
                if (cat == 10 && monitorHz(p.getPartName()) < fps) continue;
                if (price <= availableForPart && betterChoice(cat, p, choice, budget)) choice=p;
            }
            if (choice != null && price(choice) <= budget-used) {
                double chosenPrice = price(choice);
                picked.add(choice);
                byCategory.put(cat, choice);
                used += chosenPrice;
                if (cat != 3) {
                    carryOver = Math.max(0, cap - chosenPrice);
                }
            } else if (cat != 3) {
                carryOver += baseCap;
            }
        }
        used = ensureAllRequiredPicked(picked, byCategory, required, candidatesByCat, budget, used, ramGb, fps);
        upgradeWithRemainingBudget(picked, byCategory, required, candidatesByCat, budget, used, priority, ramGb, fps);
        used = ensureAllRequiredPicked(picked, byCategory, required, candidatesByCat, budget, totalPickedPrice(picked), ramGb, fps);
        fitPsuToSelectedLoad(picked, byCategory, candidatesByCat, budget);
        logBuildDiagnostic(budget, used, byCategory, required, labels);
        if (picked.isEmpty()) throw new IllegalStateException("Không tìm thấy linh kiện còn hàng phù hợp ngân sách này.");
        List<String> missing = new ArrayList<>();
        for (int i = 0; i < required.length; i++) if (!byCategory.containsKey(required[i])) missing.add(labels[i]);
        if (!missing.isEmpty()) throw new IllegalArgumentException("Ngân sách hoặc tồn kho hiện tại chưa đủ để tạo build đầy đủ. Chưa chọn được: " + String.join(", ", missing) + ". Hãy thử mức ngân sách cao hơn.");
        return picked;
    }

    private void fitPsuToSelectedLoad(List<Parts> picked, Map<Integer, Parts> byCategory,
                                      Map<Integer, List<Parts>> candidatesByCat, double budget) {
        Parts cpu = byCategory.get(1);
        Parts vga = byCategory.get(4);
        Parts currentPsu = byCategory.get(7);
        if (currentPsu == null || (cpu == null && vga == null)) return;
        if (fitsCompatibility(7, currentPsu, byCategory)) return;

        double otherCost = totalPickedPrice(picked) - price(currentPsu);
        List<Parts> psuList = candidatesByCat.getOrDefault(7, catalog.findAvailable(7, "", 5000));
        for (Parts candidate : psuList) {
            if (otherCost + price(candidate) > budget) continue;
            if (fitsCompatibility(7, candidate, byCategory)) {
                byCategory.put(7, candidate);
                for (int i = 0; i < picked.size(); i++) {
                    if (picked.get(i).getPartId() == currentPsu.getPartId()) {
                        picked.set(i, candidate);
                        return;
                    }
                }
                picked.add(candidate);
                return;
            }
        }
    }

    private void logBuildDiagnostic(double budget, double used, Map<Integer, Parts> byCategory,
                                  int[] required, String[] labels) {
        List<String> missing = new ArrayList<>();
        for (int i = 0; i < required.length; i++) {
            if (!byCategory.containsKey(required[i])) {
                missing.add(labels[i] + "(cat=" + required[i] + ")");
            }
        }
        StringBuilder pickedSummary = new StringBuilder();
        for (Map.Entry<Integer, Parts> entry : byCategory.entrySet()) {
            if (pickedSummary.length() > 0) pickedSummary.append(", ");
            pickedSummary.append("cat").append(entry.getKey())
                         .append("=").append(entry.getValue().getPartName())
                         .append("(").append(formatVnd(price(entry.getValue()))).append(")");
        }
        System.out.println("[RecommendPCService] budget=" + formatVnd(budget)
                + ", totalPicked=" + formatVnd(totalPickedPrice(new ArrayList<>(byCategory.values())))
                + ", used=" + formatVnd(used)
                + ", pickedCount=" + byCategory.size()
                + ", missingRequired=" + (missing.isEmpty() ? "NONE" : String.join("; ", missing))
                + ", parts=[" + pickedSummary + "]");
    }

    private int indexOfRequired(int[] required, int cat) {
        for (int i = 0; i < required.length; i++) if (required[i] == cat) return i;
        return 0;
    }

    private double sumRemainingMinRequired(int[] required, Map<Integer, Double> minPriceByCat,
                                           Map<Integer, Parts> byCategory, int currentCat) {
        double sum = 0;
        for (int req : required) {
            if (req == currentCat || byCategory.containsKey(req)) continue;
            double mp = minPriceByCat.getOrDefault(req, 0.0);
            if (Double.isFinite(mp)) sum += mp;
        }
        return sum;
    }

    private double totalPickedPrice(List<Parts> picked) {
        double sum = 0;
        for (Parts p : picked) sum += price(p);
        return sum;
    }

    private double ensureAllRequiredPicked(List<Parts> picked, Map<Integer, Parts> byCategory,
                                          int[] required, Map<Integer, List<Parts>> candidatesByCat,
                                          double budget, double used, int ramGb, int fps) {
        double currentUsed = used;
        for (int req : required) {
            if (byCategory.containsKey(req)) continue;
            double rem = budget - currentUsed;
            List<Parts> candidates = candidatesByCat.getOrDefault(req, catalog.findAvailable(req, "", 5000));
            Parts best = null;
            for (Parts p : candidates) {
                double pPrice = price(p);
                if (pPrice > rem) continue;
                if (req == 3 && ramSize(p.getPartName()) != ramGb) continue;
                if (req == 10 && monitorHz(p.getPartName()) < fps) continue;
                if (!fitsCompatibility(req, p, byCategory)) continue;
                if (best == null || score(p) > score(best) || (score(p) == score(best) && pPrice < price(best))) {
                    best = p;
                }
            }
            if (best == null) {
                for (Parts p : candidates) {
                    double pPrice = price(p);
                    if (pPrice > rem) continue;
                    if (req == 3 && ramSize(p.getPartName()) != ramGb) continue;
                    if (req == 10 && monitorHz(p.getPartName()) < fps) continue;
                    if (best == null || pPrice < price(best)) {
                        best = p;
                    }
                }
            }
            if (best != null) {
                picked.add(best);
                byCategory.put(req, best);
                currentUsed += price(best);
            }
        }
        return currentUsed;
    }

    private String formatVnd(double v) {
        return java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN")).format(Math.round(v)) + " ₫";
    }

    private int remainingRequiredCount(int currentIndex, Map<Integer, Parts> byCategory) {
        int[] required = {1, 2, 3, 4, 5, 7, 10};
        int count = 0;
        for (int req : required) {
            if (byCategory.containsKey(req)) continue;
            for (int i = currentIndex; i < CATEGORIES.length; i++) {
                if (CATEGORIES[i] == req) { count++; break; }
            }
        }
        return count;
    }

    private Parts minimumCandidate(List<Parts> parts, int category, int ramGb, int fps) {
        Parts best = null;
        for (Parts p : parts) {
            if (category == 3 && ramSize(p.getPartName()) != ramGb) continue;
            if (category == 10 && monitorHz(p.getPartName()) < fps) continue;
            if (best == null || price(p) < price(best)) best = p;
        }
        return best;
    }

    private double[] resolveWeights(double budget, String purpose, String priority, String game) {
        double[] weights = "office".equals(purpose)
                ? new double[]{.14, .16, .09, .02, .11, .06, .12, .04, .16}
                : new double[]{.12, .20, .09, .28, .07, .035, .11, .035, .06};
        BudgetTierRules tier = budgetTierService.findTier(BigDecimal.valueOf(budget));
        if (tier != null && !"office".equals(purpose)) {
            double cpuW = tier.getCpuPercent() / 100.0;
            double gpuW = tier.getGpuPercent() / 100.0;
            double ramW = tier.getRamPercent() / 100.0;
            double stoW = tier.getStoragePercent() / 100.0;
            double othW = tier.getOtherPercent() / 100.0;
            weights = new double[]{
                ramW,
                cpuW,
                othW * 0.45,
                gpuW,
                stoW * 0.75,
                stoW * 0.25,
                othW * 0.25,
                othW * 0.30,
                0.06
            };
        }
        if ("cs2".equals(game)) { weights[1] += .06; weights[3] -= .06; }
        if ("valorant".equals(game) || "lol".equals(game)) { weights[1] += .03; weights[3] -= .03; }
        if ("cpu".equals(priority)) { weights[1] += .04; weights[3] -= .04; }
        if ("gpu".equals(priority)) { weights[3] += .05; weights[1] -= .05; }
        if ("cost".equals(priority)) { weights[1] -= .02; weights[3] -= .04; weights[5] += .02; weights[7] += .02; }
        return weights;
    }

    public double price(Parts p) { return p.getSalePrice()!=null ? p.getSalePrice() : p.getBasePrice(); }
    private boolean betterChoice(int category, Parts candidate, Parts current, double budget) {
        if (current == null) return true;
        // Preserve more budget for core components while honoring exact RAM/Hz requirements.
        if (category == 3 || category == 10) return price(candidate) < price(current);
        int candidateScore = score(candidate);
        int currentScore = score(current);
        if (candidateScore != currentScore) return candidateScore > currentScore;
        // Khi kho chưa có điểm hiệu năng phân biệt (score bằng nhau), nếu ngân sách đủ lớn (>= 22 triệu)
        // thì ưu tiên linh kiện tầm cao hơn trong khung cap thay vì dồn về linh kiện rẻ nhất.
        if (budget >= 22_000_000) return price(candidate) > price(current);
        return price(candidate) < price(current);
    }

    private void upgradeWithRemainingBudget(List<Parts> picked, Map<Integer, Parts> byCategory,
                                            int[] required, Map<Integer, List<Parts>> candidatesByCat,
                                            double budget, double used, String priority, int ramGb, int fps) {
        if ("cost".equals(priority)) return;
        double remaining = budget - used;
        if (remaining < 1_000_000) return;

        // Ưu tiên nâng cấp các linh kiện cốt lõi (VGA -> CPU -> Mainboard -> SSD -> Màn hình) bằng ngân sách còn dư
        // nhưng luôn chừa tối thiểu đủ tiền cho các nhóm bắt buộc chưa có
        int[] upgradeOrder = "cpu".equals(priority)
                ? new int[]{1, 4, 2, 5, 10}
                : new int[]{4, 1, 2, 5, 10};

        for (int cat : upgradeOrder) {
            Parts current = byCategory.get(cat);
            if (current == null) continue;
            double minNeededForOtherMissing = 0;
            for (int req : required) {
                if (req == cat || byCategory.containsKey(req)) continue;
                double mp = minimumPrice(candidatesByCat.getOrDefault(req, catalog.findAvailable(req, "", 5000)), req, ramGb, fps);
                if (Double.isFinite(mp)) minNeededForOtherMissing += mp;
            }
            double usableRemaining = remaining - minNeededForOtherMissing;
            if (usableRemaining < 500_000) continue;
            double currentPrice = price(current);
            double maxAllowed = currentPrice + usableRemaining;
            List<Parts> candidates = candidatesByCat.getOrDefault(cat, catalog.findAvailable(cat, "", 5000));
            Parts upgraded = current;

            for (Parts p : candidates) {
                double pPrice = price(p);
                if (pPrice <= currentPrice || pPrice > maxAllowed) continue;
                if (cat == 3 && ramSize(p.getPartName()) != ramGb) continue;
                if (cat == 10 && monitorHz(p.getPartName()) < fps) continue;

                Map<Integer, Parts> trial = new HashMap<>(byCategory);
                trial.remove(cat);
                if (!fitsCompatibility(cat, p, trial)) continue;
                trial.put(cat, p);
                // Đảm bảo PSU đã chọn vẫn đủ công suất nếu nâng cấp CPU hoặc VGA
                if ((cat == 1 || cat == 4) && trial.containsKey(7)
                        && !fitsCompatibility(7, trial.get(7), trial)) {
                    continue;
                }

                int curScore = score(upgraded);
                int candScore = score(p);
                if (candScore > curScore || (candScore == curScore && pPrice > price(upgraded))) {
                    upgraded = p;
                }
            }

            if (upgraded != current) {
                double delta = price(upgraded) - currentPrice;
                remaining -= delta;
                byCategory.put(cat, upgraded);
                for (int i = 0; i < picked.size(); i++) {
                    if (picked.get(i).getPartId() == current.getPartId()) {
                        picked.set(i, upgraded);
                        break;
                    }
                }
            }
        }
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
