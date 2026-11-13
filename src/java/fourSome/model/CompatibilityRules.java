package model;


public class CompatibilityRules {

    private Integer ruleId;
    private String ruleName;
    private Integer primaryCategoryId;
    private Integer targetCategoryId;
    private String checkType;
    private String errorMessage;
    private String severity;
    private String status;

    public CompatibilityRules() {}

    public Integer getRuleId() { return ruleId; }
    public void setRuleId(Integer ruleId) { this.ruleId = ruleId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Integer getPrimaryCategoryId() { return primaryCategoryId; }
    public void setPrimaryCategoryId(Integer primaryCategoryId) { this.primaryCategoryId = primaryCategoryId; }

    public Integer getTargetCategoryId() { return targetCategoryId; }
    public void setTargetCategoryId(Integer targetCategoryId) { this.targetCategoryId = targetCategoryId; }

    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
