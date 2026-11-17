package khanhnb08894;

import fourSome.model.PurchaseOrderDetails;

public class PurchaseOrderDetailDTO extends PurchaseOrderDetails {
    
    private String productCode;
    private String partName;

    public PurchaseOrderDetailDTO() {
        super();
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
}