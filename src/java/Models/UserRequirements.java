package Models;

import java.util.Date;

public class UserRequirements {

    private Integer requirementId;
    private Integer userId;
    private Integer profileId;
    private String targetResolution;
    private String targetFps;
    private String graphicsPreset;
    private Double budget;
    private Integer cpuPriority;
    private Integer gpuPriority;
    private Integer desiredRamGb;
    private Integer desiredStorageGb;
    private String designStyle;
    private Boolean wantsRgb;
    private Boolean optimizeCost;
    private String analysisNote;
    private Integer analyzedBy;
    private Date createdAt;
    private String status;

    public UserRequirements() {
    }

    public UserRequirements(Integer requirementId, Integer userId, Integer profileId, String targetResolution, String targetFps, String graphicsPreset, Double budget, Integer cpuPriority, Integer gpuPriority, Integer desiredRamGb, Integer desiredStorageGb, String designStyle, Boolean wantsRgb, Boolean optimizeCost, String analysisNote, Integer analyzedBy, Date createdAt, String status) {
        this.requirementId = requirementId;
        this.userId = userId;
        this.profileId = profileId;
        this.targetResolution = targetResolution;
        this.targetFps = targetFps;
        this.graphicsPreset = graphicsPreset;
        this.budget = budget;
        this.cpuPriority = cpuPriority;
        this.gpuPriority = gpuPriority;
        this.desiredRamGb = desiredRamGb;
        this.desiredStorageGb = desiredStorageGb;
        this.designStyle = designStyle;
        this.wantsRgb = wantsRgb;
        this.optimizeCost = optimizeCost;
        this.analysisNote = analysisNote;
        this.analyzedBy = analyzedBy;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Integer requirementId) {
        this.requirementId = requirementId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public String getTargetResolution() {
        return targetResolution;
    }

    public void setTargetResolution(String targetResolution) {
        this.targetResolution = targetResolution;
    }

    public String getTargetFps() {
        return targetFps;
    }

    public void setTargetFps(String targetFps) {
        this.targetFps = targetFps;
    }

    public String getGraphicsPreset() {
        return graphicsPreset;
    }

    public void setGraphicsPreset(String graphicsPreset) {
        this.graphicsPreset = graphicsPreset;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Integer getCpuPriority() {
        return cpuPriority;
    }

    public void setCpuPriority(Integer cpuPriority) {
        this.cpuPriority = cpuPriority;
    }

    public Integer getGpuPriority() {
        return gpuPriority;
    }

    public void setGpuPriority(Integer gpuPriority) {
        this.gpuPriority = gpuPriority;
    }

    public Integer getDesiredRamGb() {
        return desiredRamGb;
    }

    public void setDesiredRamGb(Integer desiredRamGb) {
        this.desiredRamGb = desiredRamGb;
    }

    public Integer getDesiredStorageGb() {
        return desiredStorageGb;
    }

    public void setDesiredStorageGb(Integer desiredStorageGb) {
        this.desiredStorageGb = desiredStorageGb;
    }

    public String getDesignStyle() {
        return designStyle;
    }

    public void setDesignStyle(String designStyle) {
        this.designStyle = designStyle;
    }

    public Boolean getWantsRgb() {
        return wantsRgb;
    }

    public void setWantsRgb(Boolean wantsRgb) {
        this.wantsRgb = wantsRgb;
    }

    public Boolean getOptimizeCost() {
        return optimizeCost;
    }

    public void setOptimizeCost(Boolean optimizeCost) {
        this.optimizeCost = optimizeCost;
    }

    public String getAnalysisNote() {
        return analysisNote;
    }

    public void setAnalysisNote(String analysisNote) {
        this.analysisNote = analysisNote;
    }

    public Integer getAnalyzedBy() {
        return analyzedBy;
    }

    public void setAnalyzedBy(Integer analyzedBy) {
        this.analyzedBy = analyzedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
