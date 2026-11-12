package Models;

import java.util.Date;

public class ProductReturns {

    private Integer returnId;
    private Integer orderId;
    private Integer partId;
    private Integer quantity;
    private String serialNumber;
    private String reason;
    private Double refundAmount;
    private String refundMethod;
    private String refundTransactionCode;
    private Date refundedAt;
    private String status;
    private String rejectionReason;
    private Date receivedAt;
    private String returnedCondition;
    private Integer processedBy;
    private Date createdAt;

    public ProductReturns() {
    }

    public ProductReturns(Integer returnId, Integer orderId, Integer partId, Integer quantity, String serialNumber, String reason, Double refundAmount, String refundMethod, String refundTransactionCode, Date refundedAt, String status, String rejectionReason, Date receivedAt, String returnedCondition, Integer processedBy, Date createdAt) {
        this.returnId = returnId;
        this.orderId = orderId;
        this.partId = partId;
        this.quantity = quantity;
        this.serialNumber = serialNumber;
        this.reason = reason;
        this.refundAmount = refundAmount;
        this.refundMethod = refundMethod;
        this.refundTransactionCode = refundTransactionCode;
        this.refundedAt = refundedAt;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.receivedAt = receivedAt;
        this.returnedCondition = returnedCondition;
        this.processedBy = processedBy;
        this.createdAt = createdAt;
    }

    public Integer getReturnId() {
        return returnId;
    }

    public void setReturnId(Integer returnId) {
        this.returnId = returnId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundMethod() {
        return refundMethod;
    }

    public void setRefundMethod(String refundMethod) {
        this.refundMethod = refundMethod;
    }

    public String getRefundTransactionCode() {
        return refundTransactionCode;
    }

    public void setRefundTransactionCode(String refundTransactionCode) {
        this.refundTransactionCode = refundTransactionCode;
    }

    public Date getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(Date refundedAt) {
        this.refundedAt = refundedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getReturnedCondition() {
        return returnedCondition;
    }

    public void setReturnedCondition(String returnedCondition) {
        this.returnedCondition = returnedCondition;
    }

    public Integer getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Integer processedBy) {
        this.processedBy = processedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
