package linhhqth08598.service;

import fourSome.model.BudgetTierRules;
import linhhqth08598.dao.BudgetTierRulesDAO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UC 343 - Minh (RecommendPCService, UC 120-121/128) gọi để chia ngân sách theo khung đang hoạt động.
 *   BudgetTierRules tier = svc.findTier(budget);
 *   Map<String, BigDecimal> split = svc.allocate(budget);   // keys: CPU, GPU, RAM, STORAGE, OTHER
 */
public class BudgetTierService {

    private final BudgetTierRulesDAO dao = new BudgetTierRulesDAO();

    /** Khung ACTIVE chứa ngân sách (min <= budget < max); null nếu không có. */
    public BudgetTierRules findTier(BigDecimal budget) {
        if (budget == null || budget.signum() < 0) return null;
        for (BudgetTierRules t : dao.getAll()) {
            if (!"ACTIVE".equals(t.getStatus())) continue;
            boolean aboveMin = budget.compareTo(t.getMinBudget()) >= 0;
            boolean belowMax = t.getMaxBudget() == null || budget.compareTo(t.getMaxBudget()) < 0;
            if (aboveMin && belowMax) return t;
        }
        return null;
    }

    /** Số tiền dành cho từng nhóm linh kiện theo tỉ lệ của khung; null nếu không có khung phù hợp. */
    public Map<String, BigDecimal> allocate(BigDecimal budget) {
        BudgetTierRules t = findTier(budget);
        if (t == null) return null;
        Map<String, BigDecimal> m = new LinkedHashMap<>();
        m.put("CPU", part(budget, t.getCpuPercent()));
        m.put("GPU", part(budget, t.getGpuPercent()));
        m.put("RAM", part(budget, t.getRamPercent()));
        m.put("STORAGE", part(budget, t.getStoragePercent()));
        m.put("OTHER", part(budget, t.getOtherPercent()));
        return m;
    }

    private BigDecimal part(BigDecimal budget, int percent) {
        return budget.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
    }
}
