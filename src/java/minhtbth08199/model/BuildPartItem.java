package minhtbth08199.model;

/** Một linh kiện đã được chọn, kèm thông tin đủ để hiển thị trong trang Build PC. */
public class BuildPartItem {
    private int partId;
    private int categoryId;
    private String partName;
    private int quantity;
    private double unitPrice;

    public int getPartId() { return partId; }
    public void setPartId(int partId) { this.partId = partId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}
