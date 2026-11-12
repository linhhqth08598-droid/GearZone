package Models;

import java.util.Date;

public class PurchaseOrders {

    private Integer poId;
    private String poCode;
    private Integer supplierId;
    private Integer createdBy;
    private Double totalCost;
    private String status;
    private Date expectedDeliveryDate;
    private Date confirmedAt;
    private Date receivedAt;
    private String note;
    private Date createdAt;

    public PurchaseOrders() {
    }

    public PurchaseOrders(Integer poId, String poCode, Integer supplierId, Integer createdBy, Double totalCost, String status, Date expectedDeliveryDate, Date confirmedAt, Date receivedAt, String note, Date createdAt) {
        this.poId = poId;
        this.poCode = poCode;
        this.supplierId = supplierId;
        this.createdBy = createdBy;
        this.totalCost = totalCost;
        this.status = status;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.confirmedAt = confirmedAt;
        this.receivedAt = receivedAt;
        this.note = note;
        this.createdAt = createdAt;
    }

    public Integer getPoId() {
        return poId;
    }

    public void setPoId(Integer poId) {
        this.poId = poId;
    }

    public String getPoCode() {
        return poCode;
    }

    public void setPoCode(String poCode) {
        this.poCode = poCode;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Date getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
