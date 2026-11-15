package fourSome.model;

import java.math.BigDecimal;

/** UC 343 - Tiêu chí đề xuất cấu hình theo ngân sách (bảng budget_tier_rules). */
public class BudgetTierRules {

    private Integer tierId;
    private String tierName;
    private BigDecimal minBudget;
    private BigDecimal maxBudget; // null = không giới hạn trên
    private Integer cpuPercent;
    private Integer gpuPercent;
    private Integer ramPercent;
    private Integer storagePercent;
    private Integer otherPercent;
    private String description;
    private String status;

    public BudgetTierRules() {}

    public Integer getTierId() { return tierId; }
    public void setTierId(Integer tierId) { this.tierId = tierId; }

    public String getTierName() { return tierName; }
    public void setTierName(String tierName) { this.tierName = tierName; }

    public BigDecimal getMinBudget() { return minBudget; }
    public void setMinBudget(BigDecimal minBudget) { this.minBudget = minBudget; }

    public BigDecimal getMaxBudget() { return maxBudget; }
    public void setMaxBudget(BigDecimal maxBudget) { this.maxBudget = maxBudget; }

    public Integer getCpuPercent() { return cpuPercent; }
    public void setCpuPercent(Integer cpuPercent) { this.cpuPercent = cpuPercent; }

    public Integer getGpuPercent() { return gpuPercent; }
    public void setGpuPercent(Integer gpuPercent) { this.gpuPercent = gpuPercent; }

    public Integer getRamPercent() { return ramPercent; }
    public void setRamPercent(Integer ramPercent) { this.ramPercent = ramPercent; }

    public Integer getStoragePercent() { return storagePercent; }
    public void setStoragePercent(Integer storagePercent) { this.storagePercent = storagePercent; }

    public Integer getOtherPercent() { return otherPercent; }
    public void setOtherPercent(Integer otherPercent) { this.otherPercent = otherPercent; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
