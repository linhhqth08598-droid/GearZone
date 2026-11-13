package fourSome.model;

import java.util.Date;

public class PriceDropNotifications {

    private Integer notificationId;
    private Integer userId;
    private Integer partId;
    private Double targetPrice;
    private Boolean isNotified;
    private Date createdAt;
    private String status;

    // Bổ sung 3 trường để hiển thị giao diện
    private String partName;
    private Double currentPrice;
    private String imageUrl;

    public PriceDropNotifications() {}

    public Integer getNotificationId() { return notificationId; }
    public void setNotificationId(Integer notificationId) { this.notificationId = notificationId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public Double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(Double targetPrice) { this.targetPrice = targetPrice; }

    public Boolean getIsNotified() { return isNotified; }
    public void setIsNotified(Boolean isNotified) { this.isNotified = isNotified; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public Double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Double currentPrice) { this.currentPrice = currentPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}