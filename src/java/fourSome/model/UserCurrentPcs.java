package fourSome.model;

import java.util.Date;

public class UserCurrentPcs {

    private Integer currentPcId;
    private Integer userId;
    private Integer currentCpuId;
    private Integer currentMainboardId;
    private Integer currentRamId;
    private Integer currentVgaId;
    private Integer currentPsuId;
    private Integer currentCaseId;
    private Integer currentStorageId;
    private Integer currentCoolerId;
    private Integer currentRamQuantity;
    private Double upgradeBudget;
    private Date createdAt;
    private String status;

    public UserCurrentPcs() {}

    public Integer getCurrentPcId() { return currentPcId; }
    public void setCurrentPcId(Integer currentPcId) { this.currentPcId = currentPcId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getCurrentCpuId() { return currentCpuId; }
    public void setCurrentCpuId(Integer currentCpuId) { this.currentCpuId = currentCpuId; }

    public Integer getCurrentMainboardId() { return currentMainboardId; }
    public void setCurrentMainboardId(Integer currentMainboardId) { this.currentMainboardId = currentMainboardId; }

    public Integer getCurrentRamId() { return currentRamId; }
    public void setCurrentRamId(Integer currentRamId) { this.currentRamId = currentRamId; }

    public Integer getCurrentVgaId() { return currentVgaId; }
    public void setCurrentVgaId(Integer currentVgaId) { this.currentVgaId = currentVgaId; }

    public Integer getCurrentPsuId() { return currentPsuId; }
    public void setCurrentPsuId(Integer currentPsuId) { this.currentPsuId = currentPsuId; }

    public Integer getCurrentCaseId() { return currentCaseId; }
    public void setCurrentCaseId(Integer currentCaseId) { this.currentCaseId = currentCaseId; }

    public Integer getCurrentStorageId() { return currentStorageId; }
    public void setCurrentStorageId(Integer currentStorageId) { this.currentStorageId = currentStorageId; }

    public Integer getCurrentCoolerId() { return currentCoolerId; }
    public void setCurrentCoolerId(Integer currentCoolerId) { this.currentCoolerId = currentCoolerId; }

    public Integer getCurrentRamQuantity() { return currentRamQuantity; }
    public void setCurrentRamQuantity(Integer currentRamQuantity) { this.currentRamQuantity = currentRamQuantity; }

    public Double getUpgradeBudget() { return upgradeBudget; }
    public void setUpgradeBudget(Double upgradeBudget) { this.upgradeBudget = upgradeBudget; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
