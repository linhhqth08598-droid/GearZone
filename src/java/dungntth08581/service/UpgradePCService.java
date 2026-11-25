package dungntth08581.service;

import fourSome.model.Parts;
import fourSome.model.PcBuilds;
import fourSome.model.UpgradeRecommendations;
import fourSome.model.UserCurrentPcs;
import fourSome.url.DBConnect;
import dungntth08581.dao.UpgradeRecommendationsDAO;
import dungntth08581.dao.UserCurrentPcsDAO;
import khanhnb08894.service.CompatibilityService;
import fourSome.model.CompatibilityResult;
import minhtbth08199.dao.BuildPartsDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Module F (UC 171-196, tru 179): Khai bao PC hien tai, kiem tra tuong thich/nguon/luu tru,
 * phan tich nghen co chai, de xuat 3 goi (SAVING, BALANCED, HIGH_PERFORMANCE),
 * tao cau hinh sau nang cap (after_build_id) va day vao gio hang.
 */
public class UpgradePCService {

    public static class UpgradePlanOption {
        private String planType;
        private String planLabel;
        private String bottleneckComponent;
        private String bottleneckAnalysis;
        private Parts replacePart;
        private Parts suggestedPart;
        private double estimatedCost;
        private double estimatedGainPercent;
        private int currentScore;
        private int projectedScore;
        private boolean selected;
        private Integer afterBuildId;

        public String getPlanType() { return planType; }
        public void setPlanType(String planType) { this.planType = planType; }
        public String getPlanLabel() { return planLabel; }
        public void setPlanLabel(String planLabel) { this.planLabel = planLabel; }
        public String getBottleneckComponent() { return bottleneckComponent; }
        public void setBottleneckComponent(String bottleneckComponent) { this.bottleneckComponent = bottleneckComponent; }
        public String getBottleneckAnalysis() { return bottleneckAnalysis; }
        public void setBottleneckAnalysis(String bottleneckAnalysis) { this.bottleneckAnalysis = bottleneckAnalysis; }
        public Parts getReplacePart() { return replacePart; }
        public void setReplacePart(Parts replacePart) { this.replacePart = replacePart; }
        public Parts getSuggestedPart() { return suggestedPart; }
        public void setSuggestedPart(Parts suggestedPart) { this.suggestedPart = suggestedPart; }
        public double getEstimatedCost() { return estimatedCost; }
        public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }
        public double getEstimatedGainPercent() { return estimatedGainPercent; }
        public void setEstimatedGainPercent(double estimatedGainPercent) { this.estimatedGainPercent = estimatedGainPercent; }
        public int getCurrentScore() { return currentScore; }
        public void setCurrentScore(int currentScore) { this.currentScore = currentScore; }
        public int getProjectedScore() { return projectedScore; }
        public void setProjectedScore(int projectedScore) { this.projectedScore = projectedScore; }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }
        public Integer getAfterBuildId() { return afterBuildId; }
        public void setAfterBuildId(Integer afterBuildId) { this.afterBuildId = afterBuildId; }
    }

    public static class UpgradeAnalysis {
        private UserCurrentPcs currentPc;
        private Map<Integer, Parts> currentPartsByCat = new LinkedHashMap<>();
        private List<String> reusableComponents = new ArrayList<>();
        private List<String> compatibilityNotes = new ArrayList<>();
        private String bottleneckCategory;
        private String bottleneckSummary;
        private int currentTotalScore;
        private double currentPcValue;
        private List<UpgradePlanOption> plans = new ArrayList<>();

        public UserCurrentPcs getCurrentPc() { return currentPc; }
        public void setCurrentPc(UserCurrentPcs currentPc) { this.currentPc = currentPc; }
        public Map<Integer, Parts> getCurrentPartsByCat() { return currentPartsByCat; }
        public void setCurrentPartsByCat(Map<Integer, Parts> currentPartsByCat) { this.currentPartsByCat = currentPartsByCat; }
        public List<String> getReusableComponents() { return reusableComponents; }
        public void setReusableComponents(List<String> reusableComponents) { this.reusableComponents = reusableComponents; }
        public List<String> getCompatibilityNotes() { return compatibilityNotes; }
        public void setCompatibilityNotes(List<String> compatibilityNotes) { this.compatibilityNotes = compatibilityNotes; }
        public String getBottleneckCategory() { return bottleneckCategory; }
        public void setBottleneckCategory(String bottleneckCategory) { this.bottleneckCategory = bottleneckCategory; }
        public String getBottleneckSummary() { return bottleneckSummary; }
        public void setBottleneckSummary(String bottleneckSummary) { this.bottleneckSummary = bottleneckSummary; }
        public int getCurrentTotalScore() { return currentTotalScore; }
        public void setCurrentTotalScore(int currentTotalScore) { this.currentTotalScore = currentTotalScore; }
        public double getCurrentPcValue() { return currentPcValue; }
        public void setCurrentPcValue(double currentPcValue) { this.currentPcValue = currentPcValue; }
        public List<UpgradePlanOption> getPlans() { return plans; }
        public void setPlans(List<UpgradePlanOption> plans) { this.plans = plans; }
    }

    private final UserCurrentPcsDAO currentPcDAO = new UserCurrentPcsDAO();
    private final UpgradeRecommendationsDAO recDAO = new UpgradeRecommendationsDAO();
    private final BuildPartsDAO buildPartsDAO = new BuildPartsDAO();
    private final CompatibilityService compatibilityService = new CompatibilityService();
    private final CartService cartService = new CartService();

    public List<Parts> getPartsByCategory(int categoryId) {
        return buildPartsDAO.findAvailable(categoryId, "", 5000);
    }

    public List<fourSome.model.PcBuildHistoryItem> getUserBuildHistory(int userId) {
        return new linhhqth08598.dao.UserWishlistsDAO().getPcBuildHistory(userId);
    }

    public int applyBuildFromHistory(int userId, int buildId, double upgradeBudget) {
        if (buildId <= 0) {
            throw new IllegalArgumentException("Cấu hình lịch sử không hợp lệ.");
        }
        String checkSql = "SELECT status FROM pc_builds WHERE build_id = ? AND user_id = ? AND status IN ('DRAFT', 'SAVED')";
        String detailSql = "SELECT bd.part_id, bd.quantity, p.category_id, c.category_code, c.category_name "
                + "FROM pc_build_details bd "
                + "JOIN parts p ON p.part_id = bd.part_id "
                + "LEFT JOIN categories c ON c.category_id = p.category_id "
                + "WHERE bd.build_id = ? AND bd.status = 'ACTIVE' ORDER BY bd.build_detail_id ASC";
        try (Connection con = DBConnect.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, buildId);
                ps.setInt(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Không tìm thấy cấu hình #" + buildId + " trong lịch sử của bạn.");
                    }
                }
            }

            UserCurrentPcs pc = new UserCurrentPcs();
            pc.setUserId(userId);
            pc.setCurrentRamQuantity(1);
            pc.setUpgradeBudget(upgradeBudget >= 0 ? upgradeBudget : 0.0);

            try (PreparedStatement ps = con.prepareStatement(detailSql)) {
                ps.setInt(1, buildId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int partId = rs.getInt("part_id");
                        int qty = Math.max(1, rs.getInt("quantity"));
                        int catId = rs.getInt("category_id");
                        String code = rs.getString("category_code");
                        String name = rs.getString("category_name");
                        String slot = detectUpgradeSlot(catId, code, name);
                        if (slot == null) continue;
                        switch (slot) {
                            case "cpu":
                                if (pc.getCurrentCpuId() == null) pc.setCurrentCpuId(partId);
                                break;
                            case "main":
                                if (pc.getCurrentMainboardId() == null) pc.setCurrentMainboardId(partId);
                                break;
                            case "ram":
                                if (pc.getCurrentRamId() == null) {
                                    pc.setCurrentRamId(partId);
                                    pc.setCurrentRamQuantity(Math.min(qty, 32));
                                }
                                break;
                            case "vga":
                                if (pc.getCurrentVgaId() == null) pc.setCurrentVgaId(partId);
                                break;
                            case "ssd":
                                if (pc.getCurrentStorageId() == null) pc.setCurrentStorageId(partId);
                                break;
                            case "psu":
                                if (pc.getCurrentPsuId() == null) pc.setCurrentPsuId(partId);
                                break;
                            case "case":
                                if (pc.getCurrentCaseId() == null) pc.setCurrentCaseId(partId);
                                break;
                            case "cooler":
                                if (pc.getCurrentCoolerId() == null) pc.setCurrentCoolerId(partId);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            return saveCurrentPc(pc);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Không thể nạp cấu hình từ lịch sử build.");
        }
    }

    private String detectUpgradeSlot(int catId, String code, String name) {
        if (catId == 1) return "cpu";
        if (catId == 2) return "main";
        if (catId == 3) return "ram";
        if (catId == 4) return "vga";
        if (catId == 5) return "ssd";
        if (catId == 7) return "psu";
        if (catId == 8) return "case";
        if (catId == 9) return "cooler";
        String t = ((code == null ? "" : code) + " " + (name == null ? "" : name)).toUpperCase();
        if (t.contains("CPU") || t.contains("VI XỬ LÝ")) return "cpu";
        if (t.contains("MAIN") || t.contains("MẠCH CHỦ")) return "main";
        if (t.contains("RAM")) return "ram";
        if (t.contains("VGA") || t.contains("GPU") || t.contains("ĐỒ HỌA")) return "vga";
        if (t.contains("SSD") || t.contains("HDD") || t.contains("Ổ CỨNG") || t.contains("STORAGE")) return "ssd";
        if (t.contains("PSU") || t.contains("NGUỒN") || t.contains("POWER")) return "psu";
        if (t.contains("CASE") || t.contains("VỎ")) return "case";
        if (t.contains("COOL") || t.contains("TẢN")) return "cooler";
        return null;
    }

    public UserCurrentPcs getActiveCurrentPc(int userId) {
        return currentPcDAO.findActiveByUserId(userId);
    }

    public int saveCurrentPc(UserCurrentPcs pc) {
        int newId = currentPcDAO.saveCurrentPc(pc);
        if (newId > 0) {
            pc.setCurrentPcId(newId);
            generateAndSavePlans(pc);
        }
        return newId;
    }

    public UpgradeAnalysis analyze(int userId) {
        UserCurrentPcs pc = currentPcDAO.findActiveByUserId(userId);
        if (pc == null) return null;
        return analyzePc(pc);
    }

    public UpgradeAnalysis analyzePc(UserCurrentPcs pc) {
        UpgradeAnalysis analysis = new UpgradeAnalysis();
        analysis.setCurrentPc(pc);

        Map<Integer, Parts> map = new LinkedHashMap<>();
        loadPartIntoMap(map, 1, pc.getCurrentCpuId());
        loadPartIntoMap(map, 2, pc.getCurrentMainboardId());
        loadPartIntoMap(map, 3, pc.getCurrentRamId());
        loadPartIntoMap(map, 4, pc.getCurrentVgaId());
        loadPartIntoMap(map, 5, pc.getCurrentStorageId());
        loadPartIntoMap(map, 7, pc.getCurrentPsuId());
        loadPartIntoMap(map, 8, pc.getCurrentCaseId());
        loadPartIntoMap(map, 9, pc.getCurrentCoolerId());
        analysis.setCurrentPartsByCat(map);

        double totalVal = 0;
        int totalScore = 0;
        for (Map.Entry<Integer, Parts> e : map.entrySet()) {
            Parts p = e.getValue();
            totalVal += effectivePrice(p) * (e.getKey() == 3 ? Math.max(1, pc.getCurrentRamQuantity() != null ? pc.getCurrentRamQuantity() : 1) : 1);
            totalScore += scoreOf(p);
            analysis.getReusableComponents().add(categoryLabel(e.getKey()) + ": " + p.getPartName());
        }
        analysis.setCurrentPcValue(totalVal);
        analysis.setCurrentTotalScore(totalScore);

        Map<String, Integer> slotMap = new HashMap<>();
        if (pc.getCurrentCpuId() != null) slotMap.put("cpu", pc.getCurrentCpuId());
        if (pc.getCurrentMainboardId() != null) slotMap.put("main", pc.getCurrentMainboardId());
        if (pc.getCurrentRamId() != null) {
            slotMap.put("ram", pc.getCurrentRamId());
            slotMap.put("ramQty", pc.getCurrentRamQuantity() != null ? pc.getCurrentRamQuantity() : 1);
        }
        if (pc.getCurrentVgaId() != null) slotMap.put("vga", pc.getCurrentVgaId());
        if (pc.getCurrentStorageId() != null) slotMap.put("ssd", pc.getCurrentStorageId());
        if (pc.getCurrentPsuId() != null) slotMap.put("psu", pc.getCurrentPsuId());
        if (pc.getCurrentCaseId() != null) slotMap.put("case", pc.getCurrentCaseId());
        if (pc.getCurrentCoolerId() != null) slotMap.put("cooler", pc.getCurrentCoolerId());

        if (!slotMap.isEmpty()) {
            CompatibilityResult compat = compatibilityService.checkBuild(slotMap, pc.getUserId(), null);
            analysis.getCompatibilityNotes().addAll(compat.getErrors());
            analysis.getCompatibilityNotes().addAll(compat.getWarnings());
        }

        Parts cpu = map.get(1);
        Parts vga = map.get(4);
        int cpuScore = scoreOf(cpu);
        int vgaScore = scoreOf(vga);
        int bottleneckCat;
        if (cpu == null && vga != null) {
            bottleneckCat = 1;
            analysis.setBottleneckSummary("Chưa khai báo CPU hiện tại nên ưu tiên bổ sung/nâng cấp CPU.");
        } else if (vga == null && cpu != null) {
            bottleneckCat = 4;
            analysis.setBottleneckSummary("Chưa khai báo Card đồ họa (VGA) nên ưu tiên bổ sung/nâng cấp VGA.");
        } else if (vgaScore + 15 < cpuScore) {
            bottleneckCat = 4;
            analysis.setBottleneckSummary("Ngưỡng nghẽn cổ chai tại VGA: điểm GPU (" + vgaScore + ") thấp hơn đáng kể so với điểm CPU (" + cpuScore + ").");
        } else if (cpuScore + 15 < vgaScore) {
            bottleneckCat = 1;
            analysis.setBottleneckSummary("Ngưỡng nghẽn cổ chai tại CPU: điểm CPU (" + cpuScore + ") thấp hơn đáng kể so với điểm GPU (" + vgaScore + ").");
        } else {
            bottleneckCat = vgaScore <= cpuScore ? 4 : 1;
            analysis.setBottleneckSummary("CPU (" + cpuScore + ") và VGA (" + vgaScore + ") tương đối cân bằng; ưu tiên nâng cấp " + categoryLabel(bottleneckCat) + " theo ngân sách.");
        }
        analysis.setBottleneckCategory(categoryLabel(bottleneckCat));

        double budget = pc.getUpgradeBudget() != null ? pc.getUpgradeBudget() : 0.0;
        List<UpgradeRecommendations> savedRecs = recDAO.findByCurrentPcId(pc.getCurrentPcId());
        List<UpgradePlanOption> plans = buildPlanOptions(pc, map, bottleneckCat, budget, totalScore);
        for (UpgradePlanOption opt : plans) {
            for (UpgradeRecommendations r : savedRecs) {
                if (opt.getPlanType().equalsIgnoreCase(r.getPlanType())) {
                    opt.setSelected(Boolean.TRUE.equals(r.getIsSelected()));
                    opt.setAfterBuildId(r.getAfterBuildId());
                    break;
                }
            }
        }
        analysis.setPlans(plans);
        return analysis;
    }

    public List<UpgradeRecommendations> generateAndSavePlans(UserCurrentPcs pc) {
        UpgradeAnalysis analysis = analyzePc(pc);
        List<UpgradeRecommendations> rows = new ArrayList<>();
        for (UpgradePlanOption opt : analysis.getPlans()) {
            if (opt.getSuggestedPart() == null) continue;
            UpgradeRecommendations row = new UpgradeRecommendations();
            row.setCurrentPcId(pc.getCurrentPcId());
            row.setBottleneckComponent(opt.getBottleneckComponent());
            row.setReplacePartId(opt.getReplacePart() != null ? opt.getReplacePart().getPartId() : null);
            row.setSuggestedPartId(opt.getSuggestedPart().getPartId());
            row.setPlanType(opt.getPlanType());
            row.setEstimatedPerformanceGainPercent(opt.getEstimatedGainPercent());
            row.setEstimatedCost(opt.getEstimatedCost());
            row.setIsSelected(opt.isSelected());
            row.setAfterBuildId(opt.getAfterBuildId());
            rows.add(row);
        }
        recDAO.replaceRecommendations(pc.getCurrentPcId(), rows);
        return recDAO.findByCurrentPcId(pc.getCurrentPcId());
    }

    public PcBuilds applyPlanAndCreateBuild(int userId, String planType, boolean addToCart) {
        UserCurrentPcs pc = currentPcDAO.findActiveByUserId(userId);
        if (pc == null) throw new IllegalArgumentException("Chưa có cấu hình PC hiện tại để nâng cấp.");
        UpgradeAnalysis analysis = analyzePc(pc);
        UpgradePlanOption chosen = null;
        for (UpgradePlanOption opt : analysis.getPlans()) {
            if (opt.getPlanType().equalsIgnoreCase(planType)) {
                chosen = opt;
                break;
            }
        }
        if (chosen == null || chosen.getSuggestedPart() == null) {
            throw new IllegalArgumentException("Không tìm thấy phương án nâng cấp phù hợp.");
        }

        Map<Integer, Parts> nextMap = new LinkedHashMap<>(analysis.getCurrentPartsByCat());
        nextMap.put(chosen.getSuggestedPart().getCategoryId(), chosen.getSuggestedPart());

        String buildName = "Nâng cấp " + chosen.getPlanLabel() + " - " + chosen.getSuggestedPart().getPartName();
        int newBuildId = createBuildRecord(userId, pc, buildName, nextMap, analysis.getCurrentTotalScore() + (chosen.getProjectedScore() - chosen.getCurrentScore()));

        List<UpgradeRecommendations> recs = recDAO.findByCurrentPcId(pc.getCurrentPcId());
        for (UpgradeRecommendations r : recs) {
            boolean isTarget = chosen.getPlanType().equalsIgnoreCase(r.getPlanType());
            recDAO.markSelected(r.getRecommendationId(), pc.getCurrentPcId(), isTarget, isTarget ? newBuildId : null);
        }

        if (addToCart && chosen.getSuggestedPart() != null) {
            cartService.addPart(userId, chosen.getSuggestedPart().getPartId(), 1);
        }

        PcBuilds created = new PcBuilds();
        created.setBuildId(newBuildId);
        created.setUserId(userId);
        created.setBuildName(buildName);
        created.setTotalPrice(calculateTotal(nextMap));
        created.setStatus("SAVED");
        return created;
    }

    private List<UpgradePlanOption> buildPlanOptions(UserCurrentPcs pc, Map<Integer, Parts> currentMap,
                                                   int bottleneckCat, double budget, int currentTotalScore) {
        List<UpgradePlanOption> result = new ArrayList<>();
        Parts currentTarget = currentMap.get(bottleneckCat);
        int baseTargetScore = scoreOf(currentTarget);
        double baseTargetPrice = effectivePrice(currentTarget);
        List<Parts> candidates = buildPartsDAO.findAvailable(bottleneckCat, "", 5000);

        List<Parts> higherOptions = new ArrayList<>();
        for (Parts p : candidates) {
            if (currentTarget != null && p.getPartId().equals(currentTarget.getPartId())) continue;
            if (scoreOf(p) >= baseTargetScore || effectivePrice(p) > baseTargetPrice) {
                higherOptions.add(p);
            }
        }
        higherOptions.sort(Comparator.comparingInt(this::scoreOf).thenComparingDouble(this::effectivePrice));

        Parts savingPick = null;
        Parts balancedPick = null;
        Parts highPick = null;

        for (Parts p : higherOptions) {
            double cost = effectivePrice(p);
            if (savingPick == null && (budget <= 0 || cost <= budget * 0.75)) {
                savingPick = p;
            }
            if (balancedPick == null && (budget <= 0 || cost <= budget)) {
                balancedPick = p;
            }
            if (budget <= 0 || cost <= budget * 1.2) {
                highPick = p;
            }
        }
        if (balancedPick == null && !higherOptions.isEmpty()) {
            balancedPick = higherOptions.get(0);
        }
        if (savingPick == null) savingPick = balancedPick;
        if (highPick == null) highPick = balancedPick;

        result.add(toOption("SAVING", "Tiết kiệm", bottleneckCat, currentTarget, savingPick, currentTotalScore, analysisBottleneck(bottleneckCat)));
        result.add(toOption("BALANCED", "Cân bằng hiệu năng", bottleneckCat, currentTarget, balancedPick, currentTotalScore, analysisBottleneck(bottleneckCat)));
        result.add(toOption("HIGH_PERFORMANCE", "Hiệu năng cao", bottleneckCat, currentTarget, highPick, currentTotalScore, analysisBottleneck(bottleneckCat)));
        return result;
    }

    private UpgradePlanOption toOption(String type, String label, int bottleneckCat, Parts currentTarget,
                                       Parts suggested, int currentTotalScore, String note) {
        UpgradePlanOption opt = new UpgradePlanOption();
        opt.setPlanType(type);
        opt.setPlanLabel(label);
        opt.setBottleneckComponent(categoryLabel(bottleneckCat));
        opt.setBottleneckAnalysis(note);
        opt.setReplacePart(currentTarget);
        opt.setSuggestedPart(suggested);
        opt.setCurrentScore(currentTotalScore);
        if (suggested != null) {
            int oldS = scoreOf(currentTarget);
            int newS = Math.max(oldS, scoreOf(suggested));
            int deltaScore = Math.max(0, newS - oldS);
            opt.setProjectedScore(currentTotalScore + deltaScore);
            opt.setEstimatedCost(effectivePrice(suggested));
            double gain = currentTotalScore > 0 ? (deltaScore * 100.0 / currentTotalScore) : (deltaScore > 0 ? 15.0 : 5.0);
            opt.setEstimatedGainPercent(Math.round(gain * 100.0) / 100.0);
        } else {
            opt.setProjectedScore(currentTotalScore);
            opt.setEstimatedCost(0.0);
            opt.setEstimatedGainPercent(0.0);
        }
        return opt;
    }

    private int createBuildRecord(int userId, UserCurrentPcs pc, String buildName, Map<Integer, Parts> partsByCat, int perfScore) {
        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        double total = calculateTotal(partsByCat);
        String insBuild = "INSERT INTO pc_builds (user_id, build_name, share_token, estimated_budget, total_price, "
                       + "calculated_watts, is_compatible, is_preset_recommendation, recommendation_type, status, performance_score, created_at) "
                       + "VALUES (?, ?, ?, ?, ?, 0, 1, 0, 'UPGRADABLE', 'SAVED', ?, GETDATE())";
        String insDetail = "INSERT INTO pc_build_details (build_id, part_id, quantity, unit_price, status) VALUES (?, ?, ?, ?, 'ACTIVE')";

        try (Connection con = DBConnect.getConnection()) {
            con.setAutoCommit(false);
            try {
                int buildId = 0;
                try (PreparedStatement ps = con.prepareStatement(insBuild, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setString(2, buildName);
                    ps.setString(3, token);
                    if (pc.getUpgradeBudget() != null && pc.getUpgradeBudget() > 0) {
                        ps.setDouble(4, pc.getUpgradeBudget());
                    } else {
                        ps.setNull(4, Types.DECIMAL);
                    }
                    ps.setDouble(5, total);
                    ps.setInt(6, perfScore);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) buildId = rs.getInt(1);
                    }
                }
                if (buildId <= 0) { con.rollback(); return 0; }
                try (PreparedStatement ps = con.prepareStatement(insDetail)) {
                    for (Map.Entry<Integer, Parts> e : partsByCat.entrySet()) {
                        Parts p = e.getValue();
                        ps.setInt(1, buildId);
                        ps.setInt(2, p.getPartId());
                        ps.setInt(3, e.getKey() == 3 && pc.getCurrentRamQuantity() != null && pc.getCurrentRamQuantity() > 0 ? pc.getCurrentRamQuantity() : 1);
                        ps.setDouble(4, effectivePrice(p));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
                return buildId;
            } catch (Exception ex) {
                con.rollback();
                ex.printStackTrace();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private void loadPartIntoMap(Map<Integer, Parts> map, int catId, Integer partId) {
        if (partId == null || partId <= 0) return;
        String sql = "SELECT * FROM parts WHERE part_id = ?";
        try (Connection con = DBConnect.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Parts p = new Parts();
                    p.setPartId(rs.getInt("part_id"));
                    p.setCategoryId(rs.getInt("category_id"));
                    p.setPartName(rs.getString("part_name"));
                    p.setBasePrice(rs.getDouble("base_price"));
                    double sale = rs.getDouble("sale_price");
                    p.setSalePrice(rs.wasNull() ? null : sale);
                    p.setGamingPerformanceScore(rs.getInt("gaming_performance_score"));
                    p.setStatus(rs.getString("status"));
                    map.put(catId, p);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private double calculateTotal(Map<Integer, Parts> map) {
        double sum = 0;
        for (Parts p : map.values()) sum += effectivePrice(p);
        return sum;
    }

    private double effectivePrice(Parts p) {
        if (p == null) return 0.0;
        return p.getSalePrice() != null ? p.getSalePrice() : (p.getBasePrice() != null ? p.getBasePrice() : 0.0);
    }

    private int scoreOf(Parts p) {
        return (p == null || p.getGamingPerformanceScore() == null) ? 0 : p.getGamingPerformanceScore();
    }

    private String categoryLabel(int catId) {
        return switch (catId) {
            case 1 -> "CPU";
            case 2 -> "Mainboard";
            case 3 -> "RAM";
            case 4 -> "Card đồ họa (VGA)";
            case 5 -> "Ổ cứng (SSD/NVMe)";
            case 7 -> "Nguồn (PSU)";
            case 8 -> "Vỏ máy (Case)";
            case 9 -> "Tản nhiệt";
            default -> "Linh kiện";
        };
    }

    private String analysisBottleneck(int catId) {
        return "Ưu tiên nâng cấp " + categoryLabel(catId) + " để cải thiện điểm hiệu năng tổng thể.";
    }
}
