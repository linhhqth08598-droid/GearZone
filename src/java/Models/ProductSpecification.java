package Models;

public class ProductSpecification {
    private int productId;
    private int specId;
    private String specName; // Thuộc tính gộp từ bảng Specifications
    private String specValue;

    public ProductSpecification() {}

    public ProductSpecification(int productId, int specId, String specName, String specValue) {
        this.productId = productId;
        this.specId = specId;
        this.specName = specName;
        this.specValue = specValue;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getSpecId() { return specId; }
    public void setSpecId(int specId) { this.specId = specId; }

    public String getSpecName() { return specName; }
    public void setSpecName(String specName) { this.specName = specName; }

    public String getSpecValue() { return specValue; }
    public void setSpecValue(String specValue) { this.specValue = specValue; }
}