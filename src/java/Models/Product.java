package Models;

public class Product {
    private int productId;
    private String productName;
    private double basePrice;
    private int stockQuantity; // Thêm số lượng tồn kho
    private String description;
    private String imageBase64;

    public Product() {}

    public Product(int productId, String productName, double basePrice, int stockQuantity, String description, String imageBase64) {
        this.productId = productId;
        this.productName = productName;
        this.basePrice = basePrice;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.imageBase64 = imageBase64;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}