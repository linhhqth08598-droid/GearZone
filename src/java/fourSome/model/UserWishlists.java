package fourSome.model;

import java.util.Date;

public class UserWishlists {

    // Các trường gốc trong Database
    private Integer wishlistId;
    private Integer userId;
    private Integer partId;
    private Date createdAt;
    private String status;

    // Các trường bổ sung (JOIN từ bảng parts & part_media) để hiển thị ra giao diện
    private String partName;
    private String productCode;
    private Double salePrice;
    private String primaryImageUrl;

    public UserWishlists() {}

    public Integer getWishlistId() { return wishlistId; }
    public void setWishlistId(Integer wishlistId) { this.wishlistId = wishlistId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Getter & Setter cho các trường bổ sung
    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public Double getSalePrice() { return salePrice; }
    public void setSalePrice(Double salePrice) { this.salePrice = salePrice; }

    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
}