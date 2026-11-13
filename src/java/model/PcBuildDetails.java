package model;


public class PcBuildDetails {

    private Integer buildDetailId;
    private Integer buildId;
    private Integer partId;
    private Integer quantity;
    private Double unitPrice;
    private String status;

    public PcBuildDetails() {}

    public Integer getBuildDetailId() { return buildDetailId; }
    public void setBuildDetailId(Integer buildDetailId) { this.buildDetailId = buildDetailId; }

    public Integer getBuildId() { return buildId; }
    public void setBuildId(Integer buildId) { this.buildId = buildId; }

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
