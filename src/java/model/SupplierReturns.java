package model;

import java.util.Date;

public class SupplierReturns {

    private Integer supplierReturnId;
    private String returnCode;
    private Integer supplierId;
    private Integer poId;
    private Integer partId;
    private Integer quantity;
    private String defectType;
    private String reason;
    private String status;
    private Integer createdBy;
    private Date createdAt;

    public SupplierReturns() {}

    public Integer getSupplierReturnId() { return supplierReturnId; }
    public void setSupplierReturnId(Integer supplierReturnId) { this.supplierReturnId = supplierReturnId; }

    public String getReturnCode() { return returnCode; }
    public void setReturnCode(String returnCode) { this.returnCode = returnCode; }

    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }

    public Integer getPoId() { return poId; }
    public void setPoId(Integer poId) { this.poId = poId; }

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDefectType() { return defectType; }
    public void setDefectType(String defectType) { this.defectType = defectType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

}
