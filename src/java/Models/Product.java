package Models;

public class Product {
    private int productId;
    private String productName;
    private double basePrice;
    private String description;
    private String imageBase64;

    // Constructors
    public Product() {}

    public Product(int productId, String productName, double basePrice, String description, String imageBase64) {
        this.productId = productId;
        this.productName = productName;
        this.basePrice = basePrice;
        this.description = description;
        this.imageBase64 = imageBase64;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}