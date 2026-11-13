package model;


public class PsuPowerRules {

    private Integer ruleId;
    private Integer minSystemWattage;
    private Double recommendedPsuHeadroomPercent;
    private String description;
    private String status;

    public PsuPowerRules() {}

    public Integer getRuleId() { return ruleId; }
    public void setRuleId(Integer ruleId) { this.ruleId = ruleId; }

    public Integer getMinSystemWattage() { return minSystemWattage; }
    public void setMinSystemWattage(Integer minSystemWattage) { this.minSystemWattage = minSystemWattage; }

    public Double getRecommendedPsuHeadroomPercent() { return recommendedPsuHeadroomPercent; }
    public void setRecommendedPsuHeadroomPercent(Double recommendedPsuHeadroomPercent) { this.recommendedPsuHeadroomPercent = recommendedPsuHeadroomPercent; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
