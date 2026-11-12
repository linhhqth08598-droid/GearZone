package Models;


public class UpgradeRecommendations {

    private Integer recommendationId;
    private Integer currentPcId;
    private String bottleneckComponent;
    private Integer replacePartId;
    private Integer suggestedPartId;
    private String planType;
    private Double estimatedPerformanceGainPercent;
    private Double estimatedCost;
    private Boolean isSelected;
    private Integer afterBuildId;
    private String status;

    public UpgradeRecommendations() {
    }

    public UpgradeRecommendations(Integer recommendationId, Integer currentPcId, String bottleneckComponent, Integer replacePartId, Integer suggestedPartId, String planType, Double estimatedPerformanceGainPercent, Double estimatedCost, Boolean isSelected, Integer afterBuildId, String status) {
        this.recommendationId = recommendationId;
        this.currentPcId = currentPcId;
        this.bottleneckComponent = bottleneckComponent;
        this.replacePartId = replacePartId;
        this.suggestedPartId = suggestedPartId;
        this.planType = planType;
        this.estimatedPerformanceGainPercent = estimatedPerformanceGainPercent;
        this.estimatedCost = estimatedCost;
        this.isSelected = isSelected;
        this.afterBuildId = afterBuildId;
        this.status = status;
    }

    public Integer getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(Integer recommendationId) {
        this.recommendationId = recommendationId;
    }

    public Integer getCurrentPcId() {
        return currentPcId;
    }

    public void setCurrentPcId(Integer currentPcId) {
        this.currentPcId = currentPcId;
    }

    public String getBottleneckComponent() {
        return bottleneckComponent;
    }

    public void setBottleneckComponent(String bottleneckComponent) {
        this.bottleneckComponent = bottleneckComponent;
    }

    public Integer getReplacePartId() {
        return replacePartId;
    }

    public void setReplacePartId(Integer replacePartId) {
        this.replacePartId = replacePartId;
    }

    public Integer getSuggestedPartId() {
        return suggestedPartId;
    }

    public void setSuggestedPartId(Integer suggestedPartId) {
        this.suggestedPartId = suggestedPartId;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public Double getEstimatedPerformanceGainPercent() {
        return estimatedPerformanceGainPercent;
    }

    public void setEstimatedPerformanceGainPercent(Double estimatedPerformanceGainPercent) {
        this.estimatedPerformanceGainPercent = estimatedPerformanceGainPercent;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Integer getAfterBuildId() {
        return afterBuildId;
    }

    public void setAfterBuildId(Integer afterBuildId) {
        this.afterBuildId = afterBuildId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
