package fourSome.model;

public class CompatibilityRules {
    private int ruleId;
    private String ruleName;
    private int primaryCategoryId;
    private int targetCategoryId;
    private String checkType;
    private String errorMessage;
    private String severity;
    private String status;

    // Getters and Setters
    public int getRuleId() { return ruleId; }
    public void setRuleId(int ruleId) { this.ruleId = ruleId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public int getPrimaryCategoryId() { return primaryCategoryId; }
    public void setPrimaryCategoryId(int primaryCategoryId) { this.primaryCategoryId = primaryCategoryId; }

    public int getTargetCategoryId() { return targetCategoryId; }
    public void setTargetCategoryId(int targetCategoryId) { this.targetCategoryId = targetCategoryId; }

    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}