package Models;

import java.util.Date;

public class Inventory {

    private Integer inventoryId;
    private Integer partId;
    private Integer stockQuantity;
    private Integer reservedQuantity;
    private Integer damagedQuantity;
    private Integer lowStockThreshold;
    private Date updatedAt;
    private String status;

    public Inventory() {
    }

    public Inventory(Integer inventoryId, Integer partId, Integer stockQuantity, Integer reservedQuantity, Integer damagedQuantity, Integer lowStockThreshold, Date updatedAt, String status) {
        this.inventoryId = inventoryId;
        this.partId = partId;
        this.stockQuantity = stockQuantity;
        this.reservedQuantity = reservedQuantity;
        this.damagedQuantity = damagedQuantity;
        this.lowStockThreshold = lowStockThreshold;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Integer getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(Integer damagedQuantity) {
        this.damagedQuantity = damagedQuantity;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
