package Models;

public class CartItem {
    private int cartItemId;
    private int cartId;
    private int productId;
    private int quantity;
    
    // Các trường bổ sung từ bảng Products để hiển thị ra giao diện
    private String productName;
    private double basePrice;
    private String imageBase64;

    public CartItem() {
    }

    // Getters và Setters
    public int getCartItemId() { return cartItemId; }
    public void setCartItemId(int cartItemId) { this.cartItemId = cartItemId; }
    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
    
    // Tính tổng tiền của sản phẩm này
    public double getTotalPrice() {
        return this.basePrice * this.quantity;
    }
}