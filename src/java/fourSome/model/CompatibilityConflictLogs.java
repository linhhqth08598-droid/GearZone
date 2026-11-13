package fourSome.model;

import java.util.Date;

public class CompatibilityConflictLogs {

    private Integer logId;
    private Integer userId;
    private Integer buildId;
    private Integer ruleId;
    private Integer cpuId;
    private Integer mainboardId;
    private Integer vgaId;
    private Integer psuId;
    private Integer caseId;
    private String conflictReason;
    private Date createdAt;
    private String status;

    public CompatibilityConflictLogs() {}

    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getBuildId() { return buildId; }
    public void setBuildId(Integer buildId) { this.buildId = buildId; }

    public Integer getRuleId() { return ruleId; }
    public void setRuleId(Integer ruleId) { this.ruleId = ruleId; }

    public Integer getCpuId() { return cpuId; }
    public void setCpuId(Integer cpuId) { this.cpuId = cpuId; }

    public Integer getMainboardId() { return mainboardId; }
    public void setMainboardId(Integer mainboardId) { this.mainboardId = mainboardId; }

    public Integer getVgaId() { return vgaId; }
    public void setVgaId(Integer vgaId) { this.vgaId = vgaId; }

    public Integer getPsuId() { return psuId; }
    public void setPsuId(Integer psuId) { this.psuId = psuId; }

    public Integer getCaseId() { return caseId; }
    public void setCaseId(Integer caseId) { this.caseId = caseId; }

    public String getConflictReason() { return conflictReason; }
    public void setConflictReason(String conflictReason) { this.conflictReason = conflictReason; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
