package Models;

import java.util.Date;

public class InventoryTransactions {

    private Integer transactionId;
    private Integer partId;
    private String transactionType;
    private Integer quantityChange;
    private Integer quantityBefore;
    private Integer quantityAfter;
    private String referenceType;
    private Integer referenceId;
    private String reason;
    private Integer createdBy;
    private Date createdAt;
    private String status;

    public InventoryTransactions() {
    }

    public InventoryTransactions(Integer transactionId, Integer partId, String transactionType, Integer quantityChange, Integer quantityBefore, Integer quantityAfter, String referenceType, Integer referenceId, String reason, Integer createdBy, Date createdAt, String status) {
        this.transactionId = transactionId;
        this.partId = partId;
        this.transactionType = transactionType;
        this.quantityChange = quantityChange;
        this.quantityBefore = quantityBefore;
        this.quantityAfter = quantityAfter;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.reason = reason;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public Integer getQuantityBefore() {
        return quantityBefore;
    }

    public void setQuantityBefore(Integer quantityBefore) {
        this.quantityBefore = quantityBefore;
    }

    public Integer getQuantityAfter() {
        return quantityAfter;
    }

    public void setQuantityAfter(Integer quantityAfter) {
        this.quantityAfter = quantityAfter;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
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
