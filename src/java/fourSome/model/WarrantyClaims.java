package fourSome.model;

import java.util.Date;

public class WarrantyClaims {

    private Integer claimId;
    private Integer warrantyId;
    private Integer userId;
    private String faultDescription;
    private String status;
    private String rejectionReason;
    private String receivedCondition;
    private String serviceCenterName;
    private Integer handledBy;
    private String resolutionNote;
    private String replacementSerialNumber;
    private Date completedAt;
    private Date createdAt;

    public WarrantyClaims() {}

    public Integer getClaimId() { return claimId; }
    public void setClaimId(Integer claimId) { this.claimId = claimId; }

    public Integer getWarrantyId() { return warrantyId; }
    public void setWarrantyId(Integer warrantyId) { this.warrantyId = warrantyId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFaultDescription() { return faultDescription; }
    public void setFaultDescription(String faultDescription) { this.faultDescription = faultDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getReceivedCondition() { return receivedCondition; }
    public void setReceivedCondition(String receivedCondition) { this.receivedCondition = receivedCondition; }

    public String getServiceCenterName() { return serviceCenterName; }
    public void setServiceCenterName(String serviceCenterName) { this.serviceCenterName = serviceCenterName; }

    public Integer getHandledBy() { return handledBy; }
    public void setHandledBy(Integer handledBy) { this.handledBy = handledBy; }

    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }

    public String getReplacementSerialNumber() { return replacementSerialNumber; }
    public void setReplacementSerialNumber(String replacementSerialNumber) { this.replacementSerialNumber = replacementSerialNumber; }

    public Date getCompletedAt() { return completedAt; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

}
