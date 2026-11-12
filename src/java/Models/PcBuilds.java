package Models;

import java.util.Date;

public class PcBuilds {

    private Integer buildId;
    private Integer userId;
    private String buildName;
    private String shareToken;
    private Integer profileId;
    private Integer requirementId;
    private Double estimatedBudget;
    private Double totalPrice;
    private Integer calculatedWatts;
    private Boolean isCompatible;
    private Boolean isPresetRecommendation;
    private String recommendationType;
    private String status;
    private Integer copiedFromBuildId;
    private Integer performanceScore;
    private Integer reviewRating;
    private String reviewComment;
    private Date createdAt;

    public PcBuilds() {
    }

    public PcBuilds(Integer buildId, Integer userId, String buildName, String shareToken, Integer profileId, Integer requirementId, Double estimatedBudget, Double totalPrice, Integer calculatedWatts, Boolean isCompatible, Boolean isPresetRecommendation, String recommendationType, String status, Integer copiedFromBuildId, Integer performanceScore, Integer reviewRating, String reviewComment, Date createdAt) {
        this.buildId = buildId;
        this.userId = userId;
        this.buildName = buildName;
        this.shareToken = shareToken;
        this.profileId = profileId;
        this.requirementId = requirementId;
        this.estimatedBudget = estimatedBudget;
        this.totalPrice = totalPrice;
        this.calculatedWatts = calculatedWatts;
        this.isCompatible = isCompatible;
        this.isPresetRecommendation = isPresetRecommendation;
        this.recommendationType = recommendationType;
        this.status = status;
        this.copiedFromBuildId = copiedFromBuildId;
        this.performanceScore = performanceScore;
        this.reviewRating = reviewRating;
        this.reviewComment = reviewComment;
        this.createdAt = createdAt;
    }

    public Integer getBuildId() {
        return buildId;
    }

    public void setBuildId(Integer buildId) {
        this.buildId = buildId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public String getShareToken() {
        return shareToken;
    }

    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public Integer getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Integer requirementId) {
        this.requirementId = requirementId;
    }

    public Double getEstimatedBudget() {
        return estimatedBudget;
    }

    public void setEstimatedBudget(Double estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getCalculatedWatts() {
        return calculatedWatts;
    }

    public void setCalculatedWatts(Integer calculatedWatts) {
        this.calculatedWatts = calculatedWatts;
    }

    public Boolean getIsCompatible() {
        return isCompatible;
    }

    public void setIsCompatible(Boolean isCompatible) {
        this.isCompatible = isCompatible;
    }

    public Boolean getIsPresetRecommendation() {
        return isPresetRecommendation;
    }

    public void setIsPresetRecommendation(Boolean isPresetRecommendation) {
        this.isPresetRecommendation = isPresetRecommendation;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCopiedFromBuildId() {
        return copiedFromBuildId;
    }

    public void setCopiedFromBuildId(Integer copiedFromBuildId) {
        this.copiedFromBuildId = copiedFromBuildId;
    }

    public Integer getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(Integer performanceScore) {
        this.performanceScore = performanceScore;
    }

    public Integer getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Integer reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
