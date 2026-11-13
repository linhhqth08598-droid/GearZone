package fourSome.model;

import java.util.Date;

public class EWarranties {

    private Integer warrantyId;
    private String warrantyCode;
    private String serialNumber;
    private Integer partId;
    private Integer orderId;
    private Date startDate;
    private Date endDate;
    private String status;
    private String documentUrl;

    public EWarranties() {}

    public Integer getWarrantyId() { return warrantyId; }
    public void setWarrantyId(Integer warrantyId) { this.warrantyId = warrantyId; }

    public String getWarrantyCode() { return warrantyCode; }
    public void setWarrantyCode(String warrantyCode) { this.warrantyCode = warrantyCode; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }

}
