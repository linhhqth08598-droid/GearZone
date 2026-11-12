package Models;

import java.util.Date;

public class Notifications {

    private Integer notificationId;
    private Integer userId;
    private String notificationType;
    private String title;
    private String content;
    private String channel;
    private String referenceType;
    private Integer referenceId;
    private String status;
    private Boolean isRead;
    private Date createdAt;
    private Date sentAt;

    public Notifications() {
    }

    public Notifications(Integer notificationId, Integer userId, String notificationType, String title, String content, String channel, String referenceType, Integer referenceId, String status, Boolean isRead, Date createdAt, Date sentAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.title = title;
        this.content = content;
        this.channel = channel;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.status = status;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
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

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

}
