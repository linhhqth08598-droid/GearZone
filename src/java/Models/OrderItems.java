package Models;


public class OrderItems {

    private Integer orderItemId;
    private Integer orderId;
    private Integer partId;
    private String serialNumber;
    private Integer quantity;
    private Double unitPrice;
    private Double costPrice;
    private String status;

    public OrderItems() {
    }

    public OrderItems(Integer orderItemId, Integer orderId, Integer partId, String serialNumber, Integer quantity, Double unitPrice, Double costPrice, String status) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.partId = partId;
        this.serialNumber = serialNumber;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.costPrice = costPrice;
        this.status = status;
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
