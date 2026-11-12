package Models;

import java.util.Date;

public class PriceDropNotifications {

    private Integer notificationId;
    private Integer userId;
    private Integer partId;
    private Double targetPrice;
    private Boolean isNotified;
    private Date createdAt;
    private String status;

    public PriceDropNotifications() {
    }

    public PriceDropNotifications(Integer notificationId, Integer userId, Integer partId, Double targetPrice, Boolean isNotified, Date createdAt, String status) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.partId = partId;
        this.targetPrice = targetPrice;
        this.isNotified = isNotified;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Double getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(Double targetPrice) {
        this.targetPrice = targetPrice;
    }

    public Boolean getIsNotified() {
        return isNotified;
    }

    public void setIsNotified(Boolean isNotified) {
        this.isNotified = isNotified;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
