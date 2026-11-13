package fourSome.model;

import java.util.Date;

public class PartSerials {

    private Integer serialId;
    private Integer partId;
    private String serialNumber;
    private String status;
    private Date importedAt;

    public PartSerials() {}

    public Integer getSerialId() { return serialId; }
    public void setSerialId(Integer serialId) { this.serialId = serialId; }

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getImportedAt() { return importedAt; }
    public void setImportedAt(Date importedAt) { this.importedAt = importedAt; }

}
