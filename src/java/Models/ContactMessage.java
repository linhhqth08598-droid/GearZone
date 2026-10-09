package Models;

import java.util.Date;

public class ContactMessage {
    private int messageId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String messageContent;
    private Date createdAt;
    private boolean isRead;

    // Getters and Setters
    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public boolean isIsRead() { return isRead; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }
}