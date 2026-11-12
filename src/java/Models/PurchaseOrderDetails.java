package Models;


public class PurchaseOrderDetails {

    private Integer poDetailId;
    private Integer poId;
    private Integer partId;
    private Integer importQuantity;
    private Double importUnitPrice;
    private Integer receivedQuantity;
    private Integer missingQuantity;
    private Integer wrongItemQuantity;
    private Integer damagedQuantity;
    private String status;

    public PurchaseOrderDetails() {
    }

    public PurchaseOrderDetails(Integer poDetailId, Integer poId, Integer partId, Integer importQuantity, Double importUnitPrice, Integer receivedQuantity, Integer missingQuantity, Integer wrongItemQuantity, Integer damagedQuantity, String status) {
        this.poDetailId = poDetailId;
        this.poId = poId;
        this.partId = partId;
        this.importQuantity = importQuantity;
        this.importUnitPrice = importUnitPrice;
        this.receivedQuantity = receivedQuantity;
        this.missingQuantity = missingQuantity;
        this.wrongItemQuantity = wrongItemQuantity;
        this.damagedQuantity = damagedQuantity;
        this.status = status;
    }

    public Integer getPoDetailId() {
        return poDetailId;
    }

    public void setPoDetailId(Integer poDetailId) {
        this.poDetailId = poDetailId;
    }

    public Integer getPoId() {
        return poId;
    }

    public void setPoId(Integer poId) {
        this.poId = poId;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Integer getImportQuantity() {
        return importQuantity;
    }

    public void setImportQuantity(Integer importQuantity) {
        this.importQuantity = importQuantity;
    }

    public Double getImportUnitPrice() {
        return importUnitPrice;
    }

    public void setImportUnitPrice(Double importUnitPrice) {
        this.importUnitPrice = importUnitPrice;
    }

    public Integer getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(Integer receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public Integer getMissingQuantity() {
        return missingQuantity;
    }

    public void setMissingQuantity(Integer missingQuantity) {
        this.missingQuantity = missingQuantity;
    }

    public Integer getWrongItemQuantity() {
        return wrongItemQuantity;
    }

    public void setWrongItemQuantity(Integer wrongItemQuantity) {
        this.wrongItemQuantity = wrongItemQuantity;
    }

    public Integer getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(Integer damagedQuantity) {
        this.damagedQuantity = damagedQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
